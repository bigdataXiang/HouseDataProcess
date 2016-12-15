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
        Iterator<String> keys=obj.keys();
        String key;
        Object value;
        while (keys.hasNext()){
            key=keys.next();
            value=obj.get(key);
            doc.put(key,value);
        }
        return doc;
    }
}
