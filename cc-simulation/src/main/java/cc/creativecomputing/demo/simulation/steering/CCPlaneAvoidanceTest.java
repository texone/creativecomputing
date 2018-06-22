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
import cc.creativecomputing.graphics.CCGraphics.CCColorMaterialMode;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.shader.postprocess.deferred.CCDirectionalLight;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.model.obj.CCMaterial;
import cc.creativecomputing.simulation.CCSimulation;
import cc.creativecomputing.simulation.domain.CCPlaneDomain;
import cc.creativecomputing.simulation.steering.behavior.CCAvoidance;
import cc.creativecomputing.simulation.steering.behavior.CCWander;

public class CCPlaneAvoidanceTest extends CCGL2Adapter{
	
	private List<CCTestAgent> _myAgents = new ArrayList<CCTestAgent>();

	@CCProperty(name = "avoidance")
	private CCAvoidance _myAvoidance;
	private CCPlaneDomain _myPlane1;
	private CCPlaneDomain _myPlane2;
	@CCProperty(name = "wander")
	private CCWander _myWander;
	private CCSimulation _mySimulation;
	private CCMesh _myMesh;
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
		
	
	@CCProperty (name = "max speed", min = 0.1f, max = 5f)
	private float maxSpeed = 1;

	@CCProperty (name = "max force", min = 0.1f, max = 5f)
	private float maxForce = 0.6f;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_mySimulation = new CCSimulation();
		_myAvoidance = new CCAvoidance(200,1);
		_myWander = new CCWander();
		_myPlane1 = new CCPlaneDomain(new CCVector3(0,-200,0),new CCVector3(0,1,0));
		_myPlane2 = new CCPlaneDomain(new CCVector3(0,200,0),new CCVector3(0,1,0));
		_myAvoidance.addDomain(_myPlane1);
		_myAvoidance.addDomain(_myPlane2);
		_myAvoidance.strength(100);

		for(int i = 0; i < 500;i++){
			CCTestAgent myAgent = new CCTestAgent();
			myAgent.maxSpeed = 4.5F;
			myAgent.maxForce = 0.3F;
			myAgent.velocity(new CCVector3().randomize(100));

			myAgent.addBehavior(_myWander);
			myAgent.addBehavior(_myAvoidance);
			
			_mySimulation.addAgent(myAgent);
			_myAgents.add(myAgent);
		}
		
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
		
//		_myPlane1.draw(g);
//		_myPlane2.draw(g);
		g.clearDepthBuffer();
	}
	
	public static void main(String[] args) {
		
		CCPlaneAvoidanceTest demo = new CCPlaneAvoidanceTest();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}

}
