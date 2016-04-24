package witlab.nlas.etc;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

/**
 * @author ����
 * @since 2015-09-29
 */
@SuppressWarnings("serial")
public class MyBorder extends BorderLayout {
	
	/**
	 * �̰͸� ���� ���
	 * @return ȸ�� ���� Border ��ȯ
	 */
	public Border getBorder() {
		return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0xAA, 0xAA, 0xAA)));
	}
	
	/**
	 * ���� ��������� ����
	 * @param title
	 * @return title�� ��ġ�� ȸ�� ���� Border ��ȯ
	 */
	public Border getBorder(String title) {
		return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY), title);
	}
	
	/**
	 * ���� ��������� ����
	 * @return ���α׷��� Footer�� ���Ǵ� Border ��ȯ
	 */
	public Border getFooterBorder() {
		return BorderFactory.createEtchedBorder();
	}
}