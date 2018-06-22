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
import cc.creativecomputing.simulation.steering.behavior.CCSteerToRandom;

public class CCSteerToRandomTest extends CCGL2Adapter {

	private List<CCTestAgent> _myAgents = new ArrayList<>();
	private List<CCSteerToRandom> _mySteerToRandoms = new ArrayList<CCSteerToRandom>();
	private CCSimulation _mySimulation;
	private CCMesh _myMesh;

	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;

	@CCProperty(name = "steer to random range", min = 10f, max = 200f)
	private double steerToRandomRange = 1;

	@CCProperty(name = "steer to random switch time", min = 0.1f, max = 5f)
	private double steerToRandomSwitchTime = 0.6f;

	@CCProperty(name = "max speed", min = 0.1f, max = 5f)
	private double maxSpeed = 1;

	@CCProperty(name = "max force", min = 0.1f, max = 5f)
	private double maxForce = 0.6f;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_mySimulation = new CCSimulation();

		for (int i = 0; i < 1000; i++) {
			CCTestAgent myAgent = new CCTestAgent();
			myAgent.maxSpeed = 4.5F;
			myAgent.maxForce = 0.3F;

			// myAgent.position = CCVecMath.random(-1000, 1000, -1000, 1000,
			// -1000, 1000);
			myAgent.velocity(new CCVector3());
			myAgent.velocity().randomize();
			myAgent.velocity().multiplyLocal(10);

			CCSteerToRandom mySteerToRandom = new CCSteerToRandom(200, 2);
			myAgent.addBehavior(mySteerToRandom);

			_myAgents.add(myAgent);
			_mySimulation.addAgent(myAgent);
			_mySteerToRandoms.add(mySteerToRandom);
		}

		_myMesh = new CCMesh();

		// g.lights();
		// CCDirectionalLight myDirectionalLight = new
		// CCDirectionalLight(1,1,1,1,0,0);
		// myDirectionalLight.specular(1f, 1f, 1f);
		// g.light(myDirectionalLight);
		//
		// CCMaterial myMaterial = new CCMaterial();
		// myMaterial.diffuse(125,125,125);
		// myMaterial.specular(0,0,45);
		//
		// g.colorMaterial(CCColorMaterialMode.OFF);
		// g.material(myMaterial);

		_cCameraController = new CCCameraController(this, g, 100);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		for (CCSteerToRandom mySteerToRandom : _mySteerToRandoms) {
			mySteerToRandom.range(steerToRandomRange);
			mySteerToRandom.switchTime(steerToRandomSwitchTime);
		}

		for (CCTestAgent myAgent : _myAgents) {
			myAgent.maxSpeed = maxSpeed;
			myAgent.maxForce = maxForce;
		}
		
		_mySimulation.update(theAnimator);
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		_cCameraController.camera().draw(g);

		List<CCVector3> myVertices = new ArrayList<>();
		for (CCTestAgent myAgent : _myAgents) {
			// myAgent.frustumWrap(g);
			myAgent.draw(myVertices);
		}
		_myMesh.vertices(myVertices, true);
		_myMesh.draw(g);

		g.clearDepthBuffer();
	}

	public static void main(String[] args) {

		CCSteerToRandomTest demo = new CCSteerToRandomTest();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}

}
