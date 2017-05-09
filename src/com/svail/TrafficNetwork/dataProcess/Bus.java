package com.svail.TrafficNetwork.dataProcess;

import com.svail.util.FileTool;
import com.svail.util.Tool;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ZhouXiang on 2017/4/10.
 */
public class Bus {
    public static void main(String[] args) throws ParseException {
        String path="D:\\8_周五报告\\yiqing\\bus\\";
        //dataFormating(path+"客运信息简化版.txt",path+"客运信息标准化版.txt",path+"没有里程信息的车站.txt");

        //SimplifiedData(path+"客运信息.txt",path+"客运信息简化版.txt");

    }
    public static void SimplifiedData(String sourcefile,String storefile){
        Vector<String> pois= FileTool.Load(sourcefile,"utf-8");
        for(int i=0;i<pois.size();i++) {
            //System.out.println(i);
            String poi = pois.elementAt(i);
            JSONObject obj = JSONObject.fromObject(poi);
            obj.remove("time_consume");
            obj.remove("remark");
            obj.remove("price");
            obj.remove("models");
            obj.remove("arrival");
            FileTool.Dump(obj.toString(),storefile,"utf-8");
        }
    }


    public static void dataFormating(String sourcefile,String storefile,String mistakes) throws ParseException {

        HashMap<String,Map<String,JSONArray>> map=new HashMap<>();
        Map<String,JSONArray> ends=new HashMap<>();


        Vector<String> pois= FileTool.Load(sourcefile,"utf-8");
        //129451,285294,408907,528494,
        for(int i=528495;i<=pois.size();i++){
            //System.out.println(i);
            String poi=pois.elementAt(i-1);
            JSONObject obj=JSONObject.fromObject(poi);
            String name=obj.getString("start");

            try{
                if(name.contains("-")){
                    String[] citys=name.split("-");
                    String start_station=obj.getString("station");
                    String arrival_station=citys[1];
                    if(map.containsKey(start_station)){
                        ends=map.get(start_station);
                        if(ends.containsKey(arrival_station)){
                            JSONArray array=ends.get(arrival_station);
                            array.add(obj);
                            ends.put(arrival_station,array);
                        }else {
                            JSONArray array=new JSONArray();
                            array.add(obj);
                            ends.put(arrival_station,array);
                        }
                        map.put(start_station,ends);
                    }else {
                        ends=new HashMap<>();
                        JSONArray array=new JSONArray();
                        array.add(obj);
                        ends.put(arrival_station,array);
                        map.put(start_station,ends);
                    }
                }
            }catch (ArrayIndexOutOfBoundsException e){
                e.getStackTrace();
                //System.out.println(obj);
            }
        }

        Map<String,JSONObject> starts=new HashMap<>();
        for(Map.Entry<String,Map<String,JSONArray>> entry:map.entrySet()){

            String start_station=entry.getKey();
            Map<String,JSONArray> endss=entry.getValue();

            JSONObject arrival_stations=new JSONObject();
            JSONObject obj=new JSONObject();

            for(Map.Entry<String,JSONArray> entry1:endss.entrySet()){
                String end=entry1.getKey();
                JSONArray array=entry1.getValue();

                if(starts.containsKey(start_station)){
                    obj=starts.get(start_station);
                    arrival_stations=obj.getJSONObject("arrival_stations");

                }else {
                    obj=new JSONObject();
                    obj.put("start_station",start_station);

                    String start_city=array.getJSONObject(0).getString("start").split("-")[0];
                    obj.put("start_city",start_city);
                }

                JSONObject arrivals=new JSONObject();
                for(int i=0;i<array.size();i++){
                    JSONObject o=array.getJSONObject(i);

                    String start_time="";
                    if(o.containsKey("start_time")){
                        start_time=o.getString("start_time");
                        String mileage=o.getString("mileage").replace("km","").replace("公里","")
                                .replace("(公里)","").replace("KM","");
                        boolean num= Tool.isNumeric(mileage);
                        if(num){

                            double time;//将大巴的平均速度设置为90

                            time = Double.parseDouble(mileage)/90;
                            time =(Math.round(time*10)/10.0);
                            int h=0;
                            int m=0;
                            if(time>1){
                                h= (int) Math.floor(time);
                                m= (int) ((time-h)*60);
                            }else {
                                m= (int) time*60;
                            }


                            if(start_time.contains(":")&&(!(start_time.equals(":")))){
                                SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
                                Date dt=sdf.parse(start_time.replace("*","").replace("暂停","").replace("(","").replace(")","").replace("(隔日班)","").replace("冬",""));
                                Calendar rightNow = Calendar.getInstance();
                                rightNow.setTime(dt);
                                rightNow.add(Calendar.HOUR,h);
                                rightNow.add(Calendar.MINUTE,m);

                                Date dt1=rightNow.getTime();
                                String arrival_time = sdf.format(dt1);
                                //System.out.println(arrival_time);

                                JSONObject temp=new JSONObject();
                                temp.put("time_consume",time);
                                temp.put("arrival_time",arrival_time);
                                arrivals.put(start_time,temp);

                            }else {
                                //FileTool.Dump(o.toString(),mistakes,"utf-8");
                            }
                        }else {
                            //FileTool.Dump(o.toString(),mistakes,"utf-8");
                        }
                    }
                }
                //System.out.println(arrivals.size()+":"+arrivals);
                if(arrivals.size()>0){
                    arrival_stations.put(end,arrivals);
                }
                //System.out.println(arrival_stations);
                obj.put("arrival_stations",arrival_stations);
                starts.put(start_station,obj);
            }
        }

        for(Map.Entry<String,JSONObject> entry:starts.entrySet()){
            String start=entry.getKey();
            JSONObject obj=entry.getValue();
            JSONObject arrival_stations=obj.getJSONObject("arrival_stations");

            int size=arrival_stations.size();

            if(size>0){
                System.out.println(start+"有"+size+"个地方的客运信息");
                FileTool.Dump(obj.toString(),storefile,"utf-8");
            }else {
                FileTool.Dump(obj.toString(),mistakes,"utf-8");
            }

        }
    }
}
