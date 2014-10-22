package com.thend.friendship.mongo.dao;

import java.util.List;

import com.thend.friendship.po.User;

public interface MongoUserDao {

	public int insert(User user);
	
	public User getUserById(String _id);
	
	public List<User> selectUserByAge(int age);
	
	public void listAll();
}
