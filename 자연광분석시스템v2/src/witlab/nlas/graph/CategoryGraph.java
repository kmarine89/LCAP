package witlab.nlas.graph;

import java.awt.Color;
import java.awt.Dimension;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ShapeUtilities;

import witlab.nlas.db.DataArray;

public class CategoryGraph {

	JFreeChart chart;
	ChartPanel chartPanel;
	String titleName;
	String xAxisName;
	String yAxisName;
	int width = 400, height = 300;
	
	int[] colorPattern = new int[] {
		0xFF0000, 0xFF9900, 0x99CC00, 0x009900, 0x00CC66, 0x999900,
		0x00CCFF, 0x0066FF, 0x3333CC, 0x6600FF, 0xCC00FF, 0xFF66CC
	};
	
	DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	
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
	
	public ChartPanel getGraph() {
		chart = ChartFactory.createLineChart(titleName, xAxisName, yAxisName, dataset);
		chart.getLegend().setPosition(RectangleEdge.BOTTOM);
		
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setAxisOffset(new RectangleInsets(3.0, 3.0, 3.0, 3.0));	//TOP, RIGHT, BOTTOM, LEFT
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		CategoryItemRenderer renderer = plot.getRenderer();
		for (int i = 0; i < 100; i++) {
			renderer.setSeriesShape(i, ShapeUtilities.createDiamond(1.0f));
		}
		for (int i = 0; i < colorPattern.length; i++) {
			renderer.setSeriesPaint(i, new Color(colorPattern[i]));
		}
		
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(width, height));
		return chartPanel;
	}
	
	/**
	 * String[] str 이라 할때
	 * str[0]은 카테고리 이름
	 * str[1]는 더블형 데이터
	 */
	public void addDataset(DataArray data) {
		String seriesName = data.getName();
		data.printData();
		for (String[] item : data.getData()) {
			Double value;
			if("".equals(item[1])) value = null;
			else if("NA".equals(item[1])) value = null;
			else value = Double.parseDouble(item[1]);
			dataset.addValue(value, seriesName, item[0]);
		}
	}
	
}
