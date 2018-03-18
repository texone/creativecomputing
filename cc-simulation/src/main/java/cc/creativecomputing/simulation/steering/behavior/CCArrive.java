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
