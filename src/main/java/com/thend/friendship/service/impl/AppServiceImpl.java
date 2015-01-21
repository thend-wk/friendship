package com.thend.friendship.service.impl;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thend.friendship.dao.UserDao;
import com.thend.friendship.po.User;
import com.thend.friendship.service.AppService;
@Service
public class AppServiceImpl implements AppService {
	
	private static final Log logger = LogFactory.getLog(AppServiceImpl.class);
	
	@Resource
	private UserDao userDao;
	
	public void print() {
		logger.info("app service print!");
	}
	
	//事务注解和AOP不能同时使用
	@Transactional
	public void testTx() {
		userDao.updateUserAge(2L, 100);
		User user = new User();
		user.setUserName("michael");
		user.setUserId(3L);
		user.setUserAge(38);
		userDao.insertUser(user);
	}

}
