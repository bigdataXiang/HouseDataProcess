package com.svail.grid50;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.grid50.util.db;
import com.svail.util.FileTool;

import java.util.Vector;

/**
 * Created by ZhouXiang on 2016/12/16.
 * 该类利用【GridData_Resold】集合中的插值后的数据作为源数据
 * 生成【GridData_Resold_Acceleration】
 * 里面存储的是50*50的网格值为相对2015年的07月的增长率
 *
 * 源数据：{code(int),row(int),col(int),year(int),month(int),price(double)}
 * 结果：{code,row,col,year,month,acceleration}
 */
public class GridAcceleration_11 {
    public static void main(String[] args){

    }
    public static void calculate_Acceleration(String dbName,String collExport,String collImport,String file){

        DBCollection coll_export= db.getDB(dbName).getCollection(collExport);
        DBCollection coll_import= db.getDB(dbName).getCollection(collImport);

        Vector<String> pois= FileTool.Load(file,"utf-8");
        int code;
        int row;
        int col;
        int year;
        int month;
        double basic;
        double price;
        BasicDBObject doc;
        BasicDBObject doc_acceleration;
        double acceleration=0;

        for(int i=0;i<pois.size();i++){
            basic=0;
            code=Integer.parseInt(pois.get(i));

            BasicDBObject document=new BasicDBObject();
            document.put("code",code);

            DBCursor cs=coll_export.find(document);
            //获得基底数据
            while (cs.hasNext()){
                doc=(BasicDBObject)cs.next();
                year=doc.getInt("year");
                month=doc.getInt("month");

                if(year==2015&&month==7){
                    basic=doc.getDouble("price");
                    continue;
                }
            }

            while (cs.hasNext()){
                doc=(BasicDBObject)cs.next();
                year=doc.getInt("year");
                month=doc.getInt("month");
                price=doc.getDouble("price");
                row=doc.getInt("row");
                col=doc.getInt("col");

                doc_acceleration=new BasicDBObject();
                doc_acceleration.put("code",code);
                doc_acceleration.put("row",row);
                doc_acceleration.put("col",col);
                doc_acceleration.put("year",year);
                doc_acceleration.put("month",month);

                if(year==2015&&month==7){

                }else {
                    if(year==2015){
                        acceleration=(price-basic)/(month-7);
                        doc_acceleration.put("acceleration",acceleration);
                    }else if(year==2016){
                        acceleration=(price-basic)/(12+month-7);
                        doc_acceleration.put("acceleration",acceleration);
                    }
                }
                coll_import.insert(doc_acceleration);
            }
        }
    }
}
