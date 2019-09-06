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

public class CCEmitDemo extends CCGL2Adapter {
	
	@CCProperty(name = "life time", min = 0, max = 10)
	private float _cLifeTime = 3f;
	
	@CCProperty(name = "init vel", min = 0, max = 20)
	private float _cInitVel = 3f;
	
	@CCProperty(name = "particles")
	private CCParticles _myParticles;
	private CCParticlesIndexParticleEmitter _myEmitter;

	private double _myMouseX;
	private double _myMouseY;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		List<CCForce> myForces = new ArrayList<>();
		myForces.add(new CCGravity());
		myForces.add(new CCForceField()); 
		myForces.add(new CCViscousDrag());
		
		
		List<CCConstraint> myConstraints = new ArrayList<CCConstraint>();
		myConstraints.add(new CCPlaneConstraint(new CCPlane(new CCVector3(0,1,0), -300)));
		
		_myParticles = new CCParticles(g, myForces, new ArrayList<>(), myConstraints, 800, 800);
		_myParticles.addEmitter(_myEmitter = new CCParticlesIndexParticleEmitter(_myParticles));
		
		mouseMoved().add(e -> {
			_myMouseX = e.x() - g.width()/2;
			_myMouseY = g.height()/2 - e.y();
		});
	}

	@Override
	public void update(final CCAnimator theDeltaTime) {
		for(int i = 0; i < 1000;i++){
			_myEmitter.emit(new CCVector3(_myMouseX, _myMouseY, 0), new CCVector3().randomize(CCMath.random(_cInitVel)), _cLifeTime);
		}
		
		_myParticles.update(theDeltaTime);
	}

	@Override
	public void display(CCGraphics g) {
		_myParticles.preDisplay(g);
		g.clear();
		
		g.noDepthTest();
		g.color(255, 50);
		g.blend(CCBlendMode.ADD);
		_myParticles.display(g);
		
		g.noBlend();
	}

	public static void main(String[] args) {
		CCEmitDemo demo = new CCEmitDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

