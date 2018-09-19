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
package cc.creativecomputing.control.timeline.point;


import cc.creativecomputing.control.timeline.CCTrackData;
import cc.creativecomputing.io.data.CCDataObject;

/**
 * @author christianriekoff
 * 
 */
public class CCEventPoint extends CCControlPoint {
	
	
	public static class TimedData{
		
		private Object _myValue;
		
		public TimedData(Object theContent){
			_myValue = theContent;
		}
		
		public CCDataObject data(){
			CCDataObject myResult = new CCDataObject();
			myResult.put("value", _myValue);
			return myResult;
		}
		
		public void data(CCDataObject theData){
			try{
				_myValue = theData.get("value");
			}catch(Exception e){
				
			}
		}
		
		public Object value(){
			return _myValue;
		}
		
		public void value(Object theValue){
			_myValue = theValue;
		}
	}

	private CCHandleControlPoint _myEndPoint;
	
	private long _myID;
	
	private String _myEventType;
	
	private TimedData _myContent;
	
	private boolean _myIsSelected;
	
	private double _myContentOffset = 0;

	public CCEventPoint() {
		super(CCControlPointType.TIMED_EVENT);
	}
	
	public double contentOffset(){
		return _myContentOffset;
	}
	
	public void contentOffset(double theContentOffset){
		_myContentOffset = theContentOffset;
	}

	public CCEventPoint(double theTime, double theValue) {
		super(theTime, theValue, CCControlPointType.TIMED_EVENT);
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
	
	public void content(TimedData theContent) {
		_myContent = theContent;
	}
	
	public TimedData content() {
		return _myContent;
	}
	
	public long id() {
		return _myID;
	}

	public boolean hasHandles() {
		return true;
	}

	public CCHandleControlPoint endPoint() {
		return _myEndPoint;
	}

	public void endPoint(CCHandleControlPoint theEndPoint) {
		_myEndPoint = theEndPoint;
	}
	
	public double endTime() {
		return _myEndPoint.time();
	}
	
	public void endTime(double theEndTime) {
		if(_myEndPoint == null) {
			_myEndPoint = new CCHandleControlPoint(this, CCHandleType.TIME_END);
		}
		_myEndPoint.time(theEndTime);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.artcom.timeline.model.ControlPoint#interpolateValue(double, de.artcom.timeline.model.TrackData)
	 */
	@Override
	public double interpolateValue(double theTime, CCTrackData theData) {
		return value();
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.points.ControlPoint#clone()
	 */
	@Override
	public CCEventPoint clone() {
		CCEventPoint myResult = new CCEventPoint(_myTime, _myValue);
		myResult.endPoint(_myEndPoint);
		return myResult;
	}

	@Override
	public CCDataObject data(double theStartTime, double theEndTime) {
		CCDataObject myResult = super.data(theStartTime, theEndTime);
		myResult.put("id", _myID);
		myResult.put("eventType", _myEventType);
		myResult.put("end", _myEndPoint.data(theStartTime, theEndTime));
		if(_myContent != null)myResult.put("content", _myContent.data());
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
		_myEndPoint = new CCHandleControlPoint(
			this, 
			CCHandleType.TIME_END, 
			myEndHandleData.getDouble(TIME_ATTRIBUTE), 
			myEndHandleData.getDouble(VALUE_ATTRIBUTE)
		);
		_myContent = new TimedData(null);
		_myContent.data(theData.getObject("content"));
		_myContentOffset = theData.getDouble("offset", 0);
	}
	
	public CCDataObject contentData() {
		return _myContentData;
	}
	
	
}
