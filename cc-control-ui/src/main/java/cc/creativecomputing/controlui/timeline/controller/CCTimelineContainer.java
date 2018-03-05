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
package cc.creativecomputing.controlui.timeline.controller;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.controlui.timeline.view.CCTimelineContainerView;
import cc.creativecomputing.controlui.timeline.view.SwingTimelineView;
import cc.creativecomputing.core.events.CCListenerManager;


public class CCTimelineContainer {
	
	public interface TimelineChangeListener{
		
		void resetTimelines();
		
		void changeTimeline(CCTimelineController theController);
		
		void addTimeline(String theTimeline);
	}
	
	protected Map<String,CCTimelineController> _myTimelineController;
	protected CCObjectPropertyHandle _myRootHandle;
	
	protected CCListenerManager<TimelineChangeListener> _myTimelineChangeListener = CCListenerManager.create(TimelineChangeListener.class);

	protected CCTimelineController _myActiveController;
	
	private CCControlApp _myApp;

	public CCTimelineContainer(CCControlApp theApp, CCObjectPropertyHandle theRootHandle){
		_myTimelineController = new TreeMap<>();
		_myApp = theApp;
		rootProperty(theRootHandle);
	}
	
	public void rootProperty(CCObjectPropertyHandle theRootHandle) {
		_myRootHandle = theRootHandle;
		_myTimelineController.clear();
		_myActiveController = new CCTimelineController(this, _myRootHandle);
		_myActiveController.view(new SwingTimelineView(_myApp.window(), this));
		_myTimelineController.put("default", _myActiveController);
	}
	
	public void reset(){
		_myTimelineController.clear();
		_myTimelineChangeListener.proxy().resetTimelines();
		addTimeline("default");
		_myTimelineChangeListener.proxy().changeTimeline(_myActiveController);
	}
	
	public CCListenerManager<TimelineChangeListener> timelineChangeListener(){
		return _myTimelineChangeListener;
	}
	
	public Set<String> timelineKeys(){
		return _myTimelineController.keySet();
	}
	
	public CCTimelineController timeline(String theKey){
		return _myTimelineController.get(theKey);
	}
	
	public String defaultTimelineKey(){
		return "default";
	}
	
	public CCTimelineController activeTimeline(){
		return _myActiveController;
	}
	
	public void setActiveTimeline(String theTimeline){
		if(!_myTimelineController.containsKey(theTimeline))return;
		_myActiveController = _myTimelineController.get(theTimeline);
		_myTimelineChangeListener.proxy().changeTimeline(_myActiveController);
	}
	
	public CCTimelineController addTimeline(String theTimeline){
		if(_myTimelineController.containsKey(theTimeline))return _myTimelineController.get(theTimeline);
		_myActiveController = new CCTimelineController(this, _myRootHandle);
		_myActiveController.view(new SwingTimelineView(_myApp.window(), this));
		_myTimelineController.put(theTimeline, _myActiveController);
		_myTimelineChangeListener.proxy().addTimeline(theTimeline);
		return _myActiveController;
	}
	
	public void update(double theDeltaTime){
		if(_myActiveController == null)return;
		_myActiveController.transportController().update(theDeltaTime);
	}
	
	public void time(double theTime){
		if(_myActiveController == null)return;
		_myActiveController.transportController().time(theTime);
	}
	
	public void addTrack(CCPropertyHandle<?> thePropertyHandle){
		if(_myActiveController == null)return;
		if(thePropertyHandle.parent() != null)addTrack(thePropertyHandle.parent());
		if(thePropertyHandle instanceof CCObjectPropertyHandle){
			_myActiveController.createGroupController((CCObjectPropertyHandle)thePropertyHandle);
		}else {
			_myActiveController.createController(thePropertyHandle, null);
		}
	}
	
	public void writeValues(CCPropertyHandle<?> thePropertyHandle){
		addTrack(thePropertyHandle);
		if(thePropertyHandle instanceof CCObjectPropertyHandle){
			for(CCPropertyHandle<?> myChild:((CCObjectPropertyHandle)thePropertyHandle).children().values()){
				writeValues(myChild);
			}
		}else {
			_myActiveController.track(thePropertyHandle.path()).writeValue(_myActiveController.transportController().time());
		}
	}

}
