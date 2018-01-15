package cc.creativecomputing.controlui.timeline.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.creativecomputing.control.timeline.AbstractTrack;
import cc.creativecomputing.control.timeline.GroupTrack;
import cc.creativecomputing.control.timeline.TrackData;
import cc.creativecomputing.controlui.timeline.controller.quantize.CCQuatizeMode;
import cc.creativecomputing.controlui.timeline.controller.track.CCGroupTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCTrackController;
import cc.creativecomputing.controlui.util.UndoHistory;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.data.CCDataArray;
import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataObject;


public class FileManager {
	
	public interface FileManagerListener{
		void onLoad(Path thePath);

		void onSave(Path thePath);

		void onNew(Path thePath);
		
	}
	
	private class DataSerializer{
	
		private static final String TIMELINE_ELEMENT = "timeline";
		private static final String TIMELINES_ELEMENT = "timelines";
		
		private static final String TRANSPORT_ELEMENT = "Transport";
		private static final String PLAYBACK_SPEED_ATTRIBUTE = "speed";
		private static final String LOOP_START_ATTRIBUTE = "loop_start";
		private static final String LOOP_END_ATTRIBUTE = "loop_end";
		private static final String LOOP_ACTIVE_ATTRIBUTE = "loop_active";
		
		private static final String LOWER_BOUND_ATTRIBUTE = "lower_bound";
		private static final String UPPER_BOUND_ATTRIBUTE = "upper_bound";

		private static final String QUANTIZE_ATTRIBUTE = "quantize";
		
		public DataSerializer() {
		}
		
		////////////////////////////////////
		//
		// LOADING
		//
		////////////////////////////////////
		private void loadTransport(final CCDataObject theTransportData, final CCTransportController theTransportController) {
			
			if (theTransportData.containsKey(PLAYBACK_SPEED_ATTRIBUTE)) {
				theTransportController.speed(
					theTransportData.getDouble(PLAYBACK_SPEED_ATTRIBUTE)
				);
			}
			
			if (theTransportData.containsKey(LOOP_START_ATTRIBUTE) && theTransportData.containsKey(LOOP_END_ATTRIBUTE)) {
				theTransportController.loop(
					theTransportData.getDouble(LOOP_START_ATTRIBUTE),
					theTransportData.getDouble(LOOP_END_ATTRIBUTE)
				);
			}
			
			if (theTransportData.containsKey(LOOP_ACTIVE_ATTRIBUTE)) {
				theTransportController.doLoop(theTransportData.getBoolean(LOOP_ACTIVE_ATTRIBUTE));
			}
			
			CCDataObject myTrackDataData = theTransportData.getObject(TrackData.TRACKDATA_ELEMENT);
			if(myTrackDataData != null) {
				TrackData myTrackData = new TrackData(null);
				myTrackData.data(myTrackDataData);
				theTransportController.trackData(myTrackData);
			}
		}
		
