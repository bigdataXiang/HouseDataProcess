package com.svail.grid50;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.grid50.util.db;
import com.svail.grid50.util.db168;
import com.svail.util.FileTool;
import net.sf.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.util.*;

import static com.svail.grid50.ContourLine_10.getAnglesCoor;
import static com.svail.grid50.ContourLine_10.priceBlock;
import static com.svail.grid50.util.RowColCalculation.Code_RowCol;

/**
 * Created by ZhouXiang on 2016/12/16.
 * 该类利用【GridData_Resold】集合中的插值后的数据作为源数据
 * 生成【GridData_Resold_Acceleration】
 * 里面存储的是50*50的网格值为相对2015年的07月的增长率
 *
 * 源数据：{code(int),row(int),col(int),year(int),month(int),price(double)}
 * 结果：{code,row,col,year,month,acceleration}
 */
public class GridAcceleration_12 {
    public static void main(String[] args) throws IOException {



        /*DBCollection coll= db.getDB("paper").getCollection("GridData_Resold_gd_Interpolation");
        System.out.println(1);
        gridInterpolation(path+"所有无值融合的code_插值结果_融合.txt",coll);
        System.out.println(2);
        gridInterpolation(path+"failed_interpolation_codes_插值结果_融合.txt",coll);
        System.out.println(3);
        gridInterpolation(path+"full_value_grids.txt",coll);
        System.out.println(4);
        gridInterpolation(path+"interpolation_value_grids_中没有问题的数据.txt",coll);
        System.out.println(5);
        gridInterpolation(path+"pearson_is_0_插值结果_融合.txt",coll);
        System.out.println(6);
        gridInterpolation(path+"sparse_data_插值结果_融合.txt",coll);
        System.out.println(7);
        gridInterpolation(path+"以点代面_插值结果_融合.txt",coll);*/

        //chooseCode(path+"full_value_grids.txt");

        /*
        寻找加速度正常的数据
        Vector<String> pois=FileTool.Load(path+"全时序数据.txt","utf-8");
        for(int i=0;i<1779;i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            int code=obj.getInt("code");
            writeExcel_check(obj,path+code+".xls");
        }*/



        /*priceMatrix(path,2015,10);*/


        //提取由加速度价格矩阵经过区块处理的区块矩阵的每个范围内的等值线
        //引用【ContourLine_10】的程序
        /*System.out.println("**********"+12+"***********");
        String path="D:\\paper\\acceleration\\newest_acceleration\\201512\\";
        for(int i=1;i<=33;i++){
            priceBlock(path,"block-2015-12.txt",i);
        }
        System.out.println("**********"+1+"***********");
        path="D:\\paper\\acceleration\\newest_acceleration\\201601\\";
        for(int i=1;i<=33;i++){
            priceBlock(path,"block-2016-1.txt",i);
        }
        System.out.println("**********"+2+"***********");
        path="D:\\paper\\acceleration\\newest_acceleration\\201602\\";
        for(int i=1;i<=33;i++){
            priceBlock(path,"block-2016-2.txt",i);
        }
        System.out.println("**********"+3+"***********");
        path="D:\\paper\\acceleration\\newest_acceleration\\201603\\";
        for(int i=1;i<=33;i++){
            priceBlock(path,"block-2016-3.txt",i);
        }
        System.out.println("**********"+4+"***********");
        path="D:\\paper\\acceleration\\newest_acceleration\\201604\\";
        for(int i=1;i<=33;i++){
            priceBlock(path,"block-2016-4.txt",i);
        }
        System.out.println("**********"+5+"***********");
        path="D:\\paper\\acceleration\\newest_acceleration\\201605\\";
        for(int i=1;i<=33;i++){
            priceBlock(path,"block-2016-5.txt",i);
        }
        System.out.println("**********"+6+"***********");
        path="D:\\paper\\acceleration\\newest_acceleration\\201606\\";
        for(int i=1;i<=33;i++){
            priceBlock(path,"block-2016-6.txt",i);
        }
        System.out.println("**********"+7+"***********");
        path="D:\\paper\\acceleration\\newest_acceleration\\201607\\";
        for(int i=1;i<=33;i++){
            priceBlock(path,"block-2016-7.txt",i);
        }
        System.out.println("**********"+8+"***********");
        path="D:\\paper\\acceleration\\newest_acceleration\\201608\\";
        for(int i=1;i<=33;i++){
            priceBlock(path,"block-2016-8.txt",i);
        }
        System.out.println("**********"+9+"***********");
        path="D:\\paper\\acceleration\\newest_acceleration\\201609\\";
        for(int i=1;i<=33;i++){
            priceBlock(path,"block-2016-9.txt",i);
        }
        System.out.println("**********"+10+"***********");
        path="D:\\paper\\acceleration\\newest_acceleration\\201610\\";
        for(int i=1;i<=33;i++){
            priceBlock(path,"block-2016-10.txt",i);
        }
        System.out.println("**********"+11+"***********");
        path="D:\\paper\\acceleration\\newest_acceleration\\201611\\";
        for(int i=1;i<=33;i++){
            priceBlock(path,"block-2016-11.txt",i);
        }
*/

        //单独计算每个区块的经纬四角
        //CalculateLatLng("D:\\paper\\acceleration\\201603\\等值线_12.txt");

    }
    //由于网格数目太多一次性写不下，故先记下来格网，再计算具体的四个角的经纬度
    //事实证明这种方式也不可行，所以在v12.py代码中增加了处理四个角坐标的程序
    public static void CalculateLatLng(String file){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        String poi="";
        JSONObject obj;
        Iterator<String> it;
        String code;
        JSONObject result;
        JSONObject corners;
        for(int i=0;i<pois.size();i++){
            System.out.println("第"+(i+1)+"行");
            poi=pois.elementAt(i);
            obj=JSONObject.fromObject(poi);
            it=obj.keys();
            result=new JSONObject();
            while (it.hasNext()){
                code=it.next();
                corners=getAnglesCoor(Integer.parseInt(code));
                result.put(code,corners);
            }
            FileTool.Dump(""+result,file.replace(".txt","_corner.txt"),"utf-8");
        }
    }



