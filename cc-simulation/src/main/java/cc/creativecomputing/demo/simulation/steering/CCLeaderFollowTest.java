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
import cc.creativecomputing.simulation.CCSimulation;
import cc.creativecomputing.simulation.steering.CCNeighborhood;
import cc.creativecomputing.simulation.steering.behavior.CCLeaderFollow;
import cc.creativecomputing.simulation.steering.behavior.CCWander;

public class CCLeaderFollowTest extends CCGL2Adapter{
	
	private List<CCTestAgent> _myAgents = new ArrayList<CCTestAgent>();
	private CCSimulation _mySimulation;
	
	private CCTestAgent _myLeader;
	private CCWander _myWander;
	
	private CCMesh _myMesh;
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	

		
	@CCProperty (name = "max speed", min = 0.1f, max = 5f)
	private static float maxSpeed = 1;

	@CCProperty (name = "max force", min = 0.1f, max = 5f)
	private static float maxForce = 0.6f;

	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_mySimulation = new CCSimulation();

		_myLeader = new CCTestAgent();
		_myLeader.maxSpeed = 4.5F;
		_myLeader.maxForce = 0.3F;
		
		_myWander = new CCWander();
		_myLeader.addBehavior(_myWander);
		CCNeighborhood<CCTestAgent> myNeighborhood = new CCNeighborhood<CCTestAgent>();
		_mySimulation.addAgent(_myLeader);
		_myAgents.add(_myLeader);
		
		CCLeaderFollow myLeaderFollow = new CCLeaderFollow(_myLeader);
		
		for(int i = 0; i < 300;i++){
			CCTestAgent myAgent = new CCTestAgent();
			myAgent.maxSpeed = 4.5F;
			myAgent.maxForce = 0.3F;
			myAgent.addBehavior(myLeaderFollow);
			
			myAgent.position = new CCVector3().randomize(1000);
			myAgent.velocity(new CCVector3().randomize(10));
			
			myNeighborhood.add(myAgent);
			_myAgents.add(myAgent);
		}
		
		_mySimulation.addParticleGroup(myNeighborhood);
		_myMesh = new CCMesh();
		
		_cCameraController = new CCCameraController(this, g, 100);
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
		
		for(CCTestAgent myAgent:_myAgents){
			myAgent.maxSpeed = maxSpeed;
			myAgent.maxForce = maxForce;
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
		
		CCLeaderFollowTest demo = new CCLeaderFollowTest();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}

}
