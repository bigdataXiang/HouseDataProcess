package com.svail.grid50.util.geoCorrecting;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.grid50.util.db;
import com.svail.util.FileTool;
import com.svail.util.StrBsonTransfer;
import com.svail.util.Tool;
import net.sf.json.JSONObject;

import java.io.File;
import java.util.*;

import static com.svail.grid50.util.PoiCode.setPoiCode_50;

/**
 * Created by ZhouXiang on 2016/12/30.
 *将所有小区的地理编码校正一次，包括高德的和老师的地理编码
 */
public class GeoCodeCorrect {
    public static void main(String[] args){

        String path="D:\\小论文\\poi资料\\小区\\小区地理编码原始数据\\最后结果\\校对结果\\";
        //D:\小论文\poi资料\小区\小区地理编码原始数据\最后结果
        //wanquanpipei("D:\\小论文\\poi资料\\小区\\小区地理编码原始数据\\最后结果\\所有小区名称_地理编码_地址完全匹配_高德纠偏_高德解析信息.txt");

        //wanquanpipei_again("D:\\小论文\\poi资料\\小区\\小区地理编码原始数据\\最后结果\\所有小区名称_地理编码_地址完全匹配_高德纠偏_高德解析信息_tidy.txt");

        //bufenpipei("D:\\小论文\\poi资料\\小区\\小区地理编码原始数据\\最后结果\\所有小区名称_地理编码_地址部分匹配_高德纠偏_高德解析信息.txt");

        //shoudongshiqu(path+"所有小区名称_手动拾取.txt");

        //shppoi(path+"所有小区名称_shpPoi匹配_部分匹配.txt");

        //gaode(path+"所有小区名称__高德解析信息.txt");

        //communityMatch(path+"所有小区名称_手动拾取_tidy.txt");

        //addressMatch(path+"还是找不到匹配的小区啊.txt",path+"没有小区啊.txt");

        //dumpCommunity(path+"所有小区匹配数据汇总.txt");

        //nomatch(path+"找不到匹配的小区啊~~.txt");

        //luanma(path+"乱码小区.txt");

        dahuanxue(path+"code为负的小区信息.txt");
    }