		private void loadGroupTrack(CCDataObject theData, TimelineController theTimeline){
			if(!theData.containsKey("path"))return;
			Path myPath = Paths.get(theData.getString("path"));
			CCGroupTrackController myController;
			try{
				myController = theTimeline.createGroupController(myPath);
			}catch(Exception e){
				CCLog.info(myPath);
				return;
			}
			myController.groupTrack().data(theData);
			
			Object myData = theData.get(GroupTrack.GROUP_TRACKS);
			if(myData instanceof CCDataArray){
				for(Object myObject:(CCDataArray)myData){
					try{
						loadTrack((CCDataObject)myObject, theTimeline);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}else{
				loadTrack((CCDataObject)myData, theTimeline);
			}
			
		}
		
		private void loadDataTrack(CCDataObject theData, TimelineController theTimeline){
			if(!theData.containsKey("path"))return;
			Path myPath = Paths.get(theData.getString("path"));
			CCTrackController myController;
			try{
				myController = theTimeline.createController(myPath);
			}catch(Exception e){
				CCLog.info(myPath);
				return;
			}
			myController.data(theData);
		}
		
		private void loadTrack(CCDataObject theData, TimelineController theTimeline){
			if(theData.containsKey("tracks")){
				loadGroupTrack(theData, theTimeline);
			}else{
				loadDataTrack(theData, theTimeline);
			}
		}
		
		private void loadTracks(CCDataObject theTimelineData, TimelineController theTimeline){
			loadTrack(theTimelineData, theTimeline);
		}
		
		private void loadTimeline(CCDataObject myTimelineData, TimelineController theTimelineController){
			CCDataObject myTransportData = myTimelineData.getObject(TRANSPORT_ELEMENT);
			loadTransport(myTransportData, theTimelineController.transportController());
				
			if (myTimelineData.containsKey(LOWER_BOUND_ATTRIBUTE)) {
				theTimelineController.zoomController().setLowerBound(myTimelineData.getDouble(LOWER_BOUND_ATTRIBUTE));
			}
			
			if (myTimelineData.containsKey(UPPER_BOUND_ATTRIBUTE)) {
				theTimelineController.zoomController().setUpperBound(myTimelineData.getDouble(UPPER_BOUND_ATTRIBUTE));
			}
			
			if(myTimelineData.containsKey(QUANTIZE_ATTRIBUTE)){
				theTimelineController.quantizer(CCQuatizeMode.valueOf(myTimelineData.getString(QUANTIZE_ATTRIBUTE)));
			}
			theTimelineController.resetClipTracks();
			if(myTimelineData.containsKey(TIMELINE_ELEMENT)){
				Object myTimelineObj = myTimelineData.get(TIMELINE_ELEMENT);
				if(myTimelineObj instanceof CCDataObject){
					loadTracks((CCDataObject)myTimelineObj, theTimelineController);
				}else{
					CCDataArray myDataArray = (CCDataArray)myTimelineObj;
					for(int i = 0; i < myDataArray.size();i++){
						loadTracks(myDataArray.getObject(i), theTimelineController);
					}
				}
			}
		}
		
		public void loadTimeline(Path thePath, TimelineController theTimelineController) {
			CCDataObject myTimelineData = CCDataIO.createDataObject(thePath);
			if(myTimelineData == null)throw new RuntimeException("the given timelinedocument:" + thePath +" does not exist");
			loadTimeline(myTimelineData, theTimelineController);
		}
		
		public void loadProject(Path thePath){
			CCDataObject myProjectData = CCDataIO.createDataObject(thePath);
			CCDataObject myTimelinesObject = myProjectData.getObject(TIMELINES_ELEMENT);
			
			List<String> myKeys = new ArrayList<>(myTimelinesObject.keySet());
			Collections.sort(myKeys);
			for(String myTimeline:myKeys){
				TimelineController myController = _myTimelineContainer.addTimeline(myTimeline);
				loadTimeline(myTimelinesObject.getObject(myTimeline), myController);
			}
			
		}
		
		private void insertGroupTrack(CCDataObject theData, TimelineController theTimeline){
			if(!theData.containsKey("path"))return;
			Path myPath = Paths.get(theData.getString("path"));
			CCGroupTrackController myController = theTimeline.createGroupController(myPath);
			myController.groupTrack().insertData(theData, theTimeline.transportController().time());
			
			Object myData = theData.get(GroupTrack.GROUP_TRACKS);
			if(myData instanceof CCDataArray){
				for(Object myObject:(CCDataArray)myData){
					insertTrack((CCDataObject)myObject, theTimeline);
				}
			}else{
				insertTrack((CCDataObject)myData, theTimeline);
			}
			
		}
		
		private void insertDataTrack(CCDataObject theData, TimelineController theTimeline){
			if(!theData.containsKey("path"))return;
			Path myPath = Paths.get(theData.getString("path"));
			CCTrackController myController = theTimeline.createController(myPath);
			myController.track().insertData(theData, theTimeline.transportController().time());
		}
		
		private void insertTrack(CCDataObject theData, TimelineController theTimeline){
			CCLog.info(theData);
			if(theData.containsKey("tracks")){
				insertGroupTrack(theData, theTimeline);
			}else{
				insertDataTrack(theData, theTimeline);
			}
		}
		
		private void insertTracks(CCDataObject theTimelineData, TimelineController theTimeline){
			insertTrack(theTimelineData, theTimeline);
		}
		
		public List<AbstractTrack> insertTracks(Path thePath, TimelineController theTimelineController) {
			
			try {
				CCDataObject myTimelineData = CCDataIO.createDataObject(thePath);
				if(myTimelineData.containsKey(TIMELINES_ELEMENT)){
					myTimelineData = myTimelineData.getObject(TIMELINES_ELEMENT);
					myTimelineData = myTimelineData.getObject(new ArrayList<>(myTimelineData.keySet()).get(0));
				}
//				CCDataObject myTransportData = myTimelineData.getObject(TRANSPORT_ELEMENT);
				CCLog.info(myTimelineData);
				if(myTimelineData.containsKey(TIMELINE_ELEMENT)){
					Object myTimelineObj = myTimelineData.get(TIMELINE_ELEMENT);
					if(myTimelineObj instanceof CCDataObject){
						insertTracks((CCDataObject)myTimelineObj, theTimelineController);
					}else{
						CCDataArray myDataArray = (CCDataArray)myTimelineObj;
						for(int i = 0; i < myDataArray.size();i++){
							insertTracks(myDataArray.getObject(i), theTimelineController);
						}
					}
				}
				CCLog.info(myTimelineData);
//				loadTransport(myTransportData, theTimelineController.transportController());
				
//				CCDataObject myMarkerDataData = myTransportData.getObject(TrackData.TRACKDATA_ELEMENT);
//				TrackData myMarkerData = new TrackData(null);
//				if(myMarkerDataData != null){
//					myMarkerData.data(myMarkerDataData);
//				}
				
//				insertTracks(myTimelineData, theTimelineController);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		////////////////////////////////////
		//
		// SAVING
		//
		////////////////////////////////////
		
		private CCDataObject createTransportData(CCTransportController theTransportController, double theStart, double theEnd) {
			CCDataObject myTransportData = new CCDataObject();
			myTransportData.put(PLAYBACK_SPEED_ATTRIBUTE, theTransportController.speed());
			myTransportData.put(LOOP_START_ATTRIBUTE, theTransportController.loopStart());
			myTransportData.put(LOOP_END_ATTRIBUTE, theTransportController.loopEnd());
			myTransportData.put(LOOP_ACTIVE_ATTRIBUTE, theTransportController.doLoop());
			
			CCDataObject myMarkerData = theTransportController.trackData().data(theStart, theEnd);
			myTransportData.put("marker",myMarkerData);
			return myTransportData;
		}
		
		private CCDataObject createTimelineData(TimelineController theTimelineController, boolean theSaveSelection) {
			CCDataObject myTimelineData = new CCDataObject();
			myTimelineData.put(LOWER_BOUND_ATTRIBUTE, theTimelineController.zoomController().lowerBound());
			myTimelineData.put(UPPER_BOUND_ATTRIBUTE, theTimelineController.zoomController().upperBound());
			myTimelineData.put(QUANTIZE_ATTRIBUTE, theTimelineController.quantizer().name());
			
			double myStart = 0;
			double myEnd = theTimelineController.maximumTime();
			
			if(theSaveSelection){
				myStart = theTimelineController.transportController().loopStart();
				myEnd = theTimelineController.transportController().loopEnd();
			}
			
			myTimelineData.put(TRANSPORT_ELEMENT, createTransportData(theTimelineController.transportController(), myStart, myEnd));

			CCDataArray myTracksData= new CCDataArray();
			CCGroupTrackController myRootController = theTimelineController.rootController();
			if(myRootController != null){
				myTracksData.add(myRootController.groupTrack().data(myStart, myEnd));
			}
			CCGroupTrackController myClipController = theTimelineController.clipController();
			if(myClipController != null){
				myTracksData.add(myClipController.groupTrack().data(myStart, myEnd));
			}

			myTimelineData.put(TIMELINE_ELEMENT, myTracksData);
			
			return myTimelineData;
		}
		
		public void saveTimeline(Path thePath, TimelineController theTimelineController) {
			CCDataObject myTimelineData = createTimelineData(theTimelineController, false);
			CCDataIO.saveDataObject(myTimelineData, thePath);
		}
		
		public void saveProject(Path thePath){
			CCDataObject myProjecteData = new CCDataObject();
			CCDataObject myTimelines = myProjecteData.createObject(TIMELINES_ELEMENT);
			for(String myKey:_myTimelineContainer.timelineKeys()){
				myTimelines.put(myKey, createTimelineData(_myTimelineContainer.timeline(myKey), false));
			}
			CCDataIO.saveDataObject(myProjecteData, thePath);
		}
		
		public void saveTimelineSelection(Path thePath, TimelineController theTimelineController) {
			CCDataObject myTimelineData = createTimelineData(theTimelineController, true);
			CCDataIO.saveDataObject(myTimelineData, thePath);
		}
	}
	
	private DataSerializer _mySerializer;
	
	private TimelineContainer _myTimelineContainer;
	
	private CCListenerManager<FileManagerListener> _myEvents = CCListenerManager.create(FileManagerListener.class);
	
	private String _myExtension = "json";
	private String _myDescription = "Data Path";
	
	public FileManager(TimelineContainer theTimelineContainer) {
		_mySerializer = new DataSerializer();
		_myTimelineContainer = theTimelineContainer;
	}
	
	public void extension(String theExtension, String theDescription) {
		_myExtension = theExtension;
		_myDescription = theDescription;
	}
	
	public String extension() {
		return _myExtension;
	}
	
	public String description() {
		return _myDescription;
	}
	
	public CCListenerManager<FileManagerListener> events() {
		return _myEvents;
	}
	
	public void replaceCurrentTimeline(Path thePath) {
		TimelineController myController = _myTimelineContainer.activeTimeline();
		myController.removeAll();
		_mySerializer.loadTimeline(thePath, myController);
		_myEvents.proxy().onLoad(thePath);
		myController.render();
		UndoHistory.instance().clear();
	}
	
	public void addToCurrentTimeline(Path thePath) {
		_mySerializer.loadTimeline(thePath, _myTimelineContainer.activeTimeline());
		_myEvents.proxy().onLoad(thePath);
		_myTimelineContainer.activeTimeline().render();
		UndoHistory.instance().clear();
	}
	
	public void insertAtTimeToCurrentTimeline(Path thePath) {
		_mySerializer.insertTracks(thePath, _myTimelineContainer.activeTimeline());
		_myTimelineContainer.activeTimeline().render();
		UndoHistory.instance().clear();
	}
	
	public void resetTimeline() {
		_myTimelineContainer.activeTimeline().resetTracks();
		UndoHistory.instance().clear();
		_myEvents.proxy().onNew(Paths.get("New Path"));
	}
	
	private Path _myCurrentPath;
	
	public void exportCurrentTimeline(Path thePath) {
		_myCurrentPath = thePath;
		_mySerializer.saveTimeline(thePath, _myTimelineContainer.activeTimeline());
		UndoHistory.instance().clear();
		_myEvents.proxy().onSave(thePath);
	}
	
	public void save(){
		if(_myCurrentPath == null)return;
		exportCurrentTimeline(_myCurrentPath);
	}
	
	public void exportCurrentTimelineSelection(Path thePath){
		_mySerializer.saveTimelineSelection(thePath, _myTimelineContainer.activeTimeline());
		UndoHistory.instance().clear();
		_myEvents.proxy().onSave(thePath);
	}
	
	public void saveProject(Path thePath) {
		_mySerializer.saveProject(thePath);
		UndoHistory.instance().clear();
		_myEvents.proxy().onSave(thePath);
	}
	
	public void loadProject(Path thePath) {
		
		_myTimelineContainer.reset();
		_mySerializer.loadProject(thePath);
		UndoHistory.instance().clear();
		_myEvents.proxy().onLoad(thePath);
		
	}

	public void newProject(){
		_myTimelineContainer.reset();
	}
}
