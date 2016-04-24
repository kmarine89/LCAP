package witlab.nlas.etc;

import java.text.DecimalFormat;

import org.rosuda.JRI.RVector;
import org.rosuda.JRI.Rengine;

import witlab.nlas.db.DataArray;
import witlab.nlas.db.DataList;
import witlab.nlas.ui.FrameEditGraph;

/**
 * R 기능은 여기에 모두 정의
 * Run 할때 이렇게 설정하시오...
 * VM arguments : -Djava.library.path=C:\R-3.1.2\library\rJava\jri\i386
 * Environment (Variable : PATH, Value : C:\R-3.1.2\bin\i386; C:\R-3.1.2\library\rJava\jri\i386
 * @author 김양수
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
	 * R에서 벡터를 생성할때 공백 부분을 NA 바꾸어줄 필요가 있음
	 * @param paramdata (공백이 포함된...)
	 * @return data (공백 -> NA)
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
	 * R에 실제 벡터를 생성하는 메서드
	 * @param field
	 * @param data
	 */
	private void createVector(String[] field, String[][] data) {
		r.eval("rm(list=ls())");	// R 초기화
		String[] vectorStr = new String[field.length-1];	//date와 time을 하나도 합치므로 -1
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
	 * @return whisker의 상한과 하한 
	 */
	public double[][] getWhisker(DataList paramfield, DataArray data) {
		data = changeZeroToNA(data);	// 공백 부분은 NA로 변경
		createVector(paramfield.toArray(), data.toSquareArray());	// R에 각 데이터별 벡터 생성
		
		/**
		 * Rawdata나 Summary는 상관없지만...
		 * Comparison 기능에서 field.length-2 에서 찐빠남
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
		data = changeZeroToNA(data);	// 공백 부분은 NA로 변경
		createVector(field.toArray(), data.toSquareArray());	// R에 각 데이터별 벡터 생성
		
		// Boxplot을 그리기 위한 데이터프레임 이므로 Date ,Time(시간)은 넣지 않기 위해 2 부터...
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
		
		// 생성된 데이터프레임를 사용하여 그래프 생성
		r.eval("jpeg('"+imagePath+"')");
		r.eval("boxplot(table, ylab='"+editGraph.getYaxisName()+"', xlab='"+editGraph.getXaxisName()+"', main='"+editGraph.getTitleName()+"')");
		r.eval("dev.off()");
	}

	public void createGraphOfCorrelation(String imagePath, DataList paramfield, DataArray data) {
		data = changeZeroToNA(data);	// 공백 부분은 NA로 변경
		createVector(paramfield.toArray(), data.toSquareArray());	// R에 각 데이터별 벡터 생성
		
		// corrplot을 그리기 위한 데이터프레임 이므로 Date ,Time(시간)은 넣지 않기 위해 2 부터...
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
		
		// 생성된 데이터프레임에 대한 cor 메서드를 수행하여 변수에 저장 (R 내부에서)
		r.eval("corArr=cor(na.omit(table))");
		// 생성된 cor 가 저장된 변수를 통하여 corrplot 그래프 생성
		r.eval("library(corrplot)");
		r.eval("jpeg('"+imagePath+"')");
		r.eval("corrplot(corArr)");
		r.eval("dev.off()");
	}

	/**
	 * Summary 내부프레임에서 바로 JTable에 적용가능한 데이터 배열을 생성
	 * R 내부에서 벡터의 모든 데이터가 NA 일경우 NullPointException이 발생하므로 예외 처리해주었음
	 * @param field
	 * @param data
	 * @return Summary에서 사용된 String[][]
	 * @since 2015-10-01
	 */
	public String[][] getSummary(DataList field, DataArray data) {
		data = changeZeroToNA(data);	// 공백 부분은 NA로 변경
		createVector(field.toArray(), data.toSquareArray());	// R에 각 데이터별 벡터 생성
		
		// 각각의 벡터에 대한 Summary 수행하여 double 2차원배열에 저장
		String[] field2 = field.toArray();
		double[][] summaryData = new double[field2.length-2][6];
		for (int i = 2; i < field2.length; i++) {
			try {
				summaryData[i-2] = r.eval("summary("+field2[i]+")").asDoubleArray();
			} catch (NullPointerException e) {}
		}
		
		// UI Table에 출력하기위한 용도의 문자열 배열로 변환
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
		data = changeZeroToNA(data);	// 공백 부분은 NA로 변경
		createVector(paramfield.toArray(), data.toSquareArray());	// R에 각 데이터별 벡터 생성
		
		// cor 메서드를 수행할 데이터프레임 이므로 Date ,Time(시간)은 넣지 않기 위해 2 부터...
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
		// na.omit을 추가하면 전부 NA로 된 벡터가 하나라도 있으면 cor 결과 모두다 NaN이 되버리고...
		// na.omit을 쓰지 않으면.. 벡터에 NA가 한개만 있어도 그 열에 대한 cor 결과가 안나옴...
		// 두개를 적절히 섞으면 좋을텐데... 보류 2015-10-02
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
		data = changeZeroToNA(data);	// 공백 부분은 NA로 변경
		createVector(field.toArray(), data.toSquareArray());	// R에 각 데이터별 벡터 생성
		
		// 회귀 Summary를 보여줘야 하므로... Date, Time은 넣지 않기 위해 2부터...
		String temp = "";
		String[] field2 = field.toArray();
		for (int i = 2; i < field2.length; i++) {
			if(i == field2.length-1)		temp += field2[i];
			else if(i == 2)					temp += field2[i]+"~";
			else									temp += field2[i]+"+";
		}
		
		RVector lineValues = r.eval("summary(lm("+temp+"))").asVector();
		String[] values = new String[5];
		values[0] = lineValues.get(3).toString();	//회귀식 정보
		values[1] = lineValues.get(5).toString();	//스탠다드 에러
		values[2] = lineValues.get(6).toString();
		values[3] = lineValues.get(7).toString();	//멀티플 R 스퀘어
		values[4] = lineValues.get(8).toString();	//어드저스티드 R 스퀘어
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
		// 0~4줄 회귀식 정보 (matrix)
		// 5줄 빈공간
		// 6, 7줄 스탠다드 에러
		// 8줄 빈공간
		// 9, 10줄 R 스퀘어
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
