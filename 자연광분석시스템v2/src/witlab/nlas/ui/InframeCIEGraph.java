package witlab.nlas.ui;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import witlab.nlas.db.DataArray;
import witlab.nlas.db.NaturallightDB;
import witlab.nlas.etc.InframeAction;
import witlab.nlas.etc.MyBorder;
import witlab.nlas.etc.MyString;
import witlab.nlas.graph.CIEGraph;

@SuppressWarnings("serial")
public class InframeCIEGraph extends JInternalFrame implements InframeAction {

	DataArray data1931, data1976;
	
	JPanel cie1931LPanel = new JPanel(new GridLayout(1, 1));
	JPanel cie1931SPanel = new JPanel(new GridLayout(1, 1));
	JPanel cie1976Panel = new JPanel(new GridLayout(1, 1));
	
	NaturallightDB db = NaturallightDB.getInstance();
	
	CIEGraph cie1931L, cie1931S, cie1976;
	
	public InframeCIEGraph(int index) {
		super(MyString.TYPE_OF_CIEGRAPH_FRAME, true, true, true, true);
		setBorder(new MyBorder().getBorder());
		setBounds(10, 10, 400, 300);
		setMinimumSize(new Dimension(400, 300));
		
		cie1931L = new CIEGraph();
		cie1931S = new CIEGraph();
		cie1976 = new CIEGraph();
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setUI(new AquaBarTabbedPaneUI());
		tabbedPane.addTab("CIE1931L", cie1931LPanel);
		tabbedPane.addTab("CIE1931S", cie1931SPanel);
		tabbedPane.addTab("CIE1976", cie1976Panel);
		add(tabbedPane);
		
		setVisible(true);
	}
	
	private void settingPanelToCIE1931L() {
		cie1931LPanel.removeAll();
		
		cie1931L.addDataset(data1931);
		cie1931L.setXAxisName("x");
		cie1931L.setYAxisName("y");
		cie1931L.setTitleName("CIE1931");
		
		cie1931LPanel.add(cie1931L.getGraph1931L());
	}
	
	private void settingPanelToCIE1931S() {
		cie1931SPanel.removeAll();
		
		cie1931S.addDataset(data1931);
		cie1931S.setXAxisName("x");
		cie1931S.setYAxisName("y");
		cie1931S.setTitleName("CIE1931");
		
		cie1931SPanel.add(cie1931S.getGraph1931S());
	}
	
	private void settingPanelToCIE1976() {
		cie1976Panel.removeAll();
		
		cie1976.addDataset(data1976);
		cie1976.setXAxisName("u'");
		cie1976.setYAxisName("v'");
		cie1976.setTitleName("cie1976");
		
		cie1976Panel.add(cie1976.getGraph1976());
	}

	@Override
	public JInternalFrame getFrame() {
		return this;
	}

	@Override
	public String getType() {
		return MyString.TYPE_OF_CIEGRAPH_FRAME;
	}

	@Override
	public void execution() {
		// 컨트롤패널의 날짜 정보 가져오고
		String date = WindowFrame.ControllerPanel.dayJList.getSelectedValue().substring(0, 10);
		String sTime = WindowFrame.ControllerPanel.sTimeField.getText().toString();
		String eTime = WindowFrame.ControllerPanel.eTimeField.getText().toString();
		
		data1931 = db.getDataFrame(date, sTime, eTime, "CL_x, CL_y");
		data1976 = db.getDataFrame(date, sTime, eTime, "CL_u, CL_v");
		
		settingPanelToCIE1931L();
		settingPanelToCIE1931S();
		settingPanelToCIE1976();
	}

}
