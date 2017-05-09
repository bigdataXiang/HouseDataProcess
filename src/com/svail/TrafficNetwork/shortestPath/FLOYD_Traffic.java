package com.svail.TrafficNetwork.shortestPath;

import static com.svail.TrafficNetwork.shortestPath.MatrixConstruction.shortestTime;

/**
 * Created by ZhouXiang on 2017/4/11.
 */
public class FLOYD_Traffic {

    double[][] length = null;// 任意两点之间路径长度
    int[][][] path = null;// 任意两点之间的路径
    public FLOYD_Traffic(double[][] G) {
        int MAX = 1000;int row = G.length;// 图G的行数
        int[][] spot = new int[row][row];// 定义任意两点之间经过的点
        int[] onePath = new int[row];// 记录一条路径
        length = new double[row][row];
        path = new int[row][row][];
        for (int i = 0; i < row; i++)// 处理图两点之间的路径
            for (int j = 0; j < row; j++) {
                if (G[i][j] == 0)G[i][j] = MAX;// 没有路径的两个点之间的路径为默认最大
                if (i == j)G[i][j] = 0;// 本身的路径长度为0
            }
        for (int i = 0; i < row; i++)// 初始化为任意两点之间没有路径
            for (int j = 0; j < row; j++)
                spot[i][j] = -1;
        for (int i = 0; i < row; i++)// 假设任意两点之间的没有路径
            onePath[i] = -1;
        for (int v = 0; v < row; ++v)
            for (int w = 0; w < row; ++w)
                length[v][w] = G[v][w];
        for (int u = 0; u < row; ++u)
            for (int v = 0; v < row; ++v)
                for (int w = 0; w < row; ++w)
                    if (length[v][w] > length[v][u] + length[u][w]) {
                        length[v][w] = length[v][u] + length[u][w];// 如果存在更短路径则取更短路径
                        spot[v][w] = u;// 把经过的点加入
                    }
        for (int i = 0; i < row; i++) {// 求出所有的路径
            int[] point = new int[1];
            for (int j = 0; j < row; j++) {
                point[0] = 0;
                onePath[point[0]++] = i;
                outputPath(spot, i, j, onePath, point);
                path[i][j] = new int[point[0]];
                for (int s = 0; s < point[0]; s++)
                    path[i][j][s] = onePath[s];
            }
        }
    }
    void outputPath(int[][] spot, int i, int j, int[] onePath, int[] point) {// 输出i// 到j// 的路径的实际代码，point[]记录一条路径的长度
        if (i == j)return;
        if (spot[i][j] == -1)
            onePath[point[0]++] = j;
        else {
            outputPath(spot, i, spot[i][j], onePath, point);
            outputPath(spot, spot[i][j], j, onePath, point);
        }
    }

    public static void main(String[] args) {

        double data[][] =shortestTime("D:\\8_周五报告\\yiqing\\train\\zd99\\火车站信息标准版.txt",
                "D:\\8_周五报告\\yiqing\\aircraft\\择城网\\航班信息标准化版.txt",
                "D:\\8_周五报告\\yiqing\\bus\\客运信息标准化版.txt",
                "08:00");




        FLOYD_Traffic test=new FLOYD_Traffic(data);

        for (int i = 0; i < data.length; i++){
            for (int j = i; j < data[i].length; j++) {
                double mintime=test.length[i][j];

                if(mintime<1000&&mintime!=0){
                    System.out.println();
                    System.out.print("From " + i + " to " + j + " path is: ");
                    for (int k = 0; k < test.path[i][j].length; k++)
                        System.out.print(test.path[i][j][k] + " ");
                    System.out.println();
                    System.out.println("From " + i + " to " + j + " length :"+ mintime);
                }
            }
        }

    }

}
