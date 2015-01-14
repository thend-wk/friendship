package com.thend.friendship.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;

public class BaseORMDao<T> {
	
	@Resource
	protected SqlSession masterSqlSession;
	
	@Resource
	protected SqlSession slaveSqlSession;
	
	protected String namespace;

    protected int insert(T obj) {
    	return masterSqlSession.insert(getFullStatement("insert"), obj);
    }
    
    protected int delete(long id) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("id",id);
        return masterSqlSession.delete(getFullStatement("delete"), params);
    }
    
    protected T queryForEntry(String statement,Map<String,Object> params) {
    	return masterSqlSession.selectOne(getFullStatement(statement), params);
    }
    
    protected List<T> queryForList(String statement, Map<String,Object> params) {
    	return masterSqlSession.selectList(getFullStatement(statement), params);
    }
    
    protected int queryForCount(String statement, Map<String,Object> params) {
        Integer count = masterSqlSession.selectOne(getFullStatement(statement), params);
        if(count != null) {
        	return count;
        }
        return 0;
    }
    
    protected int update(String statement, Map<String,Object> params) {
    	return masterSqlSession.update(getFullStatement(statement), params);
    }
    
    private String getFullStatement(String statement) {
        return namespace + "." + statement;
    }

	protected void setNamespace(String namespace) {
		this.namespace = namespace;
	}
}
