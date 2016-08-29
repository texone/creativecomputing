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
package cc.creativecomputing.demo.simulation.gpuparticles.rendering;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.model.collada.CCColladaLoader;
import cc.creativecomputing.model.collada.CCColladaScene;
import cc.creativecomputing.model.collada.CCColladaSkeletonProvider;
import cc.creativecomputing.model.collada.CCColladaSkeletonSkin;
import cc.creativecomputing.model.collada.CCColladaSkinController;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.forces.target.CCSkeletonParticleTargets;
import cc.creativecomputing.simulation.particles.render.CCGPUPointSpriteRenderer;
import cc.creativecomputing.skeleton.CCSkeleton;

public class CCColladaModelDisplaceParticlesDemo extends CCApp {
	
	private class CCSkeletonParticles {
		
		@CCControl(name = "target force", min = 0, max = 2)
		private float _cTarget = 0;
		
		@CCControl(name = "target lookahead", min = 0, max = 10)
		private float _cTargetLookAhead = 0;
		
		@CCControl(name = "target max force", min = 0, max = 10)
		private float _cTargetMaxForce = 0;
		
		@CCControl(name = "target near max force", min = 0, max = 10)
		private float _cTargetNearMaxForce = 0;
		
		@CCControl(name = "target near distance", min = 0, max = 100)
		private float _cTargetNearDistance = 0;
		
		@CCControl(name = "gravity", min = 0, max = 10)
		private float _cGravity = 0;
		
		@CCControl(name = "noise force", min = 0, max = 2)
		private float _cNoise = 0;
		
		@CCControl(name = "noise speed", min = 0, max = 1)
		private float _cNoiseSpeed = 0;
		
		@CCControl(name = "noise scale", min = 0, max = 20)
		private float _cNoiseScale = 0;
		
		@CCControl(name = "point size", min = 0, max = 150)
		private int _cPointSize = 5;
		
		@CCControl(name = "floor y", min = -1000, max = 1000)
		private float _cFloorY = 0;
		
		@CCControl(name = "floor resilience", min = 0, max = 1)
		private float _cFloorResilience = 0;
		
		@CCControl(name = "floor friction", min = 0, max = 1)
		private float _cFloorFriction = 0;
		
		@CCControl(name = "min Velocity", min = 0, max = 1)
		private float _cFloorMinVel = 0;
		
		@CCControl(name = "blend target force", min = 0, max = 1)
		private float _cBlend = 0;
		
		@CCControl(name = "blend pow", min = 0.1f, max = 10)
		private float _cBlendPow = 0;
		
		@CCControl(name = "gradient range", min = 0, max = 1)
		private float _cGradientRange = 0;
		
		@CCControl(name = "gradient blend", min = 0, max = 1)
		private float _cGradientBlend = 0;
		
		@CCControl(name = "static blend", min = 0, max = 1)
		private float _cStaticBlend = 0;

		private CCParticles _myParticles;
		private CCForceField _myForceField = new CCForceField(0.005f,1,new CCVector3f(100,20,30));
		private CCGravity _myGravity;

		private CCTexture2D _myPointSpriteTexture;
		private CCGPUPointSpriteRenderer _myRenderer;
		
