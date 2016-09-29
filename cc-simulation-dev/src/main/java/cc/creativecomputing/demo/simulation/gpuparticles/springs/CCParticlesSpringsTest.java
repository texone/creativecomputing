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
package cc.creativecomputing.demo.simulation.gpuparticles.springs;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCGPUParticle;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.springs.CCGPUSprings;
import cc.creativecomputing.simulation.particles.render.CCGPUParticlePointRenderer;
import cc.creativecomputing.util.CCFormatUtil;

public class CCParticlesSpringsTest extends CCApp {
	
	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCGPUParticlePointRenderer _myRenderer;
	private CCGPUSprings _mySprings;
	
	private CCForceField _myForceField = new CCForceField(0.005f,1,new CCVector3f(100,20,30));
	
	private float _myX = 0;
	private CCArcball _myArcball;
	
	private int _myXres = 400;
	private int _myYres = 400;
	
	private CCGLSLShader _myGLSLShader;
	
	@CCControl(name = "force field strength", min = 0, max = 1)
	private float _cForceFieldStrength = 0;
	

	@CCControl(name = "spring strength", min = 0, max = 1)
	private float _cSpringStrength = 0;
	
	@CCControl(name = "alpha", min = 0, max = 1)
	private float _cAlpha = 0;

	@Override
	public void setup() {
//		frameRate(30);
		
		List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(_myForceField);
		_myForceField.strength(1f);
		
		List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();
		
		_mySprings = new CCGPUSprings(g,4,4f,5f);
//		
		myForces.add(_mySprings);
		
		_myRenderer = new CCGPUParticlePointRenderer();
		_myParticles = new CCParticles(g,_myRenderer,myForces,myConstraints,_myXres,_myYres);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		_myArcball = new CCArcball(this);
		
		float myXspace = 1500f / _myXres;
		float myYspace = 1500f / _myYres;
		
		CCGPUParticle[] myLeftParticles = new CCGPUParticle[_myYres];
		
		for(int x = 0; x < _myXres; x++){
			CCGPUParticle myParticleAbove = null;
			for(int y = 0; y < _myYres; y++){
				CCGPUParticle myParticle = _myEmitter.emit(
					new CCVector3f(x * myXspace - 750, 0, y * myYspace - 750),
					new CCVector3f(),
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
		

		_myGLSLShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "shader/triangles_vertex.glsl"), 
			CCIOUtil.classPath(this, "shader/triangles_fragment.glsl")
		);
		_myGLSLShader.load();
		
		g.textFont(CCFontIO.createTextureMapFont("arial", 12));

		addControls("app", "app", this);
	}
	

	
	
	private float _myTime = 0;

	@Override
	public void update(final float theDeltaTime) {
		_myTime += theDeltaTime * 100;
		
		_myForceField.strength(_cForceFieldStrength);
		_mySprings.strength(_cSpringStrength);
		_myForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
		
		_myParticles.update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clear();
//		g.scale(0.5f);
		g.pushMatrix();
		g.translate(_myX, 0);
		_myArcball.draw(g);
//		g.texture(_myTexture);
		
		g.blend();
		g.noDepthTest();
		g.color(0f,_cAlpha);
		_myParticles.draw();
		
		if(_myCapture) {
			CCScreenCapture.capture("export/springs03/"+CCFormatUtil.nf(frameCount,5)+".tga", width, height);
			frames++;
		}
		g.blend();
		g.popMatrix();
		g.color(0);
		if(_myCapture)g.text("seconds:" + frames/30,-width/2 + 20, height/2 - 20);
	}
	
	private boolean _myCapture = false;
	private float frames = 0;
	
	public void keyPressed(final CCKeyEvent theEvent) {
		switch (theEvent.keyCode()) {
		case VK_R:
			_myParticles.reset();
			
			float myXspace = 1500f / _myXres;
			float myYspace = 1500f / _myYres;
			
			CCGPUParticle[] myLeftParticles = new CCGPUParticle[_myYres];
			
//			List<Integer> _myIndices = new ArrayList<Integer>();
//			
			for(int x = 0; x < _myXres; x++){
				CCGPUParticle myParticleAbove = null;
				for(int y = 0; y < _myYres; y++){
					CCGPUParticle myParticle = _myEmitter.emit(
						new CCVector3f(x * myXspace - 750, 0, y * myYspace - 750),
						new CCVector3f(),
						3000, true
					);
					
					if(myParticleAbove != null) {
//						_mySprings.addSpring(myParticleAbove, myParticle, myYspace);
					}
					
					if(myLeftParticles[y] != null) {
//						_mySprings.addSpring(myLeftParticles[y], myParticle, myXspace);
					}

					myParticleAbove = myParticle;
					myLeftParticles[y] = myParticle;
				}
			}
			break;
		case VK_S:
			_myCapture = !_myCapture;
			if(_myCapture) {
				fixUpdateTime(1/30f);
				frames = 0;
			}else {
				freeUpdateTime();
			}
			break;
		default:
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesSpringsTest.class);
		myManager.settings().size(1200, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

