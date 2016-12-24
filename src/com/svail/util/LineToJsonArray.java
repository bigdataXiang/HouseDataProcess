package com.svail.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Vector;

/**
 * Created by ZhouXiang on 2016/12/23.
 * 将逐行的信息以数组的形式存储成一行，方便静态文件调用
 */
public class LineToJsonArray {
    public static void main(String[] args){

        String[] removeKeys={"PYNAME","TYPE","ADMINCODE","IMPORTANCE","ADDRESS","TELEPHONE"};
        lineToArray("D:\\小论文\\poi资料\\shpPoi\\P03火车站地铁站_point_json.txt",removeKeys);
    }
    public static void lineToArray(String file,String[] removeKeys){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        String poi;
        JSONObject obj;
        JSONArray array=new JSONArray();
        for(int i=0;i<pois.size();i++){
            poi=pois.elementAt(i);
            obj=JSONObject.fromObject(poi);

            for(int j=0;j<removeKeys.length;j++){
                if(obj.containsKey(removeKeys[j])){
                    obj.remove(removeKeys[j]);
                }
            }
            array.add(obj);
        }
        obj=new JSONObject();
        obj.put("data",array);
        FileTool.Dump(obj.toString(),file.replace(".txt","_array.txt"),"utf-8");
    }
}
