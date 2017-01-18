package com.svail.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import java.awt.Font;
import java.awt.GridLayout;
public class CurveDrawing {
	public static void main(String[] args){
		 
		JFrame frame=new JFrame("Java数据统计图");
	    frame.setLayout(new GridLayout(2,2,10,10));
		frame.add(new CurveDrawing().getChartPanel2());
	    frame.setBounds(50, 50, 800, 600);
	    frame.setVisible(true);
	}
	public static ChartPanel frame1;
	public static ChartPanel frame2;
	public static ChartPanel frame3;
	public static ChartPanel frame;
	public  CurveDrawing(){
	    	/*
	    	 * 绘制柱状图
	    	 */
	        CategoryDataset dataset = getDataSet();
	        JFreeChart chart = ChartFactory.createBarChart3D(
	                             "水果", // 图表标题
	                            "水果种类", // 目录轴的显示标签
	                            "数量", // 数值轴的显示标签
	                            dataset, // 数据集
	                            PlotOrientation.VERTICAL, // 图表方向：水平、垂直
	                            true,           // 是否显示图例(对于简单的柱状图必须是false)
	                            false,          // 是否生成工具
	                            false           // 是否生成URL链接
	                            );
	         
	        //从这里开始
	        CategoryPlot plot=chart.getCategoryPlot();//获取图表区域对象
	        CategoryAxis domainAxis=plot.getDomainAxis();         //水平底部列表
	        domainAxis.setLabelFont(new Font("黑体",Font.BOLD,14));         //水平底部标题
	        domainAxis.setTickLabelFont(new Font("宋体",Font.BOLD,12));  //垂直标题
	        ValueAxis rangeAxis=plot.getRangeAxis();//获取柱状
	        rangeAxis.setLabelFont(new Font("黑体",Font.BOLD,15));
	        chart.getLegend().setItemFont(new Font("黑体", Font.BOLD, 15));
	        chart.getTitle().setFont(new Font("宋体",Font.BOLD,20));//设置标题字体
	           
	          //到这里结束，虽然代码有点多，但只为一个目的，解决汉字乱码问题
	           
	         frame1=new ChartPanel(chart,true);        //这里也可以用chartFrame,可以直接生成一个独立的Frame
	         
	         
	         /*
	          * 绘制饼状图 
	          */
	         DefaultPieDataset data = getDataSet1();
	         JFreeChart chart1 = ChartFactory.createPieChart3D("水果产量",data,true,false,false);
	        //设置百分比
	          PiePlot pieplot = (PiePlot) chart1.getPlot();
	          DecimalFormat df = new DecimalFormat("0.00%");//获得一个DecimalFormat对象，主要是设置小数问题
	          NumberFormat nf = NumberFormat.getNumberInstance();//获得一个NumberFormat对象
	          StandardPieSectionLabelGenerator sp1 = new StandardPieSectionLabelGenerator("{0}  {2}", nf, df);//获得StandardPieSectionLabelGenerator对象
	          pieplot.setLabelGenerator(sp1);//设置饼图显示百分比
	       
	         //没有数据的时候显示的内容
	          pieplot.setNoDataMessage("无数据显示");
	          pieplot.setCircular(false);
	          pieplot.setLabelGap(0.02D);
	       
	          pieplot.setIgnoreNullValues(true);//设置不显示空值
	          pieplot.setIgnoreZeroValues(true);//设置不显示负值
	          frame2=new ChartPanel (chart1,true);
	          chart1.getTitle().setFont(new Font("宋体",Font.BOLD,20));//设置标题字体
	          PiePlot piePlot= (PiePlot) chart1.getPlot();//获取图表区域对象
	          piePlot.setLabelFont(new Font("宋体",Font.BOLD,10));//解决乱码
	          chart1.getLegend().setItemFont(new Font("黑体",Font.BOLD,10));
	          
	          /*
	           * 绘制折线图
	           */
	          XYDataset xydataset = createDataset();
	          JFreeChart jfreechart = ChartFactory.createTimeSeriesChart("Legal & General单位信托基金价格", "日期", "价格",xydataset, true, true, true);
	          XYPlot xyplot = (XYPlot) jfreechart.getPlot();
	          DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();
	          dateaxis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
	          frame3=new ChartPanel(jfreechart,true);
	          dateaxis.setLabelFont(new Font("黑体",Font.BOLD,14));         //水平底部标题
	          dateaxis.setTickLabelFont(new Font("宋体",Font.BOLD,12));  //垂直标题
	          ValueAxis rangeAxis1=xyplot.getRangeAxis();//获取柱状
	          rangeAxis1.setLabelFont(new Font("黑体",Font.BOLD,15));
	          jfreechart.getLegend().setItemFont(new Font("黑体", Font.BOLD, 15));
	          jfreechart.getTitle().setFont(new Font("宋体",Font.BOLD,20));//设置标题字体
	          
	    }

