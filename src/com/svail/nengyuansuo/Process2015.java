package com.svail.nengyuansuo;

import com.svail.util.FileTool;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static com.svail.nengyuansuo.CreatExcel.writeExcel;

/**
 * Created by ZhouXiang on 2017/1/3.
 * 利用各省2016年的统计年鉴，将各省的2015年的数据更新到各个主体功能区文件内
 *
 */
public class Process2015 {
    public static Map<String,JSONObject> map=new HashMap<>();
    public static void main(String[] args) throws IOException {
        String path="D:\\能源所\\中期汇报\\19广东——华南\\2015\\";
        String[] names={"国家级重点开发区域_json","国家级重点生态功能区_json",
                        "农产品主产区_json","生态镇in重点开发区域_json",
                        "省级重点开发区域_json","省级重点生态功能区_json",
                        "优化开发区_json"};
        makeMap(path+"经济数据_json.txt");

        for(int i=0;i<names.length;i++){
            dataFusion(path+names[i]+".txt");
        }
        for(int i=0;i<names.length;i++){
            toExcel(path,names[i]+"_2015.txt");
        }
    }
    //将2015年的经济数据存放到map中
    public static void makeMap(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");

        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            String quxian=obj.getString("区县名称");
            map.put(quxian,obj);
        }

    }

    //根据2015年的经济数据给每个区县的数据进行2015年数据的赋值
    public static void dataFusion(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");

        for(int i=0;i<pois.size();i++) {
            String poi = pois.elementAt(i);
            JSONObject obj = JSONObject.fromObject(poi);
            String quxian = obj.getString("区县名称");

            if(map.containsKey(quxian)){
                JSONObject result=new JSONObject();

                JSONObject value=map.get(quxian);
                if(obj.containsKey("所属市")){
                    result.put("所属市",obj.getString("所属市"));
                }else {
                    result.put("所属市","");
                }
                result.put("区县名称",quxian);
                result.put("主体功能区属性",obj.getString("主体功能区属性"));

                if(obj.containsKey("行政区域土地面积")){
                    result.put("行政区域土地面积",obj.getString("行政区域土地面积"));
                }else {
                    result.put("行政区域土地面积","");
                }

                result.put("年末常住人口",value.getString("年末常住人口"));
                result.put("地区生产总值",value.getString("地区生产总值"));
                result.put("人均GDP",value.getString("人均GDP"));
                result.put("第一产业",value.getString("第一产业"));
                result.put("第二产业",value.getString("第二产业"));
                result.put("第三产业",value.getString("第三产业"));

                FileTool.Dump(result.toString(),file.replace(".txt","_2015.txt"),"utf-8");

            }else {
                obj.remove("ID");
                obj.remove("年末常住人口");
                obj.remove("地区生产总值");
                obj.remove("人均GDP");
                obj.remove("第一产业");
                obj.remove("第二产业");
                obj.remove("第三产业");
                FileTool.Dump(obj.toString(),file.replace(".txt","_2015.txt"),"utf-8");
            }
        }
    }

    //将赋值后的数据转成excel格式
    public static void toExcel(String path,String filename) throws IOException {

        writeExcel(path+filename);
    }
}
