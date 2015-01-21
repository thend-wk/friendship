package com.thend.friendship.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thend.friendship.mongo.dao.MongoUserDao;
import com.thend.friendship.mq.RabbitMQSender;
import com.thend.friendship.po.User;
import com.thend.friendship.service.AppService;
import com.thend.friendship.service.UserService;
@RestController
public class AppController {
	
	@Resource
	private UserService userService;
	
	@Resource
	private AppService appService;
	
	@Resource
	private MongoUserDao mongoUserDao;
	
	@Resource
	private RabbitMQSender rabbitMQSender;
	
	@RequestMapping("/")
	String home() {
	    return "hello";
	}
	
	@RequestMapping("/print")
	void print() {
		try {
			appService.testTx();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/userCache")
	User userCache() {
		User user = userService.getCachedUserById(2L);
		return user;
	}
	
	@RequestMapping("/userDB")
	User userDB() {
		User user = userService.getUserById(1L);
		return user;
	}
	
	@RequestMapping("/userMongo")
	User userMongo() {
//		User user = appService.getCachedUserById(2L);
//		mongoUserDao.insert(user);
//	    String _id = "5446436034c2203a133b3d13";
//	    User user = mongoUserDao.getUserById(_id);
//		mongoUserDao.listAll();
		List<User> users = mongoUserDao.selectUserByAge(27);
		for(User user : users) {
			System.out.println(user.toJson());
		}
	    return null;
	}
	
	@RequestMapping("/sendMessage")
	void sendMessage(
			@RequestParam("message") String message) {
		rabbitMQSender.dispatch(message);
	}
}
