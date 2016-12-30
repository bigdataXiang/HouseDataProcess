package com.svail.grid50.util.pois;

import com.svail.util.FileTool;
import net.sf.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * Created by ZhouXiang on 2016/12/21.
 */
public class Traffic {
    public static void main(String[] args){
        //subWay("D:\\小论文\\poi资料\\地铁\\地铁线路.txt");
        busStation("D:\\小论文\\poi资料\\shpPoi\\P05公交车站_point_json.txt");
    }
    public static void subWay(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.get(i);

            FileTool.Dump("\""+poi+"地铁站\",",file.replace(".txt","_引号.txt"),"utf-8");
        }
    }

    public static void busStation(String file){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        Set<String> set=new HashSet<>();
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            String name=obj.getString("NAME");
            set.add(name);
        }

        Iterator<String> it=set.iterator();
        while (it.hasNext()){
            String name=it.next();
            JSONObject obj=new JSONObject();
            obj.put("name",name);
            FileTool.Dump(obj.toString(),file.replace(".txt","_公交站.txt"),"utf-8");
        }
    }
}
