package witlab.nlas.ui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import witlab.nlas.db.NaturallightDB;

/**
 * @author 김양수
 */
@SuppressWarnings("serial")
public class FrameSelectedDate extends JFrame {

private static FrameSelectedDate instance;
	
	public static FrameSelectedDate getInstance() {
		if(instance == null) {
			instance = new FrameSelectedDate();
		}
		return instance;
	}
	
	public void showFrame() {
		setVisible(true);
	}
	
	/**
	 * 생성자...
	 * 날짜 선택하기 위한 Frame을 생성
	 * @since 2015-09-30
	 */
	private FrameSelectedDate() {
		setLayout(null);
		setLocation(200, 100);
		setSize(400, 300);
		setResizable(false);
		setVisible(false);
		
		NaturallightDB db = NaturallightDB.getInstance();
		Container ct = getContentPane();

		final String[] dayList = db.getDate().toArray();
		final JList<String> list = new JList<String>(dayList);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setBounds(5, 5, 300, 265);
		ct.add(scrollPane);
		
		JButton button = new JButton("ADD");
		button.setBounds(310, 5, 80, 265);
		button.setFocusable(false);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				int[] index = list.getSelectedIndices();
				String[] tempArr = new String[index.length];
				for (int i = 0; i < index.length; i++) {
					String temp = dayList[index[i]].substring(0, 10)+" ("+dayList[index[i]].substring(20, 25)
							+"~"+dayList[index[i]].substring(34, 39)+")";
					tempArr[i] = temp;
					
				}
				WindowFrame.ControllerPanel.dayModel.removeAllElements();
				for (String string : tempArr)
					WindowFrame.ControllerPanel.dayModel.addElement(string);
				WindowFrame.ControllerPanel.dayJList.setSelectedIndex(0);
				setVisible(false);
			}
		});
		ct.add(button);
	}
}
