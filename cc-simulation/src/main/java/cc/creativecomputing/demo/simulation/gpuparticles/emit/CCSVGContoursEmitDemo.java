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
package cc.creativecomputing.demo.simulation.gpuparticles.emit;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.spline.CCLinearSpline;
import cc.creativecomputing.math.spline.CCSpline;
import cc.creativecomputing.model.svg.CCSVGDocument;
import cc.creativecomputing.model.svg.CCSVGIO;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.constraints.CCGPUYConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;

public class CCSVGContoursEmitDemo extends CCApp{
	
	@CCControl(name = "life time", min = 0, max = 30)
	private float _cLifeTime = 3f;
	
	@CCControl(name = "emit amount", min = 0, max = 1000)
	private float _cEmit = 3f;
	@CCControl(name = "follow speed", min = 0, max = 1)
	private float _cFollowSpeed = 0;
	
	@CCControl(name = "init vel", min = 0, max = 1000)
	private float _cInitVel = 3f;
	
	@CCControl(name = "random vel", min = 0, max = 10)
	private float _cRandomVel = 3f;
	
	@CCControl(name = "random pos", min = 0, max = 10)
	private float _cRandomPos = 3f;
	
	@CCControl(name = "gx", min = -1, max = 1)
	private float _cX = 0;
	
	@CCControl(name = "gy", min = -1, max = 1)
	private float _cY = 0;
	
	@CCControl(name = "gz", min = -1, max = 1)
	private float _cZ = 0;
	
	@CCControl(name = "g strength", min = 0, max = 1)
	private float _cGStrength = 0;
	
	@CCControl(name = "alpha", min = 0, max = 1)
	private float _cAlpha = 0;
	
	private CCForceField _myForceField;
	
	@CCControl(name = "n scale", min = 0, max = 1)
	private float _cNScale = 0;
	
	@CCControl(name = "n strength", min = 0, max = 1)
	private float _cNStrength = 0;
	
	@CCControl(name = "n speed", min = 0, max = 3)
	private float _cNSpeed = 0;
	
	private CCSVGDocument _myDocument;
	private List<CCLinearSpline>_myContours = new ArrayList<>();
	
	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCGravity _myGravity;
	
	private CCLinearSpline _mySpline = new CCLinearSpline(false);

	@Override
	public void setup(){
		_myDocument = CCSVGIO.newSVG("01_computer.svg");
		
		_myContours = _myDocument.contours();
		
		_mySpline.beginEditSpline();
		for(CCSpline myContour:_myContours){
			for(CCVector3f myPoint:myContour.points()){
			_mySpline.addPoint(myPoint);
			}
		}
		_mySpline.endEditSpline();
		
		List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(_myGravity = new CCGravity(new CCVector3f()));
		myForces.add(_myForceField = new CCForceField(0.01f, 1f, new CCVector3f()));
		myForces.add(new CCViscousDrag(0.9f));
		
		
		List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();
		myConstraints.add(new CCGPUYConstraint(-400, 1.0f, 0f, 0.1f));
		
		_myParticles = new CCParticles(g, myForces, myConstraints, 700, 700);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		
		addControls("app", "app", this);
	}
	
	float _myOffset = 0;
	
	float _myEmitPos = 0;
	float _myLastEmitPos = 0;
	
	@Override
	public void update(final float theDeltaTime) {
		for(int i = 0; i < _cEmit;i++){
			float myBlend = CCMath.random(_myLastEmitPos, _myEmitPos);
			_myEmitter.emit(_mySpline.interpolate(myBlend).clone().add(CCVecMath.random3f(CCMath.random(_cRandomPos))), new CCVector3f(0,_cInitVel,0).add(CCVecMath.random3f(CCMath.random(_cRandomVel))), _cLifeTime);
		}
		
		_myOffset += theDeltaTime * _cNSpeed;
		
		_myGravity.direction().set(_cX, _cY,_cZ);
		_myGravity.strength(_cGStrength);
		
		_myForceField.noiseScale(_cNScale);
		_myForceField.strength(_cNStrength);
		_myForceField.noiseOffset(new CCVector3f(0,0,_myOffset));
		
		_myLastEmitPos = _myEmitPos;
		_myEmitPos += theDeltaTime * _cFollowSpeed;
		_myEmitPos %= 1;
		
		_myParticles.update(theDeltaTime);
	}
	
	@Override
	public void draw() {
		g.clear();
		g.color(255);
		g.pushMatrix();
		g.translate(-width/2, -height/2);
		for(CCSpline myContour:_myContours){
			myContour.draw(g);
			
//			g.pointSize(5);
//			g.beginShape(CCDrawMode.POINTS);
//			for(CCVector2f myVertex:myContour){
//				g.vertex(myVertex);
//			}
//			g.endShape();
		}
		g.popMatrix();
		
		g.noDepthTest();
		g.color(1f, _cAlpha);
		g.blend(CCBlendMode.ADD);
		_myParticles.draw();
		
		g.noBlend();
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCSVGContoursEmitDemo.class);
		myManager.settings().size(1500, 900);
		myManager.settings().antialiasing(8);
		myManager.start();
	}

}
