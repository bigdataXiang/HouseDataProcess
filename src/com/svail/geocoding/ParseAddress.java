package com.svail.geocoding;

import com.google.gson.*;
import com.svail.geotext.GeoQuery;
import com.svail.util.FileTool;
import com.svail.util.Tool;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

/**
 * Created by ZhouXiang on 2016/8/12.
 */
public class ParseAddress {
    public static void main(String[] args) throws IOException {

        processCSV("D:\\test\\批量匹配测试\\test.txt");

    }
    public static void toClient(String file){
        Vector<String> pois = FileTool.Load(file, "utf-8");
        JSONObject obj=new JSONObject();
        JSONArray array= new JSONArray();
        String poi="";
        for(int i=0;i<pois.size();i++){
            poi=pois.elementAt(i);
            array.add(poi);
        }
        obj.put("data",array);

        FileTool.Dump(obj.toString(),file.replace(".txt","")+"_data.txt","utf-8");
    }
    public static void processCSV(String file) throws IOException {

        Vector<String> pois = FileTool.Load(file, "utf-8");
        String request = "http://192.168.6.9:8080/p41?f=json";
        String parameters = "&within=" + java.net.URLEncoder.encode("北京市", "UTF-8") + "&key=206DA5B15B5211E5BFE0B8CA3AF38727&queryStr=";

        boolean batch = true;
        Gson gson = new Gson();
        if (batch)
            request = "http://192.168.6.9:8080/p4b?";
        StringBuffer sb = new StringBuffer();
        int offset = 0;
        String poi = "";
        int count = 0;
        Vector<String> validpois = new Vector<String>();
        for (int n = 0; n < pois.size(); n++) {
            if (batch) {

                String rs = pois.get(n);
                JSONObject obj=JSONObject.fromObject(rs);
                String ADDRESS = obj.getString("addr");

                validpois.add(rs);
                count++;
                String address = ADDRESS;
                if(address.length()==0){
                    address ="地址未知";
                }
                sb.append(address).append("\n");
                if (((count == 100) || n == pois.size() - 1)) {
                    String urlParameters = sb.toString();
                    System.out.print("批量处理开始：");
                    count = 0;
                    byte[] postData;
                    try {
                        postData = (parameters + java.net.URLEncoder.encode(urlParameters, "UTF-8")).getBytes(Charset.forName("UTF-8"));
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

                                // reset输入流到开始位置
                                bis.reset();
                                BufferedReader reader = null;
                                // 判断是否是GZIP格式
                                int ss = (header[0] & 0xff) | ((header[1] & 0xff) << 8);
                                if (result != -1 && ss == GZIPInputStream.GZIP_MAGIC) {
                                    // System.out.println("为数据压缩格式...");
                                    reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(bis), "utf-8"));
                                } else {
                                    // 取前两个字节
                                    reader = new BufferedReader(new InputStreamReader(bis, "utf-8"));
                                }

                                // 创建一个JsonParser
                                JsonParser parser = new JsonParser();
                                String txt = "";
                                //通过JsonParser对象可以把json格式的字符串解析成一个JsonElement对象
                                try {

                                    txt = reader.readLine();
                                    //System.out.println(txt);
                                    if (txt == null) {
                                        System.out.println("txt为null！");
                                        for (int i = 0; i < validpois.size(); i++) {
                                            FileTool.Dump(validpois.get(i), file.replace(".txt", "") + "_NullException.txt", "UTF-8");

                                        }
                                    } else {
                                        int index1 = txt.indexOf("chinesename");
                                        String index3 = ",}";
                                        if (index1 != -1 && index3 != null)
                                            txt = txt.replace(",}", "}");
                                        JsonElement el = parser.parse(txt);
                                        // JsonElement el = parser.parse(tesobj.toString());
                                        //把JsonElement对象转换成JsonObject
                                        JsonObject jsonObj = null;
                                        if (el.isJsonObject()) {
                                            jsonObj = el.getAsJsonObject();
                                            //System.out.println(jsonObj);
                                            GeoQuery gq = gson.fromJson(jsonObj, GeoQuery.class);
                                            // System.out.println(gq);
                                            //  System.out.println(gq.getResult());
                                            System.out.println(gq.getResult().size());
                                            String lnglat = "";
                                            String Admin = "";
                                            if (gq != null && gq.getResult() != null && gq.getResult().size() > 0) {
                                                System.out.println("这批数据没有问题！");
                                                for (int m = 0; m < gq.getResult().size(); m++) {
                                                    if (gq.getResult().get(m) != null && gq.getResult().get(m).getLocation() != null) {
                                                        if (gq.getResult().get(m).getLocation().getRegion() != null)
                                                            Admin = gq.getResult().get(m).getLocation().getRegion().getProvince() + "," + gq.getResult().get(m).getLocation().getRegion().getCity() + "," + gq.getResult().get(m).getLocation().getRegion().getCounty() + "," + gq.getResult().get(m).getLocation().getRegion().getTown();
                                                        else
                                                            Admin = "暂无";
                                                        double longitude = gq.getResult().get(m).getLocation().getLng();
                                                        double latitude = gq.getResult().get(m).getLocation().getLat();

                                                        String poitemp = validpois.elementAt(m);

                                                        JSONObject jobj = JSONObject.fromObject(poitemp);
                                                        jobj.put("region", Admin.replace("null","").replace(",",""));
                                                        jobj.put("longitude", longitude);
                                                        jobj.put("latitude", latitude);

                                                        FileTool.Dump(jobj.toString().replace(" ", ""), file.replace(".txt", "") + "_result.txt", "UTF-8");



                                                    } else {
                                                        FileTool.Dump(validpois.elementAt(m).replace(" ", ""), file.replace(".txt", "") + "_nonPostalCoor1.txt", "UTF-8");
                                                        //System.out.print(validpois.elementAt(m));
                                                    }
                                                }
                                            }
                                        }
                                    }

                                } catch (JsonSyntaxException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                    System.out.println(e.getMessage());
                                    System.out.println("存在JsonSyntaxException异常！");
                                    for (int i = 0; i < validpois.size(); i++) {
                                        FileTool.Dump(validpois.get(i).replace(" ", ""), file.replace(".txt", "") + "_JsonSyntax.txt", "UTF-8");

                                    }
                                    FileTool.Dump(txt, file.replace(".txt", "") + "_JsonSyntaxException.txt", "UTF-8");
                                } catch (NullPointerException e) {
                                    System.out.println(e.getMessage());
                                    for (int i = 0; i < validpois.size(); i++) {
                                        FileTool.Dump(validpois.get(i).replace(" ", ""), file.replace(".txt", "") + "_Null.txt", "UTF-8");

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
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        FileTool.Dump(poi, file.replace(".txt", "") + "_Null1.txt", "UTF-8");
                    }

                    validpois.clear();
                    sb.setLength(0);
                }

            }
        }

    }
}


