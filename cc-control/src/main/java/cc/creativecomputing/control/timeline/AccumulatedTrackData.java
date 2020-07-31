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
package cc.creativecomputing.control.timeline;

import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.core.util.CCArrayUtil;
import cc.creativecomputing.math.CCMath;

/**
 * @author christianriekoff
 *
 */
public class AccumulatedTrackData extends TrackData{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1493782276712048182L;

	private double[] _myAccumulatedValues = new double[0];
	
	private double _myResolution = 1.0 / 60.0;
	
	public AccumulatedTrackData(Track theTrack) {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.TrackData#add(de.artcom.timeline.model.points.ControlPoint)
	 */
	@Override
	public boolean add(ControlPoint thePoint) {
		boolean myResult =  super.add(thePoint);
		
		if(getLastPoint() == thePoint) {
			int myNumberOfValues = (int)(thePoint.time() / _myResolution) + 1;
			_myAccumulatedValues = CCArrayUtil.expand(_myAccumulatedValues, myNumberOfValues);
		}
		
		changeRange(thePoint);
			
		return myResult;
	}
	
	@Override
	public boolean remove(ControlPoint thePoint) {
		boolean myShorten = getLastPoint() == thePoint;
		
		boolean myResult = super.remove(thePoint);
		
		if(myShorten) {
			if(getLastPoint() == null)return myResult;
			int myNumberOfValues = (int)(getLastPoint().time() / _myResolution) + 1;
			_myAccumulatedValues = CCArrayUtil.expand(_myAccumulatedValues, myNumberOfValues);
		}else {
			changeRange(thePoint);
		}
		
		return myResult;
	}
	
	private void changeRange(ControlPoint thePoint) {
		changeRange(lower(thePoint), higher(thePoint));
	}
	
	private void changeRange(ControlPoint theLower, ControlPoint theUpper) {
		double myLowerTime = 0;
		ControlPoint lower = theLower;
		if(lower != null)myLowerTime = lower.time();
		int myStartIndex =  (int)(myLowerTime / _myResolution);
		
		ControlPoint upper = theUpper;
		int myEndIndex;
		
		if(upper != null) {
			double myHigherTime = upper.time();
			myEndIndex = (int)(myHigherTime / _myResolution) + 1;
		}else {
			double myHigherTime = _myAccumulatedValues.length * _myResolution;
			myEndIndex =  (int)(myHigherTime / _myResolution);
		}
		
		double myOldLastValue = _myAccumulatedValues[myEndIndex - 1];
		
		for(int i = myStartIndex; i < myEndIndex;i++) {
			_myAccumulatedValues[i] = 
			i == 0 ? 0 : 
				_myAccumulatedValues[i - 1] + 
				CCMath.blend(
					0, 
					1, 
					super.value(i * _myResolution)
				);
		}
		
		double myDifference = _myAccumulatedValues[myEndIndex - 1] - myOldLastValue;
		
		for(int i = myEndIndex; i < _myAccumulatedValues.length;i++) {
			_myAccumulatedValues[i] += myDifference;
		}
	}
	
	@Override
	public double getAccumulatedValue(double theTime) {
		if(_myAccumulatedValues.length == 0)return 0;
		
		int myIndex = (int)(theTime / _myResolution);
		if(myIndex >= _myAccumulatedValues.length - 1) {
			double myTime = (theTime - (_myAccumulatedValues.length - 1) * _myResolution);
			double myLastValue = _myAccumulatedValues[_myAccumulatedValues.length - 1];
			double myPreviousValue = _myAccumulatedValues[CCMath.max(_myAccumulatedValues.length - 2,0)];
			double myValue =  _myAccumulatedValues[_myAccumulatedValues.length - 1] + myTime * (myLastValue - myPreviousValue) * 60;
			return myValue;
		}
		double myBlend = (theTime - myIndex * _myResolution);
		
		double myValue1 = _myAccumulatedValues[Math.min(myIndex, _myAccumulatedValues.length - 1)];
		double myValue2 = _myAccumulatedValues[Math.min(myIndex + 1, _myAccumulatedValues.length - 1)];
		
		return CCMath.blend(myValue1, myValue2, myBlend);
	}
}
