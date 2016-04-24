package witlab.nlas.db;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 자연광 DB 통신 모듈... 싱글턴 패턴 적용
 * @author 김양수
 */
public class NaturallightDB {
	
	Connection con = null;
	Statement stmt = null;
	String databaseName = "naturallight";
	String DB_info = "jdbc:mysql://210.102.142.14:3306/" + databaseName;
	String DB_id = "root";
	String DB_pw = "defacto8*";
	
	private static NaturallightDB instance;
	private NaturallightDB() {}
	public static NaturallightDB getInstance() {
		if(instance == null)
			instance = new NaturallightDB();
		return instance;
	}
	
	private void openDB() {
		// 210.102.142.14가 자신의 아이피면 루프백으로 설정
		try {
			InetAddress local = InetAddress.getLocalHost();
			if(local.getHostAddress().equals("210.102.142.14"))
				DB_info = "jdbc:mysql://127.0.0.1:3306/"+databaseName;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		try {
			con = DriverManager.getConnection(DB_info, DB_id, DB_pw);
			stmt = con.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void closeDB() {
		try {
			if (stmt != null)	stmt.close();
			if (con != null)		con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return YYYY-MM-DD (Sunrise 05:32, Sunset 19:52) 와 같은 형태로 반환
	 * @since 2015-07-19
	 */
	public DataList getDate() {
		openDB();
		String sql;
		DataList date = new DataList("Date List");
		ResultSet rs;
		try {
			sql = "select * from sun_info where date>='2014-05-20'";
			rs = stmt.executeQuery(sql);
			rs.beforeFirst();
			while(rs.next()) {
				String item = rs.getString(1)+" (Sunrise "+rs.getString(2).substring(0, 5)+", "
						+ "Sunset "+rs.getString(3).substring(0, 5)+")";
				date.add(item);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return date;
	}

	/**
	 * @return 데이터 프레임
	 * @since 2015-07-19
	 */
	public DataArray getDataFrame(String date, String sTime, String eTime, String dataList) {
		openDB();
		String sql;
		DataArray data = new DataArray(date+","+sTime+","+eTime);
		ResultSet rs;
		try {
			sql = "select date, " + dataList + " from row_cl200a left outer join row_cs200 using(date) "
					+ "left outer join row_jaz_cct using(date) left outer join row_jaz_lux using(date) "
					+ "left outer join row_weather using(date) left outer join row_jaz_spd using(date) "
					+ "where date>='" + date + " " + sTime + "' and date<='" + date + " " + eTime + "'";
			rs = stmt.executeQuery(sql);
			rs.beforeFirst();
			while(rs.next()) {
				String[] row = new String[dataList.split(",").length+2];
				row[0] = rs.getString(1).substring(0, 10);
				row[1] = rs.getString(1).substring(11, 19);
				for (int j = 2; j < row.length; j++) {
					row[j] = rs.getString(j);
					if(row[j] == null || row[j].equals("NULL"))
						row[j] = "";
				}
				data.add(row);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return data;
	}
	
	public String[] getDataList(String date, String[] time, String field) {
		openDB();
		String sql;
		String[] data = new String[27];
		ResultSet rs;
		try {
			for (int i = 0; i < data.length; i++) {
				sql = "select "+ field + " from row_cl200a left outer join row_cs200 using(date) "
						+ "left outer join row_jaz_cct using(date) left outer join row_jaz_lux using(date) "
						+ "left outer join row_weather using(date) left outer join row_jaz_spd using(date) "
						+ "where date='"+date+" "+time[i]+"'";
				rs = stmt.executeQuery(sql);
				rs.first();
				data[i] = rs.getString(1);
				if(data[i] == null || data[i].equals("NULL")) {
					data[i] = "";
				}
				rs.close();
			}
		} catch (Exception e) {
			System.err.println("데이터 가져오는 중 오류");
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return data;
	}
	
	public String[] getData(String date, String time, String field) {
		openDB();
		String sql;
		String[] data = new String[field.split(",").length];
		ResultSet rs;
		try {
			sql = "select " + field + " from row_cl200a left outer join row_cs200 using(date) "
					+ "left outer join row_jaz_cct using(date) left outer join row_jaz_lux using(date) "
					+ "left outer join row_weather using(date) left outer join row_jaz_spd using(date) "
					+ "where date>='" + date + " " + time + "' and date<='" + date + " " + time + "'";
			rs = stmt.executeQuery(sql);
			rs.first();
			for (int i = 0; i < data.length; i++) {
				String temp = rs.getString(i+1);
				if(temp == null || temp.equals("NULL"))
					temp = "";
				data[i] = temp;
			}
			
			rs.close();
		} catch (Exception e) {
			System.err.println("데이터 가져오는 중 오류");
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return data;
	}
	
	/**
	 * @param date
	 * @return date의 가장 마지막 시간
	 * @since 2015-07-19
	 */
	public String getMaxTime(String date) {
		openDB();
		String sql;
		String time = null;
		ResultSet rs;
		try {
			sql = "select max(time(date)) from row_cl200a left outer join row_cs200 using(date) "
					+ "left outer join row_jaz_cct using(date) left outer join row_jaz_lux using(date) "
					+ "left outer join row_weather using(date) left outer join row_jaz_spd using(date) "
					+ "where date>='" + date + " 00:01' and date<='" + date + " 23:59'";
			rs = stmt.executeQuery(sql);
			rs.first();
			time = rs.getString(1);
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return time;
	}
	
	/**
	 * @param date
	 * @return date의 가장 최초 시간
	 * @since 2015-07-19
	 */
	public String getMinTime(String date) {
		openDB();
		String sql;
		String time = null;
		ResultSet rs;
		try {
			sql = "select min(time(date)) from row_cl200a left outer join row_cs200 using(date) "
					+ "left outer join row_jaz_cct using(date) left outer join row_jaz_lux using(date) "
					+ "left outer join row_weather using(date) left outer join row_jaz_spd using(date) "
					+ "where date>='" + date + " 00:01' and date<='" + date + " 23:59'";
			rs = stmt.executeQuery(sql);
			rs.first();
			time = rs.getString(1);
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return time;
	}

}