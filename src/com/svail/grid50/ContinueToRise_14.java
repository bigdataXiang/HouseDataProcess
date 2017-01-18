package com.svail.grid50;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.grid50.util.db;
import com.svail.util.FileTool;
import net.sf.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by ZhouXiang on 2017/1/18.
 * 这个类从GridAcceleration_12延伸而来
 * 从数据库【GridData_Resold_gd_Interpolation】中读取
 */
public class ContinueToRise_14 {
    public static void main(String[] args){
        String[] dates={"2015-10","2015-11","2015-12","2016-1",
                "2016-2","2016-3","2016-4","2016-5","2016-6","2016-7",
                "2016-8","2016-9","2016-10","2016-11"};

        String path="D:\\paper\\poi资料\\小区\\小区地理编码原始数据\\最后结果\\校对结果\\插值\\2-以点代面插值结果\\";

        //findCode(path+"interpolation_value_grids_中没有问题的数据.txt");

    }
    //第一步：找出数据比较全面的真实的具有长时序数据的code
    public static void findCode(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");
        String poi;
        String code;
        for(int i=0;i<pois.size();i++){
            poi=pois.elementAt(i);
            code=poi.substring(0,poi.indexOf(";"));
            FileTool.Dump(code,file.replace(".txt","_code集合.txt"),"utf-8");
        }
    }

    //第二步，找出这些code的长时序数据
    public static void dataQuery(String file){
        DBCollection coll= db.getDB("paper").getCollection("GridData_Resold_gd_Interpolation");
        Vector<String> codes=FileTool.Load(file,"utf-8");
        int code;
        DBCursor cs;
        BasicDBObject doc;
        BasicDBObject result;
        int year;
        int month;
        double growth_adjace;
        Map<String,Double> map;
        JSONObject obj;

        for(int i=0;i<codes.size();i++){

            code=Integer.parseInt(codes.elementAt(i));

            obj=new JSONObject();
            doc=new BasicDBObject();
            map=new HashMap<>();

            doc.put("code",code);
            cs=coll.find(doc);

            while (cs.hasNext()){
                result=(BasicDBObject)cs.next();
                if(result.containsField("growth_adjace")){
                    year=result.getInt("year");
                    month=result.getInt("month");
                    growth_adjace=result.getDouble("growth_adjace");
                    if(growth_adjace>0){
                        if((year!=2015&&month!=8)&&(year!=2015&&month!=9)){
                            obj.put(year+"-"+month,growth_adjace);
                        }
                    }
                }
            }
            if(obj.size()==14){
                obj.put("code",code);
                FileTool.Dump(obj.toString(),file.replace(".txt","_一直在涨的code.txt"),"utf-8");
            }
        }
    }
}
