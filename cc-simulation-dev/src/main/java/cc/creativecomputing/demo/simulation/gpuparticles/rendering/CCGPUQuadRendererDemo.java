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
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
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
import cc.creativecomputing.simulation.particles.render.CCGPUIndexedParticleRenderer;
import cc.creativecomputing.simulation.particles.render.CCGPUParticleQuadRenderer;

public class CCGPUQuadRendererDemo extends CCApp {
	
	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCArcball _myArcball;
	
	private CCForceField _myForceField = new CCForceField(0.005f,1,new CCVector3f(100,20,30));
	
	@CCControl(name = "point size", min = 0, max = 20)
	private float _cPointSize = 0;
	
	private CCGPUParticleQuadRenderer _myQuadRenderer;

	public void setup() {
		_myArcball = new CCArcball(this);
		
		final List<CCForce> myForces = new ArrayList<CCForce>();
//		myForces.add(new CCGPUGravity(new CCVector3f(1,0,0)));
		myForces.add(new CCViscousDrag(0.3f));
		myForces.add(_myForceField);
		
		_myQuadRenderer = new CCGPUParticleQuadRenderer();
		_myParticles = new CCParticles(g, _myQuadRenderer, myForces, new ArrayList<CCGPUConstraint>(), 700,700);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
//		_myParticles.pointSizeClamp(5, 5);
//		g.pointSize(5);
		
//		frameRate(20);
		
		addControls("app", "app", this);
	}
	
	private float _myTime = 0;
	
	public void update(final float theDeltaTime){
		_myTime += 1/30f * 0.5f;
		_myParticles.update(theDeltaTime);
		for(int i = 0; i < 100; i++){
			_myEmitter.emit(
					CCColor.random(),
//				new CCVector3f(),
				new CCVector3f(CCMath.random(-width/2, width/2),CCMath.random(-height/2, height/2),CCMath.random(-height/2, height/2)),
				CCVecMath.random3f(10),
				3, false
			);
		}
		
		_myForceField.noiseOffset(new CCVector3f(0,0,_myTime));
		_myForceField.noiseScale(0.0025f);
		
		_myQuadRenderer.pointSize(_cPointSize);
	}

	public void draw() {
		g.clear();
		g.pushMatrix();
		_myArcball.draw(g);
		g.color(255);
		g.blend();
		_myParticles.draw();
		g.popMatrix();
		g.color(255);
		g.text(frameRate + ":" + _myEmitter.particlesInUse(),-width/2+20,-height/2+20);
	}
	
	public void keyPressed(CCKeyEvent theEvent) {
		_myParticles.reset();
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGPUQuadRendererDemo.class);
		myManager.settings().size(1200, 600);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
