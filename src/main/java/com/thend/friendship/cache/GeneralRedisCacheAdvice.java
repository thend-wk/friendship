package com.thend.friendship.cache;

import javax.annotation.Resource;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.ShardedJedis;

import com.thend.friendship.redis.RedisClient;
import com.thend.friendship.utils.ByteSerializer;

public class GeneralRedisCacheAdvice implements MethodInterceptor {
	
	private static final Log logger = LogFactory.getLog(GeneralRedisCacheAdvice.class);
	
	@Resource
	private RedisClient redisClient;
	
	public Object invoke(MethodInvocation invocation) throws Throwable {
		//查询缓存，如果缓存为空则调用proceed，并使用postHandle设置缓存
		//缓存命中则直接返回
		Object rval = preHandleInternal(invocation);
		if (rval != null) {
			return rval;
		}
		try {
			rval = invocation.proceed();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
		postHandleInternal(invocation, rval);
		return rval;
	}

	//查询缓存
	private Object preHandleInternal(MethodInvocation invocation) {
		try {
			Object [] paras = invocation.getArguments();
			String keyPre = invocation.getMethod().getAnnotation(GeneralCache.class).keyPre();
			String key = getPara(invocation.getMethod().getAnnotation(GeneralCache.class).key(), paras);
			final String cachekey = keyPre + "#" + key;
			return redisClient.execute(new RedisClient.ShardedRedisAction<Object>() {

				public Object act(ShardedJedis jedis) {
					byte[] bytes = jedis.get(toByteKey(cachekey));
					if(bytes != null && bytes.length > 0) {
						return ByteSerializer.deserialize(bytes);
					}
					return null;
				}
			});
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}
		
	//设置缓存
	private void postHandleInternal(MethodInvocation invocation, final Object obj) {
		try {
			Object [] paras = invocation.getArguments();
			String keyPre = invocation.getMethod().getAnnotation(GeneralCache.class).keyPre();
			String key = getPara(invocation.getMethod().getAnnotation(GeneralCache.class).key(), paras);
			final int cacheTime = invocation.getMethod().getAnnotation(GeneralCache.class).cacheTime();
			if (null == keyPre || null == key){
				return;
			}
			final String cachekey = keyPre + "#" + key;
			redisClient.execute(new RedisClient.ShardedRedisAction<Object>() {
				
				public Object act(ShardedJedis jedis) {
					jedis.setex(toByteKey(cachekey), cacheTime, ByteSerializer.serialize(obj));
					return null;
				}
			});
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	private String getPara(String orgPara, Object[] paras) {
		for(int i = 0; i< paras.length; i++){
			if (null != paras[i]){
				orgPara =  orgPara.replaceFirst("#" + i, paras[i].toString());
			} else {
				return null;
			}
		}
		return orgPara;
	}
	
    private byte[] toByteKey(String key) {
        return key.getBytes();
    }
}
