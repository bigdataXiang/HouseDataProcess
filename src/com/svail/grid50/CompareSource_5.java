package com.svail.grid50;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.grid50.util.db;
import com.svail.util.FileTool;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;

import static com.svail.util.TestStatistics.getDiffer;
import static com.svail.util.TestStatistics.getVariance;

/**
 * Created by ZhouXiang on 2016/12/14.
 */
public class CompareSource_5 {
    public static void main(String[] args){
        compare(2781237,2017,2,"InvestmentEvolution","BasicData_Resold_gd_plus","D:\\GridData_Resold.txt");
    }

    //这是用于查询【paper】数据库中的数据
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

    //这是用于查询【InvestmentEvolution】中的数据
    public static void compare(int code,int year,int month,String dbName,String collName,String file){
        DBCollection coll = db.getDB(dbName).getCollection(collName);
        BasicDBObject document = new BasicDBObject();
        document.put("code",code);
        document.put("year",year);
        document.put("month",month);
        DBCursor cursor = coll.find(document);
        String poi="";
        BasicDBObject doc;
        int count=0;

        Set<Double> set=new HashSet<>();
        if(cursor.hasNext()) {
            while (cursor.hasNext()) {
                //count++;
                //System.out.println(count);
                doc=(BasicDBObject) cursor.next();
                doc.remove("_id");
                doc.remove("day");
                doc.remove("rooms");
                doc.remove("halls");
                doc.remove("house_type");
                doc.remove("kitchen");
                doc.remove("bathrooms");
                doc.remove("code");
                doc.remove("row");
                doc.remove("col");
                //System.out.println(doc);
                //FileTool.Dump(doc.toString(),file,"utf-8");

                set.add(doc.getDouble("unitprice"));
            }
        }

        double max=1;
        while (max>=1){
            max=variance_differ(set);
        }

        JSONObject object=new JSONObject();
        object.put("code",code);
        object.put("year",year);
        object.put("month",month);
        object.put("value",set.toArray());
        //System.out.println(set.toArray().length);
        FileTool.Dump(object.toString(),file,"utf-8");
    }

    public static double variance_differ(Set<Double> set){
        int size=set.size();
        double[] up=new double[size];
        Iterator<Double> it=set.iterator();
        int i=0;
        while (it.hasNext()){
            up[i]=it.next();
            i++;
        }

        JSONObject obj=new JSONObject();
        double variance=getVariance(up);
        //System.out.println(variance+":"+set.size());
        obj.put("variance",variance);

        List<Double> list=new ArrayList<>();
        double tag;
        double differ;

        Map<Double,Double> map=new HashMap<>();
        for(int j=0;j<up.length;j++){
            tag=up[j];
            differ=getDiffer(tag,up);
            map.put(differ,tag);
            list.add(differ);
            //System.out.println(tag+"-"+differ);
        }

        double max=Collections.max(list);
        double unitprice=map.get(max);
        if(variance>1){
            set.remove(unitprice);
        }

        return variance;
    }





}
