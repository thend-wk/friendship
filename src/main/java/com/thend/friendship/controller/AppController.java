package com.thend.friendship.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thend.friendship.mongo.dao.MongoUserDao;
import com.thend.friendship.po.User;
import com.thend.friendship.service.AppService;

@RestController
public class AppController {
	
	@Resource
	private AppService appService;
	
	@Resource
	private MongoUserDao mongoUserDao;
	
	@RequestMapping("/")
	String home() {
	    return "hello";
	}
	
	@RequestMapping("/print")
	void print() {
		appService.print();
	}
	
	@RequestMapping("/user")
	User user() {
//		User user = appService.getCachedUserById(2L);
//		mongoUserDao.insert(user);
//	    String _id = "5446436034c2203a133b3d13";
//	    User user = mongoUserDao.getUserById(_id);
		mongoUserDao.listAll();
		List<User> users = mongoUserDao.selectUserByAge(27);
		for(User user : users) {
			System.out.println(user.toJson());
		}
	    return null;
	}
}
