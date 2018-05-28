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
package cc.creativecomputing.demo.simulation.particles.render;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawAttributes;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.emit.CCParticlesIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.render.CCPointSpriteRenderer;

public class CCPointSpriteRendererDemo extends CCGL2Adapter {
	
	@CCProperty(name = "particles")
	private CCParticles _myParticles;
	private CCParticlesIndexParticleEmitter _myEmitter;

	@CCProperty(name = "camera")
	private CCCameraController _myCameraController;
	
	@CCProperty(name = "attributes")
	private CCDrawAttributes _cAttributes = new CCDrawAttributes();
	
	
	private CCTexture2D _myPointSpriteTexture;
	private CCPointSpriteRenderer _myRenderer;
	
	@CCProperty(name = "emit particles", min = 0, max = 500)
	private int _cEmitParticles = 50;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {

		_myCameraController = new CCCameraController(this,g, 100);
		
		_myPointSpriteTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("golddust.png")));
		_myPointSpriteTexture.generateMipmaps(true);
		_myPointSpriteTexture.textureFilter(CCTextureFilter.LINEAR);
		_myPointSpriteTexture.textureMipmapFilter(CCTextureMipmapFilter.LINEAR);
		
		List<CCForce> myForces = new ArrayList<>();
		myForces.add(new CCViscousDrag(0.2f));
		myForces.add(new CCGravity());
		myForces.add(new CCForceField());
		
		List<CCConstraint> myConstraints = new ArrayList<CCConstraint>();
		
		_myRenderer = new CCPointSpriteRenderer(_myPointSpriteTexture,8, 3);
		
		_myParticles = new CCParticles(g, _myRenderer, myForces, myConstraints, 800, 800);
		_myParticles.addEmitter(_myEmitter = new CCParticlesIndexParticleEmitter(_myParticles));
	}
	
	private boolean _myPause = false;
	
	@Override
	public void update(final CCAnimator theAnimator){
		if(_myPause)return;
		
		_myParticles.update(theAnimator);
		_myRenderer.fadeOut(false);
	}

	@Override
	public void display(CCGraphics g) {
		for(int i = 0; i < _cEmitParticles; i++){
			_myEmitter.emit(
//				new CCVector3f(),
				new CCVector3(-g.width() / 2,CCMath.random(-g.height()/2, g.height()/2),0),
				new CCVector3().randomize(10),
				10
			);
		}
		
		_myParticles.preDisplay(g);
		
		g.clearColor(0);
		g.clear();
		g.color(255);
		g.noDepthTest();
		g.pushMatrix();
		_myCameraController.camera().draw(g);
		_cAttributes.start(g);
		_myParticles.display(g);
		_cAttributes.end(g);
		g.popMatrix();
		g.color(255);
		
		
	}
	
	public static void main(String[] args) {
		CCPointSpriteRendererDemo demo = new CCPointSpriteRendererDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
