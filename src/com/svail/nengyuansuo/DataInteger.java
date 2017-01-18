package com.svail.nengyuansuo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.svail.grid50.util.db;
import com.svail.util.FileTool;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

import static com.svail.grid50.BatchProcess_1.batchProcess;
import static com.svail.util.Mongo_Delete.ReadExcel.readExcel;
import static com.svail.util.StrBsonTransfer.strToBson;

/**
 * Created by ZhouXiang on 2016/12/26.
 * 将excel数据整合到mongodb中:
 * 1、将数据解析成json
 * 2、将数据进行地理编码
 * 3、将数据进行入库
 * 4、黑龙江和湖南的数据没有参与处理
 *
 */
public class DataInteger {
    public static void main(String[] args) throws UnsupportedEncodingException {
        geoCode();
    }

    //1、将数据转换成json格式
    public static void toJson(String file){
        Vector<String> files= FileTool.Load(file,"utf-8");
        for(int i=0;i<files.size();i++){
            System.out.println(i+":"+files.elementAt(i));
            readExcel(files.elementAt(i));
        }
    }

    //2、将数据进行地理编码，但是总是出现为null的现象
    public static void geoCode() throws UnsupportedEncodingException {
        String[] province={"01北京","02天津","03河北","04山西","05内蒙古","06辽宁",
                      "07吉林","08黑龙江","09上海","10江苏","11浙江","12安徽",
                    "13福建","14江西","15山东","16河南","17湖北","18湖南","19广东","20广西",
                   "21海南","22重庆","23四川","24贵州","25云南",
                  "26西藏","27陕西","28甘肃","29青海","30宁夏","31新疆维吾尔族自治区"};
        String[] keys={"所属市","区县名称","城镇"};
        for(int j=30;j<31;j++){
            String path = "D:\\能源所\\【各省自治区直辖市主体功能区数据库】\\"+province[j]+"\\json\\filename.txt";
            Vector<String> names=FileTool.Load(path,"utf-8");
            for(int i=0;i<names.size();i++){
                String name=names.elementAt(i);
                System.out.println(name);
                batchProcess(100,name,keys,"新疆维吾尔自治区");
            }
        }
    }

    //3、将数据导入数据库
    public static void toMongo_main(){
        String[] province={"01北京","02天津","03河北","04山西","05内蒙古","06辽宁","07吉林","09上海","10江苏","11浙江","12安徽",
                "13福建","14江西","15山东","16河南","17湖北","19广东","20广西","21海南","22重庆","23四川","24贵州","25云南",
                "26西藏","27陕西","28甘肃","29青海","30宁夏","31新疆维吾尔族自治区"};
        String[] keys={"所属市","区县名称","城镇"};
        for(int j=0;j<province.length;j++){//province.length
            String path = "D:\\能源所\\【各省自治区直辖市主体功能区数据库】\\"+province[j]+"\\json\\filename.txt";
            Vector<String> names=FileTool.Load(path,"utf-8");
            for(int i=0;i<names.size();i++){
                String name=names.elementAt(i);
                if(name.contains("禁止开发")){

                }else{
                    toMongo(name,province[j]);
                }
            }
        }
    }
    public static void toMongo(String file,String province){
        DBCollection collection= db.getDB("nys").getCollection("mf");

        Vector<String> pois=FileTool.Load(file,"utf-8");

        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            BasicDBObject doc=strToBson(poi);
            doc.put("省",province);
            try{
                //System.out.println(doc);
                collection.insert(doc);
            }catch (StringIndexOutOfBoundsException e){
                System.out.println(doc);
            }
        }
    }
}
