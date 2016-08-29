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
package cc.creativecomputing.demo.simulation.gpuparticles.blend;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCRenderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.forces.blend.CCGPUTimedTextureForceBlend;
import cc.creativecomputing.simulation.particles.forces.target.CCGPUTargetForce;
import cc.creativecomputing.simulation.particles.forces.target.CCGPUTargetMaskSetup;

public class CCParticlesTimedTextureBlendTest extends CCApp {
	
	@CCControl(name = "blend back", min = 0, max = 1f)
	private float _cBlendBack = 0.5f;
	
	@CCControl(name = "blend", min = 0, max = 1f)
	private float _cBlend = 0.5f;
	
	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCForceField _myNoiseForceField;
	
	private CCGPUTargetForce _myTargetForce = new CCGPUTargetForce();
	private CCGPUTargetMaskSetup _myTargetMaskSetup;
	
	private CCGPUTimedTextureForceBlend _myTimeBlendForce;
	
	private CCRenderBuffer _myFadeTexture;

	@Override
	public void setup() {

		addControls("app","app", this);
		_myFadeTexture = new CCRenderBuffer(g, CCTextureTarget.TEXTURE_RECT, 600, 300);
		_myNoiseForceField = new CCForceField(0.005f,1,new CCVector3f(100,20,30));

		_myTimeBlendForce = new CCGPUTimedTextureForceBlend(
			_myFadeTexture.attachment(0),
			new CCVector2f(2,2),
			new CCVector2f(300,150), 
			_myNoiseForceField, 
			_myTargetForce
		);
		addControls("forces","time texture blend", _myTimeBlendForce);
		
		final List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(_myTimeBlendForce);
		myForces.add(new CCViscousDrag(0.3f));
		
		_myParticles = new CCParticles(g,myForces, new ArrayList<CCGPUConstraint>(),600,600);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		
		_myTargetMaskSetup = new CCGPUTargetMaskSetup(CCTextureIO.newTextureData("texone2.png"),4);
		_myTargetMaskSetup.keepTargets(true);
		_myTargetForce.addTargetSetup(_myTargetMaskSetup);
		
		_myUI.drawBackground(false);
	}
	
	private float _myTime = 0;
	
	@Override
	public void update(final float theDeltaTime) {
		_myFadeTexture.beginDraw();
		g.clearColor(_cBlendBack);
		g.clear();
		g.color(_cBlend);
		g.ellipse(0, 0, 200);
		_myFadeTexture.endDraw();
		
		for(int i = 0; i < 1000; i++){
			int myIndex = _myEmitter.nextFreeId();
			if(myIndex < 0)break;
			
			_myEmitter.emit(
				CCVecMath.random(-width/2, width/2, -height/2, height/2, 0, 0),
				CCVecMath.random3f(10).subtract(-10, -10, 0),
				5, false
			);
		}
		
		_myTime += theDeltaTime;
		_myNoiseForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
		_myNoiseForceField.noiseScale((CCMath.sin(_myTime * 0.5f)+1) * 0.0025f+0.005f);
		
		_myParticles.update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clearColor(0);
		g.clear();
		g.color(255);
		g.blend(CCBlendMode.BLEND);
		_myParticles.draw();
//		g.image(_myTimeBlendForce.blendTexture(), 0,0);
//		g.image(_myFadeTexture, 0,-300);
	}
	
	private boolean _myIsEmittingLine = true;
	
	@Override
	public void keyPressed(final CCKeyEvent theEvent) {
		switch(theEvent.keyChar()) {
		case 'l':
			_myIsEmittingLine =! _myIsEmittingLine;
			break;
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesTimedTextureBlendTest.class);
		myManager.settings().size(1200, 600);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

