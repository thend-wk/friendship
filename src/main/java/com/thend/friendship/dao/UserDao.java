package com.thend.friendship.dao;

import com.thend.friendship.po.User;

public interface UserDao {
	
	public int insertUser(User user);
	
	public User getUserById(long userId);
	
	public int updateUserAge(long userId, int age);

}
