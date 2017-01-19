package com.svail.grid50;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.grid50.util.db;
import com.svail.util.FileTool;
import net.sf.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import static com.svail.grid50.PriceAccelerationDraw_15.GridCurve;


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

        String path="D:\\paper\\一直都在涨的格网\\累积增长\\";

        //findCode(path+"interpolation_value_grids_中没有问题的数据.txt");

        //dataQueryFromMongo(path+"code集合.txt");

        /*dataQueryFromFile_all(path+"full_value_grids.txt");
        dataQueryFromFile_all(path+"interpolation_value_grids_中没有问题的数据.txt");
*/
        /*findCommunity(path+"interpolation_value_grids_中没有问题的数据_一直在涨的月份_一直在涨的月份.txt",
                path+"interpolation_value_grids_中没有问题的数据_一直在涨的月份_一直在涨的月份_小区.txt");

*/

        /*cumulativeGrowth(path+"full_value_grids_全时序.txt",path+"full_value_grids_一直在涨的月份.txt",dates);
        cumulativeGrowth(path+"interpolation_value_grids_中没有问题的数据_全时序.txt",
                         path+"interpolation_value_grids_中没有问题的数据_一直在涨的月份.txt",dates);*/


        /*drawTrendLine(0,7,"D:\\paper\\一直都在涨的格网\\累积增长\\画曲线的数据\\",
                "full_value_grids_一直在涨的月份_小区.txt",
                "full_value_grids_全时序_全时序.txt",
                "full_value_grids_全时序_累积增长值.txt");*/

        /*drawTrendLine(250,308,"D:\\paper\\一直都在涨的格网\\累积增长\\画曲线的数据\\",
                "interpolation_value_grids_中没有问题的数据_一直在涨的月份_小区.txt",
                "interpolation_value_grids_中没有问题的数据_全时序_全时序.txt",
                "interpolation_value_grids_中没有问题的数据_全时序_累积增长值.txt");*/




        //将比较理想的code的曲线图作出来
        /*drawTrendLine_Code(5926158,"D:\\paper\\一直都在涨的格网\\累积增长\\画曲线的数据\\",
                "interpolation_value_grids_中没有问题的数据_一直在涨的月份_小区.txt",
                "interpolation_value_grids_中没有问题的数据_全时序_全时序.txt",
                "interpolation_value_grids_中没有问题的数据_全时序_累积增长值.txt");*/

        // 查询波动比较大的小区的数据
        batchQuery("4057609");

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

    //第二步，找出这些code的长时序数据,
    //不过这种方法真的太慢了，我还是想想别的办法吧
    public static void dataQueryFromMongo(String file){
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
            System.out.println(i);

            code=Integer.parseInt(codes.elementAt(i));

            obj=new JSONObject();
            doc=new BasicDBObject();

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


    //第二步：利用本地文件读写的方式计算加速度
    //growth_adjace:计算相邻月份的价格差
    //通过统计有多少月份的growth_adjace为正
    public static void dataQueryFromFile_adjace(String file){
        int code;
        String timeserise;
        String year_str;
        String month_str;
        int month;
        int year;
        String date;
        double price;
        double growth_adjace=0;
        double price_before=0;
        int month_before;
        JSONObject o;
        JSONObject o_plus;
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
                o=new JSONObject();
                o_plus=new JSONObject();
                while (dates.hasNext()){

                    date=dates.next();
                    price=obj.getDouble(date);
                    year_str=date.substring(0,date.indexOf("-"));
                    month_str=date.substring(date.indexOf("-")+"-".length());

                    if(month_str.startsWith("0")){
                        month_str=month_str.substring(1);
                    }
                    year=Integer.parseInt(year_str);
                    month=Integer.parseInt(month_str);

                    if(year==2015&&month==7){

                    }else if(year==2015&&month==8){

                    }else if(year==2015&&month==9){

                    }else{

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

                        o_plus.put(year+"-"+month,growth_adjace);
                        if(growth_adjace>0){
                            o.put(year+"-"+month,growth_adjace);
                        }
                    }
                }

                if(o.size()>=11){
                    o.put("code",code);
                    FileTool.Dump(o.toString(),file.replace(".txt","_一直在涨的code.txt"),"utf-8");
                    FileTool.Dump(o_plus.toString(),file.replace(".txt","_一直在涨的code_全时序.txt"),"utf-8");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //第二步：利用本地文件读写的方式计算加速度
    //growth_adjace:计算相邻月份的价格差
    //把所有的都计算出来
    public static void dataQueryFromFile_all(String file){
        int code;
        String timeserise;
        String year_str;
        String month_str;
        int month;
        int year;
        String date;
        double price;
        double growth_adjace=0;
        double price_before=0;
        int month_before;
        JSONObject o;
        JSONObject o_plus;
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
                o=new JSONObject();
                o_plus=new JSONObject();
                while (dates.hasNext()){

                    date=dates.next();
                    price=obj.getDouble(date);
                    year_str=date.substring(0,date.indexOf("-"));
                    month_str=date.substring(date.indexOf("-")+"-".length());

                    if(month_str.startsWith("0")){
                        month_str=month_str.substring(1);
                    }
                    year=Integer.parseInt(year_str);
                    month=Integer.parseInt(month_str);

                    if(year==2015&&month==7){

                    }else if(year==2015&&month==8){

                    }else if(year==2015&&month==9){

                    }else{

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

                        o_plus.put(year+"-"+month,growth_adjace);
                        if(growth_adjace>0){
                            o.put(year+"-"+month,growth_adjace);
                        }
                    }
                }

                o.put("code",code);
                o_plus.put("code",code);
                FileTool.Dump(o.toString(),file.replace(".txt","_一直在涨的月份.txt"),"utf-8");
                FileTool.Dump(o_plus.toString(),file.replace(".txt","_全时序.txt"),"utf-8");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //第三步，找到这些持续增长的code的小区名字
    public static void findCommunity(String file,String storefile){
        Vector<String> pois=FileTool.Load(file,"utf-8");

        DBCollection coll= db.getDB("paper").getCollection("community_code");
        DBCursor cs;
        BasicDBObject doc;
        BasicDBObject result;
        String community;

        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            int code=obj.getInt("code");
            doc=new BasicDBObject();
            doc.put("code",""+code);
            cs=coll.find(doc);
            while (cs.hasNext()){
                result=(BasicDBObject) cs.next();
                community=result.getString("community");
                obj.put("community",community);
            }
            FileTool.Dump(obj.toString(),storefile,"utf-8");
        }
    }

    //第四步：计算累积增长价格
    public static void cumulativeGrowth(String file,String zhangyuefen,String[] dates){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        Vector<String> zyfs=FileTool.Load(zhangyuefen,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            String zyf=zyfs.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            int code=obj.getInt("code");

            JSONObject obj_accumulate=new JSONObject();
            JSONObject rise=new JSONObject();
            for(int j=0;j<dates.length;j++){
                String date=dates[j];

                double accumulated_value=0;
                for(int m=0;m<=j;m++){
                    accumulated_value+=obj.getDouble(dates[m]);
                }
                obj_accumulate.put(date,accumulated_value);
                if(accumulated_value>0){
                    rise.put(date,accumulated_value);
                }

            }
            if(rise.size()>=14){
                obj_accumulate.put("code",code);
                FileTool.Dump(obj_accumulate.toString(),file.replace(".txt","_累积增长值.txt"),"utf-8");
                FileTool.Dump(poi,file.replace(".txt","_全时序.txt"),"utf-8");
                FileTool.Dump(zyf,zhangyuefen.replace(".txt","_一直在涨的月份.txt"),"utf-8");
            }
        }
    }


    //第五步：作图，作增长趋势线图,对比全时序和仅增长的图
    public static void drawTrendLine(int start,int end, String path,String bufen,String quanshixu,String leiji){
        Vector<String> pois1=FileTool.Load(path+bufen,"utf-8");
        Vector<String> pois2=FileTool.Load(path+quanshixu,"utf-8");
        Vector<String> pois3=FileTool.Load(path+leiji,"utf-8");
        for(int i=start;i<end;i++){
            JSONObject obj1=JSONObject.fromObject(pois1.elementAt(i));
            JSONObject obj2=JSONObject.fromObject(pois2.elementAt(i));
            JSONObject obj3=JSONObject.fromObject(pois3.elementAt(i));
            String community=obj1.getString("community");
            GridCurve(obj1,obj2,obj3,community);
        }
    }

    public static void drawTrendLine_Code(int tagcode,String path,String bufen,String quanshixu,String leiji){
        Vector<String> pois1=FileTool.Load(path+bufen,"utf-8");
        Vector<String> pois2=FileTool.Load(path+quanshixu,"utf-8");
        Vector<String> pois3=FileTool.Load(path+leiji,"utf-8");
        for(int i=0;i<pois1.size();i++){
            JSONObject obj1=JSONObject.fromObject(pois1.elementAt(i));
            JSONObject obj2=JSONObject.fromObject(pois2.elementAt(i));
            JSONObject obj3=JSONObject.fromObject(pois3.elementAt(i));
            String community=obj1.getString("community");
            int code=obj1.getInt("code");
            if(code==tagcode){
                GridCurve(obj1,obj2,obj3,community);
            }
        }
    }



    //查找价格异常的小区的信息
    public static void batchQuery(String code){

        JSONObject unitprice_obj=new JSONObject();
        for(int i=10;i<=12;i++){
            System.out.println("2015"+i+":");
            queryBaseData(unitprice_obj,code,"2015",""+i);
        }
        for(int i=1;i<=9;i++){
            System.out.println("20160"+i+":");
            queryBaseData(unitprice_obj,code,"2016","0"+i);
        }
        for(int i=10;i<=11;i++){
            System.out.println("2016"+i+":");
            queryBaseData(unitprice_obj,code,"2016",""+i);
        }
        System.out.println(unitprice_obj);
    }
    public static void queryBaseData(JSONObject unitprice_obj,String code,String year,String month){
        DBCollection coll= db.getDB("paper").getCollection("BasicData_Resold_gd");
        BasicDBObject doc=new BasicDBObject();
        doc.put("year",year);
        doc.put("month",month);
        doc.put("code",code);

        BasicDBObject docment;
        DBCursor cs=coll.find(doc);
        JSONObject obj;

        double avenrage=0;
        double total_price=0;
        int num=0;
        while (cs.hasNext()){
            docment=(BasicDBObject)cs.next();
            docment.remove("_id");

            obj=new JSONObject();
            if(docment.containsField("community")){
                obj.put("community",docment.getString("community"));
            }
            if(docment.containsField("price")){
                obj.put("price",docment.getString("price"));
            }
            if(docment.containsField("area")){
                obj.put("area",docment.getString("area"));
            }
            if(docment.containsField("unit_price")){
                num++;
                obj.put("unit_price",docment.getString("unit_price"));
                total_price+=obj.getDouble("unit_price");
            }
            if(docment.containsField("fitment")){
                obj.put("fitment",docment.getString("fitment"));
            }
            if(docment.containsField("direction")){
                obj.put("direction",docment.getString("direction"));
            }
            if(docment.containsField("house_type")){
                obj.put("house_type",docment.getString("house_type"));
            }
            System.out.println(obj);
        }
        if(num!=0){
            avenrage=total_price/(double)num;
            System.out.println("均价："+avenrage);
            unitprice_obj.put(year+"-"+month,avenrage);
        }
    }
}
