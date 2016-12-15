package com.svail.grid50;

import com.svail.util.FileTool;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static com.svail.grid50.util.RowColCalculation.Code_RowCol;

/**
 * 该部分处理“以点代面”的邻近插值部分
 */
public class Neighbor_Interpolation_9 {
    public static Map<Integer, JSONObject> jsonArray_map=new HashMap<>();
    public static Map<String, Map<String, Double>> dataset = new HashMap<>();
    public Map<String, Map<String, Double>> getDataSet() {
        return dataset;
    }
    public static Map<String, String> pearson_is_0=new HashMap<>();
    public static Map<Integer, JSONObject> sparse_data=new HashMap<>();
    public static Map<Integer, JSONObject> full_value_grids=new HashMap<>();
    public static Map<Integer, JSONObject> interpolation_value_grids=new HashMap<>();
    public static Map<Integer, JSONObject> interpolation_grids=new HashMap<>();
    public static Map<String, Map<String, Double>> interpolation_result= new HashMap<>();
    public static JSONArray failed_interpolation_codes=new JSONArray();
    public static JSONArray qualified_interpolation_codes=new JSONArray();


    public static void main(String[] args){
        getInterpolation();
        reNeighborInterpolation();
        pointToSurface();
    }

    /**21、不能用线性插值的数据：从sparse_data、failed_interpolation_codes和pearson_is_0中找到插值不成功的原始网格，并且将原始数据写于本地文件*/
    public static void reinterpolation(){
        //sparse_data:稀疏数据
        int code;
        JSONObject obj;
        for(Map.Entry<Integer, JSONObject>entry: sparse_data.entrySet()){
            code=entry.getKey();
            obj=entry.getValue();
            //FileTool.Dump(code+","+obj,"D:\\小论文\\插值完善\\sparse_data.txt","utf-8");
        }

        //failed_interpolation_codes：mae>1的网格
        for(int i=0;i<failed_interpolation_codes.size();i++){
            code=failed_interpolation_codes.getInt(i);
            obj=jsonArray_map.get(code);
            //FileTool.Dump(code+","+obj,"D:\\小论文\\插值完善\\failed_interpolation_codes.txt","utf-8");
        }

        //pearson_is_0_size:与其他网格相关系数为0网格
        for(Map.Entry<String,String>entry:pearson_is_0.entrySet()){
            code=Integer.parseInt(entry.getKey());
            obj=jsonArray_map.get(code);
            //FileTool.Dump(code+","+obj,"D:\\小论文\\插值完善\\pearson_is_0.txt","utf-8");
        }
    }
    /**22、将21中生成的本地文件用于邻近插值测试*/
    public static void neighborInterpolation(){
        Vector<String> pois= FileTool.Load("D:\\小论文\\插值完善\\failed_interpolation_codes.txt","utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            int code=Integer.parseInt(poi.substring(0,poi.indexOf(",")));
            String timeserise=poi.substring(poi.indexOf(",")+",".length());
            JSONObject obj=JSONObject.fromObject(timeserise);
            String str=findNeighborCode(code);
            str+=";"+obj;
            FileTool.Dump(str,"D:\\小论文\\插值完善\\failed_interpolation_codes_插值结果.txt","utf-8");

        }
        pois=FileTool.Load("D:\\小论文\\插值完善\\pearson_is_0.txt","utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            int code=Integer.parseInt(poi.substring(0,poi.indexOf(",")));
            String timeserise=poi.substring(poi.indexOf(",")+",".length());
            JSONObject obj=JSONObject.fromObject(timeserise);
            String str=findNeighborCode(code);
            str+=";"+obj;
            FileTool.Dump(str,"D:\\小论文\\插值完善\\pearson_is_0_插值结果.txt","utf-8");

        }
        pois=FileTool.Load("D:\\小论文\\插值完善\\sparse_data.txt","utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            int code=Integer.parseInt(poi.substring(0,poi.indexOf(",")));
            String timeserise=poi.substring(poi.indexOf(",")+",".length());
            JSONObject obj=JSONObject.fromObject(timeserise);
            String str=findNeighborCode(code);
            str+=";"+obj;
            FileTool.Dump(str,"D:\\小论文\\插值完善\\sparse_data_插值结果.txt","utf-8");

        }
    }

