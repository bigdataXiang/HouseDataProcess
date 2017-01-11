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


        //第一步：生成等值线格网，这一步在之后可以省去了
        /*
        for(int i=1;i<21;i++){
            blockCodeStatistic(path+"等值线_"+i+".txt");
        }*/


        //小学16万以上的区块无数据
        //中学14万以上的区块无数据
       //第二步：统计每个区块内的poi的数目
        /*String path="D:\\paper\\relative\\201507\\等值线\\";
        InterestPointStatistics_noqualify(path,"公交车站_高德解析信息_格网化.txt");
*/
        //第三步：将第二步的文件生成excel
        /*String[] names={"1","2","3","4","5","6","7","8","9","10","total"};
        String path="D:\\paper\\relative\\201507\\公园绿地\\";
        writeExcel(path+"公园绿地_region_json_去重_高德解析信息_格网化_每个价格梯段内的poi统计情况.txt",names);
*/

        //统计格网数目，用于计算poi密度
        //第四步:计算每种poi类型的密度,即单位个网内poi的个数
        //生成【_密度统计.txt】文件
        /*String path="D:\\paper\\relative\\201507\\公交\\";
        Poi_Density(path+"公交车站_高德解析信息_格网化_每个价格梯段内的poi统计情况.txt",
        20,"D:\\paper\\relative\\201507\\等值线\\");*/


        //第五步：统计poi密度，并且写成excel
        /*String[] names={"1","2","3","4","5","6","7","8","9","10","total"};
        String path="D:\\paper\\relative\\201507\\公交\\";
        writeExcel(path+"公交车站_高德解析信息_格网化_每个价格梯段内的poi统计情况_密度统计.txt",names);
*/

        //计算每个价格等级区块的格网个数：
        //JSONObject obj=amoutStastic(20,"D:\\paper\\relative\\201507\\等值线\\");

