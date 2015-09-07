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
import cc.creativecomputing.simulation.steering.CCAgent;

public class CCAlignment extends CCNeighborHoodBehavior{

	private boolean deltaHeading = true;

	public CCAlignment(final float theDistance, final float theAngle){
		super(theDistance, theAngle);
	}

	public boolean apply(final CCParticle theAgent, final CCVector3 theForce, float theDeltaTime){
		int myCount = 0;
		theForce.set(0,0,0);
		
		neighbors.clear();
		
		for (CCAgent myOtherAgent:_myNeighborhood.getNearAgents(theAgent, _myNearAreaRadius)){
			//if (isInAngle(theAgent,myOtherAgent)){
				myCount++;
				theForce.addLocal(myOtherAgent.forward);
				neighbors.add(myOtherAgent);
			//}
		}
		
		if (myCount > 0){
			theForce.multiplyLocal(1.0F / myCount);
			if (deltaHeading)
				theForce.subtractLocal(theAgent.forward);
			return true;
		}
		return false;
	}

}
