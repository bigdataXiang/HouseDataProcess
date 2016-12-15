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
    /**
     * full_value_grids：用来存储时序数据满格的数据
     * interpolation_value_grids:用来存储插值成功后的网格数据，其中value值是插值与真实值混合的结果
     *
     * 这两个map的key为格网code，value为时序价格json（{"2015-10":200}）
     */
    public static Map<Integer, JSONObject> full_value_grids=new HashMap<>();
    public static Map<Integer, JSONObject> interpolation_value_grids=new HashMap<>();

    public static void main(String[] args){

    }

    /**step_1:
     * 将插值成功的文件【interpolation_value_grids.txt】存储到map【interpolation_value_grids】中
     * 将数据满格的文件【full_value_grids.txt】存储到map【full_value_grids】中
     */
    public static void step_1(String path,String full,String interpolation){

        Vector<String> pois=FileTool.Load(path+full,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            String code=poi.substring(0,poi.indexOf(";"));
            String timeserise=poi.substring(poi.indexOf(";")+";".length());
            JSONObject obj=JSONObject.fromObject(timeserise);
            full_value_grids.put(Integer.parseInt(code),obj);
        }

        pois=FileTool.Load(path+interpolation,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            String code=poi.substring(0,poi.indexOf(";"));
            String timeserise=poi.substring(poi.indexOf(";")+";".length());
            JSONObject obj=JSONObject.fromObject(timeserise);
            interpolation_value_grids.put(Integer.parseInt(code),obj);
        }

    }

    /**step_2:利用插值成功的数据来插值插值失败的数据
     * 插值方式：邻近1km插值
     * deepMax:20
     */
    public static void step_2(String path,int deepMax){
        neighborInterpolation(path,deepMax);
    }
    /**step_3:将插值结果融合*/
    public static void step_3(String path,String filename){

        String[] dates={"2015-10","2015-11","2015-12","2016-1","2016-2","2016-3","2016-4","2016-5"};
        getInterpolation(dates,path,filename);
    }
    /**step_4:对【_无值融合.txt】的文件进行插值
     * 此处共有三份要插值的文件
     */
    public static void step_4(String path,int deepMax){

        reNeighborInterpolation(path,deepMax);

    }
    /**step_5:遍历整个大范围进行【以点代面】的插值*/
    public static void step_5(String path,int deepMax){
        pointToSurface(path,deepMax);
    }

    //方法：
    /**1、将类【PBSHADE_Spatial_7】函数【reInterpolationCode】中生成的本地文件用于邻近插值测试
     * 插值的文件有：
     * "failed_interpolation_codes.txt"
     * "pearson_is_0.txt"
     * "sparse_data.txt"
     */
    public static void neighborInterpolation(String path,int deepMax){

        String filename="failed_interpolation_codes.txt";
        Vector<String> pois= FileTool.Load(path+filename,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            int code=Integer.parseInt(poi.substring(0,poi.indexOf(",")));
            String timeserise=poi.substring(poi.indexOf(",")+",".length());
            JSONObject obj=JSONObject.fromObject(timeserise);
            String str=findNeighborCode(code,deepMax);
            str+=";"+obj;
            FileTool.Dump(str,path+filename.replace(".txt","_插值结果.txt"),"utf-8");

        }

        filename="pearson_is_0.txt";
        pois=FileTool.Load(path+filename,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            int code=Integer.parseInt(poi.substring(0,poi.indexOf(",")));
            String timeserise=poi.substring(poi.indexOf(",")+",".length());
            JSONObject obj=JSONObject.fromObject(timeserise);
            String str=findNeighborCode(code,deepMax);
            str+=";"+obj;
            FileTool.Dump(str,path+filename.replace(".txt","_插值结果.txt"),"utf-8");

        }

        filename="sparse_data.txt";
        pois=FileTool.Load(path+filename,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            int code=Integer.parseInt(poi.substring(0,poi.indexOf(",")));
            String timeserise=poi.substring(poi.indexOf(",")+",".length());
            JSONObject obj=JSONObject.fromObject(timeserise);
            String str=findNeighborCode(code,deepMax);
            str+=";"+obj;
            FileTool.Dump(str,path+filename.replace(".txt","_插值结果.txt"),"utf-8");
        }
    }

    /**2、利用【neighborInterpolation】中的结果"_插值结果.txt"作为插值的源数据
     * 生成"_融合.txt"数据
     */
    public static void getInterpolation(String[] dates,String path,String filename){
        List<Integer> failedcode=new ArrayList<>();

        double total;
        double avenrage;
        String date;
        JSONObject obj;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path+filename)));
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                //System.out.println(line);
                String poi=line;
                String[] array=poi.split(";");
                int code=Integer.parseInt(array[1]);
                JSONArray neighbor=JSONArray.fromObject(array[2]);



                int size=neighbor.size();
                JSONObject interpolation_result=new JSONObject();
                if(size!=0){

                    for(int d=0;d<dates.length;d++){
                        date=dates[d];
                        total=0;
                        for(int j=0;j<size;j++){
                            obj=(JSONObject)neighbor.get(j);
                            total+=obj.getDouble(date);
                        }
                        avenrage=total/size;
                        interpolation_result.put(date,avenrage);
                    }
                    FileTool.Dump(code+","+interpolation_result,path+filename.replace(".txt","_融合.txt"),"utf-8");

                }else {
                    failedcode.add(code);
                    FileTool.Dump(code+","+interpolation_result,path+filename.replace(".txt","_无值融合.txt"),"utf-8");
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**3、对于【getInterpolation】中插值失败的格网，即以【_无值融合.txt】结尾的文件，再次用第一次和第二次的插值结果实现插值
     *
     * 插值成功的文件：
     *"failed_interpolation_codes_插值结果_融合.txt"
     *"pearson_is_0_插值结果_融合.txt"
     *"sparse_data_插值结果_融合.txt"
     *"full_value_grids.txt"
     *"interpolation_value_grids.txt"
     *
     * 需要再次插值的文件：
     * "failed_interpolation_codes_插值结果_无值融合.txt"
     * "pearson_is_0_插值结果_无值融合.txt"
     * "sparse_data_插值结果_无值融合.txt"
     *
     * 最后生成的文件：
     *"所有无值融合的code_插值结果.txt"
     * 该文件包含了上述的【需要再次插值的文件】里的三个文件的code
     */
    public static void reNeighborInterpolation(String path,int deepMax){
        Vector<String> failed_interpolation=FileTool.Load(path+"failed_interpolation_codes_插值结果_融合.txt","utf-8");
        Vector<String> pearson_is_0=FileTool.Load(path+"pearson_is_0_插值结果_融合.txt","utf-8");
        Vector<String> sparse_data=FileTool.Load(path+"sparse_data_插值结果_融合.txt","utf-8");
        Vector<String> full_value_grids=FileTool.Load(path+"full_value_grids.txt","utf-8");
        Vector<String> interpolation_value_grids=FileTool.Load(path+"interpolation_value_grids.txt","utf-8");


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


        failed_interpolation=FileTool.Load(path+"failed_interpolation_codes_插值结果_无值融合.txt","utf-8");
        pearson_is_0=FileTool.Load(path+"pearson_is_0_插值结果_无值融合.txt","utf-8");
        sparse_data=FileTool.Load(path+"sparse_data_插值结果_无值融合.txt","utf-8");
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
            String str=findNeighborCode(code,deepMax,map);
            FileTool.Dump(str,path+"所有无值融合的code_插值结果.txt","utf-8");
        }


    }

    /**4、将所有格网里的没有值的格网用“以点代面的”插值方法对格网进行插值计算
     * 建立搜索深度deep=40
     * 插值范围：row:[500,2000] col:[180,2200]
     *
     * 用于插值的文件：
     * "所有无值融合的code_插值结果.txt"
     * "failed_interpolation_codes_插值结果_融合.txt"
     * "pearson_is_0_插值结果_融合.txt"
     * "sparse_data_插值结果_融合.txt"
     * "full_value_grids.txt"
     * "interpolation_value_grids.txt"
     *
     * 插值结果：
     * "以点代面_插值结果.txt"：遍历2km深度后插值成功的结果
     * "以点代面_插值插值不成功结果.txt"：遍历2km深度后插值仍然失败的结果
     *
     * */
    public static void pointToSurface(String path,int deepMax){
        Map<String,JSONObject> code_price=new HashMap<>();

        String file=path+"All_failedcode_插值结果_融合.txt";
        listAssignment(file,code_price);
        System.out.println(code_price.size());

        file=path+"failed_interpolation_codes_插值结果_融合.txt";
        listAssignment(file,code_price);
        System.out.println(code_price.size());

        file=path+"pearson_is_0_插值结果_融合.txt";
        listAssignment(file,code_price);
        System.out.println(code_price.size());

        file=path+"sparse_data_插值结果_融合.txt";
        listAssignment(file,code_price);
        System.out.println(code_price.size());

        file=path+"full_value_grids.txt";
        listAssignment(file,code_price);
        System.out.println(code_price.size());

        file=path+"interpolation_value_grids.txt";
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
                    String str=findNeighborCode(code,deepMax,code_price);
                    String[] array=str.split(";");
                    JSONArray temp=JSONArray.fromObject(array[2]);

                    //如果遍历了2km还是找不到邻近值，则视为插值失败，存储在"以点代面_插值结果_failedcode.txt"中
                    if(temp.size()!=0){
                        FileTool.Dump(str,path+"以点代面_插值结果.txt","utf-8");
                    }else {
                        FileTool.Dump(""+code,path+"以点代面_插值插值不成功结果.txt","utf-8");
                    }

                }
            }
        }
    }


    /**找出某个code中的邻近的插值满格的code，并用该code的值作为插值网格*/
    public static String findNeighborCode(int code,int deepMax){
        int[] rowcol=Code_RowCol(code,1);
        int row=rowcol[0];
        int col=rowcol[1];
        List<JSONObject> neighbor_codes=new ArrayList<>();

        //设置一个深度，该深度表示需要插值的网格的周围deep个网格的数据的遍历，
        //如果找到有数据的网格，则跳出深度循环
        int deep;
        for(deep=1;deep<deepMax;deep++){

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

    /**找出待插值点周围2km的所有格网进行【邻近插值】*/
    public static String findNeighborCode(int code,int deepMax,Map<String,JSONObject> map){
        int[] rowcol=Code_RowCol(code,1);
        int row=rowcol[0];
        int col=rowcol[1];
        List<JSONObject> neighbor_codes=new ArrayList<>();

        //设置一个深度，该深度表示需要插值的网格的周围deep个网格的数据的遍历，
        //如果找到有数据的网格，则跳出深度循环
        int deep;
        for(deep=1;deep<deepMax;deep++){

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
