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

public class CCBooleanHandle extends CCPropertyHandle<Boolean>{
	

	public CCBooleanHandle(CCObjectHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
	}
	
	@Override
	public double formatDoubleValue(double theValue) {
		return theValue >= 0.5 ? 1 : 0;
	}
	
	@Override
	public void fromDoubleValue(double theValue, boolean theOverWrite) {
		value(theValue >= 0.5, theOverWrite);
	}
	
	@Override
	public double normalizedValue() {
		return value() ? 1 : 0;
	}

}
