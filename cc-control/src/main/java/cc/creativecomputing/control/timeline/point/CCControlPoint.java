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


import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.control.timeline.CCTrackData;
import cc.creativecomputing.core.CCBlendable;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.math.CCColor;

public class CCControlPoint {
	
	public enum CCControlPointType {
		STEP, 
		LINEAR, 
		BEZIER, 
		HANDLE, 
		MARKER, 
		TIMED_EVENT, 
		TIMED_DATA_START,
		TIMED_DATA_END
	}

    public enum CCHandleType{
		BEZIER_IN_HANDLE, BEZIER_OUT_HANDLE, TIME_END
	}
	
	
	double _myIndex;

	/**
	 * Type of the curve after this point
	 */
	private CCControlPointType _myType;
	
	protected CCControlPoint _myPrevious;
	protected CCControlPoint _myNext;
	
	protected double _myTime;
	protected double _myValue;
	
	public CCControlPoint() {
		this(0, 0, CCControlPointType.LINEAR);
	}
	
	public CCControlPoint(CCControlPointType theControlPointType) {
		this(0, 0, theControlPointType);
	}

	public CCControlPoint(double theTime, double theValue) {
		this(theTime, theValue, CCControlPointType.LINEAR);
	}
	
	public CCControlPoint(double theTime, double theValue, CCControlPointType theControlPointType) {
		_myTime = theTime;
		_myValue = theValue;
		
		_myType = theControlPointType;
		_myIndex = 0;
		_myPrevious = null;
		_myNext = null;
	}
	
	protected CCBlendable<?> _myBlendable = null;
	
	public CCBlendable<?> blendable(){
		return _myBlendable;
	}
	
	public void blendable(CCBlendable<?> theBlendable){
		_myBlendable = theBlendable;
	}
	
	public boolean hasHandles() {
		return false;
	}

	/**
	 * @return the _myType
	 */
	public CCControlPointType type() {
		return _myType;
	}

	/**
	 * @param myType the _myType to set
	 */
	public void type(CCControlPointType theType) {
		_myType = theType;
	}
	
	public CCControlPoint previous() {
		return _myPrevious;
	}
	
	public void previous( CCControlPoint thePoint ) {
		if (thePoint == this) {
			return;
		}
		_myPrevious = thePoint;
	}
	
	public CCControlPoint next() {
		return _myNext;
	}
	
	public void next( CCControlPoint thePoint ) {
		if (thePoint == this) {
			return;
		}
		_myNext = thePoint;
	}
	
	public void append( CCControlPoint thePoint) {
		if (thePoint == this) {
			return;
		}
		if (thePoint != null) {
			thePoint.previous(this);
			if (_myNext != null) {
				_myNext.previous(thePoint);
				thePoint.next(_myNext);
			}
		}
		_myNext = thePoint;
	}
	
	public void prepend( CCControlPoint thePoint ) {
		if (thePoint == this) {
			return;
		}
		if (thePoint != null) {
			thePoint.next(this);
			if (_myPrevious != null) {
				_myPrevious.next( thePoint );
				thePoint.previous(_myPrevious);
			}
		}
		_myPrevious = thePoint;
	}
	
	public boolean hasNext() {
		return _myNext != null;
	}
	
	public boolean hasPrevious() {
		return _myPrevious != null;
	}
	
	public double time() {
		return _myTime;
	}
	
	public void time(final double theTime) {
		_myTime = theTime;
	}
	
	public double interpolateValue(double theTime, CCTrackData theData) {
		return _myValue;
	}
	
	public double value() {
		return _myValue;
	}
	
	public void value(final double theValue) {
		_myValue = theValue;
	}

    public double distance(final CCControlPoint theOtherPoint) {
        double myTimeDistance = _myTime - theOtherPoint.time();
        double myValueDistance = _myValue - theOtherPoint.value();
        return Math.sqrt(myTimeDistance*myTimeDistance + myValueDistance*myValueDistance);
    }
	
	public boolean isPrevious(CCControlPoint thePoint) {
		if (thePoint._myTime > _myTime) {
			return true;
		} else if (thePoint._myTime < _myTime) {
			return false;
		}
		CCControlPoint myCurrent = _myNext;
		while ( myCurrent != null ) {
			if (myCurrent == thePoint) {
				return true;
			}
			myCurrent = myCurrent.next();
		}
		return false;
	}
	
	public boolean isNext(CCControlPoint thePoint) {
		if (thePoint._myTime < _myTime) {
			return true;
		} else if (thePoint._myTime > _myTime) {
			return false;
		}
		CCControlPoint myCurrent = _myPrevious;
		while( myCurrent != null ) {
			if (myCurrent == thePoint) {
				return true;
			}
			myCurrent = myCurrent.previous();
		}
		return false;
	}
	
	public void cutLoose() {
		_myNext = null;
		_myPrevious = null;
	}
	
	public CCControlPoint clone() {
		return new CCControlPoint(_myTime, _myValue);
	}
	

	@Override
	public boolean equals(Object theObj) {
		if(!(theObj instanceof CCControlPoint)) {
			return false;
		}
		return ((CCControlPoint)theObj).time() == _myTime && ((CCControlPoint)theObj).value() == _myValue;
	}
	
	public String toString() {
		return "type: " + _myType + " time: " + _myTime + " value:" + _myValue;
	}
	
	protected static final String CONTROLPOINT_ELEMENT = "ControlPoint";

	public  static final String CONTROL_POINT_TYPE_ATTRIBUTE = "type";

	protected static final String TIME_ATTRIBUTE = "time";
	protected static final String VALUE_ATTRIBUTE = "value";
	protected static final String BLENDABLE_ATTRIBUTE = "blendable";
	
	public CCDataObject data(double theStartTime, double theEndTime) {
		CCDataObject myResult = new CCDataObject();
		myResult.put(CONTROL_POINT_TYPE_ATTRIBUTE, _myType.toString());
		myResult.put(TIME_ATTRIBUTE, _myTime - theStartTime);
		myResult.put(VALUE_ATTRIBUTE, _myValue);
		if(_myBlendable != null) {
			myResult.put(BLENDABLE_ATTRIBUTE, _myBlendable.data());
		}
		return myResult;
	}
	
	protected static final String  COLOR_TYPE = CCColor.class.getName();
	
	public void data(CCDataObject theData) {
		_myTime = theData.getDouble(TIME_ATTRIBUTE);
		_myValue = theData.getDouble(VALUE_ATTRIBUTE);
		if(!theData.containsKey(BLENDABLE_ATTRIBUTE))return;
		CCDataObject myBlendableData = theData.getObject(BLENDABLE_ATTRIBUTE);
		if(myBlendableData.getString(CCBlendable.BLENDABLE_TYPE_ATTRIBUTE).equals(CCColor.class.getName())) {
			_myBlendable = new CCColor();
		}else if(myBlendableData.getString(CCBlendable.BLENDABLE_TYPE_ATTRIBUTE).equals(CCGradient.class.getName())) {
			_myBlendable = new CCGradient();
		}
		_myBlendable.data(myBlendableData);
		
	}
	
	private boolean _myIsSelected = false;

	public void setSelected(boolean theIsSelected) {
		_myIsSelected = theIsSelected;
	}
	
	public boolean isSelected(){
		return _myIsSelected;
	}

	public void toggleSelection() {
		_myIsSelected = !_myIsSelected;
	}
}
