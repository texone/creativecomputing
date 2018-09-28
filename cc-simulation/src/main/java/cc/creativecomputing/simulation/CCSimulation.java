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

import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.core.CCEventManager.CCEvent;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.simulation.force.CCForce;
import cc.creativecomputing.simulation.steering.CCAgent;

public class CCSimulation{
	
	public static float TARGET_FRAMERATE = 30;
	
	private List<CCParticleGroup<?>> _myParticleGroups = new ArrayList<CCParticleGroup<?>>();
	
	private CCSimulationThread[] _mySimulationThreads = new CCSimulationThread[10];
	
	private CCEventManager<CCGLTimer> _myPreListeners = new CCEventManager<>();
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
	
	public void addPreListener(final CCEvent<CCGLTimer> theListener){
		_myPreListeners.add(theListener);
	}

	public void update(CCGLTimer theAnimator) {
		_myPreListeners.event(theAnimator);
		for(CCParticleGroup<?> myParticleGroup:_myParticleGroups){
			myParticleGroup.update(theAnimator.deltaTime() * _cSpeed);
		}
	}

	
}
