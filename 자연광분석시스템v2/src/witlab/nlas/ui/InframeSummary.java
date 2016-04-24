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
 * @author ����
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
	 * index�� TYPE�� ����Ͽ� inframe Ÿ��Ʋ �̸� ����
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
	 * TYPE ��ȯ
	 * @since 2015-09-30
	 */
	@Override
	public String getType() {
		return MyString.TYPE_OF_SUMMARY_FRAME;
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
		RController rc = RController.getInstance();
		String[][] summaryData = rc.getSummary(field, data);
		tablePanel.removeAll();
		// Summary Ÿ������ field�� �籸��
		String[] field2 = new String[] { "Field", "Min.", "1st Qu.", "Median", "Mean", "3rd Qu.", "Max" };
		DefaultTableModel tableModel = new DefaultTableModel(summaryData, field2);
		JTable table = new JTable(tableModel);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane scrollPane = new JScrollPane(table);
		tablePanel.add(scrollPane);
		tablePanel.updateUI();
	}

	/**
	 * �����Ϳ� ���� R ��� �׷��� ���
	 * @since 2015-09-30
	 */
	public void drawGraph() {
		graphPanel.removeAll();
		//R�� imagePath �Ѱܼ� graph image���� ��,
		RController rc = RController.getInstance();
		rc.createGraphOfSummary(this.imagePath, field, data);
		//imagePath�� �����Ͽ� JLabel�� image ���
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
	 * inframe ��ȯ
	 * @since 2015-09-30
	 */
	@Override
	public JInternalFrame getFrame() {
		return this;
	}
	
	/**
	 * Resize �̺�Ʈ ���
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
