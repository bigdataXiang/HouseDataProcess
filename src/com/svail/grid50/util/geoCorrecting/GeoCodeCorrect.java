package com.svail.grid50.util.geoCorrecting;

import com.svail.util.FileTool;
import net.sf.json.JSONObject;

import java.io.File;
import java.util.Vector;

/**
 * Created by ZhouXiang on 2016/12/30.
 *将所有小区的地理编码校正一次，包括高德的和老师的地理编码
 */
public class GeoCodeCorrect {
    public static void main(String[] args){

        String path="D:\\小论文\\poi资料\\小区\\小区地理编码原始数据\\最后结果\\";
        //D:\小论文\poi资料\小区\小区地理编码原始数据\最后结果
        //wanquanpipei("D:\\小论文\\poi资料\\小区\\小区地理编码原始数据\\最后结果\\所有小区名称_地理编码_地址完全匹配_高德纠偏_高德解析信息.txt");

        //wanquanpipei_again("D:\\小论文\\poi资料\\小区\\小区地理编码原始数据\\最后结果\\所有小区名称_地理编码_地址完全匹配_高德纠偏_高德解析信息_tidy.txt");

        //bufenpipei("D:\\小论文\\poi资料\\小区\\小区地理编码原始数据\\最后结果\\所有小区名称_地理编码_地址部分匹配_高德纠偏_高德解析信息.txt");

        //shoudongshiqu(path+"所有小区名称_手动拾取.txt");

        //shppoi(path+"所有小区名称_shpPoi匹配_部分匹配.txt");
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

    //处理高德解析的数据
    public static void gaode(){

    }
}
