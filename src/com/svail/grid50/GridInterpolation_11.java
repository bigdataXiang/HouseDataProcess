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
import java.util.Iterator;

import static com.svail.grid50.util.RowColCalculation.Code_RowCol;

/**
 * Created by ZhouXiang on 2016/12/16.
 */
public class GridInterpolation_11 {
    public static void main(String[] args){
        DBCollection coll= db.getDB("paper").getCollection("GridData_Resold_Interpolation");
        gridInterpolation("",coll);
    }

    public static void gridInterpolation(String file,DBCollection coll){
        int code;
        String timeserise;
        String month;
        String year;
        String date;
        BasicDBObject doc;
        double price;
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

                Iterator<String> dates=obj.keys();
                while (dates.hasNext()){
                    doc=new BasicDBObject();

                    date=dates.next();
                    price=doc.getDouble(date);
                    year=date.substring(0,date.indexOf("-"));
                    month=date.substring(date.indexOf("-")+"-".length());
                    if(month.startsWith("0")){
                        month=month.substring(1);
                    }
                    int[] rowcol=Code_RowCol(code,1);

                    //{code(int),row(int),col(int),year(int),month(int),price(double)}
                    doc.put("code",code);
                    doc.put("row",rowcol[0]);
                    doc.put("col",rowcol[1]);
                    doc.put("year",year);
                    doc.put("month",month);
                    doc.put("price",price);

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
