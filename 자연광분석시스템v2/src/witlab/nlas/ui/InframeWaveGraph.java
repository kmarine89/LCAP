package witlab.nlas.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import witlab.nlas.db.DataArray;
import witlab.nlas.db.NaturallightDB;
import witlab.nlas.etc.InframeAction;
import witlab.nlas.etc.MyBorder;
import witlab.nlas.etc.MyString;
import witlab.nlas.graph.WavelengthGraph;

@SuppressWarnings("serial")
public class InframeWaveGraph extends JInternalFrame implements InframeAction {

	DataArray data;
	
	JPanel panel = new JPanel(new BorderLayout());
	
	NaturallightDB db = NaturallightDB.getInstance();
	
	String nmList;
	
	JPanel graphPanel;
	
	WavelengthGraph graph = new WavelengthGraph();
	
	public InframeWaveGraph(int index) {
		super(MyString.TYPE_OF_WAVEGRAPH_FRAME, true, true, true, true);
		setBorder(new MyBorder().getBorder());
		setBounds(10, 10, 400, 300);
		setMinimumSize(new Dimension(400, 300));
		
		nmList = getNM("txt\\nm.txt");
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setUI(new AquaBarTabbedPaneUI());
		tabbedPane.addTab("Wavelength Graph ", panel);
		add(tabbedPane);
		
		setVisible(true);
	}

	private String getNM(String filepath) {
		String nmList = null;
		try {
			FileReader fr = new FileReader(filepath);
			BufferedReader br = new BufferedReader(fr);
			nmList = br.readLine();
			br.close();
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nmList;
	}

	@Override
	public JInternalFrame getFrame() {
		return this;
	}

	@Override
	public String getType() {
		return MyString.TYPE_OF_WAVEGRAPH_FRAME;
	}

	@Override
	public void execution() {
		// 컨트롤패널의 날짜 정보 가져오고
		String date = WindowFrame.ControllerPanel.dayJList.getSelectedValue().substring(0, 10);
		String sTime = WindowFrame.ControllerPanel.sTimeField.getText().toString();
		String eTime = WindowFrame.ControllerPanel.eTimeField.getText().toString();
		
		// DB 검색 후, this 전역 변수 채워 넣고
		data = db.getDataFrame(date, sTime, eTime, nmList);

		settingPanel();
		
		graph.setTitleName(date);
		graph.setXAxisName("nm");
		graph.setYAxisName("Value");
		graphPanel = graph.getGraph();
		panel.add(graphPanel, BorderLayout.CENTER);
	}

	private void settingPanel() {
		JPanel west = new JPanel(new BorderLayout());
		panel.add(west, BorderLayout.WEST);
		
		String[] timesStr = new String[data.size()];
		for (int i = 0; i < timesStr.length; i++) {
			timesStr[i] = data.getItem(i, 0)+" "+data.getItem(i, 1);
		}
		JList<String> timesList = new JList<>(timesStr);
		JScrollPane pane = new JScrollPane(timesList);
		west.add(pane, BorderLayout.CENTER);
		
		JPanel westTop = new JPanel(new GridLayout(1, 2));
		west.add(westTop, BorderLayout.NORTH);
		JButton initButton = new JButton("INIT");
		JButton addButton = new JButton("ADD");
		initButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				graph.removeAll();
			}
		});
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selectedIndex = timesList.getSelectedIndices();
				ArrayList<String[]> tempData = data.getData();
				for (int index : selectedIndex) {
					graph.addDataset(tempData.get(index));
				}
				graphPanel.removeAll();
				graphPanel = graph.getGraph();
				graphPanel.updateUI();
			}
		});
		westTop.add(initButton);
		westTop.add(addButton);
	}

}
