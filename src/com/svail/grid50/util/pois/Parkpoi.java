package com.svail.grid50.util.pois;

import com.svail.util.FileTool;
import net.sf.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

import static com.svail.grid50.BatchProcess_1.addressMatch_GaoDe;
import static com.svail.grid50.util.PoiCode.setPoiCode_50;

/**
 * Created by ZhouXiang on 2017/1/10.
 * 将poi的地址信息转换成高德信息，并且格网化
 */
public class Parkpoi {
    public static void main(String[] args){

        gongyuanlvdi("D:\\paper\\relative\\201507\\公园绿地\\公园绿地_region_json_去重_高德解析信息.txt");
    }
    public static void gaode() throws UnsupportedEncodingException {

        String path="D:\\paper\\relative\\201507\\公园绿地\\";
        String[] keys={"NAME"};//"address","name","community",,"school"
        addressMatch_GaoDe(0,keys,path,"公园绿地_region_json_去重.txt","北京","0e0926480b9a9118fb2b9d3238a20ce1");



    }

    //将公园绿地的数据格网化
    public static void gongyuanlvdi(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");
        String json="";
        String lnglat="";
        JSONObject obj;
        double lng_gd;
        double lat_gd;
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);

            if(poi.endsWith("}")){
                json=poi;
                obj=JSONObject.fromObject(json);
                //System.out.println(obj);
                lng_gd=obj.getDouble("lng_gd");
                lat_gd=obj.getDouble("lat_gd");

                String result = setPoiCode_50(lat_gd, lng_gd);
                String[] crc = result.split(",");
                obj.put("code",crc[0]);
                obj.put("row",crc[1]);
                obj.put("col",crc[2]);
            }else {
                json=poi.substring(0,poi.indexOf("}")+"}".length());
                obj=JSONObject.fromObject(json);
                lnglat=poi.substring(poi.indexOf("}")+"}".length());
                String[] ll=lnglat.split(",");


                lng_gd=Double.parseDouble(ll[0]);
                lat_gd=Double.parseDouble(ll[1]);
                obj.put("lng_gd",lng_gd);
                obj.put("lat_gd",lat_gd);

                String result = setPoiCode_50(lat_gd, lng_gd);
                String[] crc = result.split(",");
                obj.put("code",crc[0]);
                obj.put("row",crc[1]);
                obj.put("col",crc[2]);

            }

            FileTool.Dump(obj.toString(),file.replace(".txt","_格网化.txt"),"utf-8");
        }
    }
}
