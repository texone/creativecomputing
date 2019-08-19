/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.demo.simulation.particles.emit;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.emit.gpu.CCEmitter;
import cc.creativecomputing.simulation.particles.emit.gpu.CCGroupEmitter;
import cc.creativecomputing.simulation.particles.emit.gpu.CCParticleGPUEmitter;
import cc.creativecomputing.simulation.particles.emit.gpu.CCGroupEmitter.CCParticleGroup;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.render.CCIndexedParticleRenderer;
import cc.creativecomputing.simulation.particles.render.CCParticleDataDebugRenderer;

public class CCGroupEmitDemo extends CCGL2Adapter {
	@CCProperty(name = "camera")
	private CCCameraController _myCameraController;
	
	@CCProperty(name = "")
	private CCParticles _myParticles;
	
	@CCProperty(name = "debug")
	private CCParticleDataDebugRenderer _cDebugRenderer;
	
	@CCProperty(name = "draw activation Texture")
	private boolean _cDrawActivationTexture = true;
	
	private CCGroupEmitter _myGroupEmitter;
	
	private int size = 100;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myCameraController = new CCCameraController(this,g, 100);
		
		List<CCForce> myForces = new ArrayList<>();
		myForces.add(new CCViscousDrag(0.2f));
		myForces.add(new CCGravity());
		myForces.add(new CCForceField());
		
		List<CCConstraint> myConstraints = new ArrayList<CCConstraint>();
		//
		_myParticles = new CCParticles(g, new CCIndexedParticleRenderer(), myForces, new ArrayList<>(), myConstraints, size, size);
		List<CCEmitter> myEmitter = new ArrayList<>();
		_myGroupEmitter = new CCGroupEmitter();
		myEmitter.add(_myGroupEmitter);
		
		
		_myParticles.addEmitter(new CCParticleGPUEmitter(g,_myParticles, myEmitter));
		
		for(int i = 0; i < size;i++) {
			int mySize = size;
			CCParticleGroup myGroup = _myGroupEmitter.createGroup(mySize);
			myGroup.position.x = -400 + i * 8;
			
		}
		_cDebugRenderer = new CCParticleDataDebugRenderer(_myParticles);
	}
	
	@Override
	public void update(final CCAnimator theAnimator) {
		_myParticles.update(theAnimator);
		for(CCParticleGroup myGroup:_myGroupEmitter.groups()) {
			myGroup.progress = (CCMath.sin(theAnimator.time() * 0.4 + myGroup.id / 20d) + 1) / 2;
			myGroup.emitMax = (CCMath.sin(theAnimator.time() * 0.4 + myGroup.id / 20d) + 1) / 2;
		}
	}

	@Override
	public void display(CCGraphics g) {
		_myParticles.preDisplay(g);
		g.clear();
		g.color(255);
	
		g.pushMatrix();
		_myCameraController.camera().draw(g);
		g.noDepthTest();
		g.color(255, 50);
		g.blend(CCBlendMode.ADD);
		_myParticles.display(g);
		g.popMatrix();
		g.noBlend();
		_cDebugRenderer.draw(g);
		
		if(_cDrawActivationTexture) {
			g.pushMatrix();
			g.translate(-g.width()/2, -g.height()/2);
			g.image(_myGroupEmitter.activationTexture(), 0,0);
			g.popMatrix();
		}
	}

	public static void main(String[] args) {
		CCGroupEmitDemo demo = new CCGroupEmitDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
