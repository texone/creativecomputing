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

/**
 * Removes all particles older than the set maximum life time. 
 * @author info
 */
public class CCKillOld extends CCForce{
	
	private double _myMaxLifeTime;
	
	public CCKillOld(final double theMaxLifeTime){
		_myMaxLifeTime = theMaxLifeTime;
	}
	
	public double maximumLifeTime(){
		return _myMaxLifeTime;
	}
	
	public void maximumLifeTime(final double theMaxLifeTime){
		_myMaxLifeTime = theMaxLifeTime;
	}

	public boolean apply(CCParticle theParticle, CCVector3 theForce, double theDeltaTime) {
		theParticle.isKilled(theParticle.age() > _myMaxLifeTime);
		return false;
	}

}
