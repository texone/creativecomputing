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

/**
 * The arrive behavior is similar to the seek. It decelerates the agent, as it
 * comes nearer to the target position.
 * @author christianr
 *
 */
public class CCArrive extends CCTargetBehavior{
	private double _mySlowingDistance;
	
	public CCArrive(final CCVector3 theTarget, final double theSlowingDistance){
		super(theTarget);
		_mySlowingDistance = theSlowingDistance;
	}
	
	public CCArrive(final CCVector3 theTarget){
		this(theTarget,300);
	}
	
	public void slowingDistance(final double theSlowingDistance){
		_mySlowingDistance = theSlowingDistance;
	}

	public boolean apply(final CCParticle theAgent, final CCVector3 theForce, double theDeltaTime) {
		theForce.set(_myTarget);
		theForce.subtractLocal(theAgent.position);

		final double distance = theForce.length();
		
		if(distance == 0.01f)return false;
		
		final double rampedSpeed = theAgent.maxSpeed * (distance / _mySlowingDistance);
		final double clippedSpeed = Math.min(rampedSpeed, theAgent.maxSpeed);
		
		theForce.multiplyLocal(clippedSpeed / distance);
		theForce.subtractLocal(theAgent.velocity());
		if(theForce.length() > theAgent.maxForce){
			theForce.normalizeLocal().multiplyLocal(theAgent.maxForce);
		}
		return true;
	}

}
