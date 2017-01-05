package com.svail.grid50;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.grid50.util.db;
import com.svail.util.FileTool;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static com.svail.grid50.util.RowColCalculation.Code_RowCol;

/**
 * Created by ZhouXiang on 2016/12/16.
 * 将点最优线性无偏插值和邻近插值的结果存储到数据库【GridData_Resold_gd_Interpolation】中
 */
public class GridInterpolation_11 {
    public static void main(String[] args){
        String path="D:\\小论文\\poi资料\\小区\\小区地理编码原始数据\\最后结果\\校对结果\\插值\\2-以点代面插值结果\\";
        DBCollection coll= db.getDB("paper").getCollection("GridData_Resold_gd_Interpolation");
        System.out.println(1);
        gridInterpolation(path+"所有无值融合的code_插值结果_融合.txt",coll,path+"所有格网集合.txt");
        System.out.println(2);
        gridInterpolation(path+"failed_interpolation_codes_插值结果_融合.txt",coll,path+"所有格网集合.txt");
        System.out.println(3);
        gridInterpolation(path+"full_value_grids.txt",coll,path+"所有格网集合.txt");
        System.out.println(4);
        gridInterpolation(path+"interpolation_value_grids_中没有问题的数据.txt",coll,path+"所有格网集合.txt");
        System.out.println(5);
        gridInterpolation(path+"pearson_is_0_插值结果_融合.txt",coll,path+"所有格网集合.txt");
        System.out.println(6);
        gridInterpolation(path+"sparse_data_插值结果_融合.txt",coll,path+"所有格网集合.txt");
        System.out.println(7);
        gridInterpolation(path+"以点代面_插值结果_融合.txt",coll,path+"所有格网集合.txt");
    }

    public static void gridInterpolation(String file,DBCollection coll,String codefile){
        int code;
        String timeserise;
        String month;
        String year;
        String date;
        BasicDBObject doc;

        double price;
        Set<Integer> codes=new HashSet<>();
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


                Iterator<String> dates=obj.keys();

                while (dates.hasNext()){
                    doc=new BasicDBObject();

                    date=dates.next();
                    price=obj.getDouble(date);
                    year=date.substring(0,date.indexOf("-"));
                    month=date.substring(date.indexOf("-")+"-".length());
                    if(month.startsWith("0")){
                        month=month.substring(1);
                    }


                    int[] rowcol=Code_RowCol(code,1);
                    doc.put("code",code);
                    doc.put("row",rowcol[0]);
                    doc.put("col",rowcol[1]);
                    doc.put("year",year);
                    doc.put("month",month);
                    doc.put("price",price);

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
        Iterator<Integer> it=codes.iterator();
        while (it.hasNext()){
            FileTool.Dump(it.next().toString(),codefile,"utf-8");
        }

    }
}
