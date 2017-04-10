package com.svail.TrafficNetwork.dataCrawl;

import com.svail.crawlTool.OKhttp;
import com.svail.util.FileTool;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Vector;



/**
 * Created by ZhouXiang on 2017/4/10.
 */
public class Train {
    public static void main(String[] args) throws Exception {

        //String storefile="/media/bigdataxiang/data/yiqing/TrainLinks.txt";
        String storefile="D:\\8_周五报告\\zd99\\TrainContent.txt";

        //getSubLink("http://www.zd9999.com/cc/",storefile);

        Vector<String> pois= FileTool.Load("D:\\8_周五报告\\zd99\\TrainLinks.txt","utf-8");
        for(int i=5396;i<pois.size();i++){

            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            String name=obj.getString("name");
            String url=obj.getString("url");
            getTrainContent("http://www.zd9999.com"+url,name,storefile);

            System.out.println(i);

            try {
                Thread.sleep(10000 * ((int) (Math
                        .max(1, Math.random() * 3))));
            } catch (final InterruptedException e1) {
                e1.printStackTrace();
            }

        }


    }
    public static void getSubLink(String url,String storefile) throws Exception {
        String html=new OKhttp().fetchUrl(url,"gbk");
        //System.out.println(html);
        Document doc = Jsoup.parse(html);
        //System.out.println(doc);
        Elements comlist=doc.select("tr");
        for(int j=0;j<comlist.size();j++){
            Elements tds=comlist.get(j).select("td");
            for(int i=0;i<tds.size();i++){
                Element td=tds.get(i);
                String text=td.text();
                try{
                    Element a=td.select("a").get(0);
                    String href=a.attr("href");
                    JSONObject obj=new JSONObject();
                    obj.put("name",text);
                    obj.put("url",href);
                    System.out.println(obj);
                    FileTool.Dump(obj.toString(),storefile,"utf-8");
                }catch (IndexOutOfBoundsException e){
                    e.getStackTrace();
                }
            }
        }
    }

    public static void getTrainContent(String url,String name,String storefile) throws Exception {

        System.out.println(url);
        String html=new OKhttp().fetchUrl(url,"gbk");
        Document doc = Jsoup.parse(html);
        Elements trs=doc.select("tr");


        int start=0;

        for(int i=0;i<trs.size();i++){
            Element td=trs.get(i);
            String text=td.text();
            if(text.startsWith("站次")){
                start=i;
                break;
            }
        }
        JSONObject obj=new JSONObject();
        obj.put("train",name);
        String[] keys=new String[6];
        String[] values=new String[6];
        for(int i=start-1;i<trs.size();i++){
            Element tr=trs.get(i);
            String text=tr.text();


            if(text.indexOf("更新时间")!=-1){
                obj.put("update_time",text);
            }else if(text.startsWith("站次")){
                keys=text.split(" ");
            }else if(text.startsWith("车站查询")){

            }else if(text.startsWith("站站查询")){

            }else {
                values=text.split(" ");
                JSONObject o=new JSONObject();
                for(int j=1;j<values.length;j++){
                    o.put(keys[j],values[j]);
                }
                obj.put(values[0],o);
            }
        }
        System.out.println(obj);
        FileTool.Dump(obj.toString(),storefile,"utf-8");
    }
}
