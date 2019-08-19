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
package cc.creativecomputing.demo.simulation.particles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.emit.CCParticleCPUGroupEmitter;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCGravity;

public class CCGroupDemo extends CCGL2Adapter {
	
	@CCProperty(name = "particles")
	private CCParticles _myParticles;
	private CCParticleCPUGroupEmitter _myEmitter;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		final List<CCForce> myForces = new ArrayList<>();
		myForces.add(new CCGravity(new CCVector3(0,-1,0)));
		
		_myParticles = new CCParticles(g,myForces, new ArrayList<>(),new ArrayList<CCConstraint>(),100, 100);
		_myParticles.addEmitter(_myEmitter = new CCParticleCPUGroupEmitter(_myParticles));
		_myParticles.reset(g);
		for(int i = 0; i < 3;i++) {
			_myEmitter.createGroup(
				(particleGroup, particle) -> {
					CCColor myColor = CCColor.WHITE;
					double myX = 0;
					switch(particleGroup.id) {
					case 0:
						myColor = CCColor.WHITE;
						myX = -300 + CCMath.random(-100, 100);
						break;
					case 1:
						myColor = CCColor.WHITE;
						myX = CCMath.random(-100, 100);
						break;
					case 2:
						myColor = CCColor.WHITE;
						myX = 300 + CCMath.random(-100, 100);
						break;
					}
					particle.color().set(myColor);
					particle.position().set(myX, 300,0);
				},
				3000
			);
		}
		
		keyReleased().add(e -> {
			switch(e.keyCode()) {
			case VK_0:
				_myEmitter.killGroup(0);
				break;
			case VK_1:
				_myEmitter.killGroup(1);
				break;
			case VK_2:
				_myEmitter.killGroup(2);
				break;
			case VK_3:
				_myEmitter.killGroup(3);
				break;
			case VK_A:
				_myEmitter.kill();
				break;
			default:
				break;
			}
			CCLog.info("KILL");
		});
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_myParticles.update(theAnimator);
	}
	
	double angle = 0;

	@Override
	public void display(CCGraphics g) {
		_myParticles.preDisplay(g);
		
		g.noDepthTest();
		g.clearColor(50);
		g.clear();
		g.color(255);
		g.pushMatrix();
		g.translate(0, 0, -1000);
		g.rotateY(angle);
		g.blend(CCBlendMode.ADD);
		g.color(255);
		_myParticles.display(g);
		
		g.popMatrix();
		
		g.color(255);
		g.image(_myParticles.groupTexture(), 0,0, 400,400);
		g.blend();
	}

	public static void main(String[] args) {

		CCGroupDemo demo = new CCGroupDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
