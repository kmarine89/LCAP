package witlab.nlas.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;

import witlab.nlas.etc.MyBorder;
import witlab.nlas.etc.MyString;

/**
 * Item(데이터)를 선택하기 위한 Frame
 * @author 김양수
 */
@SuppressWarnings("serial")
public class FrameSelectedItem extends JFrame {

	public static FrameSelectedItem instance;
	
	public static FrameSelectedItem getInstance() {
		if(instance == null) {
			instance = new FrameSelectedItem();
		}
		return instance;
	}
	
	public void showFrame() {
		setVisible(true);
	}
	
	private final String subPanelName[] = {"CL200A", "CS200", "   JAZ   ", "Weather"};
	
	private final String clStrArr[] = { 
			"CL_Ev_lx", "CL_x", "CL_y", "CL_u", "CL_v", 
			"CL_TCP_K",	"CL_duv", "CL_TCP_K_Jis", "CL_duv_K_Jis", 
			"CL_Large_x", "CL_Large_y", "CL_Large_z", "CL_DW", "CL_Purity" };
	
	private final String csStrArr[] = { 
			"CS_Lv", "CS_x", "CS_y", 
			"CS_Large_T", "CS_Large_x",	"CS_Large_y", "CS_Large_z", 
			"CS_u", "CS_v", "CS_Large_L", "CS_a", "CS_b", 
			"CS_duv", "CS_dlv", "CS_dx", "CS_dy", "CS_DW", "CS_Purity" };
	
	private final String jzStrArr[] = {
			"JAZ_Large_x", "JAZ_Large_y", "JAZ_Large_z",
			"JAZ_x", "JAZ_y", "JAZ_z",
			"JAZ_CRIRa","JAZ_CRIRa_CCT",
			"JAZ_CRIR1","JAZ_CRIR1_CCT",
			"JAZ_CRIR2","JAZ_CRIR2_CCT",
			"JAZ_CRIR3","JAZ_CRIR3_CCT",
			"JAZ_CRIR4","JAZ_CRIR4_CCT",
			"JAZ_CRIR5","JAZ_CRIR5_CCT",
			"JAZ_CRIR6","JAZ_CRIR6_CCT",
			"JAZ_CRIR7","JAZ_CRIR7_CCT",
			"JAZ_CRIR8","JAZ_CRIR8_CCT",
			"JAZ_CRIR9","JAZ_CRIR9_CCT",
			"JAZ_CRIR10","JAZ_CRIR10_CCT",
			"JAZ_CRIR11","JAZ_CRIR11_CCT",
			"JAZ_CRIR12","JAZ_CRIR12_CCT",
			"JAZ_CRIR13","JAZ_CRIR13_CCT",
			"JAZ_CRIR14","JAZ_CRIR14_CCT",
			"JAZ_CRIR15","JAZ_CRIR15_CCT",
			"JAZ_CRIDC", "JAZ_CCT", "JAZ_u", "JAZ_v", "JAZ_w",
			"JAZ_UV_HUE_Angle", "JAZ_UV_Saturation", "JAZ_DW", "JAZ_Purity",
			"JAZ_CIE_Whiteness", "JAZ_CIE_Tint", 
			"JAZ_Hunter_L", "JAZ_Hunter_a", "JAZ_Hunter_b", 
			"JAZ_CIE_L", "JAZ_CIE_a", "JAZ_CIE_b",
			"JAZ_CIELAB_HUE_Angle", "JAZ_CIELAB_Chroma", 
			"JAZ_CIE1960_u", "JAZ_CIE1960_v", 
			"JAZ_SWR","JAZ_MWR", "JAZ_LWR", "JAZ_UV_SUM",
			"JAZ_Area", "JAZ_Lumen", "JAZ_Lux", "JAZ_Candela",
			"JAZ_PAR_photons", "JAZ_PAR_photons_diviS"};
	
	private final String wtStrArr[] = { "Temp", "Temp_High", "Temp_Low", "Humidity",
			"Dewpoint", "Wind_Speed", "Wind_Direction", "Wind_Run",
			"Wind_High_Speed", "Wind_High_Direction", "Wind_Chill",
			"Heat_Index", "THW_Index", "THSW_Index", "Barometric_Pressure",
			"Rain", "Rain_Rate", "Solar_Radiation", "Solar_Energy",
			"Solar_High_Radiation", "UV_Index", "UV_Dose", "UV_High",
			"Heating_Degree_Day", "Cooling_Degree_Day", "Inside_Temp",
			"Inside_Humidity", "Inside_Dewpoint", "Inside_Heat_Index",
			"Evapotranspiration", "Wind_Samp", "Wind_TX", "ISS_Reception",
			"Archive_Intervel" };
	
	private final String msStrArr[] = { "xy", "XYZ", "Lux", "CCT", "duv", "DW", "Purity" };
	
