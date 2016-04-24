package witlab.nlas.graph;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
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

public class LineGraphForExcel {

	JFreeChart chart;
	ChartPanel chartPanel;
	String titleName;
	String xAxisName;
	String yAxisName;
	int width = 400, height = 300;
	double yAxisMin = 0, yAxisMax = 0;

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
	
	public void drawLineGraph() {
		chart = ChartFactory.createTimeSeriesChart(titleName, xAxisName, yAxisName, dataset);
		chart.getLegend().setPosition(RectangleEdge.BOTTOM);
		chart.getXYPlot().getRangeAxis().setRange(yAxisMin, yAxisMax);
		
		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(3.0, 3.0, 3.0, 3.0));	//TOP, RIGHT, BOTTOM, LEFT
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
		renderer.setSeriesPaint(0, Color.red);
		renderer.setSeriesPaint(1, Color.green);
		renderer.setSeriesPaint(2, Color.blue);
		renderer.setSeriesLinesVisible(0, false);
		renderer.setSeriesLinesVisible(1, false);
		renderer.setSeriesLinesVisible(2, false);
		renderer.setSeriesShape(0, ShapeUtilities.createDiamond(1.0f));
		renderer.setSeriesShape(1, ShapeUtilities.createDiamond(1.0f));
		renderer.setSeriesShape(2, ShapeUtilities.createDiamond(1.0f));
		renderer.setSeriesShapesVisible(0, true);
		renderer.setSeriesShapesVisible(1, true);
		renderer.setSeriesShapesVisible(2, true);
		chart.getXYPlot().setRenderer(renderer);
	}
	
	public void drawAreaGraph() {
		chart = ChartFactory.createTimeSeriesChart(titleName, xAxisName, yAxisName, dataset);
		chart.getLegend().setPosition(RectangleEdge.BOTTOM);
		chart.getXYPlot().getRangeAxis().setRange(yAxisMin, yAxisMax);
		
		XYPlot plot = chart.getXYPlot();
		plot.setAxisOffset(new RectangleInsets(3.0, 3.0, 3.0, 3.0));	//TOP, RIGHT, BOTTOM, LEFT
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		
		XYItemRenderer renderer = new XYAreaRenderer();
		renderer.setSeriesPaint(0, new Color(0, 0, 255, 108));
		renderer.setSeriesPaint(1, new Color(0, 255, 0, 108));
		renderer.setSeriesPaint(2, new Color(255, 0, 0, 108));
		chart.getXYPlot().setRenderer(renderer);
	}
	
	public void drawDotGraph() {
		chart = ChartFactory.createTimeSeriesChart(titleName, xAxisName, yAxisName, dataset);
		chart.getLegend().setPosition(RectangleEdge.BOTTOM);
		chart.getXYPlot().getRangeAxis().setRange(yAxisMin, yAxisMax);
		
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
	}
	
	/**
	 * @return 엑셀용 그래프 (바이트 배열로 반환)
	 */
	public byte[] toByteArray() {
		BufferedImage bufferedImage = chart.createBufferedImage(600, 500);
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		try {
			ImageIO.write(bufferedImage, "png", byteOutput);
			byteOutput.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return byteOutput.toByteArray();
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
