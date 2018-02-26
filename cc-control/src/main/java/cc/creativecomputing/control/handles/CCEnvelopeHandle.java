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

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.timeline.CCTrackData;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.io.data.CCDataObject;

public class CCEnvelopeHandle extends CCPropertyHandle<CCEnvelope>{
	
	protected CCEnvelopeHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
	}
	
	@Override
	public CCDataObject data() {
		CCDataObject myResult = super.data();
		CCEnvelope myEnvelope = value();
		CCTrackData myCurve = myEnvelope.curve();
		myResult.put("curve", myCurve.data());
		return myResult;
	}
	
	@Override
	public void data(CCDataObject theData) {
		CCEnvelope myEnvelope = new CCEnvelope();
		myEnvelope.curve().clear();
		myEnvelope.curve().data(theData.getObject("curve"));
		value(myEnvelope, true);
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
