package com.svail.util;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.svail.grid50.util.db;

/**
 * Created by ZhouXiang on 2016/12/15.
 * 该类用于执行mongo中的删除操作
 */
public class Mongo_Delete {
    public static void main(String[] args){
        DBCollection collection= db.getDB("just_test").getCollection("test");
        BasicDBObject doc=new BasicDBObject();
        /*doc.put("year","2015");
        doc.put("month","10");
        doc.put("value","1");
        collection.insert(doc);

        doc=new BasicDBObject();
        doc.put("year","2015");
        doc.put("month","11");
        doc.put("value","2");
        collection.insert(doc);*/

        /*doc=new BasicDBObject();
        doc.put("year","2015");
        doc.put("month","10");
        delectDocument(collection,doc);*/

        //delectColl(collection);

        //delectDB(db.getDB("just_test"));

    }

    //删除集合中某些特定的文档
    public static void delectDocument(DBCollection coll, BasicDBObject document){
        coll.remove(document);
    }
    //删除整个集合
    public static void delectColl(DBCollection coll){
        coll.drop();
    }

    //删除整个数据库
    public static void delectDB(DB db){
        db.dropDatabase();
    }
}
