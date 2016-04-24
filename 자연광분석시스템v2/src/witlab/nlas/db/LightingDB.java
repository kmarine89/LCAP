package witlab.nlas.db;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * �ΰ��� DB ��� ���.. �̱��� ���� ����
 * @author ����
 */
public class LightingDB {
	
	Connection con = null;
	Statement stmt = null;
	String databaseName = "lighting";
	String DB_info = "jdbc:mysql://210.102.142.14:3306/" + databaseName;
	String DB_id = "root";
	String DB_pw = "defacto8*";
	
	private static LightingDB instance;
	private LightingDB() {}
	public static LightingDB getInstance() {
		if(instance == null)
			instance = new LightingDB();
		return instance;
	}
	
	private void openDB() {
		// 210.102.142.14�� �ڽ��� �����Ǹ� ���������� ����
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
	 * @return ���� ������ �ִ� �ΰ��� ���̺� ����Ʈ ��ȯ
	 */
	public DataList getLightingList() {
		openDB();
		String sql;
		DataList data = new DataList();
		ResultSet rs;
		try {
			sql = "show tables";
			rs = stmt.executeQuery(sql);
			rs.beforeFirst();
			while(rs.next()) {
				data.add(rs.getString(1));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return data;
	}

}