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
package cc.creativecomputing.demo.simulation.gpuparticles.springs;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCGPUParticle;
import cc.creativecomputing.simulation.particles.CCGPUParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCGPUAttractor;
import cc.creativecomputing.simulation.particles.forces.CCGPUNoiseCurveField;
import cc.creativecomputing.simulation.particles.forces.CCGPUForce;
import cc.creativecomputing.simulation.particles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.particles.forces.CCGPUGravity;
import cc.creativecomputing.simulation.particles.forces.CCGPUViscousDrag;
import cc.creativecomputing.simulation.particles.forces.springs.CCGPUDampedSprings;
import cc.creativecomputing.simulation.particles.forces.springs.CCGPUSprings;
import cc.creativecomputing.simulation.particles.render.CCParticlePointRenderer;
import cc.creativecomputing.simulation.particles.render.CCGPUSpringRenderer;
import cc.creativecomputing.util.CCFormatUtil;

public class CCCurveFlowFieldSpringEmitDemo extends CCApp {
	
	@CCControl(name = "spring strength", min = 0, max = 4f)
	private float _cSpringStrength = 0;
	
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

	@CCControl(name = "prediction", min = 0, max = 1)
	private float _cPrediction = 0;
	
	@CCControl(name = "curveNoiseScale", min = 0, max = 1)
	private float _cCurveNoiseScale = 0;
	
	@CCControl(name = "curveOutputScale", min = 0, max = 200)
	private float _cCurveOuputScale = 0;
	
	@CCControl(name = "curveRadius", min = 0, max = 400)
	private float _cCurveRadius = 0;
	
	@CCControl(name = "emit radius", min = 0, max = 400)
	private float _cEmitRadius = 0;

	private CCGPUParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCArcball _myArcball;
	
	private CCGPUNoiseCurveField _myCurveField = new CCGPUNoiseCurveField();
	private CCGPUForceField _myForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));
	private CCGPUGravity _myGravity = new CCGPUGravity(new CCVector3f(10,0,0));
	private CCGPUAttractor _myAttractor = new CCGPUAttractor(new CCVector3f(), 0, 0);
	
	private CCGPUSpringRenderer _myRenderer;
	private CCParticlePointRenderer _myPointRenderer;
	private CCGPUSprings _mySprings;
	
	private int _myNumberOfTrails = 1000;
	private int _myParticlesPerTrail = 20;
	private int _myRows = 10;

	public void setup() {
		_myArcball = new CCArcball(this);
		
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(new CCGPUViscousDrag(0.3f));
		myForces.add(_myCurveField);
		myForces.add(_myGravity);
		myForces.add(_myForceField);
		myForces.add(_myAttractor);
		_mySprings = new CCGPUDampedSprings(g,4,1f,0.1f,0.1f);	
		myForces.add(_mySprings);
		
		_myRenderer = new CCGPUSpringRenderer(_mySprings);
		_myPointRenderer = new CCParticlePointRenderer();
		_myParticles = new CCGPUParticles(g, _myRenderer, myForces, new ArrayList<CCGPUConstraint>(), _myNumberOfTrails,_myParticlesPerTrail * _myRows);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		
		_myPointRenderer.setup(_myParticles);
		
		addControls("app", "app", this);
		addControls("app", "point",1, _myPointRenderer);
	}
	
	private float _myTime = 0;

	private int _myParticleID = 0;
	
	public void update(final float theDeltaTime){
		_myTime += 1/30f * 0.5f;
		
		for(int bla = 0; bla < 1;bla++){
			// setup leading particle pulled by the anchored spring
			float myX = -width/2;//CCMath.random(-width/2, width/2);
			float myY = 0;
			float myZ = 0;
			
			CCGPUParticle myParticle = null;
			CCGPUParticle myFirstParticle = null;
			
			// setup trail
			for(int i = 0; i < _myParticlesPerTrail; i++){
				float myAngle = CCMath.map(i, 0, _myParticlesPerTrail - 1, 0, CCMath.TWO_PI);
				float myAngleY = CCMath.sin(myAngle) * _cEmitRadius;
				float myAngleZ = CCMath.cos(myAngle) * _cEmitRadius;
				CCGPUParticle myNewParticle = _myEmitter.emit(
					_myParticleID++,
					CCColor.WHITE,
					new CCVector3f(myX,myY+myAngleY, myZ + myAngleZ),
					new CCVector3f(),
					30, false
				);
				if(myParticle != null){
					float myDistance = myNewParticle.position().distance(myParticle.position());
					_mySprings.addSpring(myNewParticle, myParticle, myDistance, true);
				}
				else myFirstParticle = myNewParticle;
				myParticle = myNewParticle;
			}
			float myDistance = myFirstParticle.position().distance(myParticle.position());
			_mySprings.addSpring(myFirstParticle, myParticle, myDistance, true);
			}
			_myParticleID %= _myParticles.size();
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
		_myCurveField.outputScale(_cCurveOuputScale);
		_myCurveField.speed(_cCurveSpeed);
		_myCurveField.scale(_cCurveNoiseScale / 100);
		_myCurveField.radius(_cCurveRadius);
		
		_myCurveField.prediction(_cPrediction);
		
		_mySprings.strength(_cSpringStrength);
		
		_myPointRenderer.pointSize(7);
		_myPointRenderer.update(theDeltaTime);
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
		_myParticles.draw();
//		_myPointRenderer.draw(g);
//		g.noSmooth();
//		g.noPointSprite();
		g.popMatrix();
		g.color(255);
		g.text(frameRate + ":" + _myEmitter.particlesInUse(),-width/2+20,-height/2+20);
	}
	
	private int i = 0;
	
	public void keyPressed(CCKeyEvent theEvent) {
		switch(theEvent.keyCode()){
		case VK_R:
			_myParticles.reset(null);
			break;
		case VK_S:
			CCScreenCapture.capture("export/db04/"+CCFormatUtil.nf(i++, 4)+".png", width, height);
			break;
		default:
		}
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCCurveFlowFieldSpringEmitDemo.class);
		myManager.settings().size(1900, 600);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
