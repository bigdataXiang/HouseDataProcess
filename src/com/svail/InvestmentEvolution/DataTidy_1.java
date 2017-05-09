package com.svail.InvestmentEvolution;

import com.mongodb.*;
import com.mongodb.util.JSON;
import com.svail.grid50.util.db;
import com.svail.util.FileTool;
import com.svail.util.Tool;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.util.*;

import static com.svail.grid50.util.PoiCode.setPoiCode_50;

/**
 * Created by ZhouXiang on 2017/3/23.
 * 将从服务器上copy下来的所有原始数据整理成标准字段的json数据，
 * 并且地理编码之后存储到Mongodb中
 *
 * 包括二手房和出租房的数据，其中二手房的数据是更新11月23号以后的
 * 出租房的数据是全部更新
 *
 * 所有标准字段参照【字段标准.md】
 *
 * 20170428:
 * 二手房数据更新到5月2号，
 * 出租数据更新到5月2号
 *
 * 通过利用文件【D:\1_基于房产调控政策下的房产投资市场格局演变分析——以北京为例\小区地理编码\用于对房源数据地理编码的文件.txt】
 * 将【BasicData_Resold_Deduplication】中的数据地理编码并存储到【BasicData_Resold_gd】
 * 将【BasicData_Rentout_Deduplication】中的数据地理编码并存储到【BasicData_Rentout_gd】
 *
 * BasicData_Resold_gd_plus
 * 是加了单价的数据
 */
public class DataTidy_1 {
    public static void main(String[] args){
        //String anjuke_source="D:\\1_基于房产调控政策下的房产投资市场格局演变分析——以北京为例\\二手房数据\\woaiwojia\\";
        //String anjuke_store="D:\\1_基于房产调控政策下的房产投资市场格局演变分析——以北京为例\\二手房数据\\woaiwojia\\RESOLD\\";

        /*Vector<String> names=FileTool.Load(anjuke_source+"filename.txt","utf-8");
        for(int i=0;i<names.size();i++){
            processRentout_fang(anjuke_source+names.elementAt(i),
                    anjuke_store+"规范化后的出租数据7.txt",
                    true);
        }*/


        /*String source= "D:\\1_基于房产调控政策下的房产投资市场格局演变分析——以北京为例\\新数据\\出租\\";
        String store=source;

        processRentout_fang(source+"woaiwojia_rentout20170503.txt",
                store+"规范化后的出租数据2.txt",
                true,
                "woaiwojia");*/

        //batchStorage();

        //duplicateRemoval();

        //geoCode("D:\\1_基于房产调控政策下的房产投资市场格局演变分析——以北京为例\\小区地理编码\\用于对房源数据地理编码的文件.txt");

        unit_Price();
    }


    public static Set<String> communities=new HashSet<>();


