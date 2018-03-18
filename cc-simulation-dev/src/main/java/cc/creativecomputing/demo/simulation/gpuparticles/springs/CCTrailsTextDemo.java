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
package cc.creativecomputing.demo.simulation.gpuparticles.springs;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.export.CCTileSaver;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCOutlineFont;
import cc.creativecomputing.graphics.font.text.CCTextAlign;
import cc.creativecomputing.graphics.font.text.CCTextContours;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCGPUParticle;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.forces.springs.CCGPUAnchoredSprings;
import cc.creativecomputing.simulation.particles.forces.springs.CCDampedSprings;
import cc.creativecomputing.simulation.particles.forces.springs.CCGPUSprings;
import cc.creativecomputing.simulation.particles.render.CCGPUParticlePointRenderer;
import cc.creativecomputing.util.logging.CCLog;

public class CCTrailsTextDemo extends CCApp {
	
	private static class Path{
		
		private float _myLength = 0;
		private CCVector2f _myLastPoint;
		
		private List<Float> _myLengths = new ArrayList<Float>();
		private List<Float> _myDistances = new ArrayList<Float>();
		private List<CCVector2f> _myPoints = new ArrayList<CCVector2f>();
		
		public void addPoint(CCVector2f thePoint) {
			if(_myLastPoint == null) {
				_myLengths.add(0f);
			}else {
				float myDistance = _myLastPoint.distance(thePoint);
				_myLength += myDistance;
				_myLengths.add(_myLength);
				_myDistances.add(myDistance);
			}
			_myLastPoint = thePoint;
			_myPoints.add(thePoint);
		}
		
		public CCVector2f point(final float theBlend) {
			float myPointLength = theBlend * _myLength;
			
			if(theBlend == 1f)return _myPoints.get(_myPoints.size()-1);
			if(theBlend == 0f)return _myPoints.get(0);
			
			int myIndex = 0;
			float myPosition = 0;
			for(float myLength:_myLengths) {
				if(myPointLength < myLength) {
					myPosition = myLength - myPointLength;
					break;
				}
				myIndex++;
			}
			float myBlend = myPosition / _myDistances.get(myIndex - 1);

			CCVector2f myV1 = _myPoints.get(myIndex - 1);
			CCVector2f myV2 = _myPoints.get(myIndex);
			
			return CCVecMath.blend(1 - myBlend, myV1, myV2);
		}
	}
	
	@CCControl(name = "noise scale", min = 0, max = 0.1f)
	private float _cNoiseScale = 0;
	
	@CCControl(name = "noise speed", min = 0, max = 0.01f)
	private float _cNoiseSpeed = 0;
	
	@CCControl(name = "noise strength", min = 0, max = 1f)
	private float _cNoiseStrength = 0;
	@CCControl(name = "rest length", min = 0, max = 100f)
	private float _cRestLength = 0;
	@CCControl(name = "spring strength", min = 0, max = 10f)
	private float _cSpringStrength = 0;
	@CCControl(name = "gravity strength", min = -1, max = 1)
	private float _cGravityStrength = 0;
	

	
	@CCControl(name = "anchored spring strength", min = 0, max = 1f)
	private float _cAnchorStrength = 0;
	
	private CCGPUParticlePointRenderer _myRenderer;
	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCGPUAnchoredSprings _myAnchoredSprings;
	private CCGPUSprings _mySprings;
	
	private CCForceField _myForceField = new CCForceField(0.005f,1,new CCVector3f(100,20,30));
	private CCGravity _myGravity = new CCGravity(new CCVector3f(0,0,10));
	
	private CCArcball _myArcball;
	
	private boolean _myPause = false;
	
	private CCTileSaver _myTileSaver;
	
	private int _myNumberOfTrails = 1000;
	private int _myParticlesPerTrail = 10;
	private int _myRows = 20;
	
	private List<CCGPUParticle> _myLeadingParticles = new ArrayList<CCGPUParticle>();
	
	private CCShaderBuffer _myForceBlendTexture;

