package cc.creativecomputing.control.handles;

import cc.creativecomputing.control.CCSelection;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.math.CCMath;

public class CCSelectionPropertyHandle extends CCPropertyHandle<CCSelection>{
	
	private final int _myNumberOfConstants;

	protected CCSelectionPropertyHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
		_myNumberOfConstants = value().values().size();
	}
	
	@Override
	public double formatNormalizedValue(double theValue) {
		theValue = CCMath.round(theValue * _myNumberOfConstants);
		theValue /= _myNumberOfConstants;
		return theValue;
	}

	@Override
	public CCSelection convertNormalizedValue(double theValue) {
		value().value(value().values().get((int)(theValue * _myNumberOfConstants)));
		return value();
	}
	@Override
	public void fromNormalizedValue(double theValue, boolean theOverWrite) {
		return;
	}
	
	@Override
	public double normalizedValue() {
		return 0;
	}

	@Override
	public String valueString() {
		return value().value();
	}
	
	@Override
	public Object dataObject() {
		return value().value();
	}
	
	@Override
	public CCDataObject data() {
		CCDataObject myResult = super.data();
		return myResult;
	}
	
	@Override
	public void data(CCDataObject theData) {
		String myName = theData.getString("value");
		value().value(myName);
		
	}
	
	

}