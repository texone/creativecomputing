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
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.forces.blend.CCGPUTimeForceBlend;
import cc.creativecomputing.simulation.particles.forces.target.CCGPUTargetForce;
import cc.creativecomputing.simulation.particles.forces.target.CCGPUTargetMaskSetup;
import cc.creativecomputing.simulation.particles.render.CCGPUPointSpriteRenderer;

public class CCParticlesTimeForceBlendTest extends CCApp {
	
	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCForceField _myNoiseForceField;
	
	private CCGPUTargetForce _myTargetForce = new CCGPUTargetForce();
	private CCGPUTargetMaskSetup _myTargetMaskSetup;
	
	private CCGPUTimeForceBlend _myTimeBlendForce;
	
	@CCControl(name = "target strength", min = 0, max = 1, external = true)
	private float _cTargetStrength = 0;
	@CCControl(name = "target lookahead", min = 0, max = 10)
	private float _cLookAhead = 0;
	@CCControl(name = "target max force", min = 0, max = 10, external = true)
	private float _cMaxForce = 0;
	@CCControl(name = "target near distance", min = 0, max = 200)
	private float _cTargetNearDistance = 0;
	@CCControl(name = "target near max force", min = 0, max = 10)
	private float _cTargetNearMaxForce = 0;
	@CCControl(name = "look up look ahead", min = 0, max = 10)
	private float _cLookUpLookAhead = 0;
	

	private CCTexture2D _myPointSpriteTexture;
	private CCGPUPointSpriteRenderer _myRenderer;
	
	@CCControl(name = "point size", min = 0, max = 50)
	private int _cPointSize = 5;

	@Override
	public void setup() {
		_myNoiseForceField = new CCForceField(0.005f,1,new CCVector3f(100,20,30));
		
		_myPointSpriteTexture = new CCTexture2D(CCTextureIO.newTextureData("golddust.png"));
		_myPointSpriteTexture.generateMipmaps(true);
		_myPointSpriteTexture.textureFilter(CCTextureFilter.LINEAR);
		_myPointSpriteTexture.textureMipmapFilter(CCTextureMipmapFilter.LINEAR);
		
		_myTimeBlendForce = new CCGPUTimeForceBlend(0,10, _myNoiseForceField, _myTargetForce);
		_myTimeBlendForce.blend(0.002f, 1f);
		_myTimeBlendForce.power(2);
		
		final List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(_myTimeBlendForce);
		myForces.add(new CCViscousDrag(0.3f));
		
		_myRenderer = new CCGPUPointSpriteRenderer(g,_myPointSpriteTexture,8, 3);
		_myRenderer.pointSize(3);
		
		_myParticles = new CCParticles(g,_myRenderer,myForces, new ArrayList<CCGPUConstraint>(),300,300);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		
		_myTargetMaskSetup = new CCGPUTargetMaskSetup(CCTextureIO.newTextureData("gold.png"),1);
		_myTargetMaskSetup.keepTargets(true);
		_myTargetForce.addTargetSetup(_myTargetMaskSetup);
		

		addControls("app", "app", this);
	}
	
	private float _myTime = 0;
	
	@Override
	public void update(final float theDeltaTime) {
		for(int i = 0; i < 30 && _myIsEmitting; i++){
			int myIndex = _myEmitter.nextFreeId();
			if(myIndex < 0)break;
			
			CCVector3f myTarget = _myTargetMaskSetup.target(myIndex);
			
			if(_myIsEmittingLine) {
				_myEmitter.emit(
					new CCVector3f(myTarget.x,height/2,0),
					CCVecMath.random3f(10).subtract(-10, -10, 0),
					12, false
				);
			}else {
				_myEmitter.emit(
					new CCVector3f(-500,200),
					CCVecMath.random3f(10).subtract(-10, -10, 0),
					12, false
				);
			}
		}
		
		_myRenderer.pointSize(_cPointSize);
		_myRenderer.fadeOut(false);
		
		_myTime += theDeltaTime;
		_myNoiseForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
		_myNoiseForceField.noiseScale((CCMath.sin(_myTime * 0.5f)+1) * 0.0025f+0.005f);
		_myTargetForce.strength(_cTargetStrength);
		_myTargetForce.lookAhead(_cLookAhead * 100);
		_myTargetForce.maxForce(_cMaxForce);
		_myTargetForce.nearDistance(_cTargetNearDistance);
		_myTargetForce.nearMaxForce(_cTargetNearMaxForce);
		_myParticles.update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clear();
		g.color(255);
		g.noDepthTest();
		g.blend(CCBlendMode.ADD);
		_myParticles.draw();
		
	}
	
	private boolean _myIsEmittingLine = true;
	private boolean _myIsEmitting = true;
	
	@Override
	public void keyPressed(final CCKeyEvent theEvent) {
		switch(theEvent.keyChar()) {
		case 'l':
			_myIsEmittingLine =! _myIsEmittingLine;
			break;
		case 'e':
			_myIsEmitting =! _myIsEmitting;
			break;
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesTimeForceBlendTest.class);
		myManager.settings().size(1200, 600);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

