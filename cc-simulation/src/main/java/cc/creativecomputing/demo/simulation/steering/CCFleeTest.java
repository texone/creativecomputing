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
import cc.creativecomputing.simulation.force.CCGravity;
import cc.creativecomputing.simulation.steering.behavior.CCFlee;
import cc.creativecomputing.simulation.steering.behavior.CCWander;

public class CCFleeTest extends CCGL2Adapter{
	
	private List<CCTestAgent> _myAgents = new ArrayList<>();
	@CCProperty(name = "wander")
	private CCWander _myWander = new CCWander();
	@CCProperty(name = "")
	private CCFlee _myFlee = new CCFlee();
	private CCSimulation _mySimulation;
	private CCMesh _myMesh;
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	CCGravity _myWind = new CCGravity();
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_mySimulation = new CCSimulation();

		for(int i = 0; i < 1000;i++){
			CCTestAgent myAgent = new CCTestAgent();
			myAgent.maxSpeed = 4.5F;
			myAgent.maxForce = 0.3F;
			
			myAgent.position = new CCVector3().randomize(1000);
			myAgent.velocity(new CCVector3().randomize(100));

			myAgent.addBehavior(_myFlee);
			myAgent.addBehavior(_myWander);
			
			_mySimulation.addAgent(myAgent);
			_myAgents.add(myAgent);
		}
		
		mouseDragged().add(e -> {

			_myFlee.target().x = e.x() - g.width()/2;
			_myFlee.target().y = g.height()/2 - e.y();
		});
		
		_myMesh = new CCMesh();
		
		_cCameraController = new CCCameraController(this, g, 100);
	}
	
	@CCProperty (name = "max speed", min = 0.1f, max = 5f)
	void maxSpeed(float theSpeed){
		for(CCTestAgent myAgent:_myAgents){
			myAgent.maxSpeed = theSpeed;
		}
	}

	@CCProperty (name = "max force", min = 0.1f, max = 5f)
	void maxForce(float theForce){
		for(CCTestAgent myAgent:_myAgents){
			myAgent.maxForce = theForce;
		}
	}
	
	@CCProperty (name = "reset")
	void reset(boolean bla){
		for(CCTestAgent myAgent:_myAgents){
			myAgent.position = new CCVector3().randomize(1000);
			myAgent.velocity(new CCVector3().randomize(100));
		}
	}
	
	boolean set = false;
	
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
//		for(CCTestAgent myAgent:_myAgents){
//			myAgent.boundingBox().draw(g);
//		}
		
		g.clearDepthBuffer();
	}
	
	public static void main(String[] args) {
		CCFleeTest demo = new CCFleeTest();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}

}
