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