    /**23、找出某个code中的邻近的插值满格的code，并用该code的值作为插值网格*/
    public static String findNeighborCode(int code){
        int[] rowcol=Code_RowCol(code,1);
        int row=rowcol[0];
        int col=rowcol[1];
        List<JSONObject> neighbor_codes=new ArrayList<>();

        //设置一个深度，该深度表示需要插值的网格的周围deep个网格的数据的遍历，
        //如果找到有数据的网格，则跳出深度循环
        int deep;
        for(deep=1;deep<20;deep++){

            for(int r=row-deep;r<=row+deep;r++){
                for(int c=col-deep;c<=col+deep;c++){
                    int neighbor_code=(r-1)*4000+c;
                    if(neighbor_code!=code){
                        //检查周围有没有本身数据满格的网格或者通过插值数据满格的网格
                        if(interpolation_value_grids.containsKey(neighbor_code)){
                            JSONObject obj=interpolation_value_grids.get(neighbor_code);
                            neighbor_codes.add(obj);
                        }

                        if(full_value_grids.containsKey(neighbor_code)){
                            JSONObject obj=full_value_grids.get(neighbor_code);
                            neighbor_codes.add(obj);
                        }
                    }
                }
            }

            if(neighbor_codes.size()!=0){
                break;
            }

        }
        String str=deep+";"+code+";"+neighbor_codes;

        return str;
    }
    public static String findNeighborCode(int code,Map<String,JSONObject> map){
        int[] rowcol=Code_RowCol(code,1);
        int row=rowcol[0];
        int col=rowcol[1];
        List<JSONObject> neighbor_codes=new ArrayList<>();

        //设置一个深度，该深度表示需要插值的网格的周围deep个网格的数据的遍历，
        //如果找到有数据的网格，则跳出深度循环
        int deep;
        for(deep=1;deep<40;deep++){

            for(int r=row-deep;r<=row+deep;r++){
                for(int c=col-deep;c<=col+deep;c++){
                    int neighbor_code=(r-1)*4000+c;
                    if(neighbor_code!=code){
                        //检查周围有没有本身数据满格的网格或者通过插值数据满格的网格
                        if(map.containsKey(""+neighbor_code)){
                            JSONObject obj=map.get(""+neighbor_code);
                            neighbor_codes.add(obj);
                        }
                    }
                }
            }

            if(neighbor_codes.size()!=0){
                break;
            }

        }
        String str=deep+";"+code+";"+neighbor_codes;

        return str;
    }

