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
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.render.CCGPUSortedParticleRenderer;

public class CCParticlesSortedRendererTest extends CCApp {
	
	private CCParticles _myParticles;
	private CCGPUSortedParticleRenderer _myRenderer;
	private CCGPUIndexParticleEmitter _myEmitter; 
	private CCArcball _myArcball;
	
	private CCForceField _myForceField = new CCForceField(0.005f,1,new CCVector3f(100,20,30));
	

	@CCControl(name = "pointsize", min = 1, max = 10)
	private float _cPointSize = 1;

	@CCControl(name = "alpha", min = 0, max = 1)
	private float _cAlpha = 1;

	@CCControl(name = "line alpha", min = 0, max = 1)
	private float _cLineAlpha = 1;
	
	public void setup() {
		
		addControls("app", "app", this);
		_myArcball = new CCArcball(this);
		
		final List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(new CCViscousDrag(0.3f));
		myForces.add(_myForceField);
		
		_myRenderer = new CCGPUSortedParticleRenderer(g);
		_myParticles = new CCParticles(g, _myRenderer, myForces, new ArrayList<CCGPUConstraint>(), 1024,1024);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		
		
		
	}
	
	private float _myTime = 0;
	
	public void update(final float theDeltaTime){
		_myTime += 1/30f * 0.5f;
		
		
		_myForceField.noiseOffset(new CCVector3f(0,0,_myTime));
		_myForceField.noiseScale(0.0025f);
		
		for(int i = 0; i < 100; i++){
			_myEmitter.emit(
				new CCVector3f(
					CCMath.random(-width/2, width/2),
					CCMath.random(-height/2, height/2),
					CCMath.random(-height/2, height/2)
//					CCMath.map(i, 0, _myParticles.size(),height/2, -height/2)
				),
				CCVecMath.random3f(10),
				10, false
			);
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

		g.color(1f,0f,0f,_cLineAlpha);
		for(int x = 0; x < width; x+=10){
			g.line(x - width/2, -100,x - width/2, 100);
		}

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
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesSortedRendererTest.class);
		myManager.settings().size(1200, 600);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
