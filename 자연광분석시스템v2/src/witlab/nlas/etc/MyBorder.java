package witlab.nlas.etc;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

/**
 * @author 김양수
 * @since 2015-09-29
 */
@SuppressWarnings("serial")
public class MyBorder extends BorderLayout {
	
	/**
	 * 이것만 자주 사용
	 * @return 회색 심플 Border 반환
	 */
	public Border getBorder() {
		return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0xAA, 0xAA, 0xAA)));
	}
	
	/**
	 * 현재 사용하지는 않음
	 * @param title
	 * @return title을 배치한 회색 심플 Border 반환
	 */
	public Border getBorder(String title) {
		return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY), title);
	}
	
	/**
	 * 현재 사용하지는 않음
	 * @return 프로그램의 Footer에 사용되는 Border 반환
	 */
	public Border getFooterBorder() {
		return BorderFactory.createEtchedBorder();
	}
}