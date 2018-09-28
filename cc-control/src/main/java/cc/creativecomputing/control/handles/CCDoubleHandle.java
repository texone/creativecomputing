package cc.creativecomputing.control.handles;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;

public class CCDoubleHandle extends CCNumberHandle<Double>{

	public CCDoubleHandle(CCObjectHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
	}
	
	@Override
	public Double toType(double theValue) {return theValue;}

	@Override
	public String valueString() {
		return CCFormatUtil.nd(_myValue, digits());
	}
	
	@Override
	public Double typeMin() {
		return -Double.MAX_VALUE;
	}

	@Override
	public Double typeMax() {
		return Double.MAX_VALUE;
	}
}
