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


/**
 * Subtract Gradient.  After solving for the pressure disturbance, this 
 * subtracts the pressure gradient from the divergent velocity field to 
 * give a divergence-free field.
 * @author info
 * RENDER TO Velocity TEXTURE
 * Render without border
 */
public class CCFluidSubtractGradientShader extends CCGLProgram{

	private String _myPressureTextureParameter;
	private String _myVelocityTextureParameter;
	private String _myHalfRdxParameter;
	
	private int _myVelocityTextureUnit;
	private int _myPressureTextureUnit;
	private double _myHalfRdx;
	
	/**
	 * @param theG
	 * @param theVertexShaderFile
	 * @param theFragmentShaderFile
	 */
	public CCFluidSubtractGradientShader() {
		super(null, CCNIOUtil.classPath(CCFluid.class,"gradient.glsl"));
		
		_myPressureTextureParameter = "pressureTexture";
		_myVelocityTextureParameter = "velocityTexture";
		_myHalfRdxParameter = "halfrdx";
	}

	/**
	 * Set to velocity texture
	 * @param theVelocityTexture
	 */
	public void velocityTexture(final int theVelocityTexture) {
		_myVelocityTextureUnit = theVelocityTexture;
	}
	

	public void pressureTexture(final int thePressureTextureTexture) {
		_myPressureTextureUnit = thePressureTextureTexture;
	}
	
	public void halfRdx(final float theHalfRdx) {
		_myHalfRdx = theHalfRdx;
	}
	
	@Override
	public void start() {
		super.start();
		uniform1i(_myVelocityTextureParameter, _myVelocityTextureUnit);
		uniform1i(_myPressureTextureParameter, _myPressureTextureUnit);
		uniform1f(_myHalfRdxParameter, _myHalfRdx);
	}
	
}
