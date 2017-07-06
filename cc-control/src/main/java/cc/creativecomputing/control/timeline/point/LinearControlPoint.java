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

import cc.creativecomputing.control.timeline.TrackData;
import cc.creativecomputing.math.CCMath;

/**
 * @author christianriekoff
 *
 */
public class LinearControlPoint extends ControlPoint{

	public LinearControlPoint() {
		super(ControlPointType.LINEAR);
	}

	public LinearControlPoint(double theTime, double theValue) {
		super(theTime, theValue, ControlPointType.LINEAR);
	}
	
	public LinearControlPoint(ControlPoint theControlPoint) {
		this(theControlPoint.time(), theControlPoint.value());
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.ControlPoint#interpolateValue()
	 */
	@Override
	public double interpolateValue(double theTime, TrackData theData) {
		ControlPoint mySample = new StepControlPoint(theTime,0);
		ControlPoint myLower = theData.lower(mySample);
		
		if(myLower.getType() == ControlPointType.BEZIER) {
			BezierControlPoint myBezierPoint = (BezierControlPoint)myLower;

			return myBezierPoint.sampleBezierSegment(myBezierPoint, myBezierPoint.outHandle(), this, this, theTime);
		}else {
			if (myLower != null) {
				myLower = theData.getLastOnSamePosition(myLower);
			}
			ControlPoint myHigher = theData.ceiling(mySample);
	
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
	public LinearControlPoint clone() {
		return new LinearControlPoint(_myTime, _myValue);
	}
}
