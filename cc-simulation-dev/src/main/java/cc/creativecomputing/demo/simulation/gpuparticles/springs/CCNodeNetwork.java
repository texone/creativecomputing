/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
package cc.creativecomputing.demo.simulation.particles.springs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCGPUParticles;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCGPUAttractor;
import cc.creativecomputing.simulation.particles.forces.CCGPUNoiseCurveField;
import cc.creativecomputing.simulation.particles.forces.CCGPUForce;
import cc.creativecomputing.simulation.particles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.particles.forces.CCGPUGravity;
import cc.creativecomputing.simulation.particles.forces.CCGPUViscousDrag;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.forces.springs.CCDampedSprings;
import cc.creativecomputing.simulation.particles.render.CCParticlePointRenderer;
import cc.creativecomputing.simulation.particles.render.CCGPUSpringRenderer;
import cc.creativecomputing.util.CCStopWatch;

public class CCNodeNetwork {

	@CCProperty(name = "spring strength", min = 0, max = 4f)
	private float _cSpringStrength = 0;

	@CCProperty(name = "spring constant", min = 0, max = 4f)
	private float _cSpringConstant = 0;

	@CCProperty(name = "spring damping", min = 0, max = 4f)
	private float _cSpringDamping = 0;

	@CCProperty(name = "max spring length", min = 0, max = 300f)
	private float _cMaxSpringLength = 0;

	@CCProperty(name = "viscous drag", min = 0, max = 4f)
	private float _cDrag = 0;

	@CCProperty(name = "noise strength", min = 0, max = 10)
	private float _cFieldStrength = 0;

	@CCProperty(name = "attractor strength", min = -10, max = 10)
	private float _cAttractorStrength = 0;

	@CCProperty(name = "attractor radius", min = 0, max = 300)
	private float _cAttractorRadius = 0;

	@CCProperty(name = "gravity strength", min = 0, max = 1)
	private float _cGravityStrength = 0;

	@CCProperty(name = "curve strength", min = 0, max = 10)
	private float _cCurveStrength = 0;

	@CCProperty(name = "noise speed", min = 0, max = 1)
	private float _cCurveSpeed = 0;

	@CCProperty(name = "prediction", min = 0, max = 1)
	private float _cPrediction = 0;

	@CCProperty(name = "curveNoiseScale", min = 0, max = 1)
	private float _cCurveNoiseScale = 0;

	@CCProperty(name = "curveOutputScale", min = 0, max = 200)
	private float _cCurveOuputScale = 0;

	@CCProperty(name = "curveRadius", min = 0, max = 400)
	private float _cCurveRadius = 0;

	@CCProperty(name = "emit radius", min = 0, max = 400)
	private float _cEmitRadius = 0;

	@CCProperty(name = "emit amount", min = 1, max = 20)
	private int _cEmitAmount = 0;

	@CCProperty(name = "life time", min = 0, max = 30)
	private float _cLifeTime = 0;

	class CCNodeDistanceSorter implements Comparator<CCNode> {

		private CCNode _myNode;

		public CCNodeDistanceSorter(CCNode theNode) {
			_myNode = theNode;
		}

		@Override
		public int compare(CCNode theO1, CCNode theO2) {
			if (theO1.position().distanceSquared(_myNode.position()) < theO2.position().distanceSquared(_myNode.position()))
				return -1;

			return 1;
		}
	}

	private List<CCNode> _myNodeSet = new ArrayList<CCNode>();
	private CCApp _myApp;

	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;

	private CCGPUNoiseCurveField _myCurveField = new CCGPUNoiseCurveField();
	private CCGPUForceField _myForceField = new CCGPUForceField(0.005f, 1, new CCVector3f(100, 20, 30));
	private CCGravity _myGravity = new CCGPUGravity(new CCVector3f(10, 0, 0));
	private CCGPUAttractor _myAttractor = new CCGPUAttractor(new CCVector3f(), 0, 0);

	private CCGPUSpringRenderer _myRenderer;
	private CCParticlePointRenderer _myPointRenderer;
	private CCDampedSprings _mySprings;
	private CCGPUViscousDrag _myDrag;
	
