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
import cc.creativecomputing.simulation.steering.behavior.CCAlignment;

public class CCAlignmentTest extends CCGL2Adapter {

	private CCNeighborhood<CCTestAgent> _myNeighborhood = new CCNeighborhood<CCTestAgent>();
	private CCAlignment _myAlignment;
	private CCSimulation _mySimulation;
	private CCMesh _myMesh;

	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;

	@CCProperty(name = "max speed", min = 0.1f, max = 15f)
	private float _cMaxSpeed = 1;

	@CCProperty(name = "max force", min = 0.1f, max = 15f)
	private float _cMaxForce = 0.6f;

	@CCProperty(name = "alignment near radius", min = 0.1f, max = 500f)
	private float _cAlignmentNearRadius = 0.6f;

	@CCProperty(name = "alignment near angle", min = 0.1f, max = 360f)
	private float _cAlignmentNearAngle = 0.6f;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_mySimulation = new CCSimulation();
		_myAlignment = new CCAlignment(200, 160);

		for (int i = 0; i < 500; i++) {
			CCTestAgent myAgent = new CCTestAgent();

			myAgent.maxSpeed = 4.5F;
			myAgent.maxForce = 0.3F;
			myAgent.position = new CCVector3().randomize(1000);
			myAgent.velocity(new CCVector3());
			myAgent.velocity().randomize(10);

			myAgent.addBehavior(_myAlignment);
			_myNeighborhood.add(myAgent);
		}

		_mySimulation.addParticleGroup(_myNeighborhood);
		_myMesh = new CCMesh();

		_cCameraController = new CCCameraController(this, g, 100);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_myAlignment.nearAreaRadius(_cAlignmentNearRadius);
		_myAlignment.nearAngle(_cAlignmentNearAngle);

		for (CCTestAgent myAgent : _myNeighborhood) {
			myAgent.maxSpeed = _cMaxSpeed;
			myAgent.maxForce = _cMaxForce;
		}
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		_cCameraController.camera().draw(g);

		List<CCVector3> myVertices = new ArrayList<>();
		for (CCTestAgent myAgent : _myNeighborhood) {
			myAgent.draw(myVertices);
		}
		_myMesh.vertices(myVertices, true);
		_myMesh.draw(g);
	}

	public static void main(String[] args) {
		CCAlignmentTest demo = new CCAlignmentTest();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}

}