    //生成4000*4000的每个月的加速度价格矩阵
    public static void priceMatrix(String path,int year,int month){

        Map<Integer,Double> code_price=new HashMap<>();
        DBCollection coll= db168.getDB("paper").getCollection("GridData_Resold_gd_Interpolation");
        BasicDBObject document=new BasicDBObject();
        document.put("year",year);
        document.put("month",month);
        DBCursor cs=coll.find(document);

        BasicDBObject doc=new BasicDBObject();
        double growth_adjace=0;
        int code=0;
        List<Double> differ=new ArrayList<>();
        int monitor=0;
        while (cs.hasNext()){
            doc=(BasicDBObject)cs.next();
            code=doc.getInt("code");
            growth_adjace=doc.getDouble("growth_adjace");
            code_price.put(code,growth_adjace);
            differ.add(growth_adjace);

            //monitor++;
            //System.out.println(monitor);
        }
        double max=Collections.max(differ);
        double min=Collections.min(differ);

        FileTool.Dump("max:"+max,path+year+"-"+month+"-最值.txt","utf-8");
        FileTool.Dump("min:"+min,path+year+"-"+month+"-最值.txt","utf-8");


        //将200km*200km范围内的格网看作是4000*4000的二维数组，从上至下对二维数组进行赋值
        //这里要考虑到编码系统里的行列号与数组里面的行列号的差别
        //编码系统的行列号的起始位置是左下角，而数组的行列号的起始位置是左上角
        double price;
        double[][] gridmatrix=new double[4000][4000];
        int array_row=0;//其中array_row+row=4000
        //row、col指的是编码系统里的行列号
        //array_row、array_col指的是二维矩阵中的行列号
        for(int row=4000;row>=1;row--){
            System.out.println(4000-row+1);
            String str="";
            int array_col=0;//其中array_col=col-1;
            for(int col=1;col<=4000;col++){

                code=col+(row-1)*4000;

                if(code_price.containsKey(code)){
                    price=code_price.get(code);
                    gridmatrix[array_row][array_col]=price;
                }else{
                    gridmatrix[array_row][array_col]=0.0;
                }
                str+=gridmatrix[array_row][array_col]+",";
                array_col++;
            }
            FileTool.Dump(str,path+"ContourLine-"+year+"-"+month+".txt","utf-8");
            array_row++;
        }
    }


