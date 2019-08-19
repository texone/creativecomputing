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
package cc.creativecomputing.demo.simulation.particles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.signal.CCSignal;
import cc.creativecomputing.math.signal.CCSimplexNoise;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.emit.CCParticlesIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCPathFollow;
import cc.creativecomputing.simulation.particles.forces.CCPathFollow.CCPathFollowPath;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;

public class CCPathFollowDemo extends CCGL2Adapter {
	
	@CCProperty(name = "texture alpha", min = 0, max = 1)
	private float _cTextureAlpha = 0;

	@CCProperty(name = "force scale", min = 0, max = 20)
	private float _cForceScale = 0;

	@CCProperty(name = "area force scale", min = 0, max = 1)
	private float _cAreaForceScale = 0;

	@CCProperty(name = "contour force scale", min = 0, max = 1)
	private float _cContourForceScale = 0;
	
	@CCProperty(name = "contourWeight", min = 0, max = 20)
	private float _cContourWeight = 0;
	
	@CCProperty(name = "noiseSpeed", min = 0, max = 1)
	private float _cNoiseSpeed = 0;

	private CCSignal _myNoise;

	private float _myTime = 0;

	@CCProperty(name = "particles")
	private CCParticles _myParticles;
	private CCParticlesIndexParticleEmitter _myEmitter;
	private CCPathFollow _myPathFollow;

	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	private CCPathFollowPath _myPath1;
	private CCPathFollowPath _myPath2;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {


		_myNoise = new CCSimplexNoise();
		g.strokeWeight(10);


		_myPathFollow = new CCPathFollow(400,400);
		final List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(_myPathFollow);
		myForces.add(new CCViscousDrag(0.3f));
		// myForces.add(new CCGPUGravity(new CCVector3(50,0,0)));
		_myParticles = new CCParticles(g, myForces, new ArrayList<>(), new ArrayList<CCConstraint>(), 700, 700);
		_myParticles.addEmitter(_myEmitter = new CCParticlesIndexParticleEmitter(_myParticles));
		// _myParticles.make2D(true);

		_cCameraController = new CCCameraController(this, g, 100);
		
		_myPathFollow.addPath(_myPath1 = new CCPathFollowPath());
		_myPathFollow.addPath(_myPath2 = new CCPathFollowPath());
	}
	
	private void updatePath(CCPathFollowPath thePath, float theY){
		thePath.clear();
		for (int i = 0; i <= 400; i += 4) {
			double myNoise = theY + (_myNoise.value(i * 0.005f, _myTime)-0.5f) * 100;
			thePath.addPoint(new CCVector2(i, myNoise));
		}
		
		thePath.contourForce(_cContourForceScale);
		thePath.areaForce(_cAreaForceScale);
		thePath.contourWeight(_cContourWeight);
	}

	@Override
	public void update(final CCAnimator theAnimator) {
		_myTime += theAnimator.deltaTime() * _cNoiseSpeed;
		for (int i = 0; i < 300; i++) {
			_myEmitter.emit(
				new CCVector3(-300, CCMath.random(-200,200), 0), 
				new CCVector3(CCMath.random(10,20), CCMath.random(-10,10), 0), 30);
		}

		updatePath(_myPath1, 300);
		updatePath(_myPath2, 100);
		_myPathFollow.strength(_cForceScale);
		_myParticles.update(theAnimator);
		
	}

	@Override
	public void display(CCGraphics g) {
		_myParticles.preDisplay(g);
//		g.polygonMode(CCPolygonMode.LINE);
		
		//
		g.strokeWeight(1f);
		_cCameraController.camera().draw(g);
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
		_myParticles.display(g);
	}

	public static void main(String[] args) {

		CCPathFollowDemo demo = new CCPathFollowDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
