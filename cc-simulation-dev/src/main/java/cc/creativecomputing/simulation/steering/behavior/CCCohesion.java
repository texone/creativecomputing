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
