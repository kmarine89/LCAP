package witlab.nlas.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import witlab.nlas.db.LightingDB;
import witlab.nlas.etc.DefineTime;
import witlab.nlas.etc.InframeManager;
import witlab.nlas.etc.MyBorder;
import witlab.nlas.etc.MyString;

/**
 * main 함수를 가지고 있는 Class
 * Main Window Frame을 생성 
 * @author 김양수
 */
@SuppressWarnings("serial")
public class WindowFrame extends JFrame {

	ToolBar northPanel = new ToolBar();
	ControllerPanel eastPanel = new ControllerPanel();
	FooterPanel southPanel = new FooterPanel();
	Desktop desktop = new Desktop();
	
	public static void main(String[] args) {
		new WindowFrame().setVisible(true);
	}
	
	/**
	 * 생성자...
	 * 1. 매뉴 생성
	 * 2. 툴바 생성
	 * 3. inframe을 띄울 desktop 생성
	 * 4. footer 생성
	 * 5. control을 담당하는 Panel 생성 <-- static Class
	 * @since 2015-09-26
	 */
	public WindowFrame() {
		setTitle(MyString.PROGRAM_NAME);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(600, 500));
		setPreferredSize(new Dimension(1000, 700));
		setLocation(100, 50);

		Container container = getContentPane();
		
		MenuBar menu = new MenuBar(desktop);
		setJMenuBar(menu);
		container.setLayout(new BorderLayout());
		container.add(northPanel, BorderLayout.NORTH);
		container.add(eastPanel, BorderLayout.EAST);
		container.add(southPanel, BorderLayout.SOUTH);
		container.add(desktop, BorderLayout.CENTER);
		