//======================================================================================================================

        //第一步：确定累积的价格区间段，通过统计可知，10万以上的栅格数据相对较少，先以10万为分水岭
        //试试。超过十万的格网全部汇总到一起
        //测试：2015年7月11万以上的格网有9585
        //JSONObject obj=amoutStastic(20,"D:\\paper\\relative\\201507\\等值线\\",11);

        //第二步：统计每个区块内的数据，超过一定价格的所有区块的poi统计数据要叠加
         /*String path="D:\\paper\\relative\\201507\\等值线\\";

        InterestPointStatistics_noqualify(path,"1_公交_格网化.txt",11,20);
        InterestPointStatistics_noqualify(path,"2_银行_格网化.txt",11,20);
        InterestPointStatistics_noqualify(path,"3_休闲娱乐_格网化.txt",11,20);
        InterestPointStatistics_noqualify(path,"4_商业大厦_格网化.txt",11,20);
        InterestPointStatistics_noqualify(path,"5_零售行业_格网化.txt",11,20);
        InterestPointStatistics_noqualify(path,"6_公司企业_格网化.txt",11,20);
        InterestPointStatistics_noqualify(path,"7_餐饮服务_格网化.txt",11,20);
        InterestPointStatistics_noqualify(path,"8_宾馆酒店_格网化.txt",11,20);
        InterestPointStatistics_noqualify(path,"9_公园绿地_格网化.txt",11,20);
        InterestPointStatistics_qualify(path,"10_北京小学_格网化.txt",11,20);
        InterestPointStatistics_qualify(path,"11_北京中学_格网化.txt",11,20);
        InterestPointStatistics_noqualify(path,"12_地铁_格网化.txt",11,20);
        InterestPointStatistics_qualify(path,"13_医院_格网化.txt",11,20);*/

        //第三步：每类数据的密度统计，计算每种poi类型的密度,即单位个网内poi的个数
        //生成【_密度统计.txt】文件
        /*String path="D:\\paper\\relative\\201507\\所有因素统计汇总（11万以上累加）\\";
        String codepath="D:\\paper\\relative\\201507\\等值线\\";
        String houzui="_格网化_每个价格梯段内的poi统计情况.txt";
        Poi_Density(path+"1_公交"+houzui,20,codepath);
        Poi_Density(path+"2_银行"+houzui,20,codepath);
        Poi_Density(path+"3_休闲娱乐"+houzui,20,codepath);
        Poi_Density(path+"4_商业大厦"+houzui,20,codepath);
        Poi_Density(path+"5_零售行业"+houzui,20,codepath);
        Poi_Density(path+"6_公司企业"+houzui,20,codepath);
        Poi_Density(path+"7_餐饮服务"+houzui,20,codepath);
        Poi_Density(path+"8_宾馆酒店"+houzui,20,codepath);
        Poi_Density(path+"9_公园绿地"+houzui,20,codepath);
        Poi_Density(path+"10_北京小学"+houzui,20,codepath);
        Poi_Density(path+"11_北京中学"+houzui,20,codepath);
        Poi_Density(path+"12_地铁"+houzui,20,codepath);
        Poi_Density(path+"13_医院"+houzui,20,codepath);*/

        //第四步：将密度统计结果写成excel文件
        String[] names={"1","2","3","4","5","6","7","8","9","10","total"};
        String path="D:\\paper\\relative\\201507\\所有因素统计汇总（11万以上累加）\\";
        String houzui="_格网化_每个价格梯段内的poi统计情况_密度统计.txt";
        writeExcel(path+"1_公交"+houzui,names);
        writeExcel(path+"2_银行"+houzui,names);
        writeExcel(path+"3_休闲娱乐"+houzui,names);
        writeExcel(path+"4_商业大厦"+houzui,names);
        writeExcel(path+"5_零售行业"+houzui,names);
        writeExcel(path+"6_公司企业"+houzui,names);
        writeExcel(path+"7_餐饮服务"+houzui,names);
        writeExcel(path+"8_宾馆酒店"+houzui,names);
        writeExcel(path+"9_公园绿地"+houzui,names);
        writeExcel(path+"10_北京小学"+houzui,names);
        writeExcel(path+"11_北京中学"+houzui,names);
        writeExcel(path+"12_地铁"+houzui,names);
        writeExcel(path+"13_医院"+houzui,names);






    }
    //计算每种poi类型的密度,即单位个网内poi的个数
    public static void Poi_Density(String file,int n,String path){

        JSONObject gridsamount=amoutStastic(n,path);

        Vector<String> rds = FileTool.Load(file, "UTF-8");

        int count=0;
        for (int i = 0; i < rds.size(); i++) {
            String poi = rds.elementAt(i);
            String price = poi.substring(0, poi.indexOf(","));
            String json = poi.substring(poi.indexOf("{"));
            JSONObject obj = JSONObject.fromObject(json);

            int gridsnum=gridsamount.getInt(price);
            //遍历obj里面的key，求每个poi等级的密度
            Iterator<String> ranks=obj.keys();

            JSONObject ratio_obj=new JSONObject();
            double total=0;
            while (ranks.hasNext()){
                String rank=ranks.next();
                int amount=obj.getInt(rank);
                double ratio=(double)amount/(double)gridsnum*40000;
                ratio_obj.put(rank,ratio);
                total+=Integer.parseInt(rank)*ratio;
            }
            ratio_obj.put("total",total);

            String str=price+","+ratio_obj;
            System.out.println(str);
            FileTool.Dump(str,file.replace(".txt","_密度统计.txt"),"utf-8");
        }


    }
    //统计某个月份的各个等值线区间的格网的数目，并且写到json文件中
    //n表示一共有多少个等值区块
    public static JSONObject amoutStastic(int n,String path){

        JSONObject obj=new JSONObject();

        for(int i=1;i<=n;i++){

            try{
                int amount= gridsAmount_CodeCollection(path+"等值线_"+i+"_格网集合.txt");
                obj.put(""+i,amount);
            }catch (NullPointerException e){
                int amount= gridsAmount_ContourLine(path+"等值线_"+i+".txt");
                obj.put(""+i,amount);
            }

        }
        System.out.println(obj);

        return obj;
    }

    //critical_value表示超过这个价格的格网要进行合并
    public static JSONObject amoutStastic(int n,String path,int critical_value){

        JSONObject obj=new JSONObject();
        int amount=0;

        int total=0;
        for(int i=1;i<=n;i++){

            try{
                amount= gridsAmount_CodeCollection(path+"等值线_"+i+"_格网集合.txt");
            }catch (NullPointerException e){
                amount= gridsAmount_ContourLine(path+"等值线_"+i+".txt");
            }

            if(i<critical_value){
                obj.put(""+i,amount);
            }else {
                total+=amount;
                if(i==n){
                    obj.put(""+critical_value,total);
                }
            }
        }
        System.out.println(obj);

        return obj;
    }


    //利用【等值线_20_格网集合.txt】数据计算格网数目
    public static int gridsAmount_CodeCollection(String file){
        int size=0;
        Vector<String> codes=FileTool.Load(file,"utf-8");
            size=codes.size();
        return size;

    }

    //在【等值线_20_格网集合.txt】没有的情况下，利用【等值线_21.txt】文件计算格网数目
    public static int gridsAmount_ContourLine(String file){
        int size=0;
        Vector<String> pois=FileTool.Load(file,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            size+=obj.size();
        }
        return size;
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
                    row.createCell(k).setCellValue(getObjValue_dou(obj,names[k]));
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
    public static void InterestPointStatistics_noqualify(String path,String filename,int critical_value,int max){
        Map<String,Integer> price_codes=new HashMap<>();

        for(int i=1;i<=max;i++){
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
        int total=0;
        int count=0;
        for(Map.Entry<Integer,Integer> entry:price_nums.entrySet()){
            count++;
            int price=entry.getKey();
            int nums=entry.getValue();
            JSONObject o=new JSONObject();
            if(price<critical_value){
                o.put("1",nums);
                String str=price+","+o;
                FileTool.Dump(str,path+filename.replace(".txt","_每个价格梯段内的poi统计情况.txt"),"utf-8");
            }else {
                total+=nums;
                if(count==price_nums.size()){
                    o.put("1",total);
                    String str=critical_value+","+o;
                    FileTool.Dump(str,path+filename.replace(".txt","_每个价格梯段内的poi统计情况.txt"),"utf-8");
                }
            }

        }
    }

    //对于医院而言要考虑不同等级医院的权重情况
    public static void InterestPointStatistics_qualify(String path,String filename,int critical_value,int max){
        Map<String,Integer> price_codes=new HashMap<>();

        for(int i=1;i<=max;i++){
            System.out.println(i);
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
        int count=0;
        JSONObject total_obj=new JSONObject();
        for(Map.Entry<Integer,Map<String,Integer>> entry:price_nums.entrySet()){
            count++;

            int price=entry.getKey();
            Map<String,Integer> nums=entry.getValue();
            JSONObject obj=new JSONObject();

            if(price<critical_value){
                for(Map.Entry<String,Integer> entry1:nums.entrySet()){
                    String q=entry1.getKey();
                    int num=entry1.getValue();
                    obj.put(q,num);
                }
                String str=price+","+obj.toString();
                FileTool.Dump(str,path+filename.replace(".txt","_每个价格梯段内的poi统计情况.txt"),"utf-8");
            }else {

                for(Map.Entry<String,Integer> entry1:nums.entrySet()){
                    String q=entry1.getKey();
                    int num=entry1.getValue();
                    if(total_obj.containsKey(q)){
                        int n=total_obj.getInt(q);
                        n+=num;
                        total_obj.put(critical_value,n);
                    }else {
                        total_obj.put(critical_value,num);
                    }
                }

                if(count==price_nums.size()){
                    String str=price+","+total_obj.toString();
                    FileTool.Dump(str,path+filename.replace(".txt","_每个价格梯段内的poi统计情况.txt"),"utf-8");
                }
            }
        }
    }


}
