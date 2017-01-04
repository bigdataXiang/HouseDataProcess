package com.svail.grid50.util.pois;

import com.svail.util.FileTool;
import net.sf.json.JSONObject;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import static com.svail.grid50.util.PoiCode.setPoiCode_50;

/**
 * Created by ZhouXiang on 2016/12/20.
 */
public class School {
    public static void main(String[] args){
        //toJson("D:\\小论文\\poi资料\\学校\\各区小学\\延庆县小学.txt");
        String path="D:\\小论文\\影响因素相关性研究\\201507\\学校\\";
        schoolCode_middle(path+"北京中学.txt");
    }
    public static void toJson(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");
        String poi="";
        String name="";
        String location;
        String code;
        String type;
        String[] temp;
        String qualify;
        String postCode;

        JSONObject obj;
        String first;
        for(int i=0;i<pois.size();i++){
            obj=new JSONObject();
            poi=pois.elementAt(i);
            temp=poi.split(",");
            if(temp.length>=4){
                obj.put("code",temp[0]);
                obj.put("name",temp[1]);
                obj.put("address",temp[2]);
                obj.put("location",temp[3]);
            }else{
                System.out.println(poi);
            }
            //System.out.println(obj);
            if(obj.size()!=0){
                FileTool.Dump(obj.toString(),file.replace(".txt","_json.txt"),"utf-8");
            }
        }
    }

    //将中学的数据排好名次
    public static void schoolCode_middle(String file){
        Vector<String> pois=FileTool.Load(file,"utf-8");

        for(int i=0;i<pois.size();i++) {
            String poi = pois.elementAt(i);
            JSONObject obj = JSONObject.fromObject(poi);

            int rank=obj.getInt("rank");
            String name=obj.getString("name");
            double lng_gd=obj.getDouble("lng_gd");
            double lat_gd=obj.getDouble("lat_gd");

            String result=setPoiCode_50(lat_gd,lng_gd);
            String[] crc=result.split(",");
            JSONObject o=new JSONObject();
            int qv=7-rank;
            o.put("qualify_value",qv);
            o.put("name",name);
            o.put("rank",rank);
            o.put("code",crc[0]);
            o.put("row",crc[1]);
            o.put("col",crc[2]);

            FileTool.Dump(o.toString(),file.replace(".txt","_格网化.txt"),"utf-8");

        }
    }

    //将小学的数据处理成含有格网编码的形式
    public static void schoolCode_primary(String file){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        String poi=pois.elementAt(0);
        JSONObject obj=JSONObject.fromObject(poi);

    }



    public  static int schoolLevel(String qualify){
        int value=1;
        switch (qualify){
            case "三级甲等":value=10;
                break;
            case "三级乙等":value=9;
                break;
            case "三级合格":value=8;
                break;
            case "二级甲等":value=7;
                break;
            case "二级乙等":value=6;
                break;
            case "二级合格":value=5;
                break;
            case "一级甲等":value=4;
                break;
            case "一级乙等":value=3;
                break;
            case "一级合格":value=2;
                break;
            case "未评级":value=1;
                break;

        }
        return value;
    }
}
