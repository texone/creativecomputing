package cc.creativecomputing.controlui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.controlui.timeline.view.SwingGuiConstants;

public class PropertyPopUp extends JPopupMenu {
	
	private class AddToTimelineAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent theArg0) {
			_myControlComponent.timeline().addTrack(_myProperty);
		}
	}
	
	private class WriteToTimelineAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent theArg0) {
			_myControlComponent.timeline().writeValues(_myProperty);
		}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7869280073371142253L;
	
	private CCPropertyHandle<?> _myProperty;
	
	private CCControlComponent _myControlComponent;

	public PropertyPopUp(CCPropertyHandle<?> theProperty, CCControlComponent theControlComponent) {
		_myProperty = theProperty;
		_myControlComponent = theControlComponent;

		JMenuItem entryHead = new JMenuItem("Property Actions");
		entryHead.setFont(SwingGuiConstants.ARIAL_11);
		add(entryHead);
		
		addSeparator();
		
		JMenuItem myAddToTimelineAction = new JMenuItem("Add To Timeline");
		myAddToTimelineAction.setFont(SwingGuiConstants.ARIAL_11);
		myAddToTimelineAction.addActionListener(new AddToTimelineAction());
		add(myAddToTimelineAction);
		
		JMenuItem myWriteToTimelineAction = new JMenuItem("Write To Timeline");
		myWriteToTimelineAction.setFont(SwingGuiConstants.ARIAL_11);
		myWriteToTimelineAction.addActionListener(new WriteToTimelineAction());
		add(myWriteToTimelineAction);
		
	}
	
}