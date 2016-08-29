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
package cc.creativecomputing.demo.simulation.gpuparticles.impulse;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.forces.CCNoiseHeightMapForce;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.impulses.CCGPUImpulse;
import cc.creativecomputing.simulation.particles.impulses.CCGPUSphereImpulse;
import cc.creativecomputing.simulation.particles.render.CCGPUParticlePointRenderer;
import cc.creativecomputing.util.CCFormatUtil;

public class CCSphereImpulseDemo extends CCApp {
	
	@CCControl(name = "gravity", min = 0, max = 3f)
	private float _cGravity = 0.1f;
	
	@CCControl(name = "noise scale", min = 0, max = 0.01f)
	private float _cNoiseScale = 0.005f;
	
	@CCControl(name = "sphere impulse strength", min = 0, max = 10f)
	private float _cSphereImpulseStrength = 0;
	
	@CCControl(name = "sphere impulse y", min = -500, max = 500)
	private float _cSphereImpulseY = 0;
	
	@CCControl(name = "sphere impulse radius", min = 0, max = 500)
	private float _cSphereImpulseRadius = 0;
	
	@CCControl(name = "sphere impulse timer", min = 0, max = 5)
	private float _cSphereImpulseTimer = 0;
	
	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCArcball _myArcball;
	
	private CCGravity _myGravity = new CCGravity(new CCVector3f(0.1,0,0));
	private CCNoiseHeightMapForce _myForceField = new CCNoiseHeightMapForce(0.005f,1,100,new CCVector3f(100,20,30));
	
	private CCGPUSphereImpulse _mySphereImpulse;

	public void setup() {
		addControls("app", "app", this);
		_myArcball = new CCArcball(this);
		
		final List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(_myGravity);
		myForces.add(new CCViscousDrag(0.3f));
		myForces.add(_myForceField);
		
		final List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();
		
		final List<CCGPUImpulse> myImpulses = new ArrayList<CCGPUImpulse>();
		myImpulses.add(_mySphereImpulse = new CCGPUSphereImpulse(new CCVector3f(), 200, 1));
	
		_myParticles = new CCParticles(g,new CCGPUParticlePointRenderer(),myForces,myConstraints,myImpulses,800,800);
		_myEmitter = new CCGPUIndexParticleEmitter(_myParticles);
		g.smooth();
		
		addControls("app", "app", this);
		_myUI.hide();
	}
	
	float angle = 0;
	private float _myTime = 0;
	private float _myTriggerTimer = 0;
	
	public void update(final float theDeltaTime){

		_myTime += 1/30f * 0.5f;
		_myTriggerTimer += theDeltaTime;
		
		_mySphereImpulse.strength(_cSphereImpulseStrength);
		_mySphereImpulse.radius(_cSphereImpulseRadius);
		
		if(_myTriggerTimer > _cSphereImpulseTimer) {
			_myTriggerTimer -= _cSphereImpulseTimer;
			_mySphereImpulse.center(new CCVector3f(CCMath.random(-700,700),_cSphereImpulseY, CCMath.random(-700,700)));
			_mySphereImpulse.trigger();
		}
		
		angle += theDeltaTime * 30;
		for(int i = 0; i < 2500; i++){
			_myEmitter.emit(
				new CCVector3f(CCMath.random(-400,400), 0,CCMath.random(-400,400)),
				new CCVector3f(CCVecMath.random3f(20)),
				10, false
			);
		}
		
		_myGravity.direction().x = _cGravity;
		
		_myForceField.noiseScale(_cNoiseScale);
		_myForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
		_myForceField.noiseScale((CCMath.sin(_myTime * 0.5f)+1) * 0.0025f + 0.005f);
		_myParticles.update(theDeltaTime);
	}

	public void draw() {
		
		g.noDepthTest();
		g.clear();
		g.color(255,50);
		
		g.pushMatrix();
		_myArcball.draw(g);
		g.blend(CCBlendMode.ADD);
		g.color(255,50);
		_myParticles.renderer().mesh().draw(g);
		g.popMatrix();
		
		g.blend();
	}
	
	public void keyPressed(final CCKeyEvent theEvent){
		switch(theEvent.keyChar()) {
		case 's':
			CCScreenCapture.capture("export/heightmap/heightmap"+CCFormatUtil.nf(frameCount, 4) + ".png", width, height);
			break;
		}
	}
	

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCSphereImpulseDemo.class);
		myManager.settings().size(1200, 800);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
