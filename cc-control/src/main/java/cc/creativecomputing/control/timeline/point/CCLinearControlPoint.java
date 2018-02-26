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
import cc.creativecomputing.math.CCMath;

/**
 * @author christianriekoff
 *
 */
public class CCLinearControlPoint extends CCControlPoint{

	public CCLinearControlPoint() {
		super(CCControlPointType.LINEAR);
	}

	public CCLinearControlPoint(double theTime, double theValue) {
		super(theTime, theValue, CCControlPointType.LINEAR);
	}
	
	public CCLinearControlPoint(CCControlPoint theControlPoint) {
		this(theControlPoint.time(), theControlPoint.value());
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.ControlPoint#interpolateValue()
	 */
	@Override
	public double interpolateValue(double theTime, CCTrackData theData) {
		CCControlPoint mySample = new CCStepControlPoint(theTime,0);
		CCControlPoint myLower = theData.lower(mySample);
		
		if(myLower.type() == CCControlPointType.BEZIER) {
			CCBezierControlPoint myBezierPoint = (CCBezierControlPoint)myLower;

			return myBezierPoint.sampleBezierSegment(myBezierPoint, myBezierPoint.outHandle(), this, this, theTime);
		}else {
			if (myLower != null) {
				myLower = theData.getLastOnSamePosition(myLower);
			}
			CCControlPoint myHigher = theData.ceiling(mySample);
	
			if (theData.contains(mySample)) {
				return theData.tailSet(mySample, true).first().value();
			}
	
			if (myLower == null) {
				return myHigher.value();
			} else if (myHigher == null) {
				return myLower.value();
			}
			double myBlend = (theTime - myLower.time()) / (myHigher.time() - myLower.time());
			return CCMath.blend(myLower.value(), myHigher.value(), myBlend);
		}
		
	}

	@Override
	public CCLinearControlPoint clone() {
		CCLinearControlPoint myResult = new CCLinearControlPoint(_myTime, _myValue);
		if(_myBlendable != null)myResult._myBlendable = (CCBlendable<?>)_myBlendable.clone();
		return myResult;
	}
}