    //1.将出租、二手房数据简化成标准字段形式
    public static void processRentout_anjuke(String sourcefile,String storefile,boolean yn){
        Vector<String> pois= FileTool.Load(sourcefile,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);

            /*String time= Tool.getStrByKey(poi,"<TIME>","</TIME>","</TIME>");
            String ymd=time.substring(0,time.indexOf(" "));
            String[] array=ymd.split("/");
            String year=array[0];
            String month=array[1];
            String day=array[2];
            String price=Tool.getStrByKey(poi,"<PRICE>","</PRICE>","</PRICE>").replace("元/月","");
            String community=Tool.getStrByKey(poi,"<COMMUNITY>","</COMMUNITY>","</COMMUNITY>");
            community=community.substring(0,community.length()/2);
            String address=Tool.getStrByKey(poi,"<ADDRESS>","</ADDRESS>","</ADDRESS>");
            address=Tool.delect_content_inBrackets(address,"(",")");
            String area=Tool.getStrByKey(poi,"<HOUSE_AREA>","</HOUSE_AREA>","</HOUSE_AREA>").replace("平米","");
            String house_type=Tool.getStrByKey(poi,"<HOUSE_TYPE>","</HOUSE_TYPE>","</HOUSE_TYPE>");
            String rent_type=Tool.getStrByKey(poi,"<RENT_TYPE>","</RENT_TYPE>","</RENT_TYPE>");
*/

            JSONObject o= JSONObject.fromObject(poi);
            String time= o.getString("time");
            String ymd=time.substring(0,time.indexOf(" "));
            String[] array=ymd.split("/");
            String year=array[0];
            String month=array[1];
            String day=array[2];

            String price=o.getString("price").replace("元/月","");
            String community=o.getString("community");
            String address=o.getString("address");
            address=Tool.delect_content_inBrackets(address,"(",")");
            String area=o.getString("area").replace("平米","");
            String house_type=o.getString("house_type");
            String rent_type=o.getString("rent_type");

            JSONObject obj=new JSONObject();
            obj.put("year",year);
            obj.put("month",month);
            obj.put("day",day);

            obj.put("price",price);
            obj.put("community",community);
            obj.put("address",address);
            obj.put("area",area);
            obj.put("house_type",house_type);
            obj.put("rent_type",rent_type);

            if(yn){
                FileTool.Dump(obj.toString(),storefile,"utf-8");
            }else {
                System.out.println(obj);
            }

        }

    }
    public static void processRentout_fang(String sourcefile,String storefile,boolean yn,String source){

        Vector<String> pois= FileTool.Load(sourcefile,"utf-8");
        for(int i=0;i<pois.size();i++){
            System.out.println(i);
            String poi=pois.elementAt(i);

            if(!(poi.startsWith("<POI>"))){

                /*String time= Tool.getStrByKey(poi,"<TIME>","</TIME>","</TIME>");
                String ymd=time.substring(0,time.indexOf(" "));

                String[] array=new String[3];
                if(ymd.contains("/")){
                    array=ymd.split("/");
                }else if(ymd.contains("-")){
                    array=ymd.split("-");
                }
                String year=array[0];
                String month=array[1];
                String day=array[2];
                String price=Tool.getStrByKey(poi,"<PRICE>","</PRICE>","</PRICE>").replace("元/月","");
                String community=Tool.getStrByKey(poi,"<COMMUNITY>","</COMMUNITY>","</COMMUNITY>");
                //String address=Tool.getStrByKey(poi,"<ADDRESS>","</ADDRESS>","</ADDRESS>");
                //address=Tool.delect_content_inBrackets(address,"(",")");
                String area=Tool.getStrByKey(poi,"<AREA>","</AREA>","</AREA>").replace("平米","").replace("平方米","").replace("㎡","");
                String house_type=Tool.getStrByKey(poi,"<HOUSE_TYPE>","</HOUSE_TYPE>","</HOUSE_TYPE>");
                String rent_type=Tool.getStrByKey(poi,"<RENT_TYPE>","</RENT_TYPE>","</RENT_TYPE>");
*/
                JSONObject o= JSONObject.fromObject(poi);

                String time= o.getString("time");
                try{
                    String ymd=time.substring(0,time.indexOf(" "));

                    //String ymd=time;
                    String[] array=new String[3];
                    if(ymd.contains("/")){
                        array=ymd.split("/");
                    }else if(ymd.contains("-")){
                        array=ymd.split("-");
                    }
                    String year=array[0];
                    String month=array[1];
                    String day=array[2];

                    String price=o.getString("price");//.replace("元/月","").replace("万元","").replace("万","");
                    price=price.substring(0,price.indexOf("元/月"));
                    String community=o.getString("community").replace("小区：","").replace(" ","");
                    community=Tool.delect_content_inBrackets(community,"(",")");
                    String address="";
                    if(o.containsKey("address")){
                        address=o.getString("address");
                        //address=Tool.delect_content_inBrackets(address,"(",")");
                    }

                    String area=o.getString("area").replace("平米","").replace("�","").replace("O","");
                    String house_type=o.getString("house_type");
                /*String rent_type="";
                if(o.containsKey("rent_type")){
                    rent_type=o.getString("rent_type").replace("合租：","");
                }*/

                    JSONObject obj=new JSONObject();
                    obj.put("year",year);
                    obj.put("month",month);
                    obj.put("day",day);

                    obj.put("price",price);
                    obj.put("community",community);
                    //obj.put("address",address);
                    obj.put("area",area);
                    obj.put("house_type",house_type);
                    obj.put("source",source);

                    if(yn){
                        FileTool.Dump(obj.toString(),storefile,"utf-8");
                    }else {
                        System.out.println(i+":"+obj);
                    }
                }catch (StringIndexOutOfBoundsException e){
                    e.getStackTrace();
                }catch (JSONException e){
                    e.getStackTrace();
                }
            }else {
                continue;
            }
        }

    }


    //2.将各个网站的数据全都存储到mongodb数据库中，并注明数据的来源
    //将数据除此批量存入数据库【BasicData_Rentout】和【BasicData_Resold】中
    public static void batchStorage(){
        String path="D:\\1_基于房产调控政策下的房产投资市场格局演变分析——以北京为例\\新数据\\出租\\";
        String source="fang";
        System.out.println(source+":");
        DBCollection coll= db.getDB("InvestmentEvolution").getCollection("BasicData_Rentout");
        for(int i=1;i<=1;i++){
            System.out.println(i);
            String sourcefile=path+"规范化后的出租数据"+i+".txt";
            data2Mongo(sourcefile,source,coll);
        }
        System.out.println();

        String path1="D:\\1_基于房产调控政策下的房产投资市场格局演变分析——以北京为例\\新数据\\出租\\";
        String source1="woaiwojia";
        System.out.println(source1+":");
        for(int i=2;i<=2;i++){
            System.out.println(i);
            String sourcefile=path1+"规范化后的出租数据"+i+".txt";
            data2Mongo(sourcefile,source1,coll);
        }

        /*String path2="D:\\1_基于房产调控政策下的房产投资市场格局演变分析——以北京为例\\租房数据\\lianjia\\RENTOUT\\";
        String source2="lianjia";
        System.out.println(source2+":");
        for(int i=1;i<=1;i++){
            System.out.println(i);
            String sourcefile=path2+"规范化后的出租数据"+i+".txt";
            data2Mongo(sourcefile,source2,coll);
        }

        String path3="D:\\1_基于房产调控政策下的房产投资市场格局演变分析——以北京为例\\租房数据\\woaiwojia\\RENTOUT\\";
        String source3="woaiwojia";
        System.out.println(source3+":");
        for(int i=1;i<=13;i++){
            System.out.println(i);
            String sourcefile=path3+"规范化后的出租数据"+i+".txt";
            data2Mongo(sourcefile,source3,coll);
        }


        String xiaoqu="D:\\1_基于房产调控政策下的房产投资市场格局演变分析——以北京为例\\小区地理编码\\北京所有小区匹配数据汇总.txt";
        Map<String,String> map=new HashMap<>();
        Vector<String> pois=FileTool.Load(xiaoqu,"utf-8");
        JSONObject obj;
        String poi;
        for(int i=0;i<pois.size();i++){
            poi=pois.elementAt(i);
            obj=JSONObject.fromObject(poi);
            String community="";
            if(obj.containsKey("source_address")){
                community=obj.getString("source_address");
            }else {
                if(obj.containsKey("community")){
                    community=obj.getString("community");
                }
            }
            map.put(community,"");
        }


        Iterator<String> names=communities.iterator();
        String community="";
        while (names.hasNext()){
            community=names.next();
            if(map.containsKey(community)){

            }else {
                FileTool.Dump(community,"D:\\1_基于房产调控政策下的房产投资市场格局演变分析——以北京为例\\小区地理编码\\租房中无地理编码的小区.txt","utf-8");
            }
        }*/
    }
    public static void data2Mongo(String sourcefile, String source, DBCollection coll){
        Vector<String> pois=FileTool.Load(sourcefile,"utf-8");
        String poi;
        JSONObject obj;
        BasicDBObject doc;
        for(int i=0;i<pois.size();i++){
            poi=pois.elementAt(i);
            obj=JSONObject.fromObject(poi);

            doc=new BasicDBObject();
            try{
                doc.put("year",obj.getString("year"));
                doc.put("month",obj.getString("month"));
                doc.put("day",obj.getString("day"));
                doc.put("price",obj.getString("price"));
                doc.put("community",obj.getString("community"));
                if(obj.containsKey("area")){
                    doc.put("area",obj.getString("area"));
                }
                doc.put("house_type",obj.getString("house_type"));
                if(obj.containsKey("rent_type")){
                    doc.put("rent_type",obj.getString("rent_type"));
                }
                doc.put("source",source);
                communities.add(obj.getString("community"));
            }catch (JSONException e){
                //FileTool.Dump(obj.toString(),sourcefile.replace(".txt","_无具体月份数据.txt"),"utf-8");
                //System.out.println(obj);
                e.getStackTrace();
            }

            coll.insert(doc);
        }
    }


    //3.将【BasicData_Rentout】、【BasicData_Resold】和【BasicData_Resold_gd】中的数据拷贝一份到
    //【BasicData_Rentout_Deduplication】和【BasicData_Resold_Deduplication】中,并在此基础上进行去重复
    public static void duplicateRemoval(){

        Mongo m;
        DB db_output=null;
        DB db_input=null;
        try {
            m = new Mongo("127.0.0.1", 27017);//192.168.6.9
            db_output= m.getDB("paper");
            db_input= m.getDB("InvestmentEvolution");
        }catch (Exception e){
            e.printStackTrace();
        }
        DBCollection coll_output = db_output.getCollection("BasicData_Resold_gd");
        DBCollection coll_input= db_input.getCollection("BasicData_Resold_Deduplication");

        BasicDBObject doc_output;
        BasicDBObject doc_input;
        DBCursor cs=coll_output.find();
        String month="";
        String day="";
        String house_type="";
        String rooms="";
        String halls="";
        String kitchen="";
        String bathrooms="";
        String area;
        String price;

        int count=0;
        while (cs.hasNext()){
            count++;
            if(count>0){
                doc_output=(BasicDBObject)cs.next();

                try{

                    month=doc_output.getString("month");
                    if(month.startsWith("0")){
                        month=month.substring(1);
                    }
                    day=doc_output.getString("day");
                    if(day.startsWith("0")){
                        day=day.substring(1);
                    }
                    house_type=doc_output.getString("house_type").replace("null","");
                    //System.out.println(house_type);
                    if(house_type.length()>0){

                        if(doc_output.containsField("rooms")){

                            if(doc_output.containsField("rooms")){
                                rooms=doc_output.getString("rooms");
                            }
                            if(doc_output.containsField("halls")){
                                halls=doc_output.getString("halls");
                            }
                            if(doc_output.containsField("kitchen")){
                                kitchen=doc_output.getString("kitchen");
                            }
                            if(doc_output.containsField("bathrooms")){
                                bathrooms=doc_output.getString("bathrooms");
                            }

                        }else {
                            if(house_type.contains("厨")){
                                rooms=house_type.substring(0,house_type.indexOf("室"));
                                halls=house_type.substring(house_type.indexOf("室")+"室".length(),house_type.indexOf("厅"));
                                kitchen=house_type.substring(house_type.indexOf("厅")+"厅".length(),house_type.indexOf("厨"));
                                bathrooms=house_type.substring(house_type.indexOf("厨")+"厨".length(),house_type.indexOf("卫"));
                            }else {
                                if(house_type.contains("室")){
                                    rooms=house_type.substring(0,house_type.indexOf("室"));

                                    if(house_type.contains("厅")){
                                        halls=house_type.substring(house_type.indexOf("室")+"室".length(),house_type.indexOf("厅"));
                                        if(house_type.contains("卫")&&!house_type.contains("卫生间")){
                                            bathrooms=house_type.substring(house_type.indexOf("厅")+"厅".length(),house_type.indexOf("卫"));
                                        }
                                    }else {
                                        if(house_type.contains("卫")&&!house_type.contains("卫生间")){
                                            bathrooms=house_type.substring(house_type.indexOf("室")+"室".length(),house_type.indexOf("卫"));
                                        }
                                    }
                                }else {
                                    //System.out.println("没有室");
                                }

                            }
                        }


                        doc_input=new BasicDBObject();
                        try{
                            doc_input.put("year",Integer.parseInt(doc_output.getString("year")));
                            doc_input.put("month",Integer.parseInt(month));
                            doc_input.put("day",Integer.parseInt(day));
                        }catch (NumberFormatException e){
                            //System.out.println(doc_output);
                            e.getStackTrace();
                        }

                        price=doc_output.getString("price").replace("未知","");
                        if(price.contains("↑")){
                            price=price.substring(0,price.indexOf("↑"));
                        }else if(price.contains("↓")){
                            price=price.substring(0,price.indexOf("↓"));
                        }
                        try{
                            doc_input.put("price",Double.parseDouble(price));
                        }catch (NumberFormatException e){
                            System.out.println(price);
                            e.getStackTrace();
                        }

                        if(doc_output.containsField("area")){
                            area=doc_output.getString("area").replace("null","");
                            if(area.length()!=0){
                                try{
                                    doc_input.put("area",Double.parseDouble(area));
                                }catch (NumberFormatException e){
                                    //System.out.println(area);
                                }

                            }
                        }
                        doc_input.put("community",doc_output.getString("community"));
                        doc_input.put("house_type",house_type);
                        if(rooms.length()!=0){
                            try{
                                doc_input.put("rooms",Integer.parseInt(rooms));
                            }catch (NumberFormatException e){
                                e.getStackTrace();
                            }
                        }
                        if(halls.length()!=0){
                            try{
                                doc_input.put("halls",Integer.parseInt(halls));
                            }catch (NumberFormatException e){
                                e.getStackTrace();
                            }
                        }
                        if(kitchen.length()!=0){
                            try{
                                doc_input.put("kitchen",Integer.parseInt(kitchen));
                            }catch (NumberFormatException e){
                                e.getStackTrace();
                            }
                        }
                        if(bathrooms.length()!=0){
                            try{
                                doc_input.put("bathrooms",Integer.parseInt(bathrooms));
                            }catch (NumberFormatException e){
                                e.getStackTrace();
                            }

                        }
                        if(doc_output.containsField("address")){
                            doc_input.put("address",doc_output.getString("address"));
                        }
                        /*if(doc_output.containsField("rent_type")){
                            doc_input.put("rent_type",doc_output.getString("rent_type"));
                        }*/
                        doc_input.put("source",doc_output.getString("source"));
                        rooms="";
                        halls="";
                        kitchen="";
                        bathrooms="";

                        //System.out.println(count);
                        //System.out.println(doc_input);
                        coll_input.insert(doc_input);
                    }else {
                        //System.out.println("housetype为null");
                    }

                }catch (NullPointerException e){
                    //System.out.println(doc_output);
                    e.getStackTrace();
                }
            }
        }
    }

    //4.对【BasicData_Rentout_Deduplication】和【BasicData_Resold_Deduplication】中
    //的数据进行地理编码
    public static void geoCode(String geocodefile){
        Vector<String> pois=FileTool.Load(geocodefile,"utf-8");
        String poi;
        JSONObject obj;
        String community="";

        Map<String,JSONObject> map_source=new HashMap<>();
        Map<String,JSONObject> map_community=new HashMap<>();

        for(int i=0;i<pois.size();i++){
            poi=pois.elementAt(i);
            obj=JSONObject.fromObject(poi);
            if(obj.containsKey("source_address")){
                community=obj.getString("source_address");
            }else if(obj.containsKey("community")){
                community=obj.getString("community");
            }

            /*if(map.containsKey(community)){
                System.out.println("1: "+map.get(community));
                System.out.println("2: "+obj);
            }*/
            map_source.put(community,obj);
            if(obj.containsKey("community")){
                map_community.put(obj.getString("community"),obj);
            }else {
                map_community.put(obj.getString("formatted_address"),obj);
            }

        }
        System.out.println(map_source.size());

        DBCollection coll_output = db.getDB("InvestmentEvolution").getCollection("BasicData_Resold_Deduplication");
        DBCollection coll_input= db.getDB("InvestmentEvolution").getCollection("BasicData_Resold_gd");

        DBCursor cs=coll_output.find();
        BasicDBObject doc;
        Set<String> set=new HashSet<>();
        int count=0;
        double lng_gd;
        double lat_gd;
        int nomath=0;
        String city="";
        String formatted_address="";
        JSONObject lnglat=new JSONObject();
        int code;
        int row;
        int col;
        while (cs.hasNext()){
            count++;
            doc=(BasicDBObject)cs.next();
            if(doc.containsField("community")){
                community=doc.getString("community");
                if(map_source.containsKey(community)){
                    lnglat=map_source.get(community);
                }else {
                    if(map_community.containsKey(community)){
                        lnglat=map_community.get(community);
                    }else {
                        System.out.println("没有匹配信息："+doc);
                        //set.add(community);
                    }
                }

                if(lnglat.containsKey("lat_gd")){
                    lng_gd=lnglat.getDouble("lng_gd");
                    lat_gd=lnglat.getDouble("lat_gd");
                    String[] result=setPoiCode_50(lat_gd,lng_gd).split(",");
                    code = Integer.parseInt(result[0]);
                    row=Integer.parseInt(result[1]);
                    col=Integer.parseInt(result[2]);
                    doc.put("code",code);
                    doc.put("row",row);
                    doc.put("col",col);

                    if(lnglat.containsKey("city")){
                        city=lnglat.getString("city");
                        doc.put("city",city);
                    }
                    if(lnglat.containsKey("formatted_address")){
                        formatted_address=lnglat.getString("formatted_address");
                        doc.put("formatted_address",formatted_address);
                    }

                    coll_input.insert(doc);
                }else {
                    nomath++;
                }
            }else {
                System.out.println("没有小区信息："+doc);
            }


            System.out.println(count);
        }

        /*Iterator<String> it=set.iterator();
        while (it.hasNext()){
            FileTool.Dump(it.next(),"D:\\1_基于房产调控政策下的房产投资市场格局演变分析——以北京为例\\小区地理编码\\需要匹配的数据1.txt","utf-8");
        }*/

        System.out.println("有"+nomath+"条数据没有匹配~");
    }


    //5.计算每条poi的单价(二手房)或者房间单价（租房）
    //出租数据的单间价格计算会遇到很多问题，有些数据没有户型，有些没有面积，有些没有出租方式
    //即使有出租方式，price也不确定是单间价格还是整租价格
    //所以还是很有必要计算一下格网方差，如果方差较大，则应该剔除
    public static void unit_Price(){
        DBCollection coll_output = db.getDB("InvestmentEvolution").getCollection("BasicData_Rentout_gd");
        DBCollection coll_input= db.getDB("InvestmentEvolution").getCollection("BasicData_Rentout_gd_plus");

        DBCursor cs=coll_output.find();
        BasicDBObject doc;
        int count=0;
        double price=0;
        double rooms=0;
        double area=0;
        double unitprice=0;
        String rentype="";
        while (cs.hasNext()) {
            count++;
            doc = (BasicDBObject) cs.next();
            /*
            这是处理二手房的单价
            if(doc.containsField("price")){
                price=doc.getDouble("price");

                if(doc.containsField("area")){
                    area=doc.getDouble("area");

                    unitprice=price/area;
                    doc.put("unitprice",unitprice);
                    coll_input.insert(doc);
                }
            }else {
                System.out.println("没有价格信息");
            }*/

            //这是处理租房的单价
            if(doc.containsField("price")){
                price=doc.getDouble("price");

                if(doc.containsField("rooms")){
                    rooms=doc.getDouble("rooms");

                    if(doc.containsField("rent_type")){
                        rentype=doc.getString("rent_type");
                        if(rentype.contains("整租")){
                            if(price<=2000){
                                unitprice=price;
                            }else {
                                if(doc.containsField("area")){
                                    area=doc.getDouble("area");
                                    if(rooms>1&&area<50){
                                        unitprice=price;
                                    }else {
                                        unitprice=price/rooms;
                                    }
                                }else {
                                    unitprice=price/rooms;
                                }
                            }
                        }else if(rentype.contains("合租")){
                            unitprice=price;
                        }else {
                            System.out.println(rentype);
                            unitprice=price;
                        }
                    }else {
                        //System.out.println("没有出租方式信息"+doc);
                        if(doc.containsField("rooms")) {
                            rooms = doc.getDouble("rooms");
                            unitprice=price/rooms;
                        }else {
                            System.out.println("没有出租方式还没有户型");
                        }
                    }

                    /*if(unitprice<1000){
                        System.out.println("单间租金过少："+unitprice+" : "+doc);
                    }*/
                    doc.put("unitprice",unitprice);
                    unitprice=0;
                    coll_input.insert(doc);
                }else {
                    //System.out.println("没有户型信息"+doc);
                    if(doc.containsField("rent_type")){
                        rentype=doc.getString("rent_type");
                        if(rentype.contains("整租")){
                            if(price<=2000){
                                unitprice=price;
                            }else {
                                if(doc.containsField("area")){
                                    area=doc.getDouble("area");

                                    if(rooms<=40){
                                        rooms=1;
                                    }else if(40<rooms&&rooms<=80){
                                        rooms=2;
                                    }else if(80<rooms&&rooms<120){
                                        rooms=3;
                                    }else{
                                        rooms=4;
                                    }

                                    if(rooms>1&&area<50){
                                        unitprice=price;
                                    }else {
                                        unitprice=price/rooms;
                                    }
                                }else {
                                    unitprice=price;//既没有户型信息，有没有面积信息，没办法确定单间价格
                                }
                            }
                        }else if(rentype.contains("合租")){
                            unitprice=price;
                        }else {
                            //System.out.println(rentype);
                            unitprice=price;
                        }
                    }else {
                        //System.out.println("没有出租方式信息"+doc);
                        if(doc.containsField("rooms")) {
                            rooms = doc.getDouble("rooms");
                            unitprice=price/rooms;
                        }else {
                            //System.out.println("没有出租方式还没有户型");
                            if(price<=2000){
                                unitprice=price;
                            }else {
                                if(doc.containsField("area")){
                                    area=doc.getDouble("area");

                                    if(rooms<=40){
                                        rooms=1;
                                    }else if(40<rooms&&rooms<=80){
                                        rooms=2;
                                    }else if(80<rooms&&rooms<120){
                                        rooms=3;
                                    }else{
                                        rooms=4;
                                    }

                                    if(rooms>1&&area<50){
                                        unitprice=price;
                                    }else {
                                        unitprice=price/rooms;
                                    }
                                }else {
                                    unitprice=price;//既没有户型信息，有没有面积信息，没办法确定单间价格
                                }
                            }
                        }
                    }
                }
                doc.put("unitprice",unitprice);
                unitprice=0;
                coll_input.insert(doc);
            }else {
                System.out.println("没有价格信息:"+doc);
            }
        }
    }



}
