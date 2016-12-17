package com.svail.grid50.util;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.sun.glass.ui.Cursor;
import com.svail.util.FileTool;
import net.sf.json.JSONObject;

import java.util.Vector;

import static com.svail.util.StrBsonTransfer.strToBson;

/**
 * Created by ZhouXiang on 2016/12/15.
 * 将本地文件导入mongodb中
 */
public class ImportToMongo {
    public static void main(String[] args){

    }
    public static void toMongo(String file,String dbName,String collName){
        DBCollection coll = db.getDB(dbName).getCollection(collName);
        Vector<String> pois= FileTool.Load(file,"utf-8");
        String poi="";
        BasicDBObject doc;
        for(int i=0;i<pois.size();i++){
            poi=pois.elementAt(i);
            doc=strToBson(poi);

            DBCursor rls =coll.find(doc);
            if(rls == null || rls.size() == 0){
                coll.insert(doc);
            }else{
                System.out.println("exist!");
            }
        }
    }
}
