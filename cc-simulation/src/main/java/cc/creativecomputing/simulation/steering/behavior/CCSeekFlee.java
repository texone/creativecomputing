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
 * <p>CCSeek (or pursuit of a static target) acts to steer the character towards a
 * specified position in global space. This behavior adjusts the character so
 * that its velocity is radially aligned towards the target. Note that this is
 * different from an attractive force (such as gravity) which would produce an
 * orbital path around the target point.</p>
 * <p>The desired velocity is a vector in
 * the direction from the character to the target. The length of desired
 * velocity could be max_speed, or it could be the character's current speed,
 * depending on the particular application. The steering vector is the
 * difference between this desired velocity and the character's current
 * velocity.</p>
 * <p>If a character continues to seek, it will eventually pass through
 * the target, and then turn back to approach again. This produces motion a bit
 * like a moth buzzing around a light bulb.</p>
 * 
 * @author tex
 */
public class CCSeekFlee extends CCTargetBehavior{
	
	
	private double _myStrength = 1;
	
	/**
	 * Initializes a new seek directing the agent to the given target.
	 * @param i_target, Vector3f the target the agent is directed to
	 */
	public CCSeekFlee(final CCVector3 theTarget){
		super(theTarget);
	}
	
	public void strength(double theStrength) {
		_myStrength = theStrength;
	}

	/**
	 * @invisible
	 */
	public boolean apply(final CCParticle theAgent, final CCVector3 theForce, double theDeltaTime){
		final double goalLength = 1.1F * theAgent.velocity().length();

		theForce.set(_myTarget);
		theForce.subtractLocal(theAgent.position);
		if(theForce.length() > goalLength){
			theForce.normalizeLocal().multiplyLocal(goalLength);
		}
		theForce.multiplyLocal(_myStrength);
//		theForce.subtract(theAgent.velocity());
		if(theForce.length() > theAgent.maxForce){
			theForce.normalizeLocal().multiplyLocal(theAgent.maxForce);
		}
		return true;
	}

}
