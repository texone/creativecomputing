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

public class CCSteerToRandom extends CCForce {
	
	private double _myRange;
	private double _mySwitchTimer = 0;
	private double _mySwitchTime = 0;
	
	private CCVector3 leaderTarget = new CCVector3();
	private CCVector3 targetOffset;
	private double _myMinDistance = 1;
	private CCSeekFlee _mySeek;
	
	public CCSteerToRandom(){
		this(50,3);
	}
	
	public CCSteerToRandom(final double theRange, final double theSwitchTime){
		_myRange = theRange;
		_mySeek = new CCSeekFlee(leaderTarget);
		_mySwitchTime = theSwitchTime;
		_mySwitchTimer = theSwitchTime;
	}
	
	public double range(){
		return _myRange;
	}
	
	public void range(final double theRange){
		_myRange = theRange;
	}
	
	public double switchTime(){
		return _mySwitchTime;
	}
	
	public void switchTime(final double theSwitchTime){
		_mySwitchTime = theSwitchTime;
	}

	public boolean apply(CCParticle theAgent, CCVector3 theForce, double theDeltaTime) {
		if (_mySwitchTimer >= _mySwitchTime || leaderTarget.distance(theAgent.position) < _myMinDistance * 3F) {
			do {
				leaderTarget.randomize();
				leaderTarget.multiplyLocal(_myRange);
				leaderTarget.addLocal(theAgent.position);
				
				targetOffset = leaderTarget.clone();
				targetOffset.subtractLocal(theAgent.position);
				targetOffset.normalizeLocal();
			} while ( targetOffset.dot(theAgent.forward) < -0.80000000000000004f);
			
			_mySwitchTimer = 0;
			_mySeek.target(leaderTarget);
		}
		_mySwitchTimer += theDeltaTime;
		
		_mySeek.apply(theAgent, theForce, theDeltaTime);
		return true;
	}

}
