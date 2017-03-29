package com.svail.nengyuansuo;

import com.svail.util.FileTool;
import com.svail.util.Tool;
import net.sf.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by ZhouXiang on 2016/11/26.
 */
public class CreatExcel {
    public static void main(String[] args) throws IOException {

       /* String path="D:\\1126\\浙江省\\result\\objs\\infusion\\";

        String filename="省级重点生态功能区（限制开发区域）"+"_objs_所有结果.txt";
*/

        String path="D:\\4_能源所\\【二氧化碳排放数据】\\";
        /*String[] names={"所属省","所属市","区县名称", "主体功能区属性","行政区土地面积","户籍人口",
                "地区生产总值","人均GDP","第一产业","第二产业","第三产业","省"};*/
        String[] names={"省","排放量（万吨）","lng_gd","lat_gd"};

        writeExcel(path+"各省二氧化碳排放数据汇总_json_高德解析信息.txt",names);

        /*Vector<String> pois=FileTool.Load(path+"filename.txt","utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            writeExcel(poi.replace(".txt","_excel.txt"),names);
        }*/


    }

    //设计表格的第一行数据
    public static void designExcel(Sheet sheet) {
        Row row = sheet.createRow(0);
        //设置行高
        row.setHeightInPoints(30);
        Cell cell = row.createCell(0);
        for (int i = 0; i <= 14; i++) {
            switch (i) {
                case 0:
                    row.createCell(0).setCellValue("ID");
                    break;
                case 1:
                    row.createCell(1).setCellValue("所属市");
                    break;
                case 2:
                    row.createCell(2).setCellValue("区县名称");
                    break;
                case 3:
                    row.createCell(3).setCellValue("城镇");
                    break;
                case 4:
                    row.createCell(4).setCellValue("区域");
                    break;
                case 5:
                    row.createCell(5).setCellValue("主体功能区属性");
                    break;
                case 6:
                    row.createCell(6).setCellValue("行政区域土地面积");
                    break;
                case 7:
                    row.createCell(7).setCellValue("年末常住人口");
                    break;
                case 8:
                    row.createCell(8).setCellValue("地区生产总值");
                    break;
                case 9:
                    row.createCell(9).setCellValue("人均GDP");
                    break;
                case 10:
                    row.createCell(10).setCellValue("第一产业");
                    break;
                case 11:
                    row.createCell(11).setCellValue("第二产业");
                    break;
                case 12:
                    row.createCell(12).setCellValue("第三产业");
                    break;
                case 13:
                    row.createCell(13).setCellValue("经度");
                    break;
                case 14:
                    row.createCell(14).setCellValue("纬度");
                    break;
            }
        }
    }

