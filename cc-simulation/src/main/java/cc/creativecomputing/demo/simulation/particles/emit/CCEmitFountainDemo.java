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
import cc.creativecomputing.gl.app.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCPlane;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.constraints.CCPlaneConstraint;
import cc.creativecomputing.simulation.particles.emit.CCParticlesIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;

public class CCEmitFountainDemo extends CCGL2Adapter {
	
	@CCProperty(name = "life time", min = 0, max = 10)
	private float _cLifeTime = 3f;
	
	@CCProperty(name = "emit amount", min = 0, max = 1000)
	private float _cEmit = 3f;
	
	@CCProperty(name = "init vel", min = 0, max = 1000)
	private float _cInitVel = 3f;
	
	@CCProperty(name = "random vel", min = 0, max = 10)
	private float _cRandomVel = 3f;
	
	@CCProperty(name = "random pos", min = 0, max = 10)
	private float _cRandomPos = 3f;
	
	@CCProperty(name = "alpha", min = 0, max = 1)
	private float _cAlpha = 0;
	
	private CCCameraController _myCameraController;
	
	@CCProperty(name = "particles")
	private CCParticles _myParticles;
	private CCParticlesIndexParticleEmitter _myEmitter;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(new CCGravity());
		myForces.add(new CCForceField());
		myForces.add(new CCViscousDrag());
		
		
		List<CCConstraint> myConstraints = new ArrayList<CCConstraint>();
		myConstraints.add(new CCPlaneConstraint(new CCPlane(new CCVector3(0,1,0), -300), 1.0f, 0f, 0.1f));
		
		_myParticles = new CCParticles(g, myForces, myConstraints, 700, 700);
		_myParticles.addEmitter(_myEmitter = new CCParticlesIndexParticleEmitter(_myParticles));
		
		_myCameraController = new CCCameraController(this, g, 100);
	}
	
	float _myOffset = 0;

	@Override
	public void update(final CCAnimator theDeltaTime) {
		for(int i = 0; i < _cEmit;i++){
			_myEmitter.emit(
				new CCVector3(CCMath.random(-2,2), -300, 0).addLocal(new CCVector3().randomize(CCMath.random(_cRandomPos))), 
				new CCVector3(0,_cInitVel,0).addLocal(new CCVector3().randomize(CCMath.random(_cRandomVel))),
				_cLifeTime
			);
		}
		
		_myParticles.update(theDeltaTime);
	}

	@Override
	public void display(CCGraphics g) {
		_myParticles.animate(g);
		g.clear();
		_myCameraController.camera().draw(g);
		g.noDepthTest();
		g.color(1f, _cAlpha);
		g.blend(CCBlendMode.ADD);
		_myParticles.display(g);
		
		g.noBlend();
//		if(frameCount % 100 == 0)CCScreenCapture.capture("export/db02/"+CCFormatUtil.nf(i++, 4)+".png", width, height);
		
	}
	
	
	public void keyPressed(CCKeyEvent theEvent) {
		switch(theEvent.keyCode()){
		case VK_R:
//			_myParticles.reset();
			break;
		case VK_S:
//			CCScreenCapture.capture("export/db02/"+CCFormatUtil.nf(i++, 4)+".png", width, height);
			break;
		default:
		}
	}

	public static void main(String[] args) {
		CCEmitFountainDemo demo = new CCEmitFountainDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

