package witlab.nlas.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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
public class InframeComparison extends JInternalFrame implements InframeAction, ComponentListener {

	String field;
	ArrayList<DataArray> data = new ArrayList<>();
	List<String> date;
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
	public InframeComparison(int index) {
		super(MyString.TYPE_OF_COMPARISON_FRAME, true, true, true, true);
		this.imagePath = "graph/"+MyString.TYPE_OF_COMPARISON_FRAME+" "+index+".jpg";
		setBorder(new MyBorder().getBorder());
		setBounds(40, 40, 400, 300);
		setMinimumSize(new Dimension(400, 300));
		addComponentListener(this);
		
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
		return MyString.TYPE_OF_COMPARISON_FRAME;
	}
	
	/**
	 * ControllerPanel의 Execution 버튼에서 실행
	 * @since 2015-09-30
	 */
	@Override
	public void execution() {
		// 정보 가져와서
		date = WindowFrame.ControllerPanel.dayJList.getSelectedValuesList();
		String sTime = WindowFrame.ControllerPanel.sTimeField.getText().toString();
		String eTime = WindowFrame.ControllerPanel.eTimeField.getText().toString();
		boolean filterOutlier = WindowFrame.ControllerPanel.checkFilterOLabel.getText().equals("ON");
		
		// DB 검색 후, this 전역 변수 채워 넣고
		data.clear();
		for (String str : date) {
			DataArray tempData = db.getDataFrame(str.substring(0, 10), sTime, eTime, field);
			data.add(tempData);
		}
		
		// 필터링 옵션에 따른 필터링 수행
		if(filterOutlier) {
			ArrayList<DataArray> tempData = new ArrayList<>();
			for (DataArray ds : data) {
				DataList tempField = new DataList();
				tempField.setData(new String[] {field});
				tempData.add(Filtering.outlier(tempField, ds));
			}
			data = tempData;
		}
		
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
		tablePanel.setLayout(new GridLayout(1, data.size()));
		for (DataArray tempData : data) {
			String[] tempField = new String[] { "Date", "Time", field };
			DefaultTableModel tableModel = new DefaultTableModel(tempData.toSquareArray(), tempField);
			JTable table = new JTable(tableModel);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			JScrollPane scrollPane = new JScrollPane(table);
			tablePanel.add(scrollPane);
		}
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
		graphPanel.removeAll();
		
		for (DataArray dataset : data) {
			String date = dataset.getData().get(0)[0];
			DataArray data = new DataArray(date);
			for (int i = 0; i < dataset.size(); i++) {
				data.add(new String[] { dataset.getItem(i, 1), dataset.getItem(i, 2) });
			}
			lineGraph.addDataset(data);
		}
		
		lineGraph.setXAxisName("Time");
		lineGraph.setYAxisName("Value");
		lineGraph.setTitleName(field);
		graphPanel.add(lineGraph.getDotGraph());
	}

	/**
	 * inframe 반환
	 * @since 2015-09-30
	 */
	@Override
	public JInternalFrame getFrame() {
		return this;
	}

	/**
	 * Resize 이벤트 등록
	 * @since 2015-09-30
	 */
	@Override
	public void componentHidden(ComponentEvent arg0) {}

	@Override
	public void componentMoved(ComponentEvent arg0) {}

	@Override
	public void componentResized(ComponentEvent arg0) {
		if(originalImage == null) return;
		resizeImage = originalImage.getScaledInstance(getWidth(), getHeight()-30, Image.SCALE_SMOOTH);
		graphLabel.setIcon(new ImageIcon(resizeImage));
		graphPanel.updateUI();
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		showFrameOfSelectionItem();
	}
	
	private void showFrameOfSelectionItem() {
		final JFrame frame = new JFrame("Selection Item");
		frame.setLocation(200, 200);
		Container ct = frame.getContentPane();
		String[] field_list = MyString.itemList.split(",");
		frame.setSize(400, 40+15*field_list.length);
		ct.setLayout(new GridLayout((field_list.length+1)/2, 2));
		JRadioButton[] radio = new JRadioButton[field_list.length];
		ButtonGroup bg = new ButtonGroup();
		for (int i = 0; i < radio.length; i++) {
			radio[i] = new JRadioButton(field_list[i]);
			radio[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					field = event.getActionCommand();
					frame.dispose();
				}
			});
			bg.add(radio[i]);
			ct.add(radio[i]);
		}
		frame.setVisible(true);
	}

}
