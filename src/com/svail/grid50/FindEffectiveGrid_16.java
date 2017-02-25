package com.svail.grid50;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.grid50.util.db;
import com.svail.util.FileTool;
import com.svail.util.JsonListCompare;
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

        /*gridToMap("D:\\1_paper\\Investment model\\2-mongodb中的所有格网\\格网编码.txt");

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
        }*/


        /*String storepath="D:\\1_paper\\Investment model\\2-mongodb中的所有格网\\";
        findGridFromMongo(storepath+"格网编码.txt");*/

        /*String sourcepath="D:\\1_paper\\Investment model\\1-有效栅格\\201507\\";
        String storepath="D:\\1_paper\\Investment model\\3-栅格的主要户型\\201507\\";
        DBCollection coll= db.getDB("paper").getCollection("GridData_Resold_gd");
        for(int i=2;i<=21;i++){
            findHouseType(sourcepath+"等值线_"+i+".txt",storepath+"等值线_"+i+".txt",coll,"2015","07");
        }*/


        /*String[] dates={"201511","201512","201601","201602","201603","201604",
                "201605","201606","201607","201608","201609","201610","201611"};
        for(int m=0;m<dates.length;m++){
            String date=dates[m];
            for(int i=1;i<=21;i++){
                try{
                    String filename="等值线_"+i+".txt";
                    blockHouseType("D:\\1_paper\\Investment model\\3-栅格的主要户型\\"+date+"\\"+filename,
                            "D:\\1_paper\\Investment model\\4-确定栅格唯一户型\\"+date+"\\"+filename);
                }catch (NullPointerException e){
                    e.getStackTrace();
                }
            }
        }*/


        /*String[] dates={"201507","201508","201509","201510","201511","201512","201601","201602","201603","201604",
                "201605","201606","201607","201608","201609","201610","201611"};
        for(int m=6;m<dates.length;m++) {
            String date = dates[m];
            System.out.println(date);
            for (int i = 1; i <= 21; i++) {
                try {
                    System.out.println(i+":");
                    houseType_Coordinate(i,
                            "D:\\1_paper\\Investment model\\4-确定栅格唯一户型\\"+date+"\\等值线_"+i+"_最大比率户型.txt",
                            "D:\\1_paper\\relative\\"+date+"\\坐标串_"+i+".txt",
                            "D:\\1_paper\\Investment model\\5-找到每个户型区块对应的坐标串\\"+date+".txt");

                } catch (NullPointerException e) {
                    e.getStackTrace();
                }
            }
        }*/

        /*String[] dates={"201507","201508","201509","201510","201511","201512","201601","201602","201603","201604",
                "201605","201606","201607","201608","201609","201610","201611"};
        for(int m=1;m<dates.length;m++) {
            String date = dates[m];
            System.out.println(date);
            try {
                determine_Block_value("D:\\1_paper\\Investment model\\5-找到每个户型区块对应的坐标串\\"+date+".txt",
                        "D:\\1_paper\\Investment model\\6-精简统计数据\\"+date+".txt");
            } catch (NullPointerException e) {
                e.getStackTrace();
            }
        }*/

        /*String[] dates={"201507","201508","201509","201510","201511","201512","201601","201602","201603","201604",
                "201605","201606","201607","201608","201609","201610","201611"};
        for(int m=0;m<dates.length;m++) {
            String date = dates[m];
            System.out.println(date);
            try {
                classifyByPrice("D:\\1_paper\\Investment model\\6-精简统计数据\\"+date+".txt",
                        "D:\\1_paper\\Investment model\\7-按照总价区间分类\\"+date+"\\");
            } catch (NullPointerException e) {
                e.getStackTrace();
            }
        }*/


        /*String[] dates={"201507","201508","201509","201510","201511","201512","201601","201602","201603","201604",
                "201605","201606","201607","201608","201609","201610","201611"};
        for(int m=0;m<dates.length;m++) {
            String date = dates[m];
            System.out.println(date);
            try {
                classifyByHouseType("D:\\1_paper\\Investment model\\6-精简统计数据\\"+date+".txt",
                        "D:\\1_paper\\Investment model\\6-精简统计数据\\"+date+"_户型统计.txt",
                        "D:\\1_paper\\Investment model\\8-按照户型类型分类\\"+date+"\\");
            } catch (NullPointerException e) {
                e.getStackTrace();
            }
        }*/


        String[] dates={"201507","201508","201509","201510","201511","201512","201601","201602","201603","201604",
                "201605","201606","201607","201608","201609","201610","201611"};
        String[] values={"50","100","200","300","400","500","600","700","800","900","1000","1500"};
        for(int m=0;m<dates.length;m++) {
            String date = dates[m];
            System.out.println(date);
            try {
                for(int n=0;n<values.length;n++){
                    staticFile("");
                }
            } catch (NullPointerException e) {
                e.getStackTrace();
            }
        }


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
            JSONObject result=new JSONObject();
            Map<String,Integer> map=new HashMap<>();
            JSONObject obj=new JSONObject();

            Map<String,JSONObject> code_json=new HashMap<>();
            Map<String,JSONArray> ht_codes=new HashMap<>();
            if(array.size()!=0){
                for(int j=0;j<array.size();j++){
                    obj=array.getJSONObject(j);
                    Iterator<String> keys=obj.keys();
                    String code;
                    while (keys.hasNext()){
                        code=keys.next();
                        JSONObject  o=obj.getJSONObject(code);
                        code_json.put(code,o);
                        String houseType=o.getString("houseType");

                        if(map.containsKey(houseType)){
                            int num=map.get(houseType);
                            map.put(houseType,++num);
                        }else {
                            map.put(houseType,1);
                        }

                        if(ht_codes.containsKey(houseType)){
                            JSONArray array1=ht_codes.get(houseType);
                            array1.add(code);
                            ht_codes.put(houseType,array1);
                        }else {
                            JSONArray array1=new JSONArray();
                            array1.add(code);
                            ht_codes.put(houseType,array1);
                        }
                    }
                }

                List<Integer> list=new ArrayList<>();
                for(Map.Entry<String,Integer> entry:map.entrySet()){
                    String ht=entry.getKey();
                    int num=entry.getValue();
                    result.put(ht,num);
                    list.add(num);
                }

                int max=Collections.max(list);
                List<Double> totalprice=new ArrayList<>();
                Map<Double,String> price_code=new HashMap<>();
                for(Map.Entry<String,Integer> entry:map.entrySet()){
                    String ht=entry.getKey();
                    int num=entry.getValue();
                    if(num==max){
                        JSONArray array1=ht_codes.get(ht);
                        List<Double> blockprice=new ArrayList<>();
                        for(int j=0;j<array1.size();j++){
                            String code=array1.getString(j);
                            JSONObject o=code_json.get(code);
                            JSONObject p=o.getJSONObject("price");
                            double price=p.getDouble("average");
                            price_code.put(price,code);
                            blockprice.add(price);
                        }
                        double min=Collections.min(blockprice);
                        totalprice.add(min);
                    }
                }
                //相同权重的几个户型中，总价最低者为投资门槛
                double minprice=Collections.min(totalprice);
                String indexcode=price_code.get(minprice);
                JSONObject indexjson=code_json.get(indexcode);

                FileTool.Dump(indexjson.toString(),storefile.replace(".txt","_最大比率户型.txt"),"utf-8");
            }else {
                FileTool.Dump("无",storefile.replace(".txt","_最大比率户型.txt"),"utf-8");
            }
            FileTool.Dump(result.toString(),storefile,"utf-8");
        }
    }

    //6.找出每种户型对应的坐标串
    //由于坐标串的等值线将15万以后的值都叠加到一起了，所以在文件夹【4-确定栅格唯一户型】也将16及其
    //以后的坐标串全部加载到等值线15之后再将其删除。
    public static void houseType_Coordinate(int blockValue,String houseType,String coordinate,String storefile){
        Vector<String> hts=FileTool.Load(houseType,"utf-8");
        Vector<String> coors=FileTool.Load(coordinate,"utf-8");

        if(hts.size()==coors.size()){
            for(int i=0;i<hts.size();i++){
                String ht=hts.elementAt(i);
                String coor=coors.elementAt(i);
                if(ht.equals("无")){

                }else{
                    JSONObject result=new JSONObject();
                    result.put("blockVaule",blockValue);
                    result.put("ht",ht);
                    result.put("coor",coor);
                    FileTool.Dump(result.toString(),storefile,"utf-8");
                }
            }
        }else {
            System.out.println("数量不一致！");
        }
    }

    //7.确定每个区块的户型和总价
    public static void determine_Block_value(String sourcefile,String storefile){
        Vector<String> pois=FileTool.Load(sourcefile,"utf-8");

        Map<String,Integer> ht_nums=new HashMap<>();
        Map<String,Integer> pr_nums=new HashMap<>();
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);

            JSONObject result=new JSONObject();
            int blockVaule=obj.getInt("blockVaule");
            JSONObject ht=obj.getJSONObject("ht");
            JSONObject Area=ht.getJSONObject("area");
            double area=Area.getDouble("average");
            JSONObject Price=ht.getJSONObject("price");
            double price=Price.getDouble("average");
            JSONObject Unitprice=ht.getJSONObject("unitprice");
            double unitprice=Unitprice.getDouble("average");
            String houseType=ht.getString("houseType");
            if(houseType.contains("厨")){
                int index=houseType.indexOf("厨");
                houseType=houseType.substring(0,index-1)+houseType.substring(index+1);
            }
            JSONObject coor=obj.getJSONObject("coor");
            String range=priceRange(price);

            result.put("range",range);
            result.put("blockVaule",blockVaule);
            result.put("price",price);
            result.put("area",area);
            result.put("unitprice",unitprice);
            result.put("houseType",houseType);
            result.put("coor",coor);

            if(ht_nums.containsKey(houseType)){
                int num=ht_nums.get(houseType);
                ht_nums.put(houseType,++num);
            }else {
                ht_nums.put(houseType,1);
            }

            if(pr_nums.containsKey(range)){
                int num=pr_nums.get(range);
                pr_nums.put(range,++num);
            }else {
                pr_nums.put(range,1);
            }

            FileTool.Dump(result.toString(),storefile,"utf-8");
        }

        List<JSONObject> ht_list=new ArrayList<>();
        for(Map.Entry<String,Integer> entry:ht_nums.entrySet()){
            String key=entry.getKey();
            int value=entry.getValue();
            JSONObject o=new JSONObject();
            o.put("ht",key);
            o.put("num",value);
            ht_list.add(o);
        }
        List<JSONObject> pr_list=new ArrayList<>();
        for(Map.Entry<String,Integer> entry:pr_nums.entrySet()){
            String key=entry.getKey();
            int value=entry.getValue();
            JSONObject o=new JSONObject();
            o.put("pr",key);
            o.put("num",value);
            pr_list.add(o);
        }

        Collections.sort(ht_list, new JsonListCompare.numComparator());
        Collections.sort(pr_list, new JsonListCompare.numComparator());

        for(int i=0;i<ht_list.size();i++){
            FileTool.Dump(ht_list.get(i).toString(),storefile.replace(".txt","_户型统计.txt"),"utf-8");
        }
        for(int i=0;i<pr_list.size();i++){
            FileTool.Dump(pr_list.get(i).toString(),storefile.replace(".txt","_价格统计.txt"),"utf-8");
        }
    }
    public static String priceRange(double price){
        String range="";
        if(price<=100){
            range="50";
        }else if(100<price&&price<=200){
            range="100";
        }else if(200<price&&price<=300){
            range="200";
        }else if(300<price&&price<=400){
            range="300";
        }else if(400<price&&price<=500){
            range="400";
        }else if(500<price&&price<=600){
            range="500";
        }else if(600<price&&price<=700){
            range="600";
        }else if(700<price&&price<=800){
            range="700";
        }else if(800<price&&price<=900){
            range="800";
        }else if(900<price&&price<=1000){
            range="900";
        }else if(1000<price&&price<=1500){
            range="1000";
        }else if(1500<price){
            range="1500";
        }
        return range;
    }

    //8.按照总价区间对区块进行分类
    public static void classifyByPrice(String sourcefile,String storefile){
        Vector<String> pois=FileTool.Load(sourcefile,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            String range=obj.getString("range");

            if(range.equals("50")){
                FileTool.Dump(obj.toString(),storefile+"50.txt","utf-8");
            }else if(range.equals("100")){
                FileTool.Dump(obj.toString(),storefile+"100.txt","utf-8");
            }else if(range.equals("200")){
                FileTool.Dump(obj.toString(),storefile+"200.txt","utf-8");
            }else if(range.equals("300")){
                FileTool.Dump(obj.toString(),storefile+"300.txt","utf-8");
            }else if(range.equals("400")){
                FileTool.Dump(obj.toString(),storefile+"400.txt","utf-8");
            }else if(range.equals("500")){
                FileTool.Dump(obj.toString(),storefile+"500.txt","utf-8");
            }else if(range.equals("600")){
                FileTool.Dump(obj.toString(),storefile+"600.txt","utf-8");
            }else if(range.equals("700")){
                FileTool.Dump(obj.toString(),storefile+"700.txt","utf-8");
            }else if(range.equals("800")){
                FileTool.Dump(obj.toString(),storefile+"800.txt","utf-8");
            }else if(range.equals("900")){
                FileTool.Dump(obj.toString(),storefile+"900.txt","utf-8");
            }else if(range.equals("1000")){
                FileTool.Dump(obj.toString(),storefile+"1000.txt","utf-8");
            }else if(range.equals("1500")){
                FileTool.Dump(obj.toString(),storefile+"1500.txt","utf-8");
            }
        }
    }

    //9.按照户型种类进行分类
    public static void classifyByHouseType(String sourcefile,String huxing,String storefile){
        Vector<String> hxs=FileTool.Load(huxing,"utf-8");
        Map<String,String> hx_map=new HashMap<>();
        for(int i=0;i<11;i++){
            String hx=hxs.elementAt(i);
            JSONObject obj=JSONObject.fromObject(hx);
            String ht=obj.getString("ht");
            hx_map.put(ht,"");
        }

        Vector<String> pois=FileTool.Load(sourcefile,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            String houseType=obj.getString("houseType");
            if(hx_map.containsKey(houseType)){
                FileTool.Dump(poi,storefile+houseType+".txt","utf-8");
            }else {
                FileTool.Dump(poi,storefile+"其他户型.txt","utf-8");
            }
        }
    }

    //10.生成静态文件
    public static void staticFile(String file){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        JSONArray array=new JSONArray();
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            array.add(poi);
        }
        FileTool.Dump(array.toString(),file.replace(".txt",""),"utf-8");
    }
}
