package com.svail.grid50;

import com.svail.util.FileTool;
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

public class PriceAccelerationDraw_15 {
	public static void main(String[] args){

	}


	public static void GridCurve(JSONObject obj1, JSONObject obj2,JSONObject obj3,String community){
		JFrame frame=new JFrame("Java数据统计图");
	    frame.setLayout(new GridLayout(1,1,10,10));
		new PriceAccelerationDraw_15(obj1,obj2,obj3,community);
		frame.add(PriceAccelerationDraw_15.getChartPanel());
	    frame.setBounds(50, 50, 800, 600);
	    frame.setVisible(true);

	}


	static ChartPanel frame;

	public PriceAccelerationDraw_15(JSONObject obj1, JSONObject obj2,JSONObject obj3,String community){
		 /*
         * 绘制折线图
         */
        XYDataset xydataset = createDataset(obj1,obj2,obj3);

        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(community+"价格增长时序图", "", "增长值",xydataset, true, false, true);
        XYPlot plot = (XYPlot) jfreechart.getPlot();
        DateAxis dateaxis = (DateAxis)plot.getDomainAxis();
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)plot.getRenderer();

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setAxisLineStroke(new BasicStroke(2.0f));     // 设置轴线粗细
        rangeAxis.setAxisLinePaint(Color.BLACK);               // 设置轴线颜色
        rangeAxis.setUpperBound(12000f);                        // 设置坐标最大值
        rangeAxis.setLowerBound(-12000f);

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
	private static XYDataset createDataset(JSONObject obj1, JSONObject obj2, JSONObject obj3) {

        TimeSeries timeseries1 = new TimeSeries("普涨数据", Day.class);

        String[] years1={"2015","2015","2015","2016","2016","2016","2016","2016","2016","2016","2016","2016","2016","2016"};
        String[] months1={"10","11","12","1","2","3","4","5","6","7","8","9","10","11"};
        String[]   days1={"1","1","1","1","1","1","1","1","1","1","1","1","1","1"};
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

        TimeSeries timeseries2 = new TimeSeries("全时序数据", Day.class);

        String[] years2={"2015","2015","2015","2016","2016","2016","2016","2016","2016","2016","2016","2016","2016","2016"};
        String[] months2={"10","11","12","1","2","3","4","5","6","7","8","9","10","11"};
        String[]   days2={"1","1","1","1","1","1","1","1","1","1","1","1","1","1"};
        for(int i=0;i<months2.length;i++){

            String date2=years2[i]+"-"+months2[i];
            if(obj2.containsKey(date2)){
                Double Price=obj2.getDouble(date2);
                int year= Integer.parseInt(years2[i]);
                int month= Integer.parseInt(months2[i]);
                int day= Integer.parseInt(days2[i]);
                timeseries2.addOrUpdate(new Day(day,month,year), Price);
            }

        }

        TimeSeries timeseries3 = new TimeSeries("累积增长数据", Day.class);

        String[] years3={"2015","2015","2015","2016","2016","2016","2016","2016","2016","2016","2016","2016","2016","2016"};
        String[] months3={"10","11","12","1","2","3","4","5","6","7","8","9","10","11"};
        String[]   days3={"1","1","1","1","1","1","1","1","1","1","1","1","1","1"};
        for(int i=0;i<months3.length;i++){

            String date3=years3[i]+"-"+months3[i];
            if(obj3.containsKey(date3)){
                Double Price=obj3.getDouble(date3);
                int year= Integer.parseInt(years3[i]);
                int month= Integer.parseInt(months3[i]);
                int day= Integer.parseInt(days3[i]);
                timeseries3.addOrUpdate(new Day(day,month,year), Price);
            }

        }

        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(timeseries1);
        timeseriescollection.addSeries(timeseries2);
        timeseriescollection.addSeries(timeseries3);
       
        return timeseriescollection;
    }
  public static ChartPanel getChartPanel(){
        return frame;

    }
}
