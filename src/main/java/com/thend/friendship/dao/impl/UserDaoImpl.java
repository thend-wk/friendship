package com.thend.friendship.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.thend.friendship.dao.UserDao;
import com.thend.friendship.dao.BaseORMDao;
import com.thend.friendship.po.User;
@Repository
public class UserDaoImpl extends BaseORMDao<User> implements UserDao {

	public UserDaoImpl() {
		setNamespace("user");
	}

	public User getUserById(long userId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userId", userId);
		return queryForEntry("getUserById", params);
	}
	
	public int insertUser(User user) {
		return insert(user);
	}

	public int updateUserAge(long userId, int age) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userId", userId);
		params.put("age", age);
		return update("updateUserAge", params);
	}
}
