package com.svail.grid50;

import com.svail.geotext.GeoQuery;
import com.svail.util.FileTool;
import com.svail.util.Tool;
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


public class BatchProcess_1 {
    public static String sourcepath="/media/bigdataxiang/data/houseprice/temp_woaiwojia/nonPostalCoor/";
    public static String resultpath="/media/bigdataxiang/data/houseprice/temp_woaiwojia/nonPostalCoor/";


    public static void main(String argv[]) throws Exception{

        String filename="";
        Vector<String> pois=FileTool.Load("/media/bigdataxiang/data/houseprice/woaiwojia_nocoor.txt","utf-8");
        for(int i=0;i<pois.size();i++){
            filename=pois.elementAt(i);

            System.out.println(sourcepath+filename);
            batchProcess(sourcepath+filename,resultpath,filename);
        }

        System.out.println("OK!");
    }

    public static void batchProcess(String filenames,String storepath,String filename) throws UnsupportedEncodingException {

        String request ="http://192.168.6.9:8080/p41?f=json";
        String parameters ="&within="+ java.net.URLEncoder.encode("北京市", "UTF-8")+"&key=327D6A095A8111E5BFE0B8CA3AF38727&queryStr=";
        //"&within="+ java.net.URLEncoder.encode("", "UTF-8")+


            Vector<String> rain_file=FileTool.Load(filenames,"utf-8");

            boolean batch = true;
            Gson gson = new Gson();
            if (batch)
                request = "http://192.168.6.9:8080/p4b?";
            StringBuffer sb = new StringBuffer();
            int offset = 0;
            String poi="";
            int count = 0;
            Vector<String> validpois = new Vector<String>();


            String location="";
            String community="";
            String addr="";
            String address="";

            for(int j=0;j<rain_file.size();j++){

                String info=rain_file.elementAt(j);
                //System.out.println(info);
                if(info.endsWith("},")){
                    info=info.replace("},","}");
                }
                JSONObject obj=JSONObject.fromObject(info);

                if(obj.containsKey("location")){
                    location=obj.getString("location").replace("-","");
                }
                if(obj.containsKey("community")){
                    community=obj.getString("community");
                    community= Tool.delect_content_inBrackets(community,"(",")");
                }
                if(obj.containsKey("address")){
                    addr=obj.getString("address");
                    addr=Tool.delect_content_inBrackets(addr,"(",")");
                }
                address=location+community+addr;
                if(address.length()==0){
                    address="暂无";
                }

                validpois.add(info);
                count ++;
                sb.append(address).append("\n");

                if (((count == 1000) ||  j == rain_file.size() - 1)) {

                    String urlParameters = sb.toString();
                    //System.out.print("批量处理"+filename+"开始：");
                    count = 0;
                    byte[] postData;
                    try {
                        postData = (parameters + java.net.URLEncoder.encode(urlParameters,"UTF-8")).getBytes(Charset.forName("UTF-8"));
                        int postDataLength = postData.length;

                        URL url = new URL(request);
                        //System.out.println(request + urlParameters);
                        HttpURLConnection cox = (HttpURLConnection) url.openConnection();
                        cox.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; rv:11.0) like Gecko");
                        cox.setDoOutput(true);
                        cox.setDoInput(true);
                        cox.setInstanceFollowRedirects(false);
                        cox.setRequestMethod("POST");
                        // cox.setRequestProperty("Accept-Encoding", "gzip");
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
                                            FileTool.Dump(validpois.get(m), storepath+filename.replace(".json", "") + "_txtnull.txt", "UTF-8");

                                        }
                                    }
                                    else {
                                        int index1=txt .indexOf("chinesename");
                                        String index3=",}";
                                        if(index1!=-1&&index3!=null)
                                            txt =txt .replace(",}", "}");
                                        //System.out.println(txt);
                                        JsonElement el = parser.parse(txt);
                                        JsonObject jsonObj = null;
                                        if(el.isJsonObject())
                                        {
                                            jsonObj = el.getAsJsonObject();
                                            GeoQuery gq = gson.fromJson(jsonObj, GeoQuery.class);
                                            //System.out.println(gq.getResult().size());
                                            String lnglat = "";
                                            String Admin="";
                                            if (gq != null && gq.getResult() != null && gq.getResult().size() > 0) {
                                                for (int m = 0; m < gq.getResult().size(); m ++) {
                                                    if (gq.getResult().get(m) != null && gq.getResult().get(m).getLocation() != null) {
                                                        if(gq.getResult().get(m).getLocation().getRegion()!=null){
                                                            System.out.println("这批数据没有问题！");
                                                            try {
                                                                String province=gq.getResult().get(m).getLocation().getRegion().getProvince();
                                                                String city=gq.getResult().get(m).getLocation().getRegion().getCity();
                                                                String county=gq.getResult().get(m).getLocation().getRegion().getCounty();
                                                                String town=gq.getResult().get(m).getLocation().getRegion().getTown();

                                                                Admin=(province+","+city+","+county+","+town).replace("null","").replace(",","");

                                                            }catch (NullPointerException e){
                                                                System.out.println("admin这里出了问题？");
                                                            }
                                                           }else{
                                                            Admin="暂无";
                                                        }

                                                        double longitude=gq.getResult().get(m).getLocation().getLng();
                                                        double latitude=gq.getResult().get(m).getLocation().getLat();

                                                        String poitemp= validpois.elementAt(m);
                                                        JSONObject jobj=JSONObject.fromObject(poitemp);
                                                        jobj.put("region",Admin);
                                                        jobj.put("longitude",longitude);
                                                        jobj.put("latitude", latitude);
                                                        //System.out.println(jobj.toString());
                                                        FileTool.Dump(jobj.toString().replace(" ", ""), storepath+filename.replace(".txt", "")+ "_result.txt", "UTF-8");

                                                    }else {
                                                        System.out.println("没有坐标信息");
                                                        FileTool.Dump(validpois.elementAt(m).replace(" ", ""), storepath+"nonPostalCoor/"+filename.replace(".txt", "")  + "_nonPostalCoor.txt", "UTF-8");
                                                    }
                                                }
                                            }
                                        }
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
                                        FileTool.Dump(validpois.get(m).replace(" ", ""), storepath+"NullException/"+filename.replace(".txt", "") + "_NullException.txt", "UTF-8");

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

                    validpois.clear();
                    sb.setLength(0);

                }

            }
        //}
    }
}
