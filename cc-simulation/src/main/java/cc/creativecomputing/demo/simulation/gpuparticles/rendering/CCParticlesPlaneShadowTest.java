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

import javax.media.opengl.GL2;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCGPUParticleSort;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.render.CCGPUDisplayShader;
import cc.creativecomputing.simulation.particles.render.CCGPUIndexedParticleRenderer;
import cc.creativecomputing.simulation.particles.render.CCGPUSortedParticleRenderer;

public class CCParticlesPlaneShadowTest extends CCApp {
	
	private class CCGPUShadowPlaneRenderer extends CCGPUIndexedParticleRenderer{

		private CCGPUParticleSort _mySort;
		
		public CCGPUShadowPlaneRenderer(CCGraphics theGraphics, CCGPUParticleSort theSort) {
			super(
				CCIOUtil.classPath(CCGPUDisplayShader.class, "shader/planeshadow/sorted_display_vertex.glsl"),
				CCIOUtil.classPath(CCGPUDisplayShader.class, "shader/planeshadow/sorted_display_fragment.glsl")
			);
			_mySort = theSort;
		}
		
		@Override
		public void setup(CCParticles theParticles) {
			super.setup(theParticles);
		}
		
		@Override
		public void update(float theDeltaTime) {
		}
		
		@Override
		public void draw(CCGraphics g) {
			g.gl.glEnable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE);
			_myShader.start();
			g.texture(0, _myParticles.dataBuffer().attachment(0));
			g.texture(1, _myParticles.dataBuffer().attachment(1));
			g.texture(2, _mySort.indices().attachment(0));
			_myShader.uniform1i("positions", 0);
			_myShader.uniform1i("infos", 1);
			_myShader.uniform1i("indices", 2);
			_myShader.uniform1f("tanHalfFOV", CCMath.tan(g.camera().fov()) * g.height);
			_myShader.uniform1f("pointSize", _myPointsize);
			_myShader.uniform3f("planeNormal", new CCVector3f(_cNormalX,_cNormalY,_cNormalZ).normalize());
			_myShader.uniform1f("planeConstant", _cConstant);
			_myShader.uniform3f("lightDirection", new CCVector3f(_cLightX,_cLightY,_cLightZ));
			_myMesh.draw(g);
			g.noTexture();
			_myShader.end();
			g.gl.glDisable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE) ;
		}
	}
	
	private CCParticles _myParticles;
	private CCGPUSortedParticleRenderer _myRenderer;
	private CCGPUShadowPlaneRenderer _myShadowRenderer;
	private CCGPUIndexParticleEmitter _myEmitter; 
	private CCArcball _myArcball;
	
	private CCForceField _myForceField = new CCForceField(0.005f,1,new CCVector3f(100,20,30));
	

	@CCControl(name = "pointsize", min = 1, max = 10)
	private float _cPointSize = 1;

	@CCControl(name = "alpha", min = 0, max = 1)
	private float _cAlpha = 1;
	@CCControl(name = "shadow alpha", min = 0, max = 1)
	private float _cShadowAlpha = 1;
	
	@CCControl(name = "light x", min = -1000, max = 1000)
	private float _cLightX = 1;
	@CCControl(name = "light y", min = -1000, max = 1000)
	private float _cLightY = 1;
	@CCControl(name = "light z", min = -1000, max = 1000)
	private float _cLightZ = 1;
	
	@CCControl(name = "normal x", min = -1, max = 1)
	private float _cNormalX = 1;
	@CCControl(name = "normal y", min = -1, max = 1)
	private float _cNormalY = 1;
	@CCControl(name = "normal z", min = -1, max = 1)
	private float _cNormalZ = 1;
	@CCControl(name = "planeconstant ", min = -1000, max = 1000)
	private float _cConstant = 1;
	
	public void setup() {
		
		addControls("app", "app", this);
		_myArcball = new CCArcball(this);
		
		final List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(new CCViscousDrag(0.3f));
		myForces.add(_myForceField);
		
		_myRenderer = new CCGPUSortedParticleRenderer(g);
		_myParticles = new CCParticles(g, _myRenderer, myForces, new ArrayList<CCGPUConstraint>(), 256, 256);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));

		_myShadowRenderer = new CCGPUShadowPlaneRenderer(g, _myRenderer.sort());
		_myShadowRenderer.setup(_myParticles);
		g.clearColor(255);
		
	}
	
	private float _myTime = 0;
	
	public void update(final float theDeltaTime){
		_myTime += 1/30f * 0.5f;
		
		
		_myForceField.noiseOffset(new CCVector3f(0,0,_myTime));
		_myForceField.noiseScale(0.0025f);
		
		for(int i = 0; i < 100; i++){
			_myEmitter.emit(
				new CCVector3f(
					CCMath.random(-width/2, width/2),
					0,
					100
//					CCMath.map(i, 0, _myParticles.size(),height/2, -height/2)
				),
				CCVecMath.random3f(10),
				10, false
			);
		}
		_myParticles.update(theDeltaTime);
		
//		_myRenderer.update(theDeltaTime);
		
		_myRenderer.pointSize(_cPointSize);
	}

	public void draw() {
		g.clear();
		g.pushMatrix();
		_myArcball.draw(g);
		g.blend();
//		g.pointSprite(_mySpriteTexture);
//		g.smooth();
		g.blend();
		g.color(0, _cShadowAlpha);
		g.noDepthTest();
		_myShadowRenderer.draw(g);
		g.depthTest();
		g.clearDepthBuffer();
		g.color(0.8f,0,0,_cAlpha);
		_myParticles.draw();
//		g.noSmooth();
//		g.noPointSprite();
		g.popMatrix();
		g.color(255,0,0);
		g.clearDepthBuffer();
		g.text(frameRate + ":" + _myEmitter.particlesInUse(),-width/2+20,-height/2+20);
	}
	
	public void keyPressed(CCKeyEvent theEvent) {
//		_myParticles.reset();
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesPlaneShadowTest.class);
		myManager.settings().size(1200, 600);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
