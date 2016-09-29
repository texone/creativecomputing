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
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
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
import cc.creativecomputing.simulation.particles.forces.CCGPUCombinedForce;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.forces.blend.CCGPUTimeForceBlend;
import cc.creativecomputing.simulation.particles.forces.target.CCGPUTargetForce;

public class CCDynamicIndividualTargetsDemo extends CCApp {
	
	private class SAKidsBackground{

		@CCControl(name = "open speed", min = 0, max = 100)
		private float _cOpenSpeed = 0;
		@CCControl(name = "start radius", min = 0, max = 300)
		private float _cStartRadius;
		@CCControl(name = "radius range", min = 0, max = 1000)
		private float _cRadiusRange;
		@CCControl(name = "particle perimeter density", min = 0.1f, max = 20)
		private float _cParticlePerimeterDensity = 0;
		

		
		@CCControl(name = "drag", min = 0, max = 1)
		private float _cDrag = 0;
			
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
		@CCControl(name = "look up look ahead", min = 0, max = 10)
		private float _cLookUpLookAhead = 0;
		
		@CCControl(name = "life time Blend pow", min = 0, max = 10)
		private float _cLifeTimeBlendPow = 1;
		
		@CCControl(name = "n scale", min = 0, max = 1)
		public float _cNScale = 0;
		
		@CCControl(name = "n strength", min = 0, max = 1)
		public float _cStrength = 0;
		
		@CCControl(name = "n speed", min = 0, max = 3)
		public float _cSpeed = 0;
		
		@CCControl(name = "emit x", min = -1000, max = 1000)
		private float _cEmitX = 0;
		@CCControl(name = "emit y", min = -1000, max = 1000)
		private float _cEmitY = 0;
		@CCControl(name = "emit random", min = 0, max = 100)
		private float _cEmitRandomPos = 0;
		@CCControl(name = "emit velocity rand", min = 0, max = 100)
		private float _cEmitRandomVel = 0;
		@CCControl(name = "emit start velocity", min = -10, max = 10)
		private float _cStartVelocity = 0;
		
		@CCControl(name = "target x", min = -1000, max = 1000)
		private float _cTargetX = 0;
		@CCControl(name = "target y", min = -1000, max = 1000)
		private float _cTargetY = 0;
		@CCControl(name = "emit target look ahead", min = 0, max = 5)
		private float _cEmitTargetLookAhead = 0;
		
		
		private CCGPUIndexParticleEmitter _myEmitter;
		private CCParticles _myParticles;
		
		private CCForceField _myForceField;
		private CCViscousDrag _myViscousDrag;
		private CCGPUCombinedForce _myCombinedForce;
		private CCGPUTargetForce _myTargetForce = new CCGPUTargetForce();
		private CCShaderBuffer _myTargetBuffer;
		private CCGPUTimeForceBlend _myTimeBlendForce;
		
		private float _myRadius = 0;
		
		private CCTextureData _myTargetTextureData;

		private CCCGShader _myInitValueShader;
		
		public SAKidsBackground(){
			List<CCForce> myCobinedForces = new ArrayList<CCForce>();
			myCobinedForces.add(new CCGravity(new CCVector3f()));
			myCobinedForces.add(_myForceField = new CCForceField(0.01f, 1f, new CCVector3f()));
			_myCombinedForce = new CCGPUCombinedForce(myCobinedForces);
			
			_myTimeBlendForce = new CCGPUTimeForceBlend(0,4, _myCombinedForce, _myTargetForce);
			_myTimeBlendForce.blend(0.005f, 1f);
			_myTimeBlendForce.power(6);
				
			_myTargetTextureData = CCTextureIO.newTextureData("squarepusher.png");

			List<CCForce> myForces = new ArrayList<CCForce>();
//			myForces.add(_myTimeBlendForce);
			myForces.add(_myTargetForce);
			myForces.add(_myForceField = new CCForceField(0.01f, 1f, new CCVector3f()));
			myForces.add(_myViscousDrag = new CCViscousDrag(0.1f));
			_myParticles = new CCParticles(g, myForces,new ArrayList<CCGPUConstraint>(), 700, 700);
			_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
			
			
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
		}
		
		private float _myOffset = 0;
		
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
				myVelocity.scale(_cOpenSpeed * _cStartVelocity);
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
				
				CCGPUParticle myParticle = _myEmitter.emit(myParticleColor, new CCVector3f(myX, myY), myVelocity, 10, false);
								
				g.textureCoords(0, myTargetX, myTargetY);
				g.vertex(myParticle.x() + 0.5f, myParticle.y() + 0.5f);
				
				
			}
			g.endShape();
			_myTargetBuffer.endDraw();
			_myInitValueShader.end();
			
			_myTargetForce.strength(_cTargetStrength);
			_myTargetForce.lookAhead(_cLookAhead);
			_myTargetForce.maxForce(_cMaxForce);
			_myTargetForce.nearDistance(_cTargetNearDistance);
			_myTargetForce.nearMaxForce(_cTargetNearMaxForce);
				
					
			_myOffset += theDeltaTime * _cSpeed;
			_myForceField.noiseScale(_cNScale);
			_myForceField.strength(_cStrength);
			_myForceField.noiseOffset(new CCVector3f(0,0,_myOffset));
			_myTimeBlendForce.power(_cLifeTimeBlendPow);
			
			_myViscousDrag.drag(_cDrag);

			_myParticles.update(theDeltaTime);
		}
		
		public void draw(CCGraphics g){
			g.ellipse(_cEmitX, _cEmitY, 20,20);
			g.ellipse(_cTargetX, _cTargetY, 20,20);
			g.pointSize(0.1f);
			_myParticles.draw();
		}
	}
	

	private SAKidsBackground _myBackground;

	@Override
	public void setup() {
		_myBackground = new SAKidsBackground();
		
		addControls("background", "background", _myBackground);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myBackground.update(theDeltaTime);
	}
	
	@Override
	public void draw() {
		g.clear();
		_myBackground.draw(g);
	}
	
	
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCDynamicIndividualTargetsDemo.class);
		myManager.settings().size(1500, 812);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

