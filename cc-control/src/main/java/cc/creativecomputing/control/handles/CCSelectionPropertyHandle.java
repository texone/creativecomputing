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

import cc.creativecomputing.control.CCSelection;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.io.data.CCDataObject;

public class CCSelectionPropertyHandle extends CCPropertyHandle<CCSelection>{

	protected CCSelectionPropertyHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
		value().changeEvents.add(theValue ->{changeEvents.event(value());});
		
	}
	
	@Override
	public double normalizedValue() {
		return 0;
	}

	@Override
	public String valueString() {
		return value().value();
	}
	
	@Override
	public Object dataObject() {
		return value().value();
	}
	
	@Override
	public CCDataObject data() {
		CCDataObject myResult = super.data();
		return myResult;
	}
	
	@Override
	public void data(CCDataObject theData) {
		String myName = theData.getString("value");
		value().value(myName);
		
	}
	
	

}
