package cc.creativecomputing.controlui.timeline.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JViewport;
import javax.swing.border.EmptyBorder;

import cc.creativecomputing.control.handles.CCColorPropertyHandle;
import cc.creativecomputing.control.handles.CCEnumPropertyHandle;
import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.control.handles.CCPathHandle;
import cc.creativecomputing.control.handles.CCSelectionPropertyHandle;
import cc.creativecomputing.control.handles.CCStringPropertyHandle;
import cc.creativecomputing.control.timeline.TimeRange;
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
import cc.creativecomputing.controlui.timeline.controller.track.CCCurveTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCEventTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCGroupTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCTrackController;
import cc.creativecomputing.controlui.timeline.view.SwingMultiTrackPanel.MultiTrackMouseAdapter;
import cc.creativecomputing.controlui.timeline.view.track.SwingAbstractTrackDataView;
import cc.creativecomputing.controlui.timeline.view.track.SwingCurveTrackDataView;
import cc.creativecomputing.controlui.timeline.view.track.SwingEventTrackDataView;
import cc.creativecomputing.controlui.timeline.view.track.SwingEventTrackDialog;
import cc.creativecomputing.controlui.timeline.view.track.SwingGroupTrackView;
import cc.creativecomputing.controlui.timeline.view.track.SwingTrackDataRenderer;
import cc.creativecomputing.controlui.timeline.view.track.SwingTrackView;


@SuppressWarnings("serial")
public class SwingTimelineView extends JSplitPane implements ComponentListener {
	
	private JLayeredPane _myPane;
	
	private JScrollPane _myScrollPane;
	private JScrollBar _myJScrollbar;
    private JViewport _myViewport;
	private SwingMultiTrackPanel _myRulerPanel;
	private SwingMultiTrackPanel _myMultiTrackPanel;
	private SwingRulerView _myRuler;
	
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
		_myRulerPanel.insertTrackView(new JPanel(), myRulerPath, 0, 30 * SwingGuiConstants.SCALE, true);
		_myRulerPanel.insertTrackDataView(_myRuler, myRulerPath, 0);
		 
		_myMultiTrackPanel = new SwingMultiTrackPanel(_myPane);
		MultiTrackMouseAdapter myAdapter = new MultiTrackMouseAdapter(_myMainFrame, _myMultiTrackPanel, _myRulerPanel);
		_myMultiTrackPanel.addMouseListener(myAdapter);
		_myMultiTrackPanel.addMouseMotionListener(myAdapter);
		
		_myViewport = new JViewport();
        _myViewport.add(_myMultiTrackPanel);
        _myViewport.setBounds(0, 0, 300 * SwingGuiConstants.SCALE, 300 * SwingGuiConstants.SCALE);
        
        _myScrollPane = new JScrollPane(_myMultiTrackPanel);
        _myScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		_myScrollPane.getVerticalScrollBar().setUnitIncrement(20);
		_myScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		_myJScrollbar = new JScrollBar(JScrollBar.HORIZONTAL);
		_myJScrollbar.setMinimum(0);
		_myJScrollbar.setMaximum(1000);
		_myJScrollbar.addAdjustmentListener(e ->{
			double myValue = e.getValue() / 1000.0;
			double myLower = _myTimelineController.zoomController().lowerBound();
			double myUpper = _myTimelineController.zoomController().upperBound();
			double myMax = Math.max(myUpper, _myTimelineController.maximumTime());
			myValue *= myMax;
			double myRange = myUpper - myLower;
			_myTimelineController.zoomController().setRange(new TimeRange(myValue, myValue + myRange));
		});
		
		 _myPane.add(_myJScrollbar, BorderLayout.PAGE_END);
		
		_myClipTrackObjectDialog = new SwingClipTrackObjectDialog(theTimelineContainer);
		_myGroupTrackObjectDialog = new SwingGroupTrackObjectDialog();
		
		_myPane.add(_myScrollPane, BorderLayout.CENTER);
		
		_myPane.addComponentListener(this);
		
		CCUIStyler.styleSplitPane(this);
		setDividerLocation(30 * SwingGuiConstants.SCALE);
		setEnabled(false);
		
		setTopComponent(_myRulerPanel);
		setBottomComponent(_myPane);
	}
	
	public void controller(TimelineController theController){
		_myTimelineController = theController;
		_myTimelineController.zoomController().addZoomable((theLower, theUpper) ->{
				double myMax = Math.max(theUpper, _myTimelineController.maximumTime());
				
				int myValue = (int)((theLower) / myMax * 1000);
				int myExtent = (int)((theUpper - theLower) / myMax * 1000);
				_myJScrollbar.setValues(myValue, myExtent, 0, 1000);
		});
		_myRuler.timelineController(_myTimelineController);
	}
	
	public Container container(){
		return _myPane;
	}
	
	public SwingRulerView rulerView() {
		return _myRuler;
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.view.TimelineView#addGroupTrack(int, de.artcom.timeline.controller.GroupTrackController, java.lang.String)
	 */
	public SwingGroupTrackView addGroupTrack(int theIndex, CCGroupTrackController theController) {
		assert (theIndex >= 0);

		SwingGroupTrackView myTrackView = new SwingGroupTrackView(
			_myMainFrame,
			new SwingEventTrackDataView(null, _myTimelineController, theController),
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
	
	private CCStringTrackDataRenderer _myStringDataRenderer = new CCStringTrackDataRenderer();
	
	/**
	 * Gets the Track Model and creates all needed Views and adds them to the needed listeners
	 * @param theIndex
	 * @param theTrack
	 */
	public SwingTrackView addTrack(int theIndex, CCTrackController theTrackDataController, CCClipTrackObject theObject) {
		assert (theIndex >= 0);

		SwingAbstractTrackDataView<?> myDataView = null;
		
		if(theTrackDataController instanceof CCEventTrackController){
			SwingTrackDataRenderer myTrackDataRenderer = null;
			if(
				theTrackDataController.track().property() instanceof CCStringPropertyHandle || 
				theTrackDataController.track().property() instanceof CCEnumPropertyHandle || 
				theTrackDataController.track().property() instanceof CCObjectPropertyHandle || 
				theTrackDataController.track().property() instanceof CCSelectionPropertyHandle
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
			myDataView = new SwingEventTrackDataView(myTrackDataRenderer, _myTimelineController, (CCEventTrackController)theTrackDataController);
		}else if(theTrackDataController instanceof CCCurveTrackController){
			myDataView = new SwingCurveTrackDataView(_myTimelineController, (CCCurveTrackController)theTrackDataController);
		}
		
		SwingTrackView myTrackView = new SwingTrackView(
			_myMainFrame,
			myDataView,
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
//		if (e.getSource().getClass().equals(SwingTrackDataView.class)) {
//			((SwingTrackDataView) e.getSource()).render();
//		}
	}

	@Override
	public void componentShown(ComponentEvent e) {
		
	}
	
	private SwingEventTrackDialog _myEventTrackDialog = new SwingEventTrackDialog();

	public void openEventDialog(ControlPoint thePoint) {
		_myEventTrackDialog.setVisible(true);
	}

	public void openClipTrackDialog(CCEventTrackController theController, TimedEventPoint thePoint) {
		_myClipTrackObjectDialog.edit(theController, thePoint);
	}

	public void openGroupPresetDialog(CCObjectPropertyHandle theHandle, CCEventTrackController theController, TimedEventPoint thePoint) {
		_myGroupTrackObjectDialog.edit(theHandle, theController, thePoint);
	}

}
