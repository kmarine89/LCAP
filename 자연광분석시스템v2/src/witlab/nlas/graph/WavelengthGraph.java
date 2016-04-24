package witlab.nlas.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileReader;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

public class WavelengthGraph {

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
	
	XYSeriesCollection dataset = new XYSeriesCollection();
	
	double[] nm;
	
	public WavelengthGraph() {
		try {
			FileReader fr = new FileReader("txt\\nmNum.txt");
			BufferedReader br = new BufferedReader(fr);
			String[] temp = br.readLine().split(",");
			nm = new double[temp.length];
			for (int i = 0; i < temp.length; i++) {
				nm[i] = Double.parseDouble(temp[i]);
			}
			br.close();
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
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
		chart = ChartFactory.createScatterPlot(titleName, xAxisName, yAxisName, dataset);
		chart.getLegend().setPosition(RectangleEdge.RIGHT);
		
		XYPlot plot = chart.getXYPlot();
		plot.setAxisOffset(new RectangleInsets(3.0, 3.0, 3.0, 3.0));	//TOP, RIGHT, BOTTOM, LEFT
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		
		XYItemRenderer renderer = plot.getRenderer();
		for (int i = 0; i < colorPattern.length; i++) {
			renderer.setSeriesPaint(i, new Color(colorPattern[i]));
		}
		
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(width, height));
		return chartPanel;
	}

	/**
	 * String[] str 이라 할때
	 * str[0]은 YYYY-MM-dd
	 * str[1]은 HH:mm
	 * str[2]은 SPD 처음
	 * str[3]는 SPD 끝
	 */
	public void addDataset(String[] row) {
		XYSeries series = new XYSeries(row[1]);
		for (int i = 2; i < row.length; i++) {
			series.add(nm[i-2], Double.parseDouble(row[i]));
		}
		dataset.addSeries(series);
	}
	
	public void removeAll() {
		dataset.removeAllSeries();
	}
	
}
