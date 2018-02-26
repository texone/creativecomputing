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



/**
 * @author christianriekoff
 *
 */
public class CCHandleControlPoint extends CCControlPoint{
	
	private CCControlPoint _myParent;
	private CCHandleType _myHandleType;

	public CCHandleControlPoint(CCControlPoint theParent, CCHandleType theHandleType) {
		super(CCControlPointType.HANDLE);
		_myParent = theParent;
		_myHandleType = theHandleType;
	}

	public CCHandleControlPoint(CCControlPoint theParent, CCHandleType theHandleType, double theTime, double theValue) {
		super(theTime, theValue, CCControlPointType.HANDLE);
		_myParent = theParent;
		_myHandleType = theHandleType;
	}
	
	public CCControlPoint parent(){
		return _myParent;
	}
	
	public void parent(CCControlPoint theParent) {
		_myParent = theParent;
	}
	
	public CCHandleType handleType() {
		return _myHandleType;
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.points.ControlPoint#clone()
	 */
	@Override
	public CCHandleControlPoint clone() {
		return new CCHandleControlPoint(_myParent, _myHandleType, _myTime, _myValue);
	}
}
