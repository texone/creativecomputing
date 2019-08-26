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
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.shader.CCShaderBufferDebugger;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCLinearSpline;
import cc.creativecomputing.model.svg.CCSVGDocument;
import cc.creativecomputing.model.svg.CCSVGIO;
import cc.creativecomputing.simulation.particles.CCParticle;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.blends.CCBlend;
import cc.creativecomputing.simulation.particles.blends.CCConstantBlend;
import cc.creativecomputing.simulation.particles.blends.CCLifeTimeBlend;
import cc.creativecomputing.simulation.particles.blends.CCTextureBlend;
import cc.creativecomputing.simulation.particles.emit.CCParticlesIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.forces.CCAttractor;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCPathTargetForce;
import cc.creativecomputing.simulation.particles.forces.CCTargetForce;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.forces.springs.CCSpringForce;
import cc.creativecomputing.simulation.particles.render.CCParticleRenderer;
import cc.creativecomputing.simulation.particles.render.CCParticleTriangleRenderer;
import cc.creativecomputing.simulation.particles.render.CCQuadRenderer;
import cc.creativecomputing.simulation.particles.render.CCSpringLineRenderer;
import cc.creativecomputing.simulation.particles.render.CCSpringVolumentricLineRenderer;

public class CCParticlesSpringsDrawDemo2 extends CCGL2Adapter {

	

	@CCProperty(name = "particles")
	private CCParticles _myParticles;

	private CCParticlesIndexParticleEmitter _myEmitter;

	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;

	private int _myXres = 700;
	private int _myYres = 700;

	@CCProperty(name = "alpha", min = 0, max = 1)
	private double _cAlpha = 0;

	@CCProperty(name = "debugger")
	private CCShaderBufferDebugger _myDebugger;
	@CCProperty(name = "particle debugger")
	private CCShaderBufferDebugger _myParticleDebugger;
	@CCProperty(name = "screencapture")
	private CCScreenCaptureController _cScreenCaptureController;

	private CCSpringForce _mySprings;
	private CCPathTargetForce _myTargetForce;
	private CCPathTargetForce _myTextTargetForce;
	
	@CCProperty(name = "real sense")
	private CCRealSenseForceField _RealSenseForceField;
	
	private List<CCParticle> _myNewTargetParticles = new ArrayList<>();
	private List<CCVector3> _myNewDocumentTargets = new ArrayList<>();
	private List<CCVector3> _myNewTextTargets = new ArrayList<>();
	private List<CCVector3> _myBlends = new ArrayList<>();
	
	
	private CCTextureBlend _myTextureBlend;
	
	private CCTexture2D _myTexture;
	int myPathResolution = 200;
	
	@CCProperty(name = "textpath speed", min = -10, max = 10)
	private double _cTextPathSpeed = 0;
	@CCProperty(name = "document path offset", min = -1, max = 1)
	private double _cDocumentPathOffset = 0;
	@CCProperty(name = "document path scale")
	private double _cDocumentPathScale = 500;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cScreenCaptureController = new CCScreenCaptureController(this);
		_RealSenseForceField = new CCRealSenseForceField(CCNIOUtil.dataPath("realsense02.byt"),1280,720);
		_myTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("Wittgenstein.png")));
		
		CCSVGDocument myZitat = CCSVGIO.newSVG(CCNIOUtil.dataPath("Zitat.svg"));
		List<CCLinearSpline> myZitatContours = myZitat.contours(1);
		
		CCSVGDocument myDocument = CCSVGIO.newSVG(CCNIOUtil.dataPath("Wittgenstein.svg"));
		List<CCLinearSpline> myDocumentContours = myDocument.contours(1);
		
		
//		frameRate(30);
		List<CCForce> myForces = new ArrayList<>();
		myForces.add(new CCForceField());
		myForces.add(new CCViscousDrag());
		myForces.add(new CCAttractor());
		myForces.add(_mySprings = new CCSpringForce(4, 4f));
		myForces.add(_myTargetForce = new CCPathTargetForce(myDocumentContours.size(), myPathResolution));
		myForces.add(_myTextTargetForce = new CCPathTargetForce(myZitatContours.size(), myPathResolution));
