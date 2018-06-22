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
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.CCParticleGroup;
import cc.creativecomputing.simulation.CCSimulation;
import cc.creativecomputing.simulation.steering.behavior.CCArrive;

public class CCArrivalTest extends CCGL2Adapter{
	
	private CCParticleGroup<CCTestAgent> _myAgents = new CCParticleGroup<CCTestAgent>();
	private CCArrive _myArrive;
	private CCSimulation _mySimulation;
	private CCMesh _myMesh;
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	private static class Settings{
		@CCProperty (name = "slowing distance", min = 0.1f, max = 500f)
		private static float slowingDistance = 1;
		
		@CCProperty (name = "max speed", min = 0.1f, max = 15f)
		private static float maxSpeed = 1;

		@CCProperty (name = "max force", min = 0.1f, max = 5f)
		private static float maxForce = 0.6f;
	}
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_mySimulation = new CCSimulation();
		
		_myArrive = new CCArrive(new CCVector3(),100);
		
		for(int i = 0; i < 1000;i++){
			CCTestAgent myAgent = new CCTestAgent();
			myAgent.maxSpeed = 4.5F;
			myAgent.maxForce = 0.3F;
			myAgent.position = new CCVector3().randomize(1000);
			myAgent.velocity(new CCVector3().randomize(100));
			myAgent.velocity().randomize();
			myAgent.addBehavior(_myArrive);
			
			_myAgents.add(myAgent);
		}
		_mySimulation.addParticleGroup(_myAgents);
		
		_myMesh = new CCMesh();
		
		_cCameraController = new CCCameraController(this, g, 100);
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
		_myArrive.slowingDistance(Settings.slowingDistance);
		
		for(CCTestAgent myAgent:_myAgents){
			myAgent.maxSpeed = Settings.maxSpeed;
			myAgent.maxForce = Settings.maxForce;
			
			if(myAgent.position.distance(_myArrive.target()) < 10){
				myAgent.position = new CCVector3().randomize(1000);
				myAgent.velocity(new CCVector3().randomize(100));
			}
		}
	}
	
	@Override
	public void display(CCGraphics g){
		g.clear();
		
		_cCameraController.camera().draw(g);
		List<CCVector3> myVertices = new ArrayList<>();
		for(CCTestAgent myAgent:_myAgents){
			myAgent.draw(myVertices);
		}
		_myMesh.vertices(myVertices,true);
		_myMesh.draw(g);
		
		g.clearDepthBuffer();
	}
	
	public static void main(String[] args) {
		
		CCArrivalTest demo = new CCArrivalTest();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}

}
