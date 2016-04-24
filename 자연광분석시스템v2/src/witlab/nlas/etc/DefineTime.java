package witlab.nlas.etc;

import witlab.nlas.db.DataArray;
import witlab.nlas.db.NaturallightDB;

public class DefineTime {

	String startTime;
	String endTime;
	
	public DefineTime(String date, String sTime, String eTime) {
		NaturallightDB db = NaturallightDB.getInstance();
		DataArray data = db.getDataFrame(date, sTime, eTime, "CL_TCP_K");
		startTime = searchStartTime(data);
		endTime = searchEndTime(data);
	}
	
	private String searchStartTime(DataArray data) {
		String startTime = "05:00";
		double min = 999999.9; 
		for (int i = 0; i < 120; i++) {	// 약 두시간 어치 검색
			if("".equals(data.getItem(i, 2)) || data.getItem(i, 2) == null) continue;
			if(Double.parseDouble(data.getItem(i, 2)) < min) {
				min = Double.parseDouble(data.getItem(i, 2));
				startTime = data.getItem(i, 1);
			}
		}
		return startTime;
	}

	private String searchEndTime(DataArray data) {
		String endTime = "20:00";
		double min = 999999.9; 
		for (int i = data.size(); i > data.size()-120; i--) {	// 약 두시간 어치 검색
			if("".equals(data.getItem(i-1, 2)) || data.getItem(i-1, 2) == null) continue;
			if(Double.parseDouble(data.getItem(i-1, 2)) < min) {
				min = Double.parseDouble(data.getItem(i-1, 2));
				endTime = data.getItem(i-1, 1);
			}
		}
		return endTime;
	}

	public String getStartTime() {
		return startTime;
	}
	
	public String getEndTime() {
		return endTime;
	}
	
}
