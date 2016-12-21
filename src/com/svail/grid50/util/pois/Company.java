package com.svail.grid50.util.pois;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.grid50.util.db;
import com.svail.util.FileTool;
import net.sf.json.JSONObject;
import org.apache.commons.collections.Buffer;

import java.io.*;

/**
 * Created by ZhouXiang on 2016/12/20.
 */
public class Company {
    public static void main(String[] args){
       // getBeijingC("D:\\小论文\\poi资料\\企业\\remove_null.txt");
        fromMongo("D:\\小论文\\poi资料\\企业\\北京企业.txt");
    }
    public static void getBeijingC(String file){
        String[] keys=new String[20];
        try{
            int count=0;
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            for(String line=br.readLine();line!=null;line=br.readLine()){
                count++;

                if(count==1){
                    keys=line.split(",");
                }else {
                    String[] values=line.split(",");
                    JSONObject obj=new JSONObject();
                    if(keys.length==values.length){
                        for(int i=0;i<keys.length;i++){
                            obj.put(keys[i],values[i]);
                        }
                        if(obj.getString("company_region").contains("北京")){
                            FileTool.Dump(obj.toString(),file.replace(".txt","_json.txt"),"utf-8");
                        }
                    }else {
                        System.out.println(count+":"+line);
                    }
                }
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public static void fromMongo(String file){
        DBCollection coll= db.getDB("alibaba").getCollection("alibaba");
        BasicDBObject region=new BasicDBObject();
        BasicDBObject doc=new BasicDBObject();
        region.put("province","北京市");
        doc.put("company_region","北京市");

        DBCursor cs=coll.find(doc);
        while (cs.hasNext()){
            FileTool.Dump(cs.next().toString(),file,"utf-8");
        }

    }
    public static void crawl(){

    }
}
