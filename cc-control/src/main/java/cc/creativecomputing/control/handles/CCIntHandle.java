package cc.creativecomputing.control.handles;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;

public class CCIntHandle  extends CCNumberHandle<Integer>{
	
	public CCIntHandle(CCObjectHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
	}

	@Override
	public Integer toType(double theValue) {
		return (int)theValue;
	}
	@Override
	public String valueString() {
		return _myValue +"";
	}
	
	@Override
	public Integer typeMin() {
		return Integer.MIN_VALUE;
	}

	@Override
	public Integer typeMax() {
		return Integer.MAX_VALUE;
	}
}
