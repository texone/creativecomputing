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
package cc.creativecomputing.simulation;


import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.force.CCForce;

public class CCParticleGroup<ParticleType extends CCParticle> extends ArrayList<ParticleType>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8601701907871614362L;
	
	private List<CCForce> _myForces = new ArrayList<CCForce>();
	
	public CCParticleGroup(){
		
	}
	
	public void addForce(final CCForce theForce){
		_myForces.add(theForce);
	}
	
	public void update(final double theDeltaTime){

		for(int i = size() - 1; i >= 0; i--){
			if(get(i).isKilled()){
				remove(i);
			}
		}
		for(CCParticle myParticle:this){
			CCVector3 myForceVector = new CCVector3();
			for(CCForce myForce:_myForces){
				myForce.apply(myParticle, myForceVector, theDeltaTime);
			}
			myParticle.applyForce(myForceVector);

			myParticle.update(theDeltaTime);
		}
	}
}
