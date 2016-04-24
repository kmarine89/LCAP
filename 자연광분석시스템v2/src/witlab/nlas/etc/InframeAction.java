package witlab.nlas.etc;

import javax.swing.JInternalFrame;

/**
 * inframe에서 사용될 인터페이스
 * @author 김양수
 */
public interface InframeAction {

	JInternalFrame getFrame();
	String getType();
	void execution();
	boolean isSelected();
	
}
