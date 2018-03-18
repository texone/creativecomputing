/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticles;

public class CCPointCurveField extends CCForce{

	@CCProperty(name = "prediction", min = 0, max = 1)
	private float _myPrediction = 0;
	@CCProperty(name = "radius")
	private float _myRadius = 1;
	
	@CCProperty(name = "min x", readBack = true)
	private float _myMinX;
	@CCProperty(name = "max x", readBack = true)
	private float _myMaxX;
	
	@CCProperty(name = "output scale", min = 0, max = 1000)
	private float _myOutputScale = 100;
	
	private String _myPredictionParameter;
	private String _myOutputScaleParameter;
	private String _myMinXParameter;
	private String _myRangeXParameter;
	private String _myRadiusParameter;
	private String _myCurveTextureParameter;
	
	private CCShaderBuffer _myCurveData;
	private List<CCVector3> _myCurvePoints;
	
	private CCGLProgram _myInitValueShader;
	
	public CCPointCurveField(int theNumberOfPoints){
		super("PointCurveForceFieldFollow");
		_myCurveData = new CCShaderBuffer(theNumberOfPoints, 1, CCTextureTarget.TEXTURE_2D);
		_myCurvePoints = new ArrayList<>();
		for(int i = 0; i < theNumberOfPoints;i++){
			_myCurvePoints.add(new CCVector3());
		}
		
		_myPredictionParameter = parameter("prediction");
		_myOutputScaleParameter = parameter("outputScale");
		_myMinXParameter = parameter("minX");
		_myRangeXParameter = parameter("rangeX");
		_myRadiusParameter = parameter("radius");
		_myCurveTextureParameter = parameter("curveData");
	}
	
	@Override
	public void setShader(CCGLProgram theProgram) {
		super.setShader(theProgram);
		_myShader.setTextureUniform(_myCurveTextureParameter, _myCurveData.attachment(0));
	}
	
	@Override
	public void setParticles(CCParticles theParticles) {
		_myInitValueShader = theParticles.initValueShader();
	}
	
	public void curvePoint(int theIndex, CCVector3 theVector){
		if(theIndex >= _myCurveData.width())return;
		_myCurvePoints.get(theIndex).set(theVector);
	}
	
	public CCVector3 curvePoint(int theIndex){
		return _myCurvePoints.get(theIndex);
	}
	
	public CCTexture2D curveTexture(){
		return _myCurveData.attachment(0);
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
	
	public double outputScale(){
		return _myOutputScale;
	}

	@Override
	public void setUniforms() {
		super.setUniforms();

		_myShader.uniform1f(_myRadiusParameter, _myRadius);
		_myShader.uniform1f(_myPredictionParameter, _myPrediction);
		_myShader.uniform1f(_myOutputScaleParameter, _myOutputScale);
		_myShader.uniform1f(_myMinXParameter, _myMinX);
		_myShader.uniform1f(_myRangeXParameter, _myMaxX - _myMinX);
	}
	
	@Override
	public void preDisplay(CCGraphics g) {
		_myCurveData.beginDraw(g);
		g.clear();
		_myInitValueShader.start();
		g.beginShape(CCDrawMode.POINTS);
		for(int i = 0; i < _myCurvePoints.size();i++){
			
			g.textureCoords3D(_myCurvePoints.get(i));
			g.vertex(i+0.5f, 0.5f);
		}
		g.endShape();
		_myInitValueShader.end();
		_myCurveData.endDraw(g);
	}
}
