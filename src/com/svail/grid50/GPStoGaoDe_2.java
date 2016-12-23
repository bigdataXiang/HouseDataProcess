package com.svail.grid50;


import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.grid50.util.db;
import net.sf.json.JSONObject;
import org.apache.poi.ss.formula.functions.Match;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by bigdataxiang on 16-11-25.
 */
public class GPStoGaoDe_2 {
    public static void main(String[] args){
        gpsToGaoDe();
    }

    public static void gpsToGaoDe(){
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
            result=sendPost(url, params);
            obj=JSONObject.fromObject(result);
            locations=obj.getString("locations").split(",");
            doc.put("lng_gd",locations[0]);
            doc.put("lat_gd",locations[1]);

            collection_input.insert(doc);
            count++;
            System.out.println(count);

            try{
                Thread.sleep(500*((int)(Math.max(1,Math.random()*3))));
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

