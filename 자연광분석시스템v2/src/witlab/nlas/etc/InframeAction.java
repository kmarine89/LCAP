package witlab.nlas.etc;

import javax.swing.JInternalFrame;

/**
 * inframe���� ���� �������̽�
 * @author ����
 */
public interface InframeAction {

	JInternalFrame getFrame();
	String getType();
	void execution();
	boolean isSelected();
	
}
