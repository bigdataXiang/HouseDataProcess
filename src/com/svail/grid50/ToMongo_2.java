package com.svail.grid50;

import com.mongodb.*;
import com.svail.util.FileTool;
import net.sf.json.JSONObject;

import java.net.UnknownHostException;
import java.util.Vector;

import static com.svail.util.Tool.isNumeric;

/**
 * Created by bigdataxiang on 16-11-28.
 *
 * woaiwojia的房子没有装修这一选项
 *
 *
 */
public class ToMongo_2 {
    public static String sourcepath="/media/bigdataxiang/data/houseprice/temp_woaiwojia/";
    public static void main(String[] args){
        String filename="";
        Vector<String> pois= FileTool.Load(sourcepath+"filename.txt","utf-8");
        for(int i=0;i<pois.size();i++){
            filename=pois.elementAt(i);
            txtToMongo(sourcepath+filename);
        }

    }
    public static void txtToMongo(String file){
        Mongo m_import=null;

        try {
            m_import=new Mongo("127.0.0.1",27017);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        DB db_import=m_import.getDB("houseprice");

        DBCollection coll_import=db_import.getCollection("BasicData_Resold_50");


        Vector<String> pois= FileTool.Load(file,"utf-8");
        String poi;
        JSONObject obj;
        BasicDBObject document;
        String value="";
        for(int i=0;i<pois.size();i++){
            poi=pois.elementAt(i);
            obj=JSONObject.fromObject(poi);
            document=new BasicDBObject();

            if(obj.containsKey("property_company")){
                value=obj.getString("property_company");
                document.put("property_company",value);
            }
            if(obj.containsKey("green_rate")){
                value=obj.getString("green_rate");
                document.put("green_rate",value);
            }
            if(obj.containsKey("location")){
                value=obj.getString("location");
                document.put("location",value);
            }
            if(obj.containsKey("direction")){
                value=obj.getString("direction");
                document.put("direction",value);
            }
            if(obj.containsKey("heat_supply")){
                value=obj.getString("heat_supply");
                document.put("heat_supply",value);
            }
            if(obj.containsKey("community")){
                value=obj.getString("community");
                document.put("community",value);
            }
            if(obj.containsKey("property")){
                value=obj.getString("property");
                document.put("property",value);
            }
            if(obj.containsKey("property_fee")){
                value=obj.getString("property_fee");
                document.put("property_fee",value);
            }
            if(obj.containsKey("down_payment")){
                value=obj.getString("down_payment");
                document.put("down_payment",value);
            }
            if(obj.containsKey("unit_price")){
                value=obj.getString("unit_price").replace("元","");
                boolean num=isNumeric(value);
                if(num){
                    double temp=Double.parseDouble(value);

                    if(temp>100){
                        temp/=(double)10000;
                    }
                    document.put("unit_price",temp);
                }else {
                    if(obj.containsKey("price")&&obj.containsKey("area")){

                        String price=obj.getString("price").replace("万元","");
                        String area=obj.getString("area").replace("平米","").replace("建筑面积：","").replace("�","").replace("O","");
                        double temp=(Double.parseDouble(price))/(Double.parseDouble(area));
                        document.put("unit_price",temp);
                    }
                }

            }else if(obj.containsKey("price")&&obj.containsKey("area")){

                String price=obj.getString("price").replace("万","").replace("元","");
                String area=obj.getString("area").replace("平米","").replace("建筑面积：","").replace("�","").replace("O","");
                double temp=(Double.parseDouble(price))/(Double.parseDouble(area));
                document.put("unit_price",temp);
            }
            if(obj.containsKey("url")){
                value=obj.getString("url");
                document.put("url",value);
            }
            if(obj.containsKey("volume_rate")){
                value=obj.getString("volume_rate");
                document.put("volume_rate",value);
            }
            if(obj.containsKey("households")){
                value=obj.getString("households");
                document.put("households",value);
            }
            if(obj.containsKey("title")){
                value=obj.getString("title").replace("二手房","").replace(";","").replace("","");
                document.put("title",value);
            }
            if(obj.containsKey("time")){
                //"2016/06/0309:05:14"
                value=obj.getString("time");
                String[] date=value.split("/");
                String year=date[0];
                String month=date[1];
                String day=date[2].substring(0,2);

                document.put("time",value);
                document.put("year",year);
                document.put("month",month);
                document.put("day",day);

            }
            if(obj.containsKey("price")){
                value=obj.getString("price").replace("万元","");
                document.put("price",value);
            }
            if(obj.containsKey("area")){
                value=obj.getString("area").replace("平米","").replace("建筑面积：","").replace("�","").replace("O","");
                document.put("area",value);
            }
            if(obj.containsKey("house_type")){
                String ht=obj.getString("house_type");
                document.put("house_type",ht);

                //4室3厅1厨3卫
                if(ht.indexOf("室")!=-1){
                    String rooms=ht.substring(0,ht.indexOf("室"));
                    document.put("rooms",rooms.replace("：",""));
                }
                if(ht.indexOf("室")!=-1&&ht.indexOf("厅")!=-1){
                    String halls=ht.substring(ht.indexOf("室")+"室".length(),ht.indexOf("厅"));
                    document.put("halls",halls.replace("：",""));
                }
                if(ht.indexOf("厅")!=-1&&ht.indexOf("厨")!=-1){
                    String bathrooms=ht.substring(ht.indexOf("厅")+"厅".length(),ht.indexOf("厨"));
                    document.put("kitchen",bathrooms.replace("：",""));
                }
                if(ht.indexOf("卫")!=-1&&ht.indexOf("厨")!=-1){

                    String bathrooms=ht.substring(ht.indexOf("厨")+"厨".length(),ht.indexOf("卫"));
                    document.put("bathrooms",bathrooms.replace("：",""));

                }else if(ht.indexOf("卫")!=-1&&ht.indexOf("厅")!=-1){
                    String bathrooms=ht.substring(ht.indexOf("厅")+"厅".length(),ht.indexOf("卫"));
                    document.put("bathrooms",bathrooms.replace("：",""));
                }


            }
            if(obj.containsKey("floor")){
                String floor=obj.getString("floor").replace("层","");
                document.put("floor",floor);

                //高层(共30层)
                //"floor":"第1层(共29层)"
                if(floor.indexOf("(")!=-1&&floor.indexOf(")")!=-1){
                    String flooron=floor.substring(0,floor.indexOf("(")).replace("第","");
                    String floors=floor.substring(floor.indexOf("(")+"(".length(),floor.indexOf(")")).replace("共","").replace("层","");
                    document.put("flooron",flooron.replace("：",""));
                    document.put("floors",floors.replace("：",""));
                }else if(floor.contains("/")){
                    String flooron=floor.substring(0,floor.indexOf("/"));
                    String floors=floor.substring(floor.indexOf("/")+"/".length()).replace("层","");
                    document.put("flooron",flooron.replace("：",""));
                    document.put("floors",floors.replace("：",""));
                }
            }
            if(obj.containsKey("developer")){
                value=obj.getString("developer");
                document.put("developer",value);
            }
            if(obj.containsKey("totalarea")){
                value=obj.getString("totalarea");
                document.put("totalarea",value);
            }
            if(obj.containsKey("month_payment")){
                value=obj.getString("month_payment");
                document.put("month_payment",value);
            }
            if(obj.containsKey("built_year")){
                value=obj.getString("built_year");
                document.put("built_year",value);
            }
            if(obj.containsKey("region")){
                value=obj.getString("region");
                document.put("region",value);
            }
            if(obj.containsKey("longitude")){

                document.put("lng",obj.getDouble("longitude"));
            }
            if(obj.containsKey("latitude")){

                document.put("lat",obj.getDouble("latitude"));
            }
            if(obj.containsKey("fitment")){
                value=obj.getString("fitment");
                document.put("fitment",value);
            }
            if(obj.containsKey("structure")){
               value=obj.getString("structure");
                document.put("structure",value);
            }
            if(obj.containsKey("eqiupment")){
                value=obj.getString("eqiupment");
                document.put("eqiupment",value);
            }
            if(obj.containsKey("")){
                value=obj.getString("");
                document.put("",value);
            }
            if(obj.containsKey("")){
                value=obj.getString("");
                document.put("",value);
            }

            String community=obj.getString("community");
            //燕郊
            if(community.contains("燕郊")){
                System.out.println(community);
            }else {
                coll_import.insert(document);
            }

            //System.out.println(document);


        }
    }
}
