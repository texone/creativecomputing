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

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDirectionalLight;
import cc.creativecomputing.graphics.CCGraphics.CCColorMaterialMode;
import cc.creativecomputing.graphics.CCMaterial;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.CCSimulation;
import cc.creativecomputing.simulation.steering.CCNeighborhood;
import cc.creativecomputing.simulation.steering.behavior.CCAlignment;

public class CCAlignmentTest extends CCApp {

	private CCNeighborhood<CCTestAgent> _myNeighborhood = new CCNeighborhood<CCTestAgent>();
	private CCAlignment _myAlignment;
	private CCSimulation _mySimulation;
	private CCMesh _myMesh;

	private CCArcball _myArcball;

	@CCControl(name = "max speed", min = 0.1f, max = 15f)
	private float _cMaxSpeed = 1;

	@CCControl(name = "max force", min = 0.1f, max = 15f)
	private float _cMaxForce = 0.6f;

	@CCControl(name = "alignment near radius", min = 0.1f, max = 500f)
	private float _cAlignmentNearRadius = 0.6f;

	@CCControl(name = "alignment near angle", min = 0.1f, max = 360f)
	private float _cAlignmentNearAngle = 0.6f;

	@Override
	public void setup() {
		_mySimulation = new CCSimulation(this);
		_myAlignment = new CCAlignment(200, 160);

		for (int i = 0; i < 500; i++) {
			CCTestAgent myAgent = new CCTestAgent();

			myAgent.maxSpeed = 4.5F;
			myAgent.maxForce = 0.3F;
			myAgent.position = CCVecMath.random(-1000, 1000, -1000, 1000, -1000, 1000);
			myAgent.velocity(new CCVector3f());
			myAgent.velocity().randomize();
			myAgent.velocity().scale(10);

			myAgent.addBehavior(_myAlignment);
			_myNeighborhood.addParticle(myAgent);
		}

		_mySimulation.addParticleGroup(_myNeighborhood);
		_myMesh = new CCMesh();

		g.lights();
		CCDirectionalLight myDirectionalLight = new CCDirectionalLight(1, 1, 1, 1, 0, 0);
		myDirectionalLight.specular(1f, 1f, 1f);
		g.light(myDirectionalLight);

		CCMaterial myMaterial = new CCMaterial();
		myMaterial.diffuse(125, 125, 125);
		myMaterial.specular(0, 0, 45);

		g.colorMaterial(CCColorMaterialMode.OFF);
		g.material(myMaterial);

		addControls("app", "app", this);

		_myArcball = new CCArcball(this);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myAlignment.nearAreaRadius(_cAlignmentNearRadius);
		_myAlignment.nearAngle(_cAlignmentNearAngle);

		for (CCTestAgent myAgent : _myNeighborhood.particles()) {
			myAgent.maxSpeed = _cMaxSpeed;
			myAgent.maxForce = _cMaxForce;
		}
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);

		g.frustum().update();
		List<CCVector3f> myVertices = new ArrayList<CCVector3f>();
		for (CCTestAgent myAgent : _myNeighborhood.particles()) {
			myAgent.frustumWrap(g);
			myAgent.draw(myVertices);
		}
		_myMesh.vertices(myVertices, true);
		_myMesh.draw(g);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCAlignmentTest.class);
		myManager.settings().size(1000, 600);
		myManager.settings().antialiasing(8);
		myManager.start();
	}

}
