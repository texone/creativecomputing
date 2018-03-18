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


public class CCCohesion extends CCNeighborHoodBehavior{
	
	private final CCSeekFlee _mySeek = new CCSeekFlee(new CCVector3());

	public CCCohesion(final double theDistance, final double theAngle){
		super(theDistance, theAngle);
	}
	
	@Override
	public boolean apply(final CCParticle theAgent, final CCVector3 theForce, double theDeltaTime){
		boolean myDoApply = super.apply(theAgent,theForce, theDeltaTime);
		
		if(myDoApply){
			_mySeek.target(theForce);
			_mySeek.apply(theAgent, theForce, 0);
		}
		
		return myDoApply;
	}
}
