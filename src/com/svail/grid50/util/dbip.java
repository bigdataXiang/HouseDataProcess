package com.svail.grid50.util;

import com.mongodb.DB;
import com.mongodb.Mongo;

/**
 * Created by ZhouXiang on 2017/1/7.
 */
public class dbip {
    static Mongo m;
    static DB db;
    public static DB getDB(String ip,String dbname){

        if(m==null&&db==null){
            try {
                System.out.println("运行开始:");
                m = new Mongo(ip, 27017);
                db= m.getDB(dbname);
                return db;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }else{
            return db;

        }
    }
}