	@Override
	public void setup() {
		_myTileSaver = new CCTileSaver(g);
		
		addUpdateListener(_myTileSaver);
		addPostListener(_myTileSaver);
		fixUpdateTime(1/30f);
//		frameRate(30);
		
		List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(new CCGravity(new CCVector3f(-0.1f,0,0)));
		_myForceField.strength(2f);
		myForces.add(_myForceField);
		myForces.add(_myGravity);
		myForces.add(new CCViscousDrag(0.3f));
		
		_myForceBlendTexture = new CCShaderBuffer(_myNumberOfTrails,_myParticlesPerTrail * _myRows);
//		myForces.add(_myIDTextureForceBlend);
		List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();
		
		_myAnchoredSprings = new CCGPUAnchoredSprings(g,5.5f, 0.2f,1);
		_myAnchoredSprings.strength(2f);
		myForces.add(_myAnchoredSprings);
		
		_mySprings = new CCDampedSprings(g,2,1f,0.1f,1f);	
		myForces.add(_mySprings);
		
		_myRenderer = new CCGPUParticlePointRenderer();
		_myParticles = new CCParticles(g,_myRenderer,myForces, myConstraints, _myNumberOfTrails,_myParticlesPerTrail * _myRows);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		_myRenderer.mesh().drawMode(CCDrawMode.LINES);
		_myArcball = new CCArcball(this);
		
		List<Integer> _myIDs = new ArrayList<Integer>();
		List<CCColor> _myColors = new ArrayList<CCColor>();
		
		CCOutlineFont font = CCFontIO.createOutlineFont("Arial",640, 160);
		
		CCTextContours _myTextContour = new CCTextContours(font);
		_myTextContour.align(CCTextAlign.CENTER);
		_myTextContour.text("T");
		_myTextContour.draw(g);
		
		List<Path>_myLetters = new ArrayList<Path>();
		CCLog.info(_myTextContour.contours().size());
		for(List<CCVector2f> myContour:_myTextContour.contours()) {
			Path _myPath = new Path();
			for(CCVector2f myPoint:myContour) {
				_myPath.addPoint(myPoint);
			}

			_myPath.addPoint(myContour.get(0));
			
			_myLetters.add(_myPath);
			
		}
		
		_myForceBlendTexture.beginDraw();
		g.beginShape(CCDrawMode.POINTS);
		
		int i = 0;
		int res = _myNumberOfTrails;
		
		for(int myRow = 0; myRow < _myRows;myRow++) {
			for(int x = 0; x < _myNumberOfTrails; x++){
				// setup leading particle pulled by the anchored spring
				Path myPath = _myLetters.get((i++ / res) % _myLetters.size());
				CCVector2f myPos = myPath.point((i % res) / (float)res);
				float myX = myPos.x;
				float myY = myPos.y;
				
				CCGPUParticle myParticle = _myEmitter.emit(
					new CCVector3f(myX,myY, 0),
					new CCVector3f(),
					3000, false
				);
				g.color(1f);
				g.vertex(myParticle.x(),myParticle.y(),0);
				_myLeadingParticles.add(myParticle);
				_myAnchoredSprings.addSpring(
					myParticle,new CCVector3f(myX,myY,0)
				);
				_myColors.add(new CCColor(0f,0.25f));
				
				// setup trail
				for(int y = 1; y < _myParticlesPerTrail; y++){
					CCGPUParticle myNewParticle = _myEmitter.emit(
						new CCVector3f(myX,myY, y * 0.2f),
						new CCVector3f(),
						3000, false
					);
					_mySprings.addSpring(myNewParticle, myParticle, 0.01f, true);
					_myIDs.add(myParticle.index());
					_myIDs.add(myNewParticle.index());
					_myColors.add(new CCColor(0f,0.05f)); //CCMath.pow((1f - (float)y / _myParticlesPerTrail),10) * 
					myParticle = myNewParticle;
					
					g.color(CCMath.pow(1f - (float)y / _myParticlesPerTrail,1f));
					g.vertex(myParticle.x(),myParticle.y(),0);
				}
			}
		}
		
		g.endShape();
		_myForceBlendTexture.endDraw();
		
		_myRenderer.mesh().indices(_myIDs);
		_myRenderer.mesh().colors(_myColors);
		_myParticles.update(0);
		
		g.clearColor(255);

		addControls("app", "app", this);
	}
	
	private float _myTime = 0;

	@Override
	public void update(final float theDeltaTime) {
		if(_myPause)return;
		
		_myTime += theDeltaTime * 100;
		
		_myForceField.noiseOffset(new CCVector3f(0, 0, _myTime * _cNoiseSpeed));
		_myForceField.noiseScale(_cNoiseScale);
		_myForceField.strength(_cNoiseStrength);
		_mySprings.restLength(_cRestLength);
		_mySprings.strength(_cSpringStrength);
		_myAnchoredSprings.strength(_cAnchorStrength);
		_myGravity.strength(_cGravityStrength);
		
		_myParticles.update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		
//		g.noBlend();
		g.blend();
		g.noDepthTest();
		_myRenderer.mesh().draw(g);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTrailsTextDemo.class);
		myManager.settings().size(1900, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

