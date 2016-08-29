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
package cc.creativecomputing.demo.simulation.gpuparticles.target;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCGPUParticle;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.forces.blend.CCGPUTimeForceBlend;
import cc.creativecomputing.simulation.particles.forces.target.CCGPUTargetForce;
import cc.creativecomputing.simulation.particles.render.CCGPUIndexedParticleRenderer;
import cc.creativecomputing.util.CCFormatUtil;
/**
 * @author christianriekoff
 *
 */
public class CCDynamicColoredTargets extends CCApp{
	
	// CIRCLE SETTINGS

	@CCControl(name = "center x", min = -1000, max = 1000)
	private float _cEmitX = 0;
	@CCControl(name = "center y", min = -1000, max = 1000)
	private float _cEmitY = 0;
	@CCControl(name = "open speed", min = 0, max = 300)
	private float _cOpenSpeed = 0;
	@CCControl(name = "start radius", min = 0, max = 300)
	private float _cStartRadius;
	@CCControl(name = "radius range", min = 0, max = 1000)
	private float _cRadiusRange;
	@CCControl(name = "particle perimeter density", min = 0.1f, max = 1)
	private float _cParticlePerimeterDensity = 0;
	
	// FORCE FIELD SETTINGS

	@CCControl(name = "n scale", min = 0, max = 1)
	private float _cNScale = 0;
	@CCControl(name = "n strength", min = 0, max = 1)
	private float _cNStrength = 0;
	@CCControl(name = "n speed", min = 0, max = 3)
	private float _cNSpeed = 0;
	
	// EMIT SETTINGS
	

	@CCControl(name = "emit random", min = 0, max = 100)
	private float _cEmitRandomPos = 0;
	@CCControl(name = "emit velocity rand", min = 0, max = 100)
	private float _cEmitRandomVel = 0;
	@CCControl(name = "emit start velocity", min = -10, max = 10)
	private float _cEmitStartVelocity = 0;
	@CCControl(name = "life time", min = 0, max = 30)
	private float _cLifeTime = 3f;
	
	// TARGET SETTINGS
	
	@CCControl(name = "target x", min = -1000, max = 1000)
	private float _cTargetX = 0;
	@CCControl(name = "target y", min = -1000, max = 1000)
	private float _cTargetY = 0;
	@CCControl(name = "emit target look ahead", min = 0, max = 5)
	private float _cEmitTargetLookAhead = 0;

	@CCControl(name = "target strength", min = 0, max = 1, external = true)
	private float _cTargetStrength = 0;
	@CCControl(name = "target lookahead", min = 0, max = 10)
	private float _cLookAhead = 0;
	@CCControl(name = "target max force", min = 0, max = 10, external = true)
	private float _cMaxForce = 0;
	@CCControl(name = "target near distance", min = 0, max = 200)
	private float _cTargetNearDistance = 0;
	@CCControl(name = "target near max force", min = 0, max = 10)
	private float _cTargetNearMaxForce = 0;
	



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
	@CCControl(name = "life time Blend pow", min = 0, max = 10)
	private float _cLifeTimeBlendPow = 1;
	

	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;

	private CCGPUTimeForceBlend _myTimeBlendForce;

	private CCGravity _myGravity;
	private CCForceField _myForceField;
	private CCGPUTargetForce _myTargetForce = new CCGPUTargetForce();
	private CCShaderBuffer _myTargetBuffer;
	
	private CCTextureData _myTargetTextureData;
	
	private CCCGShader _myInitValueShader;


