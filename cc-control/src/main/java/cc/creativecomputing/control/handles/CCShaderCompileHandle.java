package cc.creativecomputing.control.handles;

import cc.creativecomputing.control.code.CCRealtimeCompile;
import cc.creativecomputing.control.code.CCShaderObject;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.io.data.CCDataObject;

public class CCShaderCompileHandle extends CCPropertyHandle<CCShaderObject>{
	
	protected CCShaderCompileHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
	}
	
	@Override
	public CCDataObject data() {
		CCDataObject myResult = super.data();
		CCShaderObject myShaderObject = value();
		myResult.put("source", myShaderObject.sourceCode());
		return myResult;
	}
	
	@Override
	public void data(CCDataObject theData) {
		CCShaderObject myShaderObject = value();
		if(theData.containsKey("source")){	
			myShaderObject.sourceCode(theData.getString("source"));
		}else{
			myShaderObject.sourceCode("");
		}
		onChange();
	}

	@Override
	public CCShaderObject convertNormalizedValue(double theValue) {
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