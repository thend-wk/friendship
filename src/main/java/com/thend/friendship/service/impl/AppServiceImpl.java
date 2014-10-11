package com.thend.friendship.service.impl;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import com.thend.friendship.dao.AppDao;
import com.thend.friendship.po.User;
import com.thend.friendship.service.AppService;
import com.thend.friendship.utils.Const;
import com.thend.friendship.utils.JsonSerializer;
@Service
public class AppServiceImpl implements AppService {
	
	@Resource
	private AppDao appDao;
	
	@Resource
	private ShardedJedisPool shardedJedisPool;
	
	private static final Log logger = LogFactory.getLog(AppServiceImpl.class);

	public void print() {
		logger.info("app service print!");
	}
	
	public User getCachedUserById(long userId) {
		ShardedJedis shardedJedis = shardedJedisPool.getResource();
		try {
			String cachekey = Const.getUserCacheKey(userId);
			if(shardedJedis.exists(cachekey)) {
				return JsonSerializer.fromJson(shardedJedis.get(cachekey), User.class);
			} else {
				User user = appDao.getUserById(userId);
				if(user != null) {
					shardedJedis.set(cachekey, JsonSerializer.toJson(user));
					return user;
				}
			}
		} finally {
			shardedJedisPool.returnResource(shardedJedis);
		}
		return null;
	}

}
