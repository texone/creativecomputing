package cc.creativecomputing.control.handles;

import cc.creativecomputing.control.CCPropertyMap.CCDoubleConverter;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.math.CCMath;

public class CCNumberPropertyHandle<Type extends Number> extends CCPropertyHandle<Number>{
	
	private Type _myMin;
	private Type _myMax;
	
	private CCDoubleConverter<Type> _myToType;
	
	private boolean _myIsNumberBox = false;

	public CCNumberPropertyHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember, CCDoubleConverter<Type> theToType) {
		super(theParent, theMember);
		_myToType = theToType;
		minMax(
			_myMember.annotation() == null ? _myToType.toType(-1) : _myToType.toType(_myMember.annotation().min()), 
			_myMember.annotation() == null ? _myToType.toType(-1) : _myToType.toType(_myMember.annotation().max())
		);
	}
	
	public int digits(){
		if(_myMember == null || _myMember.annotation() == null)return 2;
		return _myMember.annotation().digits();
	}
	
	public boolean isNumberBox(){
		return _myIsNumberBox;
	}
	
	public void minMax(Type theMin, Type theMax){
		_myMin = theMin;
		_myMax = theMax;
		
		boolean myIsNumberBox = _myMin.doubleValue() == -1 && _myMax.doubleValue() == -1;
		boolean myUseParentMinMax = 
			myIsNumberBox && 
			_myParent._myMember != null && 
			_myParent._myMember.annotation() != null && 
			(
				_myParent._myMember.annotation().min() != -1 || 
				_myParent._myMember.annotation().max() != -1
			);
		
		if(myUseParentMinMax){
			_myMin = _myToType.toType(_myParent._myMember.annotation().min());
			_myMax = _myToType.toType(_myParent._myMember.annotation().max());
		}else if(myIsNumberBox){
			_myMin = _myToType.min();
			_myMax = _myToType.max();
			_myIsNumberBox = true;
		}
	}
	
	public Type min(){
		return _myMin;
	}
	
	public Type max(){
		return _myMax;
	}
	
	@Override
	public Number value() {
		if(_myMember.value() == null){
			if(_myMember.annotation() == null || _myMember.annotation().defaultValue() == Double.NaN)return _myMin;
			return CCMath.constrain(_myMember.annotation().defaultValue(), _myMin.doubleValue(), _myMax.doubleValue());
		}
		return CCMath.constrain(super.value().doubleValue(), _myMin.doubleValue(), _myMax.doubleValue());
	}
	
	@Override
	public void value(Number theValue, boolean theOverWrite) {
		if(theValue == null)return;
		super.value(_myToType.toType(theValue.doubleValue()), theOverWrite);
	}
	
	@Override
	public double formatDoubleValue(double theValue) {
		if(!(_myMax instanceof Integer)){
			return theValue;
		}
		
		theValue = CCMath.blend(_myMin.doubleValue(), _myMax.doubleValue(), theValue);
		theValue = CCMath.round(theValue);
		theValue = CCMath.norm(theValue, _myMin.doubleValue(), _myMax.doubleValue());
		
		return theValue;
	}
	
	@Override
	public void fromDoubleValue(double theValue, boolean theOverWrite) {
		value(theValue, theOverWrite);
	}

//	@Override
//	public Number convertDoubleValue(double theValue) {
//		return _myToType.toType(CCMath.blend(_myMin.doubleValue(), _myMax.doubleValue(), theValue));
//	}
	
	@Override
	public double normalizedValue() {
		return CCMath.norm(value().doubleValue(), _myMin.doubleValue(), _myMax.doubleValue());
	}

	@Override
	public String valueString() {
		if(numberType() == Integer.class){
			return (int)_myValue + "";
		}
		if(numberType() == Float.class){
			return CCFormatUtil.nd((float)_myValue, digits());
		}
		return CCFormatUtil.nd((double)_myValue, digits());
			
	}
	
	public Class<?> numberType(){
		return _myToType.type();
	}
	
	@Override
	public CCDataObject data() {
		CCDataObject myResult = super.data();
		myResult.put("min", _myMin);
		myResult.put("max", _myMax);
		return myResult;
	}
	
	@Override
	public void data(CCDataObject theData) {
//		_myMin = _myToType.toType(theData.getDouble("min", 0.0));
//		_myMax = _myToType.toType(theData.getDouble("max", 1.0));
		value(_myToType.toType(theData.getDouble("value", 0.0)), true);
		super.data(theData);
	}
}