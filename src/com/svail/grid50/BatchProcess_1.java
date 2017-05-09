package com.svail.grid50;

import com.svail.geotext.GeoQuery;
import com.svail.util.FileTool;
import com.svail.util.Tool;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import static java.lang.StrictMath.random;


public class BatchProcess_1 {

    public static String sourcepath="D:\\小论文\\poi资料\\公交\\";
    public static void main(String argv[]) throws Exception{
        String filename="公交车站.txt";
        String[] keys={"name"};//"address","name","community"
        boolean gaode=true;
        if(gaode){
            addressMatch_GaoDe(0,keys,sourcepath,filename,"北京","8ca252e49863ab4f4d9fee62b14c5c18");
        }else {
            batchProcess(500,sourcepath,filename,keys);
        }
    }
    public static void match(){
        String filename="";
        Vector<String> pois=FileTool.Load("/media/bigdataxiang/data/houseprice/woaiwojia_nocoor.txt","utf-8");
        String[] keys={""};
        for(int i=0;i<pois.size();i++){
            filename=pois.elementAt(i);

            System.out.println(sourcepath+filename);
            try {
                batchProcess(1000,sourcepath+filename,filename,keys);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        System.out.println("OK!");
    }

    /**
     * @param amount 批量处理的个数
     * @param sourcepath 需要处理的源数据的路径
     * @param filename 需要处理的文件名称
     * @throws UnsupportedEncodingException
     */
    public static void batchProcess(int amount,String sourcepath,String filename,String[] keys) throws UnsupportedEncodingException {

        //http://geocode.svail.com:8080/
        //http://192.168.6.9:8080/
        String request ="http://192.168.6.9:8080/p41?f=json";
        String parameters ="&within="+ java.net.URLEncoder.encode("北京市", "UTF-8")+"&key=327D6A095A8111E5BFE0B8CA3AF38727&queryStr=";


        Vector<String> pois=FileTool.Load(sourcepath+filename,"utf-8");

        boolean batch = true;
        if (batch)
            request = "http://192.168.6.9:8080/p4b?";
        StringBuffer sb = new StringBuffer();
        String poi="";
        int count = 0;
        Vector<String> validpois = new Vector<String>();
        String address="";
        for(int j=0;j<pois.size();j++){

            String info=pois.elementAt(j);
            if(info.endsWith("},")){
                info=info.replace("},","}");
            }
            JSONObject obj=JSONObject.fromObject(info);
            for(int i=0;i<keys.length;i++){
                if(obj.containsKey(keys[i])){
                    address+=Tool.delect_content_inBrackets(obj.getString(keys[i]),"(",")");
                }
            }
            if(address.length()==0){
                address="暂无";
            }

            validpois.add(info);
            count ++;
            if(amount!=1){
                sb.append(address).append("\n");
                address="";//这里一定要记得置0，要不然所有的地址就都叠加起来了
            }else {
                sb.append(address);
                address="";
            }

            if (((count == amount) ||  j == pois.size() - 1)) {

                String urlParameters = sb.toString();
                count = 0;
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
                    cox.setRequestMethod("POST");
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
                                    for(int m=0;m<validpois.size();m++){
                                        FileTool.Dump(validpois.get(m), sourcepath+filename.replace(".json", "") + "_txtnull.txt", "UTF-8");

                                    }
                                }
                                else {
                                    JsonElement el = parser.parse(txt);
                                    JSONObject request_result=JSONObject.fromObject(el.toString());

                                    if(request_result.containsKey("result")){
                                        JSONArray array=request_result.getJSONArray("result");
                                        for(int i=0;i<array.size();i++){
                                            JSONObject array_obj=array.getJSONObject(i);

                                            String poitemp= validpois.elementAt(i);
                                            JSONObject match=JSONObject.fromObject(poitemp);
                                            if(array_obj.containsKey("nlp_status")){
                                                match.put("nlp_status",array_obj.getString("nlp_status"));
                                            }
                                            if(array_obj.containsKey("location")){
                                                JSONObject loca=array_obj.getJSONObject("location");

                                                if(loca.containsKey("matched")){
                                                    match.put("matched",loca.getString("matched"));
                                                }
                                                if(loca.containsKey("source")){
                                                    match.put("source",loca.getString("source"));
                                                }
                                                if(loca.containsKey("lng")){
                                                    match.put("lng",loca.getString("lng"));
                                                }
                                                if(loca.containsKey("lat")){
                                                    match.put("lat",loca.getString("lat"));
                                                }
                                                if(loca.containsKey("region")){
                                                    match.put("region",loca.getString("region"));
                                                }

                                            }
                                            FileTool.Dump(match.toString(),sourcepath+filename.replace(".txt","_地理编码.txt"),"utf-8");
                                        }
                                    }
                                }

                            }catch (JsonSyntaxException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                System.out.println("JsonSyntaxException:"+e.getMessage());
                                System.out.println("存在JsonSyntaxException异常！");
                                for(int m=0;m<validpois.size();m++){
                                    FileTool.Dump(validpois.get(m).replace(" ", ""), sourcepath+filename.replace(".txt", "_JsonSyntax.txt"), "UTF-8");

                                }
                                FileTool.Dump(txt, sourcepath+filename.replace(".txt", "_JsonSyntaxException.txt"), "UTF-8");
                            }catch(NullPointerException e){
                                System.out.println("NullPointerException:"+e.getMessage());
                                for(int m=0;m<validpois.size();m++){
                                    FileTool.Dump(validpois.get(m).replace(" ", ""), sourcepath+filename.replace(".txt", "_NullException.txt"), "UTF-8");

                                }
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
                    FileTool.Dump(poi,sourcepath+filename.replace(".txt", "_NullException.txt"), "UTF-8");
                }

                validpois.clear();
                sb.setLength(0);

            }

        }
    }
    public static void batchProcess(int amount,String file,String[] keys,String province) throws UnsupportedEncodingException {

        //http://geocode.svail.com:8080/
        //http://192.168.6.9:8080/
        String request ="http://192.168.6.9:8080/p41?f=json";
        String parameters ="&within="+java.net.URLEncoder.encode(province, "UTF-8")+"&key=327D6A095A8111E5BFE0B8CA3AF38727&queryStr=";
        //"&within="+java.net.URLEncoder.encode("北京市", "UTF-8")


        Vector<String> pois=FileTool.Load(file,"utf-8");

        boolean batch = true;
        if (batch)
            request = "http://192.168.6.9:8080/p4b?";
        StringBuffer sb = new StringBuffer();
        String poi="";
        int count = 0;
        Vector<String> validpois = new Vector<String>();
        String address="";
        for(int j=0;j<pois.size();j++){

            String info=pois.elementAt(j);
            if(info.endsWith("},")){
                info=info.replace("},","}");
            }
            JSONObject obj=JSONObject.fromObject(info);
            for(int i=0;i<keys.length;i++){
                if(obj.containsKey(keys[i])){
                    address+=Tool.delect_content_inBrackets(obj.getString(keys[i]),"(",")");
                }
            }
            if(address.length()==0){
                address="暂无";
            }

            validpois.add(info);
            count ++;
            if(amount!=1){
                sb.append(address).append("\n");
                address="";//这里一定要记得置0，要不然所有的地址就都叠加起来了
            }else {
                sb.append(address);
                address="";
            }

            if (((count == amount) ||  j == pois.size() - 1)) {

                String urlParameters = sb.toString();
                count = 0;
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
                    cox.setRequestMethod("POST");
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
                                    for(int m=0;m<validpois.size();m++){
                                        FileTool.Dump(validpois.get(m), file.replace(".json", "") + "_txtnull.txt", "UTF-8");

                                    }
                                }
                                else {
                                    JsonElement el = parser.parse(txt);
                                    JSONObject request_result=JSONObject.fromObject(el.toString());

                                    if(request_result.containsKey("result")){
                                        JSONArray array=request_result.getJSONArray("result");
                                        for(int i=0;i<array.size();i++){
                                            JSONObject array_obj=array.getJSONObject(i);

                                            String poitemp= validpois.elementAt(i);
                                            JSONObject match=JSONObject.fromObject(poitemp);
                                            if(array_obj.containsKey("nlp_status")){
                                                match.put("nlp_status",array_obj.getString("nlp_status"));
                                            }
                                            if(array_obj.containsKey("location")){
                                                JSONObject loca=array_obj.getJSONObject("location");

                                                if(loca.containsKey("matched")){
                                                    match.put("matched",loca.getString("matched"));
                                                }
                                                if(loca.containsKey("source")){
                                                    match.put("source",loca.getString("source"));
                                                }
                                                if(loca.containsKey("lng")){
                                                    match.put("lng",loca.getString("lng"));
                                                }
                                                if(loca.containsKey("lat")){
                                                    match.put("lat",loca.getString("lat"));
                                                }
                                                if(loca.containsKey("region")){
                                                    match.put("region",loca.getString("region"));
                                                }

                                            }
                                            FileTool.Dump(match.toString(),file.replace(".txt","_地理编码.txt"),"utf-8");
                                        }
                                    }
                                }

                            }catch (JsonSyntaxException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                System.out.println("JsonSyntaxException:"+e.getMessage());
                                System.out.println("存在JsonSyntaxException异常！");
                                for(int m=0;m<validpois.size();m++){
                                    FileTool.Dump(validpois.get(m).replace(" ", ""), file.replace(".txt", "_JsonSyntax.txt"), "UTF-8");

                                }
                                FileTool.Dump(txt, file.replace(".txt", "_JsonSyntaxException.txt"), "UTF-8");
                            }catch(NullPointerException e){
                                System.out.println("NullPointerException:"+e.getMessage());
                                for(int m=0;m<validpois.size();m++){
                                    FileTool.Dump(validpois.get(m).replace(" ", ""), file.replace(".txt", "_NullException.txt"), "UTF-8");

                                }
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
                    FileTool.Dump(poi,file.replace(".txt", "_NullException.txt"), "UTF-8");
                }

                validpois.clear();
                sb.setLength(0);

            }

        }
    }

    public static void addressMatch_GaoDe(int start,String[] name,String storepath,String filename,String city,String key) throws UnsupportedEncodingException {

        String request ="http://restapi.amap.com/v3/geocode/geo?";
        //两个高德的key,22285537f6573ffaeeba1c74bc82787d
        //34f263592a3eeb72412a825fa11b49ea
        String parameters ="output=JSON&key="+key+"&city="+city+"&address=";
        Vector<String> rain_file=FileTool.Load(storepath+filename,"utf-8");

        StringBuffer sb = new StringBuffer();
        String poi="";
        Vector<String> validpois = new Vector<String>();

        String address="";

        for(int j=start;j<rain_file.size();j++){

            String info=rain_file.elementAt(j);
            JSONObject obj=JSONObject.fromObject(info);
            for(int i=0;i<name.length;i++){
                if(obj.containsKey(name[i])){
                    address+=Tool.delect_content_inBrackets(obj.getString(name[i]),"(",")");
                }
            }

            if(address.length()==0){
                address="暂无";
            }
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
                        /*try{

                            Thread.sleep(100*((int)(Math.max(1,random()*3))));

                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/

                        JsonParser parser = new JsonParser();
                        String txt ="";
                        try {

                            txt = reader.readLine();
                            if (txt == null) {
                                System.out.println("txt为null！");
                                for(int m=0;m<validpois.size();m++){
                                    FileTool.Dump(validpois.get(m), storepath+filename.replace(".json", "") + "_txtnull.txt", "UTF-8");
                                }
                            }
                            else {
                                JsonElement el = parser.parse(txt);
                                //System.out.println(el.toString());

                                JSONObject matchResult=JSONObject.fromObject(el.toString());
                                JSONObject source=JSONObject.fromObject(info);

                                if(matchResult.containsKey("geocodes")){
                                    JSONArray array=matchResult.getJSONArray("geocodes");
                                    if(array.size()!=0){
                                        JSONObject geocodes=array.getJSONObject(0);
                                        //System.out.println(geocodes);

                                        String formatted_address=geocodes.getString("formatted_address");
                                        String[] location=geocodes.getString("location").split(",");
                                        double lng=Double.parseDouble(location[0]);
                                        double lat=Double.parseDouble(location[1]);
                                        String level=geocodes.getString("level");
                                        String adcode=geocodes.getString("adcode");
                                        String province=geocodes.getString("province");
                                        String city1=geocodes.getString("city");
                                        String district=geocodes.getString("district");

                                        source.put("adcode",adcode);
                                        source.put("province",province);
                                        source.put("city",city);
                                        source.put("district",district);
                                        source.put("formatted_address",formatted_address);
                                        source.put("lng_gd",lng);
                                        source.put("lat_gd",lat);
                                        source.put("level",level);
                                    }
                                }
                                System.out.println(j+":"+source);
                                FileTool.Dump(source.toString(),storepath+filename.replace(".txt","_高德解析信息.txt"),"utf-8");
                                sb.setLength(0);

                                /*try{

                                    Thread.sleep(500*((int)(Math.max(1,random()*3))));

                                }catch (InterruptedException e) {
                                    e.printStackTrace();
                                }*/
                            }
                        }catch (JsonSyntaxException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            System.out.println("JsonSyntaxException:"+e.getMessage());
                            System.out.println("存在JsonSyntaxException异常！");
                            for(int m=0;m<validpois.size();m++){
                                FileTool.Dump(validpois.get(m).replace(" ", ""), storepath+filename.replace(".txt", "") + "_JsonSyntax.txt", "UTF-8");

                            }
                            FileTool.Dump(txt, storepath+filename.replace(".txt", "") + "_JsonSyntaxException.txt", "UTF-8");
                        }catch(NullPointerException e){
                            System.out.println("NullPointerException:"+e.getMessage());
                            for(int m=0;m<validpois.size();m++){
                                FileTool.Dump(validpois.get(m).replace(" ", ""), storepath+filename.replace(".txt", "") + "_NullException.txt", "UTF-8");

                            }
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

}
