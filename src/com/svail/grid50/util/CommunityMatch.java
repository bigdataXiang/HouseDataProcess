package com.svail.grid50.util;

import com.svail.util.FileTool;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import static com.svail.grid50.GPStoGaoDe_2.wgs84toGaoDe;

/**
 * Created by ZhouXiang on 2016/12/25.
 * 将【D:\小论文\poi资料\小区\小区地理编码原始数据】中的
 * 【所有小区名称_去除冗余_匹配成功.txt】文件挑选出最精准
 * 地址
 *
 */
public class CommunityMatch {
    public static void main(String[] args){
        //precise_Match("D:\\小论文\\poi资料\\小区\\小区地理编码原始数据\\所有小区名称_去除冗余_匹配成功.txt");
        //get_precise_Match("D:\\小论文\\poi资料\\shpPoi\\P19住宅小区_point_json.txt",
                //"D:\\小论文\\poi资料\\小区\\小区地理编码原始数据\\所有小区名称_去除冗余_匹配成功_部分匹配.txt");
        processGeoCode_wanquan("D:\\小论文\\poi资料\\小区\\小区地理编码原始数据\\所有小区名称_去除冗余_匹配不成功_原始_地址部分匹配.txt");
    }

    //将shp_poi匹配的数据分为部分匹配和完全匹配
    public static void precise_Match(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");
        String poi;
        JSONObject obj;
        String community;
        JSONArray match;
        for(int i=0;i<pois.size();i++){
            poi=pois.elementAt(i);
            obj=JSONObject.fromObject(poi);
            Iterator<String> keys=obj.keys();
            while (keys.hasNext()){
                community=keys.next();
                match=obj.getJSONArray(community);

                JSONObject obj_new=new JSONObject();
                obj_new.put("community",community);
                if(match.size()>1){
                    for(int j=0;j<match.size();j++){
                        String str=match.get(j).toString();
                        obj_new.put("match",str);
                        if(str.equals(community)){
                            FileTool.Dump(obj_new.toString(),file.replace(".txt","_完全匹配.txt"),"utf-8");
                            break;
                        }else {
                            FileTool.Dump(obj_new.toString(),file.replace(".txt","_部分匹配.txt"),"utf-8");
                            break;
                        }
                    }
                }else {
                    String str=match.get(0).toString();
                    obj_new.put("match",str);
                    if(str.equals(community)){
                        FileTool.Dump(obj_new.toString(),file.replace(".txt","_完全匹配.txt"),"utf-8");
                    }else {
                        FileTool.Dump(obj_new.toString(),file.replace(".txt","_部分匹配.txt"),"utf-8");
                    }
                }
            }

        }
    }

    //得到shp_poi的数据的经纬度
    public static void get_precise_Match(String source,String target){
        Vector<String> pois=FileTool.Load(source,"utf-8");
        Map<String,String> community_map=new HashMap<>();
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            String NAME=obj.getString("NAME");
            String DISPLAY_X=obj.getString("DISPLAY_X");//"DISPLAY_X":"116.35015","DISPLAY_Y":"39.74008"
            String DISPLAY_Y=obj.getString("DISPLAY_Y");
            community_map.put(NAME,DISPLAY_X+","+DISPLAY_Y);
        }

        pois=FileTool.Load(target,"utf-8");
        for(int i=0;i<pois.size();i++) {
            String poi = pois.elementAt(i);
            JSONObject obj = JSONObject.fromObject(poi);
            String[] lnglat=community_map.get(obj.getString("match")).split(",");
            obj.put("lng",lnglat[0]);
            obj.put("lat",lnglat[1]);
            FileTool.Dump(obj.toString(),target.replace(".txt","_shpPoi匹配结果.txt"),"utf-8");
        }
    }

    //处理老师地理编码中完全匹配的数据，需要纠偏
    public static void processGeoCode_wanquan(String file){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        for(int i=0;i<pois.size();i++) {
            String poi = pois.elementAt(i);
            System.out.println(i+":"+poi);
            String community=poi.substring(0,poi.indexOf(";"));
            JSONObject obj = JSONObject.fromObject(poi.substring(poi.indexOf(";")+";".length()));
            double[] lnglat=wgs84toGaoDe(obj.getDouble("lng"),obj.getDouble("lat"));
            double lng=lnglat[0];
            double lat=lnglat[1];

            obj.put("community",community);
            obj.put("lng_gd",lng);
            obj.put("lat_gd",lat);

            FileTool.Dump(obj.toString(),file.replace(".txt","_高德纠偏.txt"),"utf-8");
        }
    }
}
