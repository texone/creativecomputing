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
package cc.creativecomputing.simulation.force;

import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.CCParticle;

public class CCGravity extends CCForce{
	
	private CCVector3 _myGravity;
	
	public CCGravity(final CCVector3 theGravity){
		_myGravity = theGravity;
	}
	
	public CCGravity(final double theX, final double theY, final double theZ){
		_myGravity = new CCVector3(theX, theY, theZ);
	}
	
	public CCVector3 gravity(){
		return _myGravity;
	}
	
	public void gravity(final CCVector3 theGravity){
		_myGravity = theGravity;
	}

	public boolean apply(CCParticle theParticle, CCVector3 theForce, double theDeltaTime) {
		theForce.set(_myGravity);
		return true;
	}

}
