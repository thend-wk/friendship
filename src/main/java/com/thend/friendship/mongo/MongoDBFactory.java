package com.thend.friendship.mongo;

import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;

public class MongoDBFactory {

    public DB createDB(String mongoUrl) {
        try {
            MongoURI uri = new MongoURI(mongoUrl);
            Mongo mongo = new Mongo(uri);
            DB db = mongo.getDB(uri.getDatabase());
            boolean success = db.authenticate(uri.getUsername(), uri.getPassword());
            if (!success) {
                throw new RuntimeException("mongodb auth error. db=" + uri.getDatabase());
            }
            return db;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {

        MongoDBFactory dbf = new MongoDBFactory();
        DB db = dbf.createDB("mongodb://test:test@123.58.179.105:20998/gendata");

        DBObject o = new BasicDBObject();
        o.put("f1", 1);
        o.put("f2", "String2");
        o.put("f3", new Date().getTime());

        db.getCollection("dddd").save(o);

        DBCursor c = db.getCollection("dddd").find();
        while (c.hasNext()) {
            DBObject o1 = c.next();
            System.out.println(o1);
        }

    }

}
