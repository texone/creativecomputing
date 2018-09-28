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

public class CCEnumHandle extends CCPropertyHandle<Enum<?>>{
	
	private final int _myNumberOfConstants;

	protected CCEnumHandle(CCObjectHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
	
		_myNumberOfConstants = theMember.type().getEnumConstants().length;
	}
	
	public Enum<?>[] enumConstants(){
		 return (Enum<?>[])_myMember.type().getEnumConstants();
	}
	
	@Override
	public double formatDoubleValue(double theValue) {
		theValue = CCMath.round(theValue * _myNumberOfConstants);
		theValue /= _myNumberOfConstants;
		return theValue;
	}
	
	@Override
	public void fromDoubleValue(double theValue, boolean theOverWrite) {
		return;
	}

	@Override
	public String valueString() {
		return value().name();
	}
	
	private Enum<?> enumForString(String theEnumString){
		for(Enum<?> myEnumConstant :value().getDeclaringClass().getEnumConstants()){
			if(myEnumConstant.name().equals(theEnumString)){
				return myEnumConstant;
			}
		}
		return null;
	}
	
	@Override
	public void valueCasted(Object theValue, boolean theOverWrite) {
		if(theValue instanceof String){
			value(enumForString((String)theValue), theOverWrite);
		}else{
			super.valueCasted(theValue, theOverWrite);
		}
	}
	
	@Override
	public void data(CCDataObject theData) {
		String myName = theData.getString("value");
		for(Enum<?> myEnumConstant :enumConstants()){
			if(myEnumConstant.name().equals(myName)){
				value(myEnumConstant, true);
				return;
			}
		}
	}
	
}
