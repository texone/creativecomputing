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

public class CCParticleGroup<ParticleType extends CCParticle>{

	protected List<ParticleType> _myParticles = new ArrayList<ParticleType>();
	
	private List<CCForce> _myForces = new ArrayList<CCForce>();
	
	public CCParticleGroup(){
		
	}
	
	public void addParticle(final ParticleType theParticle){
		_myParticles.add(theParticle);
	}
	
	public void addForce(final CCForce theForce){
		_myForces.add(theForce);
	}
	
	public List<ParticleType> particles(){
		return _myParticles;
	}
	
	public void update(final double theDeltaTime){
		for(int i = _myParticles.size() - 1; i >= 0; i--){
			if(_myParticles.get(i).isKilled()){
				_myParticles.remove(i);
			}
		}
		for(CCParticle myParticle:_myParticles){
			CCVector3 myForceVector = new CCVector3();
			for(CCForce myForce:_myForces){
				myForce.apply(myParticle, myForceVector, theDeltaTime);
			}
			myParticle.applyForce(myForceVector);
			myParticle.update(theDeltaTime);
		}
	}
}
