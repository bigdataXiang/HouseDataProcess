package com.svail.grid50;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.grid50.util.db;
import com.svail.util.FileTool;
import com.svail.util.Tool;
import net.sf.json.JSONObject;
import org.apache.poi.ss.formula.functions.T;

import java.util.Vector;

/**
 * Created by ZhouXiang on 2016/12/19.
 * 将老师给的大众点评上的poi进行json格式化，
 * 并且通过抓取大众上的分类进行poi的分类
 */
public class DaZhongDianPing {
    public static void main(String[] args){
        //poiToJson("D:\\小论文\\poi资料\\poi.txt");
        deuplicate("D:\\小论文\\poi资料\\poi_json.txt");
    }
    //将老师的poi转成json格式，并且将数据存入mongodb中，不去重
    public static void poiToJson(String file){
        DBCollection coll= db.getDB("pois").getCollection("poi");
        BasicDBObject doc;
        Vector<String> pois= FileTool.Load(file,"utf-8");
        String poi;
        String NAME;
        String ADDRESS;
        String PRICE;
        String[] LNGLAT;
        String URL;
        String TAG;

        for(int i=0;i<pois.size();i++){
            poi=pois.elementAt(i);
            doc=new BasicDBObject();

            if(poi.indexOf("NAME")!=-1){
                NAME= Tool.getStrByKey(poi,"<NAME>","</NAME>","</NAME>");
                doc.put("NAME",NAME);
            }
            if(poi.indexOf("ADDRESS")!=-1){
                ADDRESS= Tool.getStrByKey(poi,"<ADDRESS>","</ADDRESS>","</ADDRESS>");
                doc.put("ADDRESS",ADDRESS);
            }

            if(poi.indexOf("PRICE")!=-1){
                PRICE= Tool.getStrByKey(poi,"<PRICE>","</PRICE>","</PRICE>");
                doc.put("PRICE",PRICE);
            }
            if(poi.indexOf("LNGLAT")!=-1){
                LNGLAT= Tool.getStrByKey(poi,"<LNGLAT>","</LNGLAT>","</LNGLAT>").split(";");
                doc.put("LNG",Double.parseDouble(LNGLAT[0]));
                doc.put("LAT",Double.parseDouble(LNGLAT[1]));
            }
            if(poi.indexOf("URL")!=-1){
                URL= Tool.getStrByKey(poi,"<URL>","</URL>","</URL>");
                doc.put("URL","http://www.dianping.com/"+URL);
            }
            if(poi.indexOf("TAG")!=-1){
                TAG= Tool.getStrByKey(poi,"<TAG>","</TAG>","</TAG>");
                doc.put("TAG",TAG);
            }

            FileTool.Dump(doc.toString(),file.replace(".txt","_json.txt"),"utf-8");
            coll.insert(doc);
        }
    }

    //将mongo中去重复后的数据存储到本地一份
    public static void deuplicate(String file){
        DBCollection coll= db.getDB("pois").getCollection("poi");
        DBCursor cs=coll.find();
        BasicDBObject doc;
        while (cs.hasNext()){
            doc=(BasicDBObject)cs.next();
            doc.remove("_id");
            FileTool.Dump(doc.toString(),file.replace(".txt","_deuplicate.txt"),"utf-8");
        }
    }

    public static void getTag(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");
        String poi;
        String URL;
        String TAG;
        JSONObject obj;
        for(int i=0;i<pois.size();i++){
            poi=pois.elementAt(i);
            obj=JSONObject.fromObject(poi);
            if(obj.containsKey("URL")){
                URL=obj.getString("URL");
            }
        }
    }


}
