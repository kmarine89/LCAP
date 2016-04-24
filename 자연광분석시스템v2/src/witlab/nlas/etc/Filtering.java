package witlab.nlas.etc;

import witlab.nlas.db.DataList;
import witlab.nlas.db.DataArray;

public class Filtering {

	/**
	 * �̻�ġ ���͸�
	 * @param field
	 * @param paramdata
	 * @return Outlier ���ŵ� ������������
	 */
	public static DataArray outlier(DataList field, DataArray paramdata) {
		DataArray data = new DataArray();
		double[][] whisker = RController.getInstance().getWhisker(field, paramdata);
		
		for (String[] row : paramdata.getData()) {
			for (int i = 2; i < row.length; i++) {
				if(row[i].equals("NA")) continue;	// ���� �����ʿ� ������ ������..
				double temp = Double.valueOf(row[i]);
				if(temp <= whisker[i-2][0] || temp >= whisker[i-2][1])
					row[i] = "NA";
			}
			data.add(row);
		}
		return paramdata; 
	}
	
}
