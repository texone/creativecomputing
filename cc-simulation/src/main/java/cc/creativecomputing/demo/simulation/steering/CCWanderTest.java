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
import cc.creativecomputing.simulation.steering.behavior.CCWander;

public class CCWanderTest extends CCGL2Adapter{
	
	private List<CCTestAgent> _myAgents = new ArrayList<CCTestAgent>();
	@CCProperty(name = "wander")
	private CCWander _myWander = new CCWander();
	private CCSimulation _mySimulation;
	private CCMesh _myMesh;
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
		
	@CCProperty (name = "max speed", min = 0.1f, max = 5f)
	private float _cMaxSpeed = 1;

	@CCProperty (name = "max force", min = 0.1f, max = 5f)
	private float _cMaxForce = 0.6f;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_mySimulation = new CCSimulation();

		for(int i = 0; i < 100;i++){
			CCTestAgent myAgent = new CCTestAgent();
			myAgent.maxSpeed = 4.5F;
			myAgent.maxForce = 0.3F;

			myAgent.addBehavior(_myWander);
			
			_mySimulation.addAgent(myAgent);
			_myAgents.add(myAgent);
		}
		
		_myMesh = new CCMesh();
		
		_cCameraController = new CCCameraController(this, g, 100);
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
		
		for(CCTestAgent myAgent:_myAgents){
			myAgent.maxSpeed = _cMaxSpeed;
			myAgent.maxForce = _cMaxForce;
		}
		
		_mySimulation.update(theAnimator);
	}
	
	@Override
	public void display(CCGraphics g){
		g.clear();
//		_cCameraController.camera().draw(g);

		for(CCTestAgent myAgent:_myAgents){

			if(myAgent.position.x < -g.width() / 2)myAgent.position.x += g.width();
			if(myAgent.position.x >  g.width() / 2)myAgent.position.x -= g.width();
			if(myAgent.position.y < -g.height() / 2)myAgent.position.y += g.height();
			if(myAgent.position.y >  g.height() / 2)myAgent.position.y -= g.height();
		}
		
//		g.frustum().update();
		List<CCVector3> myVertices = new ArrayList<>();
		for(CCTestAgent myAgent:_myAgents){
//			myAgent.frustumWrap(g);
//			myAgent.draw(myVertices);
			g.color(1d,0.25d);
			g.ellipse(myAgent.position.x,  myAgent.position.y, 20,20);
			
			g.color(1d,0,0);
			g.line(
				myAgent.position.x,  
				myAgent.position.y,
				myAgent.position.x + myAgent.velocity().x * 50, 
				myAgent.position.y +  myAgent.velocity().y * 50
			);
		}
//		_myMesh.vertices(myVertices,true);
//		_myMesh.draw(g);
		
		g.clearDepthBuffer();
	}
	
	public static void main(String[] args) {
		CCWanderTest demo = new CCWanderTest();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}

}
