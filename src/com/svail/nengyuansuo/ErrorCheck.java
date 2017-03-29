package com.svail.nengyuansuo;

import com.mongodb.util.JSON;
import com.svail.util.FileTool;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.poi.POIXMLException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * Created by ZhouXiang on 2017/3/27.
 * 1、检查每个省每个县的的主体功能是否唯一
 * 2、检查是否有县的主体功能被遗漏
 * 3、汇总2014年经济数据
 * 4、把各个省的主体功能数据读取出来
 * 5、把主体功能数据和经济数据匹配起来
 */
public class ErrorCheck {
    public static Map<String,Integer> adminMap=new HashMap<>();

    public static void main(String[] args){
        //mainFunctions();

        /*streamlLine("D:\\4_能源所\\【0324所有数据汇总】\\总结性成果表\\2015\\程序处理文件夹\\",
                "县级数据汇总-完全版.txt");*/

        String path="D:\\4_能源所\\【0324所有数据汇总】\\总结性成果表\\2015\\程序处理文件夹\\2014数据\\";
        dataAggregation(path+"地级市_json.txt",
                path+"县级市_json.txt",
                path+"县级数据汇总-完全版-精简版.txt",
                path+"全国主体功能数据汇总.txt");

    }

    //1、读取每一个统计表，把表里所有的县名存到map里，看是否有县还是有两个主体功能区将最后的结果写到文本里
    //2、将得到的文本与行政区划比较，检查是否有县或区被遗漏了
    public static void check(){
        Vector<String> admins=FileTool.Load("D:\\4_能源所\\【0324所有数据汇总】\\总结性成果表\\全国2015年行政区划代码.txt","utf-8");

        for(int i=0;i<admins.size();i++){
            String s=admins.elementAt(i);
            String[] array=s.split("　　");
            if(array.length>1){
                adminMap.put(array[1],0);
            }
        }

        String[] provinces={"01北京","02天津","03河北","04山西","05内蒙古","06辽宁",
                "07吉林","08黑龙江","09上海","10江苏","11浙江","12安徽",
                "13福建","14江西","15山东","16河南","17湖北","18湖南","19广东","20广西",
                "21海南","22重庆","23四川","24贵州","25云南",
                "26西藏","27陕西","28甘肃","29青海"};
        for(int i=0;i<29;i++){
            String province=provinces[i];
            System.out.println(province+":");
            countyMap("D:\\4_能源所\\【0324所有数据汇总】\\总结性成果表\\2010\\"+province+".xlsx");
        }

        for(Map.Entry<String,Integer>entry:adminMap.entrySet()){
            String key=entry.getKey();
            int value=entry.getValue();
            if(value==0)
                if(key.endsWith("区")){

                }else {
                    System.out.println(key);
                }
        }
    }
    public static void countyMap(String fileName){
        boolean isE2007 = false;    //判断是否是excel2007格式
        if(fileName.endsWith("xlsx"))
            isE2007 = true;
        try {
            InputStream input = new FileInputStream(fileName);  //建立输入流
            Workbook wb  = null;
            //根据文件格式(2003或者2007)来初始化
            if(isE2007)
                wb = new XSSFWorkbook(input);
            else
                wb = new HSSFWorkbook(input);

            int nums=wb.getNumberOfSheets();
            Map<String,String> quxian=new HashMap<>();
            for(int j=1;j<nums;j++){

                Sheet sheet = wb.getSheetAt(j);//获得第一个表单
                String name=sheet.getSheetName();
                //System.out.println("读取第"+j+"个表:"+name);

                Iterator<Row> rows = sheet.rowIterator(); //获得第一个表单的迭代器
                int row_count=0;
                Map<Integer,String> names=new HashMap<>();

                JSONObject obj=new JSONObject();
                Cell cell_value;
                while (rows.hasNext()) {

                    Row row = rows.next();  //获得行数据
                    row_count=row.getRowNum();//获得行号从0开始

                    if(row_count==0){
                        Iterator<Cell> cells = row.cellIterator();    //获得第一行的迭代器
                        int col_count=0;
                        while (cells.hasNext()) {
                            Cell cell = cells.next();
                            names.put(col_count,cell.getStringCellValue());
                            col_count++;
                        }
                    }else {
                        for(int i=0;i<names.size();i++){
                            cell_value=row.getCell(i);
                            String keyname=names.get(i);
                            if(cell_value!=null&&keyname.length()!=0){
                                obj.put(keyname,cell_value.toString());
                            }else {
                                obj.put(keyname,"");
                            }
                        }
                        //System.out.println(obj);
                        if(obj.containsKey("区县名称")){
                            String qx=obj.getString("区县名称").replace("","");

                            if(qx.length()!=0){

                                //记录每个县名出现的次数
                                if(adminMap.containsKey(qx)){
                                    int n=adminMap.get(qx);
                                    adminMap.put(qx,++n);
                                }


                                if(quxian.containsKey(qx)){
                                    System.out.println(qx+"出现了多次！");

                                }else {
                                    quxian.put(qx,"");
                                }
                                //FileTool.Dump(obj.toString().replace(" ",""),fileName.replace(".xlsx","").replace(".xls","")+"_"+name+"_"+"json.txt","utf-8");
                            }
                        }
                    }
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }catch (POIXMLException e){
            e.getStackTrace();
        }
    }


    // 3、汇总2014年县域经济数据
    public static void dataTogether(){
        String path="D:\\4_能源所\\【数据库数据源】\\中国县域统计年鉴2015（县市卷）\\";
        Vector<String> files=FileTool.Load(path+"filename.txt","utf-8");
        for(int i=0;i<files.size();i++){
            String file=files.elementAt(i);
            together(path+file, path+"全县数据汇总.txt");
        }
    }
    public static void together(String fileName,String huizong){

        boolean isE2007 = false;
        if(fileName.endsWith("xlsx"))
            isE2007 = true;
        try {
            InputStream input = new FileInputStream(fileName);
            Workbook wb  = null;

            if(isE2007)
                wb = new XSSFWorkbook(input);
            else
                wb = new HSSFWorkbook(input);

            Sheet sheet = wb.getSheetAt(0);//获得第一个表单
            String name=sheet.getSheetName();

            Iterator<Row> rows = sheet.rowIterator(); //获得第一个表单的迭代器
            int row_count=0;
            Map<Integer,String> names=new HashMap<>();
            Map<Integer,JSONObject> qx=new HashMap<>();


            Cell cell_value;

            String provinceinfo="";
            JSONObject obj=new JSONObject();

            //遍历每一行的数据，遍历完所有行，每个县的数据才能完备
            while (rows.hasNext()) {

                Row row = rows.next();  //获得行数据
                row_count=row.getRowNum();//获得行号从0开始

                if(row_count==0){
                    //安徽省2014年县(市)社会经济主要指标统计(八)
                    provinceinfo=row.getCell(0).toString();
                }

                if(row_count==1){
                    Iterator<Cell> cells = row.cellIterator();    //获得第二行的迭代器
                    int col_count=0;
                    while (cells.hasNext()) {
                        Cell cell = cells.next();
                        String quxian=cell.getStringCellValue();
                        //System.out.println(quxian);
                        names.put(col_count,quxian);//列号——区县名
                        JSONObject o=new JSONObject();
                        qx.put(col_count,o);//列号——区县的json
                        col_count++;
                    }
                }else if(row_count>1){
                    //第三个开始才是县名

                    String keyname=row.getCell(0).toString().replace("  ","");
                    for(int i=2;i<names.size();i++){
                        cell_value=row.getCell(i);
                        String value=cell_value.toString();

                        obj=qx.get(i);
                        if(cell_value!=null&&keyname.length()!=0){
                            obj.put(keyname,value);
                            //System.out.println(keyname+","+value);
                        }else {
                            obj.put(keyname,"");
                        }
                        qx.put(i,obj);
                    }
                }
            }

            for(int i=2;i<qx.size();i++){
                obj=qx.get(i);
                String quxianname=names.get(i);
                obj.put("区县名称",quxianname);
                obj.put("来源",provinceinfo);
                System.out.println(obj);
                FileTool.Dump(obj.toString().replace(" ",""),huizong,"utf-8");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }catch (POIXMLException e){
            e.getStackTrace();
        }
    }


    //4、把各个省的主体功能数据读取出来
    public static void mainFunctions(){
        String[] provinces={"01北京","02天津","03河北","04山西","05内蒙古","06辽宁",
                "07吉林","08黑龙江","09上海","10江苏","11浙江","12安徽",
                "13福建","14江西","15山东","16河南","17湖北","18湖南","19广东","20广西",
                "21海南","22重庆","23四川","24贵州","25云南",
                "26西藏","27陕西","28甘肃","29青海"};

        String path="D:\\4_能源所\\【0324所有数据汇总】\\总结性成果表\\2010\\";
        for(int i=0;i<29;i++){
            String province=provinces[i];
            System.out.println(province+":");
            functionTogether(path+province+".xlsx",path+"全国主体功能数据汇总.txt");
        }
    }
    public static void functionTogether(String fileName,String storefile){
        boolean isE2007 = false;    //判断是否是excel2007格式
        if(fileName.endsWith("xlsx"))
            isE2007 = true;
        JSONObject obj=new JSONObject();
        try {
            InputStream input = new FileInputStream(fileName);  //建立输入流
            Workbook wb  = null;
            //根据文件格式(2003或者2007)来初始化
            if(isE2007)
                wb = new XSSFWorkbook(input);
            else
                wb = new HSSFWorkbook(input);

            int nums=wb.getNumberOfSheets();

            for(int j=1;j<nums;j++){

                Sheet sheet = wb.getSheetAt(j);//获得第一个表单
                String name=sheet.getSheetName();
                //System.out.println("读取第"+j+"个表:"+name);

                Iterator<Row> rows = sheet.rowIterator(); //获得第一个表单的迭代器
                int row_count=0;
                Map<Integer,String> names=new HashMap<>();

                Cell cell_value;
                while (rows.hasNext()) {

                    Row row = rows.next();  //获得行数据
                    row_count=row.getRowNum();//获得行号从0开始

                    if(row_count==0){
                        Iterator<Cell> cells = row.cellIterator();    //获得第一行的迭代器
                        int col_count=0;
                        while (cells.hasNext()) {
                            Cell cell = cells.next();
                            names.put(col_count,cell.getStringCellValue());
                            col_count++;
                        }
                    }else {
                        obj=new JSONObject();
                        for(int i=0;i<names.size();i++){
                            cell_value=row.getCell(i);
                            String keyname=names.get(i);
                            if(keyname.contains("所属市")||keyname.contains("区县名称")||keyname.contains("主体功能区属性")||keyname.contains("行政区土地面积")){
                                if(cell_value!=null&&keyname.length()!=0){
                                    obj.put(keyname,cell_value.toString());
                                }
                            }
                        }
                        //System.out.println(obj);
                        String shuxing=obj.getString("所属市");
                        if(shuxing.length()!=0){
                            FileTool.Dump(obj.toString().replace(" ",""),storefile,"utf-8");
                        }
                    }
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }catch (POIXMLException e){
            e.getStackTrace();
        }catch (JSONException e){
            System.out.println(obj);
        }
    }


    //5、把主体功能数据和经济数据匹配起来
    //5-1 精简【县级数据汇总-完全版.txt】
    public static void streamlLine(String path,String file){
        Vector<String> files=FileTool.Load(path+file,"utf-8");
        for(int i=0;i<files.size();i++){
            String fl=files.elementAt(i);
            JSONObject obj=JSONObject.fromObject(fl);
            String laiyuan=obj.getString("来源");
            String ly="";
            double gdp=0;
            double yichan=0;
            double erchan=0;
            double renkou=0;
            double admin=0;
            try{
                ly=laiyuan.substring(0,laiyuan.indexOf("2014年"));
                admin=obj.getDouble("行政区域面积");
                gdp=obj.getDouble("地区生产总值");
                yichan=obj.getDouble("第一产业增加值");
                erchan=obj.getDouble("第二产业增加值");
                renkou=obj.getDouble("户籍人口");
            }catch (StringIndexOutOfBoundsException e){
                ly="未知";
                e.getStackTrace();
            }catch (JSONException e){
                e.getStackTrace();
            }

            JSONObject o=new JSONObject();
            o.put("省份",ly);
            o.put("区县名称",obj.getString("区县名称"));
            o.put("行政区域面积",admin);
            o.put("户籍人口",renkou);
            o.put("地区生产总值",gdp);
            o.put("第一产业",yichan);
            o.put("第二产业",erchan);
            o.put("第三产业",(gdp-erchan-yichan));
            FileTool.Dump(o.toString(),path+file.replace(".txt","-精简版.txt"),"utf-8");
        }
    }

    //5-2 将【地级市_json.txt】、【县级市_json.txt】、【县级数据汇总-完全版-精简版.txt】
    //全部汇总到【全国主体功能数据汇总.txt】中
    public static void dataAggregation(String dijishi,String xianjishi,String xian,String ztgn){
        Vector<String> djs=FileTool.Load(dijishi,"utf-8");
        Vector<String> xjs=FileTool.Load(xianjishi,"utf-8");
        Vector<String> xn=FileTool.Load(xian,"utf-8");
        Vector<String> zg=FileTool.Load(ztgn,"utf-8");

        Map<String,JSONObject> djs_map=new HashMap<>();
        Map<String,JSONObject> xjs_map=new HashMap<>();
        Map<String,JSONObject> xn_map=new HashMap<>();

        for(int i=0;i<djs.size();i++){
            String s=djs.elementAt(i);
            JSONObject obj=JSONObject.fromObject(s);
            String chengshi=obj.getString("城市")+"市辖区";
            djs_map.put(chengshi,obj);
        }

        for(int i=0;i<xjs.size();i++){
            String s=xjs.elementAt(i);
            JSONObject obj=JSONObject.fromObject(s);
            String chengshi=obj.getString("城市");
            xjs_map.put(chengshi,obj);
        }

        for(int i=0;i<xn.size();i++){
            String s=xn.elementAt(i);
            JSONObject obj=JSONObject.fromObject(s);
            String xianming=obj.getString("区县名称");
            xn_map.put(xianming,obj);
        }

        for(int i=0;i<zg.size();i++){
            String s=zg.elementAt(i);
            JSONObject obj=JSONObject.fromObject(s);
            String quxian=obj.getString("区县名称");

            if(djs_map.containsKey(quxian)){
                JSONObject o=djs_map.get(quxian);
                obj.put("行政区面积_2014",o.getString("行政区域土地面积"));
                obj.put("人口",o.getString("年末总人口"));
                obj.put("地区生产总值",o.getString("地区生产总值"));
                obj.put("第一产业",o.getString("第一产业"));
                obj.put("第二产业",o.getString("第二产业"));
                obj.put("第三产业",o.getString("第三产业"));
                obj.put("人均GDP",o.getString("人均GDP"));
            }else if(xjs_map.containsKey(quxian)){
                JSONObject o=xjs_map.get(quxian);
                obj.put("行政区面积_2014",o.getString("行政区域土地面积"));
                obj.put("人口",o.getString("年末总人口"));
                obj.put("地区生产总值",o.getString("地区生产总值"));
                obj.put("第一产业",o.getString("第一产业"));
                obj.put("第二产业",o.getString("第二产业"));
                obj.put("第三产业",o.getString("第三产业"));
                obj.put("人均GDP",o.getString("人均GDP"));
            }else if(xn_map.containsKey(quxian)){
                JSONObject o=xn_map.get(quxian);

                String area_xn=o.getString("行政区域面积").replace(" ","");
                String area_ztgn=obj.getString("行政区土地面积").replace(" ","");
                if(area_xn.length()!=0&&area_ztgn.length()!=0){
                    double area_xn_double=Double.parseDouble(area_xn);
                    double area_ztgn_double=Double.parseDouble(area_ztgn);
                    obj.put("行政区面积_2014",o.getString("行政区域面积"));
                    obj.put("人口",o.getString("户籍人口"));
                    obj.put("地区生产总值",o.getString("地区生产总值"));
                    obj.put("第一产业",o.getString("第一产业"));
                    obj.put("第二产业",o.getString("第二产业"));
                    obj.put("第三产业",o.getString("第三产业"));
                    obj.put("省份",o.getString("省份"));
                }


            }else {
                //System.out.println("找不到对应数据："+obj);
            }
            //FileTool.Dump(obj.toString(),ztgn.replace(".txt","-数据聚合.txt"),"utf-8");
        }
    }
}
