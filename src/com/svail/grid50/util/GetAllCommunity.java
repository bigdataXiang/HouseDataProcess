package com.svail.grid50.util;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.svail.util.FileTool;
import com.svail.util.Tool;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;

/**
 * Created by ZhouXiang on 2016/12/24.
 * 获取北京的所有小区的名字，存在set中，并且将没有comunity的数据记录下来
 */
public class GetAllCommunity {
    public static void main(String[] args){
         getCommunity("D:\\小论文\\poi资料\\小区\\所有小区名称.txt");
        //removalRedundancy("D:\\小论文\\poi资料\\小区\\所有小区名称.txt");
        //communityMath("D:\\小论文\\poi资料\\shpPoi\\P19住宅小区_point_json.txt",
       //              "D:\\小论文\\poi资料\\小区\\所有小区名称_去除冗余.txt");
        //check("D:\\小论文\\poi资料\\小区\\所有小区名称_去除冗余_匹配不成功_原始.txt");
        //mongoData("D:\\小论文\\poi资料\\小区\\所有小区名称_去除冗余_匹配不成功_原始_无坐标无地址.txt");
    }

    //从数据库【BasicData_Resold】中将所有数据的小区信息读取出来
    //区分出有小区信息和没有小区信息的数据，分别存储在【hascommunity】和【nocommunity】
    public static void getCommunity(String file){

        BasicDBObject doc;
        String community;

        DBCollection collection= db.getDB("paper").getCollection("BasicData_Resold");
        DBCursor cs=collection.find();

        DBCollection collection_hascommunity= db.getDB("paper").getCollection("hascommunity");
        DBCollection collection_nocommunity= db.getDB("paper").getCollection("nocommunity");

        Set<String> communities=new HashSet<>();

        int count=0;
        while (cs.hasNext()){
            count++;
            doc=(BasicDBObject)cs.next();
            if(doc.containsField("community")){
                community=doc.getString("community");
                if(community.equals("东上园")){
                    System.out.println(doc);
                }
                communities.add(community);
                collection_hascommunity.insert(doc);
            }else {
                collection_nocommunity.insert(doc);
            }
            //System.out.println(count);
        }

        Iterator<String> it=communities.iterator();
        while (it.hasNext()){
            //FileTool.Dump(it.next().toString(),file,"utf-8");
        }
    }

