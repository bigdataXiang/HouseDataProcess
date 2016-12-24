package com.svail.grid50;


import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.grid50.util.db;
import net.sf.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by bigdataxiang on 16-11-25.
 */
public class GPStoGaoDe_2 {
    public static void main(String[] args){

        /*double[] lnglat=wgs84toGaoDe(116.39055488416854,39.906985851984146);
        System.out.println(lnglat[0]+","+lnglat[1]);*/

        gpsToGaoDe_formula();
    }

    //将github上的python程序改编成java版本的wgs84转火星坐标
    public static void gpsToGaoDe_formula(){
        double lng;
        double lat;
        BasicDBObject doc=new BasicDBObject();
        JSONObject obj;
        String[] locations;
        double[] lnglat;
        String str_lng;
        String str_lat;

        DBCollection collection= db.getDB("paper").getCollection("BasicData_Resold");
        DBCollection collection_input= db.getDB("paper").getCollection("BasicData_Resold_gd");
        DBCursor cs=collection.find();

        int count=0;
        while (cs.hasNext()){
            doc=(BasicDBObject)cs.next();
            str_lng=doc.getString("lng");
            str_lat=doc.getString("lat");
            lng=Double.parseDouble(str_lng);
            lat=Double.parseDouble(str_lat);


            count++;
            lnglat=wgs84toGaoDe(lng,lat);
            doc.put("lng_gd",lnglat[0]);
            doc.put("lat_gd",lnglat[1]);

            System.out.println(doc.getString("community"));
            System.out.println(lng+","+lat);
            System.out.println(lnglat[0]+","+lnglat[1]);
            collection_input.insert(doc);

            try{
                Thread.sleep(100*((int)(Math.max(1,Math.random()*3))));
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }





    }

    public static double pi = 3.1415926535897932384626;

    public static double  a = 6378245.0;  // 长半轴
    public static double  ee = 0.00669342162296594323;  // 扁率

    public static double[] wgs84toGaoDe(double lng, double lat){
        double[] lnglat=new double[2];
        double dlat = transformlat(lng - 105.0, lat - 35.0);
        double dlng = transformlng(lng - 105.0, lat - 35.0);
        double radlat = lat / 180.0 * pi;
        double magic = Math.sin(radlat);
        magic = 1 - ee * magic * magic;
        double sqrtmagic = Math.sqrt(magic);
        dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * pi);
        dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * pi);
        double mglat = lat + dlat;
        double mglng = lng + dlng;
        lnglat[0]=mglng;
        lnglat[1]=mglat;
        return lnglat;
    }
    public static double transformlng(double lng, double lat){
        double ret;
        ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 
        0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * pi) + 20.0 *
                Math.sin(2.0 * lng * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * pi) + 40.0 *
                Math.sin(lng / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * pi) + 300.0 *
                Math.sin(lng / 30.0 * pi)) * 2.0 / 3.0;
        return ret;
    }

    public static double transformlat(double lng, double lat){
        double ret;
        ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat +
        0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * pi) + 20.0 *
                Math.sin(2.0 * lng * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * pi) + 40.0 *
                Math.sin(lat / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * pi) + 320 *
                Math.sin(lat * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    //首先调用高德的api来获取转换后的结果，后面发现根本跑不动，所以果断放弃
    //后面将github上的python程序改编了，问题得到解决
    public static void gpsToGaoDe_api(){
        String url = "http://restapi.amap.com/v3/assistant/coordinate/convert?";
        String lng;
        String lat;
        String params;
        BasicDBObject doc=new BasicDBObject();
        String result;
        JSONObject obj;
        String[] locations;

        DBCollection collection= db.getDB("paper").getCollection("BasicData_Resold");
        DBCollection collection_input= db.getDB("paper").getCollection("BasicData_Resold_gd");
        DBCursor cs=collection.find();

        int count=0;
        while (cs.hasNext()){
            doc=(BasicDBObject)cs.next();
            lng=doc.getString("lng");
            lat=doc.getString("lat");
            params = "locations="+lng+","+lat+"&coordsys=gps&output=JSON&key=34f263592a3eeb72412a825fa11b49ea";

            //System.out.println("发送转换post请求");
            count++;
            //System.out.println(count+":"+url+params);
            result=sendPost(url, params);
            obj=JSONObject.fromObject(result);
            System.out.println(count+":"+obj);
            locations=obj.getString("locations").split(",");
            doc.put("lng_gd",locations[0]);
            doc.put("lat_gd",locations[1]);

            collection_input.insert(doc);

            try{
                Thread.sleep(100*((int)(Math.max(1,Math.random()*3))));
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }





    }
    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                //System.out.println(line);
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}

