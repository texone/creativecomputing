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
import cc.creativecomputing.demo.simulation.particles.realsense.CCRealSenseForceField;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.shader.CCShaderBufferDebugger;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.io.xml.CCXMLIO;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
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

public class CCParticlesSpringsDrawDemo extends CCGL2Adapter {
	private List<CCGesture> _myGestures = new ArrayList<>();

	private void loadStrokes(int w, int h) {
		CCDataElement myXML = CCXMLIO.createXMLElement(CCNIOUtil.dataPath("strokes.xml"));
		for (CCDataElement myStrokeXML : myXML) {
			CCGesture myStroke = new CCGesture(w, h);
			for (CCDataElement myPointXML : myStrokeXML) {
				// if(myPointXML.getFloat("x") == 0 && myPointXML.getFloat("y") == 0)continue;
				myStroke.addPoint(
					myPointXML.doubleAttribute("x"), 
					myPointXML.doubleAttribute("y"),
					myPointXML.doubleAttribute("p")
				);
			}
			_myGestures.add(myStroke);
		}
	}

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
		myForces.add(new CCTextureForceField2D(_RealSenseForceField.forceField(), new CCVector2(1920d, -1080d), new CCVector2(0.5, 0.5)));

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

		loadStrokes(g.width(), g.height());

		keyPressed().add(e -> {
			switch (e.keyCode()) {
			case VK_R:
//				_myParticles.reset(g);
//				
//				double myXspace = 1500f / _myXres;
//				double myYspace = 1500f / _myYres;
//				
//				CCParticle[] myLeftParticles = new CCParticle[_myYres];
//				
////				List<Integer> _myIndices = new ArrayList<Integer>();
////				
//				for(int x = 0; x < _myXres; x++){
//					CCParticle myParticleAbove = null;
//					for(int y = 0; y < _myYres; y++){
//						CCParticle myParticle = _myEmitter.emit(
//							new CCVector3(x * myXspace - 750, 0, y * myYspace - 750),
//							new CCVector3(),
//							3000, true
//						);
//						
//						if(myParticleAbove != null) {
////							_mySprings.addSpring(myParticleAbove, myParticle, myYspace);
//						}
//						
//						if(myLeftParticles[y] != null) {
////							_mySprings.addSpring(myLeftParticles[y], myParticle, myXspace);
//						}
//
//						myParticleAbove = myParticle;
//						myLeftParticles[y] = myParticle;
//					}
//				}
//				break;
			default:
			}
		});

		width = g.width();
		height = g.height();
		
		for(CCGesture myGesture:_myGestures) {
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
		}
	}

	int myIndex = 0;

	@Override
	public void update(final CCAnimator theAnimator) {
		_RealSenseForceField.update(theAnimator);

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
**/
		myIndex++;
		myIndex %= _myGestures.size();

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
		//_cCameraController.camera().draw(g);
		
		g.pushMatrix();
		g.scale(1,-1);
		g.image(_RealSenseForceField.forceField(),-g.width()/2, -g.height()/2, g.width(), g.height());
		g.popMatrix();

		g.blend();
		g.noDepthTest();
		g.color(0f, _cAlpha);
		_myParticles.display(g);

		g.blend();
		g.popMatrix();

		g.color(1d);
//		CCLog.info(g.vendor());
		_myDebugger.display(g);
		_myParticleDebugger.display(g);
	}

	public static void main(String[] args) {
		CCGL2Application myAppManager = new CCGL2Application(new CCParticlesSpringsDrawDemo());
		myAppManager.glcontext().size(1800, 1368);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