    //利用逐个调用数据库code的办法来计算加速度
    public static void calculate_Acceleration(String dbName,String collExport,String collImport,String file){

        DBCollection coll_export= db.getDB(dbName).getCollection(collExport);
        DBCollection coll_import= db.getDB(dbName).getCollection(collImport);

        Vector<String> pois= FileTool.Load(file,"utf-8");
        int code;
        int row;
        int col;
        int year;
        int month;
        double basic;
        double price;
        BasicDBObject doc;
        BasicDBObject doc_acceleration;
        double acceleration=0;

        for(int i=0;i<pois.size();i++){
            basic=0;
            code=Integer.parseInt(pois.get(i));

            BasicDBObject document=new BasicDBObject();
            document.put("code",code);

            DBCursor cs=coll_export.find(document);
            //获得基底数据
            while (cs.hasNext()){
                doc=(BasicDBObject)cs.next();
                year=doc.getInt("year");
                month=doc.getInt("month");

                if(year==2015&&month==7){
                    basic=doc.getDouble("price");
                    continue;
                }
            }

            while (cs.hasNext()){
                doc=(BasicDBObject)cs.next();
                year=doc.getInt("year");
                month=doc.getInt("month");
                price=doc.getDouble("price");
                row=doc.getInt("row");
                col=doc.getInt("col");

                doc_acceleration=new BasicDBObject();
                doc_acceleration.put("code",code);
                doc_acceleration.put("row",row);
                doc_acceleration.put("col",col);
                doc_acceleration.put("year",year);
                doc_acceleration.put("month",month);

                if(year==2015&&month==7){

                }else {
                    if(year==2015){
                        acceleration=(price-basic)/(month-7)*10000;
                        doc_acceleration.put("acceleration",acceleration);
                    }else if(year==2016){
                        acceleration=(price-basic)/(12+month-7)*10000;
                        doc_acceleration.put("acceleration",acceleration);
                    }
                }
                coll_import.insert(doc_acceleration);
            }
        }
    }

