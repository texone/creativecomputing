package cc.creativecomputing.control.timeline;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.data.CCDataObject;

public class Track extends AbstractTrack{

	private TrackData _myTrackData;
    
    private boolean _myAccumulateData = false;
	
	private Map<String, String> _myExtras;

	
	
	public Track(CCPropertyHandle<?> theProperty){
		super(theProperty);
		_myTrackData = new TrackData(this);
	}
	
	public void extras(Map<String, String> theExtras) {
		_myExtras = theExtras;
	}
	
	public Map<String, String> extras() {
		return _myExtras;
	}
	
	public void addExtra(String theKey, String theValue) {
		if(_myExtras == null)_myExtras = new HashMap<String, String>();
		_myExtras.put(theKey, theValue);
	}
	
	public void accumulateData(final boolean theAccumulateData) {
		_myAccumulateData = theAccumulateData;
		
		if(_myAccumulateData && !(_myTrackData instanceof AccumulatedTrackData)) {
			_myTrackData = new AccumulatedTrackData(this);
		}
	}
	
	public boolean accumulateData() {
		return _myAccumulateData;
	}
	
	public TrackData trackData() {
		return _myTrackData;
	}
	
	public void trackData(TrackData theTrackData) {
		_myTrackData = theTrackData;
		_myDirtyFlag = false;
	}
	
	public boolean isDirty() {
		return _myDirtyFlag || _myTrackData.isDirty();
	}
	
	public void setDirty(boolean theFlag) {
		_myDirtyFlag = theFlag;
		_myTrackData.setDirty(theFlag);
	}
	
	public static final String TRACK_ELEMENT = "Track";
	private static final String TRACK_EXTRAS = "Extras";
	public static final String PATH_ATTRIBUTE = "path";
	private static final String MUTE_ATTRIBUTE = "mute";
	private static final String ACCUMULATE_ATTRIBUTE = "accumulate";
	
	public CCDataObject data(double theStart, double theEnd) {
		CCDataObject myTrackData = new CCDataObject();
		myTrackData.put(PATH_ATTRIBUTE, path().toString());
		myTrackData.put(MUTE_ATTRIBUTE, mute());
		myTrackData.put(ACCUMULATE_ATTRIBUTE, accumulateData());
		myTrackData.put(TrackData.TRACKDATA_ELEMENT, trackData().data(theStart, theEnd));
		
		if(_myExtras != null && _myExtras.size() > 0) {
			CCDataObject myExtraData = new CCDataObject();
			myTrackData.put(TRACK_EXTRAS, myExtraData);
			for(String myKey:_myExtras.keySet()) {
				myTrackData.put(myKey, _myExtras.get(myKey));
			}
		}
		return myTrackData;
	}
	
	public void data(CCDataObject theTrackData) {
		CCLog.info(theTrackData + ":" + _myProperty.path());
//		setAddress(theTrackData.getString(ADDRESS_ATTRIBUTE));
		mute(theTrackData.getBoolean(MUTE_ATTRIBUTE));
		accumulateData(theTrackData.getBoolean(ACCUMULATE_ATTRIBUTE, false));
		
		CCDataObject myTrackData = theTrackData.getObject(TrackData.TRACKDATA_ELEMENT);
//		TrackData myTrackData;
//		if(accumulateData()) {
//			myTrackData = new AccumulatedTrackData(this);
//		}else {
//			myTrackData = new TrackData(this);
//		}
//		myTrackData.fromXML(myTrackDataXML);
//		trackData(myTrackData);
		_myTrackData.clear();
		_myTrackData.data(myTrackData);
		
		CCDataObject myExtrasData = theTrackData.getObject(TRACK_EXTRAS);
		if(myExtrasData == null)return;
		_myExtras = new HashMap<String, String>();
		for(String myKey:myExtrasData.keySet()) {
			_myExtras.put(myKey, myExtrasData.getString(myKey));
		}
	}

	public Path path() {
		return _myProperty.path();
	}
}
