package com.svail.grid50;

import com.svail.util.FileTool;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import static com.svail.nengyuansuo.CreatExcel.*;

/**
 * Created by ZhouXiang on 2017/1/4.
 * 研究不同价格区块的影响因素:医院、地铁、学校、商场、公司等
 */
public class PriceRelevance_13 {
    public static void main(String[] args) throws IOException {
        String path="D:\\小论文\\影响因素相关性研究\\201507\\等值线\\";

        //第一步：生成等值线格网，这一步在之后可以省去了
        /*
        for(int i=1;i<21;i++){
            blockCodeStatistic(path+"等值线_"+i+".txt");
        }*/

        //统计每个区块内的poi的数目
        /*InterestPointStatistics_hosipital(path,"北京小学_格网化.txt");
        InterestPointStatistics_hosipital(path,"北京中学_格网化.txt");*/

        String[] names={"1","2","3","4","5","6","7","8","9","10"};
        writeExcel("D:\\paper\\relative\\201507\\学校\\统计情况\\北京中学_格网化_每个价格梯段内的poi统计情况.txt",names);

    }
    //统计格网数目，用于计算poi密度
    public static void gridsAmount(String file){

    }

    //对最后的统计结果进行表格化
    public static void writeExcel(String folder,String[] names) throws IOException {

        //创建一个Excel(or new XSSFWorkbook())
        Workbook wb = new HSSFWorkbook();
        //创建表格
        Sheet sheet = wb.createSheet("test");
        //创建行
        Row row = sheet.createRow(0);
        //设置行高
        row.setHeightInPoints(30);
        designExcel(sheet,names);

        Vector<String> rds = FileTool.Load(folder, "UTF-8");

        int count=0;
        try{
            for (int i = 0; i < rds.size(); i++) {
                String poi=rds.elementAt(i);
                String price=poi.substring(0,poi.indexOf(","));
                String json=poi.substring(poi.indexOf("{"));
                JSONObject obj=JSONObject.fromObject(json);
                count++;
                row = sheet.createRow(count);
                for (int k = 0; k < names.length; k++) {
                    row.createCell(k).setCellValue(getObjValue_int(obj,names[k]));
                }
            }
        }catch (NullPointerException e){
            e.getStackTrace();
        }

        FileOutputStream fos = new FileOutputStream(folder.replace(".txt", "_excel.xls"));
        wb.write(fos);
        if (null != fos) {
            fos.close();
        }
    }


    //统计不同价格区块内格网的code，同一个价格等级的所有区块的code放在一起
    public static void blockCodeStatistic(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");
        JSONArray array=new JSONArray();
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            Iterator<String> codes=obj.keys();
            while (codes.hasNext()){
                String str=codes.next();
                System.out.println(str);
                FileTool.Dump(str,file.replace(".txt","_格网集合.txt"),"utf-8");
            }
        }
    }

    //建立一个map，key为格网code，value为该格网code对应的值
    //并且统计不同类型的兴趣点在不同的等值线区块的分布情况
    //对于地铁而言，这里可能会出现一个地铁站出现多次的情况，因为一个地铁站可能有多条换乘线路
    public static void InterestPointStatistics_subway(String path,String filename){
        Map<String,Integer> price_codes=new HashMap<>();

        for(int i=1;i<21;i++){
            Vector<String> pois=FileTool.Load(path+"等值线_"+i+"_格网集合.txt","utf-8");
            for(int j=0;j<pois.size();j++){
                String code=pois.elementAt(j);
                price_codes.put(code,i);
            }
        }

        Vector<String> pois=FileTool.Load(path+filename,"utf-8");
        Map<Integer,Integer> price_nums=new HashMap<>();//计算不同价格区块内的poi的个数
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            String code=obj.getString("code");
            if(price_codes.containsKey(code)){
                int price=price_codes.get(code);

                if(price_nums.containsKey(price)){
                    int num=price_nums.get(price);
                    num=num+1;
                    //System.out.println(num);
                    price_nums.put(price,num);
                }else {
                    price_nums.put(price,1);
                }

            }else {
                FileTool.Dump(poi,path+filename.replace(".txt","_不在等值区块内的poi.txt"),"utf-8");
            }
        }

        //最后统计每个价格区块内poi的个数
        for(Map.Entry<Integer,Integer> entry:price_nums.entrySet()){
            int price=entry.getKey();
            int nums=entry.getValue();
            String str=price+","+nums;
            FileTool.Dump(str,path+filename.replace(".txt","_每个价格梯段内的poi统计情况.txt"),"utf-8");
        }
    }

    //对于医院而言要考虑不同等级医院的权重情况
    public static void InterestPointStatistics_hosipital(String path,String filename){
        Map<String,Integer> price_codes=new HashMap<>();

        for(int i=1;i<21;i++){
            Vector<String> pois=FileTool.Load(path+"等值线_"+i+"_格网集合.txt","utf-8");
            for(int j=0;j<pois.size();j++){
                String code=pois.elementAt(j);
                price_codes.put(code,i);
            }
        }

        Vector<String> pois=FileTool.Load(path+filename,"utf-8");
        Map<Integer,Map<String,Integer>> price_nums=new HashMap<>();//计算不同价格区块内的poi的个数
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            String code=obj.getString("code");
            String qualify_value=obj.getString("qualify_value");

            if(price_codes.containsKey(code)){
                int price=price_codes.get(code);

                if(price_nums.containsKey(price)){
                    Map<String,Integer> map=price_nums.get(price);
                    if(map.containsKey(qualify_value)){
                        int num=map.get(qualify_value);
                        map.put(qualify_value,++num);
                        price_nums.put(price,map);
                    }else {
                        map.put(qualify_value,1);
                        price_nums.put(price,map);
                    }
                }else {
                    Map<String,Integer> map=new HashMap<>();
                    map.put(qualify_value,1);
                    price_nums.put(price,map);
                }

            }else {
                FileTool.Dump(poi,path+filename.replace(".txt","_不在等值区块内的poi.txt"),"utf-8");
            }
        }

        //最后统计每个价格区块内poi的个数
        for(Map.Entry<Integer,Map<String,Integer>> entry:price_nums.entrySet()){
            int price=entry.getKey();
            Map<String,Integer> nums=entry.getValue();
            JSONObject obj=new JSONObject();
            for(Map.Entry<String,Integer> entry1:nums.entrySet()){
                String q=entry1.getKey();
                int num=entry1.getValue();
                obj.put(q,num);
            }
            String str=price+","+obj.toString();
            FileTool.Dump(str,path+filename.replace(".txt","_每个价格梯段内的poi统计情况.txt"),"utf-8");
        }
    }
}
