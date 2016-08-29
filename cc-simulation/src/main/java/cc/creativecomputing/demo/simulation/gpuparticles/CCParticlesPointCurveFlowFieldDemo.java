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
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.signal.CCSimplexNoise;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCAttractor;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.forces.CCGPUPointCurveField;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.util.CCFormatUtil;

public class CCParticlesPointCurveFlowFieldDemo extends CCApp {
	
	@CCControl(name = "noise strength", min = 0, max = 10)
	private float _cFieldStrength = 0;
	
	@CCControl(name = "attractor strength", min = -10, max = 10)
	private float _cAttractorStrength = 0;
	
	@CCControl(name = "attractor radius", min = 0, max = 300)
	private float _cAttractorRadius = 0;
	
	@CCControl(name = "gravity strength", min = 0, max = 1)
	private float _cGravityStrength = 0;
	
	@CCControl(name = "curve strength", min = 0, max = 10)
	private float _cCurveStrength = 0;
	
	@CCControl(name = "noise speed", min = 0, max = 1)
	private float _cCurveSpeed = 0;
	
	@CCControl(name = "curveOutputScale", min = 0, max = 200)
	private float _cCurveOuputScale = 0;

	@CCControl(name = "prediction", min = 0, max = 1)
	private float _cPrediction = 0;
	
	@CCControl(name = "curveRadius", min = 0, max = 400)
	private float _cCurveRadius = 0;
	
	@CCControl(name = "emit radius", min = 0, max = 400)
	private float _cEmitRadius = 0;

	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCArcball _myArcball;
	
	private CCGPUPointCurveField _myCurveField;
	private CCForceField _myForceField = new CCForceField(0.005f,1,new CCVector3f(100,20,30));
	private CCGravity _myGravity = new CCGravity(new CCVector3f(10,0,0));
	private CCAttractor _myAttractor = new CCAttractor(new CCVector3f(), 0, 0);

	private CCSimplexNoise _myNoise;
	
	public void setup() {
		_myArcball = new CCArcball(this);
		
		final List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(new CCViscousDrag(0.3f));
		myForces.add(_myCurveField = new CCGPUPointCurveField(width,g));
		myForces.add(_myGravity);
		myForces.add(_myForceField);
		myForces.add(_myAttractor);
		
		_myParticles = new CCParticles(g, myForces, new ArrayList<CCGPUConstraint>(), 700,700);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		
		addControls("app", "app", this);
		
		_myNoise = new CCSimplexNoise();
		
		addControls("app", "noise", _myNoise);
	}
	
	private float _myTime = 0;
	
	public void update(final float theDeltaTime){
		_myTime += 1/30f * 0.5f;
		for(int i = 0; i < 2000; i++){
			_myEmitter.emit(
				new CCVector3f(CCMath.random(-width/2, width/2),CCMath.random(-_cEmitRadius, _cEmitRadius),CCMath.random(-50, 50)),
				CCVecMath.random3f(10),
				10, false
			);
		}
		_myParticles.update(theDeltaTime);
		
		_myGravity.strength(_cGravityStrength);
		
		_myForceField.strength(_cFieldStrength);
		_myForceField.noiseOffset(new CCVector3f(0,0,_myTime));
		_myForceField.noiseScale(0.0025f);
		
		_myAttractor.strength(_cAttractorStrength);
		_myAttractor.radius(_cAttractorRadius);
		_myAttractor.position().x = mouseX - width/2;
		_myAttractor.position().y = height/2 - mouseY;
		
		_myCurveField.strength(_cCurveStrength);
		_myCurveField.minX(-width / 2);
		_myCurveField.maxX( width / 2);
		_myCurveField.radius(_cCurveRadius);
		_myCurveField.outputScale(_cCurveOuputScale);
		
		_myCurveField.prediction(_cPrediction);
		
		for(int x = 0; x < width;x++){
			_myNoise.values(x);
			_myCurveField.curvePoint(x, new CCVector3f(x - width/2, _myNoise.value(x) - 0.5f, (_myNoise.value(x + 10000)-0.5f)));
		}
	}

	public void draw() {
		g.clear();
		g.noDepthTest();
		g.pushMatrix();
		_myArcball.draw(g);
		g.color(255,50);
		g.blend();
//		g.pointSprite(_mySpriteTexture);
//		g.smooth();
		g.blend();
		_myParticles.renderer().mesh().draw(g);
//		g.noSmooth();
//		g.noPointSprite();

		g.color(255);
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(int i = 0; i < width;i++){
			g.vertex(i - width/2, (_myNoise.value(i) - 0.5f) * 100, (_myNoise.value(i + 10000)-0.5f) * 100);
		}
		g.endShape();
		g.popMatrix();
		g.color(255);
		g.image(_myCurveField.curveTexture(),-width/2,-200);
		g.text(frameRate + ":" + _myEmitter.particlesInUse(),-width/2+20,-height/2+20);
	
	}
	
	private int i = 0;
	
	public void keyPressed(CCKeyEvent theEvent) {
		switch(theEvent.keyCode()){
		case VK_R:
			_myParticles.reset();
			break;
		case VK_S:
			CCScreenCapture.capture("export/db04/"+CCFormatUtil.nf(i++, 4)+".png", width, height);
			break;
		default:
		}
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesPointCurveFlowFieldDemo.class);
		myManager.settings().size(1200, 600);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
