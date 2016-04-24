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
 * @author 김양수
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
	 * index와 TYPE을 사용하여 inframe 타이틀 이름 설정
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
	 * TYPE 반환
	 * @since 2015-09-30
	 */
	@Override
	public String getType() {
		return MyString.TYPE_OF_RAWDATA_FRAME;
	}
	
	/**
	 * ControllerPanel의 Execution 버튼에서 실행
	 * @since 2015-09-30
	 */
	@Override
	public void execution() {
		// 정보 가져와서
		String date = WindowFrame.ControllerPanel.dayJList.getSelectedValue().substring(0, 10);
		String sTime = WindowFrame.ControllerPanel.sTimeField.getText().toString();
		String eTime = WindowFrame.ControllerPanel.eTimeField.getText().toString();
		boolean filterOutlier = WindowFrame.ControllerPanel.checkFilterOLabel.getText().equals("ON");
		
		// DB 검색 후, this 전역 변수 채워 넣고
		field = null;
		data = null;
		field = new DataList(("Date,Time,"+MyString.itemList).split(","));
		data = db.getDataFrame(date, sTime, eTime, MyString.itemList);
		
		// 필터링 옵션에 따른 필터링 수행
		if(filterOutlier)	data = Filtering.outlier(field, data); 
		
		// Table 보여주고
		drawTable();
		
		// Graph 보여주고
		drawGraph();
	}

	/**
	 * 데이터 프레임을 출력해줄 테이블 생성
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
	 * 데이터에 따른 R 기반 그래프 출력
	 * @since 2015-09-30
	 * Jfreechart 기반 그래프로 변경
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
	 * inframe 반환
	 * @since 2015-09-30
	 */
	@Override
	public JInternalFrame getFrame() {
		return this;
	}

}
