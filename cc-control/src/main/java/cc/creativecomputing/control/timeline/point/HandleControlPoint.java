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



/**
 * @author christianriekoff
 *
 */
public class HandleControlPoint extends ControlPoint{
	
	private ControlPoint _myParent;
	private HandleType _myHandleType;

	public HandleControlPoint(ControlPoint theParent, HandleType theHandleType) {
		super(ControlPointType.HANDLE);
		_myParent = theParent;
		_myHandleType = theHandleType;
	}

	public HandleControlPoint(ControlPoint theParent, HandleType theHandleType, double theTime, double theValue) {
		super(theTime, theValue, ControlPointType.HANDLE);
		_myParent = theParent;
		_myHandleType = theHandleType;
	}
	
	public ControlPoint parent(){
		return _myParent;
	}
	
	public void parent(ControlPoint theParent) {
		_myParent = theParent;
	}
	
	public HandleType handleType() {
		return _myHandleType;
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.points.ControlPoint#clone()
	 */
	@Override
	public HandleControlPoint clone() {
		return new HandleControlPoint(_myParent, _myHandleType, _myTime, _myValue);
	}
}
