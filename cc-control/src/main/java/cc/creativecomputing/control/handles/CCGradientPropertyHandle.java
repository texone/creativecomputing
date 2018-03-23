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
		if(theOverWrite)_myPresetValue = theValue.clone();
		_myValue.set(theValue);
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
		CCGradient myGradient = value();
		CCDataArray myPoints = new CCDataArray();
		for(CCGradientPoint myPoint:myGradient){
			CCDataObject myPointJson = new CCDataObject();
			myResult.put("name", name());
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
					myPointJson.getDouble("r",0),
					myPointJson.getDouble("g",0),
					myPointJson.getDouble("b",0),
					myPointJson.getDouble("a",0)
				)
			);
			myGradient.add(myPoint);
		}
		value(myGradient, true);
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
