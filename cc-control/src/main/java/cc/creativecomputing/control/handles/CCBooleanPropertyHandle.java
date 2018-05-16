package cc.creativecomputing.control.handles;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.io.data.CCDataObject;

public class CCBooleanPropertyHandle extends CCPropertyHandle<Boolean>{
	

	public CCBooleanPropertyHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
	}
	
	@Override
	public double formatDoubleValue(double theValue) {
		return theValue >= 0.5 ? 1 : 0;
	}
	
	@Override
	public void fromDoubleValue(double theValue, boolean theOverWrite) {
		value(theValue >= 0.5, theOverWrite);
	}
	
	@Override
	public double normalizedValue() {
		return value() ? 1 : 0;
	}

	@Override
	public String valueString() {
		return _myValue + "";
	}
	
	@Override
	public CCDataObject data() {
		CCDataObject myResult = super.data();
		return myResult;
	}

}