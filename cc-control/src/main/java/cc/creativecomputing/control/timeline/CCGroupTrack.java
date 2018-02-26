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
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.io.data.CCDataArray;
import cc.creativecomputing.io.data.CCDataObject;

public class CCGroupTrack extends CCTrack{

	private List<CCTrack> _myTracks;
	private boolean _myIsOpen;

	public CCGroupTrack(CCObjectPropertyHandle theObjectPropertyHandle) {
		super(theObjectPropertyHandle);
		_myTracks = new ArrayList<CCTrack>();
		_myIsOpen = true;
	}
	
	private Path _myPath;
	
	public CCGroupTrack(Path thePath){
		super(null);
		_myPath = thePath;
		_myTracks = new ArrayList<CCTrack>();
		_myIsOpen = true;
	}
	
	@Override
	public Path path() {
		if(_myPath == null)return super.path();
		return _myPath;
	}
	
	public List<CCTrack> tracks(){
		return _myTracks;
	}
	
	public void addTrack(CCTrack theTrack) {
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
		for(CCTrack myTrack:tracks()) {
			myTracks.add(myTrack.data(theStart, theEnd));
		}
		myTrackData.put(GROUP_TRACKS, myTracks);
			
		return myTrackData;
	}
	
	public void data(CCDataObject theGroupTrackData) {
		super.data(theGroupTrackData);
	}
}
