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
import cc.creativecomputing.simulation.domain.CCDomain;
import cc.creativecomputing.simulation.force.CCDomainForce;

/**
 * Steer agents away from a domain of space.
 * <p>
 * Agents are tested to see whether they will pass from being 
 * outside the specified domain to being inside it within look_ahead 
 * time units from now if the next Move() action were to occur now. 
 * The specific direction and amount of turn is dependent on the kind 
 * of domain being avoided.
 * </p>
 * <p>
 * At present the only domains for which Avoid() is implemented are 
 * CCSphere, CCRectangle, CCTriangle, CCDisc and CCPlane.
 * </p>
       
 * @author info
 *
 */
public class CCAvoidance extends CCDomainForce{
	
	private double _myLookAhead;	// how many time units ahead to look
    private double _myEpsilon;		// add to r^2 for softening
	
    /**
     * Creates a new avoidance,
     * @param theLookAhead 
     * 		how far forward along the velocity vector to look for the obstacle
     * @param theEpsilon 
     * 		The amount of acceleration falls off inversely with the squared 
     * 		distance to the edge of the domain. But when that distance is small, 
     * 		the acceleration would be infinite, so epsilon is always added to the 
     * 		distance.
     */
	public CCAvoidance(final double theLookAhead, final double theEpsilon){
		_myLookAhead = theLookAhead;
		_myEpsilon = theEpsilon;
	}
	
	public double lookAhead(){
		return _myLookAhead;
	}
	
	public void lookAhead(final double theLookAhead){
		_myLookAhead = theLookAhead;
	}
	
	public double epsilon(){
		return _myEpsilon;
	}
	
	public void epsilon(final double theEpsilon){
		_myEpsilon = theEpsilon;
	}

	public boolean apply(CCParticle theParticle, CCVector3 theForce, double theDeltaTime) {
		for(CCDomain myDomain:_myDomains){
			myDomain.avoidance(theParticle, theForce, _myLookAhead, _myEpsilon);
		}
		return false;
	}

}
