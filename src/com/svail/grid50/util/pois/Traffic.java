package com.svail.grid50.util.pois;

import com.svail.util.FileTool;
import net.sf.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import static com.svail.grid50.util.PoiCode.setPoiCode_50;

/**
 * Created by ZhouXiang on 2016/12/21.
 */
public class Traffic {
    public static void main(String[] args){
        //subWay("D:\\小论文\\poi资料\\地铁\\地铁线路.txt");
        //busStation("D:\\小论文\\poi资料\\shpPoi\\P05公交车站_point_json.txt");
        String path="D:\\小论文\\poi资料\\地铁\\";
        /*String[] names={
                "1号线.txt","2号线.txt","4号线.txt","5号线.txt","6号线.txt","7号线.txt","8号线.txt","9号线.txt","10号线.txt",
                "13号线.txt","14号线.txt","15号线.txt","八通线.txt","昌平线.txt","大兴线.txt","房山线.txt","机场线.txt","亦庄线.txt",
        };
        for(int i=0;i<names.length;i++){
            subwaytogether(path+names[i].replace(".txt","_json_高德解析信息.txt"),path+"totalsubway.txt");
        }
*/

        //subway_code(path+"totalsubway.txt");

        //busstation_code("D:\\paper\\relative\\201507\\公交\\公交车站_高德解析信息.txt");
    }
    public static void subWay(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.get(i);

            FileTool.Dump("\""+poi+"地铁站\",",file.replace(".txt","_引号.txt"),"utf-8");
        }
    }

    public static void busStation(String file){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        Set<String> set=new HashSet<>();
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            String name=obj.getString("NAME");
            set.add(name);
        }

        Iterator<String> it=set.iterator();
        while (it.hasNext()){
            String name=it.next();
            JSONObject obj=new JSONObject();
            obj.put("name",name);
            FileTool.Dump(obj.toString(),file.replace(".txt","_公交站.txt"),"utf-8");
        }
    }

    //将所有地铁站数据变成json格式
    public static void subway_tojson(String file){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            System.out.println(poi);
            String station="";
            if(poi.indexOf(":")!=-1){
                station=poi.substring(poi.indexOf("、")+"、".length(),poi.indexOf(":"));
            }else if(poi.indexOf("：")!=-1){
                station=poi.substring(poi.indexOf("、")+"、".length(),poi.indexOf("："));
            }
            JSONObject obj=new JSONObject();
            obj.put("station",station);
            FileTool.Dump(obj.toString(),file.replace(".txt","_json.txt"),"utf-8");
        }
    }

    //将所有地铁站的数据汇集到一起
    public static void subwaytogether(String file,String totalfile){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            if(poi.endsWith("}")){
                JSONObject obj=JSONObject.fromObject(poi);
                FileTool.Dump(obj.toString(),totalfile,"utf-8");
            }else {
                String str=poi.substring(poi.indexOf("}")+"}".length());
                String[] lnglat=str.split(",");
                JSONObject obj=JSONObject.fromObject(poi.substring(0,poi.indexOf("}")+"}".length()));
                obj.put("lng_gd",lnglat[0]);
                obj.put("lat_gd",lnglat[1]);
                FileTool.Dump(obj.toString(),totalfile,"utf-8");
            }
        }
    }

    //将所有的地铁站进行格网编码
    public static void subway_code(String file){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        for(int i=0;i<pois.size();i++) {
            String poi = pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            double lng=obj.getDouble("lng_gd");
            double lat=obj.getDouble("lat_gd");
            String result=setPoiCode_50(lat,lng);
            String[] crc=result.split(",");
            obj.put("code",crc[0]);
            obj.put("row",crc[1]);
            obj.put("col",crc[2]);

            FileTool.Dump(obj.toString(),file.replace(".txt","_格网化.txt"),"utf-8");
        }
    }

    public static void busstation_code(String file){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            if(obj.containsKey("lng_gd")){
                double lng=obj.getDouble("lng_gd");
                double lat=obj.getDouble("lat_gd");
                String result=setPoiCode_50(lat,lng);
                String[] crc=result.split(",");
                obj.put("code",crc[0]);
                obj.put("row",crc[1]);
                obj.put("col",crc[2]);

                FileTool.Dump(obj.toString(),file.replace(".txt","_格网化.txt"),"utf-8");

            }
        }
    }
}
