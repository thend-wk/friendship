package com.thend.friendship.redis;

import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Redis实现的分布式锁
 * 
 * @author wangkai
 * 
 */
public class RedisLock {

	private static final Log logger = LogFactory.getLog(RedisLock.class);

	// 最大锁定时间
	private static final int MAX_LOCK_DURATION = 5*60*1000;//5分钟

	public static final Random random = new Random();

	private JedisPool jedisPool;
	
	public RedisLock(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	/**
	 * 获取Redis分布式锁，返回存储的锁的时间戳，用来作为解锁时的标示
	 * 
	 * @param lockKey
	 * @return
	 */
	public long tryLock(String lockKey) {
		Jedis jedis = jedisPool.getResource();
		boolean isBroken = false;
		try {
			long current = System.currentTimeMillis();
			long expireTime = current + MAX_LOCK_DURATION;
			long ret = jedis.setnx(lockKey, Long.toString(expireTime));
			if(ret == 1) {
				// 获取锁
				logger.debug("lock key success, lock get");
				jedis.expire(lockKey, MAX_LOCK_DURATION / 1000);
				return expireTime;
			} else {
				//防止expire失败时造成永远都取不到锁
				String val = jedis.get(lockKey);
				if(StringUtils.isNotBlank(val)) {
					if((System.currentTimeMillis() - Long.parseLong(val)) > MAX_LOCK_DURATION) {
						jedis.del(lockKey);
						logger.info("exceed max lock duration, del key : " + lockKey);
					}
				}
			}
		} catch (JedisConnectionException je) {
			logger.error(je.getMessage(), je);
			isBroken = true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (null != jedis) {
				if(isBroken) {
					jedisPool.returnBrokenResource(jedis);
				} else {
					jedisPool.returnResource(jedis);
				}
			}
		}
		return 0L;
	}

	public void unlock(String lockKey, long lockValue) {
		Jedis jedis = jedisPool.getResource();
		boolean isBroken = false;
		try {
			if (jedis.exists(lockKey)) {
				String value = jedis.get(lockKey);
				if ((value != null) && Long.parseLong(value) == lockValue) {
					// 只删除由自己建立的lock，防止因为任务太久超时而删除了别的进程占有的锁
					jedis.del(lockKey);
					logger.info("unlock key : " + lockKey);
				}
			}
		} catch (JedisConnectionException je) {
			logger.error(je.getMessage(), je);
			isBroken = true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (null != jedis) {
				if(isBroken) {
					jedisPool.returnBrokenResource(jedis);
				} else {
					jedisPool.returnResource(jedis);
				}
			}
		}
	}
}
