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
package cc.creativecomputing.demo.simulation.particles.springs;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.demo.simulation.particles.realsense.CCRealSenseForceField;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.shader.CCShaderBufferDebugger;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.io.xml.CCXMLIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCLinearSpline;
import cc.creativecomputing.model.svg.CCSVGDocument;
import cc.creativecomputing.model.svg.CCSVGIO;
import cc.creativecomputing.simulation.particles.CCParticle;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.emit.CCParticlesIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.forces.CCAttractor;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCTargetForce;
import cc.creativecomputing.simulation.particles.forces.CCTextureForceField2D;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.forces.springs.CCSpringForce;
import cc.creativecomputing.simulation.particles.render.CCParticleRenderer;
import cc.creativecomputing.simulation.particles.render.CCParticleTriangleRenderer;
import cc.creativecomputing.simulation.particles.render.CCQuadRenderer;
import cc.creativecomputing.simulation.particles.render.CCSpringVolumentricLineRenderer;

public class CCParticlesSpringsDrawDemo2 extends CCGL2Adapter {

	

	@CCProperty(name = "particles")
	private CCParticles _myParticles;

	private CCParticlesIndexParticleEmitter _myEmitter;

	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;

	private int _myXres = 500;
	private int _myYres = 500;

	@CCProperty(name = "alpha", min = 0, max = 1)
	private double _cAlpha = 0;

	@CCProperty(name = "debugger")
	private CCShaderBufferDebugger _myDebugger;
	@CCProperty(name = "particle debugger")
	private CCShaderBufferDebugger _myParticleDebugger;
	@CCProperty(name = "screencapture")
	private CCScreenCaptureController _cScreenCaptureController;

	private CCSpringForce _mySprings;
	private CCTargetForce _myTargetForce;
	
	@CCProperty(name = "real sense")
	private CCRealSenseForceField _RealSenseForceField;
	
	private List<CCParticle> _myNewTargets = new ArrayList<>();

	double width;
	double height;
	
	private List<List<CCVector3>> _mySplines;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cScreenCaptureController = new CCScreenCaptureController(this);
		_RealSenseForceField = new CCRealSenseForceField(CCNIOUtil.dataPath("realsense02.byt"),1280,720);

		
//		frameRate(30);
		List<CCForce> myForces = new ArrayList<>();
		myForces.add(new CCForceField());
		myForces.add(new CCViscousDrag());
		myForces.add(new CCAttractor());
		myForces.add(_mySprings = new CCSpringForce(4, 4f));
		myForces.add(_myTargetForce = new CCTargetForce());
//		myForces.add(new CCTextureForceField2D(_RealSenseForceField.forceField(), new CCVector2(1920d, -1080d), new CCVector2(0.5, 0.5)));

		List<CCParticleRenderer> myRenderer = new ArrayList<>();

		myRenderer.add(new CCSpringVolumentricLineRenderer(_mySprings, false));
		myRenderer.add(new CCQuadRenderer());

		_myParticles = new CCParticles(g, myRenderer, myForces, new ArrayList<>(), _myXres, _myYres);
		_myParticles.addEmitter(_myEmitter = new CCParticlesIndexParticleEmitter(_myParticles));

		_cCameraController = new CCCameraController(this, g, 100);

		g.strokeWeight(0.5f);

		_myDebugger = new CCShaderBufferDebugger(_mySprings.idBuffer());
		_myParticleDebugger = new CCShaderBufferDebugger(_myParticles.infoData());

		g.textFont(CCFontIO.createTextureMapFont("arial", 12));


		width = g.width();
		height = g.height();
		CCSVGDocument _myDocument = CCSVGIO.newSVG(CCNIOUtil.dataPath("Wittgenstein.svg"));
		_mySplines = new ArrayList<>();
		for(CCLinearSpline mySpline:_myDocument.contours(1)) {
			List<CCVector3> myPoints = new ArrayList<>();
			double myLength = mySpline.totalLength();
			int myNumberOfPoints = CCMath.ceil(myLength / 1);
			
			for(int i = 0; i < myNumberOfPoints;i++) {
				double d0 = CCMath.norm(i, 0, myNumberOfPoints);
				double d1 = CCMath.norm(i + 1, 0, myNumberOfPoints);
				CCVector3 p0 = mySpline.interpolate(d0);
				CCVector3 p1 = mySpline.interpolate(d1);
				CCVector3 dir = p1.subtract(p0).normalizeLocal();
				myPoints.add(p0.add(dir.y*2, -dir.x*2,0));
				myPoints.add(p0.add(-dir.y*2, dir.x*2,0));
			}
			_mySplines.add(myPoints);
		}
		
