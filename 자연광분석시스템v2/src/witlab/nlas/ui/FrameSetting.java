package witlab.nlas.ui;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * @author ����
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
		// TODO ��������
		setLocation(200, 200);
		setSize(300, 300);
		add(new JLabel("���� ȭ�� ���� ����"), JLabel.CENTER);
		setVisible(false);
	}
}
