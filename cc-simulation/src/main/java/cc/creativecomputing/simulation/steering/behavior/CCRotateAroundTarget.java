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
