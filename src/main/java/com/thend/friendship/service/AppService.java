package com.thend.friendship.service;

import com.thend.friendship.po.User;

public interface AppService {
	
	public void print();

	public void testTx() throws Exception;
	
	public User getCachedUserById(long userId);

	public User getUserById(long userId);
}
