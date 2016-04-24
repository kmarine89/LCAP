package witlab.nlas.db;

import java.util.ArrayList;

public class DataList {

	private ArrayList<String> data = new ArrayList<>();
	private String name;
	
	public DataList() {
		this.name = "NO NAME";
	}

	public DataList(String name) {
		this.name = name;
	}
	
	public DataList(String[] data) {
		this.name = "NO NAME";
		for (String item : data) {
			this.data.add(item);
		}
	}
	
	public void setData(String[] data) {
		for (String item : data) {
			this.data.add(item);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<String> getData() {
		return data;
	}

	public String getItem(int num) {
		return data.get(num);
	}
	
	public int size() {
		return data.size();
	}
	
	public void add(String item) {
		data.add(item);
	}
	
	public String[] toArray() {
		if(this.data.size() == 0) return null;
		String[] data = new String[this.data.size()];
		for (int i = 0; i < data.length; i++) {
			data[i] = this.data.get(i);
		}
		return data;
	}
	
	public void printData() {
		for (String item : data) {
			System.out.println(item);
		}
	}

}