		pack();
		loadFrame();
	}
	
	/**
	 * 프로그램에 사용되는 frame들을 미리 loading 함
	 */
	private void loadFrame() {
		FrameDictionary.getInstance();
		FrameEditGraph.getInstance();
		FrameSelectedDate.getInstance();
		FrameSelectedItem.getInstance();
		FrameSetting.getInstance();
	}
	
	/**
	 * 툴바 생성 및 기능 구현 
	 * @author 김양수
	 * @since 2015-09-26
	 */
	class ToolBar extends JToolBar {
		final InframeManager manager = InframeManager.getInstance();
		String[] toolbarButton = new String[] { 
				"Open", "Save", 
				"RD", "SM", "CR", "RG", "CP", "PF", 
				"CIE", "Wave", 
				"Item", "Date", "Graph", 
				"Dic." };
		public ToolBar() {
			setPreferredSize(new Dimension(800, 30));
			JButton[] button = new JButton[toolbarButton.length];
			for (int i = 0; i < toolbarButton.length; i++) {
				button[i] = new JButton(toolbarButton[i]);
				button[i].setFocusable(false);
				button[i].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						String Command = event.getActionCommand();
						if(Command.equals(toolbarButton[0])) {		//Openfile
							new FrameOpenfile();
						} else if(Command.equals(toolbarButton[1])) {	//Savefile
							new FrameSavefile();
						} else if(Command.equals(toolbarButton[2])) {
							manager.addFrame(desktop, MyString.TYPE_OF_RAWDATA_FRAME);
						} else if(Command.equals(toolbarButton[3])) {
							manager.addFrame(desktop, MyString.TYPE_OF_SUMMARY_FRAME);
						} else if(Command.equals(toolbarButton[4])) {
							manager.addFrame(desktop, MyString.TYPE_OF_CORRELATION_FRAME);
						} else if(Command.equals(toolbarButton[5])) {
							manager.addFrame(desktop, MyString.TYPE_OF_REGRESSION_FRAME);
						} else if(Command.equals(toolbarButton[6])) {
							manager.addFrame(desktop, MyString.TYPE_OF_COMPARISON_FRAME);
						} else if(Command.equals(toolbarButton[7])) {
							manager.addFrame(desktop, MyString.TYPE_OF_PROFILING_FRAME);
						} else if(Command.equals(toolbarButton[8])) {
							manager.addFrame(desktop, MyString.TYPE_OF_CIEGRAPH_FRAME);
						} else if(Command.equals(toolbarButton[9])) {
							manager.addFrame(desktop, MyString.TYPE_OF_WAVEGRAPH_FRAME);
						} else if(Command.equals(toolbarButton[10])) {	//Item List Display
							FrameSelectedItem.getInstance().showFrame();
						} else if(Command.equals(toolbarButton[11])) {	//Date List Display
							FrameSelectedDate.getInstance().showFrame();
						} else if(Command.equals(toolbarButton[12])) {	//Edit Graph Display
							FrameEditGraph.getInstance().showFrame();
						} else if(Command.equals(toolbarButton[13])) {	//Dictionary Display
							FrameDictionary.getInstance().showFrame();
						}
					}
				});
				//2, 6, 9 번째 툴바마다 빈 칸 넣기
				if(i == 2 || i == 8 || i == 10 || i == 13) addSeparator();
				add(button[i]);
			}
		}
	}
	
	/**
	 * Control Panel 생성 및 기능 구현 
	 * @author 김양수
	 * @since 2015-09-26
	 */
	public static class ControllerPanel extends JPanel {
		
		public static JTextField sTimeField = new JTextField("05:00");
		public static JTextField eTimeField = new JTextField("20:00");
		public static DefaultListModel<String> dayModel = new DefaultListModel<>();
		public static JList<String> dayJList = new JList<String>(dayModel);
		public static JLabel checkFilterOLabel = new JLabel("OFF", JLabel.CENTER);
		public static JComboBox<String> lightingCombo;
		
		String[] buttonStr = new String[] { "DEL", "INIT", "Execution [F8]" };
		JButton[] button;
		String[] radioStr1 = new String[] { "T1", "T2", "T3", "ALL" };
		String[] radioStr2 = new String[] { "STime", "ETime", "ATime" };
		JRadioButton[] radio1;
		JRadioButton[] radio2;
		
		LightingDB ldb = LightingDB.getInstance();
		
		private void addComponent(GridBagConstraints c, Component component,int gridx, int gridy, 
				int gridwidth, int gridheight, double weightx, double weighty) {
			c.gridx = gridx;
			c.gridy = gridy;
			c.gridwidth = gridwidth;
			c.gridheight = gridheight;
			c.weightx = weightx; 
			c.weighty = weighty;
			add(component, c);
		}
		
		public ControllerPanel() {
			setPreferredSize(new Dimension(200, 400));
			setBackground(new Color(0xE0, 0xE0, 0xE0));
			setBorder(new MyBorder().getBorder());
			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.insets = new Insets(5, 5, 5, 5);

			dayJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			JScrollPane scrollPane = new JScrollPane(dayJList);
			
			String[] lightingStrArr = ldb.getLightingList().toArray();
			lightingCombo = new JComboBox<>(lightingStrArr);
			
			button = new JButton[buttonStr.length];
			JPanel panel = new JPanel(new FlowLayout());
			panel.setOpaque(false);
			for (int i = 0; i < buttonStr.length; i++) {
				button[i] = new JButton(buttonStr[i]);
				button[i].setFocusable(false);
				button[i].addActionListener(controlAction);
				panel.add(button[i]);
			}
			
			checkFilterOLabel.setFont(new Font(Font.SERIF, Font.BOLD, 15));
			checkFilterOLabel.setForeground(new Color(0xEE, 0x00, 0x00));
			
			JPanel panelRadio1 = new JPanel(new GridLayout(1, radioStr1.length));
			JPanel panelRadio2 = new JPanel(new GridLayout(1, radioStr2.length));
			panelRadio1.setOpaque(false);
			panelRadio2.setOpaque(false);
			ButtonGroup bg = new ButtonGroup();
			radio1 = new JRadioButton[radioStr1.length];
			for (int i = 0; i < radio1.length; i++) {
				radio1[i] = new JRadioButton(radioStr1[i]);
				radio1[i].setOpaque(false);
				radio1[i].addActionListener(controlAction);
				bg.add(radio1[i]);
				panelRadio1.add(radio1[i]);
			}
			radio2 = new JRadioButton[radioStr2.length];
			for (int i = 0; i < radio2.length; i++) {
				radio2[i] = new JRadioButton(radioStr2[i]);
				radio2[i].setOpaque(false);
				radio2[i].addActionListener(controlAction);
				bg.add(radio2[i]);
				panelRadio2.add(radio2[i]);
			}
			
			int layer = 0;
			addComponent(c, scrollPane, 0, layer, 2, 1, 1, 1);
			addComponent(c, lightingCombo, 0, ++layer, 2, 1, 1, 0);
			c.insets = new Insets(0, 0, 0, 0);
			addComponent(c, panel, 0, ++layer, 2, 1, 1, 0);
			c.insets = new Insets(5, 5, 5, 5);
			addComponent(c, new JLabel(), 0, ++layer, 2, 1, 1, 0);
			addComponent(c, new JLabel("Start Time", JLabel.RIGHT), 0, ++layer, 1, 1, 0.5, 0);
			addComponent(c, sTimeField, 1, layer, 1, 1, 0.5, 0);
			addComponent(c, new JLabel("End Time", JLabel.RIGHT), 0, ++layer, 1, 1, 0.5, 0);
			addComponent(c, eTimeField, 1, layer, 1, 1, 0.5, 0);
			addComponent(c, panelRadio1, 0, ++layer, 2, 1, 1, 0);
			addComponent(c, panelRadio2, 0, ++layer, 2, 1, 1, 0);
			addComponent(c, new JLabel("Outlier Filtering", JLabel.RIGHT), 0, ++layer, 1, 1, 0.5, 0);
			addComponent(c, checkFilterOLabel, 1, layer, 1, 1, 0.5, 0);
			addComponent(c, new JLabel(), 0, ++layer, 2, 1, 1, 0);
			addComponent(c, button[2], 0, ++layer, 2, 1, 1, 0);
		}
		
		ActionListener controlAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String Command = event.getActionCommand();
				if(Command.equals(buttonStr[0])) {
					dayModel.remove(dayJList.getSelectedIndex());
				} else if(Command.equals(buttonStr[1])) {
					dayModel.removeAllElements();
				} else if(Command.equals(buttonStr[2])) {
					InframeManager manager = InframeManager.getInstance();
					if(manager.getCurrentFrame() != null)
						manager.getCurrentFrame().execution();
				} 
				
				else if(Command.equals(radioStr1[0])) {
					String temp1 = dayJList.getSelectedValue().substring(12, 17);	//일출시간
					String temp2 = getSunrisePlusHour(temp1);
					sTimeField.setText(temp1);
					eTimeField.setText(temp2);
				} else if(Command.equals(radioStr1[1])) {
					sTimeField.setText("12:00");
					eTimeField.setText("13:00");
				} else if(Command.equals(radioStr1[2])) {
					String temp1 = dayJList.getSelectedValue().substring(18, 23);	//일몰시간
					String temp2 = getSunsetMinuseHour(temp1);
					sTimeField.setText(temp2);
					eTimeField.setText(temp1);
				} else if(Command.equals(radioStr1[3])) {
					sTimeField.setText(dayJList.getSelectedValue().substring(12, 17));
					eTimeField.setText(dayJList.getSelectedValue().substring(18, 23));
				}
				
				else if(Command.equals(radioStr2[0])) {
					String selectedDate = dayJList.getSelectedValue().substring(0, 10);
					String sTime = dayJList.getSelectedValue().substring(12, 17);
					String eTime = dayJList.getSelectedValue().substring(18, 23);
					DefineTime defineTime = new DefineTime(selectedDate, sTime, eTime);
					String temp1 = defineTime.getStartTime();
					String temp2 = getSunrisePlusHour(temp1);
					sTimeField.setText(temp1);
					eTimeField.setText(temp2);
				} else if(Command.equals(radioStr2[1])) {
					String selectedDate = dayJList.getSelectedValue().substring(0, 10);
					String sTime = dayJList.getSelectedValue().substring(12, 17);
					String eTime = dayJList.getSelectedValue().substring(18, 23);
					DefineTime defineTime = new DefineTime(selectedDate, sTime, eTime);
					String temp1 = defineTime.getEndTime();
					String temp2 = getSunsetMinuseHour(temp1);
					sTimeField.setText(temp2);
					eTimeField.setText(temp1);
				} else if(Command.equals(radioStr2[2])) {
					String selectedDate = dayJList.getSelectedValue().substring(0, 10);
					String sTime = dayJList.getSelectedValue().substring(12, 17);
					String eTime = dayJList.getSelectedValue().substring(18, 23);
					DefineTime defineTime = new DefineTime(selectedDate, sTime, eTime);
					String temp1 = defineTime.getStartTime();
					String temp2 = defineTime.getEndTime();
					sTimeField.setText(temp1);
					eTimeField.setText(temp2);
				}
			}

			private String getSunsetMinuseHour(String temp1) {
				char[] chArr = temp1.toCharArray();
				chArr[1] = (char) (chArr[1]-1);
				return String.valueOf(chArr);
			}

			private String getSunrisePlusHour(String temp1) {
				char[] chArr = temp1.toCharArray();
				chArr[1] = (char) (chArr[1]+1);
				return String.valueOf(chArr);
			}
		};
	}

	/**
	 * Footer 생성
	 * @author 김양수
	 * @since 2015-09-26
	 */
	class FooterPanel extends JPanel {
		public FooterPanel() {
			setPreferredSize(new Dimension(800, 40));
			setBackground(new Color(0xE0, 0xE0, 0xE0));
			setBorder(new MyBorder().getBorder());
			setLayout(new GridLayout(1, 1));
			add(new JLabel(MyString.PROGROM_FOOTER, JLabel.CENTER));
		}
	}
	
	/**
	 * inframe을 띄울 DesktopPane 정의
	 * @author 김양수
	 * @since 2015-09-26
	 */
	class Desktop extends JDesktopPane {
		public Desktop() {
			setBackground(new Color(0xEE, 0xEE, 0xEE));
			setDragMode(JDesktopPane.LIVE_DRAG_MODE);
		}
	}

}

