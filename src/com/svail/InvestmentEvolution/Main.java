package com.svail.InvestmentEvolution;

import com.orsoncharts.util.json.JSONObject;
import com.svail.util.FileTool;

import java.util.Map;

import static com.svail.InvestmentEvolution.OutlierTest_3.*;

/**
 * Created by ZhouXiang on 2017/5/8.
 * 用于临时运行程序的类
 */
public class Main {
    public static void main(String[] args){

        String path="D:\\1_基于房产调控政策下的房产投资市场格局演变分析——以北京为例\\格网数据\\二手房\\";

        //1.计算每个月每个格网的方差和离群值
        /*for(int i=7;i<=12;i++){
            test(2015,i,"BasicData_Resold_gd_plus","BasicData_Resold_gd_plus_ok",path);
        }
        for(int i=1;i<=12;i++){
            test(2016,i,"BasicData_Resold_gd_plus","BasicData_Resold_gd_plus_ok",path);
        }
        for(int i=1;i<=5;i++){
            test(2017,i,"BasicData_Resold_gd_plus","BasicData_Resold_gd_plus_ok",path);
        }*/


        handlingOverlimit(path+"所有方差超限的格网_原数据.txt");
    }

}
