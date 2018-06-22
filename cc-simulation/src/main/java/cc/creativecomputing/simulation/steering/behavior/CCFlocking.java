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



/**
*   class CCSeparation
*
*   Implements the CCFlocking behavior
*/
public class CCFlocking extends CCNeighborHoodBehavior{
	
	private final CCFlee _myFlee = new CCFlee(new CCVector3());
	private final CCSeekFlee _mySeek = new CCSeekFlee(new CCVector3());
	
	/** 
	* Constructor 
	*
	* @param theDistance Radius of the area to be searched for relevant vehicles
	* @param theAngle Influence of the behavior
	*/
	public CCFlocking(final double theDistance, final double theAngle){
		super(theDistance, theAngle);
		_myFlee.strength(1f);
		_mySeek.strength(1f);
	}
	
	@Override
	/**
	 * Sets the radius of the area to be searched for relevant vehicles
	 * @param nearAreaRadius New area radius
	 */
	public void nearAreaRadius(final double theNearAreaRadius) {
		super.nearAreaRadius(theNearAreaRadius);
	}
	
	/** 
	 * Calculates the resulting force vector for this frame
	 * @param veh The vehicle
	 * @return Returns the resulting force
	 */
	public boolean apply(final CCParticle theAgent, final CCVector3 theForce, double theDeltaTime){
		int myCount = 0;
		theForce.set(0,0,0);
		
		neighbors.clear();
		
		CCVector3 myTarget = new CCVector3();
		
		for (CCAgent myOtherAgent:_myNeighborhood.getNearAgents(theAgent, _myNearAreaRadius/2)){
			//if (isInAngle(theAgent,myOtherAgent)){
				myCount++;
				myTarget.addLocal(myOtherAgent.position);
				neighbors.add(myOtherAgent);
			//}
		}
		
		if(myCount  <= 0)return false;
		
		myTarget.multiplyLocal(1.0F / myCount);
		
		final CCVector3 myTempForce = new CCVector3();
		
		/* apply separation */
		_myFlee.target(myTarget);
		_myFlee.apply(theAgent, myTempForce, 0);
		myTempForce.normalizeLocal();
		myTempForce.multiplyLocal(_myFlee.strength());
		theForce.addLocal(myTempForce);
		
		myTarget = new CCVector3();
		
		for (CCAgent myOtherAgent:_myNeighborhood.getNearAgents(theAgent, _myNearAreaRadius)){
			//if (isInAngle(theAgent,myOtherAgent)){
				myCount++;
				myTarget.addLocal(myOtherAgent.position);
				neighbors.add(myOtherAgent);
			//}
		}
		
		if(myCount  <= 0)return false;
		
		myTarget.multiplyLocal(1.0F / myCount);
		
		/* apply cohesion */
		_mySeek.target(myTarget);
		_mySeek.apply(theAgent, myTempForce, 0);
		myTempForce.normalizeLocal();
		myTempForce.multiplyLocal(_mySeek.strength());
		theForce.addLocal(myTempForce);
		
		/* apply alignment */
		myTempForce.set(0,0,0);
		for(CCAgent myOtherAgent:neighbors){
			myTempForce.addLocal(myOtherAgent.forward);
		}
		
		myTempForce.multiplyLocal(3.0F / myCount);
		myTempForce.subtractLocal(theAgent.forward);
		
		theForce.addLocal(myTempForce);
		
		return true;
	}


}
