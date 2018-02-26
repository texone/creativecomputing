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

import cc.creativecomputing.control.timeline.CCTrackData;
import cc.creativecomputing.core.CCBlendable;

public class CCStepControlPoint extends CCControlPoint{

	public CCStepControlPoint() {
		super(CCControlPointType.STEP);
	}

	public CCStepControlPoint(double theTime, double theValue) {
		super(theTime, theValue, CCControlPointType.STEP);
	}
	
	public CCStepControlPoint(CCControlPoint theControlPoint) {
		this(theControlPoint.time(), theControlPoint.value());
	}
	
	public double interpolateValue(double theTime, CCTrackData theData) {
		CCControlPoint myPrevious = previous();
		
		if(myPrevious != null) {
			return myPrevious.value();
		}
		
		return super.value();
	}
	
	public CCControlPoint clone() {
		CCControlPoint myCopy = new CCStepControlPoint(time(), value());
		if(_myBlendable!= null)myCopy._myBlendable = (CCBlendable<?>)_myBlendable.clone();
		return myCopy;
	}
}
