package com.svail.grid50.util;

/**
 * Created by ZhouXiang on 2016/12/14.
 */
public class PoiCode {
    public static double LAT_MIN = 39.438283;
    public static double LNG_MIN = 115.417284;
    public static double width=5.892999999998593E-4;//每50m的经度差
    public static double length=4.501999999998674E-4;//每50m的纬度差

    public static String setPoiCode_50(double latitude,double longitude) {

        int row = (int) Math.ceil((latitude - LAT_MIN) / length);
        int col = (int) Math.ceil((longitude - LNG_MIN) / width);
        int code = (col + 4000 * (row - 1));

        String result=code+","+row+","+col;

        return result;
    }
}
