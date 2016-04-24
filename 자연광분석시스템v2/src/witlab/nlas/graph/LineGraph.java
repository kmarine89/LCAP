package witlab.nlas.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.text.SimpleDateFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ShapeUtilities;

import witlab.nlas.db.DataArray;

public class LineGraph {

	JFreeChart chart;
	ChartPanel chartPanel;
	String titleName;
	String xAxisName;
	String yAxisName;
	int width = 400, height = 300;
	double yAxisMin = 0, yAxisMax = 0;
	
	int[] colorPattern = new int[] {
		0xFF0000, 0xFF9900, 0x99CC00, 0x009900, 0x00CC66, 0x999900,
		0x00CCFF, 0x0066FF, 0x3333CC, 0x6600FF, 0xCC00FF, 0xFF66CC
	};

	TimeSeriesCollection dataset = new TimeSeriesCollection();

	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}

	public void setXAxisName(String xAxisName) {
		this.xAxisName = xAxisName;
	}

	public void setYAxisName(String yAxisName) {
		this.yAxisName = yAxisName;
	}

	public void setYAxisRange(double min, double max) {
		yAxisMin = min;
		yAxisMax = max;
	}
	
	public ChartPanel getResizeGraph(int width, int height) {
		chartPanel.setPreferredSize(new Dimension(width, height));
		return chartPanel;
	}

	public ChartPanel getLineGraph() {
		chart = ChartFactory.createTimeSeriesChart(titleName, xAxisName, yAxisName, dataset);
		chart.getLegend().setPosition(RectangleEdge.RIGHT);
		
		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(3.0, 3.0, 3.0, 3.0));	//TOP, RIGHT, BOTTOM, LEFT
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		
		XYItemRenderer renderer = plot.getRenderer();
		for (int i = 0; i < colorPattern.length; i++) {
			renderer.setSeriesPaint(i, new Color(colorPattern[i]));
		}
		
		if(yAxisMin != 0 && yAxisMax != 0)
			plot.getRangeAxis().setRange(yAxisMin, yAxisMax);
		((DateAxis) plot.getDomainAxis()).setDateFormatOverride(new SimpleDateFormat("HH:mm"));
		
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(width, height));
		return chartPanel;
	}
	
	public ChartPanel getDotGraph() {
		chart = ChartFactory.createTimeSeriesChart(titleName, xAxisName, yAxisName, dataset);
		chart.getLegend().setPosition(RectangleEdge.RIGHT);
		
		XYPlot plot = chart.getXYPlot();
		plot.setAxisOffset(new RectangleInsets(3.0, 3.0, 3.0, 3.0));	//TOP, RIGHT, BOTTOM, LEFT
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
		for (int i = 0; i < 100; i++) {
			renderer.setSeriesLinesVisible(i, false);
			renderer.setSeriesShape(i, ShapeUtilities.createDiamond(1.0f));
			renderer.setSeriesShapesVisible(i, true);
		}
		for (int i = 0; i < colorPattern.length; i++) {
			renderer.setSeriesPaint(i, new Color(colorPattern[i]));
		}
		chart.getXYPlot().setRenderer(renderer);
		
		if(yAxisMin != 0 && yAxisMax != 0)
			plot.getRangeAxis().setRange(yAxisMin, yAxisMax);
		((DateAxis) plot.getDomainAxis()).setDateFormatOverride(new SimpleDateFormat("HH:mm"));
		
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(width, height));
		return chartPanel;
	}
	
	/**
	 * String[] str 이라 할때
	 * str[0]은 HH:mm 에서 HH:mm만 추출
	 * str[1]은 double형 데이터 값
	 */
	public XYDataset addDataset(DataArray data) {
		TimeSeries series = new TimeSeries(data.getName());
		for (String[] item : data.getData()) {
			int hour = Integer.parseInt(item[0].split(":")[0]);
			int minute = Integer.parseInt(item[0].split(":")[1]);
			Double value;
			if("".equals(item[1]))	value = null;
			else if("NA".equals(item[1]))	value = null;
			else	value = Double.parseDouble(item[1]);
			series.add(new Minute(minute, hour, 1, 1, 1901), value);
		}
		dataset.addSeries(series);
		return dataset;
	}
	
}
