package com.svail.InvestmentEvolution;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.grid50.util.NumJudge;
import com.svail.grid50.util.RowColCalculation;
import com.svail.grid50.util.db;
import com.svail.util.FileTool;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.util.*;

/**
 * Created by ZhouXiang on 2017/5/8.
 * 将点数据转化成格网数据，在转化的过程中要
 * 计算同一个格网中数据的方差，判断是否该格网的数据是稳定的
 */
public class Poi2Grid_4 {

    //注意，统计不同月份的数据的时候，全局变量要清空！！！
    public static Map<Integer,Map<String,Integer>> code_houseType_map=new HashMap<>();
    public static Map<Integer,Map<String,Integer>> code_direction_map=new HashMap<>();
    public static Map<Integer,Map<String,Integer>> code_floors_map=new HashMap<>();
    public static Map<Integer,Map<String,Integer>> code_area_map=new HashMap<>();
    public static Map<Integer,Map<String,Integer>> code_price_map=new HashMap<>();
    public static Map<Integer,Map<String,Integer>> code_unitprice_map=new HashMap<>();
    public static Map<Integer,Map<String,Integer>> code_flooron_map=new HashMap<>();
    public static Map<Integer,Map<String,List<JSONObject>>> code_pois=new HashMap<>();
    public static TreeSet<Integer> codesSet= new TreeSet<>();
    public static JSONObject total=new JSONObject();

    public static void main(String[] args){

        long startTime = System.currentTimeMillis();    //获取开始时间

        String path="D:\\1_基于房产调控政策下的房产投资市场格局演变分析——以北京为例\\格网数据\\二手房\\";


        for(int i=4;i<=12;i++){
            initial(2016,i,path+"GridData_Resold_gd2016.txt");
        }
        for(int i=1;i<=5;i++){
            initial(2017,i,path+"GridData_Resold_gd2017.txt");
        }

        long endTime = System.currentTimeMillis();    //获取结束时间
        System.out.println("程序运行时间：" + ((endTime - startTime)/(double)(1000*60)) + "分钟");    //输出程序运行时间


    }
    public static void initial(int year,int month,String file){


        JSONObject condition=new JSONObject();
        condition.put("N",1);

        condition.put("year",year);
        condition.put("month",month);
        System.out.println(year+"年"+month+"月:");

        condition.put("export_collName","BasicData_Resold_gd_plus_rmError");
        condition.put("import_collName","GridData_Resold");

        code_houseType_map=new HashMap<>();
        code_direction_map=new HashMap<>();
        code_floors_map=new HashMap<>();
        code_area_map=new HashMap<>();
        code_price_map=new HashMap<>();
        code_unitprice_map=new HashMap<>();
        code_flooron_map=new HashMap<>();
        code_pois=new HashMap<>();
        codesSet= new TreeSet<>();
        total=new JSONObject();

        callDataFromMongo(condition);

        //statisticCode();

        ergodicStatistics(condition,file);

        System.out.println("ok!");
    }