		for(List<CCVector3> mySpline:_mySplines) {

			CCParticle myLast0 = null;
			CCParticle myLast1 = null;
			
			for(CCVector3 myPoint:mySpline) {
				CCVector3 myPosition = new CCVector3(myPoint.x, myPoint.y);
				CCParticle myParticle = _myEmitter.emit(myPosition, new CCVector3(), 30);
				myParticle.target().set(myPosition.x, myPosition.y, myPosition.z, 1);
				if (myLast0 != null) {
					_mySprings.addSpring(myParticle, myLast0);
				}
				if (myLast1 != null) {
					_mySprings.addSpring(myParticle, myLast1);
//					_mySprings.addSpring(myLast0, myLast1);
				}

				myLast1 = myLast0;
				myLast0 = myParticle;
				_myNewTargets.add(myParticle);
			}
		}
		

//		CCParticle myLast0 = null;
//		CCParticle myLast1 = null;
//		for(int i = 0; i < 1000;i+=10) {
//			
//			
//				CCVector3 myPosition = new CCVector3(i, 400 +(( i/10 %2 == 0) ? 30 : -30));
//				CCParticle myParticle = _myEmitter.emit(myPosition, new CCVector3(), 30);
//				myParticle.target().set(myPosition.x, myPosition.y, myPosition.z, 1);
//				CCLog.info(myPosition, myParticle.index());
//				
//				if (myLast0 != null) {
//					_mySprings.addSpring(myParticle, myLast0);
//					CCLog.info("p 0");
//				}
//				if (myLast1 != null) {
//					_mySprings.addSpring(myParticle, myLast1);
//					CCLog.info("p 1");
////					_mySprings.addSpring(myLast0, myLast1);
////					CCLog.info("0 1");
//				}
//
//				myLast1 = myLast0;
//				myLast0 = myParticle;
//				_myNewTargets.add(myParticle);
//			
//		}
	}

	int myIndex = 0;

	@Override
	public void update(final CCAnimator theAnimator) {
		_RealSenseForceField.update(theAnimator);
		
//		List<CCVector3> mySpline = _mySplines.get(myIndex);
//		CCParticle myLast = null;
//		for (CCVector3 myPoint : mySpline) {
//			CCVector3 myPosition = new CCVector3(myPoint.x - width / 2, myPoint.y - height / 2);
//			CCParticle myParticle = _myEmitter.emit(myPosition, new CCVector3(), 3);
//			myParticle.target().set(myPosition.x, myPosition.y, myPosition.z, 1);
//			if (myLast != null) {
//				_mySprings.addSpring(myParticle, myLast);
//			}
//
//			myLast = myParticle;
//			_myNewTargets.add(myParticle);
//		}
//
//		myIndex++;
//		myIndex %= _mySplines.size();

		/**
		CCGesture myGesture = _myGestures.get(myIndex);
		CCParticle myLast = null;
		for (CCVector3 myPoint : myGesture) {
			CCVector3 myPosition = new CCVector3(myPoint.x - width / 2, myPoint.y - height / 2);
			CCParticle myParticle = _myEmitter.emit(myPosition, new CCVector3(), 30);
			myParticle.target().set(myPosition.x, myPosition.y, myPosition.z, 1);
			if (myLast != null) {
				_mySprings.addSpring(myParticle, myLast);
			}

			myLast = myParticle;
			_myNewTargets.add(myParticle);
		}

		myIndex++;
		myIndex %= _myGestures.size();
**/
		_myParticles.update(theAnimator);
	}

	@CCProperty(name = "debug")
	private boolean _cDebug = true;

	@Override
	public void display(CCGraphics g) {
		g.clearColor(0);
		_RealSenseForceField.preDisplay(g);
		_myTargetForce.beginSetTargets(g);
		for(CCParticle myParticle:_myNewTargets){
			_myTargetForce.addTarget(myParticle);
		}
		_myTargetForce.endSetTargets(g);
		_myNewTargets.clear();
		
		_myParticles.preDisplay(g);
		g.clear();
		g.pushMatrix();
//		g.ortho();
		_cCameraController.camera().draw(g);
		
		g.pushMatrix();
		g.scale(1,-1);
		g.image(_RealSenseForceField.forceField(),-g.width()/2, -g.height()/2, g.width(), g.height());
		g.popMatrix();

		g.blend();
		g.noDepthTest();
		g.color(0f, _cAlpha);
		_myParticles.display(g);
//
//		g.blend();
		g.popMatrix();
		
		g.pushAttribute();
		g.polygonMode(CCPolygonMode.LINE);
		g.color(1d,0,0);
		for(List<CCVector3> mySpline :_mySplines) {
			g.beginShape(CCDrawMode.TRIANGLE_STRIP);
			for(CCVector3 myPoint:mySpline) {
				g.vertex(myPoint);
//				CCLog.info(myPoint);
			}
			g.endShape();
		}
		g.popAttribute();
//		g.color(1d);
////		CCLog.info(g.vendor());
//		_myDebugger.display(g);
//		_myParticleDebugger.display(g);
	}

	public static void main(String[] args) {
		CCGL2Application myAppManager = new CCGL2Application(new CCParticlesSpringsDrawDemo2());
		myAppManager.glcontext().size(1800, 1368);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