    //将txt数据写成表格
    public static void writeExcel(String folder) throws IOException {

        //创建一个Excel(or new XSSFWorkbook())
        Workbook wb = new HSSFWorkbook();
        //创建表格
        Sheet sheet = wb.createSheet("test");
        //创建行
        Row row = sheet.createRow(0);
        //设置行高
        row.setHeightInPoints(30);
        designExcel(sheet);

        Vector<String> rds = FileTool.Load(folder, "UTF-8");

        JSONObject obj=new JSONObject();
        String element;
        int count=0;
        try{
            for (int i = 0; i < rds.size(); i++) {
                element = rds.elementAt(i);
                obj = JSONObject.fromObject(element);
                count++;
                row = sheet.createRow(count);
                for (int k = 0; k <= 14; k++) {
                    switch (k) {
                        case 0:
                            row.createCell(0).setCellValue(getObjValue_str(obj,"ID"));
                            break;
                        case 1:
                            row.createCell(1).setCellValue(getObjValue_str(obj,"所属市"));
                            break;
                        case 2:
                            row.createCell(2).setCellValue(getObjValue_str(obj,"区县名称"));
                            break;
                        case 3:
                            row.createCell(3).setCellValue(getObjValue_str(obj,"城镇"));
                            break;
                        case 4:
                            row.createCell(4).setCellValue(getObjValue_str(obj,"区域"));
                            break;
                        case 5:
                            row.createCell(5).setCellValue(getObjValue_str(obj,"主体功能区属性"));
                            break;
                        case 6:
                            row.createCell(6).setCellValue(getObjValue_dou(obj,"行政区域土地面积"));
                            break;
                        case 7:
                            row.createCell(7).setCellValue(getObjValue_dou(obj,"年末常住人口"));
                            break;
                        case 8:
                            row.createCell(8).setCellValue(getObjValue_dou(obj,"地区生产总值"));
                            break;
                        case 9:
                            row.createCell(9).setCellValue(getObjValue_dou(obj,"人均GDP"));
                            break;
                        case 10:
                            row.createCell(10).setCellValue(getObjValue_dou(obj,"第一产业"));
                            break;
                        case 11:
                            row.createCell(11).setCellValue(getObjValue_dou(obj,"第二产业"));
                            break;
                        case 12:
                            row.createCell(12).setCellValue(getObjValue_dou(obj,"第三产业"));
                            break;
                        case 13:
                            row.createCell(13).setCellValue(getObjValue_dou(obj,"经度"));
                            break;
                        case 14:
                            row.createCell(14).setCellValue(getObjValue_dou(obj,"纬度"));
                            break;
                    }
                }
            }
        }catch (NullPointerException e){
            System.out.println(obj);
        }

        FileOutputStream fos = new FileOutputStream(folder.replace(".txt", "") + "_excel.xls");
        wb.write(fos);
        if (null != fos) {
            fos.close();
        }
    }

    public static String getObjValue_str(JSONObject obj, String key){
        String value="";
        if(obj.containsKey(key)){
            value=obj.getString(key);
        }
        return value;
    }
    public static double getObjValue_dou(JSONObject obj, String key){
        double value=0;
        if(obj.containsKey(key)){
            value=obj.getDouble(key);
        }
        return value;
    }
    public static int getObjValue_int(JSONObject obj, String key){
        int value=0;
        if(obj.containsKey(key)){
            value=obj.getInt(key);
        }
        return value;
    }

    //设置成可自定义的标签的形式
    public static void designExcel(Sheet sheet,String[] names){
        //将标签放置在第一栏
        Row row = sheet.createRow(0);
        //设置行高
        row.setHeightInPoints(30);
        Cell cell = row.createCell(0);
        for (int i = 0; i <names.length; i++) {
            row.createCell(i).setCellValue(names[i]);
        }
    }
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

        JSONObject obj=new JSONObject();
        String element;
        int count=0;
        JSONObject region;
        try{
            for (int i = 0; i < rds.size(); i++) {
                element = rds.elementAt(i);
                obj = JSONObject.fromObject(element);
                if(obj.containsKey("region")){
                    region=obj.getJSONObject("region");
                    if(region.containsKey("province")){
                        obj.put("province",region.getString("province"));
                    }else {
                        obj.put("province","无");
                    }
                    if(region.containsKey("city")){
                        obj.put("city",region.getString("city"));
                    }else {
                        obj.put("city","无");
                    }
                    if(region.containsKey("county")){
                        obj.put("county",region.getString("county"));
                    }else {
                        obj.put("county","无");
                    }
                }
                count++;
                row = sheet.createRow(count);
                for (int k = 0; k < names.length; k++) {

                    String value=obj.getString(names[k]);
                    boolean num= Tool.isNumeric(value);
                    if(num){
                        row.createCell(k).setCellValue(getObjValue_dou(obj,names[k]));
                    }else {
                        row.createCell(k).setCellValue(getObjValue_str(obj,names[k]));
                    }

                }
            }
        }catch (NullPointerException e){
            System.out.println(obj);
        }

        FileOutputStream fos = new FileOutputStream(folder.replace(".txt", "_excel.xls"));
        wb.write(fos);
        if (null != fos) {
            fos.close();
        }
    }
}