//		myForces.add(new CCTextureForceField2D(_RealSenseForceField.forceField(), new CCVector2(1920d, -1080d), new CCVector2(0.5, 0.5)));
		
		List<CCBlend> myBlends = new ArrayList<>();
		myBlends.add(new CCLifeTimeBlend());
		myBlends.add(new CCConstantBlend());
		myBlends.add(_myTextureBlend = new CCTextureBlend());

		CCParticleTriangleRenderer _myTriangleRenderer = new CCParticleTriangleRenderer(3);
		_myTriangleRenderer.texture(_myTexture);
		List<CCParticleRenderer> myRenderer = new ArrayList<>();

		myRenderer.add(new CCSpringVolumentricLineRenderer(_mySprings, false));
		myRenderer.add(new CCSpringLineRenderer(_mySprings));
		myRenderer.add(_myTriangleRenderer);
		myRenderer.add(new CCQuadRenderer());

		_myParticles = new CCParticles(g, myRenderer, myForces, myBlends, new ArrayList<>(), new ArrayList<>(), _myXres, _myYres);
		_myParticles.addEmitter(_myEmitter = new CCParticlesIndexParticleEmitter(_myParticles));

		_cCameraController = new CCCameraController(this, g, 100);

		g.strokeWeight(0.5f);

		_myDebugger = new CCShaderBufferDebugger(_mySprings.idBuffer());
		_myParticleDebugger = new CCShaderBufferDebugger(_myParticles.infoData());

		g.textFont(CCFontIO.createTextureMapFont("arial", 12));
		
		_myTargetForce.beginSetPaths(g);
		int p = 0;
		for(CCLinearSpline myDocumentSpline:myDocumentContours) {
			CCVector3 myFirst = myDocumentSpline.first().clone();
			myFirst.y = _myTexture.height() - myFirst.y;
			CCVector3 myLast = myDocumentSpline.last().clone();
			myLast.y = _myTexture.height() - myLast.y;
			_myTargetForce.setJump(p, myLast.subtract(myFirst));
			for(int i = 0; i < myPathResolution;i++) {
				double d = CCMath.norm(i, 0, myPathResolution - 1);
				CCVector3 myPoint = myDocumentSpline.interpolate(d);
				myPoint.y = _myTexture.height() - myPoint.y;
				_myTargetForce.addPath(p, i, myPoint);
			}
			p++;
		}
		_myTargetForce.endSetPaths(g);
		_myTargetForce.pathLength(myPathResolution);

		_myTextTargetForce.beginSetPaths(g);
		p = 0;
		for(CCLinearSpline myTextSpline:myZitatContours) {
			_myTextTargetForce.setJump(p, new CCVector3());
			for(int i = 0; i < myPathResolution;i++) {
				double d = CCMath.norm(i, 0, myPathResolution - 1);
				CCVector3 myPoint = myTextSpline.interpolate(d);
				myPoint.y = _myTexture.height() - myPoint.y;
				_myTextTargetForce.addPath(p, i, myPoint);
			}
			p++;
		}
		_myTextTargetForce.endSetPaths(g);
		_myTextTargetForce.pathLength(myPathResolution);
		
		List<List<CCVector3>> mySplines = new ArrayList<>();
		List<List<CCVector3>> myDocumentSplines = new ArrayList<>();
		List<List<CCVector3>> myTextSplines = new ArrayList<>();
		
		int zc = 0;
		int myDocumentPath = 0;
		
		for(CCLinearSpline mySpline:myDocumentContours) {
			int myTextPath = (int)CCMath.map(zc++, 0, myDocumentContours.size(), 0, myZitatContours.size());
			CCLinearSpline myTextSpline = myDocumentContours.get(myTextPath);

			List<CCVector3> myPoints = new ArrayList<>();
			List<CCVector3> myDocumentPoints = new ArrayList<>();
			List<CCVector3> myTextPoints = new ArrayList<>();
			
			double myDocumentLength = mySpline.totalLength();
			double myTextSplineLength = myTextSpline.totalLength();
			
			double myRatio = 1;//myLength / myTextSplineLength;
			
			double textOffset = CCMath.random();
			int myNumberOfPoints = CCMath.ceil(myDocumentLength / 1);
			
			for(int i = 0; i < myNumberOfPoints - 1;i++) {
				double d0 = CCMath.norm(i, 0, myNumberOfPoints);
				double d1 = CCMath.norm(i + 1, 0, myNumberOfPoints);
				
				CCVector3 p0 = mySpline.interpolate(d0);
				CCVector3 p1 = mySpline.interpolate(d1);
				CCVector3 dir = p1.subtract(p0).normalizeLocal();
				myPoints.add(p0.add( dir.y * 10, -dir.x * 10, 0));
				myPoints.add(p0.add(-dir.y * 10,  dir.x * 10, 0));
				
				double myPathIndex = CCMath.map(i, 0, myNumberOfPoints - 1, 0,myPathResolution);
				CCVector3 pd0 = new CCVector3(myDocumentPath,myPathIndex,  10);
				CCVector3 pd1 = new CCVector3(myDocumentPath,myPathIndex, -10);
				
				myDocumentPoints.add(pd0);
				myDocumentPoints.add(pd1);
				
				double td0= (d0 * myRatio + textOffset) % 1;
				double td1= (d1 * myRatio + textOffset) % 1;
				
				CCVector3 pt0 = new CCVector3(myTextPath,CCMath.blend(0d, myPathResolution, td0), 10);
				CCVector3 pt1 = new CCVector3(myTextPath,CCMath.blend(0d, myPathResolution, td0), -10);
				
				myTextPoints.add(pt0); 
				myTextPoints.add(pt1); 
			}

			myDocumentPath++;
			mySplines.add(myPoints);
			myDocumentSplines.add(myDocumentPoints);
			myTextSplines.add(myTextPoints);
		}
		
		for(int j = 0; j < myDocumentSplines.size();j++) {
			List<CCVector3> mySpline = mySplines.get(j);
			List<CCVector3> myDocumentSpline = myDocumentSplines.get(j);
			List<CCVector3> myTextSpline = myTextSplines.get(j);
			CCParticle myLast0 = null;
			CCParticle myLast1 = null;
			
			for(int i = 0; i < mySpline.size();i++) {
				CCVector3 myPoint = mySpline.get(i);
				CCVector3 myPosition = new CCVector3(myPoint.x,  _myTexture.height() - myPoint.y);

				CCParticle myParticle = _myEmitter.emit(myPosition, new CCVector3(), 30);
				myParticle.texCoords().set(myPosition.x / _myTexture.width(), myPosition.y / _myTexture.height(), 0, 0);
				myParticle.target().set(myPosition.x, myPosition.y, myPosition.z, 1);
				
				if (myLast0 != null) {
					_mySprings.addSpring(myParticle, myLast0);
				}
				if (myLast1 != null) {
					_mySprings.addSpring(myParticle, myLast1);
				}
				_myTriangleRenderer.addTriangle(myParticle, myLast0, myLast1);
			
				myLast1 = myLast0;
				myLast0 = myParticle;
				_myNewTargetParticles.add(myParticle);
				_myNewDocumentTargets.add(myDocumentSpline.get(i));
				_myNewTextTargets.add(myTextSpline.get(i));
				_myBlends.add(new CCVector3(CCMath.norm(j, 0, myDocumentSplines.size() - 1),CCMath.norm(i, 0, mySpline.size() - 1),CCMath.random()));
			}
		}
	}

	int myIndex = 0;
	double _myTextOffset = 0;

	@Override
	public void update(final CCAnimator theAnimator) {
		_myTextOffset += theAnimator.deltaTime() * _cTextPathSpeed;
		
		_myTargetForce.pathAdd(_cDocumentPathOffset * _cDocumentPathScale);
		_myTextTargetForce.pathAdd(_myTextOffset);
		_RealSenseForceField.update(theAnimator);
		_myParticles.update(theAnimator);
	}

	@CCProperty(name = "debug")
	private boolean _cDebug = true;

	@Override
	public void display(CCGraphics g) {
		g.clearColor(0);
		_RealSenseForceField.preDisplay(g);
		
		_myTargetForce.beginSetTargets(g);
		for(int i = 0; i < _myNewDocumentTargets.size();i++){
			CCParticle myParticle = _myNewTargetParticles.get(i);
			CCVector3 myParticleTarget = _myNewDocumentTargets.get(i);
			_myTargetForce.addTarget(myParticle, myParticleTarget);
		}
		_myTargetForce.endSetTargets(g);
		_myNewDocumentTargets.clear();
		
		_myTextTargetForce.beginSetTargets(g);
		for(int i = 0; i < _myNewTextTargets.size();i++){
			CCParticle myParticle = _myNewTargetParticles.get(i);
			CCVector3 myParticleTarget = _myNewTextTargets.get(i);
			_myTextTargetForce.addTarget(myParticle, myParticleTarget);
		}
		_myTextTargetForce.endSetTargets(g);
		_myNewTextTargets.clear();
		
		_myTextureBlend.beginSetBlends(g);
		for(int i = 0; i < _myBlends.size();i++){
			CCParticle myParticle = _myNewTargetParticles.get(i);
			CCVector3 myParticleBlends = _myBlends.get(i);
			_myTextureBlend.addBlend(myParticle, myParticleBlends.x, myParticleBlends.y, myParticleBlends.z);
		}
		_myTextureBlend.endSetBlends(g);
		_myBlends.clear();
		
		_myNewTargetParticles.clear();
		
		_myParticles.preDisplay(g);
		g.clear();
		g.pushMatrix();
//		g.ortho();
		_cCameraController.camera().draw(g);
		
		g.pushMatrix();
		g.scale(1,-1);
		g.image(_RealSenseForceField.forceField(),-g.width()/2, -g.height()/2, g.width(), g.height());
		g.popMatrix();
		

		g.color(255);
		g.image(_myTextureBlend.texture(), 0,0);

		g.blend();
		g.noDepthTest();
		g.color(0f, _cAlpha);
		_myParticles.display(g);
//
//		g.blend();
		g.popMatrix();
		
		
		
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
