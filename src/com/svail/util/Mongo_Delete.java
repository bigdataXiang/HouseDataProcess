package com.svail.util;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.svail.grid50.util.db;
import net.sf.json.JSONObject;
import org.apache.poi.POIXMLException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ZhouXiang on 2016/12/15.
 * 该类用于执行mongo中的删除操作
 */
public class Mongo_Delete {
    public static void main(String[] args){
        DBCollection collection= db.getDB("just_test").getCollection("test");
        BasicDBObject doc=new BasicDBObject();
        /*doc.put("year","2015");
        doc.put("month","10");
        doc.put("value","1");
        collection.insert(doc);

        doc=new BasicDBObject();
        doc.put("year","2015");
        doc.put("month","11");
        doc.put("value","2");
        collection.insert(doc);*/

        /*doc=new BasicDBObject();
        doc.put("year","2015");
        doc.put("month","10");
        delectDocument(collection,doc);*/

        //delectColl(collection);

        //delectDB(db.getDB("just_test"));

    }

    //删除集合中某些特定的文档
    public static void delectDocument(DBCollection coll, BasicDBObject document){
        coll.remove(document);
    }
    //删除整个集合
    public static void delectColl(DBCollection coll){
        coll.drop();
    }

    //删除整个数据库
    public static void delectDB(DB db){
        db.dropDatabase();
    }

    /**
     * Created by ZhouXiang on 2016/12/21.
     *
     * 内存溢出：-Xms258m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=768m
     *
     * //市界的地理坐标为：北纬39”26’至41”03’，东经115”25’至 117”30’。
     */
    public static class ReadExcel {
        public static void main(String[] args){
            /*Vector<String> files=FileTool.Load("D:\\小论文\\poi资料\\shpPoi\\filename.txt","utf-8");
            for(int i=0;i<files.size();i++){
                System.out.println(i+":"+files.elementAt(i));
                readExcel(files.elementAt(i));
            }*/


             readExcel("D:\\小论文\\poi资料\\企业\\北京企业名录大全.xls");
            //D:\小论文\poi资料\北京poi
            //D:\小论文\poi资料\shpPoi数据\

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
                                obj.put(names.get(i),cell_value.toString());
                            }
                        }
                        //System.out.println(obj);
                        FileTool.Dump(obj.toString(),fileName.replace(".xlsx","").replace(".xls","")+"_json.txt","utf-8");
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }catch (POIXMLException e){
                e.getStackTrace();
            }
        }

        //将csv文件转换成txt文件再进行读写操作
        public static void readCSV(String file,String[] names){
            try {
                BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                int count=0;
                String[] infos;

                for(String line=br.readLine();line!=null;line=br.readLine()){
                    count++;
                    System.out.println(count);
                    infos=line.split(",");

                    if(names.length==infos.length){
                        JSONObject obj=new JSONObject();
                        for(int i=0;i<names.length;i++){
                            obj.put(names[i],infos[i]);
                        }
                        FileTool.Dump(obj.toString(),file.replace(".txt","_json.txt"),"utf-8");
                    }else {
                        System.out.println("数组长度不一致");
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public static JSONObject readByRow(Cell cell){
            JSONObject obj=new JSONObject();
            int count=0;
                count++;
                switch (cell.getCellType()) {   //根据cell中的类型来输出数据
                    case HSSFCell.CELL_TYPE_NUMERIC:
                        obj.put("" + count, cell.getNumericCellValue());
                        break;
                    case HSSFCell.CELL_TYPE_STRING:
                        obj.put("" + count, cell.getStringCellValue());
                        break;
                    case HSSFCell.CELL_TYPE_BOOLEAN:
                        obj.put("" + count, cell.getBooleanCellValue());
                        break;
                    case HSSFCell.CELL_TYPE_FORMULA:
                        obj.put("" + count, cell.getCellFormula());
                        break;
                    case HSSFCell.CELL_TYPE_BLANK:
                        System.out.println("空白");
                        break;
                    default:
                        System.out.print("unsuported sell type");
                        break;
                }
            return obj;
        }
    }
}
