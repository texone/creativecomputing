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

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticlesIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.forces.CCTerrainForce;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;

public class CCTerrainForceDemo extends CCGL2Adapter {
	
	@CCProperty(name = "particles")
	private CCParticles _myParticles;
	private CCParticlesIndexParticleEmitter _myEmitter;
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	private CCTexture2D _myTexture;
	private CCTerrainForce _myTerrainForce;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		final List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(new CCGravity(new CCVector3(0.1,0,0)));
		myForces.add(new CCViscousDrag(0.3f));
		
		_myTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("heightmap.png")), CCTextureTarget.TEXTURE_RECT);
		_myTerrainForce = new CCTerrainForce(
			_myTexture,
			new CCVector3(4f,300,4f), 
			new CCVector3(200, 00, 100)
		);
		myForces.add(_myTerrainForce);
		
		_myParticles = new CCParticles(g, myForces, new ArrayList<CCConstraint>(), 1000,1000);
		_myParticles.addEmitter(_myEmitter = new CCParticlesIndexParticleEmitter(_myParticles));
		
		_cCameraController = new CCCameraController(this, g, 100);
	}
	
	float angle = 0;
	
	double height = 100;
	
	@Override
	public void update(final CCAnimator theAnimator){
	
		angle += theAnimator.deltaTime() * 30;
		for(int i = 0; i < 100; i++){
			_myEmitter.emit(
				new CCVector3(CCMath.random(-400,400), height/2,CCMath.random(-400,400)),
				new CCVector3().randomize(20),
				10, false
			);
		}
		_myParticles.update(theAnimator);
	}

	public void display(CCGraphics g) {
		height = g.height();
		g.noDepthTest();
		g.clear();
		g.color(255,50);
		
		g.pushMatrix();
		_cCameraController.camera().draw(g);
		g.blend(CCBlendMode.ADD);
		g.color(255,25);
		_myParticles.display(g);
		

		g.popMatrix();
		
		g.blend();
	}
	
	public static void main(String[] args) {

		CCTerrainForceDemo demo = new CCTerrainForceDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}

}
