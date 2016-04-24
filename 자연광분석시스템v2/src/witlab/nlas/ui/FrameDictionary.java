package witlab.nlas.ui;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * @author 김양수
 */
@SuppressWarnings("serial")
public class FrameDictionary extends JFrame {
	
	private static FrameDictionary instance;
	
	public static FrameDictionary getInstance() {
		if(instance == null) {
			instance = new FrameDictionary();
		}
		return instance;
	}
	
	public void showFrame() {
		setVisible(true);
	}
	
	private FrameDictionary() {
		// TODO 구현 예정
		setLocation(200, 200);
		setSize(300, 300);
		add(new JLabel("언어 사전 구현 예정"), JLabel.CENTER);
		setVisible(false);
	}
}
