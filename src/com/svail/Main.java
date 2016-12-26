package com.svail;

import com.svail.util.FileTool;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

import static com.svail.grid50.BatchProcess_1.addressMatch_GaoDe;
import static com.svail.grid50.BatchProcess_1.batchProcess;
import static com.svail.grid50.util.GetAllCommunity.screen;
import static com.svail.grid50.util.GetAllCommunity.tidy_gaode;

public class Main {

    public static String sourcepath="D:\\小论文\\poi资料\\企业\\";
    public static void main(String[] args) throws UnsupportedEncodingException {

        String filename="北京写字楼名录(500家)_json.txt";
        String[] keys={"地址"};//"address","name","community"
        batchProcess(1,sourcepath,filename,keys);

    }

    //老师的地理编码程序
    public static void geocode(){
        try {
            boolean bath=false;
            String filename;
            String[] keys={"ADDRESS"};//"address","name","community"
            if(bath){
                Vector<String> filenames= FileTool.Load(sourcepath+"filename.txt","utf-8");
                for(int i=0;i<filenames.size();i++){
                    filename=filenames.elementAt(i);
                    batchProcess(2000,sourcepath,filename,keys);
                }
            }else {
                filename="poi_json_deuplicate.txt";
                batchProcess(2000,sourcepath,filename,keys);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void gaode() throws UnsupportedEncodingException {
        String filename="所有小区名称_去除冗余git_匹配不成功_原始_地址匹配失败_高德解析信息_高德地址匹配成功.txt";
        String[] keys={"community"};//"address","name","community"

        addressMatch_GaoDe(0,keys,sourcepath,filename,"北京","8ca252e49863ab4f4d9fee62b14c5c18");

    }


}
