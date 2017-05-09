package com.svail.TrafficNetwork.dataProcess;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.util.JSON;
import com.svail.grid50.util.db;
import com.svail.util.FileTool;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by ZhouXiang on 2017/4/10.
 */
public class AirCraft {
    public static void main(String[] args){
        //1.将【China+Airplane+Routs.xls】转成json格式，利用【ReadExcel】

        //2.将【China+Airplane+Routs_json.txt】数据转成标准形式
        /*JSONObject obj=new JSONObject();
        obj.put("13:30:00","16:30:00");
        System.out.println(obj);
        BasicDBObject doc=new BasicDBObject();
        doc.put("13:30:00","16:30:00");
        DBCollection collection= db.getDB("traffic").getCollection("aircraft");
        collection.insert(doc);*/

        String path="D:\\8_周五报告\\yiqing\\aircraft\\择城网\\";
        dataFormating(path+"航班信息.txt",path+"航班信息标准化版.txt");





    }

    public static void dataFormating(String sourcefile,String storefile){

        HashMap<String,Map<String,JSONArray>> map=new HashMap<>();
        Map<String,JSONArray> ends=new HashMap<>();


        Vector<String> pois= FileTool.Load(sourcefile,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            String name=obj.getString("name");

            String[] citys=name.split("到");
            String start_station=citys[0];
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
                    obj.put("start_city",start_station);
                }

                JSONObject arrivals=new JSONObject();
                for(int i=0;i<array.size();i++){
                    JSONObject o=array.getJSONObject(i);

                    String start_time=o.getString("起飞时间");
                    String arrival_time=o.getString("到达时间");
                    String cycle=o.getString("航班周期");
                    String flight_number=o.getString("航班号");

                    DateFormat df = new SimpleDateFormat("HH:mm:ss");
                    double hours =0;
                    try
                    {
                        Date d_start = df.parse(start_time);
                        Date d_end = df.parse(arrival_time);
                        long diff = d_end.getTime() - d_start.getTime();
                        if(diff<0){
                            diff+=1000 * 60 * 60*24;
                        }
                        hours =(double) diff / (1000 * 60 * 60 );
                        hours =(Math.round(hours*100)/100.0);
                    }catch (Exception e) {
                        e.getStackTrace();
                    }

                    JSONObject temp=new JSONObject();
                    temp.put("time_consume",hours);
                    temp.put("arrival_time",arrival_time);
                    temp.put("cycle",cycle);
                    temp.put("flight_number",flight_number);


                    //如果起飞时间一样，即使航班不一样也会被替换掉，在这里同一时间起飞的
                    //不同航班看作是同一个航线
                    arrivals.put(start_time,temp);
                }
                //System.out.println(arrivals.size()+":"+arrivals);
                arrival_stations.put(end,arrivals);
                //System.out.println(arrival_stations);
                obj.put("arrival_stations",arrival_stations);
                starts.put(start_station,obj);
            }
        }

        for(Map.Entry<String,JSONObject> entry:starts.entrySet()){
            String start=entry.getKey();
            JSONObject obj=entry.getValue();
            JSONObject arrival_stations=obj.getJSONObject("arrival_stations");

            System.out.println(start+"有"+arrival_stations.size()+"个城市的航班");
            FileTool.Dump(obj.toString(),storefile,"utf-8");

        }
    }
}
