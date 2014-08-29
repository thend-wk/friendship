package com.thend.friendship.utils;

public class Const {
	
	
	//redis key
	public static final String SHARDED_JEDIS_PATTERN = "#([0-9]+)#";
	
	public static final String CACHE_USER = "user";
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static String getUserCacheKey(long userId) {
		return CACHE_USER + "#" + userId;
	}

}
