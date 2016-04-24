package witlab.nlas.etc;

import java.beans.PropertyVetoException;
import java.util.ArrayList;

import javax.swing.JDesktopPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import witlab.nlas.ui.InframeCIEGraph;
import witlab.nlas.ui.InframeComparison;
import witlab.nlas.ui.InframeCorrelation;
import witlab.nlas.ui.InframeProfiling;
import witlab.nlas.ui.InframeRawdata;
import witlab.nlas.ui.InframeRegression;
import witlab.nlas.ui.InframeSummary;
import witlab.nlas.ui.InframeWaveGraph;
import witlab.nlas.ui.WindowFrame;

/**
 * @author 김양수
 */
public class InframeManager {

	private ArrayList<InframeAction> list = new ArrayList<InframeAction>();
	
	private static InframeManager instance = null;
	
	public static InframeManager getInstance() {
		if(instance == null)
			instance = new InframeManager();
		return instance;
	}
	
	/**
	 * type별 inframe의 개수를 파악한 후(index),
	 * index를 매개변수로 inframe 생성
	 * 생선된 inframe을 선택
	 * @param desktop
	 * @param type=MyString에 있는 문자열 상수를 사용
	 * @since 2015-09-29
	 */
	public void addFrame(final JDesktopPane desktop, final String type) {
		int[] count = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		for (InframeAction inframe : list) {
			if(inframe.getType().equals(MyString.TYPE_OF_RAWDATA_FRAME)) count[0]++;
			else if(inframe.getType().equals(MyString.TYPE_OF_SUMMARY_FRAME)) count[1]++;
			else if(inframe.getType().equals(MyString.TYPE_OF_CORRELATION_FRAME)) count[2]++;
			else if(inframe.getType().equals(MyString.TYPE_OF_REGRESSION_FRAME)) count[3]++;
			else if(inframe.getType().equals(MyString.TYPE_OF_COMPARISON_FRAME)) count[4]++;
			else if(inframe.getType().equals(MyString.TYPE_OF_PROFILING_FRAME)) count[5]++;
			else if(inframe.getType().equals(MyString.TYPE_OF_CIEGRAPH_FRAME)) count[6]++;
			else if(inframe.getType().equals(MyString.TYPE_OF_WAVEGRAPH_FRAME)) count[7]++;
		}
		InframeAction inframe = null;
		if(type.equals(MyString.TYPE_OF_RAWDATA_FRAME)) {
			inframe = new InframeRawdata(count[0]);
		} else if(type.equals(MyString.TYPE_OF_SUMMARY_FRAME)) {
			inframe = new InframeSummary(count[1]);
		} else if(type.equals(MyString.TYPE_OF_CORRELATION_FRAME)) {
			inframe = new InframeCorrelation(count[2]);
		} else if(type.equals(MyString.TYPE_OF_REGRESSION_FRAME)) {
			inframe = new InframeRegression(count[3]);
		} else if(type.equals(MyString.TYPE_OF_COMPARISON_FRAME)) {
			inframe = new InframeComparison(count[4]);
		} else if(type.equals(MyString.TYPE_OF_PROFILING_FRAME)) {
			inframe = new InframeProfiling(count[5]);
		} else if(type.equals(MyString.TYPE_OF_CIEGRAPH_FRAME)) {
			inframe = new InframeCIEGraph(count[6]);
		} else if(type.equals(MyString.TYPE_OF_WAVEGRAPH_FRAME)) {
			inframe = new InframeWaveGraph(count[7]);
		} 
		inframe.getFrame().addInternalFrameListener(new InternalFrameListener() {
			public void internalFrameOpened(InternalFrameEvent arg0) {}
			public void internalFrameIconified(InternalFrameEvent arg0) {}
			public void internalFrameDeiconified(InternalFrameEvent arg0) {}
			public void internalFrameDeactivated(InternalFrameEvent arg0) {}
			public void internalFrameClosing(InternalFrameEvent arg0) {
				removeCurrentFrame(desktop);
			}
			public void internalFrameClosed(InternalFrameEvent arg0) {}
			public void internalFrameActivated(InternalFrameEvent arg0) {
				if(type == MyString.TYPE_OF_COMPARISON_FRAME || type == MyString.TYPE_OF_PROFILING_FRAME) {
					WindowFrame.ControllerPanel.dayJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				} else {
					WindowFrame.ControllerPanel.dayJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					int index = WindowFrame.ControllerPanel.dayJList.getSelectedIndex();
					WindowFrame.ControllerPanel.dayJList.setSelectedIndex(index);
				}
			}
		});
		desktop.add(inframe.getFrame());
		list.add(inframe);
		try {
			inframe.getFrame().setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return 현재 선택되어져 있는 inframe
	 * @since 2015-09-29
	 */
	public InframeAction getCurrentFrame() {
		for (InframeAction inframe : list) {
			if(inframe.isSelected()) {
				return inframe;
			}
		}
		return null;
	}
	
	/**
	 * 현재 선택되어져 있는 inframe을 삭제한 후,
	 * list의 마지막 inframe을 선택
	 * @param desktop
	 * @since 2015-09-29
	 */
	public void removeCurrentFrame(JDesktopPane desktop) {
		int index = 0;
		try {
			for (InframeAction inframe : list) {
				if(inframe.isSelected()) {
					list.remove(index);
					desktop.remove(inframe.getFrame());
					desktop.updateUI();
				}
				index++;
			}
		} catch (Exception e) {}
		try {
			if(list.size()>0)
				list.get(list.size()-1).getFrame().setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}

	/**
	 * list의 모든 inframe을 삭제
	 * @param desktop
	 * @since 2015-09-29
	 */
	public void removeAll(JDesktopPane desktop) {
		list.removeAll(list);
		desktop.removeAll();
		desktop.updateUI();
	}
	
}