    /**24、利用22方法中的结果"_插值结果.txt"作为插值的源数据*/
    public static void getInterpolation(){
        List<Integer> failedcode=new ArrayList<>();
        String[] dates={"2015-10","2015-11","2015-12","2016-1","2016-2","2016-3","2016-4","2016-5"};


        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\小论文\\插值完善\\以点代面_插值结果.txt")));
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                //System.out.println(line);
                String poi=line;
                String[] array=poi.split(";");
                int code=Integer.parseInt(array[1]);
                JSONArray neighbor=JSONArray.fromObject(array[2]);
                double total_10=0;
                double total_11=0;
                double total_12=0;
                double total_1=0;
                double total_2=0;
                double total_3=0;
                double total_4=0;
                double total_5=0;

                int size=neighbor.size();
                if(size!=0){
                    for(int j=0;j<size;j++){
                        JSONObject obj=(JSONObject)neighbor.get(j);
                        total_10+=obj.getDouble(dates[0]);
                        total_11+=obj.getDouble(dates[1]);
                        total_12+=obj.getDouble(dates[2]);
                        total_1+=obj.getDouble(dates[3]);
                        total_2+=obj.getDouble(dates[4]);
                        total_3+=obj.getDouble(dates[5]);
                        total_4+=obj.getDouble(dates[6]);
                        total_5+=obj.getDouble(dates[7]);
                    }

                    double avenrage_10=total_10/size;
                    double avenrage_11=total_11/size;
                    double avenrage_12=total_12/size;
                    double avenrage_1=total_1/size;
                    double avenrage_2=total_2/size;
                    double avenrage_3=total_3/size;
                    double avenrage_4=total_4/size;
                    double avenrage_5=total_5/size;

                    JSONObject interpolation_result=new JSONObject();
                    interpolation_result.put(dates[0],avenrage_10);
                    interpolation_result.put(dates[1],avenrage_11);
                    interpolation_result.put(dates[2],avenrage_12);
                    interpolation_result.put(dates[3],avenrage_1);
                    interpolation_result.put(dates[4],avenrage_2);
                    interpolation_result.put(dates[5],avenrage_3);
                    interpolation_result.put(dates[6],avenrage_4);
                    interpolation_result.put(dates[7],avenrage_5);

                    FileTool.Dump(code+","+interpolation_result,"D:\\小论文\\插值完善\\以点代面_插值结果_融合.txt","utf-8");

                }else {
                    failedcode.add(code);
                    FileTool.Dump(code+","+interpolation_result,"D:\\小论文\\插值完善\\以点代面_插值结果_failedcode.txt","utf-8");
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Vector<String> pois=FileTool.Load("D:\\小论文\\插值完善\\以点代面_插值结果.txt","utf-8");

    }

    /**25、对于周边50*50m范围内一个邻接数据都没有的，再次用第一次和第二次的插值结果实现插值*/
    public static void reNeighborInterpolation(){
        Vector<String> failed_interpolation=FileTool.Load("D:\\小论文\\插值完善\\failed_interpolation_codes_插值结果_融合.txt","utf-8");
        Vector<String> pearson_is_0=FileTool.Load("D:\\小论文\\插值完善\\pearson_is_0_插值结果_融合.txt","utf-8");
        Vector<String> sparse_data=FileTool.Load("D:\\小论文\\插值完善\\sparse_data_插值结果_融合.txt","utf-8");
        Vector<String> full_value_grids=FileTool.Load("D:\\小论文\\插值完善\\full_value_grids.txt","utf-8");
        Vector<String> interpolation_value_grids=FileTool.Load("D:\\小论文\\插值完善\\interpolation_value_grids.txt","utf-8");


        Map<String,JSONObject> map=new HashMap<>();
        for(int i=0;i<failed_interpolation.size();i++){
            String poi=failed_interpolation.elementAt(i);
            String code=poi.substring(0,poi.indexOf(","));
            String timeserise=poi.substring(poi.indexOf(",")+",".length());
            JSONObject obj=JSONObject.fromObject(timeserise);
            map.put(code,obj);
        }

        for(int i=0;i<pearson_is_0.size();i++){
            String poi=pearson_is_0.elementAt(i);
            String code=poi.substring(0,poi.indexOf(","));
            String timeserise=poi.substring(poi.indexOf(",")+",".length());
            JSONObject obj=JSONObject.fromObject(timeserise);
            map.put(code,obj);
        }

        for(int i=0;i<sparse_data.size();i++){
            String poi=sparse_data.elementAt(i);
            String code=poi.substring(0,poi.indexOf(","));
            String timeserise=poi.substring(poi.indexOf(",")+",".length());
            JSONObject obj=JSONObject.fromObject(timeserise);
            map.put(code,obj);
        }

        for(int i=0;i<full_value_grids.size();i++){
            String poi=full_value_grids.elementAt(i);
            String code=poi.substring(0,poi.indexOf(";"));
            String timeserise=poi.substring(poi.indexOf(";")+";".length());
            JSONObject obj=JSONObject.fromObject(timeserise);
            map.put(code,obj);
        }

        for(int i=0;i<interpolation_value_grids.size();i++){
            String poi=interpolation_value_grids.elementAt(i);
            String code=poi.substring(0,poi.indexOf(";"));
            String timeserise=poi.substring(poi.indexOf(";")+";".length());
            JSONObject obj=JSONObject.fromObject(timeserise);
            map.put(code,obj);
        }


        failed_interpolation=FileTool.Load("D:\\小论文\\插值完善\\failed_interpolation_codes_插值结果_failedcode.txt","utf-8");
        pearson_is_0=FileTool.Load("D:\\小论文\\插值完善\\pearson_is_0_插值结果_failedcode.txt","utf-8");
        sparse_data=FileTool.Load("D:\\小论文\\插值完善\\sparse_data_插值结果_failedcode.txt","utf-8");
        List<String> failedcodes=new ArrayList<>();
        for(int i=0;i<failed_interpolation.size();i++) {
            String poi = failed_interpolation.elementAt(i);
            failedcodes.add(poi);
        }
        for(int i=0;i<pearson_is_0.size();i++) {
            String poi = pearson_is_0.elementAt(i);
            failedcodes.add(poi);
        }
        for(int i=0;i<sparse_data.size();i++) {
            String poi = sparse_data.elementAt(i);
            failedcodes.add(poi);
        }


        for(int i=0;i<failedcodes.size();i++){
            int code=Integer.parseInt(failedcodes.get(i).replace(",{}",""));
            String str=findNeighborCode(code,map);
            FileTool.Dump(str,"D:\\小论文\\插值完善\\All_failedcode_插值结果.txt","utf-8");
        }


    }

    /**27、将所有格网里的没有值的格网用“以点代面的”插值方法对格网进行插值计算
     * 建立搜索深度deep=40*/
    public static void pointToSurface(){

        String path="D:\\小论文\\插值完善\\所有的插值结果\\";
        String file=path+"All_failedcode_插值结果_融合.txt";
        Map<String,JSONObject> code_price=new HashMap<>();
        listAssignment(file,code_price);
        System.out.println(code_price.size());

        file=path+"failed_interpolation_codes_插值结果_融合.txt";
        listAssignment(file,code_price);
        System.out.println(code_price.size());

        file=path+"full_value_grids.txt";
        listAssignment(file,code_price);
        System.out.println(code_price.size());

        file=path+"interpolation_value_grids.txt";
        listAssignment(file,code_price);
        System.out.println(code_price.size());

        file=path+"pearson_is_0_插值结果_融合.txt";
        listAssignment(file,code_price);
        System.out.println(code_price.size());

        file=path+"sparse_data_插值结果_融合.txt";
        listAssignment(file,code_price);
        System.out.println(code_price.size());

        for(int row=500;row<=2000;row++){
            System.out.println(row);
            for(int col=180;col<=2200;col++){
                int code=(row-1)*4000+col;
                if(code_price.containsKey(code)){
                    //如果code_price中含有该code则不需要插值
                }else{
                    //进行深度遍历插值
                    String str=findNeighborCode(code,code_price);
                    String[] array=str.split(";");
                    JSONArray temp=JSONArray.fromObject(array[2]);
                    if(temp.size()!=0){
                        FileTool.Dump(str,"D:\\小论文\\插值完善\\"+"以点代面_插值结果.txt","utf-8");
                    }else {
                        FileTool.Dump(""+code,"D:\\小论文\\插值完善\\"+"以点代面_插值结果_failedcode.txt","utf-8");
                    }

                }
            }
        }
    }

    /**将文件中时序价格数据存到map中*/
    public static void listAssignment(String file,Map<String,JSONObject> code_price){
        Vector<String> pois= FileTool.Load(file,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            String code=poi.substring(0,poi.indexOf(","));
            String timeserise=poi.substring(poi.indexOf(",")+",".length());
            JSONObject obj=JSONObject.fromObject(timeserise);

            code_price.put(code,obj);
        }
    }


}
