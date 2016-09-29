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
package cc.creativecomputing.demo.simulation.gpuparticles.emit;

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
import cc.creativecomputing.simulation.particles.constraints.CCGPUYConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.util.CCFormatUtil;

public class CCEmitFountainDemo extends CCApp {
	
	@CCControl(name = "life time", min = 0, max = 10)
	private float _cLifeTime = 3f;
	
	@CCControl(name = "emit amount", min = 0, max = 1000)
	private float _cEmit = 3f;
	
	@CCControl(name = "init vel", min = 0, max = 1000)
	private float _cInitVel = 3f;
	
	@CCControl(name = "random vel", min = 0, max = 10)
	private float _cRandomVel = 3f;
	
	@CCControl(name = "random pos", min = 0, max = 10)
	private float _cRandomPos = 3f;
	
	@CCControl(name = "gx", min = -1, max = 1)
	private float _cX = 0;
	
	@CCControl(name = "gy", min = -1, max = 1)
	private float _cY = 0;
	
	@CCControl(name = "gz", min = -1, max = 1)
	private float _cZ = 0;
	
	@CCControl(name = "g strength", min = 0, max = 1)
	private float _cGStrength = 0;
	
	@CCControl(name = "alpha", min = 0, max = 1)
	private float _cAlpha = 0;
	
	private CCForceField _myForceField;
	
	@CCControl(name = "n scale", min = 0, max = 1)
	private float _cNScale = 0;
	
	@CCControl(name = "n strength", min = 0, max = 1)
	private float _cNStrength = 0;
	
	@CCControl(name = "n speed", min = 0, max = 3)
	private float _cNSpeed = 0;
	
	private CCArcball _myArcball;
	

	
	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCGravity _myGravity;

	@Override
	public void setup() {
		List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(_myGravity = new CCGravity(new CCVector3f()));
		myForces.add(_myForceField = new CCForceField(0.01f, 1f, new CCVector3f()));
//		myForces.add(new CCGPUViscousDrag(0.2f));
		
		
		List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();
		myConstraints.add(new CCGPUYConstraint(-400, 1.0f, 0f, 0.1f));
		
		_myParticles = new CCParticles(g, myForces, myConstraints, 700, 700);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		
		addControls("app", "app", this);
		
		_myArcball = new CCArcball(this);
	}
	
	float _myOffset = 0;

	@Override
	public void update(final float theDeltaTime) {
		for(int i = 0; i < _cEmit;i++){
			_myEmitter.emit(new CCVector3f(CCMath.random(-200,200), -300, 0).add(CCVecMath.random3f(CCMath.random(_cRandomPos))), new CCVector3f(0,_cInitVel,0).add(CCVecMath.random3f(CCMath.random(_cRandomVel))), _cLifeTime);
		}
		
		_myOffset += theDeltaTime * _cNSpeed;
		
		_myGravity.direction().set(_cX, _cY,_cZ);
		_myGravity.strength(_cGStrength);
		
		_myForceField.noiseScale(_cNScale);
		_myForceField.strength(_cNStrength);
		_myForceField.noiseOffset(new CCVector3f(0,0,_myOffset));
		
		_myParticles.update(theDeltaTime * 2);
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		g.noDepthTest();
		g.color(1f, _cAlpha);
		g.blend(CCBlendMode.ADD);
		_myParticles.draw();
		
		g.noBlend();
//		if(frameCount % 100 == 0)CCScreenCapture.capture("export/db02/"+CCFormatUtil.nf(i++, 4)+".png", width, height);
		
	}
	
	private int i = 0;
	
	public void keyPressed(CCKeyEvent theEvent) {
		switch(theEvent.keyCode()){
		case VK_R:
			_myParticles.reset();
			break;
		case VK_S:
			CCScreenCapture.capture("export/db02/"+CCFormatUtil.nf(i++, 4)+".png", width, height);
			break;
		default:
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCEmitFountainDemo.class);
		myManager.settings().size(1200, 800);
//		myManager.settings().displayMode(CCDisplayMode.OFFSCREEN);
		myManager.start();
	}
}

