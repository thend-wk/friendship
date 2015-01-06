package com.thend.friendship;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mongodb.DB;
import com.rabbitmq.client.Address;
import com.thend.friendship.mongo.MongoDBFactory;
import com.thend.friendship.mq.RabbitMQListener;
import com.thend.friendship.mq.RabbitMQSender;
import com.thend.friendship.property.JdbcProperty;
import com.thend.friendship.property.MongoProperty;
import com.thend.friendship.property.RabbitMQProperty;
import com.thend.friendship.property.RedisProperty;
import com.thend.friendship.property.SolrProperty;
import com.thend.friendship.redis.RedisClient;
import com.thend.friendship.redis.SimpleShardedJedisPool;
import com.thend.friendship.solr.client.SolrPingClient;
import com.thend.friendship.solr.client.SolrSearchClient;
import com.thend.friendship.utils.Const;

@Configuration
public class AppConfig {
	
	@Autowired
	private JdbcProperty jdbcProperty;
	
	@Autowired
	private RedisProperty redisProperty;
	
	@Autowired
	private MongoProperty mongoProperty;
	
	@Autowired
	private RabbitMQProperty rabbitMQProperty;
	
	@Autowired
	private SolrProperty solrProperty;
	
	@Bean
	public SqlSession masterSqlSession() {
		return wrapSqlSession(jdbcProperty.getMasterUrl(), jdbcProperty.getMasterUsername(), jdbcProperty.getMasterPassword());
	}
	
	@Bean
	public SqlSession slaveSqlSession() {
		return wrapSqlSession(jdbcProperty.getSlaveUrl(), jdbcProperty.getSlaveUsername(), jdbcProperty.getSlavePassword());
	}
	
	private SqlSession wrapSqlSession(String jdbcUrl, String username, String password) {
		try {
			ComboPooledDataSource dataSource = new ComboPooledDataSource();
			dataSource.setJdbcUrl(jdbcUrl);
			dataSource.setUser(username);
			dataSource.setPassword(password);
			dataSource.setDriverClass(jdbcProperty.getDriver());
			dataSource.setMaxIdleTime(jdbcProperty.getMaxIdleTime());
			dataSource.setMinPoolSize(jdbcProperty.getMinPoolSize());
			dataSource.setMaxPoolSize(jdbcProperty.getMaxPoolSize());
			
			SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
			sqlSessionFactory.setDataSource(dataSource);
			List<Resource> resList = new ArrayList<Resource>();
			resList.add(new ClassPathResource("ibatis/user_sql.xml"));
			sqlSessionFactory.setMapperLocations(resList.toArray(new Resource[0]));
			
			SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory.getObject());
			return sqlSessionTemplate;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public JedisPool soloJedisPool() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(redisProperty.getSoloMaxTotal());
		config.setMaxIdle(redisProperty.getSoloMaxIdle());
		config.setMaxWaitMillis(redisProperty.getMaxWaitMillis());
		config.setTestOnBorrow(redisProperty.isTestOnBorrow());
		JedisPool soloJedisPool = new JedisPool(config, redisProperty.getHost(), redisProperty.getPort());
		return soloJedisPool;
	}
	
	public SimpleShardedJedisPool shardedJedisPool() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(redisProperty.getShardedMaxTotal());
		config.setMaxIdle(redisProperty.getShardedMaxIdle());
		config.setMaxWaitMillis(redisProperty.getMaxWaitMillis());
		config.setTestOnBorrow(redisProperty.isTestOnBorrow());
		SimpleShardedJedisPool shardedJedisPool = new SimpleShardedJedisPool(config, redisProperty.getClusters(), Const.SHARDED_JEDIS_PATTERN);
		return shardedJedisPool;
	}
	
	@Bean
	public RedisClient redisClient() {
		RedisClient redisClient = new RedisClient();
		redisClient.setSoloJedisPool(soloJedisPool());
		redisClient.setShardedJedisPool(shardedJedisPool());
		return redisClient;
	}
	
	@Bean
	public DB mongodb() {
		MongoDBFactory dbf = new MongoDBFactory();
	    DB db = dbf.createDB(mongoProperty.getMongoUrl());
	    return db;
	}
	
	@Bean
	public RabbitMQListener rabbitMQListener() {
		RabbitMQListener listener = new RabbitMQListener(rabbitMQProperty.getUri(), 
				rabbitMQProperty.getExchange(), rabbitMQProperty.getRoutingKey(), getRabbitAddr());
		return listener;
	}
	
	@Bean
	public RabbitMQSender rabbitMQSender() {
		RabbitMQSender sender = new RabbitMQSender(rabbitMQProperty.getUri(), 
				rabbitMQProperty.getExchange(), getRabbitAddr());
		sender.setRoutingKey(rabbitMQProperty.getRoutingKey());
		return sender;
	}
	
	private Address[] getRabbitAddr() {
		List<Address> addList = new ArrayList<Address>();
		String clusters = rabbitMQProperty.getClusters();
		String[] hostPortPairs = clusters.split(",");
		for(String hostPortPair : hostPortPairs) {
			String[] items = hostPortPair.split(":");
			if(items.length > 1) {
				Address addr = new Address(items[0], Integer.parseInt(items[1]));
				addList.add(addr);
			}
		}
		return addList.toArray(new Address[0]);
	}
	
	@Bean
	public SolrPingClient solrPingClient() {
		SolrPingClient client = new SolrPingClient(solrProperty.getPingUrl());
		return client;
	}
	
	@Bean
	public SolrSearchClient solrSearchClient() {
		SolrSearchClient client = new SolrSearchClient(solrProperty.getSearchUrl());
		return client;
	}
	
	@Bean
	public JdbcProperty jdbcProperty() {
		return new JdbcProperty();
	}
	
	@Bean
	public RedisProperty redisProperty() {
		return new RedisProperty();
	}
	
	@Bean
	public MongoProperty mongoProperty() {
		return new MongoProperty();
	}
	
	@Bean
	public RabbitMQProperty rabbitMQProperty() {
		return new RabbitMQProperty();
	}
	
	@Bean
	public SolrProperty solrProperty() {
		return new SolrProperty();
	}
}
