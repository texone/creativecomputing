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
package cc.creativecomputing.simulation.particles.fluidfield;

import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;

/**
 * @author info
 *
 */
public class CCFluidAddColorOrImpulseShader extends CCGLProgram{
	

	private String _myWindowDimensionParameter;
	private String _myPositionParameter;
	private String _myColorParameter;
	
	private String _myRadiusParameter;
	
	private String _myBaseTextureParameter;
	
	private CCVector2 _myPosition;
	private CCVector2 _myWindowDimension;
	private CCColor _myColor;
	
	private double _myRadius;
	private int _myBaseTextureUnit;

	/**
	 * @param theG
	 * @param theVertexShaderFile
	 * @param theFragmentShaderFile
	 */
	public CCFluidAddColorOrImpulseShader(String theShader) {
		super(null, CCNIOUtil.classPath(CCFluidAddColorOrImpulseShader.class,theShader));
		_myWindowDimensionParameter = "windowDimension";
		_myPositionParameter = "position";
		_myColorParameter = "color";
		_myRadiusParameter = "radius";
		_myBaseTextureParameter = "baseTexture";
		
		_myPosition = new CCVector2();
		_myWindowDimension = new CCVector2();
		_myColor = new CCColor();
	}

	public void position(final CCVector2 thePosition) {
		_myPosition.set(thePosition);
	}
	
	public void windowDimension(final double theWidth, final double theHeight) {
		_myWindowDimension.set(theWidth, theHeight);
	}
	
	public void color(final double theRed, final double theGreen, final double theBlue) {
		_myColor.set(theRed, theGreen, theBlue);
	}
	
	public void color(final CCColor theColor) {
		_myColor.set(theColor);
	}
	
	public void radius(final double theRadius) {
		_myRadius = theRadius;
	}
	
	public void baseTexture(final int theBaseTextureUnit) {
		_myBaseTextureUnit = theBaseTextureUnit;
	}
	
	@Override
	public void start() {
		super.start();
		uniform2f(_myPositionParameter, _myPosition);
		uniform2f(_myWindowDimensionParameter, _myWindowDimension);
		uniform4f(_myColorParameter, _myColor);
		uniform1f(_myRadiusParameter, _myRadius);
		uniform1i(_myBaseTextureParameter, _myBaseTextureUnit);
	}
}
