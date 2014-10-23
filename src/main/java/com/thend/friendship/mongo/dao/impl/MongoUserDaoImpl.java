package com.thend.friendship.mongo.dao.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.thend.friendship.mongo.BaseMongoDBStorage;
import com.thend.friendship.mongo.dao.MongoUserDao;
import com.thend.friendship.po.User;
@Service
public class MongoUserDaoImpl extends BaseMongoDBStorage<User> implements
		MongoUserDao {
	
	public MongoUserDaoImpl() {
		setCollectionName("user");
	}

	public int insert(User user) {
		boolean ret = save(user);
		if(ret) {
			return 1;
		}
		return 0;
	}

	public User getUserById(String _id) {
		return get(_id, User.class);
	}
	
	public List<User> selectUserByAge(int age) {
		Map<String,Object> queryMap = new HashMap<String,Object>();
		Map<String,Object> sortMap = new LinkedHashMap<String,Object>();
		queryMap.put("userAge", age);
		sortMap.put("createTime", -1);
		sortMap.put("userId", -1);
		return queryList(queryMap, sortMap, 0, 10, User.class);
	}
	
	public void listAll() {
		DBCursor c = getCollection().find();
        while (c.hasNext()) {
            DBObject o1 = c.next();
            System.out.println(o1);
        }
	}

}
