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
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.shader.CCShaderBufferDebugger;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticle;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.emit.CCParticlesIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.forces.CCAttractor;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.forces.springs.CCSpringForce;
import cc.creativecomputing.simulation.particles.render.CCParticleRenderer;
import cc.creativecomputing.simulation.particles.render.CCParticleTriangleRenderer;
import cc.creativecomputing.simulation.particles.render.CCQuadRenderer;
import cc.creativecomputing.simulation.particles.render.CCSpringLineRenderer;
import cc.creativecomputing.simulation.particles.render.CCSpringVolumentricLineRenderer;

public class CCParticlesSpringsDemo extends CCGL2Adapter {
	
	@CCProperty(name = "particles")
	private CCParticles _myParticles;
	
	private CCParticlesIndexParticleEmitter _myEmitter;
	
	
	private double _myX = 0;
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	private int _myXres = 100;
	private int _myYres = 100;
	
	@CCProperty(name = "alpha", min = 0, max = 1)
	private double _cAlpha = 0;
	
	@CCProperty(name = "debugger")
	private CCShaderBufferDebugger _myDebugger;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
//		frameRate(30);
		CCSpringForce mySprings;
		List<CCForce> myForces = new ArrayList<>();
		myForces.add(new CCForceField());
		myForces.add(new CCViscousDrag());
		myForces.add(new CCAttractor());
		
		mySprings = new CCSpringForce(4,4f);
		myForces.add(mySprings);
		
		List<CCParticleRenderer> myRenderer = new ArrayList<>();
		
		CCParticleTriangleRenderer myTriangleRenderer = new CCParticleTriangleRenderer(3);
		myRenderer.add(myTriangleRenderer);
		myRenderer.add(new CCSpringVolumentricLineRenderer(mySprings, false));
		myRenderer.add(new CCQuadRenderer());
		
		_myParticles = new CCParticles(g,myRenderer, myForces, new ArrayList<>(),new ArrayList<>(),new ArrayList<>(), _myXres, _myYres);
		_myParticles.addEmitter(_myEmitter = new CCParticlesIndexParticleEmitter(_myParticles));
		
		_cCameraController = new CCCameraController(this, g, 100);
		
		double myXspace = 500f / _myXres;
		double myYspace = 500f / _myYres;
		
		
		for(int x = 0; x < _myXres; x++){
			for(int y = 0; y < _myYres; y+=3){
				CCParticle myParticle0 = _myEmitter.emit(
					new CCVector3(x * myXspace - 750 - 60, 0, y * myYspace - 750),
					new CCVector3(),
					3000
				);
				CCParticle myParticle1 = _myEmitter.emit(
					new CCVector3(x * myXspace - 750, 0, y * myYspace - 750 + 100),
					new CCVector3(),
					3000
				);
				CCParticle myParticle2 = _myEmitter.emit(
					new CCVector3(x * myXspace - 750 + 60, 0, y * myYspace - 750),
					new CCVector3(),
					3000
				);
				
				if(
					mySprings.addSpring(myParticle0, myParticle1, 120)&&
					mySprings.addSpring(myParticle1, myParticle2, 120)&&
					mySprings.addSpring(myParticle2, myParticle0, 120)
				) {
					myTriangleRenderer.addTriangle(myParticle0, myParticle1, myParticle2);
				}else {
					CCLog.info("no Spring");
				}
				
			}
		}
		
		g.strokeWeight(0.5f);
		
		_myDebugger = new CCShaderBufferDebugger(mySprings.idBuffer());
		
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

	@Override
	public void update(final CCAnimator theAnimator) {
		_myTime += theAnimator.deltaTime() * 100;
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
		if(_cDebug)_myDebugger.display(g);
	}

	public static void main(String[] args) {
		CCGL2Application myAppManager = new CCGL2Application(new CCParticlesSpringsDemo());
		myAppManager.glcontext().size(1200, 1200);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