/**
 * @author 김양수
 */
@SuppressWarnings("serial")
class MenuBar extends JMenuBar {
	
	String[] fileItems = new String[] { "New", "Open File...", "Save As...", "Export Excel", "Close", "Close All", "Exit" };
	String[] editItems = new String[] { "Filter", "List Items...", "List Dates...", "Edit Graph..." };
	String[] runItems = new String[] { "Run" };
	String[] otherItems = new String[] { "Dictionary", "Setting" };
	String[] newItems = new String[] { "Raw Data", "Summary", "Correlation", "Regression", "Comparison", "Profiling", "CIE Graph", "Wave Graph" };
	String[] filterItems = new String[] { "Outlier" };
	char[] fileShortcuts = { 'N', 'O', 'S', 'P', 'W', 'Q', 'E' };
	String[] runShortcuts = { "F8" };
	JMenuItem[] filterItem = new JCheckBoxMenuItem[filterItems.length];
	
	JDesktopPane desktop = null;
	
	/**
	 * 생성자..
	 * 매뉴 생성 및 기능 구현
	 * @param desktop
	 * @since 2015-09-29
	 */
	public MenuBar(final JDesktopPane desktop) {
		this.desktop = desktop;
		final InframeManager manager = InframeManager.getInstance();
		
		//각  메뉴 리스너 정의 
		ActionListener fileListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String Command = event.getActionCommand();
				if(Command.equals(newItems[0])) {		//Raw Data
					manager.addFrame(desktop, MyString.TYPE_OF_RAWDATA_FRAME);
				} else if(Command.equals(newItems[1])) {
					manager.addFrame(desktop, MyString.TYPE_OF_SUMMARY_FRAME);
				} else if(Command.equals(newItems[2])) {
					manager.addFrame(desktop, MyString.TYPE_OF_CORRELATION_FRAME);
				} else if(Command.equals(newItems[3])) {
					manager.addFrame(desktop, MyString.TYPE_OF_REGRESSION_FRAME);
				} else if(Command.equals(newItems[4])) {
					manager.addFrame(desktop, MyString.TYPE_OF_COMPARISON_FRAME);
				} else if(Command.equals(newItems[5])) {
					manager.addFrame(desktop, MyString.TYPE_OF_PROFILING_FRAME);
				} else if(Command.equals(newItems[6])) {
					manager.addFrame(desktop, MyString.TYPE_OF_CIEGRAPH_FRAME);
				} else if(Command.equals(newItems[7])) {
					manager.addFrame(desktop, MyString.TYPE_OF_WAVEGRAPH_FRAME);
				} else if(Command.equals(fileItems[1])) {	//Open File
					new FrameOpenfile();
				} else if(Command.equals(fileItems[2])) {	//Save As
					new FrameSavefile();
				} else if(Command.equals(fileItems[3])) {	//Export Excel
					new FrameExportExcel();
				} else if(Command.equals(fileItems[4])) {	//Close
					manager.removeCurrentFrame(desktop);
				} else if(Command.equals(fileItems[5])) {	//Close All
					manager.removeAll(desktop);
				} else if(Command.equals(fileItems[6])) { //Exit
					System.exit(0);
				}
			
			}
		};
		
		ActionListener editListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String Command = event.getActionCommand();
				if(Command.equals(filterItems[0]) || event.getActionCommand().equals(filterItems[1])) {//filter
					JLabel label1 = WindowFrame.ControllerPanel.checkFilterOLabel;
					if(filterItem[0].isSelected()) {
						label1.setText("ON");
						label1.setForeground(new Color(0x00, 0xAA, 0x00));
					} else {
						label1.setText("OFF");
						label1.setForeground(new Color(0xEE, 0x00, 0x00));
					}
				} else if(Command.equals(editItems[1])) {	//Item List Display
					FrameSelectedItem.getInstance().showFrame();
				} else if(Command.equals(editItems[2])) {	//Date List Display
					FrameSelectedDate.getInstance().showFrame();
				} else if(Command.equals(editItems[3])) {	//Graph Editor Display
					FrameEditGraph.getInstance().showFrame();
				}
			}
		};

		ActionListener runListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String Command = event.getActionCommand();
				if(Command.equals(runItems[0])) {
					InframeManager manager = InframeManager.getInstance();
					if(manager.getCurrentFrame() != null)
						manager.getCurrentFrame().execution();
				}
			}
		};
		
		ActionListener otherListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String Command = event.getActionCommand();
				if(Command.equals(otherItems[0])) {	//Dictionary Display
					FrameDictionary.getInstance().showFrame();
				} else if(Command.equals(otherItems[1])) {	//Setting Display
					FrameSetting.getInstance().showFrame();
				}
			}
		};
		
		//주메뉴 객체 생성
		JMenu fileMenu = new JMenu("File");
		JMenu editMenu = new JMenu("Edit");
		JMenu runMenu = new JMenu("Run");
		JMenu otherMenu = new JMenu("Other");
		fileMenu.setMnemonic('F');
		editMenu.setMnemonic('E');
		runMenu.setMnemonic('R');
		otherMenu.setMnemonic('O');
		
		//File 메뉴의 아이템 생성
		JMenu newMenu = new JMenu(fileItems[0]);
		newMenu.setMnemonic(fileShortcuts[0]);
		newMenu.addActionListener(fileListener);
		fileMenu.add(newMenu);
		for (int i = 1; i < fileItems.length; i++) {
			JMenuItem item = new JMenuItem(fileItems[i]);
//			if(i != 3)
			item.setAccelerator(KeyStroke
					.getKeyStroke(fileShortcuts[i], ActionEvent.CTRL_MASK, false));
			item.addActionListener(fileListener);
			fileMenu.add(item);
		}
		fileMenu.insertSeparator(2);
		fileMenu.insertSeparator(5);
		fileMenu.insertSeparator(8);
		
		//File-new 메뉴의 아이템 생성
		JMenuItem subItem;
		for (int i = 0; i < newItems.length; i++) {
			newMenu.add(subItem = new JMenuItem(newItems[i]));
			subItem.addActionListener(fileListener);
		}

		//Edit 메뉴의 아이템 생성
		JMenu filterMenu = new JMenu(editItems[0]);
		filterMenu.addActionListener(editListener);
		editMenu.add(filterMenu);
		for (int i = 1; i < editItems.length; i++) {
			JMenuItem item = new JMenuItem(editItems[i]);
			item.addActionListener(editListener);
			editMenu.add(item);
		}
		editMenu.insertSeparator(1);
		
		//Edit-Filter 메뉴의 아이템 생성
		for (int i = 0; i < filterItems.length; i++) {
			filterItem[i] = new JCheckBoxMenuItem(filterItems[i]);
			filterItem[i].addActionListener(editListener);
			filterMenu.add(filterItem[i]);
		}
		
		//Run 메뉴의 아이템 생성
		for (int i = 0; i < runItems.length; i++) {
			JMenuItem item = new JMenuItem(runItems[i]);
			item.setAccelerator(KeyStroke.getKeyStroke(runShortcuts[i]));
			item.addActionListener(runListener);
			runMenu.add(item);
		}
		
		//Other 메뉴의 아이템 생성
		for (int i = 0; i < otherItems.length; i++) {
			JMenuItem item = new JMenuItem(otherItems[i]);
			item.addActionListener(otherListener);
			otherMenu.add(item);
		}
		otherMenu.insertSeparator(1);
		
		add(fileMenu);
		add(editMenu);
		add(runMenu);
		add(otherMenu);
	}
	
}
