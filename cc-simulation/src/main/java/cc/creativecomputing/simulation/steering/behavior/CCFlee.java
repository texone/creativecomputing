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
