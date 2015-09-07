package cc.creativecomputing.controlui.timeline.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JViewport;

import cc.creativecomputing.control.handles.CCEnumPropertyHandle;
import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.control.handles.CCPathHandle;
import cc.creativecomputing.control.handles.CCStringPropertyHandle;
import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.controlui.controls.CCClipTrackDataRenderer;
import cc.creativecomputing.controlui.controls.CCPathTrackDataRenderer;
import cc.creativecomputing.controlui.controls.CCStringTrackDataRenderer;
import cc.creativecomputing.controlui.controls.CCUIStyler;
import cc.creativecomputing.controlui.timeline.controller.TimelineContainer;
import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.controller.arrange.CCClipTrackObject;
import cc.creativecomputing.controlui.timeline.controller.arrange.SwingClipTrackObjectDialog;
import cc.creativecomputing.controlui.timeline.controller.arrange.SwingGroupTrackObjectDialog;
import cc.creativecomputing.controlui.timeline.controller.track.EventTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.GroupTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.TrackController;
import cc.creativecomputing.controlui.timeline.view.SwingMultiTrackPanel.MultiTrackMouseAdapter;
import cc.creativecomputing.controlui.timeline.view.track.SwingEventTrackDialog;
import cc.creativecomputing.controlui.timeline.view.track.SwingGroupTrackView;
import cc.creativecomputing.controlui.timeline.view.track.SwingTrackDataRenderer;
import cc.creativecomputing.controlui.timeline.view.track.SwingTrackDataView;
import cc.creativecomputing.controlui.timeline.view.track.SwingTrackView;
import cc.creativecomputing.core.logging.CCLog;


@SuppressWarnings("serial")
public class SwingTimelineView extends JSplitPane implements ComponentListener {
	
	private JLayeredPane _myPane;
	
	private JScrollPane _myScrollPane;
	private JScrollBar _myJScrollbar;
    private JViewport _myViewport;
	private SwingMultiTrackPanel _myRulerPanel;
	private SwingMultiTrackPanel _myMultiTrackPanel;
	private SwingRulerView _myRuler;

	private SwingToolChooserPopup _myToolChooserPopup;
	
	private SwingTimelineScrollbar _myScrollbar;
	
	private List<Object> _myTracks = new ArrayList<Object>();
	
	private TimelineController _myTimelineController;
	
	private SwingClipTrackObjectDialog _myClipTrackObjectDialog;
	private SwingGroupTrackObjectDialog _myGroupTrackObjectDialog;
	
	private final JFrame _myMainFrame;

	public SwingTimelineView(JFrame theMainFrame, TimelineContainer theTimelineContainer) {
		super(JSplitPane.VERTICAL_SPLIT);
		
		_myMainFrame = theMainFrame;
		_myPane = new JLayeredPane();
		_myPane.setLayout(new BorderLayout());
//		_myPane.add(createColoredLabel("TEXONE", Color.RED, new Point(300,100)), 3);

		_myRuler = new SwingRulerView(_myMainFrame, null);
		Path myRulerPath = Paths.get("ruler");
		_myRulerPanel = new SwingMultiTrackPanel(_myPane);
		_myRulerPanel.insertTrackView(new JPanel(), myRulerPath, 0, 30, true);
		_myRulerPanel.insertTrackDataView(_myRuler, myRulerPath, 0);
		 
		_myMultiTrackPanel = new SwingMultiTrackPanel(_myPane);
		MultiTrackMouseAdapter myAdapter = new MultiTrackMouseAdapter(_myMainFrame, _myMultiTrackPanel, _myRulerPanel);
		_myMultiTrackPanel.addMouseListener(myAdapter);
		_myMultiTrackPanel.addMouseMotionListener(myAdapter);
		
		_myViewport = new JViewport();
        _myViewport.add(_myMultiTrackPanel);
        _myViewport.setBounds(0, 0, 300, 300);
        
        _myScrollPane = new JScrollPane(_myMultiTrackPanel);
		_myScrollPane.getVerticalScrollBar().setUnitIncrement(20);
		_myScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		_myJScrollbar = new JScrollBar(JScrollBar.HORIZONTAL);
		_myJScrollbar.setMinimum(0);
		_myJScrollbar.setMaximum(1000);
		 _myPane.add(_myJScrollbar, BorderLayout.PAGE_END);
		
		_myClipTrackObjectDialog = new SwingClipTrackObjectDialog(theTimelineContainer);
		_myGroupTrackObjectDialog = new SwingGroupTrackObjectDialog();
		
		_myPane.add(_myScrollPane, BorderLayout.CENTER);
		
		_myPane.addComponentListener(this);
		
		CCUIStyler.styleSplitPane(this);
		setDividerLocation(30);
		setEnabled(false);
		
		setTopComponent(_myRulerPanel);
		setBottomComponent(_myPane);
	}

