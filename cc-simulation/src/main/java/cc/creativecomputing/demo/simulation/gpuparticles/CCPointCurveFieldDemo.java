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

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.signal.CCSimplexNoise;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCAttractor;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.forces.CCPointCurveField;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;

public class CCPointCurveFieldDemo extends CCGL2Adapter {
	
	@CCProperty(name = "noise speed", min = 0, max = 1)
	private float _cCurveSpeed = 0;
	
	@CCProperty(name = "emit radius", min = 0, max = 400)
	private float _cEmitRadius = 0;

	@CCProperty(name = "particles")
	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	private CCPointCurveField _myCurveField;
	private CCForceField _myForceField = new CCForceField();
	private CCGravity _myGravity = new CCGravity(new CCVector3(10,0,0));
	private CCAttractor _myAttractor = new CCAttractor(new CCVector3(), 0);

	@CCProperty(name = "noise")
	private CCSimplexNoise _myNoise;
	
	private int width = 100;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		
		final List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(new CCViscousDrag(0.3f));
		myForces.add(_myCurveField = new CCPointCurveField(g.width()));
		myForces.add(_myGravity);
		myForces.add(_myForceField);
		myForces.add(_myAttractor);
		
		_myParticles = new CCParticles(g, myForces, new ArrayList<CCGPUConstraint>(), 700,700);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		
		_myNoise = new CCSimplexNoise();
		
		_cCameraController = new CCCameraController(this, g, 100);
		
		mouseMoved().add(theMouseEvent -> {
			_myAttractor.position().x = theMouseEvent.x() - g.width()/2;
			_myAttractor.position().y = g.height()/2 - theMouseEvent.y();
		});
		
		width = g.width();
	}
	
	private float _myTime = 0;
	
	@Override
	public void update(final CCAnimator theAnimator){
		_myTime += 1/30f * 0.5f;
		for(int i = 0; i < 2000; i++){
			double myX = CCMath.random(-width/2, width/2);
			_myEmitter.emit(
				new CCVector3(
					myX, 
					(_myNoise.value(myX) - 0.5f) * _myCurveField.outputScale(), 
					(_myNoise.value(myX + 10000)-0.5f) * _myCurveField.outputScale()
				),
				new CCVector3().randomize(10),
				10, false
			);
		}
		_myParticles.update(theAnimator);
		
		_myForceField.offset(new CCVector3(0,0,_myTime));
	
		_myCurveField.minX(-width / 2);
		_myCurveField.maxX( width / 2);
		
		
		for(int x = 0; x < width;x++){
			double myX = x - width/2;
			_myCurveField.curvePoint(x, new CCVector3(myX, _myNoise.value(myX) - 0.5f, (_myNoise.value(myX + 10000)-0.5f)));
		}
	}

	public void display(CCGraphics g) {
		g.clear();
		g.noDepthTest();
		g.pushMatrix();
		_cCameraController.camera().draw(g);
		g.color(255,50);
		g.blend();
		g.blend();
		_myParticles.display(g);

		g.color(255);
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(int x = 0; x < g.width();x++){
			double myX = x - width/2;
			g.vertex(
				myX, 
				(_myNoise.value(myX) - 0.5f) * _myCurveField.outputScale(), 
				(_myNoise.value(myX + 10000)-0.5f) * _myCurveField.outputScale()
			);
		}
		g.endShape();
		g.popMatrix();
		g.color(255);
//		g.image(_myCurveField.curveTexture(),-g.width()/2,-200);
	
	}
	
	
//	public void keyPressed(CCKeyEvent theEvent) {
//		switch(theEvent.keyCode()){
//		case VK_R:
//			_myParticles.reset(g);
//			break;
//		default:
//		}
//	}
	
	public static void main(String[] args) {

		CCPointCurveFieldDemo demo = new CCPointCurveFieldDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
