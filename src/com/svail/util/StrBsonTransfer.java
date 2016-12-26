package com.svail.util;

import com.mongodb.BasicDBObject;
import net.sf.json.JSONObject;

import java.util.Iterator;

/**
 * Created by ZhouXiang on 2016/12/15.
 */
public class StrBsonTransfer {

    public static BasicDBObject strToBson(String str){
        BasicDBObject doc=new BasicDBObject();
        JSONObject obj=JSONObject.fromObject(str);

        if(obj.size()>0){
            Iterator<String> keys=obj.keys();
            String key;
            String value;
            while (keys.hasNext()){
                key=keys.next();
                value=obj.get(key).toString();
                if(value.length()>0){
                    doc.put(key,value);
                }else {
                    //System.out.println(value);
                }

            }
        }else {
            //System.out.println(str);
            return null;
        }

        return doc;
    }
}
