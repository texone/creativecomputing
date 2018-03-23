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
package cc.creativecomputing.control.timeline;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.io.data.CCDataObject;

public class CCTrack extends CCAbstractTrack{

	private CCTrackData _myTrackData;
	
	private Map<String, String> _myExtras;

	
	
	public CCTrack(CCPropertyHandle<?> theProperty){
		super(theProperty);
		_myTrackData = new CCTrackData();
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
	
	public CCTrackData trackData() {
		return _myTrackData;
	}
	
	public void trackData(CCTrackData theTrackData) {
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
	private static final String MIN_ATTRIBUTE = "min";
	private static final String MAX_ATTRIBUTE = "max";
	
	public CCDataObject data(double theStart, double theEnd) {
		CCDataObject myTrackData = new CCDataObject();
		myTrackData.put(PATH_ATTRIBUTE, path().toString());
		myTrackData.put(MUTE_ATTRIBUTE, mute());
		myTrackData.put(MIN_ATTRIBUTE, min());
		myTrackData.put(MAX_ATTRIBUTE, max());
		myTrackData.put(CCTrackData.TRACKDATA_ELEMENT, trackData().data(theStart, theEnd));
		
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
//		setAddress(theTrackData.getString(ADDRESS_ATTRIBUTE));
		mute(theTrackData.getBoolean(MUTE_ATTRIBUTE));
		min(theTrackData.getDouble(MIN_ATTRIBUTE, 0));
		max(theTrackData.getDouble(MAX_ATTRIBUTE, 1));
		
		CCDataObject myTrackData = theTrackData.getObject(CCTrackData.TRACKDATA_ELEMENT);
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
	
	public void insertData(CCDataObject theTrackData, double theTime){
		CCDataObject myTrackData = theTrackData.getObject(CCTrackData.TRACKDATA_ELEMENT);
		_myTrackData.insert(myTrackData, theTime);
		
		CCDataObject myExtrasData = theTrackData.getObject(TRACK_EXTRAS);
		if(myExtrasData == null)return;
		for(String myKey:myExtrasData.keySet()) {
			_myExtras.put(myKey, myExtrasData.getString(myKey));
		}
	}

	public Path path() {
		return _myProperty.path();
	}
}
