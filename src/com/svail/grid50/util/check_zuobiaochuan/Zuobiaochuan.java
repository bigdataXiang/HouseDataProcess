package com.svail.grid50.util.check_zuobiaochuan;

import com.svail.util.FileTool;
import net.sf.json.JSONObject;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * Created by ZhouXiang on 2017/1/4.
 */
public class Zuobiaochuan {
    public static void main(String[] args){
        String path="D:\\小论文\\poi资料\\小区\\小区地理编码原始数据\\最后结果\\校对结果\\插值\\";
        check(path+"等值线_3.txt");
    }

    //6410083
    public static void check(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");

        for(int i=0;i<pois.size();i++) {
            String poi = pois.elementAt(i);
            JSONObject obj = JSONObject.fromObject(poi);
            System.out.println(i);
            if(obj.containsKey("6410083")){

                System.out.println(obj);
                System.out.println(obj.size());
            }
        }
    }

}
