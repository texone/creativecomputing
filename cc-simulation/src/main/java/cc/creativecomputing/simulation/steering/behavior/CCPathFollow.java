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
package cc.creativecomputing.simulation.steering.behavior;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCSpline;
import cc.creativecomputing.simulation.CCParticle;

public class CCPathFollow extends CCSeekFlee {

	private CCSpline _myPath;
	
	@CCProperty (name = "direction strength", min = 0.f, max = 5f)
	private float _cDirectionStrength = 1;
	@CCProperty (name = "predict range", min = 0.1f, max = 15f)
	private float _cPredictRange = 10f;

	public CCPathFollow(final CCSpline thePathway) {
		super(new CCVector3());
		_myPath = thePathway;
	}


	public void pathway(final CCSpline thePathway) {
		_myPath = thePathway;
	}

	public boolean apply(final CCParticle theAgent, final CCVector3 theForce, double theDeltaTime) {
		if (_myPath == null)
			return false;
		if(_myStrength == 0)return false;
		CCVector3 myFuturePosition = theAgent.velocity().clone().multiplyLocal(_cPredictRange);

		myFuturePosition.addLocal(theAgent.position);
		
		CCVector3 myClosestPoint = new CCVector3();
		CCVector3 myDirection = new CCVector3();
		_myPath.closestPointAndDirection(myFuturePosition, myClosestPoint, myDirection);
		if (myClosestPoint == null)
			return false;
		if(myClosestPoint.distance(myFuturePosition) < 40) {
			return false;
		}
		_myTarget.set(myClosestPoint);

		final double goalLength = 1.1F * theAgent.velocity().length();

		theForce.set(_myTarget);
		theForce.subtractLocal(theAgent.position);
		if(theForce.length() > goalLength){
			theForce.normalizeLocal().multiplyLocal(goalLength);
		}
		
		theForce.addLocal(myDirection.multiplyLocal(_cDirectionStrength));
		theForce.multiplyLocal(_myStrength);
//		theForce.subtract(theAgent.velocity());
		if(theForce.length() > theAgent.maxForce){
			theForce.normalizeLocal().multiplyLocal(theAgent.maxForce);
		}
		return true;
//		return false;
	}

}
