package com.svail.grid50.util.pois;

import com.svail.util.FileTool;
import net.sf.json.JSONObject;

import java.util.Vector;

/**
 * Created by ZhouXiang on 2016/12/20.
 */
public class School {
    public static void main(String[] args){
        toJson("D:\\小论文\\poi资料\\学校\\各区小学\\延庆县小学.txt");
    }
    public static void toJson(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");
        String poi="";
        String name="";
        String location;
        String code;
        String type;
        String[] temp;
        String qualify;
        String postCode;

        JSONObject obj;
        String first;
        for(int i=0;i<pois.size();i++){
            obj=new JSONObject();
            poi=pois.elementAt(i);
            temp=poi.split(",");
            if(temp.length>=4){
                obj.put("code",temp[0]);
                obj.put("name",temp[1]);
                obj.put("address",temp[2]);
                obj.put("location",temp[3]);
            }else{
                System.out.println(poi);
            }
            //System.out.println(obj);
            if(obj.size()!=0){
                FileTool.Dump(obj.toString(),file.replace(".txt","_json.txt"),"utf-8");
            }
        }
    }
}
