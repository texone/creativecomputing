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
package cc.creativecomputing.demo.simulation.gpuparticles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.CCTesselator;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.signal.CCSignal;
import cc.creativecomputing.math.signal.CCSimplexNoise;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCGPUTextureForceField;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;

public class CCPathFollowTest2 extends CCApp {

	@CCControl(name = "texture alpha", min = 0, max = 1)
	private float _cTextureAlpha = 0;

	@CCControl(name = "force scale", min = 0, max = 20)
	private float _cForceScale = 0;
	
	@CCControl(name = "contourWeight", min = 0, max = 20)
	private float _cContourWeight = 0;
	
	@CCControl(name = "noiseSpeed", min = 0, max = 1)
	private float _cNoiseSpeed = 0;

	private CCShaderBuffer _myPathForceFieldTexture;

	private CCTesselator _myTesselator;

	private CCCGShader _myContourShader;

	private CCSignal _myNoise;

	private float _myTime = 0;

	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCGPUTextureForceField _myForceField;

	private CCArcball _myArcball;

	@Override
	public void setup() {

		addControls("app", "app", this);
		// frameRate(30);
		_myPathForceFieldTexture = new CCShaderBuffer(400, 400);

		_myContourShader = new CCCGShader(
			CCIOUtil.classPath(CCParticles.class,"shader/contour.vp"), 
			CCIOUtil.classPath(CCParticles.class,"shader/contour.fp")
		);
		_myContourShader.load();

		_myNoise = new CCSimplexNoise();
		g.strokeWeight(10);

		_myTesselator = new CCTesselator();

		_myForceField = new CCGPUTextureForceField(_myPathForceFieldTexture.attachment(0), new CCVector2f(1, 1), new CCVector2f(200, 200));
		final List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(_myForceField);
		myForces.add(new CCViscousDrag(0.3f));
		// myForces.add(new CCGPUGravity(new CCVector3f(50,0,0)));
		_myParticles = new CCParticles(g, myForces, new ArrayList<CCGPUConstraint>(), 700, 700);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		// _myParticles.make2D(true);

		_myArcball = new CCArcball(this);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myTime += theDeltaTime * _cNoiseSpeed;
		for (int i = 0; i < 500; i++) {
			_myEmitter.emit(new CCVector3f(-width / 2, CCMath.random(-50, 50), 0), new CCVector3f(20, 0, 0), 30);
		}
		_myForceField.strength(_cForceScale);

		_myParticles.update(theDeltaTime);
	}

	@Override
	public void draw() {
		_myPathForceFieldTexture.beginDraw();
		g.clearColor(0);
		g.clear();
		_myContourShader.start();

		_myTesselator.beginPolygon();
		_myTesselator.beginContour();
		for (int i = 400; i >= 0; i -= 5) {
			_myTesselator.normal(0, 1, 0);
			_myTesselator.vertex(i, 0);
		}
		for (int i = 0; i <= 400; i += 5) {
			_myTesselator.normal(0, -1, 0);
			_myTesselator.vertex(i, 400);
		}
		_myTesselator.endContour();

		_myTesselator.beginContour();
		float myLastNoise = 200 + (_myNoise.value(404 * 0.005f, _myTime) - 0.5f) * 200;
		for (int i = 400; i >= 0; i -= 4) {
			float myNoise = 200 + (_myNoise.value(i * 0.005f, _myTime) - 0.5f) * 200;
			CCVector2f myNormal = new CCVector2f(4, myNoise - myLastNoise).normalize();
			_myTesselator.normal(myNormal.y, myNormal.x, 0);
			_myTesselator.vertex(i, myNoise-10);
			myLastNoise = myNoise;
		}
		myLastNoise = 200 + (_myNoise.value(-4 * 0.005f, _myTime) - 0.5f) * 200;
		for (int i = 0; i <= 400; i += 4) {
			float myNoise = 200 + (_myNoise.value(i * 0.005f, _myTime) - 0.5f) * 200;
			CCVector2f myNormal = new CCVector2f(4, myNoise - myLastNoise).normalize();
			_myTesselator.normal(myNormal.y, -myNormal.x, 0);
			_myTesselator.vertex(i, myNoise + 1);
			myLastNoise = myNoise;
		}
		_myTesselator.endContour();
		_myTesselator.endPolygon();
		g.polygonMode(CCPolygonMode.FILL);

		g.strokeWeight(_cContourWeight);
		g.beginShape(CCDrawMode.LINES);
		myLastNoise = 200 + (_myNoise.value(-4 * 0.005f, _myTime) - 0.5f) * 200;
		for (int i = 0; i < 400; i += 4) {
			float myNoise = 200 + (_myNoise.value(i * 0.005f, _myTime) - 0.5f) * 200;
			g.normal(new CCVector3f(4, myNoise - myLastNoise).normalize());
			g.vertex(i - 4, myLastNoise);
			g.vertex(i, myNoise);
			myLastNoise = myNoise;
		}
		g.endShape();

		_myContourShader.end();
		_myPathForceFieldTexture.endDraw();
		//
		g.strokeWeight(1f);
		_myArcball.draw(g);
		g.clearColor(0);
		g.clear();
		g.blend(CCBlendMode.ADD);
		g.color(0, 25);
		// g.clearDepthBuffer();
		// g.rect(-1000,-1000,2000,2000);
		g.color(1f, _cTextureAlpha);
		g.image(_myPathForceFieldTexture.attachment(0), -200, -200);
		
		g.color(255, 10);
		g.noDepthTest();
		// g.noBlend();
		_myParticles.draw();
		g.blend();

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCPathFollowTest2.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
