package com.svail.grid50;

import com.svail.util.FileTool;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import static com.svail.util.TestStatistics.getVariance;

/**
 * Created by ZhouXiang on 2017/4/14.
 * 这个类包含了小论文修改所需要的所有程序
 * 1、将json数据转成geojson
 * 2、计算每个区块内值的方差和区块间的方差
 */
public class ThesisModification_18 {
    public static void main(String[] args){

        /*
        1、计算莫兰指数
        String path="D:\\1_小论文修改\\莫兰指数计算\\";

        for(int i=8;i<=21;i++){

            try{
                if(i<=7){
                    toGeoJson(path+"坐标串_"+i+".txt",i);
                    JSONObject obj=new JSONObject();
                    obj.put("type","FeatureCollection");
                    obj.put("features",features);
                    FileTool.Dump(obj.toString(),path+"结果"+i+".txt","utf-8");
                    features=new JSONArray();
                }else {
                    toGeoJson(path+"坐标串_"+i+".txt",i);
                }

            }catch (NullPointerException e){
                e.getStackTrace();
            }



        }

        JSONObject obj=new JSONObject();
        obj.put("type","FeatureCollection");
        obj.put("features",features);
        FileTool.Dump(obj.toString(),path+"结果"+8+".txt","utf-8");*/

        /*2、计算每个小区块的方差值*/

        /*String path="D:\\1_小论文修改\\地理探测器\\";
        for(int i=1;i<=21;i++){
            try{
                ContourLineCode(path+"等值线_"+i+".txt",path+"坐标串_"+i+".txt",path,i-1);
            }catch (NullPointerException e){
                e.getStackTrace();
            }
        }*/


        /*3、将矩阵数据存储在map中*/

        //geoDetector();

        /*4.批量将文件中低价的大斑块过滤*/
        String storepath="D:\\1_小论文修改\\论文\\结果最新截图\\加速度\\";
        String sourcepath="D:\\1_paper\\acceleration\\";
        String[] dates={"201511","201512","201601","201602","201603","201604",
                "201605","201606","201607","201608","201609"};
        for(int d=0;d<1;d++){
            String date=dates[d];
            System.out.println(date);
            for(int i=1;i<=25;i++){
                try{
                    ContourLineCode_acceleration(sourcepath+date+"\\等值线_"+i+".txt",sourcepath+date+"\\坐标串_"+i+".txt",storepath+date+"\\",i);
                }catch (NullPointerException e){
                    e.getStackTrace();
                }
            }
        }


    }

    public static JSONArray features=new JSONArray();
    public static void toGeoJson(String file,int value){

        Vector<String> pois= FileTool.Load(file,"utf-8");
        String poi;
        JSONObject obj;

        JSONObject result;
        JSONObject properties;
        for(int i=0;i<pois.size();i++){

            poi=pois.elementAt(i);
            obj=JSONObject.fromObject(poi);

            result=new JSONObject();
            result.put("type","Feature");

            properties=new JSONObject();
            properties.put("price",value);
            result.put("properties",properties);

            result.put("geometry",obj);

            features.add(result);
        }
    }

