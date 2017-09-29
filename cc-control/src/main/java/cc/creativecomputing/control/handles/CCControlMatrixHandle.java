package cc.creativecomputing.control.handles;

import cc.creativecomputing.control.CCControlMatrix;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.io.data.CCDataObject;

public class CCControlMatrixHandle extends CCPropertyHandle<CCControlMatrix>{
	
	protected CCControlMatrixHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
	}
	
	@Override
	public CCDataObject data() {
		CCDataObject myResult = super.data();
		CCControlMatrix myEnvelope = value();
		return myResult;
	}
	
	@Override
	public void data(CCDataObject theData) {
//		CCControlMatrix myEnvelope = new CCControlMatrix();
//		myEnvelope.curve().data(theData.getObject("curve"));
//		value(myEnvelope, true);
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