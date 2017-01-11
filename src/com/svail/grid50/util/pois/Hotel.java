package com.svail.grid50.util.pois;

import com.svail.util.FileTool;
import net.sf.json.JSONObject;

import java.util.Vector;

import static com.svail.grid50.util.PoiCode.setPoiCode_50;

/**
 * Created by ZhouXiang on 2017/1/10.
 */
public class Hotel {
    public static void main(String[] args){

        //hotelCode("D:\\paper\\relative\\201507\\宾馆酒店\\P13宾馆酒店_point_json.txt");

        //hotelCode("D:\\paper\\relative\\201507\\餐饮服务\\P21餐饮服务_point_json.txt");

       //hotelCode("D:\\paper\\relative\\201507\\公司企业\\P17公司企业_point_json.txt");


        //hotelCode("D:\\paper\\relative\\201507\\零售行业\\P12零售行业_point_json.txt");

        //hotelCode("D:\\paper\\relative\\201507\\商业大楼\\P11商业大厦_point_json.txt");

        //hotelCode("D:\\paper\\relative\\201507\\休闲娱乐\\P14休闲娱乐_point_json.txt");


        //hotelCode("D:\\paper\\relative\\201507\\银行\\P10金融服务_point_json.txt");
    }
    public static void hotelCode(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");

        for(int i=0;i<pois.size();i++) {
            String poi = pois.elementAt(i);
            JSONObject obj = JSONObject.fromObject(poi);
            double lng_gd=obj.getDouble("DISPLAY_X");
            double lat_gd=obj.getDouble("DISPLAY_Y");

            String result = setPoiCode_50(lat_gd, lng_gd);
            String[] crc = result.split(",");
            obj.put("code",crc[0]);
            obj.put("row",crc[1]);
            obj.put("col",crc[2]);

            FileTool.Dump(obj.toString(),file.replace(".txt","_格网化.txt"),"utf-8");
        }
    }
}
