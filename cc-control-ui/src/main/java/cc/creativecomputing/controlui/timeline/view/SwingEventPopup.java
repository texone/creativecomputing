/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.controlui.timeline.view;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import cc.creativecomputing.control.handles.CCPathHandle;
import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.controller.TrackContext;
import cc.creativecomputing.controlui.timeline.controller.track.CCEventTrackController;
import cc.creativecomputing.math.CCMath;


public  class SwingEventPopup extends JPopupMenu {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7869280073371142253L;
	private CCEventTrackController _myEventTrackController;
	private TimedEventPoint _myEvent;

	public SwingEventPopup(CCEventTrackController theEventTrackController) {
		_myEventTrackController = theEventTrackController;

		JMenuItem entryHead = new JMenuItem("Timed Event");
		entryHead.setFont(SwingGuiConstants.ARIAL_11);
		add(entryHead);
		addSeparator();
		
		JMenuItem myDeleteItem = new JMenuItem("Delete");
		myDeleteItem.setFont(SwingGuiConstants.ARIAL_11);
		myDeleteItem.addActionListener(e -> {
			if(_myEvent == null) return;
			
			_myEventTrackController.delete(_myEvent);
			_myEventTrackController.view().render();
		});
		add(myDeleteItem);
		
		JMenuItem myDeleteAllItem = new JMenuItem("Delete All");
		myDeleteAllItem.setFont(SwingGuiConstants.ARIAL_11);
		myDeleteAllItem.addActionListener(e -> {
			if(_myEvent == null) return;
			
			_myEventTrackController.trackData().clear();
			_myEventTrackController.view().render();
		});
		add(myDeleteAllItem);
		
		JMenuItem myResetItem = new JMenuItem("Reset");
		myResetItem.setFont(SwingGuiConstants.ARIAL_11);
		myResetItem.addActionListener(e -> {
			if(_myEvent == null) return;
			if(_myEventTrackController.property() instanceof CCPathHandle){
				((CCPathHandle)_myEventTrackController.property()).reset(_myEvent);
			}
			_myEventTrackController.view().render();
		});
		add(myResetItem);
		
		JMenuItem myLoopItem = new JMenuItem("Apply to Loop");
		myLoopItem.setFont(SwingGuiConstants.ARIAL_11);
		myLoopItem.addActionListener(e -> {
			if(_myEvent == null) return;
			
			TrackContext myContext = _myEventTrackController.context();
			if(!( myContext instanceof TimelineController)){
				return;
			}
			
			TimelineController myTimelineController = (TimelineController)myContext;
			myTimelineController.transportController().loop(_myEvent.time(), _myEvent.endTime());
			myTimelineController.transportController().doLoop(true);
			myTimelineController.transportController().view().render();
		});
		add(myLoopItem);
		
		JMenuItem myZoomItem = new JMenuItem("Zoom In");
		myZoomItem.setFont(SwingGuiConstants.ARIAL_11);
		myZoomItem.addActionListener(e -> {
			if(_myEvent == null) return;
			
			TrackContext myContext = _myEventTrackController.context();
			if(!( myContext instanceof TimelineController)){
				return;
			}
			
			TimelineController myTimelineController = (TimelineController)myContext;
			double myOffset = (_myEvent.endTime() - _myEvent.time()) / 10;
			myTimelineController.zoomController().setRange(CCMath.max(0,_myEvent.time() - myOffset), _myEvent.endTime() + myOffset);
			myTimelineController.transportController().view().render();
		});
		add(myZoomItem);
		
		JMenuItem myPropertyItem = new JMenuItem("Properties");
		myPropertyItem.setFont(SwingGuiConstants.ARIAL_11);
		myPropertyItem.addActionListener(e -> {
			if(_myEvent == null) return;
			
			_myEventTrackController.properties(_myEvent);
		});
		add(myPropertyItem);
	}
	
	public void event(TimedEventPoint theEvent) {
		_myEvent = theEvent;
	}
}