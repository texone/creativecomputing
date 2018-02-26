package cc.creativecomputing.controlui.timeline.view;

import javax.swing.JFrame;
import javax.swing.JMenu;

import cc.creativecomputing.controlui.timeline.controller.TimelineContainer;
import cc.creativecomputing.controlui.timeline.view.transport.SwingTransportView;
import cc.creativecomputing.controlui.view.menu.SwingFileMenu;
import cc.creativecomputing.controlui.view.menu.SwingTimelineMenu;

public class SwingTimelineContainerView {

	private SwingTransportView _myTransport;
	
	private JMenu _myTimelineMenue;
	private JMenu _myFileMenue;
	
	private JFrame _myMainFrame;
	
	public SwingTimelineContainerView(JFrame theMainFrame){
		_myMainFrame = theMainFrame;
	}
	
	public void timelineContainer(TimelineContainer theTimelineContainer){
		_myTransport = new SwingTransportView(theTimelineContainer);
		_myTimelineMenue = new SwingTimelineMenu(theTimelineContainer);
		_myFileMenue = new SwingFileMenu(theTimelineContainer);
	}

	public JMenu fileMenu(){
		return _myFileMenue;
	}
	
	public JMenu timelineMenu(){
		return _myTimelineMenue;
	}
	
	public SwingTransportView transportView() {
		return _myTransport;
	}

	public SwingTimelineView createView(TimelineContainer theTimelineContainer) {
		return new SwingTimelineView(_myMainFrame, theTimelineContainer);
	}
}