    //去除小区的冗余，即去掉小区括号里面的数据
    public static void removalRedundancy(String file){
        Vector<String> pois=FileTool.Load(file,"utf-8");
        Set<String> communities=new HashSet<>();
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            poi=Tool.delect_content_inBrackets(poi,"(",")");
            communities.add(poi);
        }
        Iterator<String> it=communities.iterator();
        while (it.hasNext()){
            FileTool.Dump(it.next().toString(),file.replace(".txt","_去除冗余.txt"),"utf-8");
        }
    }

    //利用shp_poi中的数据匹配小区地址
    public static void communityMath(String source,String target){
        Vector<String> pois=FileTool.Load(source,"utf-8");
        Map<String,String> community_map=new HashMap<>();
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=JSONObject.fromObject(poi);
            community_map.put(obj.getString("NAME"),"");
        }

        pois=FileTool.Load(target,"utf-8");
        String key;
        for(int i=0;i<pois.size();i++){
            String poi=pois.elementAt(i);
            JSONObject obj=new JSONObject();
            JSONArray array=new JSONArray();
            for(Map.Entry<String,String> entry:community_map.entrySet()){
                key=entry.getKey();
                if(key.contains(poi)){
                    array.add(key);
                }
            }
            obj.put(poi,array);
            if(array.size()!=0){
                //FileTool.Dump(obj.toString(),target.replace(".txt","_匹配成功.txt"),"utf-8");
            }else {
                FileTool.Dump(poi,target.replace(".txt","_匹配不成功_原始.txt"),"utf-8");
            }
           System.out.println(i);
        }
    }

    //检查小区匹配不成功的数据，调用数据库中的数据得到地理编码匹配的结果
    public static void check(String file){
        DBCollection collection= db.getDB("paper").getCollection("BasicData_Resold");

        Vector<String> pois=FileTool.Load(file,"utf-8");
        String poi="";
        for(int i=0;i<pois.size();i++){
            poi=pois.elementAt(i);
            BasicDBObject doc=new BasicDBObject();
            doc.put("community",poi);
            DBCursor cs=collection.find(doc);

            int count=0;
            BasicDBObject docment;
            System.out.println(i);
            while (cs.hasNext()){
                docment=(BasicDBObject)cs.next();
                JSONObject obj=new JSONObject();
                obj.put("community",poi);
                obj.put("lng",docment.getString("lng"));
                obj.put("lat",docment.getString("lat"));
                if(doc.containsField("address")){
                    count++;
                    obj.put("address",docment.getString("address"));
                    FileTool.Dump(obj.toString(),file.replace(".txt","_有坐标有地址.txt"),"utf-8");
                    break;
                }else {
                    count++;
                    obj.put("address",docment.getString("address"));
                    FileTool.Dump(obj.toString(),file.replace(".txt","_有坐标无地址.txt"),"utf-8");
                    break;
                }
            }

            if(count==0){
                System.out.println(i+"：无坐标无地址");
                FileTool.Dump(poi,file.replace(".txt","_无坐标无地址.txt"),"utf-8");
            }
        }

    }

    //筛选文件【所有小区名称_去除冗余_匹配不成功_原始.txt】地理编码后的结果，其中认为地址完全匹配的
    //为正确的，其他的酌情考虑
    public static void screen(String addressMatch,String community){
        Vector<String> addressMatchs=FileTool.Load(addressMatch,"utf-8");
        Vector<String> communitys=FileTool.Load(community,"utf-8");
        String poi;
        JSONObject obj;
        String com;
        for(int i=0;i<addressMatchs.size();i++){
            poi=addressMatchs.elementAt(i);
            obj=JSONObject.fromObject(poi);
            com=communitys.get(i);
            if(obj.size()==0){
                FileTool.Dump(com,community.replace(".txt","_地址匹配失败.txt"),"utf-8");
            }else {
                String nlp=obj.getString("nlp_status");
                if(nlp.equals("地址完全匹配")){
                    FileTool.Dump(com+";"+poi,community.replace(".txt","_地址完全匹配.txt"),"utf-8");
                }else if(nlp.equals("地址部分匹配")){
                    FileTool.Dump(com+";"+poi,community.replace(".txt","_地址部分匹配.txt"),"utf-8");
                }else if(nlp.equals("地址匹配失败")){
                    FileTool.Dump(com+";"+poi,community.replace(".txt","_地址匹配失败.txt"),"utf-8");
                }else {
                    FileTool.Dump(com+";"+poi,community.replace(".txt","_其他.txt"),"utf-8");
                }
            }
        }
    }

    //将调用高德api得到的数据进行整理，对比，清理未匹配出的地址
    public static void tidy_gaode(String addressMatch,String community){
        Vector<String> addressMatchs=FileTool.Load(addressMatch,"utf-8");
        Vector<String> communitys=FileTool.Load(community,"utf-8");
        String poi;
        JSONObject obj;
        String com;
        for(int i=0;i<addressMatchs.size();i++){
            poi=addressMatchs.elementAt(i);
            obj=JSONObject.fromObject(poi);
            com=communitys.get(i);

            int count=obj.getInt("count");
            if(count==0){
                FileTool.Dump(com,community.replace(".txt","_高德地址匹配失败.txt"),"utf-8");
            }else {
                JSONObject result=new JSONObject();
                JSONArray array=obj.getJSONArray("geocodes");
                JSONObject geocodes=array.getJSONObject(0);

                String formatted_address=geocodes.getString("formatted_address");
                String district=geocodes.getString("district");
                String[] location=geocodes.getString("location").split(",");
                double lng=Double.parseDouble(location[0]);
                double lat=Double.parseDouble(location[0]);
                String level=geocodes.getString("level");

                result.put("community",com);
                result.put("formatted_address",formatted_address);
                result.put("district",district);
                result.put("lng",lng);
                result.put("lat",lat);
                result.put("level",level);

                FileTool.Dump(result.toString(),addressMatch.replace(".txt","_高德地址匹配成功.txt"),"utf-8");
            }
        }
    }

    //检查数据库中那些小区定位不到坐标的数据
    public static void mongoData(String file){
        DBCollection collection= db.getDB("paper").getCollection("BasicData_Resold");
        Vector<String> pois=FileTool.Load(file,"utf-8");
        String poi="";
        for(int i=0;i<2;i++) {//pois.size()
            poi = pois.elementAt(i);
            BasicDBObject doc = new BasicDBObject();
            doc.put("community", poi);
            DBCursor cs = collection.find(doc);

            int count=0;
            BasicDBObject docment;
            System.out.println(i);
            while (cs.hasNext()) {
                docment = (BasicDBObject) cs.next();
                System.out.println(docment);
            }
        }
    }
}
