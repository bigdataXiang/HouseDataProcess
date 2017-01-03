package com.svail;

import com.svail.util.FileTool;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

import static com.svail.grid50.BatchProcess_1.addressMatch_GaoDe;
import static com.svail.grid50.BatchProcess_1.batchProcess;
import static com.svail.grid50.GridData_Resold_6.initial;
import static com.svail.grid50.util.GetAllCommunity.screen;
import static com.svail.grid50.util.GetAllCommunity.tidy_gaode;

public class Main {

    public static String sourcepath="D:\\小论文\\poi资料\\学校\\各区中学\\";
    public static void main(String[] args) throws UnsupportedEncodingException {

        gaode();

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
        String filename="城八区中学名录_json.txt";
        String[] keys={"name"};//"address","name","community"

        addressMatch_GaoDe(381,keys,sourcepath,filename,"北京","47c4f310a83c76373706414a6a22907e");

    }


}
