package cc.creativecomputing.control.handles;

import cc.creativecomputing.control.code.CCRealtimeCompile;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.io.data.CCDataObject;

public class CCRealtimeCompileHandle extends CCPropertyHandle<CCRealtimeCompile<?>>{
	
	protected CCRealtimeCompileHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
	}
	
	@Override
	public CCDataObject data() {
		CCDataObject myResult = super.data();
		CCRealtimeCompile<?> myRealtimeObject = value();
		myResult.put("source", myRealtimeObject.sourceCode());
		return myResult;
	}
	
	@Override
	public void data(CCDataObject theData) {
		CCRealtimeCompile<?> myRealtimeObject = value();
		if(theData.containsKey("source")){	
			myRealtimeObject.sourceCode(theData.getString("source"));
		}else{
			myRealtimeObject.sourceCode(myRealtimeObject.codeTemplate());
		}
		onChange();
	}

	@Override
	public CCRealtimeCompile<?> convertNormalizedValue(double theValue) {
		return null;
	}
	
	@Override
	public double normalizedValue() {
		return 0;
	}

	@Override
	public String valueString() {
		return null;
	}
}