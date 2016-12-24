package com.svail.grid50.util;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.util.JSON;
import com.svail.util.FileTool;
import com.svail.util.Tool;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZhouXiang on 2016/12/24.
 * 建立小区的小区地址之间的映射关系
 *
 */
public class Community_Address_Match {
    public static void main(String[] args){
        String file="D:\\小论文\\poi资料\\小区—地址匹配数据.txt";
        DBCollection coll=db.getDB("houseprice").getCollection("BasicData_Resold_100");
        DBCursor cs=coll.find();
        BasicDBObject doc;
        String address;
        String community;
        Map<String,String> com_addr=new HashMap<>();
        int count=0;
        while (cs.hasNext()){
            count++;
            System.out.println(count);
            doc=(BasicDBObject) cs.next();

            if(doc.containsField("address")){
                if(doc.containsField("community")){
                    community= Tool.delect_content_inBrackets(doc.getString("community"),"(",")");
                    address=doc.getString("address");
                    if(com_addr.containsKey(community)){

                    }else {
                        com_addr.put(community,address);
                    }
                }
                System.out.println("存在地址！");
            }
        }

        for(Map.Entry<String,String> entry:com_addr.entrySet()){
            JSONObject obj=new JSONObject();
            obj.put("community",entry.getKey());
            obj.put("address",entry.getValue());
            FileTool.Dump(obj.toString(),file,"utf-8");
        }
    }
}
