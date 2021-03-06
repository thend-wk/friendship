package com.thend.friendship.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import redis.clients.jedis.ShardedJedis;

import com.thend.friendship.cache.GeneralCache;
import com.thend.friendship.dao.UserDao;
import com.thend.friendship.po.User;
import com.thend.friendship.redis.RedisClient;
import com.thend.friendship.service.UserService;
import com.thend.friendship.utils.Const;
@Service
public class UserServiceImpl implements UserService {
	
	@Resource
	private UserDao userDao;
	
	@Resource
	private RedisClient redisClient;
	
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
			user = userDao.getUserById(userId);
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
	
	@GeneralCache(keyPre = Const.CACHE_USER, key = "#0", cacheTime = 2 * 60)
	public User getUserById(long userId) {
		return userDao.getUserById(userId);
	}
}