    //1：将每个格网的数据（obj）存储在codelists_map中，其中key是格网code，value是装了所有房源数据的list
    public static void callDataFromMongo(JSONObject condition) {

        String collName_export = condition.getString("export_collName");
        DBCollection coll_export = db.getDB("InvestmentEvolution").getCollection(collName_export);


        BasicDBObject document = new BasicDBObject();
        Iterator<String> it = condition.keys();
        while (it.hasNext()) {
            String key = it.next();
            String value = condition.getString(key);
            if (key.equals("year") || key.equals("month")) {
                document.put(key, Integer.parseInt(value));
            }
        }
        String year=condition.getString("year");
        String month = condition.getString("month");

        DBCursor cursor = coll_export.find(document);
        System.out.println(document);

        String poi;
        JSONObject obj;
        JSONObject o;
        int code;

        String house_type;
        String area;
        String price;
        String unit_price;


        int count = 0;
        if (cursor.hasNext()) {
            while (cursor.hasNext()) {
                BasicDBObject cs = (BasicDBObject) cursor.next();
                poi = cs.toString();


                //System.out.println(o);
                try{
                    o = JSONObject.fromObject(poi);
                    obj=new JSONObject();
                    obj.put("price",o.getDouble("price"));
                    obj.put("area",o.getDouble("area"));
                    obj.put("unitprice",o.getDouble("unitprice"));
                    if (o.containsKey("house_type")) {
                        obj.put("house_type",o.getString("house_type"));
                    }
                    obj.put("code",o.getInt("code"));


                    code = obj.getInt("code");
                    codesSet.add(code);

                    if (obj.containsKey("house_type")) {
                        house_type = obj.getString("house_type");
                        setAttributeMap(code, house_type, code_houseType_map);

                        //以code为key建立一个poi的索引表
                        //Map<Integer,Map<String,List<JSONObject>>> code_pois

                        if ((year.endsWith("2017")&&month.equals("10"))) {//


                        } else {
                            //当月份为12时，这一部分代码暂时不用了，因为12月份的数据太多，导致内存总是溢出要寻求新的办法
                            if (code_pois.containsKey(code)) {
                                Map<String, List<JSONObject>> hy_pois = code_pois.get(code);

                                if (hy_pois.containsKey(house_type)) {

                                    List<JSONObject> pois = hy_pois.get(house_type);
                                    pois.add(obj);
                                    hy_pois.put(house_type, pois);
                                    code_pois.put(code, hy_pois);

                                } else {

                                    List<JSONObject> pois = new ArrayList<>();
                                    pois.add(obj);
                                    hy_pois.put(house_type, pois);
                                    code_pois.put(code, hy_pois);
                                }

                            } else {
                                Map<String, List<JSONObject>> hy_pois = new HashMap<>();
                                List<JSONObject> pois = new ArrayList<>();
                                pois.add(obj);
                                hy_pois.put(house_type, pois);
                                code_pois.put(code, hy_pois);
                            }
                        }

                        if (obj.containsKey("area")) {
                            area = obj.getString("area");
                            boolean num = NumJudge.isNum(area);
                            if (num) {
                                setAttributeMap(code, area, code_area_map);
                            } else {
                                System.out.println(code + ":" + area);
                            }

                        }

                        if (obj.containsKey("price")) {
                            price = obj.getString("price");
                            boolean num = NumJudge.isNum(price);
                            if (num) {
                                setAttributeMap(code, price, code_price_map);
                            } else {
                                System.out.println(code + ":" + price);
                            }
                        }

                        if (obj.containsKey("unitprice")) {
                            unit_price = obj.getString("unitprice");
                            boolean num = NumJudge.isNum(unit_price);
                            if (num) {
                                setAttributeMap(code, unit_price, code_unitprice_map);
                            } else {
                                System.out.println(code + ":" + unit_price);
                            }
                        }
                        ++count;
                    }else {
                        System.out.println("没有户型数据");
                    }
                }catch (JSONException e){
                    System.out.println(poi);
                    e.getStackTrace();
                }
            }
            System.out.println("共有" + count + "条数据");
        }
    }

    //2:遍历整个code_houseType_map，计算每个网格里边的户型的个数，并生成json格式数据
    public static void statisticCode(){
        stasticAttributeNum(code_houseType_map);
        stasticAttributeNum(code_direction_map);
        stasticAttributeNum(code_floors_map);
        stasticAttributeNum(code_flooron_map);
        stasticAttributeNum(code_area_map);
        stasticAttributeNum(code_price_map);
        stasticAttributeNum(code_unitprice_map);
    }

