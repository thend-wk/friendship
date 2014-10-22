package com.thend.friendship.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;

public class BaseORMDao<T> {
	
	private static final Log logger = LogFactory.getLog(BaseORMDao.class);
	
	@Resource
	protected SqlSession masterSqlSession;
	
	@Resource
	protected SqlSession slaveSqlSession;
	
	protected String namespace;

    protected int insert(T obj) {
    	try {
    		return masterSqlSession.insert(getFullStatement("insert"), obj);
    	} catch (Exception e) {
    		logger.error(e.getMessage());
    	}
		return 0;
    }
    
    protected int delete(long id) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("id",id);
        try {
        	return masterSqlSession.delete(getFullStatement("delete"), params);
        } catch (Exception e) {
        	logger.error(e.getMessage());
        }
    	return 0;
    }
    
    protected T queryForEntry(String statement,Map<String,Object> params) {
    	try {
    		return masterSqlSession.selectOne(getFullStatement(statement), params);
    	} catch (Exception e) {
    		logger.error(e.getMessage());
    	}
		return null;
    }
    
    protected List<T> queryForList(String statement, Map<String,Object> params) {
        try {
            return masterSqlSession.selectList(getFullStatement(statement), params);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return Collections.emptyList();
    }
    
    protected int queryForCount(String statement, Map<String,Object> params) {
        try {
            Integer count = masterSqlSession.selectOne(getFullStatement(statement), params);
            if(count != null) {
            	return count;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return 0;
    }
    
    protected int update(String statement, Map<String,Object> params) {
        try {
            return masterSqlSession.update(getFullStatement(statement), params);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return 0;
    }
    
    private String getFullStatement(String statement) {
        return namespace + "." + statement;
    }

	protected void setNamespace(String namespace) {
		this.namespace = namespace;
	}
}
