package com.thend.friendship.service;

import com.thend.friendship.po.User;

public interface AppService {
	
	public void print();
	
	public User getCachedUserById(long userId);

}
