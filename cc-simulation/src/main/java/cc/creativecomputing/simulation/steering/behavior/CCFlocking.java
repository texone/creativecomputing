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
		_myFlee.weight(1f);
		_mySeek.weight(1f);
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
		myTempForce.multiplyLocal(_myFlee.weight());
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
		myTempForce.multiplyLocal(_mySeek.weight());
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
