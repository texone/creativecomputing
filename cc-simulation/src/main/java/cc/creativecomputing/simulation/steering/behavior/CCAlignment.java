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
import cc.creativecomputing.simulation.steering.CCAgent;

public class CCAlignment extends CCNeighborHoodBehavior{

	private boolean deltaHeading = true;

	public CCAlignment(final double theDistance, final double theAngle){
		super(theDistance, theAngle);
	}

	@Override
	public boolean apply(final CCParticle theAgent, final CCVector3 theForce, double theDeltaTime){
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
