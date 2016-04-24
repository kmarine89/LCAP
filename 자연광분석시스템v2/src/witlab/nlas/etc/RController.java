package witlab.nlas.etc;

import java.text.DecimalFormat;

import org.rosuda.JRI.RVector;
import org.rosuda.JRI.Rengine;

import witlab.nlas.db.DataArray;
import witlab.nlas.db.DataList;
import witlab.nlas.ui.FrameEditGraph;

/**
 * R ����� ���⿡ ��� ����
 * Run �Ҷ� �̷��� �����Ͻÿ�...
 * VM arguments : -Djava.library.path=C:\R-3.1.2\library\rJava\jri\i386
 * Environment (Variable : PATH, Value : C:\R-3.1.2\bin\i386; C:\R-3.1.2\library\rJava\jri\i386
 * @author ����
 */
public class RController {

	private FrameEditGraph editGraph;
	private Rengine r;
	private static RController instance;
	
	public static RController getInstance() {
		if(instance == null)
			instance = new RController();
		return instance;
	}
	
	private RController() {
		r = new Rengine(null, false, null);
		editGraph = FrameEditGraph.getInstance();
	}

	/**
	 * R���� ���͸� �����Ҷ� ���� �κ��� NA �ٲپ��� �ʿ䰡 ����
	 * @param paramdata (������ ���Ե�...)
	 * @return data (���� -> NA)
	 * @since 2015-10-01
	 */
	private DataArray changeZeroToNA(DataArray paramdata) {
		DataArray data = new DataArray();
		for (String[] row : paramdata.getData()) {
			for (int i = 0; i < row.length; i++) {
				if(row[i] == null) 
					continue;
				if("".equals(row[i]))
					row[i] = "NA";
			}
			data.add(row);
		}
		return data;
	}
	
	/**
	 * R�� ���� ���͸� �����ϴ� �޼���
	 * @param field
	 * @param data
	 */
	private void createVector(String[] field, String[][] data) {
		r.eval("rm(list=ls())");	// R �ʱ�ȭ
		String[] vectorStr = new String[field.length-1];	//date�� time�� �ϳ��� ��ġ�Ƿ� -1
		for (int i = 0; i < vectorStr.length; i++) {
			vectorStr[i] = field[i+1]+"=c(";
			for (int j = 1; j < data.length; j++) {
				if(i == 0) {
					if(j == data.length-1) {
						vectorStr[i] += "strptime('"+data[j][0]+" "+data[j][1]+"','%Y-%m-%d %H:%M:%S'))";
					} else {
						vectorStr[i] += "strptime('"+data[j][0]+" "+data[j][1]+"','%Y-%m-%d %H:%M:%S'),";
					}
				} else {
					if(j == data.length-1) {
						vectorStr[i] += data[j][i+1]+")";
					} else {
						vectorStr[i] += data[j][i+1]+",";
					}
				}
			}
			r.eval(vectorStr[i]);
		}
	}
	
	/**
	 * @param paramfield
	 * @param data
	 * @return whisker�� ���Ѱ� ���� 
	 */
	public double[][] getWhisker(DataList paramfield, DataArray data) {
		data = changeZeroToNA(data);	// ���� �κ��� NA�� ����
		createVector(paramfield.toArray(), data.toSquareArray());	// R�� �� �����ͺ� ���� ����
		
		/**
		 * Rawdata�� Summary�� ���������...
		 * Comparison ��ɿ��� field.length-2 ���� �����
		 */
		String[] field = paramfield.toArray();
		double[][] values = new double[field.length-2][2];
		for (int i = 0; i < values.length; i++) {
			values[i][0] = r.eval("quantile(na.omit("+field[i+2]+"), 0.25)").asDouble()-r.eval("IQR(na.omit("+field[i+2]+"))").asDouble()*1.5;
			values[i][1] = r.eval("quantile(na.omit("+field[i+2]+"), 0.75)").asDouble()+r.eval("IQR(na.omit("+field[i+2]+"))").asDouble()*1.5;
		}
		return values;
	}
	
