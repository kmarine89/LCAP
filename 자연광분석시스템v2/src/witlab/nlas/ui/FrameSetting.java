package witlab.nlas.ui;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * @author 김양수
 */
@SuppressWarnings("serial")
public class FrameSetting extends JFrame {

public static FrameSetting instance;
	
	public static FrameSetting getInstance() {
		if(instance == null) {
			instance = new FrameSetting();
		}
		return instance;
	}
	
	public void showFrame() {
		setVisible(true);
	}
	
	private FrameSetting() {
		// TODO 구현예정
		setLocation(200, 200);
		setSize(300, 300);
		add(new JLabel("설정 화면 구현 예정"), JLabel.CENTER);
		setVisible(false);
	}
}
