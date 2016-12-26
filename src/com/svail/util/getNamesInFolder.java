package com.svail.util;

import java.io.File;

/**
 * Created by ZhouXiang on 2016/12/21.
 */
public class getNamesInFolder {
    public static void main(String[] args) {

        String[] province={"01北京","02天津","03河北","04山西","05内蒙古","06辽宁","07吉林","09上海","10江苏","11浙江","12安徽",
                "13福建","14江西","15山东","16河南","17湖北","19广东","20广西","21海南","22重庆","23四川","24贵州","25云南",
                "26西藏","27陕西","28甘肃","29青海","30宁夏","31新疆维吾尔族自治区"};
        for(int i=0;i<province.length;i++){
            String path = "D:\\能源所\\【各省自治区直辖市主体功能区数据库】\\"+province[i]+"\\json\\";
            //getFileName(path);
            getFilePath(path);
        }
        //getFileName(path);
        //getFilePath(path);
    }

    private static void getFileName(String path){
        File file = new File(path);
        File[] array = file.listFiles();

        for(int i=0;i<array.length;i++){
            if(array[i].isFile()){
                System.out.println(array[i].getName());
                FileTool.Dump(array[i].getName(),path+"filename.txt","utf-8");
            }else if(array[i].isDirectory()){
                getFileName(array[i].getPath());
            }
        }
    }

    private static void getFilePath(String path){
        File file = new File(path);
        File[] array = file.listFiles();

        for(int i=0;i<array.length;i++){
            if(array[i].isFile()){
                System.out.println(array[i].getPath());
                FileTool.Dump(array[i].getPath(),path+"filename.txt","utf-8");
            }else if(array[i].isDirectory()){
                getFilePath(array[i].getPath());
            }
        }
    }


}
