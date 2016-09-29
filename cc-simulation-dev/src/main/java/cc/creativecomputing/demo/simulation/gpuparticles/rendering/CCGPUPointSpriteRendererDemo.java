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
package cc.creativecomputing.demo.simulation.gpuparticles.rendering;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.graphics.texture.CCTexture2D;
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
import cc.creativecomputing.simulation.particles.render.CCGPUPointSpriteRenderer;
import cc.creativecomputing.util.CCFormatUtil;

public class CCGPUPointSpriteRendererDemo extends CCApp {
	
	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCArcball _myArcball;
	
	private CCForceField _myForceField = new CCForceField(0.005f,1,new CCVector3f(100,20,30));
	private CCGravity _myGravity;
	
	private CCTexture2D _myPointSpriteTexture;
	private CCGPUPointSpriteRenderer _myRenderer;
	
	@CCControl(name = "emit particles", min = 0, max = 500)
	private int _cEmitParticles = 50;
	
	@CCControl(name = "point size", min = 0, max = 50)
	private int _cPointSize = 5;
	
	@CCControl(name = "gravity", min = 0, max = 10)
	private float _cGravity = 0;
	
	@CCControl(name = "force field", min = 0, max = 10)
	private float _cForceField = 0;

	public void setup() {
		_myArcball = new CCArcball(this);
		_myPointSpriteTexture = new CCTexture2D(CCTextureIO.newTextureData("golddust.png"));
		_myPointSpriteTexture.generateMipmaps(true);
		_myPointSpriteTexture.textureFilter(CCTextureFilter.LINEAR);
		_myPointSpriteTexture.textureMipmapFilter(CCTextureMipmapFilter.LINEAR);
		
		final List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(_myGravity = new CCGravity(new CCVector3f(2,0,0)));
		myForces.add(new CCViscousDrag(0.3f));
		myForces.add(_myForceField);
		
		_myRenderer = new CCGPUPointSpriteRenderer(g,_myPointSpriteTexture,8, 3);
		_myRenderer.pointSize(3);
		_myParticles = new CCParticles(g, _myRenderer, myForces, new ArrayList<CCGPUConstraint>(), 500,500);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		
		addControls("app", "app", this);
	}
	
	private float _myTime = 0;
	
	private boolean _myPause = false;
	
	public void update(final float theDeltaTime){
		if(_myPause)return;
		
		_myTime += 1/30f * 0.5f;
		_myParticles.update(theDeltaTime);
		for(int i = 0; i < _cEmitParticles; i++){
			_myEmitter.emit(
//				new CCVector3f(),
				new CCVector3f(-width/2,CCMath.random(-height/2, height/2),0),
				CCVecMath.random3f(10),
				10, false
			);
		}
		
		_myForceField.noiseOffset(new CCVector3f(0,0,_myTime));
		_myForceField.noiseScale(0.0025f);
		_myForceField.strength(_cForceField);
		
		_myGravity.strength(_cGravity);
		
		_myRenderer.pointSize(_cPointSize);
		_myRenderer.fadeOut(false);
	}

	public void draw() {
		g.clearColor(0);
		g.clear();
		g.color(255);
		g.noDepthTest();
		g.pushMatrix();
		_myArcball.draw(g);
		g.color(255);
		g.blend(CCBlendMode.ALPHA);
		g.noDepthTest();
		g.blendMode(CCBlendMode.ADD);
		_myParticles.draw();
		g.popMatrix();
		g.color(255);
		g.text(frameRate + ":" + _myEmitter.particlesInUse(),-width/2+20,-height/2+20);
		
		
	}
	
	private int i = 0;
	
	public void keyPressed(CCKeyEvent theEvent) {
		switch(theEvent.keyCode()){
		case VK_R:
			_myParticles.reset();
			break;
		case VK_P:
			_myPause = !_myPause;
			break;
		case VK_S:
			CCScreenCapture.capture("export/db01/"+CCFormatUtil.nf(i++, 4)+".png", width, height);
			break;
		default:
		}
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGPUPointSpriteRendererDemo.class);
		myManager.settings().size(1200, 600);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