		public CCSkeletonParticles(CCApp theApp, CCShaderBuffer theTargets, int theXRes, int theYRes) {
			g = theApp.g;
			_myPointSpriteTexture = new CCTexture2D(CCTextureIO.newTextureData("spheres.png"));
			_myPointSpriteTexture.generateMipmaps(true);
			_myPointSpriteTexture.textureFilter(CCTextureFilter.LINEAR);
			_myPointSpriteTexture.textureMipmapFilter(CCTextureMipmapFilter.LINEAR);
			
			_myRenderer = new CCGPUPointSpriteRenderer(theApp.g, _myPointSpriteTexture, 5, 1);
			_myRenderer.pointSize(3);
			
			final List<CCForce> myForces = new ArrayList<CCForce>();
			myForces.add(new CCViscousDrag(0.3f));
			myForces.add(_myGravity = new CCGravity(new CCVector3f(0,-2,0)));
			myForces.add(_myForceField);
			
			final List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();
			
			CCGPUIndexParticleEmitter myEmitter;
			_myParticles = new CCParticles(theApp.g, _myRenderer, myForces, myConstraints, theXRes, theYRes);
			_myParticles.addEmitter(myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
			
			for(int i = 0; i < theXRes * theYRes; i++){
				myEmitter.emit(
					CCVecMath.random3f(400),
					CCVecMath.random3f(10),
					100000, true
				);
			}
			
			_myParticles.staticPositions(theTargets.attachment(0));
		}

		float _myTime = 0;
		
		public void update(float theDeltaTime) {
			_myForceField.strength(_cNoise);
			_myParticles.staticPositionBlend(_cStaticBlend);
			_myTime +=theDeltaTime * _cNoiseSpeed;
			
			_myParticles.update(theDeltaTime);
			
			_myForceField.noiseOffset(new CCVector3f(0,0,_myTime));
			_myForceField.noiseScale(_cNoiseScale / 100f);
			_myGravity.strength(_cGravity);
			

			_myRenderer.pointSize(_cPointSize);
//			_myRenderer.fadeOut(false);
		}
		
		public void draw(CCGraphics g) {
			g.noDepthTest();
			g.color(255);
//			g.blend(CCBlendMode.ADD);
			g.blend(CCBlendMode.ALPHA);
			_myParticles.draw();
			g.depthTest();
			g.blend();
		}
	}
	
	@CCControl(name = "time", min = 0, max = 2.0f)
	private float _cTime = 0;

	@CCControl(name = "x", min = -1000, max = 1000)
	private float _cX = 0;

	@CCControl(name = "y", min = -1000, max = 1000)
	private float _cY = 0;

	@CCControl(name = "z", min = -1000, max = 1000)
	private float _cZ = 0;
	
	private CCArcball _myArcball;
	
	private int _myXRes = 100;
	private int _myYRes = 100;
	
	private CCColladaSkeletonProvider _myColladaSkeletonProvider;
	private CCColladaSkeletonSkin _mySkeletonSkin;
	private CCSkeleton _mySkeleton;
	
	private CCSkeletonParticleTargets _myTriangleTargets;
	private CCSkeletonParticles _myParticles;

	public void setup() {
		_myArcball = new CCArcball(this);
		
		CCColladaLoader myColladaLoader = new CCColladaLoader("maya_dae_fbx.dae");
		
		CCColladaScene myScene = myColladaLoader.scenes().element(0);
		CCColladaSkinController mySkinController = myColladaLoader.controllers().element(0).skin();
		
		_myColladaSkeletonProvider = new CCColladaSkeletonProvider(
			myColladaLoader.animations().animations(),
			mySkinController,
			myScene.node("root")
		);
		_mySkeleton = _myColladaSkeletonProvider.skeleton();
		
		_mySkeletonSkin = new CCColladaSkeletonSkin(mySkinController);
		
		_myTriangleTargets = new CCSkeletonParticleTargets(
			this,
			_mySkeletonSkin,
			_mySkeleton,
			_myXRes, _myYRes
		);
		_myParticles = new CCSkeletonParticles(this, _myTriangleTargets.targets(), _myXRes, _myYRes);
		
		addControls("app", "position", this);
		addControls("app", "targets", _myTriangleTargets);
		addControls("app", "particles", _myParticles);
	}
	
	
	@Override
	public void update(float theDeltaTime) {
		_myColladaSkeletonProvider.time(_cTime);
		_myTriangleTargets.update(theDeltaTime);
		_myParticles.update(theDeltaTime);
	}

	public void draw() {
		g.clearColor(0);
		g.clear();
		
		g.translate(_cX, _cY, _cZ);
		_myArcball.draw(g);
		g.clearDepthBuffer();
		g.color(255,0,0);
		_mySkeleton.draw(g);
		
		_myParticles.draw(g);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCColladaModelDisplaceParticlesDemo.class);
		myManager.settings().size(1500, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
