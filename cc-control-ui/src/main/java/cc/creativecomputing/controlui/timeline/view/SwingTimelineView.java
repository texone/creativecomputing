/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.controlui.timeline.view;

import java.nio.file.Path;

import cc.creativecomputing.control.handles.CCEnumPropertyHandle;
import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.control.handles.CCPathHandle;
import cc.creativecomputing.control.handles.CCSelectionPropertyHandle;
import cc.creativecomputing.control.handles.CCStringPropertyHandle;
import cc.creativecomputing.control.timeline.CCTimeRange;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCTimedEventPoint;
import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.controlui.controls.CCClipTrackDataRenderer;
import cc.creativecomputing.controlui.controls.CCPathTrackDataRenderer;
import cc.creativecomputing.controlui.controls.CCStringTrackDataRenderer;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineContainer;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.controlui.timeline.controller.arrange.CCClipTrackObject;
import cc.creativecomputing.controlui.timeline.controller.arrange.SwingClipTrackObjectDialog;
import cc.creativecomputing.controlui.timeline.controller.arrange.SwingGroupTrackObjectDialog;
import cc.creativecomputing.controlui.timeline.controller.track.CCColorTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCCurveTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCEventTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCGradientTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCGroupTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCTrackController;
import cc.creativecomputing.controlui.timeline.view.SwingMultiTrackPanel.MultiTrackMouseAdapter;
import cc.creativecomputing.controlui.timeline.view.track.CCAbstractTrackDataView;
import cc.creativecomputing.controlui.timeline.view.track.SwingColorTrackDataView;
import cc.creativecomputing.controlui.timeline.view.track.SwingCurveTrackDataView;
import cc.creativecomputing.controlui.timeline.view.track.SwingEventTrackDataView;
import cc.creativecomputing.controlui.timeline.view.track.SwingEventTrackDialog;
import cc.creativecomputing.controlui.timeline.view.track.SwingGradientTrackDataView;
import cc.creativecomputing.controlui.timeline.view.track.SwingGroupTrackView;
import cc.creativecomputing.controlui.timeline.view.track.CCTrackDataRenderer;
import cc.creativecomputing.controlui.timeline.view.track.SwingTrackView;
import cc.creativecomputing.controlui.timeline.view.transport.CCRulerView;
import cc.creativecomputing.math.CCVector2;


@SuppressWarnings("serial")
public class SwingTimelineView extends JSplitPane implements ComponentListener {
	
	private JLayeredPane _myPane;
	
	private JScrollPane _myScrollPane;
	private JScrollBar _myJScrollbar;
    private JViewport _myViewport;
	private SwingMultiTrackPanel _myRulerPanel;
	private SwingMultiTrackPanel _myMultiTrackPanel;
	private CCRulerView _myRuler;
	
	private List<Object> _myTracks = new ArrayList<Object>();
	
	private CCTimelineController _myTimelineController;
	
	private SwingClipTrackObjectDialog _myClipTrackObjectDialog;
	private SwingGroupTrackObjectDialog _myGroupTrackObjectDialog;
	
	private final JFrame _myMainFrame;

	public SwingTimelineView(JFrame theMainFrame, CCTimelineContainer theTimelineContainer) {
		super(JSplitPane.VERTICAL_SPLIT);
		
		_myMainFrame = theMainFrame;
		_myPane = new JLayeredPane();
		_myPane.setLayout(new BorderLayout());
//		_myPane.add(createColoredLabel("TEXONE", Color.RED, new Point(300,100)), 3);

		_myRuler = new CCRulerView(null);
		Path myRulerPath = Paths.get("ruler");
		_myRulerPanel = new SwingMultiTrackPanel(_myPane);
		_myRulerPanel.insertTrackView(new JPanel(), myRulerPath, 0, 30 * CCUIConstants.SCALE, true);
		_myRulerPanel.insertTrackDataView(_myRuler, myRulerPath, 0);
		 
		_myMultiTrackPanel = new SwingMultiTrackPanel(_myPane);
		MultiTrackMouseAdapter myAdapter = new MultiTrackMouseAdapter(_myMainFrame, _myMultiTrackPanel, _myRulerPanel);
		_myMultiTrackPanel.addMouseListener(myAdapter);
		_myMultiTrackPanel.addMouseMotionListener(myAdapter);
		
		_myViewport = new JViewport();
        _myViewport.add(_myMultiTrackPanel);
        _myViewport.setBounds(0, 0, 300 * CCUIConstants.SCALE, 300 * CCUIConstants.SCALE);
        
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
			_myTimelineController.zoomController().setRange(new CCTimeRange(myValue, myValue + myRange));
		});
		
		 _myPane.add(_myJScrollbar, BorderLayout.PAGE_END);
		
		_myClipTrackObjectDialog = new SwingClipTrackObjectDialog(theTimelineContainer);
		_myGroupTrackObjectDialog = new SwingGroupTrackObjectDialog();
		
		_myPane.add(_myScrollPane, BorderLayout.CENTER);
		
		_myPane.addComponentListener(this);
		
		CCUIStyler.styleSplitPane(this);
		setDividerLocation(30 * CCUIConstants.SCALE);
		setEnabled(false);
		
		setTopComponent(_myRulerPanel);
		setBottomComponent(_myPane);
	}
	
	public void controller(CCTimelineController theController){
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
	
	public CCRulerView rulerView() {
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

		CCAbstractTrackDataView<?> myDataView = null;
		
		if(theTrackDataController instanceof CCEventTrackController){
			CCTrackDataRenderer myTrackDataRenderer = null;
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
		}else if(theTrackDataController instanceof CCColorTrackController){
			myDataView = new SwingColorTrackDataView(_myTimelineController, (CCColorTrackController)theTrackDataController);
		}else if(theTrackDataController instanceof CCGradientTrackController){
			myDataView = new SwingGradientTrackDataView(_myTimelineController, (CCGradientTrackController)theTrackDataController);
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

	public CCVector2 getMaximumSize() {
		int myMaxYSize = 0;
		for (Component myPanel : _myMultiTrackPanel) {
			myMaxYSize += myPanel.getMaximumSize().getHeight();
		}
		return new CCVector2(6000, myMaxYSize);
	}
	
	private SwingEventTrackDialog _myEventTrackDialog = new SwingEventTrackDialog();

	public void openEventDialog(CCControlPoint thePoint) {
		_myEventTrackDialog.setVisible(true);
	}

	public void openClipTrackDialog(CCEventTrackController theController, CCTimedEventPoint thePoint) {
		_myClipTrackObjectDialog.edit(theController, thePoint);
	}

	public void openGroupPresetDialog(CCObjectPropertyHandle theHandle, CCEventTrackController theController, CCTimedEventPoint thePoint) {
		_myGroupTrackObjectDialog.edit(theHandle, theController, thePoint);
	}

}
