package com.svail.TrafficNetwork.dataProcess;

import com.svail.util.FileTool;
import com.svail.util.Tool;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ZhouXiang on 2017/4/10.
 */
public class Trains {
    public static void main(String[] args) throws ParseException {

        String path="D:\\8_周五报告\\yiqing\\train\\zd99\\";

        //SimplifiedData(path+"TrainContent.txt",path+"火车站信息中间版.txt",path+"");

        dataFormating(path+"火车站信息中间版.txt",path+"火车站信息标准版.txt",path+"标准化过程中有问题的数据.txt");
    }

    //将数据简化成方便处理的形式
    public static void SimplifiedData(String sourcefile,String storefile,String mistakes) throws ParseException {

        Vector<String> pois= FileTool.Load(sourcefile,"utf-8");
        for(int i=0;i<pois.size();i++) {
            String poi = pois.elementAt(i);
            JSONObject obj = JSONObject.fromObject(poi);

            int size=obj.size();

            String train=obj.getString("train");
            for(int j=1;j<=size-2;j++){
                String start_station=j+"";
                for(int m=j+1;m<=size-2;m++){
                    String end_station=m+"";

                    if(obj.containsKey(start_station)&&obj.containsKey(end_station)){

                        JSONObject o_start=obj.getJSONObject(start_station);
                        String station1=o_start.getString("车站");
                        String start1=o_start.getString("开车时间");
                        if(start1.equals("-")){
                            start1=o_start.getString("到达时间");
                        }
                        String prefix1="";
                        String start_time="";
                        if(start1.indexOf("天")!=-1){
                            prefix1=start1.substring(0,start1.indexOf("天")+"天".length());
                            start_time=start1.substring(start1.indexOf("天")+"天".length());
                        }else {
                            start_time=start1;
                        }

                        JSONObject o_end=obj.getJSONObject(end_station);
                        String station2=o_end.getString("车站");
                        String arrival2=o_end.getString("到达时间");
                        String prefix2="";
                        String arrival_time="";
                        if(arrival2.indexOf("天")!=-1){
                            prefix2=arrival2.substring(0,arrival2.indexOf("天")+"天".length());
                            arrival_time=arrival2.substring(arrival2.indexOf("天")+"天".length());
                        }else {
                            arrival_time=arrival2;
                        }

                        DateFormat df = new SimpleDateFormat("HH:mm");
                        Date d_start = df.parse(start_time);
                        Date d_end = df.parse(arrival_time);
                        long diff = 0;
                        if(prefix1.equals(prefix2)){
                            diff = d_end.getTime() - d_start.getTime();
                        }else {
                            int d=0;
                            int index1=1;
                            int index2=1;
                            if(prefix1.indexOf("天")!=-1){
                                index1=Integer.parseInt(prefix1.replace("第","").replace("天",""));
                            }
                            if(prefix2.indexOf("天")!=-1){
                                index2=Integer.parseInt(prefix2.replace("第","").replace("天",""));
                            }
                            d=index2-index1;
                            diff = d_end.getTime()+d*1000 * 60 * 60*24 - d_start.getTime();
                        }double hours =(double) diff / (1000 * 60 * 60 );
                        hours =(Math.round(hours*100)/100.0);



                        JSONObject result=new JSONObject();
                        result.put("start_station",station1);
                        result.put("arrival_station",station2);
                        result.put("start_time",start_time);
                        result.put("arrival_time",arrival_time);
                        result.put("time_consume",hours);
                        result.put("train",train);

                        FileTool.Dump(result.toString(),storefile,"utf-8");
                    }

                }
            }
        }
    }

    //
    public static void dataFormating(String sourcefile,String storefile,String mistakes) throws ParseException {

        HashMap<String,Map<String,JSONArray>> map=new HashMap<>();
        Map<String,JSONArray> ends=new HashMap<>();


        Vector<String> pois= FileTool.Load(sourcefile,"utf-8");
        Set<String> set=new HashSet<>();
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);

            try{
                String start_station=obj.getString("start_station");
                String arrival_station=obj.getString("arrival_station");


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
            }catch (ArrayIndexOutOfBoundsException e){
                e.getStackTrace();
                //System.out.println(obj);
            }
        }

        /*Iterator<String> it=set.iterator();
        while (it.hasNext()){
            FileTool.Dump(it.next().toString(),mistakes,"utf-8");
            //System.out.println(it.next());
        }*/

        Map<String,JSONObject> starts=new HashMap<>();
        for(Map.Entry<String,Map<String,JSONArray>> entry:map.entrySet()){

            String start_station=entry.getKey();
            System.out.println(start_station);
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

                    String start_city=start_station;
                    if(start_station.length()>2){
                        if(start_station.endsWith("东")||start_station.endsWith("南")||
                                start_station.endsWith("西")||start_station.endsWith("北")){
                            start_city=start_city.substring(0,start_city.length()-1);
                        }
                    }
                    obj.put("start_city",start_city);
                }

                JSONObject arrivals=new JSONObject();
                for(int i=0;i<array.size();i++){
                    JSONObject o=array.getJSONObject(i);

                    String start_time=o.getString("start_time");
                    String arrival_time =o.getString("arrival_time");
                    String time=o.getString("time_consume");
                    String train=o.getString("train");

                    JSONObject temp=new JSONObject();
                    temp.put("time_consume",time);
                    temp.put("arrival_time",arrival_time);
                    temp.put("train",train);
                    arrivals.put(start_time,temp);
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
                System.out.println(start+"有"+size+"个地方的火车信息");
                FileTool.Dump(obj.toString(),storefile,"utf-8");
            }else {
                FileTool.Dump(obj.toString(),mistakes,"utf-8");
            }

        }
    }



}
