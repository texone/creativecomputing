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
package cc.creativecomputing.control.timeline.point;


import cc.creativecomputing.control.timeline.TrackData;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.math.CCColor;

/**
 * @author christianriekoff
 * 
 */
public class ColorPoint extends ControlPoint {
	
	

	private HandleControlPoint _myEndPoint;
	
	private long _myID;
	
	private String _myEventType;
	
	private CCColor _myColor = new CCColor();
	
	private boolean _myIsSelected;
	
	private double _myContentOffset = 0;

	public ColorPoint() {
		super(ControlPointType.COLOR);
	}
	
	public double contentOffset(){
		return _myContentOffset;
	}
	
	public void contentOffset(double theContentOffset){
		_myContentOffset = theContentOffset;
	}

	public ColorPoint(double theTime, double theValue) {
		super(theTime, theValue, ControlPointType.COLOR);
		_myID = System.currentTimeMillis();
	}
	
	public void eventType(String theEventType) {
		_myEventType = theEventType;
	}
	
	public String eventType(){
		return _myEventType;
	}
	
	public void isSelected(boolean theIsSelected){
		_myIsSelected = theIsSelected;
	}
	
	public boolean isSelected(){
		return _myIsSelected;
	}
	
	public void color(CCColor theContent) {
		_myColor = theContent;
	}
	
	public CCColor color() {
		return _myColor;
	}
	
	public long id() {
		return _myID;
	}

	public boolean hasHandles() {
		return true;
	}

	public HandleControlPoint endPoint() {
		return _myEndPoint;
	}

	public void endPoint(HandleControlPoint theEndPoint) {
		_myEndPoint = theEndPoint;
	}
	
	public double endTime() {
		return _myEndPoint.time();
	}
	
	public void endTime(double theEndTime) {
		if(_myEndPoint == null) {
			_myEndPoint = new HandleControlPoint(this, HandleType.TIME_END);
		}
		_myEndPoint.time(theEndTime);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.artcom.timeline.model.ControlPoint#interpolateValue(double, de.artcom.timeline.model.TrackData)
	 */
	@Override
	public double interpolateValue(double theTime, TrackData theData) {
		return value();
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.points.ControlPoint#clone()
	 */
	@Override
	public ColorPoint clone() {
		ColorPoint myResult = new ColorPoint(_myTime, _myValue);
		myResult.endPoint(_myEndPoint);
		return myResult;
	}

	@Override
	public CCDataObject data(double theStartTime, double theEndTime) {
		CCDataObject myResult = super.data(theStartTime, theEndTime);
		myResult.put("id", _myID);
		myResult.put("eventType", _myEventType);
		myResult.put("end", _myEndPoint.data(theStartTime, theEndTime));
		myResult.put("r", _myColor.r);
		myResult.put("g", _myColor.g);
		myResult.put("b", _myColor.b);
		myResult.put("a", _myColor.a);
		if(_myContentOffset != 0)myResult.put("offset", _myContentOffset);
		return myResult;
	}
	
	private CCDataObject _myContentData;
	
	@Override
	public void data(CCDataObject theData) {
		super.data(theData);
		_myID = theData.getLong("id");
		_myEventType = theData.getString("eventType");
		CCDataObject myEndHandleData = theData.getObject("end");
		_myEndPoint = new HandleControlPoint(
			this, 
			HandleType.TIME_END, 
			myEndHandleData.getDouble(TIME_ATTRIBUTE), 
			myEndHandleData.getDouble(VALUE_ATTRIBUTE)
		);
		_myColor = new CCColor(
			myEndHandleData.getDouble("r",0),
			myEndHandleData.getDouble("g",0),
			myEndHandleData.getDouble("b",0),
			myEndHandleData.getDouble("a",0)
		);
		_myContentOffset = theData.getDouble("offset", 0);
	}
	
	public CCDataObject contentData() {
		return _myContentData;
	}
	
	
}
