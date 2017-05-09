package com.svail.TrafficNetwork.shortestPath;

import com.svail.util.FileTool;
import com.svail.util.Tool;
import net.sf.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ZhouXiang on 2017/4/11.
 * 将所有的火车，客运和航班的地点混合到一起，构建一个n*n的矩阵
 */
public class MatrixConstruction {
    public static void main(String[] args){

        //第一步：获得所有的交通点

        /*System.out.println("火车信息");
        allPoints("D:\\8_周五报告\\yiqing\\train\\zd99\\火车站信息标准版.txt");
        System.out.println("航班信息");
        allPoints("D:\\8_周五报告\\yiqing\\aircraft\\择城网\\航班信息标准化版.txt");
        System.out.println("客运信息");
        allPoints("D:\\8_周五报告\\yiqing\\bus\\客运信息标准化版.txt");

        Iterator<String> it=set.iterator();
        while (it.hasNext()){

            String str=it.next().toString();
            FileTool.Dump(str,"D:\\8_周五报告\\yiqing\\点矩阵\\全国所有交通站点集合(一个城市一个站点).txt","utf-8");
        }*/


        //第二步：获得两点之间的最短交通时间（考虑时间损耗）
        shortestTime("D:\\8_周五报告\\yiqing\\train\\zd99\\火车站信息标准版.txt",
                "D:\\8_周五报告\\yiqing\\aircraft\\择城网\\航班信息标准化版.txt",
                "D:\\8_周五报告\\yiqing\\bus\\客运信息标准化版.txt",
                "08:00");




    }

