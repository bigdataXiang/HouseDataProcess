package com.svail.grid50.util;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.util.FileTool;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;

import static com.svail.util.StrBsonTransfer.strToBson;

/**
 * Created by ZhouXiang on 2016/12/15.
 * 由于mongodb中不允许key中含有诸如"81.0"带有"."号出现的数据，所以要将数据作适当的转换
 * 【 "2室2厅1厨1卫" : { "area" : { "average" : 81.0 , "data" : { "81.0" : 1}} , "price" : { "average" : 55.0 , "data" : { "55.0" : 1}} , "unitprice" : { "average" : 0.67901236 , "data" : { "0.67901236" : 1}} , "ratio" : 0.25}】
 */
public class SolveDotProblem {
    public static void main(String[] args){

        removeDot("D:\\gridOld.txt","D:\\gridNew.txt","D:\\nullException.txt");
        removeDot("D:\\GridData_Resold_12.txt","D:\\gridOld.txt","D:\\gridNew.txt","D:\\nullException.txt");
        removeDot("D:\\GridData_Resold_11.txt","D:\\gridOld.txt","D:\\gridNew.txt","D:\\nullException.txt");

    }

    //!!!将数据库【GridData_Resold】格式修整后的数据临时存在temp中
    public static void removeDot(String gridOld,String gridNew,String nullException){
        DBCollection collection=db.getDB("paper").getCollection("GridData_Resold");
        DBCollection collection_temp=db.getDB("temp").getCollection("temp");


        DBCursor cs=collection.find();
        BasicDBObject doc=new BasicDBObject();
        BasicDBObject doc_new=new BasicDBObject();

        int count=0;
        String key="";
        while (cs.hasNext()){
            doc=(BasicDBObject)cs.next();
            doc.remove("_id");
            FileTool.Dump(doc.toString(),gridOld,"utf-8");

            doc_new=new BasicDBObject();
            Iterator<String> keys=doc.keySet().iterator();

            try{
                while (keys.hasNext()){
                    key=keys.next();
                    if(key.equals("type")){

                        JSONObject type=JSONObject.fromObject(doc.getString("type"));
                        //System.out.println(type);
                        Iterator<String> hts=type.keySet().iterator();

                        while (hts.hasNext()){
                            try{
                                String ht=hts.next();
                                BasicDBObject infos=strToBson(type.getString(ht));

                                //System.out.println(infos);
                                BasicDBObject data;
                                JSONArray array;
                                Iterator<String> datas;
                                BasicDBObject area=strToBson(infos.getString("area"));
                                if(area!=null){
                                    data=strToBson(area.getString("data"));
                                    array=new JSONArray();
                                    datas=data.keySet().iterator();
                                    while (datas.hasNext()){
                                        JSONObject obj=new JSONObject();
                                        String k=datas.next();
                                        int num=data.getInt(k);
                                        obj.put("area",Double.parseDouble(k));
                                        obj.put("amount",num);
                                        array.add(obj);
                                    }
                                    area.put("data",array);
                                    infos.put("area",area);
                                }else {
                                    FileTool.Dump(doc.toString(),nullException,"utf-8");
                                }


                                BasicDBObject price=strToBson(infos.getString("price"));
                                if(price!=null){
                                    data=strToBson(price.getString("data"));
                                    array=new JSONArray();
                                    datas=data.keySet().iterator();
                                    while (datas.hasNext()){
                                        JSONObject obj=new JSONObject();
                                        String k=datas.next();
                                        int num=data.getInt(k);
                                        obj.put("price",Double.parseDouble(k));
                                        obj.put("amount",num);
                                        array.add(obj);
                                    }
                                    price.put("data",array);
                                    infos.put("price",price);
                                }else {
                                    FileTool.Dump(doc.toString(),nullException,"utf-8");
                                }


                                BasicDBObject unitprice=strToBson(infos.getString("unitprice"));
                                if(unitprice!=null){
                                    data=strToBson(unitprice.getString("data"));
                                    array=new JSONArray();
                                    datas=data.keySet().iterator();
                                    while (datas.hasNext()){
                                        JSONObject obj=new JSONObject();
                                        String k=datas.next();
                                        int num=data.getInt(k);
                                        obj.put("unitprice",Double.parseDouble(k));
                                        obj.put("amount",num);
                                        array.add(obj);
                                    }
                                    unitprice.put("data",array);
                                    infos.put("unitprice",price);
                                }else {
                                    FileTool.Dump(doc.toString(),nullException,"utf-8");
                                }
                                type.put(ht,infos);

                            }catch (ConcurrentModificationException e){
                                e.getStackTrace();
                            }
                        }
                        doc_new.put("type",type);
                    }else {
                        doc_new.put(key,doc.get(key));
                    }
                }

                FileTool.Dump(doc_new.toString(),gridNew,"utf-8");
                collection_temp.insert(doc_new);
            }catch (RuntimeException e){
                FileTool.Dump(doc.toString(),nullException,"utf-8");
            }
        }
        System.out.println("2015年10月的数据有"+count);
    }