	@Override
	public void setup() {
		List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(_myGravity = new CCGravity(new CCVector3f()));
		myForces.add(new CCViscousDrag(0.1f));
		
		_myTimeBlendForce = new CCGPUTimeForceBlend(0,4, _myForceField = new CCForceField(0.01f, 1f, new CCVector3f()), _myTargetForce);
		_myTimeBlendForce.blend(0.005f, 1f);
		_myTimeBlendForce.power(6);
		myForces.add(_myTimeBlendForce);

		_myParticles = new CCParticles(g, new CCGPUIndexedParticleRenderer(),myForces, new ArrayList<CCGPUConstraint>(), 700, 700);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));

		addControls("app", "app", this);

		_myTargetBuffer = new CCShaderBuffer(16,4, _myParticles.width(),_myParticles.height());
		_myTargetBuffer.beginDraw();
		g.clear();
		_myTargetBuffer.endDraw();
		_myTargetForce.addTargetSetup(_myTargetBuffer);
		
		_myInitValueShader = new CCCGShader(
			null, 
			CCIOUtil.classPath(CCParticles.class, "shader/initvalue.fp")
		);
		_myInitValueShader.load();
		
		_myTargetTextureData = CCTextureIO.newTextureData("lena.png");
	}

	float _myOffset = 0;
	
	private float _myRadius = 0;

	@Override
	public void update(final float theDeltaTime) {
		
		_myRadius += theDeltaTime * _cOpenSpeed;
		if(_myRadius > _cStartRadius + _cRadiusRange){
			_myRadius = _cStartRadius;
		}
		
		CCVector3f myVelocity = new CCVector3f();
		
		float myPerimeter = CCMath.TWO_PI * _myRadius;
		float myNumberOfParticles = myPerimeter / _cParticlePerimeterDensity;
		float myStep = 360 / myNumberOfParticles;
		
		_myInitValueShader.start();
		_myTargetBuffer.beginDraw();
		
		float myTargetX2 = _myTargetTextureData.width() + _cTargetX;
		float myTargetY2 = _myTargetTextureData.height() + _cTargetY;
		
		
		g.beginShape(CCDrawMode.POINTS);
		for(float angle = 0; angle < 360; angle += myStep){
			float radAng = CCMath.radians(angle);
			float myX = CCMath.sin(radAng) * _myRadius;
			float myY = CCMath.cos(radAng) * _myRadius;
			
			myVelocity.set(myX, myY, 0);
			myVelocity.normalize();
			myVelocity.scale(_cOpenSpeed * _cEmitStartVelocity);
			myVelocity.add(CCMath.random(-_cEmitRandomVel, _cEmitRandomVel), CCMath.random(-_cEmitRandomVel, _cEmitRandomVel), 0);
			
			myX += _cEmitX + CCMath.random(-_cEmitRandomPos, _cEmitRandomPos);
			myY += _cEmitY + CCMath.random(-_cEmitRandomPos, _cEmitRandomPos);
			
			float myTargetX = myX + myVelocity.x * _cEmitTargetLookAhead;
			float myTargetY = myY + myVelocity.y * _cEmitTargetLookAhead;
			if(myTargetX <= _cTargetX || myTargetX >= myTargetX2 || myTargetY <= _cTargetY || myTargetY >= myTargetY2){
				continue;
			}
			
			int myColorLookUpX = (int)(myTargetX - _cTargetX);
			int myColorLookUpY = (int)(myTargetY - _cTargetY);
			
			CCColor myParticleColor = _myTargetTextureData.getPixel(myColorLookUpX, myColorLookUpY);
			
			CCGPUParticle myParticle = _myEmitter.emit(myParticleColor, new CCVector3f(myX, myY), myVelocity, _cLifeTime, false);
			if(myParticle == null)break;
							
			g.textureCoords(0, myTargetX, myTargetY);
			g.vertex(myParticle.x() + 0.5f, myParticle.y() + 0.5f);
			
			
		}
		g.endShape();
		_myTargetBuffer.endDraw();
		_myInitValueShader.end();

		_myOffset += theDeltaTime * _cNSpeed;

		_myGravity.direction().set(_cX, _cY, _cZ);
		_myGravity.strength(_cGStrength);

		_myForceField.noiseScale(_cNScale);
		_myForceField.strength(_cNStrength);
		_myForceField.noiseOffset(new CCVector3f(0, 0, _myOffset));
		

		_myTargetForce.strength(_cTargetStrength);
		_myTargetForce.lookAhead(_cLookAhead);
		_myTargetForce.maxForce(_cMaxForce);
		_myTargetForce.nearDistance(_cTargetNearDistance);
		_myTargetForce.nearMaxForce(_cTargetNearMaxForce);

		_myTimeBlendForce.power(_cLifeTimeBlendPow);

		_myParticles.update(theDeltaTime * 2);
	}

	@Override
	public void draw() {
		g.clear();

//		g.color(255,50);
//		g.ellipse(_cEmitX, _cEmitY, _myRadius * 2,_myRadius * 2);
//		g.color(255);
//		g.ellipse(_cTargetX, _cTargetY, 20,20);
//		g.image(_myTargetTexture, _cTargetX, _cTargetY);
		
		g.noDepthTest();
		g.pointSize(0.1f);
		g.color(1f, _cAlpha);
		_myParticles.draw();
		// g.ellipse(_myAttractor.position(), _myAttractor.radius());
		g.noBlend();
		
		g.color(255);
//		g.image(_myParticles.dataBuffer().attachment(3), -width/2,-height/2);
		

	}

	private int i = 0;

	public void keyPressed(CCKeyEvent theEvent) {
		switch (theEvent.keyCode()) {
		case VK_R:
			_myParticles.reset();
			break;
		case VK_S:
			CCScreenCapture.capture("export/orchid/" + CCFormatUtil.nf(i++, 4) + ".png", width, height);
			break;
		default:
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCDynamicColoredTargets.class);
		myManager.settings().size(1400, 900);
		myManager.settings().vsync(true);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
