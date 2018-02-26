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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import cc.creativecomputing.control.CCPropertyMap;
import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.controlui.timeline.controller.FileManager.FileManagerListener;
import cc.creativecomputing.controlui.timeline.view.CCTimelineContainerView;
import cc.creativecomputing.controlui.util.UndoHistory;
import cc.creativecomputing.controlui.util.UndoHistory.HistoryListener;
import cc.creativecomputing.core.events.CCListenerManager;


public class CCTimelineContainer implements FileManagerListener, HistoryListener{
	
	public interface TimelineChangeListener{
		
		void resetTimelines();
		
		void changeTimeline(CCTimelineController theController);
		
		void addTimeline(String theTimeline);
	}
	
	protected Map<String,CCTimelineController> _myTimelineController;
	protected FileManager _myFileManager;
	protected CCPropertyMap _myPropertyMap;
	
	protected CCListenerManager<TimelineChangeListener> _myTimelineChangeListener = CCListenerManager.create(TimelineChangeListener.class);

	protected CCTimelineController _myActiveController;
	protected CCTimelineContainerView _myTimelineContainerView;

	public CCTimelineContainer(CCPropertyMap theProperties){
		_myPropertyMap = theProperties;
		_myTimelineController = new TreeMap<>();
		_myActiveController = new CCTimelineController(this, _myPropertyMap);
		_myTimelineController.put("default", _myActiveController);
		_myFileManager = new FileManager(this);
		_myFileManager.events().add(this);
		
		UndoHistory.instance().events().add(this);
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
	
	public void view(CCTimelineContainerView theTimelineContainerView){
		_myTimelineContainerView = theTimelineContainerView;
		_myActiveController.view(_myTimelineContainerView.createView(this));
		_myTimelineContainerView.timelineContainer(this);
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
		_myActiveController = new CCTimelineController(this, _myPropertyMap);
		_myActiveController.view(_myTimelineContainerView.createView(this));
		_myTimelineController.put(theTimeline, _myActiveController);
		_myTimelineChangeListener.proxy().addTimeline(theTimeline);
		return _myActiveController;
	}
	
	public FileManager fileManager(){
		return _myFileManager;
	}
	
	public void extension(String theExtension, String theDescription) {
		_myFileManager.extension(theExtension, theDescription);
	}
	
	public void loadFile(Path thePath){
		_myFileManager.replaceCurrentTimeline(thePath);
	}
	
	public void loadFile(String thePath){
		_myFileManager.replaceCurrentTimeline(Paths.get(thePath));
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
	
	@Override
	public void onLoad(Path thePath) {
	}
	
	@Override
	public void onChange(UndoHistory theHistory) {
		
	} 
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.timeline.controller.FileManager.FileManagerListener#onSave(java.io.Path)
	 */
	@Override
	public void onSave(Path thePath) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.timeline.controller.FileManager.FileManagerListener#onNew(java.io.Path)
	 */
	@Override
	public void onNew(Path thePath) {
		// TODO Auto-generated method stub
		
	}
}
