package com.svail.util;

import java.io.File;

/**
 * Created by ZhouXiang on 2016/12/21.
 */
public class getNamesInFolder {
    public static void main(String[] args) {

        String path = "D:\\小论文\\poi资料\\学校\\奥数网\\";
        getFileName(path);
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
