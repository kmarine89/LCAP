package witlab.nlas.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author 김양수
 */
@SuppressWarnings("serial")
public class FrameEditGraph extends JFrame {

	private static FrameEditGraph instance;
	private final String[] labelName = new String[] {
			"Title name", "X axis name", "Y axis name", "Y range", "Legend ocation"
	};
	JCheckBox[] cb;
	private JTextField tfTitle = new JTextField();
	private JTextField tfXaxis = new JTextField();
	private JTextField tfYaxis = new JTextField();
	private JTextField tfYrange1 = new JTextField();
	private JTextField tfYrange2 = new JTextField();
	private JComboBox<String> comboLegend;
	
	public static FrameEditGraph getInstance() {
		if(instance == null) {
			instance = new FrameEditGraph();
		}
		return instance;
	}
	
	public void showFrame() {
		setVisible(true);
	}
	
	private FrameEditGraph() {
		setLocation(200, 200);
		setSize(300, 200);
		setResizable(false);

		int count = labelName.length;
		
		Container ct = getContentPane();
		ct.setLayout(new GridLayout(count, 2));
		
		cb = new JCheckBox[count];
		JPanel[] panel = new JPanel[count];
		for (int i = 0; i < count; i++) {
			cb[i] = new JCheckBox(labelName[i]);
			cb[i].setFocusable(false);
			panel[i] = new JPanel();
			panel[i].setLayout(new FlowLayout());
			ct.add(cb[i]);
			ct.add(panel[i]);
		}

		String[] comboStr = new String[] { "topleft", "topright" };
		comboLegend = new JComboBox<String>(comboStr);
		
		tfTitle.setPreferredSize(new Dimension(125, 20));
		tfXaxis.setPreferredSize(new Dimension(125, 20));
		tfYaxis.setPreferredSize(new Dimension(125, 20));
		tfYrange1.setPreferredSize(new Dimension(60, 20));
		tfYrange2.setPreferredSize(new Dimension(60, 20));
		comboLegend.setPreferredSize(new Dimension(125, 20));
		panel[0].add(tfTitle);
		panel[1].add(tfXaxis);
		panel[2].add(tfYaxis);
		panel[3].add(tfYrange1);
		panel[3].add(tfYrange2);
		panel[4].add(comboLegend);
		
		/**
		 * 신버전 오면서 사용하지 않는 것들 비활성화
		 */
		tfYrange1.setEnabled(false);
		tfYrange2.setEnabled(false);
		comboLegend.setEnabled(false);
		cb[3].setEnabled(false);
		cb[4].setEnabled(false);
		
		for (int i = 0; i < panel.length; i++) {
			panel[i].updateUI();
		}
		setVisible(false);
	}
	
	public String getTitleName() {
		if(cb[0].isSelected()) {
			return tfTitle.getText().toString();
		} else {
			return "Title Name";
		}
	}
	
	public String getXaxisName() {
		if(cb[1].isSelected()) {
			return tfXaxis.getText().toString();
		} else {
			return "X Name";
		}
	}
	
	public String getYaxisName() {
		if(cb[2].isSelected()) {
			return tfYaxis.getText().toString();
		} else {
			return "Y Name";
		}
	}
	
	public String getMinRange(double min) {
		if(cb[3].isSelected()) {
			return tfYrange1.getText().toString();
		} else {
			return String.valueOf(min);
		}
	}
	
	public String getMaxRange(double max) {
		if(cb[3].isSelected()) {
			return tfYrange2.getText().toString();
		} else {
			return String.valueOf(max);
		}
	}
	
	public String getLegendLocation() {
		if(cb[4].isSelected()) {
			return comboLegend.getSelectedItem().toString();
		} else {
			return "topleft";
		}
	}
}
