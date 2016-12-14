package com.svail.grid50;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.grid50.util.db;

/**
 * Created by ZhouXiang on 2016/12/14.
 */
public class CompareSource_5 {
    public static void main(String[] args){
        compare("385568");
    }
    public static void compare(String code){
        DBCollection coll = db.getDB("paper").getCollection("BasicData_Resold");
        BasicDBObject document = new BasicDBObject();
        document.put("code",code);
        //document.put("source","woaiwojia");
        document.put("year","2016");
        document.put("month","05");
        DBCursor cursor = coll.find(document);
        String poi="";
        if(cursor.hasNext()) {
            while (cursor.hasNext()) {
                poi=cursor.next().toString();
                System.out.println(poi);
            }
        }
    }
}