    //3、遍历所有网格，汇总每一个网格的统计信息
    public static void ergodicStatistics(JSONObject condition,String file) {

        JSONObject obj;
        double weight_area = 0;
        double weight_price = 0;
        double weight_unitprice = 0;
        int row;
        int col;
        int[] rc = new int[2];
        int code;
        Object[] codeslist = codesSet.toArray();


        String year = condition.getString("year");
        String month = condition.getString("month");

        String collName_import = condition.getString("import_collName");
        String collName_export=condition.getString("export_collName");
        DBCollection coll_import = db.getDB("InvestmentEvolution").getCollection(collName_import);
        BasicDBObject document;
        int index = 0;
        int documentcount = 0;
        for (int i = 0; i < codeslist.length; i++) {

            code = (int) codeslist[i];
            document = new BasicDBObject();
            if (code_houseType_map.containsKey(code)) {
                index++;
                Map<String, Integer> houseType = code_houseType_map.get(code);
                String type = getAttributeJson(houseType);
                document.put("houseType_statistics", type);
            }

            if (code_area_map.containsKey(code)) {
                index++;
                Map<String, Integer> area = code_area_map.get(code);
                String ar = getAttributeJson(area);

                weight_area = getInvestmentThreshold(area);
                document.put("area_statistics", ar);

            }

            if (code_price_map.containsKey(code)) {
                index++;
                Map<String, Integer> price = code_price_map.get(code);
                String pr = getAttributeJson(price);

                weight_price = getInvestmentThreshold(price);
                document.put("price_statistics", pr);
            }

            if (code_unitprice_map.containsKey(code)) {
                index++;
                Map<String, Integer> unitprice = code_unitprice_map.get(code);
                String up = getAttributeJson(unitprice);

                weight_unitprice = getInvestmentThreshold(unitprice);
                document.put("unitprice_statistics", up);
            }

            //在这里加一个ratio的key，该ley里面存放的value能够确定主打户型的基本情况（面积、朝向、楼层等）
            //以code为key建立一个poi的索引表:Map<Integer,Map<String,List<JSONObject>>> code_pois
            Map<String, Map<String, Integer>> hy_area_map = new HashMap<>();
            Map<String, Map<String, Integer>> hy_price_map = new HashMap<>();
            Map<String, Map<String, Integer>> hy_unitprice_map = new HashMap<>();

            int count = 0;
            Map<Integer, Map<String, List<JSONObject>>> codepois = new HashMap<>();

            if ((year.endsWith("2017")&&month.equals("10"))) {//

                codepois = setCode_pois(year, month, code,collName_export);
            } else {
                codepois = code_pois;
            }

            if (codepois.containsKey(code)) {

                Map<String, List<JSONObject>> hy_pois = codepois.get(code);
                //统计每个户型所占的比率
                JSONObject hy_ratio = new JSONObject();

                int total_size = 0;

                for (Map.Entry<String, List<JSONObject>> entry : hy_pois.entrySet()) {
                    List<JSONObject> pois = entry.getValue();
                    int size = pois.size();
                    total_size += size;
                }


                for (Map.Entry<String, List<JSONObject>> entry : hy_pois.entrySet()) {
                    String houseType = entry.getKey();
                    List<JSONObject> pois = entry.getValue();
                    int size = pois.size();
                    double ratio = (double) size / (double) total_size;
                    hy_ratio.put(houseType, ratio);
                }

                //先统计每一个户型都有那些价格、面积特征
                for (Map.Entry<String, List<JSONObject>> entry : hy_pois.entrySet()) {
                    String houseType = entry.getKey();
                    List<JSONObject> pois = entry.getValue();

                    for (int m = 0; m < pois.size(); m++) {
                        JSONObject poi = pois.get(m);

                        String area;
                        String price;
                        String unit_price;

                        if (poi.containsKey("area")) {
                            area = poi.getString("area");
                            boolean num = NumJudge.isNum(area);
                            if (num) {
                                setAttributeMap(houseType, area, hy_area_map);
                            } else {
                                System.out.println(code + ":" + area);
                            }

                        }

                        if (poi.containsKey("price")) {
                            price = poi.getString("price");
                            boolean num = NumJudge.isNum(price);
                            if (num) {
                                setAttributeMap(houseType, price, hy_price_map);
                            } else {
                                System.out.println(code + ":" + price);
                            }
                        }

                        if (poi.containsKey("unit_price")) {
                            unit_price = poi.getString("unit_price");
                            boolean num = NumJudge.isNum(unit_price);
                            if (num) {
                                setAttributeMap(houseType, unit_price, hy_unitprice_map);
                            } else {
                                System.out.println(code + ":" + unit_price);
                            }
                        }
                    }

                }

                //对每一个户型的价格、面积特征进行汇总,并且以计算加权值
                JSONObject hy_obj = new JSONObject();
                for (Map.Entry<String, List<JSONObject>> entry : hy_pois.entrySet()) {
                    String houseType = entry.getKey();
                    JSONObject ht = new JSONObject();

                    if (hy_area_map.containsKey(houseType)) {
                        Map<String, Integer> area_map = hy_area_map.get(houseType);
                        getweightAttributeJson(area_map, "area", ht);
                    }

                    if (hy_price_map.containsKey(houseType)) {
                        Map<String, Integer> price_map = hy_price_map.get(houseType);
                        getweightAttributeJson(price_map, "price", ht);
                    }

                    if (hy_unitprice_map.containsKey(houseType)) {
                        Map<String, Integer> unitprice_map = hy_unitprice_map.get(houseType);
                        getweightAttributeJson(unitprice_map, "unitprice", ht);
                    }

                    double ratio = hy_ratio.getDouble(houseType);
                    ht.put("ratio", ratio);

                    //System.out.println(ht);
                    hy_obj.put(houseType, ht);
                }
                document.put("type", hy_obj);

            } else {
                count++;
            }
            //System.out.println(document);


            if (index != 0) {
                document.put("code", code);
                rc = RowColCalculation.Code_RowCol(code, 1);
                row = rc[0];
                col = rc[1];
                document.put("row", row);
                document.put("col", col);
                document.put("price_weightPrice", weight_price);
                document.put("price_weightUnitprice", weight_area * weight_unitprice);
                document.put("unitprice_weightUnitprice", weight_unitprice);
                if(weight_unitprice==0){
                    System.out.println("weight_unitprice为0："+code);
                }

                Iterator<String> it = condition.keys();
                while (it.hasNext()) {
                    String key = it.next();
                    String value = condition.getString(key);
                    if (key.equals("year") || key.equals("month")) {
                        document.put(key, value);
                    }
                }

                DBCursor rls = coll_import.find(document);

                if (rls == null || rls.size() == 0) {
                    documentcount++;
                    //System.out.println(document);
                    FileTool.Dump(document.toString(), file, "utf-8");
                    try {
                        coll_import.insert(document);
                    } catch (IllegalArgumentException e) {
                        //coll_import.insert(document);
                    }

                } else {
                    System.out.println("该数据已经存在!");
                }
            }
            //}

        }
        System.out.println("一共导入" + documentcount + "条数据");
    }