	public void createGraphOfSummary(String imagePath, DataList field, DataArray data) {
		data = changeZeroToNA(data);	// ���� �κ��� NA�� ����
		createVector(field.toArray(), data.toSquareArray());	// R�� �� �����ͺ� ���� ����
		
		// Boxplot�� �׸��� ���� ������������ �̹Ƿ� Date ,Time(�ð�)�� ���� �ʱ� ���� 2 ����...
		String[] field2 = field.toArray();
		String dataframeStr = "table=data.frame(";
		for (int i = 2; i < field2.length; i++) {
			if(i == field2.length-1) {
				dataframeStr += field2[i]+")"; 
			} else {
				dataframeStr += field2[i]+","; 
			}
		}
		r.eval(dataframeStr);
		
		// ������ �����������Ӹ� ����Ͽ� �׷��� ����
		r.eval("jpeg('"+imagePath+"')");
		r.eval("boxplot(table, ylab='"+editGraph.getYaxisName()+"', xlab='"+editGraph.getXaxisName()+"', main='"+editGraph.getTitleName()+"')");
		r.eval("dev.off()");
	}

	public void createGraphOfCorrelation(String imagePath, DataList paramfield, DataArray data) {
		data = changeZeroToNA(data);	// ���� �κ��� NA�� ����
		createVector(paramfield.toArray(), data.toSquareArray());	// R�� �� �����ͺ� ���� ����
		
		// corrplot�� �׸��� ���� ������������ �̹Ƿ� Date ,Time(�ð�)�� ���� �ʱ� ���� 2 ����...
		String[] field = paramfield.toArray();
		String dataframeStr = "table=data.frame(";
		for (int i = 2; i < field.length; i++) {
			if(i == field.length-1) {
				dataframeStr += field[i]+")"; 
			} else {
				dataframeStr += field[i]+","; 
			}
		}
		r.eval(dataframeStr);
		
		// ������ �����������ӿ� ���� cor �޼��带 �����Ͽ� ������ ���� (R ���ο���)
		r.eval("corArr=cor(na.omit(table))");
		// ������ cor �� ����� ������ ���Ͽ� corrplot �׷��� ����
		r.eval("library(corrplot)");
		r.eval("jpeg('"+imagePath+"')");
		r.eval("corrplot(corArr)");
		r.eval("dev.off()");
	}

	/**
	 * Summary ���������ӿ��� �ٷ� JTable�� ���밡���� ������ �迭�� ����
	 * R ���ο��� ������ ��� �����Ͱ� NA �ϰ�� NullPointException�� �߻��ϹǷ� ���� ó�����־���
	 * @param field
	 * @param data
	 * @return Summary���� ���� String[][]
	 * @since 2015-10-01
	 */
	public String[][] getSummary(DataList field, DataArray data) {
		data = changeZeroToNA(data);	// ���� �κ��� NA�� ����
		createVector(field.toArray(), data.toSquareArray());	// R�� �� �����ͺ� ���� ����
		
		// ������ ���Ϳ� ���� Summary �����Ͽ� double 2�����迭�� ����
		String[] field2 = field.toArray();
		double[][] summaryData = new double[field2.length-2][6];
		for (int i = 2; i < field2.length; i++) {
			try {
				summaryData[i-2] = r.eval("summary("+field2[i]+")").asDoubleArray();
			} catch (NullPointerException e) {}
		}
		
		// UI Table�� ����ϱ����� �뵵�� ���ڿ� �迭�� ��ȯ
		String[][] summaryData2 = new String[field2.length-2][7];
		for (int i = 0; i < summaryData2.length; i++) {
			summaryData2[i][0] = field2[i+2];
			for (int j = 1; j < summaryData2[0].length; j++) {
				try{
					summaryData2[i][j] = String.valueOf(summaryData[i][j-1]);
				} catch (NullPointerException e) {
					summaryData2[i][j] = "";
				}
			}
		}
		return summaryData2;
	}

