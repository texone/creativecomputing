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
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.forces.target.CCGPUTargetForce;
import cc.creativecomputing.simulation.particles.forces.target.CCGPUTargetMaskSetup;
import cc.creativecomputing.simulation.particles.forces.target.CCGPUTargetMaskSetup.CCGPUTargetMaskSetupPlane;
import cc.creativecomputing.util.CCFormatUtil;

public class CCParticlesKeepTargetTest extends CCApp {
	
	@CCControl(name = "target strength", min = 0, max = 10)
	private float _cTargetStrength = 0;
	@CCControl(name = "target lookahead", min = 0, max = 10)
	private float _cLookAhead = 0;
	@CCControl(name = "target max force", min = 0, max = 10)
	private float _cMaxForce = 0;
	
	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCArcball _myArcball;
	
	private CCForceField _myForceField = new CCForceField(0.005f,1,new CCVector3f(100,20,30));
	private CCGPUTargetForce _myTargetForce = new CCGPUTargetForce();
	private CCGPUTargetMaskSetup _myTargetMaskSetup;
	
	private List<CCVector3f> _myEmitters = new ArrayList<CCVector3f>();

	public void setup() {
		_myArcball = new CCArcball(this);
		
		final List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(new CCGravity(new CCVector3f(0,-1,0)));
		myForces.add(new CCViscousDrag(0.3f));
		myForces.add(_myForceField);
		myForces.add(_myTargetForce);
		
		_myParticles = new CCParticles(g, myForces, new ArrayList<CCGPUConstraint>(), 500,500);
		_myEmitter = new CCGPUIndexParticleEmitter(_myParticles);
		
		_myTargetMaskSetup = new CCGPUTargetMaskSetup(CCTextureIO.newTextureData("squarepusher.png"),1,CCGPUTargetMaskSetupPlane.XY );
		_myTargetMaskSetup.keepTargets(true);
		_myTargetForce.addTargetSetup(_myTargetMaskSetup);
		
		for(int i = 0; i < 10;i++) {
			_myEmitters.add(new CCVector3f(CCMath.random(-100,100),400));
		}
		
		addControls("app", "app", this);
	}
	
	private float _myTime = 0;
	
	public void update(final float theDeltaTime){
		for(int i = 0; i < 5000; i++){
			int myIndex = _myEmitter.nextFreeId();
			if(myIndex < 0)break;
			
			CCVector3f myTarget = _myTargetMaskSetup.target(myIndex);
			float myMinDistance = Float.MAX_VALUE;
			CCVector3f myNearestEmitter = _myEmitters.get(0);
			
			for(CCVector3f myEmitter:_myEmitters) {
				float myDistanceSquared = myEmitter.distanceSquared(myTarget);
		
				if(myDistanceSquared < myMinDistance) {
					myNearestEmitter = myEmitter;
					myMinDistance = myDistanceSquared;
				}
			}
			
			_myEmitter.emit(
				myNearestEmitter,
				CCVecMath.random3f(10),
				15, false
			);
			
			
		}
		float myBlend = mouseX / (float)width;
		_myForceField.strength(1 - myBlend);
		_myTargetForce.strength(_cTargetStrength);
		
		_myTargetForce.lookAhead(_cLookAhead);
		_myTargetForce.maxForce(_cMaxForce);
		
		_myTime += theDeltaTime * 0.5f;
		
		_myParticles.update(theDeltaTime);
		
		_myForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
		_myForceField.noiseScale((CCMath.sin(_myTime * 0.5f)+1) * 0.0025f+0.005f);
	}

	public void draw() {
		g.clear();
		g.noDepthTest();
		_myArcball.draw(g);
		g.blend(CCBlendMode.BLEND);
		g.color(255,50);
		_myParticles.renderer().mesh().draw(g);
		g.noTexture();
	}
	
	public void keyPressed(CCKeyEvent theEvent) {
		switch(theEvent.keyChar()) {
		case 's':
			CCScreenCapture.capture("export/target/target"+CCFormatUtil.nf(frameCount, 4) + ".png", width, height);
			break;
		}
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesKeepTargetTest.class);
		myManager.settings().size(800, 800);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
