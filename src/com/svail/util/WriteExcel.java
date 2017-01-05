package com.svail.util;

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
 * Created by ZhouXiang on 2017/1/5.
 */
public class WriteExcel {
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
                            row.createCell(0).setCellValue(getObjValue(obj,"ID"));
                            break;
                        case 1:
                            row.createCell(1).setCellValue(getObjValue(obj,"所属市"));
                            break;
                        case 2:
                            row.createCell(2).setCellValue(getObjValue(obj,"区县名称"));
                            break;
                        case 3:
                            row.createCell(3).setCellValue(getObjValue(obj,"城镇"));
                            break;
                        case 4:
                            row.createCell(4).setCellValue(getObjValue(obj,"区域"));
                            break;
                        case 5:
                            row.createCell(5).setCellValue(getObjValue(obj,"主体功能区属性"));
                            break;
                        case 6:
                            row.createCell(6).setCellValue(getObjValue(obj,"行政区域土地面积"));
                            break;
                        case 7:
                            row.createCell(7).setCellValue(getObjValue(obj,"年末常住人口"));
                            break;
                        case 8:
                            row.createCell(8).setCellValue(getObjValue(obj,"地区生产总值"));
                            break;
                        case 9:
                            row.createCell(9).setCellValue(getObjValue(obj,"人均GDP"));
                            break;
                        case 10:
                            row.createCell(10).setCellValue(getObjValue(obj,"第一产业"));
                            break;
                        case 11:
                            row.createCell(11).setCellValue(getObjValue(obj,"第二产业"));
                            break;
                        case 12:
                            row.createCell(12).setCellValue(getObjValue(obj,"第三产业"));
                            break;
                        case 13:
                            row.createCell(13).setCellValue(getObjValue(obj,"经度"));
                            break;
                        case 14:
                            row.createCell(14).setCellValue(getObjValue(obj,"纬度"));
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

    public static String getObjValue(JSONObject obj,String key){
        String value="";
        if(obj.containsKey(key)){
            value=obj.getString(key);
        }
        return value;
    }
}
