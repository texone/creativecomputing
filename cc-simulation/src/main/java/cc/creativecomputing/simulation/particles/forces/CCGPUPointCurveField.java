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
package cc.creativecomputing.simulation.particles.forces;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.particles.CCParticles;

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUPointCurveField extends CCForce{

	private float _myPrediction = 0;
	private float _myRadius = 1;
	
	private float _myMinX;
	private float _myMaxX;
	
	private float _myOutputScale;
	
	private CGparameter _myPredictionParameter;
	private CGparameter _myOutputScaleParameter;
	private CGparameter _myMinXParameter;
	private CGparameter _myRangeXParameter;
	private CGparameter _myRadiusParameter;
	private CGparameter _myCurveTextureParameter;
	
	private CCShaderBuffer _myCurveData;
	private List<CCVector3f> _myCurvePoints;
	
	private CCCGShader _myInitValueShader;
	private CCGraphics _myGraphics;
	
	public CCGPUPointCurveField(int theNumberOfPoints, CCGraphics g){
		super("PointCurveForceFieldFollow");
		_myCurveData = new CCShaderBuffer(theNumberOfPoints, 1, CCTextureTarget.TEXTURE_2D);
		_myCurvePoints = new ArrayList<>();
		for(int i = 0; i < theNumberOfPoints;i++){
			_myCurvePoints.add(new CCVector3f());
		}
		_myInitValueShader = new CCCGShader(null, CCIOUtil.classPath(CCParticles.class, "shader/initvalue.fp"));
		_myInitValueShader.load();
		
		_myGraphics = g;
	}
	
	public void curvePoint(int theIndex, CCVector3f theVector){
		if(theIndex >= _myCurveData.width())return;
		_myCurvePoints.get(theIndex).set(theVector);
	}
	
	public CCVector3f curvePoint(int theIndex){
		return _myCurvePoints.get(theIndex);
	}
	
	public CCTexture2D curveTexture(){
		return _myCurveData.attachment(0);
	}
	
	@Override
	public void setupParameter(int theWidth, int theHeight){
		super.setupParameter(theWidth, theHeight);
		
		_myPredictionParameter = parameter("prediction");
		_myOutputScaleParameter = parameter("outputScale");
		_myMinXParameter = parameter("minX");
		_myRangeXParameter = parameter("rangeX");
		_myRadiusParameter = parameter("radius");
		_myCurveTextureParameter = parameter("curveData");
	}
	
	public void prediction(final float thePrediction) {
		_myPrediction = thePrediction;
	}
	
	public void radius(float theRadius) {
		_myRadius = theRadius;
	}
	
	public void minX(float theX){
		_myMinX = theX;
	}
	
	public void maxX(float theX){
		_myMaxX = theX;
	}
	
	public void outputScale(float theOutputScale){
		_myOutputScale = theOutputScale;
	}

	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		
		_myVelocityShader.parameter(_myRadiusParameter, _myRadius);
		_myVelocityShader.parameter(_myPredictionParameter, _myPrediction);
		_myVelocityShader.parameter(_myOutputScaleParameter, _myOutputScale);
		_myVelocityShader.parameter(_myMinXParameter, _myMinX);
		_myVelocityShader.parameter(_myRangeXParameter, _myMaxX - _myMinX);
		_myVelocityShader.texture(_myCurveTextureParameter, _myCurveData.attachment(0).id());
		
		_myCurveData.beginDraw();
		_myGraphics.clear();
		_myInitValueShader.start();
		_myGraphics.beginShape(CCDrawMode.POINTS);
		for(int i = 0; i < _myCurvePoints.size();i++){
			_myGraphics.textureCoords(_myCurvePoints.get(i));
			_myGraphics.vertex(i+0.5f, 0.5f);
		}
		_myGraphics.endShape();
		_myInitValueShader.end();
		_myCurveData.endDraw();
	}
	
}
