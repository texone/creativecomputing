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
package cc.creativecomputing.demo.simulation.steering;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.simulation.CCSimulation;
import cc.creativecomputing.simulation.steering.CCAgent;
import cc.creativecomputing.simulation.steering.behavior.CCWander;

public class CCWanderTest extends CCGL2Adapter{
	
	private List<CCAgent> _myAgents = new ArrayList<>();
	private List<CCWander> _myWanders = new ArrayList<>();
	private CCSimulation _mySimulation;
	
	
	@CCProperty (name = "wanderer strength", min = 0.1f, max = 5f)
	private float _cWandererStrength = 1;

	@CCProperty (name = "wanderer rate", min = 0.1f, max = 15f)
	private float _cWandererRate = 0.6f;
		
	@CCProperty (name = "max speed", min = 0.1f, max = 5f)
	private float _cMaxSpeed = 1;

	@CCProperty (name = "max force", min = 0.1f, max = 5f)
	private float _cMaxForce = 0.6f;
	
	@Override
	public void start(CCAnimator theAnimator){
		_mySimulation = new CCSimulation();

		for(int i = 0; i < 1000;i++){
			CCAgent myAgent = new CCAgent();
			myAgent.maxSpeed = 4.5F;
			myAgent.maxForce = 0.3F;

			CCWander myWanderer = new CCWander();
			myAgent.addBehavior(myWanderer);
			
			_mySimulation.addAgent(myAgent);
			_myAgents.add(myAgent);
			_myWanders.add(myWanderer);
		}
		
		
//		g.lights();
		
	}
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		
	}
	
	@Override
	public void update(final CCAnimator theAnimator){
		for(CCWander myWanderer:_myWanders){
			myWanderer.wanderStrength(_cWandererStrength);
			myWanderer.wanderRate(_cWandererRate);
		}
		
		for(CCAgent myAgent:_myAgents){
			myAgent.maxSpeed = _cMaxSpeed;
			myAgent.maxForce = _cMaxForce;
		}
		
		_mySimulation.update(theAnimator);
	}
	
	@Override
	public void display(CCGraphics g){
		g.clear();
//		_myArcball.draw(g);
		g.color(255);
		for(CCAgent myAgent:_myAgents){
			g.ellipse(myAgent.position,30);
		}
		
		g.clearDepthBuffer();
	}
	
	public static void main(String[] args) {
		
		CCGL2Application myAppManager = new CCGL2Application(new CCWanderTest());
		myAppManager.glcontext().size(1800, 900);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.glcontext().inVsync = false;
		myAppManager.glcontext().deviceSetup().display(1);
		myAppManager.start();
	}

}
