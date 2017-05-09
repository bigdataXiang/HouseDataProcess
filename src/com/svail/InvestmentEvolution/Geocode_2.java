package com.svail.InvestmentEvolution;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.svail.util.FileTool;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

/**
 * Created by ZhouXiang on 2017/4/27.
 * 将【二手房中无地理编码的小区】中的数据
 */
public class Geocode_2 {
    public static void main(String[] args){

       /* community2Gaode("D:\\1_基于房产调控政策下的房产投资市场格局演变分析——以北京为例\\小区地理编码\\",
                "需要匹配的数据1_无高德解析信息.txt",0,"fadb595a83ed974b99794d041b626f68","河北省");
*/
        coordinateNormalization("D:\\1_基于房产调控政策下的房产投资市场格局演变分析——以北京为例\\小区地理编码\\",
                "需要匹配的数据_无高德解析信息_无高德解析信息.txt");
    }

    //将【二手房中无地理编码的小区.txt】中的小区进行高德地理编码
    public static void community2Gaode(String storepath,String filename,int start,String key,String city){

        String request ="http://restapi.amap.com/v3/geocode/geo?";
        String parameters ="output=JSON&key="+key+"&city="+city+"&address=";
        StringBuffer sb = new StringBuffer();

        Vector<String> pois= FileTool.Load(storepath+filename,"utf-8");
        String poi;
        String address="";
        for(int i=start;i<pois.size();i++){
            poi=pois.elementAt(i);
            address=poi;
            sb.append(address);
            address="";

            String urlParameters = sb.toString();
            byte[] postData;
            try {
                postData = (parameters + java.net.URLEncoder.encode(urlParameters,"UTF-8")).getBytes(Charset.forName("UTF-8"));
                int postDataLength = postData.length;

                URL url = new URL(request);
                HttpURLConnection cox = (HttpURLConnection) url.openConnection();
                cox.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; rv:11.0) like Gecko");
                cox.setDoOutput(true);
                cox.setDoInput(true);
                cox.setInstanceFollowRedirects(false);
                cox.setRequestMethod("GET");
                cox.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                cox.setRequestProperty("charset", "utf-8");
                cox.setRequestProperty("Content-Length",
                        Integer.toString(postDataLength));
                cox.setUseCaches(false);

                try (DataOutputStream wr = new DataOutputStream(
                        cox.getOutputStream())) {

                    wr.write(postData);

                    InputStream is = cox.getInputStream();
                    if (is != null) {
                        byte[] header = new byte[2];
                        BufferedInputStream bis = new BufferedInputStream(is);
                        bis.mark(2);
                        int result = bis.read(header);
                        bis.reset();
                        BufferedReader reader = null;
                        // 判断是否是GZIP格式
                        int ss = (header[0] & 0xff) | ((header[1] & 0xff) << 8);
                        if (result != -1 && ss == GZIPInputStream.GZIP_MAGIC) {

                            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(bis), "utf-8"));
                        } else {

                            reader = new BufferedReader(new InputStreamReader(bis, "utf-8"));
                        }


                        JsonParser parser = new JsonParser();
                        String txt ="";
                        try {

                            txt = reader.readLine();
                            if (txt == null) {
                                System.out.println("txt为null！");
                            }
                            else {
                                JsonElement el = parser.parse(txt);
                                //System.out.println(el.toString());

                                JSONObject matchResult=JSONObject.fromObject(el.toString());
                                JSONObject source=new JSONObject();
                                String city1="";
                                if(matchResult.containsKey("geocodes")){
                                    JSONArray array=matchResult.getJSONArray("geocodes");
                                    if(array.size()!=0){
                                        JSONObject geocodes=array.getJSONObject(0);

                                        String formatted_address=geocodes.getString("formatted_address");
                                        String[] location=geocodes.getString("location").split(",");
                                        double lng=Double.parseDouble(location[0]);
                                        double lat=Double.parseDouble(location[1]);
                                        String level=geocodes.getString("level");
                                        String adcode=geocodes.getString("adcode");
                                        String province=geocodes.getString("province");
                                        city1=geocodes.getString("city");
                                        String district=geocodes.getString("district");


                                        source.put("level",level);
                                        source.put("community",poi);
                                        source.put("city",city1);
                                        source.put("district",district);
                                        source.put("formatted_address",formatted_address);
                                        source.put("lng_gd",lng);
                                        source.put("lat_gd",lat);

                                    }
                                }
                                System.out.println(i+":"+source);
                                if(source.size()!=0){
                                    if(city1.equals("北京市")){
                                        FileTool.Dump(source.toString(),storepath+filename.replace(".txt","_高德解析信息_北京.txt"),"utf-8");
                                    }else {
                                        FileTool.Dump(source.toString(),storepath+filename.replace(".txt","_高德解析信息_非北京.txt"),"utf-8");
                                    }

                                }else{
                                    FileTool.Dump(poi,storepath+filename.replace(".txt","_无高德解析信息.txt"),"utf-8");
                                }
                                sb.setLength(0);
                            }
                        }catch (JsonSyntaxException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            System.out.println("JsonSyntaxException:"+e.getMessage());
                            System.out.println("存在JsonSyntaxException异常！");

                            FileTool.Dump(txt, storepath+filename.replace(".txt", "") + "_JsonSyntaxException.txt", "UTF-8");
                        }catch(NullPointerException e){
                            System.out.println("NullPointerException:"+e.getMessage());
                        }

                    }
                }

            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }catch(NullPointerException e){
                e.printStackTrace();
                FileTool.Dump(poi,storepath+"NullException/"+filename.replace(".txt", "")  + "_NullException.txt", "UTF-8");
            }


        }
    }


    //将【二手房中无地理编码的小区_无高德解析信息.txt】中的小区数据进行规范化
    public static void coordinateNormalization(String storepath,String filename){
        Vector<String> pois= FileTool.Load(storepath+filename,"utf-8");
        String poi;
        for(int i=0;i<pois.size();i++){
            poi=pois.elementAt(i);
            if(poi.contains("；")){
                String[] array=poi.split("；");
                int size=array.length;
                JSONObject obj=new JSONObject();
                if(size==2){
                    obj.put("source",array[0]);
                    obj.put("formatted_address",array[1]);
                }else if(size==3){
                    obj.put("source",array[0]);
                    obj.put("formatted_address",array[1]);
                    String[] coor=array[2].split(",");
                    obj.put("lng_gd",coor[0]);
                    obj.put("lat_gd",coor[1]);
                }
                System.out.println(obj);
            }
        }
    }
}
