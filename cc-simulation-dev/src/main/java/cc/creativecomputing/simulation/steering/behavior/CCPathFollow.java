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

import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.CCParticle;
import cc.creativecomputing.simulation.force.CCForce;
import cc.creativecomputing.simulation.steering.CCPathway;
import cc.creativecomputing.simulation.steering.CCPolylinePathway;

public class CCPathFollow extends CCForce{
	private CCPolylinePathway _myPathway;
	private double _myPredictRange;
	
	private CCVector3 _myFuturePosition;
	private CCVector3 _myTangent;
	private CCVector3 _myOnPath;
	private CCVector3 _myTarget;
	
	private final CCSeekFlee _mySeek;
	
	public CCPathFollow(final CCPolylinePathway thePathway, final double thePredictRange){
		_myPathway = thePathway;
		if(_myPathway == null){
			_myNumberOfPoints = 0;
		}else{
			_myNumberOfPoints = _myPathway.numberOfPoints();
		}
		
		_myPredictRange = thePredictRange;

		_myFuturePosition = new CCVector3();
		_myTangent = new CCVector3();
		_myOnPath = new CCVector3();
		_myTarget = new CCVector3();
		
		_mySeek = new CCSeekFlee(_myTarget);
	}
	
	public void pathway(final CCPolylinePathway thePathway){
		_myPathway = thePathway;
		if(!_myUseSubPath)_myNumberOfPoints = _myPathway.numberOfPoints();
	}
	
	public CCPathway pathway(){
		return _myPathway;
	}
	
	private int _myStart = 0;
	private int _myNumberOfPoints;
	private boolean _myUseSubPath = false;
	
	public void useSubPath(final int theNumberOfPoints){
		_myNumberOfPoints = theNumberOfPoints;
		_myUseSubPath = true;
	}

	public boolean apply(final CCParticle theAgent, final CCVector3 theForce, double theDeltaTime) {
		if(_myPathway == null)return false;
		_myFuturePosition = theAgent.velocity().clone();
		_myFuturePosition.multiplyLocal(_myPredictRange);
		
		final double myLead = _myFuturePosition.length();
	     
		_myFuturePosition.addLocal(theAgent.position);
		
		int pointIndex = -1;
		
		if(_myUseSubPath){
			pointIndex = _myPathway.mapPointToPath(_myFuturePosition, _myOnPath, _myTangent,_myStart,_myNumberOfPoints);
			if(pointIndex > -1){
				_myStart = pointIndex;
			}
		}else{
			pointIndex = _myPathway.mapPointToPath(_myFuturePosition, _myOnPath, _myTangent);
		}
		
		if(pointIndex > -1 && theAgent.forward.dot(_myTangent) > 0.0F)return false;
		
			
		final double myPathDistance = _myPathway.mapPointToPathDistance(theAgent.position);
		_myTarget = _myPathway.mapPathDistanceToPoint(myPathDistance + myLead);
		_mySeek.target(_myTarget);
		_mySeek.apply(theAgent, theForce, 0);
		return true;
	}

}