	private float[] _myData;

	public CCNodeNetwork(CCApp theApp) {

		_myApp = theApp;

		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(_myDrag = new CCGPUViscousDrag(0.3f));
		myForces.add(_myCurveField);
		myForces.add(_myGravity);
		myForces.add(_myForceField);
		myForces.add(_myAttractor);
		_mySprings = new CCDampedSprings(_myApp.g, 8, 1f, 0.1f, 0.1f);
		myForces.add(_mySprings);

		_myRenderer = new CCGPUSpringRenderer(_mySprings);
		_myPointRenderer = new CCParticlePointRenderer();
		_myParticles = new CCGPUParticles(_myApp.g, _myRenderer, myForces, new ArrayList<CCGPUConstraint>(), 10, 10);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));

		_myPointRenderer.setup(_myParticles);

		_myData = new float[_myParticles.size() * 4];
		// createNodeSet();

		theApp.addControls("app", "app", 3, this);

	}

	private void createConnections(List<CCNode> theNewNodes) {

		List<CCNode> myCheckList = new ArrayList<>();

		for (int i = 0; i < theNewNodes.size(); i++) {
			CCNode myNode1 = theNewNodes.get(i);
			myCheckList.clear();
			for (CCNode myNode2 : _myNodeSet) {
				if (myNode2._myNeighbors.size() > 5)
					continue;
				if (myNode2.position().distance(myNode1.position()) > _cMaxSpringLength)
					continue;

				myCheckList.add(myNode2);
			}

			// System.out.println(myCheckList.size());

			Collections.sort(myCheckList, new CCNodeDistanceSorter(myNode1));

			for (int j = 1; j < myCheckList.size() && j < 4; j++) {
				CCNode myNode2 = myCheckList.get(j);
				if (myNode2.position().distance(myNode1.position()) > _cMaxSpringLength)
					break;
				if (!myNode1._myNeighbors.contains(myNode2) && !myNode2._myNeighbors.contains(myNode1)) {
					float myDistance = myNode1.particle().position().distance(myNode2.particle().position());
					_mySprings.addSpring(myNode1.particle(), myNode2.particle(), myDistance);
				}
				if (!myNode1._myNeighbors.contains(myNode2)) {
					myNode1._myNeighbors.add(myNode2);
				}
				if (!myNode2._myNeighbors.contains(myNode1)) {
					myNode2._myNeighbors.add(myNode1);
				}
			}
		}

	}

	private int _myParticleCounter = 0;

	float _myTime = 0;

	public void update(final float theDeltaTime) {
		CCStopWatch.instance().startWatch("data");
		_myParticles.dataBuffer().getData(0).get(_myData);
//		_myParticles.dataBuffer().getPBOData(0);
		CCStopWatch.instance().endWatch("data");

		CCStopWatch.instance().startWatch("check nodes");
		for (CCNode myCheckedNode : new ArrayList<>(_myNodeSet)) {
			
			int i = myCheckedNode.particle().index() * 4;
			if(myCheckedNode.age() > 0.1)myCheckedNode.position().set(_myData[i], _myData[i+1], _myData[i+2]);
			// _myParticles.position(myCheckedNode.particle(),);
			myCheckedNode.update(theDeltaTime);

			if (myCheckedNode.age() < myCheckedNode.particle().lifeTime())
				continue;
			_myNodeSet.remove(myCheckedNode);
			for (CCNode myNeighbour : myCheckedNode._myNeighbors) {
				myNeighbour._myNeighbors.remove(myCheckedNode);
			}
		}
		CCStopWatch.instance().endWatch("check nodes");
		// System.out.println("myChecks:" + myChecks);

		CCStopWatch.instance().startWatch("create nodes");
		List<CCNode> myNewNodes = new ArrayList<>();
		for (int i = 0; i < _cEmitAmount; i++) {
			CCNode myNode1 = new CCNode(CCMath.random(-_myApp.width / 2, _myApp.width / 2), CCMath.random(CCMath.TWO_PI), _cEmitRadius);
			myNewNodes.add(myNode1);
			myNode1.particle(_myEmitter.emit(_myParticleCounter++, new CCColor(255), myNode1.position(), new CCVector3f(), _cLifeTime, true));
			_myParticleCounter %= _myEmitter.numberOfParticles();
			_myNodeSet.add(myNode1);
		}
		System.out.println(_myParticleCounter);
		CCStopWatch.instance().endWatch("create nodes");

		CCStopWatch.instance().startWatch("createConnections");
		createConnections(myNewNodes);
		CCStopWatch.instance().endWatch("createConnections");

		for (CCNode myNode : _myNodeSet) {
			myNode._myAlpha = 0;
			myNode._myDepth = 0;
			// _myPositionBuffer.position(_myParticle.index() * 4);
			// myNode.particlePosition().set(
			// CCMath.blend(_myPosition.x, _myPositionBuffer.get(),
			// _cPositionBlend),
			// CCMath.blend(_myPosition.y, _myPositionBuffer.get(),
			// _cPositionBlend),
			// CCMath.blend(_myPosition.z, _myPositionBuffer.get(),
			// _cPositionBlend)
			// );
		}

		CCStopWatch.instance().startWatch("update particles");
		_myParticles.update(theDeltaTime);

		_myGravity.strength(_cGravityStrength);

		_myForceField.strength(_cFieldStrength);
		_myForceField.noiseOffset(new CCVector3f(0, 0, _myTime));
		_myForceField.noiseScale(0.0025f);

		_myAttractor.strength(0);
		// _myAttractor.radius(_cAttractorRadius);
		// _myAttractor.position().x = mouseX - width/2;
		// _myAttractor.position().y = height/2 - mouseY;

		_myCurveField.strength(_cCurveStrength);
		_myCurveField.outputScale(_cCurveOuputScale);
		_myCurveField.speed(_cCurveSpeed);
		_myCurveField.scale(_cCurveNoiseScale / 100);
		_myCurveField.radius(_cCurveRadius);

		_myCurveField.prediction(_cPrediction);

		_mySprings.strength(_cSpringStrength);
		_mySprings.springConstant(_cSpringConstant);
		_mySprings.springDamping(_cSpringDamping);

		_myDrag.drag(_cDrag);

		CCStopWatch.instance().endWatch("update particles");
	}

	public void draw(CCGraphics g) {

		g.noDepthTest();

		// _myPointShader.start();
		// g.pointSprite();
		// for(CCNode myNode:_myNodeSet) {
		// float myPointSize = CCMath.blend(_cMinPointSize, _cMaxPointSize,
		// myNode._myAlpha) * _myDrawScale;
		// if(myPointSize < 1)continue;
		// g.pointSize(myPointSize);
		// g.beginShape(CCDrawMode.POINTS);
		// g.color(_myColor.r, _myColor.g, _myColor.b,
		// pointAlpha(myNode.alpha()));
		// g.vertex(
		// myNode._myX,
		// myNode._myRandom,
		// myNode._myAngle
		// );
		// g.endShape();
		// }
		// g.noPointSprite();
		// _myPointShader.end();

		// g.beginShape(CCDrawMode.LINES);
		// for(CCConnection myLine:_myConnections) {
		// g.vertex(
		// myLine._myNode1.position()
		// );
		// g.vertex(
		// myLine._myNode2.position()
		// );
		// }
		// g.endShape();

		g.color(255);
		g.blend();
		// g.pointSprite(_mySpriteTexture);
		// g.smooth();
		g.blend();
		_myParticles.draw(g);

		// _myShader.start();
		// g.color(255);
		// g.beginShape(CCDrawMode.LINES);
		// for(float x = -_myApp.width/2; x < _myApp.width/2;x++) {
		// float myAngle = CCMath.map(x, -_myApp.width/2, _myApp.width/2, 0,
		// CCMath.TWO_PI * 5);
		// g.vertex(x,0,myAngle);
		// g.vertex(x,1,myAngle );
		// }
		// g.endShape();
		// _myShader.end();

		g.color(1f);
		_myParticles.draw(g);
	}
}
