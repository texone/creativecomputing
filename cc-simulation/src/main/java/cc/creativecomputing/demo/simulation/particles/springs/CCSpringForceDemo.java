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

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.demo.simulation.particles.CCForceFieldDemo;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticle;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.CCParticlesIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.springs.CCSpringForce;
import cc.creativecomputing.simulation.particles.render.CCParticlePointRenderer;

public class CCSpringForceDemo extends CCGL2Adapter {
	
	@CCProperty(name = "particles")
	private CCParticles _myParticles;
	private CCParticlesIndexParticleEmitter _myEmitter;
	private CCParticlePointRenderer _myRenderer;
	private CCSpringForce _mySprings;
	
	private CCForceField _myForceField = new CCForceField(); //0.005f,1,new CCVector3(100,20,30)
	
	private double _myX = 0;
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	private int _myXres = 400;
	private int _myYres = 400;
	
	private CCGLProgram _myGLSLShader;
	
	@CCProperty(name = "force field strength", min = 0, max = 1)
	private double _cForceFieldStrength = 0;
	

	@CCProperty(name = "spring strength", min = 0, max = 1)
	private double _cSpringStrength = 0;
	
	@CCProperty(name = "alpha", min = 0, max = 1)
	private double _cAlpha = 0;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
//		frameRate(30);
		CCGraphics.debug();
		List<CCForce> myForces = new ArrayList<>();
		myForces.add(_myForceField);
		_myForceField.strength(1f);
		
		List<CCConstraint> myConstraints = new ArrayList<>();
		
		_mySprings = new CCSpringForce(4,4d,5d);
//		
		myForces.add(_mySprings);
		
		_myRenderer = new CCParticlePointRenderer();
		_myParticles = new CCParticles(g,_myRenderer,myForces,myConstraints,_myXres,_myYres);
		_myParticles.addEmitter(_myEmitter = new CCParticlesIndexParticleEmitter(_myParticles));

		
		_cCameraController = new CCCameraController(this, g, 100);
		
		double myXspace = 1500f / _myXres;
		double myYspace = 1500f / _myYres;
		
		CCParticle[] myLeftParticles = new CCParticle[_myYres];
		
		for(int x = 0; x < _myXres; x++){
			CCParticle myParticleAbove = null;
			for(int y = 0; y < _myYres; y++){
				CCParticle myParticle = _myEmitter.emit(
					new CCVector3(x * myXspace - 750, 0, y * myYspace - 750),
					new CCVector3(),
					3000, true
				);
				
				if(myParticleAbove != null) {
					_mySprings.addSpring(myParticleAbove, myParticle, myYspace);
				}
				
				if(myLeftParticles[y] != null) {
					_mySprings.addSpring(myLeftParticles[y], myParticle, myXspace);
				}

				myParticleAbove = myParticle;
				myLeftParticles[y] = myParticle;
			}
		}
		
		g.strokeWeight(0.5f);
		g.clearColor(255);
		

		_myGLSLShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "shader/triangles_vertex.glsl"), 
			CCNIOUtil.classPath(this, "shader/triangles_fragment.glsl")
		);
		
		g.textFont(CCFontIO.createTextureMapFont("arial", 12));
		
		keyPressed().add(theEvent ->{
			switch (theEvent.keyCode()) {
			case VK_R:
				_myParticles.reset(null);
//				
				for(int x = 0; x < _myXres; x++){
					CCParticle myParticleAbove = null;
					for(int y = 0; y < _myYres; y++){
						CCParticle myParticle = _myEmitter.emit(
							new CCVector3(x * myXspace - 750, 0, y * myYspace - 750),
							new CCVector3(),
							3000, true
						);
						
						if(myParticleAbove != null) {
//							_mySprings.addSpring(myParticleAbove, myParticle, myYspace);
						}
						
						if(myLeftParticles[y] != null) {
//							_mySprings.addSpring(myLeftParticles[y], myParticle, myXspace);
						}

						myParticleAbove = myParticle;
						myLeftParticles[y] = myParticle;
					}
				}
				break;
			default:
			}
		});
	}
	

	
	
	private double _myTime = 0;

	@Override
	public void update(final CCAnimator theAnimator) {
		_myTime += theAnimator.deltaTime() * 100;
		
		_myForceField.strength(_cForceFieldStrength);
		_mySprings.strength(_cSpringStrength);
		_myForceField.offset(new CCVector3(_myTime*0.5f,0,0));
		
		_myParticles.update(theAnimator);
	}

	@Override
	public void display(CCGraphics g) {
		_myParticles.animate(g);
		
		g.clear();
//		g.scale(0.5f);
		g.pushMatrix();
		g.translate(_myX, 0);
		_cCameraController.camera().draw(g);
//		g.texture(_myTexture);
		
		g.blend();
		g.noDepthTest();
		g.color(0f,_cAlpha);
		_myParticles.display(g);
		
		
		g.blend();
		g.popMatrix();
		g.color(0);
	}

	public static void main(String[] args) {
		CCSpringForceDemo demo = new CCSpringForceDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

