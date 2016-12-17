package com.svail.grid50;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.grid50.util.db;
import com.svail.util.FileTool;

/**
 * Created by ZhouXiang on 2016/12/14.
 */
public class CompareSource_5 {
    public static void main(String[] args){
        compare("4314006","paper","GridData_Resold","D:\\GridData_Resold_month10.txt");
    }
    public static void compare(String code,String dbName,String collName,String file){
        DBCollection coll = db.getDB(dbName).getCollection(collName);
        BasicDBObject document = new BasicDBObject();
        //document.put("code",code);
        document.put("year","2015");
        document.put("month","10");
        DBCursor cursor = coll.find(document);
        String poi="";
        int count=0;
        if(cursor.hasNext()) {
            while (cursor.hasNext()) {
                //count++;
                //System.out.println(count);
                //poi=cursor.next().toString();
                //System.out.println(poi);
                FileTool.Dump(cursor.next().toString(),file,"utf-8");
            }
        }
    }
}
