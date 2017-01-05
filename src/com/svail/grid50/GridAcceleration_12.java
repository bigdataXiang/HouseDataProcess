package com.svail.grid50;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.grid50.util.db;
import com.svail.util.FileTool;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import static com.svail.grid50.util.RowColCalculation.Code_RowCol;

/**
 * Created by ZhouXiang on 2016/12/16.
 * 该类利用【GridData_Resold】集合中的插值后的数据作为源数据
 * 生成【GridData_Resold_Acceleration】
 * 里面存储的是50*50的网格值为相对2015年的07月的增长率
 *
 * 源数据：{code(int),row(int),col(int),year(int),month(int),price(double)}
 * 结果：{code,row,col,year,month,acceleration}
 */
public class GridAcceleration_12 {
    public static void main(String[] args){
        String path="D:\\小论文\\poi资料\\小区\\小区地理编码原始数据\\最后结果\\校对结果\\插值\\2-以点代面插值结果\\";
        DBCollection coll= db.getDB("paper").getCollection("GridData_Resold_gd_Interpolation");
        System.out.println(1);
        gridInterpolation(path+"所有无值融合的code_插值结果_融合.txt",coll);
        System.out.println(2);
        gridInterpolation(path+"failed_interpolation_codes_插值结果_融合.txt",coll);
        System.out.println(3);
        gridInterpolation(path+"full_value_grids.txt",coll);
        System.out.println(4);
        gridInterpolation(path+"interpolation_value_grids_中没有问题的数据.txt",coll);
        System.out.println(5);
        gridInterpolation(path+"pearson_is_0_插值结果_融合.txt",coll);
        System.out.println(6);
        gridInterpolation(path+"sparse_data_插值结果_融合.txt",coll);
        System.out.println(7);
        gridInterpolation(path+"以点代面_插值结果_融合.txt",coll);

    }

    //利用逐个调用数据库code的办法来计算加速度
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
                        acceleration=(price-basic)/(month-7)*10000;
                        doc_acceleration.put("acceleration",acceleration);
                    }else if(year==2016){
                        acceleration=(price-basic)/(12+month-7)*10000;
                        doc_acceleration.put("acceleration",acceleration);
                    }
                }
                coll_import.insert(doc_acceleration);
            }
        }
    }

    //利用本地文件读写的方式计算加速度
    //growth_basic:以2015年7月数据为基底计算增长率
    //differ_basic:以2015年7月数据为基底计算相邻月份的价格差
    //growth_adjace:计算相邻月份的价格差
    public static void gridInterpolation(String file,DBCollection coll){
        int code;
        String timeserise;
        String year_str;
        String month_str;
        int month;
        int year;
        String date;
        BasicDBObject doc;
        double price;
        double growth=0;
        double growth_adjace=0;
        double price_before=0;
        int month_before;
        double differ=0;
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                String poi=line;

                if(poi.indexOf(";")!=-1){
                    code=Integer.parseInt(poi.substring(0,poi.indexOf(";")));
                    timeserise=poi.substring(poi.indexOf(";")+";".length());
                }else{
                    code=Integer.parseInt(poi.substring(0,poi.indexOf(",")));
                    timeserise=poi.substring(poi.indexOf(",")+",".length());
                }
                JSONObject obj=JSONObject.fromObject(timeserise);
                //System.out.println(obj);

                Iterator<String> dates=obj.keys();

                //先找到基底价格
                double basic_price=0;
                while (dates.hasNext()){
                    date=dates.next();
                    if(date.equals("2015-07")){
                        basic_price=obj.getDouble("2015-07");
                    }
                }

                while (dates.hasNext()){
                    doc=new BasicDBObject();

                    date=dates.next();
                    price=obj.getDouble(date);
                    year_str=date.substring(0,date.indexOf("-"));
                    month_str=date.substring(date.indexOf("-")+"-".length());

                    if(month_str.startsWith("0")){
                        month_str=month_str.substring(1);
                    }
                    year=Integer.parseInt(year_str);
                    month=Integer.parseInt(month_str);
                    int[] rowcol=Code_RowCol(code,1);

                    doc.put("code",code);
                    doc.put("row",rowcol[0]);
                    doc.put("col",rowcol[1]);
                    doc.put("year",year);
                    doc.put("month",month);
                    doc.put("price",price);

                    if(year==2015&&month==7){

                    }else {

                        //基底价格计算法
                        differ=(price-basic_price)*10000;
                        doc.put("differ_basic",differ);
                        if(year==2015){
                            growth=differ/(month-7);
                        }else if(year==2016){
                            growth=differ/(12+month-7);
                        }
                        doc.put("growth_basic",growth);

                        //相邻价格计算法
                        month_before=month-1;
                        if(month_before==0){
                            price_before=obj.getDouble("2015-12");
                        }else {
                            if (month_before<10){
                                price_before=obj.getDouble(year+"-0"+month_before);
                            }else {
                                price_before=obj.getDouble(year+"-"+month_before);
                            }
                        }
                        growth_adjace=(price-price_before)*10000;
                        doc.put("growth_adjace",growth_adjace);

                    }

                    DBCursor cs=coll.find(doc);
                    if(cs==null||cs.size()==0){
                        coll.insert(doc);
                    }else {
                        System.out.println("exist");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //增长率计算方式:计算相邻月份的价格差
    public static void gridInterpolation_AdjacentMonth(String file,DBCollection coll){
        int code;
        String timeserise;
        String year_str;
        String month_str;
        int month;
        int year;
        String date;
        BasicDBObject doc;
        double price;
        double growth_adjace=0;
        double price_before=0;
        int month_before;
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                String poi=line;

                if(poi.indexOf(";")!=-1){
                    code=Integer.parseInt(poi.substring(0,poi.indexOf(";")));
                    timeserise=poi.substring(poi.indexOf(";")+";".length());
                }else{
                    code=Integer.parseInt(poi.substring(0,poi.indexOf(",")));
                    timeserise=poi.substring(poi.indexOf(",")+",".length());
                }
                JSONObject obj=JSONObject.fromObject(timeserise);
                //System.out.println(obj);

                Iterator<String> dates=obj.keys();


                while (dates.hasNext()){
                    doc=new BasicDBObject();

                    date=dates.next();
                    price=obj.getDouble(date);
                    year_str=date.substring(0,date.indexOf("-"));
                    month_str=date.substring(date.indexOf("-")+"-".length());

                    if(month_str.startsWith("0")){
                        month_str=month_str.substring(1);
                    }
                    year=Integer.parseInt(year_str);
                    month=Integer.parseInt(month_str);
                    int[] rowcol=Code_RowCol(code,1);

                    doc.put("code",code);
                    doc.put("row",rowcol[0]);
                    doc.put("col",rowcol[1]);
                    doc.put("year",year);
                    doc.put("month",month);
                    doc.put("price",price);

                    if(year==2015&&month==7){

                    }else {
                        month_before=month-1;
                        if(month_before==0){
                            price_before=obj.getDouble("2015-12");
                        }else {
                            if (month_before<10){
                                price_before=obj.getDouble(year+"-0"+month_before);
                            }else {
                                price_before=obj.getDouble(year+"-"+month_before);
                            }
                        }
                        growth_adjace=(price-price_before)*10000;
                        doc.put("growth_adjace",growth_adjace);

                    }

                    DBCursor cs=coll.find(doc);
                    if(cs==null||cs.size()==0){
                        coll.insert(doc);
                    }else {
                        System.out.println("exist");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
