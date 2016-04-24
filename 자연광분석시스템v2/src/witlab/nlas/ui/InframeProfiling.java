package witlab.nlas.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import witlab.nlas.graph.CategoryGraph;

/**
 * @author 김양수
 */
@SuppressWarnings("serial")
public class InframeProfiling extends JInternalFrame implements InframeAction, ComponentListener {

	String field;
	String[][] data;
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
	public InframeProfiling(int index) {
		super(MyString.TYPE_OF_PROFILING_FRAME, true, true, true, true);
		this.imagePath = "graph/"+MyString.TYPE_OF_PROFILING_FRAME+" "+index+".jpg";
		setBorder(new MyBorder().getBorder());
		setBounds(60, 60, 400, 300);
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

	@Override
	public JInternalFrame getFrame() {
		return this;
	}

	@Override
	public String getType() {
		return MyString.TYPE_OF_PROFILING_FRAME;
	}

	@Override
	public void execution() {
		// 정보 가져와서
		date = WindowFrame.ControllerPanel.dayJList.getSelectedValuesList();
		boolean filterOutlier = WindowFrame.ControllerPanel.checkFilterOLabel.getText().equals("ON");
		data = new String[date.size()][26];
		String[] time = null;
		int i=0;
		for (String str : date) {
			time = getRelativeTime(str.substring(12, 17), str.substring(18, 23));
			data[i] = db.getDataList(str.substring(0, 10), time, field);
			i++;
		}
		
		// 필터링 옵션에 따른 필터링 수행
		if(filterOutlier) {
			DataList tempField = new DataList(new String[] {field});
			DataArray tempData = new DataArray(data);
			data = Filtering.outlier(tempField, tempData).toSquareArray();
		}
		
		// Table 보여주고
		drawTable();
		
		// Graph 보여주고
		drawGraph();
	}

	private String[] getRelativeTime(String time1, String time2) {
		String[] time = new String[27];
		Calendar sunriseCal = Calendar.getInstance();
		Calendar sunsetCal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		for (int i = 0; i <= 6; i++) {
			sunriseCal.set(2015, 1, 1, Integer.parseInt(time1.substring(0, 2)), Integer.parseInt(time1.substring(3, 5))+10*i, 0);
			sunsetCal.set(2015, 1, 1, Integer.parseInt(time2.substring(0, 2)), Integer.parseInt(time2.substring(3, 5))-10*i, 0);
			time[i] = format.format(sunriseCal.getTime());
			time[26-i] = format.format(sunsetCal.getTime());
		}
		time[7] = "09:00";
		time[8] = "09:30";
		time[9] = "10:00";
		time[10] = "10:30";
		time[11] = "11:00";
		time[12] = "11:30";
		time[13] = "12:00";
		time[14] = "12:30";
		time[15] = "13:00";
		time[16] = "13:30";
		time[17] = "14:00";
		time[18] = "14:30";
		time[19] = "15:00";
		return time;
	}

	public void drawTable() {
		tablePanel.removeAll();
		final String[] field = { 
				"(일출)", "(일출)+10분", "(일출)+20분", "(일출)+30분", "(일출)+40분", "(일출)+50분", "(일출)+60분",  
				"09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30",  "14:00", "14:30", "15:00",
				"(일몰)-60분", "(일몰)-50분", "(일몰)-40분", "(일몰)-30분", "(일몰)-20분", "(일몰)-10분", "(일몰)",
		};
		
		//테이블 안에 들어갈 2차원 문자열 배열
		String[][] data = new String[field.length][date.size()+1];
		for (int i = 0; i < data.length; i++) {
			data[i][0] = field[i];
			for (int j = 1; j < data[0].length; j++) {
				data[i][j] = this.data[j-1][i];
			}
		}
		
		//테이블 상단 행 문자열 배치
		String[] table_date = new String[date.size()+1];
		table_date[0] = "날짜";
		for (int j = 1; j < table_date.length; j++) {
			table_date[j] = date.get(j-1);
		}
		
		DefaultTableModel tableModel = new DefaultTableModel(data, table_date);
		JTable table = new JTable(tableModel);
//		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane scrollPane = new JScrollPane(table);
		tablePanel.add(scrollPane);
		tablePanel.updateUI();
	}

	public void drawGraph() {
		String[] field = { 
				"R", "R10", "R20", "R30", "R40", "R50", "R60",  
				"09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30",  "14:00", "14:30", "15:00",
				"S60", "S50", "S40", "S30", "S20", "S10", "S"
		};
		CategoryGraph categoryGraph = new CategoryGraph();
		graphPanel.removeAll();
		
		int index = 0;
		for (String dateName : date) {
			DataArray addedRow = new DataArray(dateName);
			for (int i = 0; i < data[index].length; i++) {
				addedRow.add(new String[] { field[i], data[index][i] });
			}
			categoryGraph.addDataset(addedRow);
			index++;
		}
		
		categoryGraph.setXAxisName("Time");
		categoryGraph.setYAxisName("Value");
		categoryGraph.setTitleName(this.field);
		graphPanel.add(categoryGraph.getGraph());
	}

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
