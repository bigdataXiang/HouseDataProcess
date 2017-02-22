package com.svail.grid50;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.grid50.util.db;
import com.svail.util.FileTool;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.bcel.generic.F2D;

import java.util.*;

/**
 * Created by ZhouXiang on 2017/2/21.
 * 此类用于房地产投资模型的计算
 * 1、获取小区划内的有效栅格数据
 * 2、确定单元栅格内的主打户型
 */
public class FindEffectiveGrid_16 {
    public static void main(String[] args){

        gridToMap("D:\\1_paper\\Investment model\\2-mongodb中的所有格网\\格网编码.txt");

        String[] dates={"201509","201510","201511","201512","201601","201602","201603","201604",
                "201605","201606","201607","201608","201609","201610","201611"};
        for(int m=1;m<dates.length;m++){
            String sourcepath="D:\\1_paper\\relative\\"+dates[m]+"\\";
            String storepath="D:\\1_paper\\Investment model\\1-有效栅格\\"+dates[m]+"\\";
            for(int i=1;i<=21;i++){
                try{
                    System.out.println(i);
                    findGrid(sourcepath+"等值线_"+i+".txt",
                            storepath+"等值线_"+i+".txt");
                }catch (NullPointerException e){
                    e.getStackTrace();
                }
            }
        }


        /*String storepath="D:\\1_paper\\Investment model\\2-mongodb中的所有格网\\";
        findGridFromMongo(storepath+"格网编码.txt");*/

        /*String sourcepath="D:\\1_paper\\Investment model\\1-有效栅格\\201507\\";
        String storepath="D:\\1_paper\\Investment model\\3-栅格的主要户型\\201507\\";
        DBCollection coll= db.getDB("paper").getCollection("GridData_Resold_gd");
        for(int i=2;i<=21;i++){
            findHouseType(sourcepath+"等值线_"+i+".txt",storepath+"等值线_"+i+".txt",coll,"2015","07");
        }*/

        /*blockHouseType("D:\\1_paper\\Investment model\\3-栅格的主要户型\\201507\\等值线_1.txt",
                "D:\\1_paper\\Investment model\\4-确定栅格唯一户型\\201507\\等值线_1.txt");*/


    }

    //1.获取每个月份的每个价格区划下的小区划中的有具体房源的小栅格
    public  static void findGrid(String sourcefile,String storefile){
        Vector<String> pois= FileTool.Load(sourcefile,"utf-8");
        String poi;
        JSONObject obj;
        Iterator<String> it;
        String key;
        for(int i=0;i<pois.size();i++){
            poi=pois.elementAt(i);
            obj=JSONObject.fromObject(poi);
            it=obj.keys();

            System.out.println(i+":"+obj.size());
            JSONArray array=new JSONArray();
            while (it.hasNext()){
                key=it.next();
                if(GridMap.containsKey(key)){
                    array.add(key);
                }
            }
            FileTool.Dump(array.toString(),storefile,"utf-8");
        }
    }

    //2.获取数据库中有数据的栅格
    public static void findGridFromMongo(String storefile){
        DBCollection coll= db.getDB("paper").getCollection("GridData_Resold_gd");
        DBCursor cs=coll.find();

        Set<Integer> grids=new HashSet<>();
        BasicDBObject doc;
        int code;
        int i=0;
        while (cs.hasNext()){
            i++;
            System.out.println(i);
            doc=(BasicDBObject)cs.next();
            code=doc.getInt("code");
            grids.add(code);
        }

        Iterator<Integer> it=grids.iterator();

        while (it.hasNext()){
            FileTool.Dump(it.next().toString(),storefile,"utf-8");
        }

    }

    //3.将2中得到的所有格网存在Map中
    public static Map<String,String> GridMap=new HashMap<>();
    public static void gridToMap(String file){
        Vector<String> codes=FileTool.Load(file,"utf-8");
        String code;
        for(int i=0;i<codes.size();i++){
            code=codes.elementAt(i);
            GridMap.put(code,"");
        }
    }

    //4.挑选出每个栅格每个月份中的主打户型及其附属的面积、价格信息
    public static void findHouseType(String sourcefile,String storefile,DBCollection coll,String year,String month){
        Vector<String> codes=FileTool.Load(sourcefile,"utf-8");
        JSONArray codearray;
        BasicDBObject doc;
        JSONObject obj;
        for(int i=0;i<codes.size();i++){
            codearray= JSONArray.fromObject(codes.elementAt(i));
            JSONArray resultarray=new JSONArray();
            if(codearray.size()!=0){
                for(int j=0;j<codearray.size();j++){
                    int code=codearray.getInt(j);
                    BasicDBObject document=new BasicDBObject();
                    document.put("code",code);
                    //document.put("year",year);
                    //document.put("month",month);
                    DBCursor cs=coll.find(document);

                    JSONObject result=new JSONObject();
                    if(cs.hasNext()){
                        doc=(BasicDBObject)cs.next();
                        //System.out.println(doc);
                        if(doc.containsField("type")){
                            JSONObject type=JSONObject.fromObject(doc.get("type"));
                            Iterator<String> it=type.keys();
                            double ratio;
                            if(type.size()>1){
                                List<Double> ratios=new ArrayList<>();
                                while (it.hasNext()){
                                    String housetype=it.next();
                                    JSONObject ht=type.getJSONObject(housetype);
                                    //System.out.println(ht);
                                    if(ht.containsKey("ratio")){
                                        ratio=ht.getDouble("ratio");
                                        ratios.add(ratio);
                                    }
                                }

                                double max_ratio=Collections.max(ratios);
                                it=type.keys();
                                while (it.hasNext()){
                                    String housetype=it.next();
                                    JSONObject ht=type.getJSONObject(housetype);
                                    double r=ht.getDouble("ratio");
                                    if(r==max_ratio){
                                        ht.put("houseType",housetype);
                                        result.put(code,ht);
                                        break;
                                    }
                                }
                            }else {
                                while (it.hasNext()) {
                                    String housetype = it.next();
                                    JSONObject ht = type.getJSONObject(housetype);
                                    ht.put("houseType",housetype);
                                    result.put(code,ht);
                                }
                            }
                            resultarray.add(result);
                        }
                    }
                }
            }
            FileTool.Dump(resultarray.toString(),storefile,"utf-8");
        }
    }

    //5.找出区块中的主要户型
    public static void blockHouseType(String sourcefile,String storefile){
        Vector<String> codes=FileTool.Load(sourcefile,"utf-8");
        for(int i=0;i<codes.size();i++){
            JSONArray array=JSONArray.fromObject(codes.elementAt(i));
            JSONArray result=new JSONArray();
            if(array.size()!=0){
                for(int j=0;j<array.size();j++){
                    JSONObject obj=array.getJSONObject(i);
                    System.out.println(obj);
                    String houseType=obj.getString("houseType");
                    result.add(houseType);
                }
            }
            FileTool.Dump(result.toString(),storefile,"utf-8");
        }
    }
}
