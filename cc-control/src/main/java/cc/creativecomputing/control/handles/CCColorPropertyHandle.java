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
import cc.creativecomputing.math.CCColor;

public class CCColorPropertyHandle extends CCPropertyHandle<CCColor>{
	
	protected CCColorPropertyHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
	}
	
	@Override
	public void value(CCColor theValue, boolean theOverWrite) {
		if(theValue == null)return;
		if(theOverWrite)_myPresetValue = theValue.clone();
		_myValue = theValue.clone();
		_myUpdateMember = true;
	}
	
	@Override
	public void restorePreset() {
		if(_myValue != null && _myPresetValue != null)
			_myValue.set(_myPresetValue);
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
	public double normalizedValue() {
		return 0;
	}

	@Override
	public String valueString() {
		return _myValue.toString();
	}
}
