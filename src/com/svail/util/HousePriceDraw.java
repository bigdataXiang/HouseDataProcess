package com.svail.util;

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
import java.util.Vector;

public class HousePriceDraw {
	public static void main(String[] args){
		GridCurve();
	}
	public static void GridCurve(){
		JFrame frame=new JFrame("Java数据统计图");
	    frame.setLayout(new GridLayout(1,1,10,10));
		new HousePriceDraw();
		frame.add(HousePriceDraw.getChartPanel());
	    frame.setBounds(50, 50, 800, 600);
	    frame.setVisible(true);
		
	}
	
	
	static ChartPanel frame;
	
	public HousePriceDraw(){
		 /*
         * 绘制折线图
         */
        XYDataset xydataset = createDataset("D:\\test\\infofusion\\grid\\fang.txt","D:\\test\\infofusion\\grid\\fang1.txt","D:\\test\\infofusion\\grid\\fang2.txt");
        
        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart("北京二手房价格", "", "价格",xydataset, true, false, true);
        XYPlot plot = (XYPlot) jfreechart.getPlot();
        DateAxis dateaxis = (DateAxis)plot.getDomainAxis();
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)plot.getRenderer();

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setAxisLineStroke(new BasicStroke(2.0f));     // 设置轴线粗细
        rangeAxis.setAxisLinePaint(Color.BLACK);               // 设置轴线颜色
        rangeAxis.setUpperBound(15f);                        // 设置坐标最大值
        rangeAxis.setLowerBound(0f);

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
	private static XYDataset createDataset(String folder, String folder1, String folder2) {

		TimeSeries timeseries = new TimeSeries("我爱我家", org.jfree.data.time.Day.class);

        Vector<String> Pois= FileTool.Load(folder, "utf-8");
        String[] years={"2015","2015","2015","2015","2015","2015","2016","2016","2016","2016","2016"};
        String[] months={"07","08","09","10","11","12","01","02","03","04","05"};
        String[]   days={"01","01","01","01","01","01","01","01","01","01","01"};
        for(int i=0;i<Pois.size();i++){

            String poi=Pois.elementAt(i);
            JSONObject obj= JSONObject.fromObject(poi);
            Double Price=obj.getDouble("average_price");
            int year= Integer.parseInt(years[i]);
            int month= Integer.parseInt(months[i]);
            int day= Integer.parseInt(days[i]);
            timeseries.addOrUpdate(new Day(day,month,year), Price);
        }

        TimeSeries timeseries1 = new TimeSeries("房天下", org.jfree.data.time.Day.class);

        Vector<String> Pois1= FileTool.Load(folder1, "utf-8");
        String[] years1={"2015","2015","2015","2015","2015","2015","2016","2016","2016","2016","2016"};
        String[] months1={"07","08","09","10","11","12","01","02","03","04","05"};
        String[]   days1={"01","01","01","01","01","01","01","01","01","01","01"};
        for(int i=0;i<Pois1.size();i++){

            String poi=Pois1.elementAt(i);
            JSONObject obj= JSONObject.fromObject(poi);
            Double Price=obj.getDouble("average_price");
            int year= Integer.parseInt(years[i]);
            int month= Integer.parseInt(months[i]);
            int day= Integer.parseInt(days[i]);
            timeseries1.addOrUpdate(new Day(day,month,year), Price);
        }

        TimeSeries timeseries2 = new TimeSeries("安居客", org.jfree.data.time.Day.class);

        Vector<String> Pois2= FileTool.Load(folder2, "utf-8");
        String[] years2={"2015","2015","2015","2015","2015","2015","2016","2016","2016","2016","2016"};
        String[] months2={"07","08","09","10","11","12","01","02","03","04","05"};
        String[]   days2={"01","01","01","01","01","01","01","01","01","01","01"};
        for(int i=0;i<Pois2.size();i++){

            String poi=Pois2.elementAt(i);
            JSONObject obj= JSONObject.fromObject(poi);
            Double Price=obj.getDouble("average_price");
            int year= Integer.parseInt(years[i]);
            int month= Integer.parseInt(months[i]);
            int day= Integer.parseInt(days[i]);
            timeseries2.addOrUpdate(new Day(day,month,year), Price);
        }

        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(timeseries);
        timeseriescollection.addSeries(timeseries1);
        timeseriescollection.addSeries(timeseries2);
       
        return timeseriescollection;
    }
  public static ChartPanel getChartPanel(){
        return frame;
         
    }


}
