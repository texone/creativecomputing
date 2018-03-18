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
