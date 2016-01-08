package cc.creativecomputing.control.handles;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.math.CCColor;

public class CCColorPropertyHandle extends CCPropertyHandle<CCColor>{
	
	protected CCColorPropertyHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
	}
	
	@Override
	public void value(CCColor theValue, boolean theOverWrite) {
		if(theValue == null)return;
		if(theOverWrite)_myOriginalValue = theValue.clone();
		_myValue = theValue.clone();
		_myUpdateMember = true;
	}
	
	@Override
	public void restore() {
		if(_myValue != null && _myOriginalValue != null)
			_myValue.set(_myOriginalValue);
	}
	
	@Override
	public CCDataObject data() {
		CCDataObject myResult = super.data();
		CCColor myColor = value();
		myResult.put("r", myColor.r);
		myResult.put("g", myColor.g);
		myResult.put("b", myColor.b);
		myResult.put("a", myColor.a);
		return myResult;
	}
	
	@Override
	public void data(CCDataObject theData) {
		CCColor myColor = new CCColor(
			theData.getFloat("r",0),
			theData.getFloat("g",0),
			theData.getFloat("b",0),
			theData.getFloat("a",0)
		);
		value(myColor, true);
	}

	@Override
	public CCColor convertNormalizedValue(double theValue) {
		return null;
	}
	
	@Override
	public double normalizedValue() {
		return 0;
	}

	@Override
	public String valueString() {
		return _myValue.toString();
	}
}