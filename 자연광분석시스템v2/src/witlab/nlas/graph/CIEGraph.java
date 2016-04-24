package witlab.nlas.graph;

import java.awt.Dimension;

import javax.swing.ImageIcon;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ShapeUtilities;

import witlab.nlas.db.DataArray;

public class CIEGraph {

	JFreeChart chart;
	ChartPanel chartPanel;
	String titleName;
	String xAxisName;
	String yAxisName;
	int width = 400, height = 300;

	XYSeriesCollection dataset = new XYSeriesCollection();

	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}

	public void setXAxisName(String xAxisName) {
		this.xAxisName = xAxisName;
	}

	public void setYAxisName(String yAxisName) {
		this.yAxisName = yAxisName;
	}

	public ChartPanel getResizeGraph(int width, int height) {
		chartPanel.setPreferredSize(new Dimension(width, height));
		return chartPanel;
	}

	public ChartPanel getGraph1931L() {
		chart = ChartFactory.createScatterPlot(titleName, xAxisName, yAxisName, dataset);
		chart.getLegend().setPosition(RectangleEdge.BOTTOM);
		
		XYPlot plot = chart.getXYPlot();
		plot.setAxisOffset(new RectangleInsets(3.0, 3.0, 3.0, 3.0));	//TOP, RIGHT, BOTTOM, LEFT
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		plot.getDomainAxis().setRange(0.0, 0.8);
		plot.getRangeAxis().setRange(0.0, 0.9);
		
		XYItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesShape(0, ShapeUtilities.createDownTriangle(2.0f));
        
        ImageIcon image = new ImageIcon(".\\image\\CIE1931(1).jpg");
        plot.setBackgroundImage(image.getImage());
        plot.setBackgroundPaint(null);
        
        chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(width, height));
		return chartPanel;
	}

	public ChartPanel getGraph1931S() {
		chart = ChartFactory.createScatterPlot(titleName, xAxisName, yAxisName, dataset);
		chart.getLegend().setPosition(RectangleEdge.BOTTOM);
		
		XYPlot plot = chart.getXYPlot();
		plot.setAxisOffset(new RectangleInsets(3.0, 3.0, 3.0, 3.0));	//TOP, RIGHT, BOTTOM, LEFT
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		plot.getDomainAxis().setRange(0.2, 0.55);
		plot.getRangeAxis().setRange(0.2, 0.5);
		
		XYItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesShape(0, ShapeUtilities.createDownTriangle(2.0f));
        
        ImageIcon image = new ImageIcon(".\\image\\CIE1931(2).jpg");
        plot.setBackgroundImage(image.getImage());
        plot.setBackgroundPaint(null);
        
        chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(width, height));
		return chartPanel;
	}
	
	public ChartPanel getGraph1976() {
		chart = ChartFactory.createScatterPlot(titleName, xAxisName, yAxisName, dataset);
		chart.getLegend().setPosition(RectangleEdge.BOTTOM);
		
		XYPlot plot = chart.getXYPlot();
		plot.setAxisOffset(new RectangleInsets(3.0, 3.0, 3.0, 3.0));	//TOP, RIGHT, BOTTOM, LEFT
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		plot.getDomainAxis().setRange(0.0, 0.6);
		plot.getRangeAxis().setRange(0.0, 0.6);
		
		XYItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesShape(0, ShapeUtilities.createDownTriangle(2.0f));
        
        ImageIcon image = new ImageIcon(".\\image\\CIE1976.jpg");
        plot.setBackgroundImage(image.getImage());
        plot.setBackgroundPaint(null);
        
        chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(width, height));
		return chartPanel;
	}

	/**
	 * String[] str 이라 할때
	 * str[0]은 YYYY-MM-dd
	 * str[1]은 HH:mm
	 * str[2]은 x좌표
	 * str[3]는 y좌표
	 */
	public XYDataset addDataset(DataArray data) {
		XYSeries series = new XYSeries(data.getItem(0, 0));
		for (String[] item : data.getData()) {
			Double x, y;
			if("".equals(item[2]) || "NA".equals(item[2])) 	x = y = -99.9;
			else		x = Double.parseDouble(item[2]);
			if("".equals(item[3]) || "NA".equals(item[3]))	x = y = -99.9;
			else		y = Double.parseDouble(item[3]);
			series.add(x, y);
		}
		dataset.addSeries(series);
		return dataset;
	}
	
}
