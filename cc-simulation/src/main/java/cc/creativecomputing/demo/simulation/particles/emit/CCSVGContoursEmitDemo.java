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
package cc.creativecomputing.demo.simulation.particles.emit;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCPlane;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCLinearSpline;
import cc.creativecomputing.math.spline.CCSpline;
import cc.creativecomputing.model.svg.CCSVGDocument;
import cc.creativecomputing.model.svg.CCSVGIO;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.constraints.CCPlaneConstraint;
import cc.creativecomputing.simulation.particles.emit.CCParticlesIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;

public class CCSVGContoursEmitDemo extends CCGL2Adapter{
	
	@CCProperty(name = "life time", min = 0, max = 30)
	private float _cLifeTime = 3f;
	
	@CCProperty(name = "emit amount", min = 0, max = 1000)
	private float _cEmit = 3f;
	@CCProperty(name = "follow speed", min = 0, max = 1)
	private float _cFollowSpeed = 0;
	
	@CCProperty(name = "init vel", min = 0, max = 1000)
	private float _cInitVel = 3f;
	
	@CCProperty(name = "random vel", min = 0, max = 10)
	private float _cRandomVel = 3f;
	
	@CCProperty(name = "random pos", min = 0, max = 10)
	private float _cRandomPos = 3f;
	
	@CCProperty(name = "alpha", min = 0, max = 1)
	private float _cAlpha = 0;
	
	
	
	private CCSVGDocument _myDocument;
	private List<CCLinearSpline>_myContours = new ArrayList<>();
	
	private CCParticles _myParticles;
	private CCParticlesIndexParticleEmitter _myEmitter;
	
	private CCLinearSpline _mySpline = new CCLinearSpline(false);

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myDocument = CCSVGIO.newSVG(CCNIOUtil.dataPath("01_computer.svg"));
		
		_myContours = _myDocument.contours();
		
		_mySpline.beginEditSpline();
		for(CCSpline myContour:_myContours){
			for(CCVector3 myPoint:myContour.points()){
			_mySpline.addPoint(myPoint);
			}
		}
		_mySpline.endEditSpline();
		
		List<CCForce> myForces = new ArrayList<>();
		myForces.add(new CCGravity(new CCVector3()));
		myForces.add(new CCForceField());
		myForces.add(new CCViscousDrag(0.9));
		
		
		List<CCConstraint> myConstraints = new ArrayList<CCConstraint>();
		myConstraints.add(new CCPlaneConstraint(new CCPlane(new CCVector3(0,-1,0), 400)));
		
		_myParticles = new CCParticles(g, myForces, new ArrayList<>(), myConstraints, 700, 700);
		_myParticles.addEmitter(_myEmitter = new CCParticlesIndexParticleEmitter(_myParticles));
	}
	
	
	float _myEmitPos = 0;
	float _myLastEmitPos = 0;
	
	@Override
	public void update(final CCAnimator theDeltaTime) {
		for(int i = 0; i < _cEmit;i++){
			float myBlend = CCMath.random(_myLastEmitPos, _myEmitPos);
			_myEmitter.emit(
				_mySpline.interpolate(myBlend).clone().add(new CCVector3().randomize(_cRandomPos)), 
				new CCVector3(0,_cInitVel,0).addLocal(new CCVector3().randomize(_cRandomVel)),
				_cLifeTime
			);
		}
		
		
		_myLastEmitPos = _myEmitPos;
		_myEmitPos += theDeltaTime.deltaTime() * _cFollowSpeed;
		_myEmitPos %= 1;
		
		_myParticles.update(theDeltaTime);
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(255);

		
		g.noDepthTest();
		g.color(1f, _cAlpha);
		g.blend(CCBlendMode.ADD);
		_myParticles.display(g);
		
		g.noBlend();
	}
	
	public static void main(String[] args) {
		CCSVGContoursEmitDemo demo = new CCSVGContoursEmitDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}

}
