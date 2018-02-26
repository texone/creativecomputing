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
package cc.creativecomputing.control.timeline.point;

import cc.creativecomputing.io.data.CCDataObject;

/**
 * @author christianriekoff
 *
 */
public class CCMarkerPoint extends CCControlPoint{
	
	private String _myName;

	public CCMarkerPoint() {
		super(CCControlPointType.MARKER);
	}

	public CCMarkerPoint(double theTime, final String theName) {
		super(theTime, 0, CCControlPointType.MARKER);
		_myName = theName;
	}

	public String name() {
		return _myName;
	}
	
	public void name(String theName) {
		_myName = theName;
	}
	
	@Override
	public CCMarkerPoint clone() {
		return new CCMarkerPoint(time(), _myName);
	}
	
	private static final String CONTROL_POINT_NAME_ATTRIBUTE = "name";
	
	@Override
	public CCDataObject data(double theStartTime, double theEndTime) {
		CCDataObject myResult = super.data(theStartTime, theEndTime);
		myResult.put(CONTROL_POINT_NAME_ATTRIBUTE, _myName);
		return myResult;
	}
	
	@Override
	public void data(CCDataObject theData) {
		super.data(theData);
		_myName = theData.getString(CONTROL_POINT_NAME_ATTRIBUTE);
	}
}
