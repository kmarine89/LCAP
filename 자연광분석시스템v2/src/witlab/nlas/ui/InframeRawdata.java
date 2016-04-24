package witlab.nlas.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import witlab.nlas.db.DataList;
import witlab.nlas.db.DataArray;
import witlab.nlas.db.NaturallightDB;
import witlab.nlas.etc.Filtering;
import witlab.nlas.etc.InframeAction;
import witlab.nlas.etc.MyBorder;
import witlab.nlas.etc.MyString;
import witlab.nlas.graph.LineGraph;

/**
 * @author ����
 */
@SuppressWarnings("serial")
public class InframeRawdata extends JInternalFrame implements InframeAction {

	DataList field;
	DataArray data;
	String imagePath;
	
	JPanel tablePanel = new JPanel();
	JPanel graphPanel = new JPanel();
	
	NaturallightDB db = NaturallightDB.getInstance();
	
	JLabel graphLabel;
	Image originalImage = null;
	Image resizeImage = null;
	
	/**
	 * index�� TYPE�� ����Ͽ� inframe Ÿ��Ʋ �̸� ����
	 * @param index
	 */
	public InframeRawdata(int index) {
		super(MyString.TYPE_OF_RAWDATA_FRAME, true, true, true, true);
		this.imagePath = "graph/"+MyString.TYPE_OF_RAWDATA_FRAME+" "+index+".jpg";
		setBorder(new MyBorder().getBorder());
		setBounds(10, 10, 400, 300);
		setMinimumSize(new Dimension(400, 300));
		
		tablePanel.setLayout(new GridLayout(1, 1));
		graphPanel.setLayout(new GridLayout(1, 1));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setUI(new AquaBarTabbedPaneUI());
		tabbedPane.addTab("Table", tablePanel);
		tabbedPane.addTab("Graph", graphPanel);
		add(tabbedPane);
		
		setVisible(true);
	}
	
	/**
	 * TYPE ��ȯ
	 * @since 2015-09-30
	 */
	@Override
	public String getType() {
		return MyString.TYPE_OF_RAWDATA_FRAME;
	}
	
	/**
	 * ControllerPanel�� Execution ��ư���� ����
	 * @since 2015-09-30
	 */
	@Override
	public void execution() {
		// ���� �����ͼ�
		String date = WindowFrame.ControllerPanel.dayJList.getSelectedValue().substring(0, 10);
		String sTime = WindowFrame.ControllerPanel.sTimeField.getText().toString();
		String eTime = WindowFrame.ControllerPanel.eTimeField.getText().toString();
		boolean filterOutlier = WindowFrame.ControllerPanel.checkFilterOLabel.getText().equals("ON");
		
		// DB �˻� ��, this ���� ���� ä�� �ְ�
		field = null;
		data = null;
		field = new DataList(("Date,Time,"+MyString.itemList).split(","));
		data = db.getDataFrame(date, sTime, eTime, MyString.itemList);
		
		// ���͸� �ɼǿ� ���� ���͸� ����
		if(filterOutlier)	data = Filtering.outlier(field, data); 
		
		// Table �����ְ�
		drawTable();
		
		// Graph �����ְ�
		drawGraph();
	}

	/**
	 * ������ �������� ������� ���̺� ����
	 * @since 2015-09-30
	 */
	public void drawTable() {
		tablePanel.removeAll();
		DefaultTableModel tableModel = new DefaultTableModel(data.toSquareArray(), field.toArray());
		JTable table = new JTable(tableModel);
//		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane scrollPane = new JScrollPane(table);
		tablePanel.add(scrollPane);
		tablePanel.updateUI();
	}
	
	/**
	 * �����Ϳ� ���� R ��� �׷��� ���
	 * @since 2015-09-30
	 * Jfreechart ��� �׷����� ����
	 * @since 2016-01-29
	 */
	public void drawGraph() {
		LineGraph lineGraph = new LineGraph();
		String date = this.data.getData().get(0)[0];
		graphPanel.removeAll();

		for (int i = 2; i < field.size(); i++) {
			DataArray data = new DataArray(field.getItem(i));
			for (int j = 0; j < this.data.size(); j++) {
				data.add(new String[] { this.data.getItem(j, 1), this.data.getItem(j, i) });
			}
			lineGraph.addDataset(data);
		}
		
		lineGraph.setXAxisName("Time");
		lineGraph.setYAxisName("Value");
		lineGraph.setTitleName(date);
		graphPanel.add(lineGraph.getLineGraph());
	}

	/**
	 * inframe ��ȯ
	 * @since 2015-09-30
	 */
	@Override
	public JInternalFrame getFrame() {
		return this;
	}

}
