package witlab.nlas.ui;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * @author ����
 */
@SuppressWarnings("serial")
public class FrameSavefile extends JFrame {
	public FrameSavefile() {
		// TODO ��������
		setLocation(200, 200);
		setSize(200, 100);
		add(new JLabel("���� ����"), JLabel.CENTER);
		setVisible(true);
	}
}
