package com.svail.grid50.util;

import com.mongodb.util.JSON;
import com.svail.util.FileTool;
import net.sf.json.JSONObject;

import java.util.Iterator;
import java.util.Vector;

/**
 * Created by ZhouXiang on 2016/12/16.
 * 这个类主要将【PBSHADE_Spatial_7】生成的满足插值精度的数据【interpolation_value_grids.txt】
 * 再次作检验排查插值结果的极高值或者极低值
 */
public class Interpolation_Precision_Inspection {
    public static void main(String[] args){
        precision_Inspection("D:\\小论文\\PBSHADE-邻近插值\\4-17个月时序大于1的插值数据\\interpolation_value_grids.txt",
                "2015-07",3,
                "D:\\小论文\\PBSHADE-邻近插值\\4-17个月时序大于1的插值数据\\interpolation_value_grids_中有问题的数据.txt",
                "D:\\小论文\\PBSHADE-邻近插值\\4-17个月时序大于1的插值数据\\interpolation_value_grids_中没有问题的数据.txt");
    }
    public static void precision_Inspection(String file,String compare_month,double differ,String problem,String ok){
        Vector<String> pois= FileTool.Load(file,"utf-8");
        String poi;
        String date_price;
        JSONObject obj;
        String month;
        double difference;
        double price;
        for(int i=0;i<pois.size();i++){

            int count=0;
            poi=pois.elementAt(i);
            date_price=poi.substring(poi.indexOf(";")+";".length());
            obj=JSONObject.fromObject(date_price);

            Iterator<String> months=obj.keys();
            while (months.hasNext()){
                month=months.next();

                if(month.equals(compare_month)){

                }else {
                    price=obj.getDouble(month);
                    if(price<0){
                        System.out.println("price小于0："+poi);
                        FileTool.Dump("price小于0："+poi,problem,"utf-8");
                        count++;
                        break;
                    }else {
                        difference=Math.abs(price-obj.getDouble(compare_month));
                        if (difference>differ){
                            System.out.println("价格相差太大："+poi);
                            FileTool.Dump("价格相差太大："+poi,problem,"utf-8");
                            count++;
                            break;
                        }
                    }
                }
            }
            if(count==0){
                FileTool.Dump(poi,ok,"utf-8");
            }
        }
    }
}
