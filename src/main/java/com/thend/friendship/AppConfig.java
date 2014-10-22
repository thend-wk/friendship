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
import com.thend.friendship.mongo.MongoDBFactory;
import com.thend.friendship.property.JdbcProperty;
import com.thend.friendship.property.MongoProperty;
import com.thend.friendship.property.RedisProperty;
import com.thend.friendship.redis.RedisClient;
import com.thend.friendship.redis.SimpleShardedJedisPool;
import com.thend.friendship.utils.Const;

@Configuration
public class AppConfig {
	
	@Autowired
	private JdbcProperty jdbcProperty;
	
	@Autowired
	private RedisProperty redisProperty;
	
	@Autowired
	private MongoProperty mongoProperty;
	
	@Bean
	public SqlSession masterSqlSession() throws Exception {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setDriverClass(jdbcProperty.getDriver());
		dataSource.setJdbcUrl(jdbcProperty.getMasterUrl());
		dataSource.setUser(jdbcProperty.getMasterUsername());
		dataSource.setPassword(jdbcProperty.getMasterPassword());
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
	}
	
	@Bean
	public SqlSession slaveSqlSession() throws Exception {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setDriverClass(jdbcProperty.getDriver());
		dataSource.setJdbcUrl(jdbcProperty.getSlaveUrl());
		dataSource.setUser(jdbcProperty.getSlaveUsername());
		dataSource.setPassword(jdbcProperty.getSlavePassword());
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
}
