/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
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
