package com.svail;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.svail.grid50.util.db;
import com.svail.util.FileTool;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import static com.svail.grid50.BatchProcess_1.addressMatch_GaoDe;
import static com.svail.grid50.BatchProcess_1.batchProcess;
import static com.svail.grid50.ContinueToRise_14.batchQuery;

public class Main {

    public static String sourcepath="D:\\小论文\\poi资料\\学校\\各区小学\\";
    public static void main(String[] args) throws UnsupportedEncodingException {


         //gaode();
        //temp("D:\\ContourLine-2016-10.txt");

        //shujuquchong("D:\\paper\\relative\\201507\\公园绿地\\公园绿地_region_json.txt");
        //gaode();

        /*for(int i=15;i<16;i++){
            System.out.println(i+"***********");
            temp("D:\\paper\\acceleration\\newest_acceleration\\201608\\等值线_"+i+".txt");
        }*/

        /*String path="D:\\paper\\一直都在涨的格网\\累积增长\\画曲线的数据\\";
        batchQuery(280,308,
                path+"interpolation_value_grids_中没有问题的数据_一直在涨的月份_小区.txt",
                path+"小区价格曲线运行记录_plus.txt");
        System.out.println("ok");*/

        /*String sourcepath="D:\\1_paper\\Investment model\\1-有效栅格\\201611\\";
        String storepath="D:\\1_paper\\Investment model\\3-栅格的主要户型\\201611\\";
        DBCollection coll= db.getDB("paper").getCollection("GridData_Resold_gd");
        for(int i=1;i<=21;i++){
            try{
                findHouseType(sourcepath+"等值线_"+i+".txt",storepath+"等值线_"+i+".txt",coll,"2016","11");
            }catch (NullPointerException e){
                e.getStackTrace();
            }
        }*/

        gaode();

        //staticFile("D:\\1_paper\\poi资料\\小区\\小区地理编码原始数据\\最后结果\\校对结果\\北京所有小区匹配数据汇总_拷贝.txt");

        //D:\1_paper\poi资料\学校\各区中学\地理编码
        //poiToMongo("D:\\1_paper\\poi资料\\学校\\各区中学\\地理编码\\北京所有中学名单_json_地理编码.txt");

    }
    public static void staticFile(String file){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        JSONArray array=new JSONArray();
        for(int i=0;i<pois.size();i++){
            System.out.println(i);
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            JSONObject o=new JSONObject();
            o.put("community",obj.getString("community"));
            o.put("lng_gd",obj.getDouble("lng_gd"));
            o.put("lat_gd",obj.getDouble("lat_gd"));
            array.add(o);
        }
        FileTool.Dump(array.toString(),file.replace(".txt","_array.txt"),"utf-8");
    }

    public static void temp(String file){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            int n=obj.size();
            if(n>20000){
                System.out.println(i+":"+n);//
            }
        }
    }

    //老师的地理编码程序
    public static void geocode(){
        try {
            boolean bath=false;
            String filename;
            String[] keys={"ADDRESS"};//"address","name","community"
            if(bath){
                Vector<String> filenames= FileTool.Load(sourcepath+"filename.txt","utf-8");
                for(int i=0;i<filenames.size();i++){
                    filename=filenames.elementAt(i);
                    batchProcess(2000,sourcepath,filename,keys);
                }
            }else {
                filename="poi_json_deuplicate.txt";
                batchProcess(2000,sourcepath,filename,keys);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void gaode() throws UnsupportedEncodingException {
        /*String path="D:\\小论文\\poi资料\\学校\\奥数网\\";
        String[] names={"幼儿园_高德解析信息.txt","小学_高德解析信息.txt",
                "初中_高德解析信息.txt","高中_高德解析信息.txt","大学_高德解析信息.txt",
                "成人教育_高德解析信息.txt","培训机构_高德解析信息.txt","小学排名_高德解析信息.txt",
                "初中排名_高德解析信息.txt","高中排名_高德解析信息.txt","大学排名_高德解析信息.txt",
                "幼儿园排名_高德解析信息.txt","成人教育排名_高德解析信息.txt","培训机构排名_高德解析信息.txt",
                "奥数网_中学库_高德解析信息.txt","奥数网_高中库_高德解析信息.txt"
        };*/

        /*String path="D:\\小论文\\poi资料\\地铁\\";
        String[] names={
                "1号线.txt","2号线.txt","4号线.txt","5号线.txt","6号线.txt","7号线.txt","8号线.txt","9号线.txt","10号线.txt",
                "13号线.txt","14号线.txt","15号线.txt","八通线.txt","昌平线.txt","大兴线.txt","房山线.txt","机场线.txt","亦庄线.txt",
        };
        String[] keys={"station"};//"address","name","community"
        addressMatch_GaoDe(0,keys,path,names[18].replace(".txt","_json.txt"),"北京","0e0926480b9a9118fb2b9d3238a20ce1");
*/
        String path="D:\\4_能源所\\【二氧化碳排放数据】\\";
        String[] keys={"省","名"};//"address","name","community",,"school"
        addressMatch_GaoDe(0,keys,path,"各省二氧化碳排放数据汇总_json.txt","","0e0926480b9a9118fb2b9d3238a20ce1");

        //String filename="昌平区小学_json_高德解析信息_再次匹配.txt";

    }

    public static void shujuquchong(String file){
        Vector<String> pois=FileTool.Load(file,"utf-8");

        Map<String,String> monitor=new HashMap<>();
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            String NAME=obj.getString("NAME");
            if(monitor.containsKey(NAME)){

            }else {
                monitor.put(NAME,"");
                FileTool.Dump(poi,file.replace(".txt","_去重.txt"),"utf-8");
            }

        }

    }

    public static void poiToMongo(String file){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        DBCollection collection= db.getDB("paper").getCollection("POI");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            BasicDBObject document=new BasicDBObject();
            Iterator<String> it=obj.keySet().iterator();
            while (it.hasNext()){
                String key=it.next();
                document.put(key,obj.get(key));
            }
            document.remove("nlp_status");
            document.put("type","中学");
            collection.insert(document);
        }
    }




}
