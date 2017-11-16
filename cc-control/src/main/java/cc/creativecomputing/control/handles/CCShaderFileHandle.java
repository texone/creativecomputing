package cc.creativecomputing.control.handles;

import cc.creativecomputing.control.code.CCShaderFile;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.io.data.CCDataObject;

public class CCShaderFileHandle extends CCPropertyHandle<CCShaderFile>{
	
	protected CCShaderFileHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
	}
	
	@Override
	public CCDataObject data() {
		CCDataObject myResult = super.data();
		CCShaderFile myShaderObject = value();
		if(!myShaderObject.object().saveInFile()){
			myResult.put("source", myShaderObject.source());
		}
		return myResult;
	}
	
	@Override
	public void data(CCDataObject theData) {
		CCShaderFile myShaderObject = value();
		if(!myShaderObject.object().saveInFile()){
			if(theData.containsKey("source")){	
				myShaderObject.source(theData.getString("source"));
			}else{
				myShaderObject.source("");
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