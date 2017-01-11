package com.svail;

import com.svail.util.FileTool;
import net.sf.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static com.svail.grid50.BatchProcess_1.addressMatch_GaoDe;
import static com.svail.grid50.BatchProcess_1.batchProcess;
import static com.svail.grid50.GridData_Resold_6.initial;
import static com.svail.grid50.util.GetAllCommunity.screen;
import static com.svail.grid50.util.GetAllCommunity.tidy_gaode;

public class Main {

    public static String sourcepath="D:\\小论文\\poi资料\\学校\\各区小学\\";
    public static void main(String[] args) throws UnsupportedEncodingException {


         //gaode();
        //temp("D:\\ContourLine-2016-10.txt");

        //shujuquchong("D:\\paper\\relative\\201507\\公园绿地\\公园绿地_region_json.txt");
        gaode();
    }

    public static void temp(String file){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        for(int i=1761;i<1762;i++){
            String poi=pois.elementAt(i);
            String[] ay=poi.split(",");
            System.out.println(ay[945]);
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
        String path="D:\\paper\\relative\\201507\\公园绿地\\";
        String[] keys={"NAME"};//"address","name","community",,"school"
        addressMatch_GaoDe(0,keys,path,"公园绿地_region_json_去重.txt","北京","0e0926480b9a9118fb2b9d3238a20ce1");

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



}
