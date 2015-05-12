package com.thend.friendship.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.exceptions.JedisException;

public class RedisClient {
	
	private static final Log logger = LogFactory.getLog(RedisClient.class);
	
	private ShardedJedisPool shardedJedisPool;
	
	private JedisPool soloJedisPool;

	public void setShardedJedisPool(ShardedJedisPool shardedJedisPool) {
		this.shardedJedisPool = shardedJedisPool;
	}

	public void setSoloJedisPool(JedisPool soloJedisPool) {
		this.soloJedisPool = soloJedisPool;
	}
	
	public <T> T execute(SoloRedisAction<T> redisAction) {
		boolean isBroken = false;
		Jedis jedis = soloJedisPool.getResource();
		try {
			return redisAction.act(jedis);
		} catch (JedisException je) {
			logger.error(je.getMessage());
			isBroken = true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			if(isBroken) {
				soloJedisPool.returnBrokenResource(jedis);
			} else {
				soloJedisPool.returnResource(jedis);
			}
		}
		return null;
	}
	
	public <T> T execute(ShardedRedisAction<T> redisAction) {
		boolean isBroken = false;
		ShardedJedis shardedJedis = shardedJedisPool.getResource();
		try {
			return redisAction.act(shardedJedis);
		} catch (JedisException je) {
			logger.error(je.getMessage());
			isBroken = true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			if(isBroken) {
				shardedJedisPool.returnBrokenResource(shardedJedis);
			} else {
				shardedJedisPool.returnResource(shardedJedis);
			}
		}
		return null;
	}
	
	public static interface SoloRedisAction<T> {
		T act(Jedis soloJedis);
	}
	
	public static interface ShardedRedisAction<T> {
		T act(ShardedJedis shardedJedis);
	}
	
	public static void main(String[] args) {
		RedisClient redisClient = new RedisClient();
		redisClient.execute(new SoloRedisAction<Boolean>() {

			public Boolean act(Jedis soloJedis) {
				// TODO Auto-generated method stub
				return true;
			}
			
		});
	}

}
