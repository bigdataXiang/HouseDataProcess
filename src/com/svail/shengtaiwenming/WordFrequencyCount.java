package com.svail.shengtaiwenming;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.grid50.util.db;
import com.svail.grid50.util.dbip;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZhouXiang on 2017/3/9.
 */
public class WordFrequencyCount {
    public static void main(String[] args){
        String[] keywords={"生态文明","生态农业","无公害蔬菜",""};
        accessDatabase(keywords,500);
    }

    public static void accessDatabase(String[] keywords,int limit){
        DBCollection collection= dbip.getDB("192.168.6.9","geoknowledge").getCollection("cnki");

        Map<String,String> keywords_title=new HashMap<>();
        for(int i=0;i<keywords.length;i++){
            keywords_title.put(keywords[i],"");
        }
        Map<String,Integer> keywords_num=new HashMap<>();

        DBCursor cs=collection.find();
        BasicDBObject doc;
        int monitor=0;
        while (cs.hasNext()){
            monitor++;
            if(monitor<limit){
                //System.out.println(monitor);
                doc=(BasicDBObject)cs.next();
                if(doc.containsField("keywords")){
                    String[] kws=doc.getString("keywords").split(" ");
                    for(int k=0;k<kws.length;k++){
                        String kw=kws[k].replace(" ","");
                        if(keywords_title.containsKey(kw)){
                            if(keywords_num.containsKey(kw)){
                                int num=keywords_num.get(kw);
                                keywords_num.put(kw,++num);
                            }else {
                                keywords_num.put(kw,1);
                            }
                        }
                    }
                }
            }else {
                break;
            }
        }

        //最后统计输出所有关键词的频次
        for(Map.Entry<String,Integer> entry:keywords_num.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }

    }
}
