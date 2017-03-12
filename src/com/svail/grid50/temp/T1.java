package com.svail.grid50.temp;

import com.svail.util.FileTool;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

/**
 * Created by ZhouXiang on 2017/1/1.
 */
public class T1 {
    public static void main(String[] args) throws UnsupportedEncodingException {


        /*String sourcepath="D:\\1_paper\\Investment model\\1-有效栅格\\201608\\";
        String storepath="D:\\1_paper\\Investment model\\3-栅格的主要户型\\201608\\";
        DBCollection coll= db.getDB("paper").getCollection("GridData_Resold_gd");
        for(int i=1;i<=21;i++){
            try{
                findHouseType(sourcepath+"等值线_"+i+".txt",storepath+"等值线_"+i+".txt",coll,"2016","08");
            }catch (NullPointerException e){
                e.getStackTrace();
            }
        }*/

        Vector<String> pois= FileTool.Load("D:\\1_paper\\rerun\\201510\\block-2015-10.txt","utf-8");
        for(int i=0;i<pois.size();i++){
            String[] array=pois.elementAt(i).split(",");
            for(int j=0;j<4000;j++){
                String s="";
                for(int m=0;m<4000;m++){
                    s+=array[j*4000+m];
                }
                FileTool.Dump(s,"D:\\1_paper\\rerun\\201510\\block-2015-10-ok.txt","utf-8");
            }
        }
    }
}