	private String currentItemList = "";
	
	JToggleButton[] button;

	JCheckBox[] ckCL200A;
	JCheckBox[] ckCS200;
	JCheckBox[] ckJAZ;
	JCheckBox[] ckWeather;

	JTabbedPane tabbedPane = new JTabbedPane();
	
	CLPanel panel1;
	CSPanel panel2;
	JZPanel panel3;
	WTPanel panel4;
	
	private FrameSelectedItem() {
		setLayout(null);
		setLocation(200, 100);
		setSize(480, 320);
		setResizable(false);
		setVisible(false);
		
		button = new JToggleButton[msStrArr.length];
		ckCL200A = new JCheckBox[clStrArr.length];
		ckCS200 = new JCheckBox[csStrArr.length];
		ckJAZ = new JCheckBox[jzStrArr.length];
		ckWeather = new JCheckBox[wtStrArr.length];
		
		setButton();
		setPanel();
	}
	
	private void setButton() {
		for (int i = 0; i < button.length; i++) {
			button[i] = new JToggleButton(msStrArr[i]);
			button[i].setEnabled(false);
			button[i].setBounds(5+65*i, 5, 60, 20);
			if(i == button.length-1)	button[i].setBounds(5+65*i, 5, 70, 20);
			button[i].setFocusable(false);
			button[i].addActionListener(multiButtonAction);
			add(button[i]);
		}
	}
	
	private void setPanel() {
		panel1 = new CLPanel();
		panel2 = new CSPanel();
		panel3 = new JZPanel();
		panel4 = new WTPanel();
		
		JScrollPane[] scrollPane = new JScrollPane[4];
		for (int i = 0; i < scrollPane.length; i++) {
			scrollPane[i] = new JScrollPane();
			scrollPane[i].setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane[i].setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		}
		scrollPane[0].setViewportView(panel1);
		scrollPane[1].setViewportView(panel2);
		scrollPane[2].setViewportView(panel3);
		scrollPane[3].setViewportView(panel4);
		
		tabbedPane.setBounds(0, 30, 475, 263);
		tabbedPane.setBorder(new MyBorder().getBorder());
		tabbedPane.setUI(new AquaBarTabbedPaneUI());
		for (int i = 0; i < scrollPane.length; i++) {
			tabbedPane.addTab(subPanelName[i], scrollPane[i]);
		}
		add(tabbedPane);
	}
	
	public String getItemList() {
		return currentItemList;
	}
	
	class CLPanel extends JPanel {
		JCheckBox[] checkBox;
		public CLPanel() {
			setLayout(null);
			setPreferredSize(new Dimension(500, 150));
			checkBox = ckCL200A;
			for (int i = 0; i < checkBox.length; i++) {
				checkBox[i] = new JCheckBox(clStrArr[i]);
				checkBox[i].addActionListener(singleCheckboxAction);
				if(i%2 == 0)
					checkBox[i].setBounds(20, 10+i*10, 200, 20);
				else
					checkBox[i].setBounds(240, 10+(i-1)*10, 200, 20);
				add(checkBox[i]);
			}
		}
		public void setCheckbox(String str, boolean check) {
			if(str.equals(msStrArr[0])) {
				ckCL200A[1].setSelected(check);
				ckCL200A[2].setSelected(check);
			}
			else if(str.equals(msStrArr[1])) {
				ckCL200A[9].setSelected(check);
				ckCL200A[10].setSelected(check);
				ckCL200A[11].setSelected(check);
			}
			else if(str.equals(msStrArr[2])) {
				ckCL200A[0].setSelected(check);
			}
			else if(str.equals(msStrArr[3])) {
				ckCL200A[5].setSelected(check);
			}
			else if(str.equals(msStrArr[4])) {
				ckCL200A[6].setSelected(check);
			}
			else if(str.equals(msStrArr[5])) {
				ckCL200A[12].setSelected(check);
			}
			else if(str.equals(msStrArr[6])) {
				ckCL200A[13].setSelected(check);
			}
		}
	}

