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
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCGPUParticleShaderEmitter;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.render.CCGPUIndexedParticleRenderer;
import cc.creativecomputing.util.logging.CCLog;

public class CCShaderEmitDemo extends CCApp {
	
	@CCControl(name = "life time", min = 0, max = 10)
	private float _cLifeTime = 3f;
	
	@CCControl(name = "init vel", min = 0, max = 20)
	private float _cInitVel = 3f;
	
	@CCControl(name = "gx", min = -1, max = 1)
	private float _cX = 0;
	
	@CCControl(name = "gy", min = -1, max = 1)
	private float _cY = 0;
	
	@CCControl(name = "gz", min = -1, max = 1)
	private float _cZ = 0;
	
	@CCControl(name = "g strength", min = 0, max = 1)
	private float _cGStrength = 0;
	
	@CCControl(name = "n scale", min = 0, max = 1)
	private float _cNScale = 0;
	
	@CCControl(name = "n strength", min = 0, max = 1)
	private float _cNStrength = 0;
	
	@CCControl(name = "emit prob", min = 0, max = 1)
	private float _cEmitProb = 0;

	private CCArcball _myArcball;
	
	private CCParticles _myParticles;
	private CCGPUParticleShaderEmitter _myEmitter;
	private CCGravity _myGravity;
	private CCForceField _myForceField;

	public void setup() {
		
		CCLog.info(g.camera().near() +":" + g.camera().far());
		
		_myArcball = new CCArcball(this);
		
		List<CCForce> myCombindedForces = new ArrayList<CCForce>();
		myCombindedForces.add(_myGravity = new CCGravity(new CCVector3f()));
		myCombindedForces.add(_myForceField = new CCForceField(0.01f, 1f, new CCVector3f()));
		
		List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(new CCViscousDrag(0.2f));
		myForces.add(_myGravity = new CCGravity(new CCVector3f()));
		myForces.add(_myForceField = new CCForceField(0.01f, 1f, new CCVector3f()));
		
		List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();
		
		_myParticles = new CCParticles(g, new CCGPUIndexedParticleRenderer(), myForces, myConstraints, 800, 800);
		_myParticles.addEmitter(_myEmitter = new CCGPUParticleShaderEmitter(_myParticles));
		
		addControls("app", "app", this);
	}
	
	public void update(final float theDeltaTime) {
		_myGravity.direction().set(_cX, _cY,_cZ);
		_myGravity.strength(_cGStrength);
		
		_myForceField.noiseScale(_cNScale);
		_myForceField.strength(_cNStrength);
		
		_myEmitter.emitPropability(_cEmitProb);
		_myParticles.update(theDeltaTime);
	}

	public void draw() {
		g.clear();
		

		g.clearColor(0, 255);
		g.clear();
		g.color(255);
		
		g.pushMatrix();
		_myArcball.draw(g);
		g.noDepthTest();
		g.color(255, 50);
		g.blend(CCBlendMode.ADD);
		_myParticles.draw();
		g.popMatrix();
		g.noBlend();
		
		
		
		
//		_myRenderContext.renderTexture().bindDepthTexture();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCShaderEmitDemo.class);
		myManager.settings().size(1500, 900);
		myManager.settings().antialiasing(8);
//		myManager.settings().frameRate(5);
		myManager.start();
	}
}
