/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.control.timeline.point;

import cc.creativecomputing.io.data.CCDataObject;

/**
 * @author christianriekoff
 *
 */
public class MarkerPoint extends ControlPoint{
	
	private String _myName;

	public MarkerPoint() {
		super(ControlPointType.MARKER);
	}

	public MarkerPoint(double theTime, final String theName) {
		super(theTime, 0, ControlPointType.MARKER);
		_myName = theName;
	}

	public String name() {
		return _myName;
	}
	
	public void name(String theName) {
		_myName = theName;
	}
	
	@Override
	public MarkerPoint clone() {
		return new MarkerPoint(time(), _myName);
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