    public static Set<String> set=new HashSet<>();
    //1.将所有的火车，客运和航班的地点混合到一起，看看总共有多少个点
    public static void allPoints(String sorcefile){
        Vector<String> pois= FileTool.Load(sorcefile,"utf-8");
        for(int i=0;i<pois.size();i++){
            System.out.println(i);

            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);

            //为了精简站点数据，将start_station改成start_city
            String start_station=obj.getString("start_city");
            start_station= Tool.delect_content_inBrackets(start_station,"(",")");
            start_station= Tool.delect_content_inBrackets(start_station,"（","）");
            start_station= Tool.delect_content_inBrackets(start_station,"(","）");
            start_station= Tool.delect_content_inBrackets(start_station,"（",")");
            set.add(start_station);
            JSONObject arrival_stations=obj.getJSONObject("arrival_stations");
            Iterator<String> stations=arrival_stations.keys();
            while (stations.hasNext()){
                String station=stations.next();
                station= Tool.delect_content_inBrackets(station,"(",")");
                station= Tool.delect_content_inBrackets(station,"（","）");
                station= Tool.delect_content_inBrackets(station,"(","）");
                station= Tool.delect_content_inBrackets(station,"（",")");
                set.add(station);
            }
        }
    }




    public static Map<String,Map<String,Double>> map=new HashMap<>();
    public static Map<String,Double> arrival_costime;
    //2.求所有点之间的最短时间（考虑损耗）
    public static double[][] shortestTime(String trainfile,String airfile,String busfile,String time){

        Vector<String> pois1= FileTool.Load(trainfile,"utf-8");
        fileProcess(pois1,time);

        Vector<String> pois2= FileTool.Load(airfile,"utf-8");
        fileProcess(pois2,time);

        Vector<String> pois3= FileTool.Load(busfile,"utf-8");
        fileProcess(pois3,time);

        System.out.println("共计有"+map.size()+"出发点");

        Vector<String> pois=FileTool.Load("D:\\8_周五报告\\yiqing\\点矩阵\\全国所有交通站点集合.txt","utf-8");
        System.out.println(pois.size());

        double[][] data=new double[2000][2000];
        Map<Integer,String> point_name=new HashMap<>();

        for(int i=0;i<2000;i++){
            //System.out.println("第"+i+"行：");
            String poi_start=pois.elementAt(i);
            point_name.put(i,poi_start);

            if(map.containsKey(poi_start)){
                Map<String,Double> ac=map.get(poi_start);

                for(int j=0;j<2000;j++){
                    String poi_end=pois.elementAt(j);

                    if(ac.containsKey(poi_end)){
                        data[i][j]=ac.get(poi_end);
                    }else {
                        data[i][j]=0;
                    }
                }
            }else {
                for(int j=0;j<2000;j++){
                    data[i][j]=0;
                }
            }
        }

        FLOYD_Traffic test=new FLOYD_Traffic(data);

        for (int i = 0; i < data.length; i++){
            for (int j = i; j < data[i].length; j++) {
                double mintime=test.length[i][j];

                if(mintime<1000&&mintime!=0){
                    System.out.println();
                    System.out.print("From " + i + " to " + j + " path is: ");
                    for (int k = 0; k < test.path[i][j].length; k++){

                        int point=test.path[i][j][k];
                        String name=point_name.get(point);
                        System.out.print(name + " ");
                    }

                    System.out.println();
                    System.out.println("From " + i + " to " + j + " length :"+ mintime);
                }
            }
        }


        return data;
    }

    //分别处理三类交通数据
    public static void fileProcess(Vector<String> pois,String time){
        for(int i=0;i<pois.size();i++){
            //System.out.println(i);
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);

            //String start_station=obj.getString("start_station");
            //为了精简站点数据，将start_station改成start_city
            String start_station=obj.getString("start_city");
            start_station= Tool.delect_content_inBrackets(start_station,"(",")");
            start_station= Tool.delect_content_inBrackets(start_station,"（","）");
            start_station= Tool.delect_content_inBrackets(start_station,"(","）");
            start_station= Tool.delect_content_inBrackets(start_station,"（",")");

            if(map.containsKey(start_station)){
                arrival_costime=map.get(start_station);
            }else {
                arrival_costime=new HashMap<>();
            }
            timeCalculationMethod(obj,time,arrival_costime);
        }
    }

    //在基准时间的条件下，计算人任意两点之间的最短时间
    public static void timeCalculationMethod(JSONObject obj,String time,Map<String,Double> arrival_costime){
        JSONObject arrival_stations=obj.getJSONObject("arrival_stations");


        Iterator<String> stations=arrival_stations.keys();

        DateFormat df = new SimpleDateFormat("HH:mm");
        Date standtime=null;
        try {
            standtime=df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        while (stations.hasNext()){
            String station=stations.next();
            JSONObject timeTable=arrival_stations.getJSONObject(station);

            Iterator<String> ts=timeTable.keys();

            Map<Long,String> costime=new HashMap<>();
            List<Long> list=new ArrayList<>();
            while (ts.hasNext()){
                try {

                    String t=ts.next();
                    Date d_start = df.parse(t.replace("*","").replace("(13:50暂停)","13:50").replace("冬","").replace("00(9:30)","09:30"));
                    long diff=d_start.getTime()-standtime.getTime();

                    if(diff<0){
                        diff=d_start.getTime()+1000 * 60 * 60*24-standtime.getTime();
                    }

                    costime.put(diff,t);
                    list.add(diff);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            Long min=Collections.min(list);
            double hours =(double) min / (1000 * 60 * 60 );
            hours =(Math.round(hours*100)/100.0);

            String  closestime=costime.get(min);
            JSONObject current=timeTable.getJSONObject(closestime);
            double time_consume=current.getDouble("time_consume")+hours;

            //String start_station=obj.getString("start_station");
            String start_station=obj.getString("start_city");
            start_station= Tool.delect_content_inBrackets(start_station,"(",")");
            start_station= Tool.delect_content_inBrackets(start_station,"（","）");
            start_station= Tool.delect_content_inBrackets(start_station,"(","）");
            start_station= Tool.delect_content_inBrackets(start_station,"（",")");

            station= Tool.delect_content_inBrackets(station,"(",")");
            station= Tool.delect_content_inBrackets(station,"（","）");
            station= Tool.delect_content_inBrackets(station,"(","）");
            station= Tool.delect_content_inBrackets(station,"（",")");

            if(arrival_costime.containsKey(station)){
                //System.out.println(start_station+"到"+station+"之前有记录了~");
                double c1=arrival_costime.get(station);
                if(c1>time_consume){
                    //System.out.println("这次计算的最短时间比上次更短~");
                    arrival_costime.put(station,time_consume);
                    map.put(start_station,arrival_costime);
                }
            }else {
                arrival_costime.put(station,time_consume);
                map.put(start_station,arrival_costime);
            }
        }
    }

}
