package com.svail.grid50.util.pois;

import com.svail.util.FileTool;

import java.util.Vector;

/**
 * Created by ZhouXiang on 2016/12/21.
 */
public class Traffic {
    public static void main(String[] args){
        subWay("D:\\小论文\\poi资料\\地铁\\地铁线路.txt");
    }
    public static void subWay(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.get(i);

            FileTool.Dump("\""+poi+"地铁站\",",file.replace(".txt","_引号.txt"),"utf-8");
        }
    }
}
