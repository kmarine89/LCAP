package witlab.nlas.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

import witlab.nlas.db.DataList;
import witlab.nlas.db.DataArray;
import witlab.nlas.db.NaturallightDB;
import witlab.nlas.etc.Filtering;
import witlab.nlas.etc.InframeAction;
import witlab.nlas.etc.MyBorder;
import witlab.nlas.etc.MyString;
import witlab.nlas.etc.RController;

@SuppressWarnings("serial")
public class InframeRegression extends JInternalFrame implements InframeAction, ComponentListener {
	
	private final String itemStrArr[] = { "CL_Ev_lx", "CL_x", "CL_y", "CL_u", "CL_v", "CL_TCP_K",
			"CL_duv", "CL_TCP_K_Jis", "CL_duv_K_Jis", "CL_Large_x", "CL_Large_y", "CL_Large_z", 
			"CL_DW", "CL_Purity", "CS_Lv", "CS_x", "CS_y", "CS_Large_T", "CS_Large_x", "CS_Large_y", 
			"CS_Large_z", "CS_u", "CS_v", "CS_Large_L", "CS_a", 	"CS_b", "CS_duv", "CS_dlv", 
			"CS_dx", "CS_dy", "CS_DW", "CS_Purity", "JAZ_Large_x", "JAZ_Large_y", "JAZ_Large_z",
			"JAZ_x", "JAZ_y", "JAZ_z", };
	
	DataList field;
	DataArray data;
	
	JPanel tablePanel = new JPanel();
	JPanel graphPanel = new JPanel();
	
	JList<String> listPV;
	JList<String> listRV;
	
	NaturallightDB db = NaturallightDB.getInstance();
	
	public InframeRegression(int index) {
		super(MyString.TYPE_OF_REGRESSION_FRAME, true, true, true, true);
		setBorder(new MyBorder().getBorder());
		setBounds(50, 50, 500, 300);
		setMinimumSize(new Dimension(500, 300));
		addComponentListener(this);
		
		tablePanel.setLayout(new GridBagLayout());
		graphPanel.setLayout(new GridLayout(1, 1));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setUI(new AquaBarTabbedPaneUI());
		tabbedPane.addTab("Variable", tablePanel);
		tabbedPane.addTab("Summary", graphPanel);
		add(tabbedPane);
		
		drawTable();
		
		setVisible(true);
	}
	
	private void addComponent(GridBagConstraints c, Component component,int gridx, int gridy, 
			int gridwidth, int gridheight, double weightx, double weighty) {
		c.gridx = gridx;
		c.gridy = gridy;
		c.gridwidth = gridwidth;
		c.gridheight = gridheight;
		c.weightx = weightx; 
		c.weighty = weighty;
		tablePanel.add(component, c);
	}
	
	@Override
	public void execution() {
		// 정보 가져와서
		String date = WindowFrame.ControllerPanel.dayJList.getSelectedValue().substring(0, 10);
		String sTime = WindowFrame.ControllerPanel.sTimeField.getText().toString();
		String eTime = WindowFrame.ControllerPanel.eTimeField.getText().toString();
		boolean filterOutlier = WindowFrame.ControllerPanel.checkFilterOLabel.getText().equals("ON");
		List<String> pv = listPV.getSelectedValuesList();
		String rv = listRV.getSelectedValue();

		// DB 검색 후, this 전역 변수 채워 넣고
		field = null;
		data = null;
		String itemList = rv;
		for (String item : pv) {
			itemList += ","+item;
		}
		field = new DataList(("Date,Time,"+itemList).split(","));
		data = db.getDataFrame(date, sTime, eTime, itemList);
		
		// 필터링 옵션에 따른 필터링 수행
		if(filterOutlier)	data = Filtering.outlier(field, data); 
		
		drawGraph();
	}

	public void drawTable() {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		
		JLabel label1 = new JLabel("Predictor variable");
		JLabel label2 = new JLabel("Reaction variable");
		listPV = new JList<String>(itemStrArr);
		listRV = new JList<String>(itemStrArr);
		JScrollPane scrollPV = new JScrollPane(listPV);
		JScrollPane scrollRV = new JScrollPane(listRV);
		
		listPV.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		listRV.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		addComponent(c, label1, 0, 0, 1, 1, 0, 0);
		addComponent(c, label2, 1, 0, 1, 1, 0, 0);
		addComponent(c, scrollPV, 0, 1, 1, 1, 0, 1);
		addComponent(c, scrollRV, 1, 1, 1, 1, 0, 1);
	}

	public void drawGraph() {
		graphPanel.removeAll();
		RController rc = RController.getInstance();
		String[][] lmSum = rc.getRegression(field, data);
		JTextArea area = new JTextArea(lmSum.length, lmSum[0].length);
		JScrollPane scroll = new JScrollPane(area);
		area.setText(null);
		for (int i = 0; i < lmSum.length; i++) {
			for (int j = 0; j < lmSum[0].length; j++) {
				area.append(lmSum[i][j]+"\t");
			}
			area.append("\n");
		}
		graphPanel.add(scroll);
		graphPanel.updateUI();
	}
	
	@Override
	public JInternalFrame getFrame() {
		return this;
	}

	@Override
	public String getType() {
		return MyString.TYPE_OF_REGRESSION_FRAME;
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {}

	@Override
	public void componentMoved(ComponentEvent arg0) {}

	@Override
	public void componentResized(ComponentEvent arg0) {
		
	}

	@Override
	public void componentShown(ComponentEvent arg0) {}
	
}