    //将linux上生成的2015年的11月和12月份的数据也同样处理掉
    public static void removeDot(String sourcefile,String gridOld,String gridNew,String nullException){
        Vector<String> pois=FileTool.Load(sourcefile,"utf-8");
        DBCollection collection_temp=db.getDB("temp").getCollection("temp");

        JSONObject doc=new JSONObject();
        BasicDBObject doc_new=new BasicDBObject();

        String key="";
        String poi="";

        for(int i=0;i<pois.size();i++){
            poi=pois.elementAt(i);

            doc=JSONObject.fromObject(poi);
            doc.remove("_id");
            FileTool.Dump(doc.toString(),gridOld,"utf-8");

            doc_new=new BasicDBObject();
            Iterator<String> keys=doc.keySet().iterator();

            try{
                while (keys.hasNext()){
                    key=keys.next();
                    if(key.equals("type")){

                        JSONObject type=JSONObject.fromObject(doc.getString("type"));
                        Iterator<String> hts=type.keySet().iterator();

                        while (hts.hasNext()){
                            try{
                                String ht=hts.next();
                                BasicDBObject infos=strToBson(type.getString(ht));

                                //System.out.println(infos);
                                BasicDBObject data;
                                JSONArray array;
                                Iterator<String> datas;
                                BasicDBObject area=strToBson(infos.getString("area"));
                                if(area!=null){
                                    data=strToBson(area.getString("data"));
                                    array=new JSONArray();
                                    datas=data.keySet().iterator();
                                    while (datas.hasNext()){
                                        JSONObject obj=new JSONObject();
                                        String k=datas.next();
                                        int num=data.getInt(k);
                                        obj.put("area",Double.parseDouble(k));
                                        obj.put("amount",num);
                                        array.add(obj);
                                    }
                                    area.put("data",array);
                                    infos.put("area",area);
                                }else {
                                    FileTool.Dump(doc.toString(),nullException,"utf-8");
                                }


                                BasicDBObject price=strToBson(infos.getString("price"));
                                if(price!=null){
                                    data=strToBson(price.getString("data"));
                                    array=new JSONArray();
                                    datas=data.keySet().iterator();
                                    while (datas.hasNext()){
                                        JSONObject obj=new JSONObject();
                                        String k=datas.next();
                                        int num=data.getInt(k);
                                        obj.put("price",Double.parseDouble(k));
                                        obj.put("amount",num);
                                        array.add(obj);
                                    }
                                    price.put("data",array);
                                    infos.put("price",price);
                                }else {
                                    FileTool.Dump(doc.toString(),nullException,"utf-8");
                                }


                                BasicDBObject unitprice=strToBson(infos.getString("unitprice"));
                                if(unitprice!=null){
                                    data=strToBson(unitprice.getString("data"));
                                    array=new JSONArray();
                                    datas=data.keySet().iterator();
                                    while (datas.hasNext()){
                                        JSONObject obj=new JSONObject();
                                        String k=datas.next();
                                        int num=data.getInt(k);
                                        obj.put("unitprice",Double.parseDouble(k));
                                        obj.put("amount",num);
                                        array.add(obj);
                                    }
                                    unitprice.put("data",array);
                                    infos.put("unitprice",price);
                                }else {
                                    FileTool.Dump(doc.toString(),nullException,"utf-8");
                                }
                                type.put(ht,infos);

                            }catch (ConcurrentModificationException e){
                                e.getStackTrace();
                            }
                        }
                        doc_new.put("type",type);
                    }else {
                        doc_new.put(key,doc.get(key));
                    }
                }

                FileTool.Dump(doc_new.toString(),gridNew,"utf-8");
                collection_temp.insert(doc_new);
            }catch (RuntimeException e){
                FileTool.Dump(doc.toString(),nullException,"utf-8");
            }
        }
    }
}
