package witlab.nlas.ui;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * @author ����
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
		// TODO ���� ����
		setLocation(200, 200);
		setSize(300, 300);
		add(new JLabel("��� ���� ���� ����"), JLabel.CENTER);
		setVisible(false);
	}
}