	class CSPanel extends JPanel {
		JCheckBox[] checkBox;
		public CSPanel() {
			setLayout(null);
			setPreferredSize(new Dimension(500, 200));
			checkBox = ckCS200;
			for (int i = 0; i < checkBox.length; i++) {
				checkBox[i] = new JCheckBox(csStrArr[i]);
				checkBox[i].addActionListener(singleCheckboxAction);
//				checkBox[i].setEnabled(false);
				if(i%2 == 0)
					checkBox[i].setBounds(20, 10+i*10, 200, 20);
				else
					checkBox[i].setBounds(240, 10+(i-1)*10, 200, 20);
				add(checkBox[i]);
			}
		}
		public void setCheckbox(String str, boolean check) {
			if(str.equals(msStrArr[0])) {
				ckCS200[1].setSelected(check);
				ckCS200[2].setSelected(check);
			}
			else if(str.equals(msStrArr[1])) {
				ckCS200[4].setSelected(check);
				ckCS200[5].setSelected(check);
				ckCS200[6].setSelected(check);
			}
			else if(str.equals(msStrArr[2])) {
				ckCS200[0].setSelected(check);
			}
			else if(str.equals(msStrArr[3])) {
				ckCS200[3].setSelected(check);
			}
			else if(str.equals(msStrArr[4])) {
				ckCS200[12].setSelected(check);
			}
			else if(str.equals(msStrArr[5])) {
				ckCS200[16].setSelected(check);
			}
			else if(str.equals(msStrArr[6])) {
				ckCS200[17].setSelected(check);
			}
		}
	}

	class JZPanel extends JPanel {
		JCheckBox[] checkBox;
		public JZPanel() {
			setLayout(null);
			setPreferredSize(new Dimension(500, 720));
			checkBox = ckJAZ;
			for (int i = 0; i < checkBox.length; i++) {
				checkBox[i] = new JCheckBox(jzStrArr[i]);
				checkBox[i].addActionListener(singleCheckboxAction);
//				checkBox[i].setEnabled(false);
				if(i%2 == 0)
					checkBox[i].setBounds(20, 10+i*10, 200, 20);
				else
					checkBox[i].setBounds(240, 10+(i-1)*10, 200, 20);
				add(checkBox[i]);
			}
		}
		public void setCheckbox(String str, boolean check) {
			if(str.equals(msStrArr[0])) {
				ckJAZ[3].setSelected(check);
				ckJAZ[4].setSelected(check);
			}
			else if(str.equals(msStrArr[1])) {
				ckJAZ[0].setSelected(check);
				ckJAZ[1].setSelected(check);
				ckJAZ[2].setSelected(check);
			}
			else if(str.equals(msStrArr[2])) {
				ckJAZ[43].setSelected(check);
			}
			else if(str.equals(msStrArr[3])) {
				ckJAZ[10].setSelected(check);
			}
			else if(str.equals(msStrArr[4])) {
				
			}
			else if(str.equals(msStrArr[5])) {
				ckJAZ[16].setSelected(check);
			}
			else if(str.equals(msStrArr[6])) {
				ckJAZ[17].setSelected(check);
			}
		}
	}

	class WTPanel extends JPanel {
		JCheckBox[] checkBox;
		public WTPanel() {
			setLayout(null);
			setPreferredSize(new Dimension(500, 360));
			checkBox = ckWeather;
			for (int i = 0; i < checkBox.length; i++) {
				checkBox[i] = new JCheckBox(wtStrArr[i]);
				checkBox[i].addActionListener(singleCheckboxAction);
				checkBox[i].setEnabled(false);
				if(i%2 == 0)
					checkBox[i].setBounds(20, 10+i*10, 200, 20);
				else
					checkBox[i].setBounds(240, 10+(i-1)*10, 200, 20);
				add(checkBox[i]);
			}
		}
	}
	
	protected void makeDataList() {
		String str = "";
		for (int i = 0; i < ckCL200A.length; i++)
			if(ckCL200A[i].isSelected()) str += clStrArr[i] + ",";
		for (int i = 0; i < ckCS200.length; i++)
			if(ckCS200[i].isSelected()) str += csStrArr[i] + ",";
		for (int i = 0; i < ckJAZ.length; i++)
			if(ckJAZ[i].isSelected()) str += jzStrArr[i] + ",";
		for (int i = 0; i < ckWeather.length; i++)
			if(ckWeather[i].isSelected()) str += wtStrArr[i] + ",";
		if(!str.equals(""))
			currentItemList = str.substring(0, str.length()-1);
		MyString.itemList = currentItemList;
	}
	
	ActionListener multiButtonAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent event) {
			if(event.getSource() == button[0])		buttonAction(0);
			else if(event.getSource() == button[1])	buttonAction(1);
			else if(event.getSource() == button[2])	buttonAction(2);
			else if(event.getSource() == button[3])	buttonAction(3);
			else if(event.getSource() == button[4])	buttonAction(4);
			else if(event.getSource() == button[5])	buttonAction(5);
			else if(event.getSource() == button[6])	buttonAction(6);
			makeDataList();
		}

		private void buttonAction(int number) {
			panel1.setCheckbox(msStrArr[number], button[number].isSelected());
			panel2.setCheckbox(msStrArr[number], button[number].isSelected());
			panel3.setCheckbox(msStrArr[number], button[number].isSelected());
		}
	};
	
	ActionListener singleCheckboxAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent event) {
			makeDataList();
		}
	}; 
	
}
