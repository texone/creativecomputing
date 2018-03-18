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

import java.util.List;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.CCParticle;
import cc.creativecomputing.simulation.force.CCForce;

public class CCPredictPathFollow extends CCForce{
	private List<CCVector3> _myPathway;
	private double _myPredictRange;
	private CCVector3 _myTarget;
	private int _myIndex = 0;
	private final CCArrive _mySeek;
	
	public CCPredictPathFollow(final List<CCVector3> thePathway, final double thePredictRange){
		_myPathway = thePathway;
		
		_myPredictRange = thePredictRange;
		_myIndex = (int)CCMath.random(_myPathway.size());
		_myTarget = _myPathway.get(_myIndex).clone();
		_mySeek = new CCArrive(_myTarget);
	}
	
	public void pathWay(final List<CCVector3> thePathway){
		_myPathway = thePathway;
		_myIndex = (int)CCMath.random(_myPathway.size());
		_myTarget = _myPathway.get(_myIndex).clone();
	    _mySeek.target(_myTarget);
	}

	public boolean apply(final CCParticle theAgent, final CCVector3 theForce, double theDeltaTime) {
		double dist = CCMath.abs(theAgent.position.x - _myTarget.x);
		if(dist < _myPredictRange){
			_myTarget = _myPathway.get(_myIndex++).clone();
			_myIndex %= _myPathway.size();
		}
	    _mySeek.target(_myTarget);
	    _mySeek.apply(theAgent, theForce, 0);
	    return true;
	    
	}

}
