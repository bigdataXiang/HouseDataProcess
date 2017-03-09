package com.svail.shengtaiwenming;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.grid50.util.dbip;
import com.svail.util.FileTool;
import com.svail.util.JsonListCompare;
import net.sf.json.JSONObject;

import java.util.*;

/**
 * Created by ZhouXiang on 2017/3/9.
 */
public class WordFrequencyCount {
    public static void main(String[] args){
        //获取程序开始时间
        long startTime = System.currentTimeMillis();

        String[] keywords={"生态"};

        String storefile="";
        accessDatabase(keywords,0,50000,storefile);

        long endTime = System.currentTimeMillis();    //获取结束时间

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
    }

    public static void accessDatabase(String[] keywords,int start,int end,String storefile){
        DBCollection collection= dbip.getDB("192.168.6.9","geoknowledge").getCollection("cnki");

        Map<String,String> keywords_title=new HashMap<>();
        for(int i=0;i<keywords.length;i++){
            keywords_title.put(keywords[i],"");
        }
        Map<String,Integer> keywords_num=new HashMap<>();

        DBCursor cs=collection.find().batchSize(50000);
        BasicDBObject doc;
        int monitor=0;
        while (cs.hasNext()){
            monitor++;
            if(monitor>0&&monitor<=end){
                //System.out.println(monitor);
                doc=(BasicDBObject)cs.next();
                if(doc.containsField("keywords")){
                    String[] kws=doc.getString("keywords").split(" ");
                    for(int k=0;k<kws.length;k++){
                        String kw=kws[k].replace(" ","").replace("；",";");
                        if(kw.indexOf(";")!=-1){
                            String[] kws_array=kw.split(";");
                            for(int m=0;m<kws_array.length;m++){
                                String ar=kws_array[m];
                                if(ar.length()>0){
                                    if(ar.indexOf("生态")!=-1){
                                        if(keywords_num.containsKey(ar)){
                                            int num=keywords_num.get(ar);
                                            keywords_num.put(ar,++num);
                                        }else {
                                            keywords_num.put(ar,1);
                                        }
                                    }

                                }
                            }
                        }else {
                            if(kw.length()>0){

                                if(kw.indexOf("生态")!=-1){
                                    if(keywords_num.containsKey(kw)){
                                        int num=keywords_num.get(kw);
                                        keywords_num.put(kw,++num);
                                    }else {
                                        keywords_num.put(kw,1);
                                    }
                                }

                            }
                        }

                    }
                }
            }else {
                break;
            }
        }

        //最后统计输出所有关键词的频次
        List<JSONObject> list=new ArrayList<>();
        for(Map.Entry<String,Integer> entry:keywords_num.entrySet()){
            String kw=entry.getKey();
            int num=entry.getValue();
            JSONObject obj=new JSONObject();
            obj.put("kw",kw);
            obj.put("num",num);
            list.add(obj);
        }
        Collections.sort(list, new JsonListCompare.numComparator());
        for(int i=0;i<list.size();i++){
            FileTool.Dump(list.get(i).toString(),storefile,"utf-8");
        }

    }
}
