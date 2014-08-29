package com.thend.friendship.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thend.friendship.po.User;
import com.thend.friendship.service.AppService;

@RestController
public class AppController {
	
	@Resource
	private AppService appService;
	
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
		return appService.getCachedUserById(1L);
	}
}
