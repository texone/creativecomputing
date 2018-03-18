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

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorListener;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.simulation.force.CCForce;
import cc.creativecomputing.simulation.steering.CCAgent;

public class CCSimulation{
	
	public static float TARGET_FRAMERATE = 30;
	
	private List<CCParticleGroup<?>> _myParticleGroups = new ArrayList<CCParticleGroup<?>>();
	
	private CCSimulationThread[] _mySimulationThreads = new CCSimulationThread[10];
	
	private List<CCAnimatorListener> _myPreListeners = new ArrayList<CCAnimatorListener>();
	private CCParticleGroup<CCParticle> _myDefaultParticleGroup;
	private CCParticleGroup<CCAgent> _myDefaultAgentGroup;
	
	@CCProperty(name = "speed")
	private double _cSpeed = 1;

	public CCSimulation(){
		_myDefaultParticleGroup = new CCParticleGroup<CCParticle>();
		_myDefaultAgentGroup = new CCParticleGroup<CCAgent>();
		_myParticleGroups.add(_myDefaultParticleGroup);
		_myParticleGroups.add(_myDefaultAgentGroup);
	}
	
	public void addParticleGroup(final CCParticleGroup<?> theParticleGroup){
		_myParticleGroups.add(theParticleGroup);
	}
	
	public void addParticleGroup(final CCParticleGroup<?> theParticleGroup, final int theThreadID){
		if(_mySimulationThreads[theThreadID] == null){
			_mySimulationThreads[theThreadID] = new CCSimulationThread(theParticleGroup);
			_mySimulationThreads[theThreadID].start();
		}else{
			_mySimulationThreads[theThreadID].addParticleGroup(theParticleGroup);
		}
		
	}
	
	public void addParticle(final CCParticle theParticle){
		_myDefaultParticleGroup.add(theParticle);
	}
	
	public void addForce(final CCForce theForce){
		_myDefaultParticleGroup.addForce(theForce);
	}
	
	public void addAgent(final CCAgent theAgent){
		_myDefaultAgentGroup.add(theAgent);
		_myDefaultParticleGroup.add(theAgent);
	}
	
	public List<CCAgent> agents(){
		return _myDefaultAgentGroup;
	}
	
	public void addPreListener(final CCAnimatorListener theListener){
		_myPreListeners.add(theListener);
	}

	public void update(CCAnimator theAnimator) {
		for(CCAnimatorListener myPreListener:_myPreListeners){
			myPreListener.update(theAnimator);
		}
		for(CCParticleGroup<?> myParticleGroup:_myParticleGroups){
			myParticleGroup.update(theAnimator.deltaTime() * _cSpeed);
		}
	}

	
}
