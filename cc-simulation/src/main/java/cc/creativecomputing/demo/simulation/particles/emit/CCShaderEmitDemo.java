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
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.emit.gpu.CCEmitter;
import cc.creativecomputing.simulation.particles.emit.gpu.CCParticleGPUEmitter;
import cc.creativecomputing.simulation.particles.emit.gpu.CCRingEmitter;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.render.CCIndexedParticleRenderer;
import cc.creativecomputing.simulation.particles.render.CCParticleDataDebugRenderer;

public class CCShaderEmitDemo extends CCGL2Adapter {

	private CCCameraController _myCameraController;
	
	@CCProperty(name = "particles")
	private CCParticles _myParticles;
	
	@CCProperty(name = "debug")
	private CCParticleDataDebugRenderer _cDebugRenderer;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myCameraController = new CCCameraController(this,g, 100);
		
		List<CCForce> myForces = new ArrayList<>();
		myForces.add(new CCViscousDrag(0.2f));
		myForces.add(new CCGravity());
		myForces.add(new CCForceField());
		
		List<CCConstraint> myConstraints = new ArrayList<CCConstraint>();
		
		_myParticles = new CCParticles(g, new CCIndexedParticleRenderer(), myForces, myConstraints, 2000, 2000);
		List<CCEmitter> myEmitter = new ArrayList<>();
		myEmitter.add(new CCRingEmitter());
		myEmitter.add(new CCRingEmitter());
		_myParticles.addEmitter(new CCParticleGPUEmitter(g,_myParticles, myEmitter));
		_cDebugRenderer = new CCParticleDataDebugRenderer(_myParticles);
	}
	
	@Override
	public void update(final CCAnimator theDeltaTime) {
		_myParticles.update(theDeltaTime);
	}

	@Override
	public void display(CCGraphics g) {
		_myParticles.preDisplay(g);
		g.clear();
		

		g.clearColor(0, 255);
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
	}

	public static void main(String[] args) {
		CCShaderEmitDemo demo = new CCShaderEmitDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
