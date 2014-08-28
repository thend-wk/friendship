package com.thend.friendship.service.impl;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.thend.friendship.dao.AppDao;
import com.thend.friendship.po.User;
import com.thend.friendship.service.AppService;
@Service
public class AppServiceImpl implements AppService {
	
	@Resource
	private AppDao appDao;
	
	private static final Log logger = LogFactory.getLog(AppServiceImpl.class);

	public void print() {
		logger.info("app service print!");
	}
	
	public User getUserById(long userId) {
		return appDao.getUserById(userId);
	}

}
