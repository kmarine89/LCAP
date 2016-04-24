package witlab.nlas.ui;

import java.io.FileOutputStream;
import java.text.DecimalFormat;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import witlab.nlas.db.DataArray;
import witlab.nlas.db.NaturallightDB;
import witlab.nlas.graph.CIEGraphForExcel;
import witlab.nlas.graph.LineGraphForExcel;

public class FrameExportExcel {

	String path;	// 파일 경로
	String name;	// 파일 이름
	String date;	// 출력 대상 날짜
	String sTime, eTime;	// 출력 대상 시작, 끝 시간
	
	XSSFWorkbook wb = new XSSFWorkbook();
	Sheet sheet = wb.createSheet();
	Row row = sheet.createRow(0);
	
	NaturallightDB db = NaturallightDB.getInstance();
	
	LineGraphForExcel lineGraph;
	CIEGraphForExcel cieGraph;
	
	public FrameExportExcel() {
		JFileChooser chooser = new JFileChooser("C:");
		int value = chooser.showSaveDialog(null);
		if(value == JFileChooser.APPROVE_OPTION) {
			path = chooser.getSelectedFile().getParent();
			name = chooser.getSelectedFile().getName()+".xlsx";
			String temp = WindowFrame.ControllerPanel.dayJList.getSelectedValue();
			date = temp.substring(0, 10);
			sTime = temp.substring(12, 17);
			eTime = temp.substring(18, 23);
			
			if(date == null) {
				JOptionPane.showMessageDialog(null, 
						"날짜를 선택해주세요.", "경고", 
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			exportExcel();
		}
	}
	
	private void exportExcel() {
		XSSFFont font = null;
		font = wb.createFont();
		font.setFontHeight(20);
		
		CellStyle style = null;
		CellStyle style2 = null;
		CellStyle style3 = null;
		
		drawTitle(font, style);			// 가장 맨 위 타이틀
		drawGraph(style);				// 그래프 4개
		drawMemoSpace(font, style3);	// 메모 공간
		drawDataTable(font, style2);	// 데이터 테이블
		
		try {
			FileOutputStream out = new FileOutputStream(path+"\\"+name);
			wb.write(out);
			out.close();
			wb.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void drawDataTable(XSSFFont font, CellStyle style2) {
		// 데이터 테이블 기본 틀 설정
		style2 = wb.createCellStyle();
		style2.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style2.setAlignment(CellStyle.ALIGN_CENTER);
		style2.setBorderBottom(CellStyle.BORDER_THIN);
		style2.setBorderLeft(CellStyle.BORDER_THIN);
		style2.setBorderRight(CellStyle.BORDER_THIN);
		style2.setBorderTop(CellStyle.BORDER_THIN);

		String[][] data = new String[47][11];
		data[0] = new String[] { "", "", 
				"일출", "일출+10", "일출+20", "일출+30", "일출+40", 
				"일출+50", "일출+60", "일출+70", "일출+80" };
		data[1] = new String[] { "", "",
				plutMinute(sTime, 0), plutMinute(sTime, 10), plutMinute(sTime, 20), 
				plutMinute(sTime, 30), plutMinute(sTime, 40), plutMinute(sTime, 50), 
				plutMinute(sTime, 60), plutMinute(sTime, 70), plutMinute(sTime, 80) };
		data[16] = new String[] {"", "",
				"10:00", "10:30", "11:00", "11:30", "12:00",
				"12:30", "13:00", "13:30", "14:00" };
		data[17] = new String[] {"", "",
				"10:00", "10:30", "11:00", "11:30", "12:00",
				"12:30", "13:00", "13:30", "14:00" };
		data[32] = new String[] { "", "", 
				"일몰-80", "일몰-70", "일몰-60", "일몰-50", "일몰-40", 
				"일몰-30", "일몰-20", "일몰-10", "일몰" };
		data[33] = new String[] { "", "",
				minusMinute(eTime, 80), minusMinute(eTime, 70), minusMinute(eTime, 60), 
				minusMinute(eTime, 50), minusMinute(eTime, 40), minusMinute(eTime, 30), 
				minusMinute(eTime, 20), minusMinute(eTime, 10), minusMinute(eTime, 0) };
		String[] tempArr = new String[] {
				"상대 시간", "절대 시간", "단파장", "중파장", "장파장",
				"색차계 Lv", "휘도계 Lv", "분광계 Lv",
				"색차계 K", "휘도계 K", "분광계 K",
				"색좌표 x", "색좌표 y", "온도[℃]", "습도[%]" };
		for (int i = 0; i < tempArr.length; i++) {
			data[i][0] = tempArr[i];
			data[i+16][0] = tempArr[i];
			data[i+32][0] = tempArr[i];
		}
		
		// data[2~14][2] 	--> 일출 (data[1][2])
		// data[2~14][10]	--> 일출+80 (data[1][10])
		
		// data[18~30][2]	--> 10:00 (data[17][2])
		// data[18~30][10]	--> 14:00 (data[17][10])
		
		// data[34~46][2]	--> 일몰-80 (data[33][2])
		// data[34~46][10]	--> 일몰 (data[33][10])
		String field = "JAZ_SWR, JAZ_MWR, JAZ_LWR, CL_Ev_lx, CS_Lv, JAZ_Lux, "
				+ "CL_TCP_K, CS_Large_T, JAZ_CCT, CL_x, CL_y, tempout, humout";
		String[] tempData;
		for (int i = 2; i < 11; i++) {
			// data[2~14][2] 	--> 일출 (data[1][2])
			// ·····
			// data[2~14][10]	--> 일출+80 (data[1][10])
			tempData = db.getData(date, data[1][i], field);
			for (int j = 0; j < tempData.length; j++)
				data[j+2][i] = tempData[j];
			
			// data[18~30][2]	--> 10:00 (data[17][2])
			// ·····
			// data[18~30][10]	--> 14:00 (data[17][10])
			tempData = db.getData(date, data[17][i], field);
			for (int j = 0; j < tempData.length; j++)
				data[j+18][i] = tempData[j];
			
			// data[34~46][2]	--> 일몰-80 (data[33][2])
			// ·····
			// data[34~46][10]	--> 일몰 (data[33][10])
			tempData = db.getData(date, data[33][i], field);
			for (int j = 0; j < tempData.length; j++)
				data[j+34][i] = tempData[j];
		}
		
		// 준비된 Data Table 엑셀에 넣기
		for (int i = 37; i < 84; i++) {
			if(i == 52 || i == 68) continue;
			sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 1));
			for (int j = 0; j < 11; j++) {
				setCellText(j, i, style2, data[i-37][j]);
			}
		}
	}

	private void drawMemoSpace(XSSFFont font, CellStyle style3) {
		// 메모 공간
		style3 = wb.createCellStyle();
		style3.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style3.setWrapText(true);
		style3.setFont(font);
		sheet.addMergedRegion(new CellRangeAddress(37, 83, 11, 15));
		String contentStr = 
				"     작성자 : \n\n"
				+ "     관측자 : \n\n"
				+ "     특이사항 : \n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		setCellText(11, 37, style3, contentStr);
	}

	private void drawGraph(CellStyle style) {
		// 조도그래프
		sheet.addMergedRegion(new CellRangeAddress(2, 18, 0, 7));	//이놈은 단순 셀병합
//		setCellText(0, 2, style, "조도 그래프");
		lineGraph = new LineGraphForExcel();
		String[] fieldArr = new String[] { "CL_Ev_lx", "CS_Lv", "JAZ_Lux" };
		for (int i = 0; i < fieldArr.length; i++) {
			DataArray data = db.getDataFrame(date, sTime, eTime, fieldArr[i]);
			DataArray data2 = new DataArray(fieldArr[i]);
			for (int j = 0; j < data.size(); j++) {
				data2.add(new String[] { data.getItem(j, 1), data.getItem(j, 2) });
			}
			lineGraph.addDataset(data2);
		}
		lineGraph.setXAxisName("Time");
		lineGraph.setYAxisName("Lux");
		lineGraph.setYAxisRange(0, 110000);
		lineGraph.setTitleName("Illuminance Graph");
		lineGraph.drawLineGraph();
		byte[] byteArr = lineGraph.toByteArray();
		int index = wb.addPicture(byteArr, XSSFWorkbook.PICTURE_TYPE_PNG);
		Drawing draw = sheet.createDrawingPatriarch();
		XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, 0, 2, 8, 19);
		anchor.setAnchorType(ClientAnchor.MOVE_DONT_RESIZE);
		draw.createPicture(anchor, index);
		
		// CCT그래프
		sheet.addMergedRegion(new CellRangeAddress(2, 18, 8, 15));
		setCellText(8, 2, style, "CCT 그래프");
		lineGraph = new LineGraphForExcel();
		fieldArr = new String[] { "CL_TCP_K", "CS_Large_T", "JAZ_CCT" };
		for (int i = 0; i < fieldArr.length; i++) {
			DataArray data = db.getDataFrame(date, sTime, eTime, fieldArr[i]);
			DataArray data2 = new DataArray(fieldArr[i]);
			for (int j = 0; j < data.size(); j++) {
				data2.add(new String[] { data.getItem(j, 1), data.getItem(j, 2) });
			}
			lineGraph.addDataset(data2);
		}
		lineGraph.setXAxisName("Time");
		lineGraph.setYAxisName("CCT (K)");
		lineGraph.setYAxisRange(0, 20000);
		lineGraph.setTitleName("CCT Graph");
		lineGraph.drawLineGraph();
		byteArr = lineGraph.toByteArray();
		index = wb.addPicture(byteArr, XSSFWorkbook.PICTURE_TYPE_PNG);
		draw = sheet.createDrawingPatriarch();
		anchor = new XSSFClientAnchor(0, 0, 0, 0, 8, 2, 16, 19);
		anchor.setAnchorType(ClientAnchor.MOVE_DONT_RESIZE);
		draw.createPicture(anchor, index);
		
		// SWR그래프
		sheet.addMergedRegion(new CellRangeAddress(19, 35, 0, 7));
		setCellText(0, 22, style, "SWR 그래프");
		lineGraph = new LineGraphForExcel();
		fieldArr = new String[] { "JAZ_SWR", "JAZ_MWR", "JAZ_LWR" };	// 이름 사용하기 위한...
		String[] fieldArr2 = new String[] { "JAZ_SWR", "JAZ_LWR", "JAZ_LWR" };	// 실제 데이터 검색을 위한.. (이유는 아래 주석)
		for (int i = 0; i < fieldArr.length; i++) {
			DataArray data = db.getDataFrame(date, sTime, eTime, fieldArr2[i]);
			DataArray data2 = new DataArray(fieldArr[i]);
			for (int j = 0; j < data.size(); j++) {
				if(i == 0) {
					data2.add(new String[] { data.getItem(j, 1), data.getItem(j, 2) });	// 단파장
				} else if(i == 1) {
					double mwr = 0;
					if(!data.getItem(j, 2).equals(""))
						mwr = 100-Double.parseDouble(data.getItem(j, 2));	// 100-장파장 = 단+중파장
					data2.add(new String[] { data.getItem(j, 1), String.valueOf(mwr) });
				} else if(i == 2) {
					double lwr = 0;
					if(!data.getItem(j, 2).equals(""))
						lwr = 100.0;		// 100 = 단+중+장파장
					data2.add(new String[] { data.getItem(j, 1), String.valueOf(lwr) });
				}
			}
			lineGraph.addDataset(data2);
		}
		lineGraph.setXAxisName("Time");
		lineGraph.setYAxisName("Ratio (%)");
		lineGraph.setYAxisRange(0, 100);
		lineGraph.setTitleName("Wavelength Ratio Graph");
		lineGraph.drawAreaGraph();
		byteArr = lineGraph.toByteArray();
		index = wb.addPicture(byteArr, XSSFWorkbook.PICTURE_TYPE_PNG);
		draw = sheet.createDrawingPatriarch();
		anchor = new XSSFClientAnchor(0, 0, 0, 0, 0, 19, 8, 36);
		anchor.setAnchorType(ClientAnchor.MOVE_DONT_RESIZE);
		draw.createPicture(anchor, index);
		
		// 색좌표 그래프
		sheet.addMergedRegion(new CellRangeAddress(19, 35, 8, 15));
		setCellText(8, 22, style, "CIE1931 그래프");
		cieGraph = new CIEGraphForExcel();
		String xyField = "CL_x, CL_y";
		DataArray cieData = db.getDataFrame(date, sTime, eTime, xyField);
		cieGraph.addDataset(cieData);
		cieGraph.setXAxisName("x");
		cieGraph.setYAxisName("y");
		cieGraph.setTitleName("CCT Graph");
		cieGraph.getGraph();
		byteArr = cieGraph.toByteArray();
		index = wb.addPicture(byteArr, XSSFWorkbook.PICTURE_TYPE_PNG);
		draw = sheet.createDrawingPatriarch();
		anchor = new XSSFClientAnchor(0, 0, 0, 0, 8, 19, 16, 36);
		anchor.setAnchorType(ClientAnchor.MOVE_DONT_RESIZE);
		draw.createPicture(anchor, index);
	}

	private void drawTitle(XSSFFont font, CellStyle style) {
		// 맨 위 타이틀 셀
		style = wb.createCellStyle();
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(font);
		setCellText(0, 0, style, date + " (Sunrise : " + sTime +", Sunset : " + eTime + ")");
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 15));
	}

	private String minusMinute(String eTime, int minus) {
		int hour = Integer.parseInt(eTime.split(":")[0])-2;
		int minute = Integer.parseInt(eTime.split(":")[1])-minus+120;
		int a = minute/60;
		int b = minute%60;
		hour += a;
		minute = b;
		DecimalFormat f= new DecimalFormat("00");
		return f.format(hour)+":"+f.format(minute);
	}

	private String plutMinute(String sTime, int plue) {
		int hour = Integer.parseInt(sTime.split(":")[0]);
		int minute = Integer.parseInt(sTime.split(":")[1])+plue;
		int a = minute/60;
		int b = minute%60;
		hour += a;
		minute = b;
		DecimalFormat f= new DecimalFormat("00");
		return f.format(hour)+":"+f.format(minute);
	}

	private void setCellText(int x, int y, CellStyle style, String string) {
		if(row.getRowNum() != y)
			row = sheet.createRow(y);
		Cell cell = row.createCell(x);
		cell.setCellStyle(style);
		cell.setCellValue(string);
	}
	
}
