package cc.creativecomputing.controlui.timeline.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cc.creativecomputing.control.CCPropertyMap;
import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.controlui.timeline.controller.FileManager.FileManagerListener;
import cc.creativecomputing.controlui.timeline.view.SwingTimelineContainerView;
import cc.creativecomputing.controlui.util.UndoHistory;
import cc.creativecomputing.controlui.util.UndoHistory.HistoryListener;
import cc.creativecomputing.core.events.CCListenerManager;


public class TimelineContainer implements FileManagerListener, HistoryListener{
	
	public static interface TimelineChangeListener{
		public void changeTimeline(TimelineController theController);
		
		public void addTimeline(String theTimeline);
	}
	
	protected final Map<String,TimelineController> _myTimelineController;
	protected final FileManager _myFileManager;
	protected final CCPropertyMap _myPropertyMap;
	
	protected CCListenerManager<TimelineChangeListener> _myTimelineChangeListener = CCListenerManager.create(TimelineChangeListener.class);

	protected TimelineController _myActiveController;
	protected SwingTimelineContainerView _myTimelineContainerView;

	public TimelineContainer(CCPropertyMap theProperties){
		_myPropertyMap = theProperties;
		_myTimelineController = new HashMap<>();
		_myActiveController = new TimelineController(this, _myPropertyMap);
		_myTimelineController.put("default", _myActiveController);
		_myFileManager = new FileManager(this);
		_myFileManager.events().add(this);
		
		UndoHistory.instance().events().add(this);
	}
	
	public void reset(){
		_myTimelineController.clear();
		_myActiveController = new TimelineController(this, _myPropertyMap);
		_myTimelineController.put("default", _myActiveController);
		_myTimelineChangeListener.proxy().changeTimeline(_myActiveController);
	}
	
	public CCListenerManager<TimelineChangeListener> timelineChangeListener(){
		return _myTimelineChangeListener;
	}
	
	public void view(SwingTimelineContainerView theTimelineContainerView){
		_myTimelineContainerView = theTimelineContainerView;
		_myActiveController.view(_myTimelineContainerView.createView(this));
		_myTimelineContainerView.timelineContainer(this);
	}
	
	public Set<String> timelineKeys(){
		return _myTimelineController.keySet();
	}
	
	public TimelineController timeline(String theKey){
		return _myTimelineController.get(theKey);
	}
	
	public String defaultTimelineKey(){
		return "default";
	}
	
	public TimelineController activeTimeline(){
		return _myActiveController;
	}
	
	public void setActiveTimeline(String theTimeline){
		if(!_myTimelineController.containsKey(theTimeline))return;
		_myActiveController = _myTimelineController.get(theTimeline);
		_myTimelineChangeListener.proxy().changeTimeline(_myActiveController);
	}
	
	public TimelineController addTimeline(String theTimeline){
		if(_myTimelineController.containsKey(theTimeline))return _myTimelineController.get(theTimeline);
		_myActiveController = new TimelineController(this, _myPropertyMap);
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
	
	public void addFileManagerListener(FileManagerListener theListener) {
		_myFileManager.events().add(theListener);
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
