package cc.creativecomputing.control.handles;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;

public class CCFloatHandle extends CCNumberHandle<Float>{

	public CCFloatHandle(CCObjectHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
	}

	@Override
	public Float toType(double theValue) {return (float)theValue;}
	
	@Override
	public String valueString() {
		return CCFormatUtil.nd(_myValue, digits());
	}
	
	@Override
	public Float typeMin() {
		return -Float.MAX_VALUE;
	}

	@Override
	public Float typeMax() {
		return Float.MAX_VALUE;
	}
}
