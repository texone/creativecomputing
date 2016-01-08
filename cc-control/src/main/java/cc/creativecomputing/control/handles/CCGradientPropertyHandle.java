package cc.creativecomputing.control.handles;

import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.control.CCGradientPoint;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.io.data.CCDataArray;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.math.CCColor;

public class CCGradientPropertyHandle extends CCPropertyHandle<CCGradient>{
	
	protected CCGradientPropertyHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
	}
	
	@Override
	public void value(CCGradient theValue, boolean theOverWrite) {
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
		CCGradient myGradient = value();
		CCDataArray myPoints = new CCDataArray();
		for(CCGradientPoint myPoint:myGradient){
			CCDataObject myPointJson = super.data();
			myPointJson.put("r", myPoint.color().r);
			myPointJson.put("g", myPoint.color().g);
			myPointJson.put("b", myPoint.color().b);
			myPointJson.put("a", myPoint.color().a);
			myPointJson.put("position", myPoint.position());
			myPoints.add(myPointJson);
		}
		myResult.put("gradient", myPoints);
		return myResult;
	}
	
	@Override
	public void data(CCDataObject theData) {
		CCDataArray myGradientJson = theData.getArray("gradient");
		CCGradient myGradient = new CCGradient();
		for(int i = 0; i < myGradientJson.size();i++){
			CCDataObject myPointJson = myGradientJson.getObject(i);
			CCGradientPoint myPoint = new CCGradientPoint(
				myPointJson.getDouble("position"), 
				new CCColor(
					theData.getDouble("r",0),
					theData.getDouble("g",0),
					theData.getDouble("b",0),
					theData.getDouble("a",0)
				)
			);
			myGradient.add(myPoint);
		}
		value(myGradient, true);
	}

	@Override
	public CCGradient convertNormalizedValue(double theValue) {
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