package com.thend.friendship.service.impl;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import redis.clients.jedis.ShardedJedis;

import com.thend.friendship.dao.AppDao;
import com.thend.friendship.po.User;
import com.thend.friendship.redis.RedisClient;
import com.thend.friendship.service.AppService;
import com.thend.friendship.utils.Const;
@Service
public class AppServiceImpl implements AppService {
	
	@Resource
	private AppDao appDao;
	
	@Resource
	private RedisClient redisClient;
	
	private static final Log logger = LogFactory.getLog(AppServiceImpl.class);

	public void print() {
		logger.info("app service print!");
	}
	
	public User getCachedUserById(final long userId) {
		User user = redisClient.execute(new RedisClient.ShardedRedisAction<User>() {

			public User act(ShardedJedis shardedJedis) {
				String cachekey = Const.getUserCacheKey(userId);
				if(shardedJedis.exists(cachekey)) {
					return User.fromJson(shardedJedis.get(cachekey));
				}
				return null;
			}
		});
		if(user == null) {
			user = appDao.getUserById(userId);
			if(user != null) {
				final User dbUser = user;
				redisClient.execute(new RedisClient.ShardedRedisAction<User>() {
					public User act(ShardedJedis shardedJedis) {
						String cachekey = Const.getUserCacheKey(userId);
						shardedJedis.set(cachekey, dbUser.toJson());
						return dbUser;
					}
				});
			}
		}
		return user;
	}
	

	public User getUserById(long userId) {
		return appDao.getUserById(userId);
	}
	
	@Transactional
	public void testTx() {
		appDao.updateUserAge(2L, 100);
		User user = new User();
		user.setUserName("michael");
		user.setUserId(3L);
		user.setUserAge(38);
		appDao.insertUser(user);
	}
}
