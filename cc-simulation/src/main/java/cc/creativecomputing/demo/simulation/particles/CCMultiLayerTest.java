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
package cc.creativecomputing.demo.simulation.particles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCGPUParticleSuperEmitter;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.forces.target.CCGPUTargetCalculator;
import cc.creativecomputing.simulation.particles.forces.target.CCGPUTargetForce;
import cc.creativecomputing.util.CCFormatUtil;

public class CCMultiLayerTest extends CCApp {
	
	private CCParticles _myParticles;
	private CCGPUParticleSuperEmitter _myEmitter;
	private CCArcball _myArcball;
	private static final int SIZE = 1000;
	
	private CCTexture2D _myEmitTexture;
	private CCTexture2D _myTargetTexture;
	
	private CCGPUTargetForce _myTargetForce;
	private CCShaderBuffer _myTargetBuffer;
	private CCGPUTargetCalculator _myTargetCalculator;
	
	@CCControl(min=0, max=1)
	public float _cTargetStrength = 0;
	
	@CCControl(min=0, max=100)
	public float _cLookAhead = 0;
	
	@CCControl(min=0, max=10)
	public float _cMaxForce = 0;
	
	
	public void setup() {
		_myArcball = new CCArcball(this);
		
		_myEmitTexture = new CCTexture2D(CCTextureIO.newTextureData("texone2.png"), CCTextureTarget.TEXTURE_RECT);
		_myEmitTexture.mustFlipVertically(true);
		
		_myTargetTexture = new CCTexture2D(CCTextureIO.newTextureData("texone3.png"), CCTextureTarget.TEXTURE_RECT);
		_myTargetTexture.mustFlipVertically(true);
		
		
		_myTargetBuffer = new CCShaderBuffer(32, 3, 2, SIZE, SIZE);
		_myTargetBuffer.clear();
		
	
		_myTargetForce = new CCGPUTargetForce();
		
		
		final List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(new CCGravity(new CCVector3f(0,0,0)));
		myForces.add(_myTargetForce);
	
		final List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();

		_myParticles = new CCParticles(g, myForces, myConstraints, SIZE, SIZE);
		_myParticles.addEmitter(_myEmitter = new CCGPUParticleSuperEmitter (_myParticles, 0, SIZE*SIZE));
	
		_myTargetCalculator = new CCGPUTargetCalculator (_myParticles, _myTargetBuffer, _myTargetTexture);
		_myTargetForce.addTargetSetup (_myTargetBuffer);
		
		_myEmitter.setEmitTextures (_myEmitTexture, _myEmitTexture);
		
		g.smooth();
		
		addControls ("app", "app", this);
		addControls ("emit","emit", _myEmitter);
	}
	
	public void update(final float theDeltaTime){
		_myTargetForce.strength(_cTargetStrength);
		_myTargetForce.lookAhead(_cLookAhead);
		_myTargetForce.maxForce(_cMaxForce);
		_myParticles.update(theDeltaTime);
		_myTargetCalculator.update(theDeltaTime);
	}

	public void draw() {
		
		
		
		g.clear();
		g.pushAttribute();
		g.clearColor(0);
		g.blend();
		g.color(255, 80);
		g.pushMatrix();
		_myArcball.draw(g);
		//g.blend(CCBlendMode.ADD);
		_myParticles.draw();
		g.popMatrix();
		g.popAttribute();
	}
	
	public void keyPressed(final CCKeyEvent theEvent){
		switch(theEvent.keyChar()) {
		case 's':
			CCScreenCapture.capture("export/heightmap/heightmap"+CCFormatUtil.nf(frameCount, 4) + ".png", width, height);
			break;
		}
	}
	

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCMultiLayerTest.class);
		myManager.settings().size(1200, 800);
		myManager.settings().antialiasing(4);
		myManager.start();
	}
}
