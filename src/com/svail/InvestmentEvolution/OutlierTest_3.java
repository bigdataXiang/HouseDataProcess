package com.svail.InvestmentEvolution;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.grid50.util.db;
import com.svail.util.FileTool;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.util.*;

import static com.svail.grid50.CompareSource_5.compare;
import static com.svail.util.TestStatistics.getDiffer;
import static com.svail.util.TestStatistics.getVariance;

/**
 * Created by ZhouXiang on 2017/5/8.
 * 检验格网中是否有离群值
 */
public class OutlierTest_3 {
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


        //2.统计所有方差有误的格网
        /*for(int i=7;i<=12;i++){
            String ym="2015-"+i;
            statisticCode(path+"方差超限的格网_"+ym+".txt",ym);
        }
        for(int i=1;i<=12;i++){
            String ym="2016-"+i;
            statisticCode(path+"方差超限的格网_"+ym+".txt",ym);
        }
        for(int i=1;i<=5;i++){
            String ym="2017-"+i;
            statisticCode(path+"方差超限的格网_"+ym+".txt",ym);
        }
        for(Map.Entry<Integer,JSONObject> entry:map.entrySet()){
            FileTool.Dump(entry.getKey()+";"+entry.getValue(),path+"所有方差超限的格网.txt","utf-8");
        }*/


        //3.查看每个格网的超限数据
        callErrorData(path+"所有方差超限的格网.txt",path+"所有方差超限的格网_原数据.txt");




    }
    public static Map<Integer,JSONObject> map=new HashMap<>();

    //1.计算每个月每个格网的方差和离群值
    public static void test(int year,int month,String outname,String inname,String path){
        DBCollection coll_output = db.getDB("InvestmentEvolution").getCollection(outname);
        DBCollection coll_input = db.getDB("InvestmentEvolution").getCollection(inname);

        BasicDBObject doc=new BasicDBObject();
        doc.put("year",year);
        doc.put("month",month);

        DBCursor cs=coll_output.find(doc);
        Map<Integer,Set<Double>> map=new HashMap<>();
        BasicDBObject doc1=new BasicDBObject();
        Set<Double> set;
        int code;
        while (cs.hasNext()){
            doc1=(BasicDBObject)cs.next();
            code=doc1.getInt("code");
            if(map.containsKey(code)){
                set=map.get(code);
            }else {
                set=new HashSet<>();
            }
            set.add(doc1.getDouble("unitprice"));
            map.put(code,set);
        }


        int size;
        double variance;
        double differ;
        double tag;
        for(Map.Entry<Integer,Set<Double>> entry:map.entrySet()){

            try{
                code=entry.getKey();
                set=entry.getValue();
                size=set.size();
                double[] up=new double[size];
                Iterator<Double> it=set.iterator();
                int i=0;
                while (it.hasNext()){
                    up[i]=it.next();
                    i++;
                }

                JSONObject obj=new JSONObject();
                variance=getVariance(up);
                obj.put("code",code);
                //System.out.println(variance);
                obj.put("variance",variance);

                JSONArray array=new JSONArray();
                List<Double> list=new ArrayList<>();
                for(int j=0;j<up.length;j++){
                    tag=up[j];
                    differ=getDiffer(tag,up);

                    if(differ>1.5){
                        array.add(tag+"-"+differ);
                        list.add(differ);
                    }

                }
                obj.put("list",list);
                obj.put("array",array);

                if(variance>2){
                    //System.out.println(obj);
                    FileTool.Dump(obj.toString(),path+"方差超限的格网_"+year+"-"+month+".txt","utf-8");
                }else {
                    if(list.size()!=0){
                        //System.out.println(obj);
                    }
                }
            }catch (JSONException e){
                System.out.println(doc);
            }
        }

    }

    //2.统计所有方差有误的格网
    public static void statisticCode(String sourcefile,String ym){
        Vector<String> pois=FileTool.Load(sourcefile,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            int code=obj.getInt("code");
            double variance=obj.getDouble("variance");

            JSONObject o=new JSONObject();
            if(map.containsKey(code)){
                o=map.get(code);
            }
            o.put(ym,variance);
            map.put(code,o);
        }
    }

    //3.查看每个格网的超限数据
    public static void callErrorData(String sourcefile,String storefile){
        Vector<String> pois=FileTool.Load(sourcefile,"utf-8");
        for(int i=543;i<pois.size();i++){
            String[] poi=pois.elementAt(i).split(";");
            int code=Integer.parseInt(poi[0]);
            System.out.println(i);
            JSONObject obj=JSONObject.fromObject(poi[1]);
            Iterator<String> it=obj.keys();
            while (it.hasNext()){
                String ym=it.next();
                double variance=obj.getDouble(ym);
                String[] date=ym.split("-");
                int year=Integer.parseInt(date[0]);
                int month=Integer.parseInt(date[1]);

                String str=code+"-"+variance+":";
                //System.out.println(str);
                //FileTool.Dump(str,storefile,"utf-8");
                compare(code,year,month,"InvestmentEvolution","BasicData_Resold_gd_plus",storefile);

                String str1="*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*";
                //System.out.println(str1);
                //FileTool.Dump(str1,storefile,"utf-8");
            }
        }
    }


}
