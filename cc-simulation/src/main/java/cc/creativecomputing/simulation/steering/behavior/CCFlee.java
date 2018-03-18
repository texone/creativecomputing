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
 * The flee behavior is the opposite of the seek. Instead of directing an agent
 * towards a target position it is steering it away from it.
 * @author christianr
 *
 */
public class CCFlee extends CCTargetBehavior{
	
	public CCFlee(final CCVector3 theTarget){
		super(theTarget);
	}

	public boolean apply(final CCParticle theAgent, final CCVector3 theForce, double theDeltaTime){
		double goalLength = 1.1F * theAgent.velocity().length();

		theForce.set(theAgent.position);
		theForce.subtractLocal(_myTarget);
		if(theForce.length() > goalLength){
			theForce.normalizeLocal().multiplyLocal(goalLength);
		}
		theForce.subtractLocal(theAgent.velocity());
		return true;
	}
}