    //建立一个map，其中key为code，value是一个属性值——个数的一个子map
    public static void setAttributeMap(int code,String attribute,Map<Integer,Map<String,Integer>> map){
        if(map.containsKey(code)){

            Map<String,Integer> num_map=map.get(code);
            if(num_map.containsKey(attribute)){
                int num=num_map.get(attribute);
                num_map.put(attribute,++num);
                map.put(code,num_map);

            }else {
                num_map.put(attribute,1);
                map.put(code,num_map);
            }

        }else {
            Map<String,Integer> num_map=new HashMap<>();
            num_map.put(attribute,1);
            map.put(code,num_map);
        }
    }
    public static void setAttributeMap(String hy,String attribute,Map<String,Map<String,Integer>> map){
        if(map.containsKey(hy)){

            Map<String,Integer> num_map=map.get(hy);
            if(num_map.containsKey(attribute)){
                int num=num_map.get(attribute);
                num_map.put(attribute,++num);
                map.put(hy,num_map);

            }else {
                num_map.put(attribute,1);
                map.put(hy,num_map);
            }

        }else {
            Map<String,Integer> num_map=new HashMap<>();
            num_map.put(attribute,1);
            map.put(hy,num_map);
        }

    }

    //验证所有的统计结果是否与总的数据的相符
    public static void stasticAttributeNum(Map<Integer,Map<String,Integer>> map){
        int code;
        String attribute="";
        int num;
        Map<String,Integer> attribute_num;

        int count=0;
        for(Map.Entry<Integer,Map<String,Integer>> entry:map.entrySet()){
            code=entry.getKey();
            attribute_num=entry.getValue();

            JSONObject obj=new JSONObject();
            obj.put("code",code);
            for(Map.Entry<String,Integer> entry1:attribute_num.entrySet()){
                attribute=entry1.getKey();
                num=entry1.getValue();
                obj.put(attribute,num);
                count+=num;
            }
            // System.out.println(obj);
        }
        System.out.println("共有"+count+"条"+attribute+"信息");
    }

