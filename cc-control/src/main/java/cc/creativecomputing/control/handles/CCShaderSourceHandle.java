package cc.creativecomputing.control.handles;

import cc.creativecomputing.control.code.CCShaderSource;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.io.data.CCDataObject;

public class CCShaderSourceHandle extends CCPropertyHandle<CCShaderSource>{
	
	protected CCShaderSourceHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
	}
	
	@Override
	public CCDataObject data() {
		CCDataObject myResult = super.data();
		CCShaderSource myShaderObject = value();
		if(!myShaderObject.object().saveInFile()){
			myResult.put("source", myShaderObject.sourceCode());
		}
		return myResult;
	}
	
	@Override
	public void data(CCDataObject theData) {
		CCShaderSource myShaderObject = value();
		if(!myShaderObject.object().saveInFile()){
			if(theData.containsKey("source")){	
				myShaderObject.sourceCode(theData.getString("source"));
			}else{
				myShaderObject.sourceCode("");
			}
		}
		onChange();
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