	private JLabel createColoredLabel(String text, Color color, Point origin) {
		JLabel label = new JLabel(text);
		label.setVerticalAlignment(JLabel.TOP);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setOpaque(true);
		label.setBackground(color);
		label.setForeground(Color.black);
		label.setBorder(BorderFactory.createLineBorder(Color.black));
		label.setBounds(origin.x, origin.y, 140, 140);
		return label;
	}
	
	public void controller(TimelineController theController){
		_myTimelineController = theController;

		_myRuler.timelineController(_myTimelineController);
		 
		if(_myScrollbar != null)_myJScrollbar.removeAdjustmentListener(_myScrollbar);
		_myScrollbar = new SwingTimelineScrollbar(_myJScrollbar, _myTimelineController);
		_myJScrollbar.addAdjustmentListener(_myScrollbar);
		
		_myToolChooserPopup = new SwingToolChooserPopup(_myTimelineController.curveTool());
		 
	}
	
	public Container container(){
		return _myPane;
	}
	
	public SwingRulerView rulerView() {
		return _myRuler;
	}
	
	public void openTimelinePopup(int theX, int theY) {
		_myToolChooserPopup.show(_myMultiTrackPanel, theX, theY);
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.view.TimelineView#addGroupTrack(int, de.artcom.timeline.controller.GroupTrackController, java.lang.String)
	 */
	public SwingGroupTrackView addGroupTrack(int theIndex, GroupTrackController theController) {
		assert (theIndex >= 0);

		SwingGroupTrackView myTrackView = new SwingGroupTrackView(
			_myMainFrame,
			_myToolChooserPopup, 
			_myStringDataRenderer,
			_myMultiTrackPanel,
			_myTimelineController, 
			theController
		);
		insertGroupTrack(theIndex, myTrackView, theController.groupTrack().path());
		
		_myTracks.add(theIndex,myTrackView);
		return myTrackView;
	}
	
	private void insertGroupTrack(int theIndex, SwingGroupTrackView theTrackView, Path thePath){
		assert (theIndex >= 0);
		_myMultiTrackPanel.insertTrackView(theTrackView, thePath, theIndex, SwingTableLayout.DEFAULT_GROUP_HEIGHT, true);
		_myMultiTrackPanel.insertTrackDataView(theTrackView.dataView(), thePath, theIndex);
		_myPane.updateUI();
	}
	
	private SwingTrackDataRenderer _myTrackDataRenderer = new SwingTrackDataRenderer();
	private CCStringTrackDataRenderer _myStringDataRenderer = new CCStringTrackDataRenderer();

	public void trackDataRenderer(SwingTrackDataRenderer theTrackDataRenderer) {
		_myTrackDataRenderer = theTrackDataRenderer;
	}
	
	/**
	 * Gets the Track Model and creates all needed Views and adds them to the needed listeners
	 * @param theIndex
	 * @param theTrack
	 */
	public SwingTrackView addTrack(int theIndex, TrackController theTrackDataController, CCClipTrackObject theObject) {
		assert (theIndex >= 0);
		
		SwingTrackDataRenderer myTrackDataRenderer = _myTrackDataRenderer;
		
		if(
			theTrackDataController.track().property() instanceof CCStringPropertyHandle || 
			theTrackDataController.track().property() instanceof CCEnumPropertyHandle || 
			theTrackDataController.track().property() instanceof CCObjectPropertyHandle
		){
			if(theObject == null){
				myTrackDataRenderer = _myStringDataRenderer;
			}else{
				myTrackDataRenderer = new CCClipTrackDataRenderer(theObject);
			}
		}
		
		if(theTrackDataController.track().property() instanceof CCPathHandle){
			myTrackDataRenderer = new CCPathTrackDataRenderer();
		}
		
		SwingTrackView myTrackView = new SwingTrackView(
			_myMainFrame,
			_myToolChooserPopup, 
			myTrackDataRenderer,
			_myTimelineController,
			theTrackDataController
		);
		myTrackView.dataView().addComponentListener(this);

		insertTrack(theIndex,myTrackView,theTrackDataController.track().path());
		
		_myTracks.add(theIndex,myTrackView);
		
		return myTrackView;
	}
	
	private void insertTrack(int theIndex, SwingTrackView theTrackView, Path thePath){
		assert (theIndex >= 0);
		_myMultiTrackPanel.insertTrackView(theTrackView.controlView(), thePath, theIndex, SwingTableLayout.DEFAULT_ROW_HEIGHT, false);
		_myMultiTrackPanel.insertTrackDataView(theTrackView.dataView(), thePath, theIndex);
		_myPane.updateUI();
	}

	public void removeTrack(Path thePath) {
		_myMultiTrackPanel.removeTrackView(thePath);
	}
	
	public void showUnusedTracks() {
		showUnusedTracks(true);
	}
	
	public void hideUnusedTracks() {
		showUnusedTracks(false);
	}
	
	private void showUnusedTracks(boolean theShowTracks){
		_myTimelineController.openGroups();
		_myMultiTrackPanel.clear();
		int i = 0;
		for(Object myTrack:_myTracks){
			if(myTrack instanceof SwingTrackView){
				SwingTrackView myTrackView = (SwingTrackView)myTrack;
				if(!theShowTracks && myTrackView.dataView().controller().trackData().size() <= 1)continue;
				
				insertTrack(i++, myTrackView, myTrackView.dataView().controller().track().path());
			}else if(myTrack instanceof SwingGroupTrackView){
				SwingGroupTrackView myTrackView = (SwingGroupTrackView)myTrack;
				myTrackView.showUnusedItems(theShowTracks);
				
				if(!theShowTracks && !myTrackView.containsData())continue;

				insertGroupTrack(i++, myTrackView, myTrackView.controller().groupTrack().path());

			}
		}_myTimelineController.closeGroups();
	}

	public Dimension getMaximumSize() {
		int myMaxYSize = 0;
		for (Component myPanel : _myMultiTrackPanel) {
			myMaxYSize += myPanel.getMaximumSize().getHeight();
		}
		return new Dimension(6000, myMaxYSize);
	}
	
	@Override
	public void componentHidden(ComponentEvent e) {}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentResized(ComponentEvent e) {
		if (e.getSource().getClass().equals(SwingTrackDataView.class)) {
			((SwingTrackDataView) e.getSource()).render();
		}
	}

	@Override
	public void componentShown(ComponentEvent e) {
		
	}
	
	private SwingEventTrackDialog _myEventTrackDialog = new SwingEventTrackDialog();

	public void openEventDialog(ControlPoint thePoint) {
		_myEventTrackDialog.setVisible(true);
	}

	public void openClipTrackDialog(EventTrackController theController, TimedEventPoint thePoint) {
		_myClipTrackObjectDialog.edit(theController, thePoint);
	}

	public void openGroupPresetDialog(CCObjectPropertyHandle theHandle, EventTrackController theController, TimedEventPoint thePoint) {
		_myGroupTrackObjectDialog.edit(theHandle, theController, thePoint);
	}

}
