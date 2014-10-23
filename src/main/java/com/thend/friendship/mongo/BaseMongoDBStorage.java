package com.thend.friendship.mongo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.thend.friendship.utils.Const;

public abstract class BaseMongoDBStorage<T> {
    
    private static final Log logger = LogFactory.getLog(BaseMongoDBStorage.class);
    
    private String collectionName;

	@Resource
    private DB mongodb;

    public DB getMongodb() {
        return mongodb;
    }

    public void setMongodb(DB mongodb) {
        this.mongodb = mongodb;
    }

    public String getCollectionName() {
		return collectionName;
	}
    
    public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

    public DBCollection getCollection() {
        getMongodb().requestStart();
        getMongodb().requestEnsureConnection();
        if(StringUtils.isEmpty(collectionName)) {
        	throw new RuntimeException("collectionName is null!");
        }
        return getMongodb().getCollection(collectionName);
    }

    public void requestDone() {
        getMongodb().requestDone();
    }

    public Map<String,Object> describeBean(Object bean) throws IllegalArgumentException, IllegalAccessException {
        if (bean == null) {
            return new HashMap<String,Object>();
        }
        Map<String,Object> description = new HashMap<String,Object>();
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            String fieldName = declaredField.getName();
            Object value = declaredField.get(bean);
            if ("_id".equals(fieldName)) {
                if (value != null && StringUtils.isNotEmpty(value.toString())) {
                    description.put(fieldName, new ObjectId(value.toString()));
                }
            } else {
                description.put(fieldName, value);
            }
        }
        return description;
    }

    public DBObject convertBasicDBObject(Object pojo) {
        DBObject o = new BasicDBObject();
        Map<String,Object> map = null;
        try {
            map = describeBean(pojo);
        } catch (Exception e) {
        	logger.error(e.getMessage());
        }
        o.putAll(map);
        return o;
    }

    public void convertPojo(Object d, DBObject dbObject) {
        try {
            BeanUtils.populate(d, dbObject.toMap());
        } catch (Exception e) {
        	logger.error(e.getMessage());
        }
    }

    protected boolean save(T obj) {
        try {
            WriteResult result = getCollection().save(convertBasicDBObject(obj));
            if (result.getError() == null && result.getN() > 0) {
                return true;
            }
        } catch (Exception e) {
        	logger.error(e.getMessage());
        } finally {
            requestDone();
        }
        return false;
    }
    
    protected boolean delete(String _id) {
        DBObject query = new BasicDBObject();
        query.put(Const.OBJECTID, new ObjectId(_id));
        try {
        	WriteResult result = getCollection().remove(query);
        	if (result.getError() == null && result.getN() > 0) {
                return true;
            }
        } catch (Exception e) {
        	logger.error(e.getMessage());
        } finally {
            requestDone();
        }
        return false;
    }
    
    public T get(String _id, Class<T> clazz) {
        DBObject query = new BasicDBObject();
        query.put(Const.OBJECTID, new ObjectId(_id));
        try {
            DBObject object = getCollection().findOne(query);
            if (object != null) {
            	T mp = clazz.newInstance();
                convertPojo(mp, object);
                return mp;
            }
        } catch (Exception e) {
        	logger.error(e.getMessage());
        } finally {
            requestDone();
        }
        return null;
    }
    

    public List<T> queryList(Map<String, Object> queryMap, Map<String, Object> sortMap, 
    		int offset, int size, Class<T> clazz) {
        List<T> list = new ArrayList<T>();
        try {
            DBCursor cursor = null;
            if (queryMap != null && queryMap.size() > 0) {
                DBObject queryObject = new BasicDBObject();
                queryObject.putAll(queryMap);
                cursor = getCollection().find(queryObject);
            } else {
                cursor = getCollection().find();
            }

            if (sortMap != null && sortMap.size() > 0) {
                DBObject sortObject = new BasicDBObject();
                sortObject.putAll(sortMap);
                cursor = cursor.sort(sortObject);
            }
            cursor = cursor.skip(offset).limit(size);
            while (cursor.hasNext()) {
                T mp = clazz.newInstance();
                convertPojo(mp, cursor.next());
                list.add(mp);
            }
            return list;
        } catch (Exception e) {
        	logger.error(e.getMessage());
        } finally {
            requestDone();
        }
        return Collections.emptyList();
    }
}
