package com.svail.grid50.util.pois;

import com.svail.util.FileTool;
import net.sf.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import static com.svail.grid50.util.NumJudge.isNum;
import static com.svail.grid50.util.PoiCode.setPoiCode_50;

/**
 * Created by ZhouXiang on 2016/12/19.
 */
public class Hospital {
    public static void main(String[] args){
        //makeLine("D:\\小论文\\poi资料\\医院\\北京医院大全.txt");
        //toJson("D:\\小论文\\poi资料\\医院\\北京医院大全_line.txt");
        //toJson_("D:\\小论文\\poi资料\\医院\\卫生服务中心.txt");

        /*这里乱入教育类数据的匹配
        String path="D:\\小论文\\poi资料\\学校\\奥数网\\";
        String[] names={"幼儿园_高德解析信息.txt","小学_高德解析信息.txt",
                "初中_高德解析信息.txt","高中_高德解析信息.txt","大学_高德解析信息.txt",
                "成人教育_高德解析信息.txt","培训机构_高德解析信息.txt","小学排名_高德解析信息.txt",
                "初中排名_高德解析信息.txt","高中排名_高德解析信息.txt","大学排名_高德解析信息.txt",
                "幼儿园排名_高德解析信息.txt","成人教育排名_高德解析信息.txt","培训机构排名_高德解析信息.txt",
                "奥数网_中学库_高德解析信息.txt","奥数网_高中库_高德解析信息.txt"
        };
        for (int i=0;i<names.length;i++){
            match_again(path+names[i]);
        }*/

        String[] qualifys={"三级甲等","三级乙等","三级合格","",
                           "二级甲等","二级乙等","二级合格",
                           "一级甲等","一级乙等","一级合格","未评级"};

        hospitalCode("D:\\小论文\\影响因素相关性研究\\201507\\医院\\北京所有医院名单_json_高德解析信息_ok.txt");


    }
    public static void makeLine(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");
        String poi;
        String str="";
        String first;
        for(int i=0;i<pois.size();i++){
            poi=pois.elementAt(i);
            //System.out.println(poi);
            first=poi.substring(0,1);
            if(isNum(first)){//&&poi.indexOf("级")!=-1
                if(str.length()!=0){
                    System.out.println(str);
                    FileTool.Dump(str,file.replace(".txt","_line.txt"),"utf-8");
                    str="";
                }
                if(poi.indexOf("级")!=-1){
                    System.out.println(poi);
                    FileTool.Dump(str,file.replace(".txt","_line.txt"),"utf-8");
                }else {
                    str+=poi.toString().replace("\n","").replace("\r\n","").replace("\t","");
                }
               }else {
                str+=poi.toString().replace("\n","").replace("\r\n","").replace("\t","");
                //System.out.println(str);
            }
        }
    }
    public static void toJson(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");
        String poi="";
        String name="";
        String temp;
        String[] rest;
        String code;
        JSONObject obj;
        for(int i=0;i<pois.size();i++){
            obj=new JSONObject();
            poi=pois.elementAt(i);
            code=poi.substring(0,8);
            obj.put("code",code);
            name=poi.substring(8,poi.indexOf(")")+")".length());
            obj.put("name",name);
            temp=poi.substring(poi.indexOf(")")+")".length());
            if(temp.startsWith(" ")){
                temp=temp.substring(1);
            }
            rest=temp.split(" ");
            //System.out.println(rest.length);
            try{
                /*for(int j=0;j<rest.length;j++){
                    System.out.println(rest[j]);
                }*/
                obj.put("location",rest[0]);
                obj.put("type",rest[1]);
                obj.put("qualify",rest[2]);

                System.out.println(obj);
                FileTool.Dump(obj.toString(),file.replace(".txt","_json.txt"),"utf-8");
            }catch (ArrayIndexOutOfBoundsException e){
                System.out.println(i+"："+poi);
            }
        }
    }
    public static void toJson_(String file){
        Vector<String> pois= FileTool.Load(file,"utf-8");
        String poi="";
        String name="";
        String location;
        String code;
        String type;
        String[] temp;
        String qualify;
        String postCode;

        JSONObject obj;
        String first;
        for(int i=0;i<pois.size();i++){
            obj=new JSONObject();
            poi=pois.elementAt(i);
            temp=poi.split(",");
            if(temp.length>=4){
                obj.put("name",temp[1]);
                obj.put("management",temp[2]);
                obj.put("approval",temp[3]);

            }
            System.out.println(obj);
            FileTool.Dump(obj.toString(),file.replace(".txt","_json.txt"),"utf-8");

        }
    }

    //找出不是兴趣点的数据，需要再次匹配
    public static void match_again(String file){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            //address
            if(obj.containsKey("level")){
                String level=obj.getString("level");
                if(level.equals("兴趣点")||level.equals("门牌号")){
                    FileTool.Dump(poi,file.replace(".txt","_ok.txt"),"utf-8");
                }else {
                    FileTool.Dump(poi,file.replace(".txt","_再次匹配.txt"),"utf-8");
                }
            }else {
                FileTool.Dump(poi,file.replace(".txt","_再次匹配.txt"),"utf-8");
            }
        }
    }

    //根据医院的坐标赋予格网编号的信息，并且根据等级赋值
    public static void hospitalCode(String file){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        Set<String> qualifys=new HashSet<>();
        for(int i=0;i<pois.size();i++) {
            String poi = pois.elementAt(i);
            JSONObject obj = JSONObject.fromObject(poi);
            String qualify=obj.getString("qualify");
            qualifys.add(qualify);
            String type=obj.getString("type");
            String name=obj.getString("name");
            double lng_gd=obj.getDouble("lng_gd");
            double lat_gd=obj.getDouble("lat_gd");

            String result=setPoiCode_50(lat_gd,lng_gd);
            String[] crc=result.split(",");
            JSONObject o=new JSONObject();
            int qv=HospitalLevel(qualify);
            o.put("qualify_value",qv);
            o.put("qualify",qualify);
            o.put("type",type);
            o.put("name",name);
            o.put("code",crc[0]);
            o.put("row",crc[1]);
            o.put("col",crc[2]);

            FileTool.Dump(o.toString(),file.replace(".txt","_格网化.txt"),"utf-8");

        }
        Iterator<String> it=qualifys.iterator();
        while (it.hasNext()){
            System.out.println(it.next());
        }

    }

    public static int HospitalLevel(String qualify){
        int value=1;
        switch (qualify){
            case "三级甲等":value=10;
                break;
            case "三级乙等":value=9;
                break;
            case "三级合格":value=8;
                break;
            case "二级甲等":value=7;
                break;
            case "二级乙等":value=6;
                break;
            case "二级合格":value=5;
                break;
            case "一级甲等":value=4;
                break;
            case "一级乙等":value=3;
                break;
            case "一级合格":value=2;
                break;
            case "未评级":value=1;
                break;

        }
        return value;
    }
}
