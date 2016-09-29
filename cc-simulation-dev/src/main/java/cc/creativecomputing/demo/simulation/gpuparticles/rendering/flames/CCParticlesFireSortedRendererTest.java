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
package cc.creativecomputing.demo.simulation.gpuparticles.rendering.flames;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTexture3D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.io.CCIOUtil;
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

public class CCParticlesFireSortedRendererTest extends CCApp {
	
	private CCParticles _myParticles;
	
	@CCControl(name = "fire")
	private CCGPUParticleFireRenderer _myRenderer;
	
	private CCGPUIndexParticleEmitter _myEmitter; 
	private CCArcball _myArcball;
	
	private CCForceField _myForceField = new CCForceField(0.005f,1,new CCVector3f(100,20,30));
	private CCGravity _myGravity;
	
	@CCControl(name = "emit particles", min = 0, max = 50)
	private int _cEmitParticles = 50;

	@CCControl(name = "pointsize", min = 1, max = 300)
	private float _cPointSize = 1;

	@CCControl(name = "alpha", min = 0, max = 1)
	private float _cAlpha = 1;
	
	@CCControl(name = "gravity", min = 0, max = 10)
	private float _cGravity = 0;
	
	@CCControl(name = "force field", min = 0, max = 10)
	private float _cForceField = 0;
	
	@CCControl(name = "emit radius", min = 0, max = 100)
	private float _cEmitRadius = 0;
	
	@CCControl(name = "emit line size", min = 0, max = 500)
	private float _cEmitLineSize = 0;
	
	@CCControl(name = "emit height", min = -500, max = 500)
	private float _cEmitHeight = 0;
	
	@CCControl(name = "emit lifetime", min = 0, max = 10)
	private float _cEmitLifeTime = 0;
	
	
	private CCTexture3D _myFlamesMovie;
	
	public void setup() {
		
		_myArcball = new CCArcball(this);
		
		final List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(new CCViscousDrag(0.3f));
		myForces.add(_myForceField);
		myForces.add(_myGravity = new CCGravity(new CCVector3f(0,1,0)));
		
		CCTexture2D myPointSpriteTexture = new CCTexture2D(CCTextureIO.newTextureData("spheres.png"));
		myPointSpriteTexture.generateMipmaps(true);
		myPointSpriteTexture.textureFilter(CCTextureFilter.LINEAR);
		myPointSpriteTexture.textureMipmapFilter(CCTextureMipmapFilter.LINEAR);
		
		String myFolder = "fire_black/";
		String[] myFiles = CCIOUtil.list(myFolder, "png");
		_myFlamesMovie = new CCTexture3D(CCTextureIO.newTextureData(myFolder + myFiles[0]), myFiles.length);
		for(int i = 0; i < myFiles.length; i++) {
			_myFlamesMovie.updateData(CCTextureIO.newTextureData(myFolder + myFiles[i]), i);
		}
		_myFlamesMovie.textureFilter(CCTextureFilter.LINEAR);
		
		_myRenderer = new CCGPUParticleFireRenderer(g, _myFlamesMovie);
		_myParticles = new CCParticles(g, _myRenderer, myForces, new ArrayList<CCGPUConstraint>(), 128,128);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		
		

		addControls("app", "app", this);
	}
	
	private float _myTime = 0;
	
	public void update(final float theDeltaTime){
		_myTime += 1/30f * 0.5f;
		
		
		_myForceField.noiseOffset(new CCVector3f(0,0,_myTime));
		_myForceField.noiseScale(0.0025f);
		_myForceField.strength(_cForceField);
		
		_myGravity.strength(_cGravity);
		
//		for(int i = 0; i < 100; i++){
//			_myEmitter.emit(
//				new CCVector3f(
//					CCMath.random(-width/2, width/2),
//					CCMath.random(-height/2, height/2),
//					CCMath.random(-height/2, height/2)
////					CCMath.map(i, 0, _myParticles.size(),height/2, -height/2)
//				),
//				CCVecMath.random3f(10),
//				10, false
//			);
//		}
//		if(frameCount % _cEmitParticles == 0){
		for(int i = 0; i < _cEmitParticles; i++){
			CCVector3f myEmitPosition = new CCVector3f(CCMath.random(-_cEmitRadius, _cEmitRadius),_cEmitHeight,CCMath.random(-_cEmitRadius,_cEmitRadius));
			myEmitPosition.add(CCMath.random(-_cEmitLineSize, _cEmitLineSize),0,0);
			_myEmitter.emit(
//				new CCVector3f(),
				myEmitPosition,
				CCVecMath.random3f(10),
				_cEmitLifeTime, false
			);
//		}
		}
		_myParticles.update(theDeltaTime);
		
//		_myRenderer.update(theDeltaTime);
		_myRenderer.pointSize(_cPointSize);
	}

	public void draw() {
		g.clear();
		g.pushMatrix();
		_myArcball.draw(g);
		g.blend();
//		g.pointSprite(_mySpriteTexture);
//		g.smooth();

		g.noDepthTest();
		g.color(1f,_cAlpha);
		g.blend();
		_myParticles.draw();
//		g.noSmooth();
//		g.noPointSprite();
		g.popMatrix();
		g.color(255,0,0);
		g.clearDepthBuffer();
		g.text(frameRate + ":" + _myEmitter.particlesInUse(),-width/2+20,-height/2+20);
	}
	
	public void keyPressed(CCKeyEvent theEvent) {
//		_myParticles.reset();
	}
	
//	public static void main(String[] args) {
//		CCApplicationManager myManager = new CCApplicationManager(CCParticlesSortedRendererTest.class);
//		myManager.settings().size(1200, 600);
////		myManager.settings().antialiasing(8);
//		myManager.start();
//	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesFireSortedRendererTest.class);
		myManager.settings().size(1200, 1000);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
