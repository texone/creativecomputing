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
package cc.creativecomputing.demo.simulation.gpuparticles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.signal.CCSignal;
import cc.creativecomputing.math.signal.CCSimplexNoise;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCGPUPathFollow;
import cc.creativecomputing.simulation.particles.forces.CCGPUPathFollow.CCGPUPath;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;

public class CCPathFollowTest extends CCApp {
	
	@CCControl(name = "texture alpha", min = 0, max = 1)
	private float _cTextureAlpha = 0;

	@CCControl(name = "force scale", min = 0, max = 20)
	private float _cForceScale = 0;

	@CCControl(name = "area force scale", min = 0, max = 1)
	private float _cAreaForceScale = 0;

	@CCControl(name = "contour force scale", min = 0, max = 1)
	private float _cContourForceScale = 0;
	
	@CCControl(name = "contourWeight", min = 0, max = 20)
	private float _cContourWeight = 0;
	
	@CCControl(name = "noiseSpeed", min = 0, max = 1)
	private float _cNoiseSpeed = 0;

	private CCSignal _myNoise;

	private float _myTime = 0;

	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCGPUPathFollow _myPathFollow;

	private CCArcball _myArcball;
	
	private CCGPUPath _myPath1;
	private CCGPUPath _myPath2;

	@Override
	public void setup() {
		addControls("app", "app", this);
		// frameRate(30);


		_myNoise = new CCSimplexNoise();
		g.strokeWeight(10);


		_myPathFollow = new CCGPUPathFollow(g, 400,400);
		final List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(_myPathFollow);
		myForces.add(new CCViscousDrag(0.3f));
		// myForces.add(new CCGPUGravity(new CCVector3f(50,0,0)));
		_myParticles = new CCParticles(g, myForces, new ArrayList<CCGPUConstraint>(), 700, 700);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		// _myParticles.make2D(true);

		_myArcball = new CCArcball(this);
		
		_myPathFollow.addPath(_myPath1 = new CCGPUPath());
		_myPathFollow.addPath(_myPath2 = new CCGPUPath());
	}
	
	private void updatePath(CCGPUPath thePath, float theY){
		thePath.clear();
		for (int i = 0; i <= 400; i += 4) {
			float myNoise = theY + (_myNoise.value(i * 0.005f, _myTime)-0.5f) * 100;
			thePath.addPoint(new CCVector2f(i, myNoise));
		}
		
		thePath.contourForce(_cContourForceScale);
		thePath.areaForce(_cAreaForceScale);
		thePath.contourWeight(_cContourWeight);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myTime += theDeltaTime * _cNoiseSpeed;
		for (int i = 0; i < 300; i++) {
			_myEmitter.emit(
				new CCVector3f(-300, CCMath.random(-200,200), 0), 
				new CCVector3f(CCMath.random(10,20), CCMath.random(-10,10), 0), 30);
		}

		updatePath(_myPath1, 300);
		updatePath(_myPath2, 100);
		_myPathFollow.strength(_cForceScale);
		_myParticles.update(theDeltaTime);
		
	}

	@Override
	public void draw() {
//		g.polygonMode(CCPolygonMode.LINE);
		
		//
		g.strokeWeight(1f);
		_myArcball.draw(g);
		g.clearColor(0);
		g.clear();
		g.blend();
		g.color(0, 25);
		// g.clearDepthBuffer();
		// g.rect(-1000,-1000,2000,2000);
		g.color(1f, _cTextureAlpha);
		g.polygonMode(CCPolygonMode.FILL);
		g.image(_myPathFollow.texture(), -400, -400,800,800);
		g.color(255, 50);
		// g.noBlend();
		
		g.noDepthTest();
		g.blend(CCBlendMode.ADD);
		_myParticles.draw();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCPathFollowTest.class);
		myManager.settings().size(1200, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