    //校对老师的完全匹配的数据:6020条
    //level,community,formatted_address,district,lng_gd,lat_gd,nlp_status,matched,source,region,lng,lat
    public static void wanquanpipei(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");
        JSONObject obj=new JSONObject();
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            String[] array={"","",""};
            if(poi.endsWith("}")){
                obj=JSONObject.fromObject(poi);
            }else {
                String s=poi.substring(0,poi.indexOf("}",poi.indexOf("community"))+"}".length());
                String e=poi.substring(poi.indexOf("}",poi.indexOf("community"))+"}".length());
                obj=JSONObject.fromObject(s);
                array=e.split(";");
            }
            JSONObject result=new JSONObject();
            if(obj.containsKey("level")){
                result.put("level",obj.getString("level"));
            }else {
                result.put("level","兴趣点");
            }
            if(obj.containsKey("community")){
                result.put("community",obj.getString("community"));
            }
            if(obj.containsKey("formatted_address")){
                result.put("formatted_address",obj.getString("formatted_address"));
            }else {
                result.put("formatted_address",array[1]);
            }
            if(obj.containsKey("district")){
                result.put("district",obj.getString("district"));
            }else {
                if(array.length==3){
                    result.put("district",array[2]);
                }
            }
            String[] lnglat=new String[2];
            if(array[0].length()!=0){
                lnglat=array[0].split(",");
            }
            if(obj.containsKey("lng_gd")){
                result.put("lng_gd",obj.getString("lng_gd"));
            }else {
                result.put("lng_gd",lnglat[0]);
            }
            if(obj.containsKey("lat_gd")){
                result.put("lat_gd",obj.getString("lat_gd"));
            }else{
                result.put("lat_gd",lnglat[1]);
            }
            if(obj.containsKey("nlp_status")){
                result.put("nlp_status",obj.getString("nlp_status"));
            }
            if(obj.containsKey("matched")){
                result.put("matched",obj.getString("matched"));
            }
            if(obj.containsKey("source")){
                result.put("source",obj.getString("source"));
            }
            if(obj.containsKey("region")){
                result.put("region",obj.getString("region"));
            }
            if(obj.containsKey("lng")){
                result.put("lng",obj.getString("lng"));
            }
            if(obj.containsKey("lat")){
                result.put("lat",obj.getString("lat"));
            }
            System.out.println(i+":"+result);
            FileTool.Dump(result.toString(),file.replace(".txt","_tidy.txt"),"utf-8");
        }
    }

    //对【wanquanpipei】的结果再次调整
    public static void wanquanpipei_again(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");
        String lng_gd="";
        String lat_gd="";
        for(int i=0;i<pois.size();i++) {
            String poi = pois.elementAt(i);
            if(poi.startsWith("{")){
                JSONObject obj=JSONObject.fromObject(poi);
                String str=obj.getString("community")+","+obj.getString("formatted_address")+","+"("+obj.getString("lng_gd")+","+obj.getString("lat_gd")+")";
                FileTool.Dump(poi,file.replace(".txt","_tidy.txt"),"utf-8");
                FileTool.Dump(str,file.replace(".txt","_对比.txt"),"utf-8");
            }else {
                String[] str=poi.substring(0,poi.indexOf("{")).split(",");
                lng_gd=str[0];
                lat_gd=str[1];
                JSONObject obj=JSONObject.fromObject(poi.substring(poi.indexOf("{")));
                obj.put("level","兴趣点");
                obj.put("lng_gd",lng_gd);
                obj.put("lat_gd",lat_gd);
                String str1=obj.getString("community")+","+obj.getString("formatted_address")+","+"("+str+")";

                FileTool.Dump(obj.toString(),file.replace(".txt","_tidy.txt"),"utf-8");
                FileTool.Dump(str1,file.replace(".txt","_对比.txt"),"utf-8");
            }
        }
    }

    //校对老师部分匹配的数据：2682条
    public static void bufenpipei(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");
        for(int i=0;i<pois.size();i++) {
            String poi = pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            JSONObject result=new JSONObject();

            if(obj.containsKey("level")){
                result.put("level",obj.getString("level"));
            }
            if(obj.containsKey("community")){
                result.put("community",obj.getString("community"));
            }
            if(obj.containsKey("formatted_address")){
                result.put("formatted_address",obj.getString("formatted_address"));
            }
            if(obj.containsKey("district")){
                result.put("district",obj.getString("district"));
            }
            if(obj.containsKey("lng_gd")){
                result.put("lng_gd",obj.getString("lng_gd"));
            }
            if(obj.containsKey("lat_gd")){
                result.put("lat_gd",obj.getString("lat_gd"));
            }
            if(obj.containsKey("nlp_status")){
                result.put("nlp_status",obj.getString("nlp_status"));
            }
            if(obj.containsKey("matched")){
                result.put("matched",obj.getString("matched"));
            }
            if(obj.containsKey("source")){
                result.put("source",obj.getString("source"));
            }
            if(obj.containsKey("region")){
                result.put("region",obj.getString("region"));
            }
            if(obj.containsKey("lng")){
                result.put("lng",obj.getString("lng"));
            }
            if(obj.containsKey("lat")){
                result.put("lat",obj.getString("lat"));
            }
            System.out.println(i+1+":"+obj);
            String s="";
            if(obj.containsKey("formatted_address")){
                s=obj.getString("formatted_address");
            }
            String str=obj.getString("community")+","+s+","+"("+obj.getString("lng_gd")+","+obj.getString("lat_gd")+")";
            FileTool.Dump(str,file.replace(".txt","_对比.txt"),"utf-8");
            FileTool.Dump(result.toString(),file.replace(".txt","_tidy.txt"),"utf-8");
        }
    }

    //校对手动拾取坐标的数据：208
    public static void shoudongshiqu(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");
        for(int i=0;i<pois.size();i++) {
            String[] poi = pois.elementAt(i).split(";");
            System.out.println(i+1+":"+poi[2]);
            String[] lnglat=poi[2].split(",");
            //System.out.println(i+1+":"+pois.elementAt(i));

            JSONObject result = new JSONObject();
            result.put("level","兴趣点");
            result.put("community",poi[0]);
            result.put("formatted_address",poi[1]);
            result.put("lng_gd",lnglat[0]);
            result.put("lat_gd",lnglat[1]);

            FileTool.Dump(result.toString(),file.replace(".txt","_tidy.txt"),"utf-8");
        }
    }

    //处理shppoi部分匹配和完全匹配的数据
    //完全匹配：2314
    //部分匹配：2020
    public static void shppoi(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");
        for(int i=0;i<pois.size();i++) {
            String poi = pois.elementAt(i);
            System.out.println(i+1+":"+poi);
            JSONObject obj=JSONObject.fromObject(poi);

            JSONObject result = new JSONObject();
            result.put("level","兴趣点");
            result.put("community",obj.getString("community"));
            result.put("formatted_address",obj.getString("match"));
            result.put("lng_gd",obj.getString("lng"));
            result.put("lat_gd",obj.getString("lat"));

            FileTool.Dump(result.toString(),file.replace(".txt","_tidy.txt"),"utf-8");
        }
    }

    //处理高德解析的数据:918
    public static void gaode(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");
        for(int i=0;i<pois.size();i++) {
            System.out.println(i+1);
            String poi = pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);

            JSONObject result = new JSONObject();
            result.put("level",obj.getString("level"));
            result.put("community",obj.getString("community"));
            result.put("formatted_address",obj.getString("formatted_address"));
            result.put("district",obj.getString("district"));
            result.put("lng_gd",obj.getString("lng"));
            result.put("lat_gd",obj.getString("lat"));

            FileTool.Dump(result.toString(),file.replace(".txt","_tidy.txt"),"utf-8");
        }
    }

    //将所有的校对的小区数据存入到数据库【community】:14162
    public static void communityMatch(String file){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        DBCollection collection= db.getDB("paper").getCollection("community");

        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i).replace("・","").replace("•","").replace("·","")
                        .replace("・","").replace("�","").replace("・","").replace("・","");
            BasicDBObject document= StrBsonTransfer.strToBson(poi);
            collection.insert(document);
        }
    }

    //将所有的基础数据【BasicData_Resold】要重新匹配
    public static void addressMatch(String file,String nocommunity){
        DBCollection coll_Data=db.getDB("paper").getCollection("BasicData_Resold");
        DBCollection coll_Community=db.getDB("paper").getCollection("community");

        Map<String,BasicDBObject> map=new HashMap<>();
        DBCursor cs=coll_Community.find();
        BasicDBObject doc;
        while (cs.hasNext()){
            doc=(BasicDBObject)cs.next();
            String community=doc.getString("community");
            map.put(community,doc);
        }

        DBCursor cs_data=coll_Data.find();
        int count=0;
        while (cs_data.hasNext()){
            count++;
            System.out.println(count);
            doc=(BasicDBObject)cs_data.next();

            if(doc.containsField("community")){
                String community=doc.getString("community").replace("・","").replace("•","").replace("·","")
                        .replace("・","").replace("�","").replace("・","").replace("・","");

                community= Tool.delect_content_inBrackets(community,"(",")");

                if(map.containsKey(community)){

                }else {
                    FileTool.Dump(doc.toString(),file,"utf-8");
                }
            }

        }
    }

    //将数据库【community】中的数据存一份到本地备份
    public static void dumpCommunity(String file){
        DBCollection coll_Community=db.getDB("paper").getCollection("community");
        DBCursor cs=coll_Community.find();
        BasicDBObject doc;
        while (cs.hasNext()){
            doc=(BasicDBObject)cs.next();
            doc.remove("_id");
            FileTool.Dump(doc.toString(),file,"utf-8");
        }
    }

    //将【找不到匹配的小区啊~~.txt】的小区数据整理一下
    //并且存入数据库中
    public static void nomatch(String file){
        DBCollection coll=db.getDB("paper").getCollection("community");
        Vector<String> pois=FileTool.Load(file,"utf-8");
        for (int i=0;i<pois.size();i++){
            System.out.println(i+1);
            String poi=pois.elementAt(i);
            String[] array=poi.split(";");
            BasicDBObject result=new BasicDBObject();
            if(array[1].startsWith("{")){
                JSONObject obj=JSONObject.fromObject(array[1]);
                result.put("level","兴趣点");
                result.put("community",array[0]);
                result.put("formatted_address",obj.getString("community")+";"+obj.getString("formatted_address"));
                result.put("lng_gd",obj.getString("lng_gd"));
                result.put("lat_gd",obj.getString("lat_gd"));

                coll.insert(result);
                FileTool.Dump(result.toString(),file.replace(".txt","_tidy.txt"),"utf-8");

            }else {
                result.put("level","兴趣点");
                result.put("community",array[0]);
                String[] lnglat=array[1].split(",");
                result.put("lng_gd",lnglat[0]);
                result.put("lat_gd",lnglat[1]);

                coll.insert(result);
                FileTool.Dump(result.toString(),file.replace(".txt","_tidy.txt"),"utf-8");
            }
        }
    }

    //将乱码的小区的校正情况输入到数据库中
    public static void luanma(String file){
        DBCollection coll=db.getDB("paper").getCollection("community");
        Vector<String> pois=FileTool.Load(file,"utf-8");
        for (int i=0;i<pois.size();i++) {
            System.out.println(i + 1);
            String poi = pois.elementAt(i);
            String[] array = poi.split(";");

            String community=array[0].replace("・","").replace("•","").replace("·","")
                    .replace("・","").replace("�","").replace("・","").replace("・","");;
            String formatted_address=array[1];
            String[] lnglat=array[2].split(",");
            BasicDBObject result = new BasicDBObject();
            result.put("level","兴趣点");
            result.put("community",community);
            result.put("formatted_address",formatted_address);
            result.put("lng_gd",lnglat[0]);
            result.put("lat_gd",lnglat[1]);
            coll.insert(result);
            FileTool.Dump(result.toString(),file.replace(".txt","_tidy.txt"),"utf-8");
        }
    }

    //将所有的【BasicData_Resold】数据换一次血，并且另存到【BasicData_Resold_gd】
    //一共是3016388条原始数据
    //由于固安等地方的经纬度数在北京的经纬度范围之外，所以计算出来的结果是负数，因此
    //这类结果不应该存储在数据库中，
    //最后入库共计3008811
    public static void dahuanxue(String file){
        DBCollection coll_community=db.getDB("paper").getCollection("community");
        DBCollection coll_BR=db.getDB("paper").getCollection("BasicData_Resold");
        DBCollection coll_BR_gd=db.getDB("paper").getCollection("BasicData_Resold_gd");

        Map<String,BasicDBObject> map=new HashMap<>();
        DBCursor cs=coll_community.find();
        BasicDBObject doc;
        while (cs.hasNext()){
            doc=(BasicDBObject)cs.next();
            String community=doc.getString("community");
            map.put(community,doc);
        }

        DBCursor cs_data=coll_BR.find();
        int count=0;
        while (cs_data.hasNext()){
            count++;
            System.out.println(count);
            doc=(BasicDBObject)cs_data.next();

            if(doc.containsField("community")){
                String community=doc.getString("community").replace("・","").replace("•","").replace("·","")
                        .replace("・","").replace("�","").replace("・","").replace("・","");

                community= Tool.delect_content_inBrackets(community,"(",")");

                if(map.containsKey(community)){
                    BasicDBObject document=map.get(community);
                    doc.remove("lng");
                    doc.remove("lat");
                    doc.remove("code");
                    doc.remove("row");
                    doc.remove("col");
                    //System.out.println(doc);

                    double lng_gd=Double.parseDouble(document.getString("lng_gd"));
                    double lat_gd=Double.parseDouble(document.getString("lat_gd"));
                    String[] crc=setPoiCode_50(lat_gd,lng_gd).split(",");
                    doc.put("community",community);


                    doc.put("code",crc[0]);
                    doc.put("row",crc[1]);
                    doc.put("col",crc[2]);

                    if(document.containsField("formatted_address")){
                        doc.put("formatted_address",document.getString("formatted_address"));
                    }
                    if(document.containsField("lng_gd")){
                        doc.put("lng_gd",""+lng_gd);
                    }
                    if(document.containsField("lat_gd")){
                        doc.put("lat_gd",""+lat_gd);
                    }

                    //System.out.println(doc);
                    int code=Integer.parseInt(crc[0]);
                    if(code<0){
                        FileTool.Dump(doc.toString(),file,"utf-8");
                    }else {
                        coll_BR_gd.insert(doc);
                    }

                }else {
                    System.out.println(doc);
                }
            }

        }
    }
}
