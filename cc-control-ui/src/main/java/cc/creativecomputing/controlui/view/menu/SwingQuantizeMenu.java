package cc.creativecomputing.controlui.view.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import cc.creativecomputing.controlui.timeline.controller.TimelineContainer;
import cc.creativecomputing.controlui.timeline.controller.quantize.CCQuatizeMode;


@SuppressWarnings("serial")
public class SwingQuantizeMenu extends JMenu {
	
	private class QuantizeAction implements ActionListener{
		
		private final CCQuatizeMode _myQuantizeMode;
		
		private QuantizeAction(CCQuatizeMode theQuantizeMode){
			_myQuantizeMode = theQuantizeMode;
		}

		@Override
		public void actionPerformed(ActionEvent theE) {
			_myTimelineContainer.activeTimeline().quantizer(_myQuantizeMode);
		}
		
	}
	
	private TimelineContainer _myTimelineContainer;
	
	private ButtonGroup _myButtonGroup;
	
	public SwingQuantizeMenu(TimelineContainer theTimelineContainer) {
		super("Quantize");

		setMnemonic(KeyEvent.VK_Q);

		_myTimelineContainer = theTimelineContainer;
		
		_myButtonGroup = new ButtonGroup();

		for(CCQuatizeMode myMode:CCQuatizeMode.values()){
			add(createItem(myMode));
		}
	}

	private JRadioButtonMenuItem createItem(CCQuatizeMode theMode){
		JRadioButtonMenuItem myResult = new JRadioButtonMenuItem(theMode.desc());
		myResult.addActionListener(new QuantizeAction(theMode));
		_myButtonGroup.add(myResult);
		return myResult;
	}
}
