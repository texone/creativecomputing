package cc.creativecomputing.control.handles;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.io.data.CCDataObject;

public class CCStringPropertyHandle extends CCPropertyHandle<String>{
	

	public CCStringPropertyHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
	}
	
	@Override
	public double formatNormalizedValue(double theValue) {
		return theValue >= 0.5 ? 1 : 0;
	}
	
	@Override
	public void fromNormalizedValue(double theValue, boolean theOverWrite) {
		return;
	}
	
	@Override
	public CCDataObject data() {
		CCDataObject myResult = super.data();
		return myResult;
	}

	@Override
	public String convertNormalizedValue(double theValue) {
		return null;
	}
	
	@Override
	public double normalizedValue() {
		return 0;
	}

	@Override
	public String valueString() {
		return _myValue;
	}

}