package com.svail.grid50.temp;

import com.mongodb.DBCollection;
import com.svail.grid50.util.db;

import java.io.UnsupportedEncodingException;

import static com.svail.grid50.FindEffectiveGrid_16.blockHouseType;
import static com.svail.grid50.FindEffectiveGrid_16.findHouseType;
import static com.svail.grid50.GridData_Resold_6.initial;

/**
 * Created by ZhouXiang on 2017/1/1.
 */
public class T1 {
    public static void main(String[] args) throws UnsupportedEncodingException {


        String sourcepath="D:\\1_paper\\Investment model\\1-有效栅格\\201608\\";
        String storepath="D:\\1_paper\\Investment model\\3-栅格的主要户型\\201608\\";
        DBCollection coll= db.getDB("paper").getCollection("GridData_Resold_gd");
        for(int i=1;i<=21;i++){
            try{
                findHouseType(sourcepath+"等值线_"+i+".txt",storepath+"等值线_"+i+".txt",coll,"2016","08");
            }catch (NullPointerException e){
                e.getStackTrace();
            }
        }

    }
}
