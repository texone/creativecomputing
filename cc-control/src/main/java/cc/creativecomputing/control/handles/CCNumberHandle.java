/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.control.handles;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.math.CCMath;

public abstract class CCNumberHandle<Type extends Number> extends CCPropertyHandle<Type>{
	
	private Type _myMin;
	private Type _myMax;
	
	private boolean _myIsNumberBox = false;

	public CCNumberHandle(CCObjectHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
		minMax(
			_myMember.annotation() == null ? toType(-1) : toType(_myMember.annotation().min()), 
			_myMember.annotation() == null ? toType(-1) : toType(_myMember.annotation().max())
		);
	}
	
	public abstract Type toType(double theValue);
	
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
			_myMin = toType(_myParent._myMember.annotation().min());
			_myMax = toType(_myParent._myMember.annotation().max());
		}else if(myIsNumberBox){
			_myMin = typeMin();
			_myMax = typeMax();
			_myIsNumberBox = true;
		}
	}
	
	public Type min(){
		return _myMin;
	}
	
	public Type max(){
		return _myMax;
	}
	
	public abstract Type typeMin();
	
	public abstract Type typeMax();
	
	@Override
	public Type value() {
		if(_myMember.value() == null){
			if(_myMember.annotation() == null || _myMember.annotation().defaultValue() == Double.NaN)return _myMin;
			return toType(CCMath.constrain(_myMember.annotation().defaultValue(), _myMin.doubleValue(), _myMax.doubleValue()));
		}
		return toType(CCMath.constrain(super.value().doubleValue(), _myMin.doubleValue(), _myMax.doubleValue()));
	}
	
	@Override
	public void value(Number theValue, boolean theOverWrite) {
		if(theValue == null)return;
		super.value(toType(theValue.doubleValue()), theOverWrite);
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
//		return toType(CCMath.blend(_myMin.doubleValue(), _myMax.doubleValue(), theValue));
//	}
	
	@Override
	public double normalizedValue() {
		return CCMath.norm(value().doubleValue(), _myMin.doubleValue(), _myMax.doubleValue());
	}

	@Override
	public abstract String valueString();
	
//	public Class<?> numberType(){
//		return _myToType.type();
//	}
	
	@Override
	public CCDataObject data() {
		CCDataObject myResult = super.data();
		myResult.put("min", _myMin);
		myResult.put("max", _myMax);
		return myResult;
	}
	
	@Override
	public void data(CCDataObject theData) {
//		_myMin = toType(theData.getDouble("min", 0.0));
//		_myMax = toType(theData.getDouble("max", 1.0));
		value(toType(theData.getDouble("value", 0.0)), true);
		super.data(theData);
	}
}
