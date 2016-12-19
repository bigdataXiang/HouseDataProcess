package com.svail.grid50.util;

import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by ZhouXiang on 2016/12/19.
 * 该过程主要是检查【ContourLine_10】的中【priceMatrix("D:\\小论文\\PBSHADE-邻近插值\\","2015-07");】
 * 的异常问题，问题的原因在于起初字典里面只赋值了0至19的区块，没有20的区块，所以
 * 导致越界，现在需要检查这几行是否含有超过20万单价的数据
 */
public class Block_Exception {
    public static void main(String[] args){
        bugCheck("D:\\小论文\\PBSHADE-邻近插值\\block\\ContourLine-2015-10.txt");
    }
    public static void bugCheck(String file){
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            int count=0;
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                count++;
                //if(count>2894&&count<2896){
                    String poi=line;
                    String[] datas=poi.split(",");

                    for(int i=0;i<datas.length;i++){
                        if(Double.parseDouble(datas[i])>20){
                            System.out.println(datas[i]);
                            System.out.println(count);
                        }
                    }
                //}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
