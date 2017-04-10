package com.svail.Datastruct.Sort;

/**
 * Created by ZhouXiang on 2017/4/5.
 */
public class InsertSort {
    public static void sort(int[] A){
        for(int i=1;i<A.length;i++){
            int key=A[i];//将当前的牌的值存到key中
            int j=i-1;
            while (j>=0&&A[j]>key){
                A[j+1]=A[j];
                j--;
            }
            if(j!=i-1){
                A[j+1]=key;
            }
        }
    }
}