    //遍历code下的子map，并将所有的值以json形式返回,这里统计的是每个属性所含有的个数
    public static String getAttributeJson(Map<String,Integer> attribute){

        String attr;
        int num;
        String obj="";
        JSONObject object=new JSONObject();
        for(Map.Entry<String,Integer> entry:attribute.entrySet()){
            attr=entry.getKey();
            num=entry.getValue();

            //object.put(attr,num);
            obj+=attr+","+num+";";
        }
        return obj;
    }
    //遍历每个户型下的子map，并且将子map的值进行加权，得到该户型的面积，价格和均价的加权值
    public static void getweightAttributeJson (Map<String,Integer> attribute,String key,JSONObject ht){
        JSONObject obj=new JSONObject();
        String attr;
        int num;

        int totalnum=0;
        for(Map.Entry<String,Integer> entry:attribute.entrySet()){

            num=entry.getValue();
            totalnum+=num;
        }

        double result=0;
        for(Map.Entry<String,Integer> entry:attribute.entrySet()){
            attr=entry.getKey();
            double data=Double.parseDouble(attr);
            num=entry.getValue();

            String str_data=""+data;
            if(obj.containsKey(str_data)){
                int count=obj.getInt(str_data);
                count+=num;
                obj.put(str_data,count);
            }else {
                obj.put(str_data,num);
            }

            result+=data*((double)num/totalnum);
        }

        JSONObject R=new JSONObject();
        R.put("average",result);

        Iterator<String> it=obj.keySet().iterator();
        JSONArray array=new JSONArray();
        while (it.hasNext()){
            String k=it.next();
            int v=obj.getInt(k);

            JSONObject o=new JSONObject();
            o.put("vaule",k);
            o.put("amount",v);
            array.add(o);
        }
        R.put("data",array);
        ht.put(key,R);
    }

    public static double getInvestmentThreshold(Map<String,Integer> attribute){

        String attr;
        int num;
        int totalnum=0;
        double ratio=0;
        double weightresult=0;
        for(Map.Entry<String,Integer> entry:attribute.entrySet()){
            num=entry.getValue();
            totalnum+=num;
        }

        for(Map.Entry<String,Integer> entry:attribute.entrySet()){
            attr=entry.getKey();
            num=entry.getValue();

            if(totalnum!=0){
                ratio=(double)num/(double)totalnum;
            }

            weightresult+=ratio*Double.parseDouble(attr);
        }
        return weightresult;
    }

    //单独建立一个函数setCode_pois来设置每个格网的数据
    public static Map<Integer,Map<String,List<JSONObject>>> setCode_pois(String year,String month,int code,String coll){
        DBCollection coll_Basic50 = db.getDB("paper").getCollection(coll);

        Map<Integer,Map<String,List<JSONObject>>> code_pois=new HashMap<>();

        BasicDBObject document = new BasicDBObject();
        document.put("year",year);
        document.put("month",month);
        document.put("code",""+code);



        DBCursor cursor = coll_Basic50.find(document);
        String poi;
        JSONObject obj;
        String house_type;
        if(cursor.hasNext()) {
            while (cursor.hasNext()) {
                BasicDBObject cs = (BasicDBObject)cursor.next();
                poi=cs.toString();
                obj= JSONObject.fromObject(poi);


                if(obj.containsKey("house_type")){
                    house_type=obj.getString("house_type");

                    //以code为key建立一个poi的索引表
                    //Map<Integer,Map<String,List<JSONObject>>> code_pois

                    if(code_pois.containsKey(code)){
                        Map<String,List<JSONObject>> hy_pois=code_pois.get(code);

                        if(hy_pois.containsKey(house_type)){

                            List<JSONObject> pois=hy_pois.get(house_type);
                            pois.add(obj);
                            hy_pois.put(house_type,pois);
                            code_pois.put(code,hy_pois);

                        }else{

                            List<JSONObject> pois=new ArrayList<>();
                            pois.add(obj);
                            hy_pois.put(house_type,pois);
                            code_pois.put(code,hy_pois);
                        }

                    }else{
                        Map<String,List<JSONObject>> hy_pois=new HashMap<>();
                        List<JSONObject> pois=new ArrayList<>();
                        pois.add(obj);
                        hy_pois.put(house_type,pois);
                        code_pois.put(code,hy_pois);

                    }
                }
            }
        }

        return code_pois;
    }



}
