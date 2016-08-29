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
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.forces.blend.CCGPUTextureForceBlend;

public class CCParticlesTextureForceBlendTest extends CCApp {
	
	public static class ForceFieldSettings{
		
		@CCControl(name = "scale x", min = 0.1f, max = 10)
		public float scaleX = 0;
		
		@CCControl(name = "scale y", min = 0.1f, max = 10)
		public float scaleY = 0;
		
		@CCControl(name = "offset x", min = -1000, max = 1000)
		public float offsetX = 0;

		@CCControl(name = "offset y", min = -1000, max = 1000)
		public float offsetY = 0;
		
		@CCControl(name = "force scale", min = 0, max = 20)
		public float forceScale = 0;
	}
	
	@CCControl(name = "texture force field")
	private ForceFieldSettings _cForceFieldSettings = new ForceFieldSettings();
	
	public static class ForceBlendSettings{
		
		@CCControl(name = "scale x", min = 0.1f, max = 10)
		public float scaleX = 0;
		
		@CCControl(name = "scale y", min = 0.1f, max = 10)
		public float scaleY = 0;
		
		@CCControl(name = "offset x", min = -1000, max = 1000)
		public float offsetX = 0;

		@CCControl(name = "offset y", min = -1000, max = 1000)
		public float offsetY = 0;
		
		@CCControl(name = "force scale", min = 0, max = 20)
		public float forceScale = 0;
	}
	
	@CCControl(name = "texture force blend")
	private ForceBlendSettings _cForceBlendSettings = new ForceBlendSettings();
	
	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCForceField _myNoiseForceField;
	
	private CCGPUTextureForceBlend _myForceBlend1;

	@Override
	public void setup() {
		_myNoiseForceField = new CCForceField(0.005f,1,new CCVector3f(100,20,30));
		
		CCTexture2D myBlendTexture = new CCTexture2D(CCTextureIO.newTextureData("texone.png"), CCTextureTarget.TEXTURE_RECT);
		_myForceBlend1 = new CCGPUTextureForceBlend(myBlendTexture, new CCGravity(new CCVector3f(0,0,0)), _myNoiseForceField);
		
		
		final List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(_myForceBlend1);
		myForces.add(new CCViscousDrag(0.3f));
		
		_myParticles = new CCParticles(g,myForces, new ArrayList<CCGPUConstraint>(),300,300);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		
		addControls("app", "app", this);
	}
	
	private float _myTime = 0;
	
	@Override
	public void update(final float theDeltaTime) {
		for(int i = 0; i < 100;i++) {
			_myEmitter.emit(
				new CCVector3f(CCMath.random(-width/2, width/2),CCMath.random(-height/2, height/2),0),
				new CCVector3f(), 
				5
			);
		}
		
		_myTime += theDeltaTime * 0.1f;
		_myNoiseForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
		_myNoiseForceField.noiseScale((CCMath.sin(_myTime * 0.5f)+1) * 0.0025f+0.005f);
		
		_myForceBlend1.textureScale().set(_cForceBlendSettings.scaleX, _cForceBlendSettings.scaleY);
		_myForceBlend1.textureOffset().set(_cForceBlendSettings.offsetX, _cForceBlendSettings.offsetY);
		
		_myParticles.update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clear();
//		g.image(_myForceFieldTexture, -width/2, -height/2);
		_myParticles.draw();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesTextureForceBlendTest.class);
		myManager.settings().size(600, 300);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

