package com.svail.nengyuansuo;

import com.svail.util.FileTool;
import net.sf.json.JSONObject;
import org.apache.poi.POIXMLException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import static com.svail.util.ReadExcel.readExcel;
import static com.svail.util.getNamesInFolder.getFilePath;

/**
 * Created by ZhouXiang on 2017/1/21.
 * 年前回去，通宵把能源所数据检查一遍，统一标签
 */
public class UniformLabel {
    public static void main(String[] args){
        String path="D:\\能源所\\中期汇报\\各省数据整理 - 标签统一版\\";

        //第一步：获取各个文件夹内的excel表格的绝对路径，存放在类似于【01北京filename.txt】中
        /*for(int i=0;i<province.length;i++){
            String root=path+province[i];
            getFilePath(root);
        }*/

        //按照【01北京filename.txt】逐个读取excel文件
        for(int i=0;i<province.length;i++){
            String filename=province[i]+"filename.txt";
            Vector<String> ps= FileTool.Load(path+filename,"utf-8");
            for(int j=0;j<ps.size();j++){
                String p=ps.elementAt(j);
                System.out.println(p);
                readExcel(p);
            }
        }

    }
    public static void readExcel(String fileName){
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
            Sheet sheet = wb.getSheetAt(0);     //获得第一个表单
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
                        if(cell_value!=null){
                            if(cell_value.toString().length()!=0){
                                obj.put(names.get(i),cell_value.toString());
                            }

                        }else {
                            obj.put(names.get(i),"");
                        }
                    }
                    //System.out.println(obj);
                    FileTool.Dump(obj.toString().replace(" ",""),fileName.replace(".xlsx","").replace(".xls","")+"_json.txt","utf-8");

                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }catch (POIXMLException e){
            e.getStackTrace();
        }
    }



    public static String[] province={"01北京","02天津","03河北","04山西","05内蒙古","06辽宁",
            "07吉林","08黑龙江","09上海","10江苏","11浙江","12安徽",
            "13福建","14江西","15山东","16河南","17湖北","18湖南","19广东","20广西",
            "21海南","22重庆","23四川","24贵州","25云南",
            "26西藏","27陕西","28甘肃","29青海","30宁夏","31新疆维吾尔族自治区"};
}
