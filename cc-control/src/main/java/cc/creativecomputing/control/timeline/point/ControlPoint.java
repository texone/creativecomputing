package cc.creativecomputing.control.timeline.point;


import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.control.timeline.TrackData;
import cc.creativecomputing.core.CCBlendable;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.math.CCColor;

public class ControlPoint {
	
	public static enum ControlPointType {
		STEP, 
		LINEAR, 
		BEZIER, 
		HANDLE, 
		MARKER, 
		TIMED_EVENT, 
		TIMED_DATA_START,
		TIMED_DATA_END
	};
	
	public static enum HandleType{
		BEZIER_IN_HANDLE, BEZIER_OUT_HANDLE, TIME_END
	}
	
	
	double _myIndex;

	/**
	 * Type of the curve after this point
	 */
	private ControlPointType _myType;
	
	protected ControlPoint _myPrevious;
	protected ControlPoint _myNext;
	
	protected double _myTime;
	protected double _myValue;
	
	public ControlPoint() {
		this(0, 0, ControlPointType.LINEAR);
	}
	
	public ControlPoint(ControlPointType theControlPointType) {
		this(0, 0, theControlPointType);
	}

	public ControlPoint(double theTime, double theValue) {
		this(theTime, theValue, ControlPointType.LINEAR);
	}
	
	public ControlPoint(double theTime, double theValue, ControlPointType theControlPointType) {
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
	public ControlPointType getType() {
		return _myType;
	}

	/**
	 * @param myType the _myType to set
	 */
	public void setType(ControlPointType theType) {
		_myType = theType;
	}
	
	public ControlPoint getPrevious() {
		return _myPrevious;
	}
	
	public void setPrevious( ControlPoint thePoint ) {
		if (thePoint == this) {
			return;
		}
		_myPrevious = thePoint;
	}
	
	public ControlPoint getNext() {
		return _myNext;
	}
	
	public void setNext( ControlPoint thePoint ) {
		if (thePoint == this) {
			return;
		}
		_myNext = thePoint;
	}
	
	public void append( ControlPoint thePoint) {
		if (thePoint == this) {
			return;
		}
		if (thePoint != null) {
			thePoint.setPrevious(this);
			if (_myNext != null) {
				_myNext.setPrevious(thePoint);
				thePoint.setNext(_myNext);
			}
		}
		_myNext = thePoint;
	}
	
	public void prepend( ControlPoint thePoint ) {
		if (thePoint == this) {
			return;
		}
		if (thePoint != null) {
			thePoint.setNext(this);
			if (_myPrevious != null) {
				_myPrevious.setNext( thePoint );
				thePoint.setPrevious(_myPrevious);
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
	
	public double interpolateValue(double theTime, TrackData theData) {
		return _myValue;
	}
	
	public double value() {
		return _myValue;
	}
	
	public void value(final double theValue) {
		_myValue = theValue;
	}

    public double distance(final ControlPoint theOtherPoint) {
        double myTimeDistance = _myTime - theOtherPoint.time();
        double myValueDistance = _myValue - theOtherPoint.value();
        return Math.sqrt(myTimeDistance*myTimeDistance + myValueDistance*myValueDistance);
    }
	
	public boolean isPrevious(ControlPoint thePoint) {
		if (thePoint._myTime > _myTime) {
			return true;
		} else if (thePoint._myTime < _myTime) {
			return false;
		}
		ControlPoint myCurrent = _myNext;
		while ( myCurrent != null ) {
			if (myCurrent == thePoint) {
				return true;
			}
			myCurrent = myCurrent.getNext();
		}
		return false;
	}
	
	public boolean isNext(ControlPoint thePoint) {
		if (thePoint._myTime < _myTime) {
			return true;
		} else if (thePoint._myTime > _myTime) {
			return false;
		}
		ControlPoint myCurrent = _myPrevious;
		while( myCurrent != null ) {
			if (myCurrent == thePoint) {
				return true;
			}
			myCurrent = myCurrent.getPrevious();
		}
		return false;
	}
	
	public void cutLoose() {
		_myNext = null;
		_myPrevious = null;
	}
	
	public ControlPoint clone() {
		return new ControlPoint(_myTime, _myValue);
	}
	

	@Override
	public boolean equals(Object theObj) {
		if(!(theObj instanceof ControlPoint)) {
			return false;
		}
		return ((ControlPoint)theObj).time() == _myTime && ((ControlPoint)theObj).value() == _myValue;
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
