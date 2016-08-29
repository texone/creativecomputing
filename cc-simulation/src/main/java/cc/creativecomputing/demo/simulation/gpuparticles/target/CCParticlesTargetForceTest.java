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
package cc.creativecomputing.demo.simulation.gpuparticles.target;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.CCVector4f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.forces.target.CCGPUTargetForce;
import cc.creativecomputing.simulation.particles.forces.target.CCGPUTargetPointSetSetup;
import cc.creativecomputing.util.CCFormatUtil;

public class CCParticlesTargetForceTest extends CCApp {
	
	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCArcball _myArcball;
	
	private CCForceField _myForceField = new CCForceField(0.005f,1,new CCVector3f(100,20,30));
	private CCGPUTargetForce _myTargetForce = new CCGPUTargetForce();
	

	public void setup() {
		_myArcball = new CCArcball(this);
		
		final List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(new CCViscousDrag(0.3f));
		myForces.add(_myForceField);
		myForces.add(_myTargetForce);
		
		_myParticles = new CCParticles(g, myForces, new ArrayList<CCGPUConstraint>(), 600,600);
		_myEmitter = new CCGPUIndexParticleEmitter(_myParticles);
		
		CCGPUTargetPointSetSetup myPointSetup = new CCGPUTargetPointSetSetup();
		myPointSetup.points().add(new CCVector4f(1,1,1,1));
		_myTargetForce.addTargetSetup(myPointSetup);
		
		for(int i = 0; i < 600 * 600; i++){
			_myEmitter.emit(
				CCVecMath.random3f(200),
				CCVecMath.random3f(10),
				10, true
			);
		}
	}
	
	private float _myTime = 0;
	
	public void update(final float theDeltaTime){
		
		float myBlend = mouseX / (float)width;
		_myForceField.strength(1 - myBlend);
		_myTargetForce.strength(myBlend);
		
		_myTime += 1/30f * 0.5f;
		
		_myParticles.update(theDeltaTime);
		
		_myForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
		_myForceField.noiseScale((CCMath.sin(_myTime * 0.5f)+1) * 0.0025f+0.005f);
	}

	public void draw() {
		g.clear();
		g.noDepthTest();
		_myArcball.draw(g);
		g.blend();
		g.color(255,50);
		_myParticles.draw();
	}
	
	public void keyPressed(CCKeyEvent theEvent) {
		switch(theEvent.keyChar()) {
		case 's':
			CCScreenCapture.capture("export/target/target"+CCFormatUtil.nf(frameCount, 4) + ".png", width, height);
			break;
		}
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesTargetForceTest.class);
		myManager.settings().size(1024, 800);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