	private static CategoryDataset getDataSet() {
	           DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	           dataset.addValue(100, "北京", "苹果");
	           dataset.addValue(100, "上海", "苹果");
	           dataset.addValue(100, "广州", "苹果");
	           dataset.addValue(200, "北京", "梨子");
	           dataset.addValue(200, "上海", "梨子");
	           dataset.addValue(200, "广州", "梨子");
	           dataset.addValue(300, "北京", "葡萄");
	           dataset.addValue(300, "上海", "葡萄");
	           dataset.addValue(300, "广州", "葡萄");
	           dataset.addValue(400, "北京", "香蕉");
	           dataset.addValue(400, "上海", "香蕉");
	           dataset.addValue(400, "广州", "香蕉");
	           dataset.addValue(500, "北京", "荔枝");
	           dataset.addValue(500, "上海", "荔枝");
	           dataset.addValue(500, "广州", "荔枝");
	           return dataset;
	}
	public static ChartPanel getChartPanel(){
	    return frame1;
	     
	}
	private static DefaultPieDataset getDataSet1() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("苹果",100);
        dataset.setValue("梨子",200);
        dataset.setValue("葡萄",300);
        dataset.setValue("香蕉",400);
        dataset.setValue("荔枝",500);
        return dataset;
}
    public static ChartPanel getChartPanel1(){
        return frame2;
         
    }
    
    private static XYDataset createDataset() {  //这个数据集有点多，但都不难理解
        TimeSeries timeseries = new TimeSeries("legal & general欧洲指数信任",
                org.jfree.data.time.Month.class);
        timeseries.add(new Month(2, 2001), 181.80000000000001D);
        timeseries.add(new Month(3, 2001), 167.30000000000001D);
        timeseries.add(new Month(4, 2001), 153.80000000000001D);
        timeseries.add(new Month(5, 2001), 167.59999999999999D);
        timeseries.add(new Month(6, 2001), 158.80000000000001D);
        timeseries.add(new Month(7, 2001), 148.30000000000001D);
        timeseries.add(new Month(8, 2001), 153.90000000000001D);
        timeseries.add(new Month(9, 2001), 142.69999999999999D);
        timeseries.add(new Month(10, 2001), 123.2D);
        timeseries.add(new Month(11, 2001), 131.80000000000001D);
        timeseries.add(new Month(12, 2001), 139.59999999999999D);
        timeseries.add(new Month(1, 2002), 142.90000000000001D);
        timeseries.add(new Month(2, 2002), 138.69999999999999D);
        timeseries.add(new Month(3, 2002), 137.30000000000001D);
        timeseries.add(new Month(4, 2002), 143.90000000000001D);
        timeseries.add(new Month(5, 2002), 139.80000000000001D);
        timeseries.add(new Month(6, 2002), 137D);
        timeseries.add(new Month(7, 2002), 132.80000000000001D);
        TimeSeries timeseries1 = new TimeSeries("legal & general英国指数信任",
                org.jfree.data.time.Month.class);
        timeseries1.add(new Month(2, 2001), 129.59999999999999D);
        timeseries1.add(new Month(3, 2001), 123.2D);
        timeseries1.add(new Month(4, 2001), 117.2D);
        timeseries1.add(new Month(5, 2001), 124.09999999999999D);
        timeseries1.add(new Month(6, 2001), 122.59999999999999D);
        timeseries1.add(new Month(7, 2001), 119.2D);
        timeseries1.add(new Month(8, 2001), 116.5D);
        timeseries1.add(new Month(9, 2001), 112.7D);
        timeseries1.add(new Month(10, 2001), 101.5D);
        timeseries1.add(new Month(11, 2001), 106.09999999999999D);
        timeseries1.add(new Month(12, 2001), 110.3D);
        timeseries1.add(new Month(1, 2002), 111.7D);
        timeseries1.add(new Month(2, 2002), 111D);
        timeseries1.add(new Month(3, 2002), 109.59999999999999D);
        timeseries1.add(new Month(4, 2002), 113.2D);
        timeseries1.add(new Month(5, 2002), 111.59999999999999D);
        timeseries1.add(new Month(6, 2002), 108.8D);
        timeseries1.add(new Month(7, 2002), 101.59999999999999D);
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(timeseries);
        timeseriescollection.addSeries(timeseries1);
        return timeseriescollection;
    }
  public static ChartPanel getChartPanel2(){
        return frame3;
         
    }
    
	public static XYDataset CurveDraw(){
		//访问量统计时间线
		TimeSeries timeSeries=new TimeSeries("价格统计",Month.class);
		//时间区县数据集合
		TimeSeriesCollection lineDataset=new TimeSeriesCollection();
		
		//构造数据集合
		timeSeries.add(new Month(1,2007),11200);
		timeSeries.add(new Month(2,2007),9000);
		timeSeries.add(new Month(3,2007),11000);
		timeSeries.add(new Month(4,2007),10000);
		timeSeries.add(new Month(5,2007),9000);
		timeSeries.add(new Month(6,2007),11300);
		timeSeries.add(new Month(7,2007),11400);
		timeSeries.add(new Month(8,2007),11500);
		timeSeries.add(new Month(9,2007),8000);
		timeSeries.add(new Month(10,2007),7500);
		
		lineDataset.addSeries(timeSeries);
		JFreeChart chart=ChartFactory.createTimeSeriesChart("访问量统计时间线","月份","访问量",lineDataset,true,true,true);;
		
		//设置子标题
		TextTitle subtitle=new TextTitle("2007年度",new Font("黑体",Font.BOLD,10));
		chart.addSubtitle(subtitle);
		
		//设置主标题
		chart.setTitle(new TextTitle("价格统计", new Font("隶书", Font.ITALIC, 15)));
		chart.setAntiAlias(true);
		return lineDataset;
	}
	public static ChartPanel getCurve(){
        return frame;
         
    }

}
