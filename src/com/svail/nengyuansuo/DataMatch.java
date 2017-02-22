package com.svail.nengyuansuo;

import com.svail.util.FileTool;
import net.sf.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by ZhouXiang on 2017/2/10.
 */
public class DataMatch {
    public static void main(String[] args){
        String path="D:\\能源所\\";

        match(path+"四省数据汇总_json.txt",path+"四省主体功能区数据汇总_json.txt");
    }
    public static void match(String source,String target){
        Vector<String> ss= FileTool.Load(source,"utf-8");
        Vector<String> tt= FileTool.Load(target,"utf-8");

        Map<String,JSONObject> map=new HashMap<>();
        for(int i=0;i<ss.size();i++){
            String s=ss.elementAt(i);
            JSONObject obj=JSONObject.fromObject(s);
            String quxian=obj.getString("区县名称");
            map.put(quxian,obj);
        }

        for(int i=0;i<tt.size();i++){
            String t=tt.elementAt(i);
            JSONObject obj=JSONObject.fromObject(t);
            String quxian=obj.getString("区县名称");

            if(map.containsKey(quxian)){
                JSONObject o=map.get(quxian);

                System.out.println(o);
                obj.put("行政区土地面积",o.get("行政区土地面积"));
                obj.put("户籍人口",o.get("户籍人口"));
                obj.put("地区生产总值",o.get("地区生产总值"));
                obj.put("人均GDP",o.get("人均GDP"));
                obj.put("第一产业",o.get("第一产业"));
                obj.put("第二产业",o.get("第二产业"));
                obj.put("第三产业",o.get("第三产业"));
                obj.put("省",o.get("所属省"));
                //所属省
            }
            FileTool.Dump(obj.toString(),target.replace(".txt","_结果.txt"),"utf-8");
        }

    }
}
