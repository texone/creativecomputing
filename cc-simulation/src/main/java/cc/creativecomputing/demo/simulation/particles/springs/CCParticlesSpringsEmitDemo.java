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
package cc.creativecomputing.demo.simulation.particles.springs;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.shader.CCShaderBufferDebugger;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticle;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.emit.CCParticlesIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.forces.CCAttractor;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.forces.springs.CCSpringForce;
import cc.creativecomputing.simulation.particles.render.CCIndexedParticleRenderer;
import cc.creativecomputing.simulation.particles.render.CCParticleRenderer;
import cc.creativecomputing.simulation.particles.render.CCParticleTriangleRenderer;
import cc.creativecomputing.simulation.particles.render.CCQuadRenderer;
import cc.creativecomputing.simulation.particles.render.CCSpringLineRenderer;
import cc.creativecomputing.simulation.particles.render.CCSpringVolumentricLineRenderer;

public class CCParticlesSpringsEmitDemo extends CCGL2Adapter {
	
	@CCProperty(name = "particles")
	private CCParticles _myParticles;
	
	private CCParticlesIndexParticleEmitter _myEmitter;
	
	
	private double _myX = 0;
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	private int _myXres = 500;
	private int _myYres = 500;
	
	@CCProperty(name = "alpha", min = 0, max = 1)
	private double _cAlpha = 0;
	
	@CCProperty(name = "debugger")
	private CCShaderBufferDebugger _myDebugger;
	@CCProperty(name = "particle debugger")
	private CCShaderBufferDebugger _myParticleDebugger;
	@CCProperty(name = "screencapture")
	private CCScreenCaptureController _cScreenCaptureController;

	CCSpringForce mySprings;
	CCParticleTriangleRenderer myTriangleRenderer;
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cScreenCaptureController = new CCScreenCaptureController(this);
//		frameRate(30);
		List<CCForce> myForces = new ArrayList<>();
		myForces.add(new CCForceField());
		myForces.add(new CCViscousDrag());
		myForces.add(new CCAttractor());
		
		mySprings = new CCSpringForce(4,4f);
		myForces.add(mySprings);
		
		List<CCParticleRenderer> myRenderer = new ArrayList<>();
		
		myTriangleRenderer = new CCParticleTriangleRenderer(3);
//		myRenderer.add(myTriangleRenderer);
		myRenderer.add(new CCSpringVolumentricLineRenderer(mySprings, false));
//		myRenderer.add(new CCSpringLineRenderer(mySprings));
		myRenderer.add(new CCQuadRenderer());
		
		_myParticles = new CCParticles(g,myRenderer, myForces, new ArrayList<>(),_myXres,_myYres);
		_myParticles.addEmitter(_myEmitter = new CCParticlesIndexParticleEmitter(_myParticles));
		
//		mySprings.setParticles(_myParticles);
//		mySprings.setSize(g, _myXres,_myYres);
//		mySprings.setShader(_myParticles.updateShader());
		_cCameraController = new CCCameraController(this, g, 100);
		
		
		g.strokeWeight(0.5f);
		
		_myDebugger = new CCShaderBufferDebugger(mySprings.idBuffer());
		_myParticleDebugger = new CCShaderBufferDebugger(_myParticles.infoData());
		
		g.textFont(CCFontIO.createTextureMapFont("arial", 12));
		
		keyPressed().add(e -> {
			switch (e.keyCode()) {
			case VK_R:
//				_myParticles.reset(g);
//				
//				double myXspace = 1500f / _myXres;
//				double myYspace = 1500f / _myYres;
//				
//				CCParticle[] myLeftParticles = new CCParticle[_myYres];
//				
////				List<Integer> _myIndices = new ArrayList<Integer>();
////				
//				for(int x = 0; x < _myXres; x++){
//					CCParticle myParticleAbove = null;
//					for(int y = 0; y < _myYres; y++){
//						CCParticle myParticle = _myEmitter.emit(
//							new CCVector3(x * myXspace - 750, 0, y * myYspace - 750),
//							new CCVector3(),
//							3000, true
//						);
//						
//						if(myParticleAbove != null) {
////							_mySprings.addSpring(myParticleAbove, myParticle, myYspace);
//						}
//						
//						if(myLeftParticles[y] != null) {
////							_mySprings.addSpring(myLeftParticles[y], myParticle, myXspace);
//						}
//
//						myParticleAbove = myParticle;
//						myLeftParticles[y] = myParticle;
//					}
//				}
//				break;
			default:
			}	
		});
	}
	

	
	
	private double _myTime = 0;
	
	private CCParticle _myParticle0;
	private CCParticle _myParticle1;
	private CCParticle _myParticle2;

	@Override
	public void update(final CCAnimator theAnimator) {

		double speed = CCMath.map(CCMath.sin(_myTime * 0.1),-1,1,4,8);
		_myTime += theAnimator.deltaTime() * speed;
		
		for(int i = 0; i < 20;i++) {
			CCVector2 myPosition = CCVector2.toCartesian(_myTime - theAnimator.deltaTime() / 20 * i * speed, 150);
			double z = CCMath.map(CCMath.sin(_myTime *0.567- theAnimator.deltaTime() / 20 * i * speed),-1,1,-150,150);
			if(_myEmitter.freeParticles() < 3)continue;
			CCParticle myParticle0 = _myEmitter.emit(
				new CCVector3().randomize(50).addLocal(myPosition.x, myPosition.y, z),
				new CCVector3(),
				30
			);
			CCParticle myParticle1 = _myEmitter.emit(
				new CCVector3().randomize(50).addLocal(myPosition.x, myPosition.y, z),
				new CCVector3(),
				30
			);
			CCParticle myParticle2 = _myEmitter.emit(
				new CCVector3().randomize(50).addLocal(myPosition.x, myPosition.y, z),
				new CCVector3(),
				30
			);
			
			double chance = 0.5;
			int length = 15;
			if(CCMath.chance(chance))	mySprings.addSpring(myParticle0, myParticle1, length);
			if(CCMath.chance(chance))	mySprings.addSpring(myParticle1, myParticle2, length);
			if(CCMath.chance(chance))	mySprings.addSpring(myParticle2, myParticle0, length);
			
			if(CCMath.chance(chance))	mySprings.addSpring(myParticle0, _myParticle0, length);
			if(CCMath.chance(chance))	mySprings.addSpring(myParticle1, _myParticle1, length);
			if(CCMath.chance(chance))	mySprings.addSpring(myParticle2, _myParticle2, length);
			
			_myParticle0 = myParticle0;
			_myParticle1 = myParticle1;
			_myParticle2 = myParticle2;
		
		}
		_myParticles.update(theAnimator);
	}
	
	@CCProperty(name = "debug")
	private boolean _cDebug = true;

	@Override
	public void display(CCGraphics g) {
		_myParticles.preDisplay(g);
		g.clear();
//		g.scale(0.5f);
		g.pushMatrix();
//		g.translate(_myX, 0);
		_cCameraController.camera().draw(g);
//		g.texture(_myTexture);
		
		g.blend();
		g.noDepthTest();
		g.color(0f,_cAlpha);
		_myParticles.display(g);
		
		g.blend();
		g.popMatrix();
		
		
		g.color(1d);
//		CCLog.info(g.vendor());
		_myDebugger.display(g);
		_myParticleDebugger.display(g);
	}

	public static void main(String[] args) {
		CCGL2Application myAppManager = new CCGL2Application(new CCParticlesSpringsEmitDemo());
		myAppManager.glcontext().size(1200, 1200);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

