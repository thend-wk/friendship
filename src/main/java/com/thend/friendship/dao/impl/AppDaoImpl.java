package com.thend.friendship.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.thend.friendship.dao.AppDao;
import com.thend.friendship.dao.BaseORMDao;
import com.thend.friendship.po.User;
@Repository
public class AppDaoImpl extends BaseORMDao<User> implements AppDao {

	public AppDaoImpl() {
		setNamespace("user");
	}

	public User getUserById(long userId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userId", userId);
		return queryForEntry("getUserById", params);
	}

}
