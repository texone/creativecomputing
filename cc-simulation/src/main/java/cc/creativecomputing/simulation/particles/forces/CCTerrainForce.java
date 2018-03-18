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

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCTexture;
import cc.creativecomputing.math.CCVector3;


public class CCTerrainForce extends CCForce {

	private CCTexture _myTexture;
	private CCVector3 _myScale;
	private CCVector3 _myOffset;
	
	private double _myExponent = 1;

	private String _myTextureParameter;
	private String _myTextureSizeParameter;
	private String _myScaleParameter;
	private String _myOffsetParameter;
	private String _myExponentParameter;

	public CCTerrainForce(
		final CCTexture theTexture, 
		final CCVector3 theScale, 
		final CCVector3 theOffset
	) {
		super("terrain");
		
		_myTexture = theTexture;
		_myScale = theScale;
		_myOffset = theOffset;

		_myTextureParameter = parameter("terrainTexture");
		_myTextureSizeParameter = parameter("textureSize");
		_myScaleParameter = parameter("scale");
		_myOffsetParameter = parameter("offset");
		_myExponentParameter = parameter("exponent");
	}
	
	@Override
	public void setShader(CCGLProgram theProgram) {
		super.setShader(theProgram);
		_myShader.setTextureUniform(_myTextureParameter, _myTexture);
	}
	
	@Override
	public void setUniforms() {
		super.setUniforms();
		_myShader.uniform3f(_myScaleParameter, _myScale);
		_myShader.uniform3f(_myOffsetParameter, _myOffset);
		_myShader.uniform2f(_myTextureSizeParameter, _myTexture.width(), _myTexture.height());
		_myShader.uniform1f(_myExponentParameter, _myExponent);
	}

	public void texture(final CCTexture theTexture) {
		_myTexture = theTexture;
		_myShader.setTextureUniform(_myTextureParameter, theTexture);
	}
	
	public void textureScale(final CCVector3 theTextureScale) {
		_myScale.set(theTextureScale);
	}
	
	public void textureScale(final float theX, final float theY, final float theZ){
		_myScale.set(theX, theY, theZ);
	}
	
	public void textureOffset(final CCVector3 theTextureOffset) {
		_myOffset.set(theTextureOffset);
	}
	
	public void textureOffset(final float theX, final float theY, final float theZ) {
		_myOffset.set(theX, theY, theZ);
	}
	
	/**
	 * Sets the exponent to change the values of the height map. All pixel values
	 * are read as brightness between 0 and 1. Where 0 is low and 1 is high. Setting 
	 * the exponent you can have different increases in height. The default value is 1.
	 * @param theExponent exponent to control the height increase
	 */
	@CCProperty(name = "exponent", min = 0, max = 2)
	public void exponent(final double theExponent){
		_myExponent = theExponent;
	}

}
