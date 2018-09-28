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
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.handles.CCEnumHandle;
import cc.creativecomputing.control.handles.CCObjectHandle;
import cc.creativecomputing.control.handles.CCPathHandle;
import cc.creativecomputing.control.handles.CCSelectionHandle;
import cc.creativecomputing.control.handles.CCStringHandle;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCEventPoint;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineContainer;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.controlui.timeline.controller.arrange.CCClipTrackObject;
import cc.creativecomputing.controlui.timeline.controller.track.CCColorTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCCurveTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCEventTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCGradientTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCGroupTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCTrackController;
import cc.creativecomputing.controlui.timeline.view.track.CCAbstractTrackDataView;
import cc.creativecomputing.controlui.timeline.view.track.CCAbstractTrackView;
import cc.creativecomputing.controlui.timeline.view.track.CCClipTrackDataRenderer;
import cc.creativecomputing.controlui.timeline.view.track.CCTrackDataRenderer;
import cc.creativecomputing.controlui.timeline.view.track.SwingColorTrackDataView;
import cc.creativecomputing.controlui.timeline.view.track.CCCurveTrackDataView;
import cc.creativecomputing.controlui.timeline.view.track.CCPathTrackDataRenderer;
import cc.creativecomputing.controlui.timeline.view.track.CCStringTrackDataRenderer;
import cc.creativecomputing.controlui.timeline.view.track.SwingEventTrackDataView;
import cc.creativecomputing.controlui.timeline.view.track.SwingGradientTrackDataView;
import cc.creativecomputing.controlui.timeline.view.track.SwingGroupTrackView;
import cc.creativecomputing.controlui.timeline.view.track.CCTrackView;
import cc.creativecomputing.controlui.timeline.view.transport.CCRulerView;
import cc.creativecomputing.gl.app.CCGLWindow;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.ui.widget.CCUIWidget;


public class SwingTimelineView extends CCUIWidget {
	
	private CCRulerView _myRuler;
	
	private List<CCAbstractTrackView> _myTracks = new ArrayList<>();
	
	private CCTimelineController _myTimelineController;
	
	private final CCGLWindow _myMainFrame;

	public SwingTimelineView(CCGLWindow theMainFrame, CCTimelineContainer theTimelineContainer) {
//		columnWidths(20,80);
		
		_myMainFrame = theMainFrame;

		_myRuler = new CCRulerView(null);
		
		
		addChild(_myRuler);
		
//		_myJScrollbar = new JScrollBar(JScrollBar.HORIZONTAL);
//		_myJScrollbar.setMinimum(0);
//		_myJScrollbar.setMaximum(1000);
//		_myJScrollbar.addAdjustmentListener(e ->{
//			double myValue = e.getValue() / 1000.0;
//			double myLower = _myTimelineController.zoomController().lowerBound();
//			double myUpper = _myTimelineController.zoomController().upperBound();
//			double myMax = Math.max(myUpper, _myTimelineController.maximumTime());
//			myValue *= myMax;
//			double myRange = myUpper - myLower;
//			_myTimelineController.zoomController().setRange(new CCTimeRange(myValue, myValue + myRange));
//		});
//		
//		 _myPane.add(_myJScrollbar, BorderLayout.PAGE_END);
	}
	
	public void controller(CCTimelineController theController){
		_myTimelineController = theController;
		_myTimelineController.zoomController().events.add(range ->{
			double myMax = CCMath.max(range.end, _myTimelineController.maximumTime());
				
			int myValue = (int)((range.start) / myMax * 1000);
			int myExtent = (int)(range.length() / myMax * 1000);
//			_myJScrollbar.setValues(myValue, myExtent, 0, 1000);
		});
		_myRuler.timelineController(_myTimelineController);
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
			this,
			_myTimelineController, 
			theController
		);
		insertGroupTrack(theIndex, myTrackView, theController.groupTrack().path());
		
		_myTracks.add(theIndex,myTrackView);
		return myTrackView;
	}
	
	
	private CCStringTrackDataRenderer _myStringDataRenderer = new CCStringTrackDataRenderer();
	
	/**
	 * Gets the Track Model and creates all needed Views and adds them to the needed listeners
	 * @param theIndex
	 * @param theTrack
	 */
	public CCTrackView addTrack(int theIndex, CCTrackController theTrackDataController) {
		assert (theIndex >= 0);

		CCAbstractTrackDataView<?> myDataView = null;
		
		if(theTrackDataController instanceof CCEventTrackController){
			CCTrackDataRenderer myTrackDataRenderer = null;
			if(
				theTrackDataController.track().property() instanceof CCStringHandle || 
				theTrackDataController.track().property() instanceof CCEnumHandle || 
				theTrackDataController.track().property() instanceof CCObjectHandle || 
				theTrackDataController.track().property() instanceof CCSelectionHandle
			){
				myTrackDataRenderer = _myStringDataRenderer;
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
			myDataView = new CCCurveTrackDataView(_myTimelineController, (CCCurveTrackController)theTrackDataController);
		}
		
		CCTrackView myTrackView = new CCTrackView(
			_myMainFrame,
			myDataView,
			_myTimelineController,
			theTrackDataController
		);

		insertTrack(theIndex,myTrackView,theTrackDataController.track().path());
		
		_myTracks.add(theIndex,myTrackView);
		
		return myTrackView;
	}
	
	private void insertTrack(int theIndex, CCTrackView theTrackView, Path thePath){
		assert (theIndex >= 0);
	
		CCUIWidget myTrackWidget = new CCUIWidget();
		myTrackWidget.flexDirection(CCYogaFlexDirection.ROW);
		myTrackWidget.addChild(theTrackView.controlView());
		myTrackWidget.addChild(theTrackView.dataView());
	}
	
	private void insertGroupTrack(int theIndex, SwingGroupTrackView theTrackView, Path thePath){
		assert (theIndex >= 0);
		CCUIWidget myTrackWidget = new CCUIWidget();
		myTrackWidget.flexDirection(CCYogaFlexDirection.ROW);
		myTrackWidget.addChild(theTrackView);
		myTrackWidget.addChild(theTrackView.dataView());
		
		addChild(myTrackWidget);
	}

	public void removeTrack(Path thePath) {
//		_myMultiTrackPanel.removeTrackView(thePath);
	}
	
	public void showUnusedTracks() {
		showUnusedTracks(true);
	}
	
	public void hideUnusedTracks() {
		showUnusedTracks(false);
	}
	
	private void showUnusedTracks(boolean theShowTracks){
		_myTimelineController.openGroups();
		removeAllChildren();
		int i = 0;
		for(Object myTrack:_myTracks){
			if(myTrack instanceof CCTrackView){
				CCTrackView myTrackView = (CCTrackView)myTrack;
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

}