    //计算地理探测器p值
    public static Map<Integer,Map<Integer,JSONObject>> block_codeValue=new HashMap<>();
    public static Map<Integer,Double> map=new HashMap<>();
    public static void geoDetector(){
        String path="D:\\1_小论文修改\\地理探测器\\";
        for(int i=1;i<=21;i++){
            try{
                System.out.println(i);
                matrixToCodePrice(path+"ContourLine-2015-10.txt",path+"删减大面积图斑后_"+i+".txt",i);
            }catch (NullPointerException e){
                e.getStackTrace();
            }
        }

        Map<Integer,JSONObject> row_json;
        JSONObject obj;
        int code;

        double total=0;
        int N=0;
        double SUM_Y2=0;
        double SUM_NY2=0;
        for(Map.Entry<Integer,Map<Integer,JSONObject>> entry:block_codeValue.entrySet()){

            //value=entry.getKey();
            row_json=entry.getValue();
            double[] block_average=new double[row_json.size()];
            int i=0;
            for(Map.Entry<Integer,JSONObject> entry1:row_json.entrySet()){

                //row=entry1.getKey();
                obj=entry1.getValue();

                Iterator<String> codes=obj.keys();
                double sum=0;
                double average=0;
                int n=0;
                while (codes.hasNext()){
                    code=Integer.parseInt(codes.next());
                    if(map.containsKey(code)){
                        n++;
                        //System.out.println(map.get(code));
                        sum+=map.get(code);
                    }else {
                        System.out.println("这个code没有值啊");
                    }
                }
                average=sum/n;
                block_average[i]=average;//单个区块的价格均值
                i++;
            }
            double variance=getVariance(block_average);
            int nh=row_json.size();

            total+=nh*variance;
            System.out.println(nh*variance+"="+nh+"*"+variance);
            N+=nh;

            //计算每个阈值的所有区块的均值的平方和
            double sum_yh=0;
            for(int m=0;m<block_average.length;m++){
                sum_yh+=block_average[m];
            }
            System.out.println("nh="+nh+", "+"block_average.length="+block_average.length);

            double sum_yh_aver=sum_yh/block_average.length;
            SUM_Y2+=Math.pow(sum_yh_aver,2);
            SUM_NY2+=Math.sqrt(nh)*sum_yh_aver;

        }
        System.out.println(total);


        double[] blocks=new double[20];
        int j=0;
        for(int i=1;i<=21;i++){
            if(i!=19){
                blocks[j]=(double) i;
                j++;
            }
        }
        double variance=getVariance(blocks);
        double sumVariance=N*variance;
        System.out.println(sumVariance+"="+N+"*"+variance);

        double p=1-total/sumVariance;
        System.out.println(p);

        int L_1=19;
        int N_L=N-20;
        double lamda=0;
        lamda=(1/(variance))*(SUM_Y2-(1/(double)(N))*Math.pow(SUM_NY2,2));
        System.out.println("L-1 : "+L_1);
        System.out.println("N-L : "+N_L);
        System.out.println("lamda : "+lamda);
    }
    public static void matrixToCodePrice(String matrixfile,String codefile,int value){
        Vector<String> pois= FileTool.Load(matrixfile,"utf-8");
        String poi;

        int code;
        int row;
        int col;
        double price;

        for(int i=0;i<pois.size();i++){

            //System.out.println(i);
            poi=pois.elementAt(i);
            String[] array=poi.split(",");
            for(int j=0;j<array.length;j++){
                row=4000-i;
                col=j+1;
                code=(row-1)*4000+col;
                price=Double.parseDouble(array[j]);
                if(price>0){
                    map.put(code,price);
                }
            }
        }

        JSONObject obj;
        Vector<String> codes=FileTool.Load(codefile,"utf-8");

        Map<Integer,JSONObject> row_json=new HashMap<>();
        for(int i=0;i<codes.size();i++){
            poi=codes.elementAt(i);
            obj=JSONObject.fromObject(poi);

            row_json.put(i,obj);
        }
        block_codeValue.put(value,row_json);
    }


    //过滤大斑块
    public static void ContourLineCode_price(String codefile,String coordinatefile,String path,int value){
        Vector<String> pois_code= FileTool.Load(codefile,"utf-8");
        Vector<String> pois_coordinate= FileTool.Load(coordinatefile,"utf-8");
        String poi;
        JSONObject obj;

        JSONArray result=new JSONArray();

        if(pois_code.size()==pois_coordinate.size()){
            if(value<=2){
                for(int i=0;i<pois_code.size();i++){
                    poi=pois_code.elementAt(i);
                    obj=JSONObject.fromObject(poi);
                    int size=obj.size();
                    if(size>5000){
                        //System.out.println(size);
                    }else {
                        result.add(pois_coordinate.elementAt(i));
                        //FileTool.Dump(poi,path+"删减大面积图斑后_"+(value+1)+".txt","utf-8");
                    }
                }
                FileTool.Dump(result.toString(),path+"polygon_"+value,"utf-8");
            }else {
                for(int i=0;i<pois_code.size();i++){
                    poi=pois_code.elementAt(i);
                    //obj=JSONObject.fromObject(poi);
                    result.add(pois_coordinate.elementAt(i));
                    //FileTool.Dump(poi,path+"删减大面积图斑后_"+(value+1)+".txt","utf-8");
                }
                FileTool.Dump(result.toString(),path+"polygon_"+value,"utf-8");
            }

        }else {
            System.out.println("两者不相等~");
        }

    }

    public static void ContourLineCode_acceleration(String codefile,String coordinatefile,String path,int value){
        Vector<String> pois_code= FileTool.Load(codefile,"utf-8");
        Vector<String> pois_coordinate= FileTool.Load(coordinatefile,"utf-8");
        String poi;
        JSONObject obj;

        JSONArray result=new JSONArray();

        if(pois_code.size()==pois_coordinate.size()){
            for(int i=0;i<pois_code.size();i++){
                poi=pois_code.elementAt(i);
                obj=JSONObject.fromObject(poi);
                int size=obj.size();
                if(size>5000){
                    System.out.println(size);
                }else {
                    result.add(pois_coordinate.elementAt(i));
                    //FileTool.Dump(poi,path+"删减大面积图斑后_"+(value+1)+".txt","utf-8");
                }
            }
            FileTool.Dump(result.toString(),path+"polygon_"+value,"utf-8");
        }else {
            System.out.println("两者不相等~");
        }

    }


}
