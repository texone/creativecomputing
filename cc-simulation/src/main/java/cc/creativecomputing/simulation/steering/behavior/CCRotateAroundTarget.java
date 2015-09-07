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

public class CCRotateAroundTarget extends CCTargetBehavior{
	
	private double _myRadius;

	public CCRotateAroundTarget(CCVector3 theTarget, final double theRadius) {
		super(theTarget);
		_myRadius = theRadius;
	}

	public boolean apply(CCParticle theAgent, CCVector3 theForce, double theDeltaTime) {
		CCVector3 dist = theAgent.position.clone();
		dist.subtractLocal(_myTarget);
		
		CCVector3 myForce = dist.clone();
		myForce.normalizeLocal();
		myForce = myForce.cross(new CCVector3(0,1,0));
		theForce.set(theAgent.forward);
		theForce.multiplyLocal(20);
		double distance = _myRadius - dist.length();
		
		dist.normalizeLocal();
		dist.multiplyLocal(distance);
		
		theForce.addLocal(dist);
		
		return true;
	}

}
