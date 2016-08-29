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
package cc.creativecomputing.graphics.shader.postprocess.deferred;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCRenderBuffer;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.postprocess.CCGeometryBuffer;
import cc.creativecomputing.graphics.shader.postprocess.CCPostProcessEffect;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

/**
 * @author christianriekoff
 *
 */
public class CCDeferredShading extends CCPostProcessEffect{
	
	private CCRenderBuffer	_myRenderBuffer;
	
	private CCGLProgram _myDirectionalLightShader;
	private CCGLProgram _myPointLightShader;
	private CCGLProgram _mySpotLightShader;
	
	private List<CCDirectionalLight> _myDirectionalLights = new ArrayList<CCDirectionalLight>();
	private List<CCPointLight> _myPointLights = new ArrayList<CCPointLight>();
	private List<CCSpotLight> _mySpotLights = new ArrayList<CCSpotLight>();
	
	private int _myWidth;
	private int _myHeight;
	
	private CCGraphics _myGraphics;
	
	public CCDeferredShading(CCGraphics g) {
		_myGraphics = g;
		_myDirectionalLightShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "directionalLight_vert.glsl"),
			CCNIOUtil.classPath(this, "directionalLight.glsl")
		);
		
		_myPointLightShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "pointLight_vert.glsl"),
			CCNIOUtil.classPath(this, "pointLight.glsl")
		);
		
		_mySpotLightShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "spotLight_vert.glsl"),
			CCNIOUtil.classPath(this, "spotLight.glsl")
		);
	}
	
	public void add(CCDirectionalLight theDirectionalLight) {
		_myDirectionalLights.add(theDirectionalLight);
	}
	
	public void add(CCPointLight thePointLight) {
		_myPointLights.add(thePointLight);
	}
	
	public void add(CCSpotLight theSpotLight){
		_mySpotLights.add(theSpotLight);
	}
	
	@Override
	public void initialize(int theWidth, int theHeight) {
		_myRenderBuffer = new CCRenderBuffer(_myGraphics, theWidth, theHeight);
		_myWidth = theWidth;
		_myHeight = theHeight;
	}
	
	private void applyDirectionalLights() {
		_myDirectionalLightShader.start();
		_myDirectionalLightShader.uniform3f("inCamPosition", _myGraphics.camera().position());
		_myDirectionalLightShader.uniform1i("positions", 0);
		_myDirectionalLightShader.uniform1i("normals", 1);
		_myDirectionalLightShader.uniform2f("screenSize", _myWidth, _myHeight);
		
		for(CCDirectionalLight myDirectionalLight:_myDirectionalLights) {
			CCVector3 myDirection = myDirectionalLight.lightDirection().clone().normalize().negate();
			_myDirectionalLightShader.uniform3f("lightDirection", myDirection);
			_myDirectionalLightShader.uniform3f(
				"lightColor", 
				myDirectionalLight.color().r, 
				myDirectionalLight.color().g, 
				myDirectionalLight.color().b
			);
			_myDirectionalLightShader.uniform1f("specularPower", myDirectionalLight.specularPower());
			_myDirectionalLightShader.uniform1f("specularIntensity", myDirectionalLight.specularIntensity());
			
			_myGraphics.beginShape(CCDrawMode.QUADS);
			_myGraphics.vertex(-_myWidth/2, -_myHeight/2);
			_myGraphics.vertex( _myWidth/2, -_myHeight/2);
			_myGraphics.vertex( _myWidth/2,  _myHeight/2);
			_myGraphics.vertex(-_myWidth/2,  _myHeight/2);
			_myGraphics.endShape();
		}
		
		_myDirectionalLightShader.end();
	}
	
	private void applyPointLights() {
		_myPointLightShader.start();
		_myPointLightShader.uniform3f("inCamPosition", _myGraphics.camera().position());
		_myPointLightShader.uniform1i("positions", 0);
		_myPointLightShader.uniform1i("normals", 1);
		_myPointLightShader.uniform2f("screenSize", _myWidth, _myHeight);
		
		for(CCPointLight myPointLight:_myPointLights) {
			CCVector3 myPosition = myPointLight.position();
			_myPointLightShader.uniform3f("position", myPosition);
			_myPointLightShader.uniform3f(
				"lightColor", 
				myPointLight.color().r, 
				myPointLight.color().g, 
				myPointLight.color().b
			);
			_myPointLightShader.uniform1f("intensity", myPointLight.intensity());
			_myPointLightShader.uniform1f("radius", myPointLight.radius() / 2);

			_myPointLightShader.uniform1f("specularPower", myPointLight.specularPower());
			_myPointLightShader.uniform1f("specularIntensity", myPointLight.specularIntensity());
			
			double myLightX = myPointLight.position().x;
			double myLightY = myPointLight.position().y;
			double myLightZ = myPointLight.position().z;

			_myGraphics.beginShape(CCDrawMode.QUADS);
			_myGraphics.vertex(myLightX - myPointLight.radius(), myLightY - myPointLight.radius(), myLightZ);
			_myGraphics.vertex(myLightX + myPointLight.radius(), myLightY - myPointLight.radius(), myLightZ);
			_myGraphics.vertex(myLightX + myPointLight.radius(), myLightY + myPointLight.radius(), myLightZ);
			_myGraphics.vertex(myLightX - myPointLight.radius(), myLightY + myPointLight.radius(), myLightZ);
			_myGraphics.endShape();
		}
		
		_myPointLightShader.end();
	}

	private void applySpotLights() {
		_mySpotLightShader.start();
		_mySpotLightShader.uniform1i("positions", 0);
		_mySpotLightShader.uniform1i("normals", 1);
		_mySpotLightShader.uniform2f("screenSize", _myWidth, _myHeight);
		
		for(CCSpotLight mySpotLight:_mySpotLights) {
			CCVector3 myPosition = mySpotLight.position();
			_mySpotLightShader.uniform3f("position", myPosition);
			_mySpotLightShader.uniform3f("direction", mySpotLight.direction());
			_mySpotLightShader.uniform3f(
				"lightColor", 
				mySpotLight.color().r, 
				mySpotLight.color().g, 
				mySpotLight.color().b
			);
			_mySpotLightShader.uniform1f("intensity", mySpotLight.intensity());
			_mySpotLightShader.uniform1f("radius", mySpotLight.radius());
			_mySpotLightShader.uniform1f("spotDecayExponent", mySpotLight.spotDecayExponent());
			_mySpotLightShader.uniform1f("spotLightAngleCosine", CCMath.cos(CCMath.radians(mySpotLight.coneAngle())));
			float myHeight = CCMath.sin(CCMath.radians(mySpotLight.coneAngle())) * mySpotLight.radius();
			
			CCVector3 myFirst = mySpotLight.direction().perp().normalize().multiply(myHeight);
			CCVector3 mySecond = myFirst.cross(mySpotLight.direction()).normalize().multiply(myHeight);
			CCVector3 myEnd = mySpotLight.position().add(mySpotLight.direction().normalize().multiply(mySpotLight.radius()));
			
			CCVector3 myA = myEnd.add(myFirst).add(mySecond);
			CCVector3 myB = myEnd.add(myFirst.negate()).add(mySecond);
			CCVector3 myC = myEnd.add(myFirst).add(mySecond.negate());
			CCVector3 myD = myEnd.add(myFirst).negate().add(mySecond);

			_myGraphics.beginShape(CCDrawMode.TRIANGLES);
			_myGraphics.vertex(myPosition);
			_myGraphics.vertex(myA);
			_myGraphics.vertex(myB);
			_myGraphics.vertex(myPosition);
			_myGraphics.vertex(myB);
			_myGraphics.vertex(myC);
			_myGraphics.vertex(myPosition);
			_myGraphics.vertex(myC);
			_myGraphics.vertex(myD);
			_myGraphics.vertex(myPosition);
			_myGraphics.vertex(myD);
			_myGraphics.vertex(myA);
			_myGraphics.endShape();

			_myGraphics.beginShape(CCDrawMode.QUADS);
			_myGraphics.vertex(myA);
			_myGraphics.vertex(myB);
			_myGraphics.vertex(myC);
			_myGraphics.vertex(myD);
			_myGraphics.endShape();
		}
		
		_mySpotLightShader.end();
	}
	
	@Override
	public void apply(CCGeometryBuffer theGeometryBuffer, CCGraphics g) {
		g.pushAttribute();
		g.blend(CCBlendMode.ADD);
		g.noDepthTest();
		
		g.color(255);
		_myRenderBuffer.beginDraw();
		g.clear();
		
		g.texture(0, theGeometryBuffer.positions());
		g.texture(1, theGeometryBuffer.normals());
		g.texture(2, theGeometryBuffer.colors());

		applyDirectionalLights();
		applyPointLights();
		applySpotLights();

		g.noTexture();
		
		g.noBlend();
//		g.color(255,0,0);
//		g.polygonMode(CCPolygonMode.LINE);
//		for(CCPointLight myPointLight:_myPointLights) {
//			CCVector3 myPosition = myPointLight.position();
//			
//			float myLightX = myPointLight.position().x;
//			float myLightY = myPointLight.position().y;
//			float myLightZ = myPointLight.position().z;
//
//			_myGraphics.beginShape(CCDrawMode.QUADS);
//			_myGraphics.vertex(myLightX - myPointLight.radius(), myLightY - myPointLight.radius(), myLightZ);
//			_myGraphics.vertex(myLightX + myPointLight.radius(), myLightY - myPointLight.radius(), myLightZ);
//			_myGraphics.vertex(myLightX + myPointLight.radius(), myLightY + myPointLight.radius(), myLightZ);
//			_myGraphics.vertex(myLightX - myPointLight.radius(), myLightY + myPointLight.radius(), myLightZ);
//			_myGraphics.endShape();
//		}
//		g.polygonMode(CCPolygonMode.FILL);
		_myRenderBuffer.endDraw();
		
		g.popAttribute();
		
	}

	public CCTexture2D content() {
		return _myRenderBuffer.attachment(0);
	}
}