	public String[][] getCorrelation(DataList paramfield, DataArray data) {
		data = changeZeroToNA(data);	// ���� �κ��� NA�� ����
		createVector(paramfield.toArray(), data.toSquareArray());	// R�� �� �����ͺ� ���� ����
		
		// cor �޼��带 ������ ������������ �̹Ƿ� Date ,Time(�ð�)�� ���� �ʱ� ���� 2 ����...
		String[] field = paramfield.toArray();
		String dataframeStr = "table=data.frame(";
		for (int i = 2; i < field.length; i++) {
			if(i == field.length-1) {
				dataframeStr += field[i]+")"; 
			} else {
				dataframeStr += field[i]+","; 
			}
		}
		r.eval(dataframeStr);
		// na.omit�� �߰��ϸ� ���� NA�� �� ���Ͱ� �ϳ��� ������ cor ��� ��δ� NaN�� �ǹ�����...
		// na.omit�� ���� ������.. ���Ϳ� NA�� �Ѱ��� �־ �� ���� ���� cor ����� �ȳ���...
		// �ΰ��� ������ ������ �����ٵ�... ���� 2015-10-02
		double[][] correlationData = r.eval("cor(na.omit(table))").asDoubleMatrix();
		String[][] correlationData2 = new String[field.length-1][field.length-1];
		DecimalFormat format = new DecimalFormat("#.#####");
		correlationData2[0][0] = "";
		for (int i = 1; i < correlationData2.length; i++) {
			correlationData2[i][0] = field[i+1];
			correlationData2[0][i] = field[i+1];
			for (int j = 1; j < correlationData2[0].length; j++) {
				if(Double.isNaN(correlationData[i-1][j-1])) correlationData2[i][j] = "NaN";
				else	correlationData2[i][j] = format.format(correlationData[i-1][j-1]); 
			}
		}
		return correlationData2;
	}

	public String[][] getRegression(DataList field, DataArray data) {
		data = changeZeroToNA(data);	// ���� �κ��� NA�� ����
		createVector(field.toArray(), data.toSquareArray());	// R�� �� �����ͺ� ���� ����
		
		// ȸ�� Summary�� ������� �ϹǷ�... Date, Time�� ���� �ʱ� ���� 2����...
		String temp = "";
		String[] field2 = field.toArray();
		for (int i = 2; i < field2.length; i++) {
			if(i == field2.length-1)		temp += field2[i];
			else if(i == 2)					temp += field2[i]+"~";
			else									temp += field2[i]+"+";
		}
		
		RVector lineValues = r.eval("summary(lm("+temp+"))").asVector();
		String[] values = new String[5];
		values[0] = lineValues.get(3).toString();	//ȸ�ͽ� ����
		values[1] = lineValues.get(5).toString();	//���Ĵٵ� ����
		values[2] = lineValues.get(6).toString();
		values[3] = lineValues.get(7).toString();	//��Ƽ�� R ������
		values[4] = lineValues.get(8).toString();	//�������Ƽ�� R ������
		for (int i = 0; i < values.length; i++) {
			if(i == 2)
				values[i] = values[i].substring(7, values[i].length()-2);
			else
				values[i] = values[i].substring(8, values[i].length()-2);
		}
		String[] linear = values[0].split(",");
		String[][] matrix = new String[4][linear.length/4];
		String standardError = values[1];
		String degree = values[2].split(",")[1];
		String multipleR2 = values[3];
		String adjustedR2 = values[4];
		int k = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				matrix[i][j] = linear[k];
				k++;
			}
		}
		DecimalFormat format = new DecimalFormat("#.#####");
		// 0~4�� ȸ�ͽ� ���� (matrix)
		// 5�� �����
		// 6, 7�� ���Ĵٵ� ����
		// 8�� �����
		// 9, 10�� R ������
		String[][] lmSum = new String[11][matrix[0].length+1];
		for (int i = 0; i < lmSum.length; i++) {
			for (int j = 0; j < lmSum[0].length; j++) {
				lmSum[i][j] = "";
			}
		}
		lmSum[0][1] = "(Intercept)";
		for (int i = 3; i < field2.length; i++) {
			lmSum[0][i-1] = field2[i];
		}
		lmSum[1][0] = "Estimate Std.";
		lmSum[2][0] = "Error";
		lmSum[3][0] = "t value";
		lmSum[4][0] = "Pr(>|t|)";
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				lmSum[i+1][j+1] = format.format(Double.parseDouble(matrix[i][j]));
			}
		}
		lmSum[6][0] = "Residual standard error : ";
		lmSum[7][0] = format.format(Double.parseDouble(standardError))+" on "
				+format.format(Double.parseDouble(degree))+" degrees of freedom";
		lmSum[9][0] = "Multiple R-squared : "+format.format(Double.parseDouble(multipleR2));
		lmSum[10][0] = "Adjusted R-squared : "+format.format(Double.parseDouble(adjustedR2));
		
		return lmSum;
	}

}
