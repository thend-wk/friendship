package com.thend.friendship.dao.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.thend.friendship.dao.AppDao;
import com.thend.friendship.po.User;
@Repository
public class AppDaoImpl implements AppDao {
	
	@Resource
	private SqlSession sqlSession;

	private static final String namespace = "user";

	public User getUserById(long userId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userId", userId);
		return sqlSession.selectOne(namespace + ".getUserById", params);
	}

}
