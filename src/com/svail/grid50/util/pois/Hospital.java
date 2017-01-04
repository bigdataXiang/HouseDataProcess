package com.svail.grid50.util.pois;

import com.svail.util.FileTool;
import net.sf.json.JSONObject;

import java.util.Vector;

import static com.svail.grid50.util.NumJudge.isNum;

/**
 * Created by ZhouXiang on 2016/12/19.
 */
public class Hospital {
    public static void main(String[] args){
        //makeLine("D:\\小论文\\poi资料\\医院\\北京医院大全.txt");
        //toJson("D:\\小论文\\poi资料\\医院\\北京医院大全_line.txt");
        //toJson_("D:\\小论文\\poi资料\\医院\\卫生服务中心.txt");

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
        }

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
}
