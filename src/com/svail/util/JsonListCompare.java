package com.svail.util;

import net.sf.json.JSONObject;

import java.util.Comparator;
import java.util.GregorianCalendar;

/**
 * Created by ZhouXiang on 2017/2/23.
 * 比较一个含有很有json的数组的大小
 */
public class JsonListCompare {
    public static class TimeComparator implements Comparator {
        public int compare(Object object1, Object object2) {// 实现接口中的方法
            JSONObject p1 = (JSONObject) object1; // 强制转换
            //System.out.println(p1);
            JSONObject p2 = (JSONObject) object2;

            String date1=(String)p1.get("date");
            String date2=(String)p2.get("date");

            String[] dates=date1.split("-");

            int year1=Integer.parseInt(dates[0]);
            int month1=Integer.parseInt(dates[1]);
            int day1=1;
            if(dates.length==2){
                day1=1;
            }else if(dates.length==3){
                day1=Integer.parseInt(dates[2]);
            }
            GregorianCalendar calendar1=new GregorianCalendar(year1,month1,day1);
            // System.out.println(calendar1);

            dates=date2.split("-");
            int year2=Integer.parseInt(dates[0]);
            int month2=Integer.parseInt(dates[1]);
            int day2=1;
            if(dates.length==2){
                day2=1;
            }else if(dates.length==3){
                day2=Integer.parseInt(dates[2]);
            }
            GregorianCalendar calendar2=new GregorianCalendar(year2,month2,day2);

            return calendar1.compareTo(calendar2);

        }
    }
    public static class CodeComparator implements Comparator {
        public int compare(Object object1, Object object2) {

            JSONObject obj1=JSONObject.fromObject(object1);
            JSONObject obj2=JSONObject.fromObject(object2);

            int code1=obj1.getInt("code");
            int code2=obj2.getInt("code");

            int flag = new Integer(code1).compareTo(new Integer(code2));
            return flag;
        }
    }
    public static class Average_PriceComparator implements Comparator {
        public int compare(Object object1, Object object2) {

            JSONObject obj1=JSONObject.fromObject(object1);
            JSONObject obj2=JSONObject.fromObject(object2);

            double price1=obj1.getDouble("average_price");
            double price2=obj2.getDouble("average_price");

            int flag = new Double(price1).compareTo(new Double(price2));
            return flag;
        }
    }
    public static class RComparator implements Comparator {
        public int compare(Object object1, Object object2) {

            JSONObject obj1=JSONObject.fromObject(object1);
            JSONObject obj2=JSONObject.fromObject(object2);

            double price1=obj1.getDouble("r");
            double price2=obj2.getDouble("r");

            int flag = new Double(price1).compareTo(new Double(price2));
            return flag;
        }
    }
    public static class numComparator implements Comparator{
        public int compare(Object object1, Object object2){
            JSONObject obj1=JSONObject.fromObject(object1);
            JSONObject obj2=JSONObject.fromObject(object2);

            int num1=obj1.getInt("num");
            int num2=obj2.getInt("num");

            int flag = new Integer(num1).compareTo(new Integer(num2));
            return -flag;
        }
    }
}
