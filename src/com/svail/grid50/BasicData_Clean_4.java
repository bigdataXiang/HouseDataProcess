package com.svail.grid50;

import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.Iterator;

import static com.svail.grid50.util.PoiCode.setPoiCode_50;
import static com.svail.util.Tool.isNumeric;

/**
 * Created by ZhouXiang on 2016/12/14.
 */
public class BasicData_Clean_4 {
    public static void main(String[] args){
        clean();
    }

    public static void clean(){
        Mongo m_import=null;

        try {
            m_import=new Mongo("127.0.0.1",27017);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DB db_import=m_import.getDB("paper");

        DBCollection coll_export=db_import.getCollection("BasicData_Resold_50");
        DBCollection coll_import=db_import.getCollection("BasicData_Resold");

        DBCursor cs=coll_export.find();
        BasicDBObject document;

        int count=0;
        System.out.println("begin:");
        while(cs.hasNext()){
            count++;
            document=(BasicDBObject)cs.next();
            BasicDBObject doc=makeValueString(document);
            //System.out.println(document);
            try{
                if(doc.containsField("community")){
                    String com=doc.getString("community");
                    if(com.contains("燕郊")){
                        System.out.println(doc.getString("source")+"含有燕郊的数据");
                    }else {
                        coll_import.insert(doc);
                        System.out.println(count);
                    }
                }else {
                    System.out.println(count+"：没有小区数据");
                    coll_import.insert(doc);
                }

            }catch( MongoInternalException e){
                e.getStackTrace();
            }/*catch (NullPointerException e){
                System.out.println(document);
            }*/
        }
    }

    public static BasicDBObject makeValueString(BasicDBObject obj){

        BasicDBObject document=new BasicDBObject();
        String value="";

        String url="";
        if(obj.containsField("url")){
            url=obj.getString("url");
            document.put("url",url);
        }
        if(url.contains("fang")){
            document.put("source","fang");
        }else if(url.contains("5i5j")){
            document.put("source","woaiwojia");
        }else if(url.contains("anjuke")){
            document.put("source","anjuke");
        }else if(url.contains("lianjia")){
            document.put("source","lianjia");
        }
        if(obj.containsField("region")){
            value=obj.getString("region");
            document.put("region",value);
        }
        if(obj.containsField("location")){
            value=obj.getString("location");
            document.put("location",value);
        }
        if(obj.containsField("community")){
            value=obj.getString("community");
            document.put("community",value);
        }
        String price="";
        if(obj.containsField("price")){
            price=obj.getString("price").replace("万","").replace("元","");
            document.put("price",price);
        }
        String area="";
        if(obj.containsField("area")){
            area=obj.getString("area").replace("㎡","").replace("平方米","").replace("平米","").replace("建筑面积：","").replace("�","").replace("O","");
            document.put("area",area);
        }
        if(obj.containsField("unit_price")){
            value=obj.getString("unit_price").replace("元","");
            document.put("unit_price",value);
        }else if(price.length()!=0&&area.length()!=0){
            double up=Double.parseDouble(price)/Double.parseDouble(area);
            document.put("unit_price",""+up);
        }else {
            System.out.println("面积和价格数据没有");
        }
        if(obj.containsField("year")){
            document.put("year",obj.getString("year"));
        }
        if(obj.containsField("month")){
            document.put("month",obj.getString("month"));
        }
        if(obj.containsField("day")){
            document.put("day",obj.getString("day"));
        }
        if(obj.containsField("time")){
            value=obj.getString("time");
            document.put("time",value);
        }
        String longitude="";
        if(obj.containsField("lng")){
            longitude=obj.getString("lng");
            document.put("lng",longitude);
        }
        String latitude="";
        if(obj.containsField("lat")){
            latitude=obj.getString("lat");
            document.put("lat",latitude);
        }
        if(longitude.length()!=0&&latitude.length()!=0){
            String[] result=setPoiCode_50(Double.parseDouble(latitude),Double.parseDouble(longitude)).split(",");
            String code = result[0];
            String row=result[1];
            String col=result[2];
            document.put("code",code);
            document.put("row",row);
            document.put("col",col);
        }

        if(obj.containsField("house_type")){
            String ht=obj.getString("house_type");
            document.put("house_type",ht);
        }
        if(obj.containsField("rooms")){
            document.put("rooms",obj.getString("rooms"));
        }
        if(obj.containsField("halls")){
            document.put("halls",obj.getString("halls"));
        }
        if(obj.containsField("kitchen")){
            document.put("kitchen",obj.getString("kitchen"));
        }
        if(obj.containsField("bathrooms")){
            document.put("bathrooms",obj.getString("bathrooms"));
        }
        if(obj.containsField("floor")){
            String floor=obj.getString("floor").replace("层","").replace("第","").replace("共","");
            document.put("floor",floor);
        }
        if(obj.containsField("flooron")){
            document.put("flooron",obj.getString("flooron"));
        }
        if(obj.containsField("floors")){
            document.put("floors",obj.getString("floors"));
        }
        if(obj.containsField("fitment")){
            value=obj.getString("fitment");
            document.put("fitment",value);
        }
        if(obj.containsField("title")){
            value=obj.getString("title").replace("二手房","").replace(";","").replace("","");
            document.put("title",value);
        }
        if(obj.containsField("property_company")){
            value=obj.getString("property_company");
            document.put("property_company",value);
        }
        if(obj.containsField("green_rate")){
            value=obj.getString("green_rate");
            document.put("green_rate",value);
        }
        if(obj.containsField("direction")){
            value=obj.getString("direction");
            document.put("direction",value);
        }
        if(obj.containsField("heat_supply")){
            value=obj.getString("heat_supply");
            document.put("heat_supply",value);
        }
        if(obj.containsField("property")){
            value=obj.getString("property");
            document.put("property",value);
        }
        if(obj.containsField("property_fee")){
            value=obj.getString("property_fee");
            document.put("property_fee",value);
        }
        if(obj.containsField("down_payment")){
            value=obj.getString("down_payment");
            document.put("down_payment",value);
        }
        if(obj.containsField("volume_rate")){
            value=obj.getString("volume_rate");
            document.put("volume_rate",value);
        }
        if(obj.containsField("households")){
            value=obj.getString("households");
            document.put("households",value);
        }
        if(obj.containsField("developer")){
            value=obj.getString("developer");
            document.put("developer",value);
        }
        if(obj.containsField("totalarea")){
            value=obj.getString("totalarea");
            document.put("totalarea",value);
        }
        if(obj.containsField("month_payment")){
            value=obj.getString("month_payment");
            document.put("month_payment",value);
        }
        if(obj.containsField("built_year")){
            value=obj.getString("built_year");
            document.put("built_year",value);
        }
        if(obj.containsField("structure")){
            value=obj.getString("structure");
            document.put("structure",value);
        }
        if(obj.containsField("eqiupment")){
            value=obj.getString("eqiupment");
            document.put("eqiupment",value);
        }

        return document;
    }
}
