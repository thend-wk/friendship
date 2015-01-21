package com.thend.friendship.service;

import com.thend.friendship.po.User;

public interface UserService {
	
	public User getCachedUserById(long userId);

	public User getUserById(long userId);
}
