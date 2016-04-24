package witlab.nlas.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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
import witlab.nlas.etc.RController;

/**
 * @author 김양수
 */
@SuppressWarnings("serial")
public class InframeSummary extends JInternalFrame implements InframeAction, ComponentListener {

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
	public InframeSummary(int index) {
		super(MyString.TYPE_OF_SUMMARY_FRAME, true, true, true, true);
		this.imagePath = "graph/"+MyString.TYPE_OF_SUMMARY_FRAME+" "+index+".jpg";
		setBorder(new MyBorder().getBorder());
		setBounds(20, 20, 400, 300);
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
		return MyString.TYPE_OF_SUMMARY_FRAME;
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
		RController rc = RController.getInstance();
		String[][] summaryData = rc.getSummary(field, data);
		tablePanel.removeAll();
		// Summary 타입으로 field를 재구성
		String[] field2 = new String[] { "Field", "Min.", "1st Qu.", "Median", "Mean", "3rd Qu.", "Max" };
		DefaultTableModel tableModel = new DefaultTableModel(summaryData, field2);
		JTable table = new JTable(tableModel);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane scrollPane = new JScrollPane(table);
		tablePanel.add(scrollPane);
		tablePanel.updateUI();
	}

	/**
	 * 데이터에 따른 R 기반 그래프 출력
	 * @since 2015-09-30
	 */
	public void drawGraph() {
		graphPanel.removeAll();
		//R로 imagePath 넘겨서 graph image생성 후,
		RController rc = RController.getInstance();
		rc.createGraphOfSummary(this.imagePath, field, data);
		//imagePath에 접근하여 JLabel에 image 출력
		try {
			originalImage = ImageIO.read(new File(this.imagePath));
			resizeImage = originalImage.getScaledInstance(getWidth(), getHeight()-30, Image.SCALE_SMOOTH);
		} catch (IOException e) {
			e.printStackTrace();
		}
		graphLabel = new JLabel(new ImageIcon(resizeImage));
		graphPanel.add(graphLabel);
		graphPanel.updateUI();
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
	public void componentShown(ComponentEvent arg0) {}
	
}
