package witlab.nlas.db;

import java.util.ArrayList;

public class DataArray {

	private ArrayList<String[]> data = new ArrayList<>();
	private String name;
	
	public DataArray() {
		this.name = "NO NAME";
	}
	
	public DataArray(String name) {
		this.name = name;
	}
	
	public DataArray(String[][] data) {
		this.name = "NO NAME";
		for (String[] row : data) {
			this.add(row);
		}
	}
	
	public void setData(String[][] data) {
		for (String[] row : data) {
			this.add(row);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<String[]> getData() {
		return data;
	}

	public String getItem(int row, int col) {
		return data.get(row)[col];
	}
	
	public int size() {
		return data.size();
	}
	
	public void add(String[] row) {
		data.add(row);
	}
	
	public String[][] toSquareArray() {
		if(this.data.size() == 0) return null;
		String[][] data = new String[this.data.size()][this.data.get(0).length];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				data[i][j] = this.data.get(i)[j];
			}
		}
		return data;
	}
	
	public void printData() {
		for (String[] row : data) {
			for (String item : row) {
				System.out.print(item+"\t");
			}
			System.out.println();
		}
	}
	
}