    //利用本地文件读写的方式计算加速度
    //growth_basic:以2015年7月数据为基底计算增长率
    //differ_basic:以2015年7月数据为基底计算相邻月份的价格差
    //growth_adjace:计算相邻月份的价格差
    public static void gridInterpolation(String file,DBCollection coll){
        int code;
        String timeserise;
        String year_str;
        String month_str;
        int month;
        int year;
        String date;
        BasicDBObject doc;
        double price;
        double growth=0;
        double growth_adjace=0;
        double price_before=0;
        int month_before;
        double differ=0;
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                String poi=line;

                if(poi.indexOf(";")!=-1){
                    code=Integer.parseInt(poi.substring(0,poi.indexOf(";")));
                    timeserise=poi.substring(poi.indexOf(";")+";".length());
                }else{
                    code=Integer.parseInt(poi.substring(0,poi.indexOf(",")));
                    timeserise=poi.substring(poi.indexOf(",")+",".length());
                }
                JSONObject obj=JSONObject.fromObject(timeserise);
                //System.out.println(obj);

                Iterator<String> d=obj.keys();
                //先找到基底价格
                double basic_price=0;
                while (d.hasNext()){
                    date=d.next();
                    if(date.equals("2015-07")){
                        basic_price=obj.getDouble("2015-07");
                    }
                }
                Iterator<String> dates=obj.keys();
                while (dates.hasNext()){
                    doc=new BasicDBObject();

                    date=dates.next();
                    price=obj.getDouble(date);
                    year_str=date.substring(0,date.indexOf("-"));
                    month_str=date.substring(date.indexOf("-")+"-".length());

                    if(month_str.startsWith("0")){
                        month_str=month_str.substring(1);
                    }
                    year=Integer.parseInt(year_str);
                    month=Integer.parseInt(month_str);
                    int[] rowcol=Code_RowCol(code,1);

                    doc.put("code",code);
                    doc.put("row",rowcol[0]);
                    doc.put("col",rowcol[1]);
                    doc.put("year",year);
                    doc.put("month",month);
                    doc.put("price",price);

                    if(year==2015&&month==7){

                    }else {

                        //基底价格计算法
                        differ=(price-basic_price)*10000;
                        doc.put("differ_basic",differ);
                        if(year==2015){
                            growth=differ/(month-7);
                        }else if(year==2016){
                            growth=differ/(12+month-7);
                        }
                        doc.put("growth_basic",growth);

                        //相邻价格计算法
                        month_before=month-1;
                        if(month_before==0){
                            price_before=obj.getDouble("2015-12");
                        }else {
                            if (month_before<10){
                                price_before=obj.getDouble(year+"-0"+month_before);
                            }else {
                                price_before=obj.getDouble(year+"-"+month_before);
                            }
                        }
                        growth_adjace=(price-price_before)*10000;
                        doc.put("growth_adjace",growth_adjace);

                    }

                    DBCursor cs=coll.find(doc);
                    if(cs==null||cs.size()==0){
                        coll.insert(doc);
                    }else {
                        System.out.println("exist");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //从数据库中下载感兴趣的格网的数据，比较他们的时序价格，加速度
    //比较不同的加速度计算方式生成的结果
    public static void download(DBCollection coll,int code,String file){
        BasicDBObject find=new BasicDBObject();
        find.put("code",code);
        DBCursor cs=coll.find(find);

        BasicDBObject doc;
        JSONObject obj=new JSONObject();
        String month;
        String year;

        String date;
        while (cs.hasNext()){
            doc=(BasicDBObject)cs.next();
            doc.remove("_id");
            month=doc.getString("month");
            year=doc.getString("year");

            date=year+"-"+month;
            obj.put(date,doc);
        }

        JSONObject result=new JSONObject();
        result.put("code",code);
        result.put("data",obj);

        FileTool.Dump(result.toString(),file,"utf-8");
    }

    //挑出需要进行对比的数据，即全时序数据
    public static void chooseCode(String file){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            String code=poi.substring(0,poi.indexOf(";"));
            FileTool.Dump(code,file.replace(".txt","_code集合.txt"),"utf-8");
        }

    }

    //查询涨幅比较快的数据
    public static void writeExcel_check(JSONObject obj,String file) throws IOException {

        //首先判断这个格网的数据是否可靠，可靠才生成表格，不可靠直接忽略
        JSONObject data=obj.getJSONObject("data");
        String date;
        JSONObject grid;
        double value;
        String[] dates={"2015-7","2015-8","2015-9","2015-10","2015-11","2015-12",
                "2016-1","2016-2","2016-3", "2016-4","2016-5","2016-6",
                "2016-7","2016-8","2016-9","2016-10","2016-11",};
        int monitor=0;
        for(int i=1;i<dates.length;i++){
            date=dates[i];
            grid=data.getJSONObject(date);
            //System.out.println(grid);
            value=grid.getDouble("differ_basic");
            if(value>10000){
                ++monitor;
            }
        }

        if(monitor>1){
//创建一个Excel(or new XSSFWorkbook())
            Workbook wb = new HSSFWorkbook();
            //创建表格
            Sheet sheet = wb.createSheet("test");
            //创建行
            Row row = sheet.createRow(0);
            //设置行高
            row.setHeightInPoints(30);
            designExcel(sheet);
            int count=0;
            try{
                for(int i=0;i<dates.length;i++){
                    date=dates[i];
                    grid=data.getJSONObject(date);

                    count++;
                    row = sheet.createRow(count);
                    for (int k = 0; k <= 4; k++) {
                        switch (k) {
                            case 0:
                                row.createCell(0).setCellValue(date);
                                break;
                            case 1:
                                row.createCell(1).setCellValue(getObjValue(grid,"price"));
                                break;
                            case 2:
                                row.createCell(2).setCellValue(getObjValue(grid,"growth_basic"));
                                break;
                            case 3:
                                row.createCell(3).setCellValue(getObjValue(grid,"growth_adjace"));
                                break;
                            case 4:
                                row.createCell(4).setCellValue(getObjValue(grid,"differ_basic"));
                                break;
                        }
                    }
                }

            }catch (NullPointerException e){
                System.out.println(obj);
            }

            FileOutputStream fos = new FileOutputStream(file);
            wb.write(fos);
            if (null != fos) {
                fos.close();
            }
        }else {
            System.out.println("数据不是持续上涨~");
        }




    }

    public static void writeExcel(JSONObject obj,String file) throws IOException {

        //首先判断这个格网的数据是否可靠，可靠才生成表格，不可靠直接忽略
        JSONObject data=obj.getJSONObject("data");
        String date;
        JSONObject grid;
        double value;
        String[] dates={"2015-7","2015-8","2015-9","2015-10","2015-11","2015-12",
                "2016-1","2016-2","2016-3", "2016-4","2016-5","2016-6",
                "2016-7","2016-8","2016-9","2016-10","2016-11",};
        int monitor=0;
        for(int i=1;i<dates.length;i++){
            date=dates[i];
            grid=data.getJSONObject(date);
            //System.out.println(grid);
            value=grid.getDouble("differ_basic");
            if(value<0){
                ++monitor;
            }
        }

        if(monitor<3){
//创建一个Excel(or new XSSFWorkbook())
            Workbook wb = new HSSFWorkbook();
            //创建表格
            Sheet sheet = wb.createSheet("test");
            //创建行
            Row row = sheet.createRow(0);
            //设置行高
            row.setHeightInPoints(30);
            designExcel(sheet);
            int count=0;
            try{
                for(int i=0;i<dates.length;i++){
                    date=dates[i];
                    grid=data.getJSONObject(date);

                    count++;
                    row = sheet.createRow(count);
                    for (int k = 0; k <= 4; k++) {
                        switch (k) {
                            case 0:
                                row.createCell(0).setCellValue(date);
                                break;
                            case 1:
                                row.createCell(1).setCellValue(getObjValue(grid,"price"));
                                break;
                            case 2:
                                row.createCell(2).setCellValue(getObjValue(grid,"growth_basic"));
                                break;
                            case 3:
                                row.createCell(3).setCellValue(getObjValue(grid,"growth_adjace"));
                                break;
                            case 4:
                                row.createCell(4).setCellValue(getObjValue(grid,"differ_basic"));
                                break;
                        }
                    }
                }

            }catch (NullPointerException e){
                System.out.println(obj);
            }

            FileOutputStream fos = new FileOutputStream(file);
            wb.write(fos);
            if (null != fos) {
                fos.close();
            }
        }else {
            System.out.println("数据不是持续上涨~");
        }




    }


    //设计表格的第一行数据
    public static void designExcel(Sheet sheet) {
        Row row = sheet.createRow(0);
        //设置行高
        row.setHeightInPoints(30);
        Cell cell = row.createCell(0);
        for (int i = 0; i <= 4; i++) {
            switch (i) {
                case 0:
                    row.createCell(0).setCellValue("date");
                    break;
                case 1:
                    row.createCell(1).setCellValue("price");
                    break;
                case 2:
                    row.createCell(2).setCellValue("growth_basic");
                    break;
                case 3:
                    row.createCell(3).setCellValue("growth_adjace");
                    break;
                case 4:
                    row.createCell(4).setCellValue("differ_basic");
                    break;
            }
        }
    }
    public static double getObjValue(JSONObject obj,String key){
        double value=0;
        if(obj.containsKey(key)){
            value=obj.getDouble(key);
        }
        return value;
    }
}
