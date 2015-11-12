package cc.creativecomputing.control.timeline;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.io.data.CCDataArray;
import cc.creativecomputing.io.data.CCDataObject;

public class GroupTrack extends Track{

	private List<Track> _myTracks;
	private boolean _myIsOpen;

	public GroupTrack(CCObjectPropertyHandle theObjectPropertyHandle) {
		super(theObjectPropertyHandle);
		_myTracks = new ArrayList<Track>();
		_myIsOpen = true;
	}
	
	public List<Track> tracks(){
		return _myTracks;
	}
	
	public void addTrack(Track theTrack) {
		if(!_myTracks.contains(theTrack))_myTracks.add(theTrack);
	}
	
	public boolean isOpen(){
		return _myIsOpen;
	}
	
	public void isOpen(boolean theIsOpen){
		_myIsOpen = theIsOpen;
	}
	
	public static final String GROUP_TRACK_ELEMENT = "GroupTrack";
	public static final String GROUP_PATH_ATTRIBUTE = "path";
	private static final String GROUP_OPEN_ATTRIBUTE = "open";
	public static final String GROUP_TRACKS = "tracks";
	
	public CCDataObject data(double theStart, double theEnd) {
		CCDataObject myTrackData = super.data(theStart, theEnd);
		myTrackData.put(GROUP_PATH_ATTRIBUTE, path().toString());
		myTrackData.put(GROUP_OPEN_ATTRIBUTE, isOpen());
			
		CCDataArray myTracks = new CCDataArray();
		for(Track myTrack:tracks()) {
			myTracks.add(myTrack.data(theStart, theEnd));
		}
		myTrackData.put(GROUP_TRACKS, myTracks);
			
		return myTrackData;
	}
	
	public void data(CCDataObject theGroupTrackData) {
		super.data(theGroupTrackData);
	}
}
