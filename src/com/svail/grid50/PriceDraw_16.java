package com.svail.grid50;
import net.sf.json.JSONObject;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

/**
 * 将从【BasicData_Resold_gd】查询的小区的时序真实数据进行画曲线处理
 */
public class PriceDraw_16 {


    public static void PriceCurve(JSONObject obj1,String community,double max,double min){
        JFrame frame=new JFrame("Java数据统计图");
        frame.setLayout(new GridLayout(1,1,10,10));
        new PriceDraw_16(obj1,community,max,min);
        frame.add(PriceDraw_16.getChartPanel());
        frame.setBounds(50, 50, 800, 600);
        frame.setVisible(true);

    }


    static ChartPanel frame;

    public PriceDraw_16(JSONObject obj1,String community,double max,double min){
		 /*
         * 绘制折线图
         */
        XYDataset xydataset = createDataset(obj1);

        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(community+"房价时序图", "", "房价(万元/㎡)",xydataset, true, false, true);
        XYPlot plot = (XYPlot) jfreechart.getPlot();
        DateAxis dateaxis = (DateAxis)plot.getDomainAxis();
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)plot.getRenderer();

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setAxisLineStroke(new BasicStroke(2.0f));     // 设置轴线粗细
        rangeAxis.setAxisLinePaint(Color.BLACK);               // 设置轴线颜色
        rangeAxis.setUpperBound(max);                        // 设置坐标最大值
        rangeAxis.setLowerBound(min);

        //设置网格背景颜色
        plot.setBackgroundPaint(Color.white);
        //设置网格竖线颜色
        plot.setDomainGridlinePaint(Color.pink);
        //设置网格横线颜色
        plot.setRangeGridlinePaint(Color.pink);
        //设置曲线图与xy轴的距离
        plot.setAxisOffset(new RectangleInsets(10D, 10D, 10D, 0.1D));
        //设置曲线是否显示数据点
        xylineandshaperenderer.setBaseShapesVisible(true);
        //设置曲线显示各数据点的值
        XYItemRenderer xyitem = plot.getRenderer();
        xyitem.setBaseItemLabelsVisible(true);
        xyitem.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.INSIDE12, TextAnchor.BOTTOM_CENTER));//.OUTSIDE12  BASELINE_LEFT
        xyitem.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
        xyitem.setBaseItemLabelFont(new Font("Dialog", 1, 14));


        dateaxis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
        frame=new ChartPanel(jfreechart,true);
        dateaxis.setLabelFont(new Font("黑体", Font.BOLD,14));         //水平底部标题
        dateaxis.setTickLabelFont(new Font("宋体", Font.BOLD,12));  //垂直标题
        ValueAxis rangeAxis1=plot.getRangeAxis();//获取柱状
        rangeAxis1.setLabelFont(new Font("黑体", Font.BOLD,15));
        jfreechart.getLegend().setItemFont(new Font("黑体", Font.BOLD, 15));
        jfreechart.getTitle().setFont(new Font("宋体", Font.BOLD,20));//设置标题字体
    }
    private static XYDataset createDataset(JSONObject obj1) {

        TimeSeries timeseries1 = new TimeSeries("时序房价数据", Day.class);

        String[] years1={"2015","2015","2015","2016","2016","2016","2016","2016","2016","2016","2016","2016","2016","2016"};
        String[] months1={"10","11","12","01","02","03","04","05","06","07","08","09","10","11"};
        String[]   days1={"01","01","01","01","01","01","01","01","01","01","01","01","01","01"};
        for(int i=0;i<months1.length;i++){

            String date1=years1[i]+"-"+months1[i];
            if(obj1.containsKey(date1)){
                Double Price=obj1.getDouble(date1);
                int year= Integer.parseInt(years1[i]);
                int month= Integer.parseInt(months1[i]);
                int day= Integer.parseInt(days1[i]);
                timeseries1.addOrUpdate(new Day(day,month,year), Price);
            }
        }

        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(timeseries1);

        return timeseriescollection;
    }
    public static ChartPanel getChartPanel(){
        return frame;

    }
}
