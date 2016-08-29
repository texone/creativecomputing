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

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCIOUtil;

import com.jogamp.opengl.cg.CGparameter;

/**
 * Subtract Gradient.  After solving for the pressure disturbance, this 
 * subtracts the pressure gradient from the divergent velocity field to 
 * give a divergence-free field.
 * @author info
 * RENDER TO Velocity TEXTURE
 * Render without border
 */
public class CCGPUFluidSubtractGradientShader extends CCCGShader{

	private CGparameter _myPressureTextureParameter;
	private CGparameter _myVelocityTextureParameter;
	private CGparameter _myHalfRdxParameter;
	
	/**
	 * @param theG
	 * @param theVertexShaderFile
	 * @param theFragmentShaderFile
	 */
	public CCGPUFluidSubtractGradientShader(CCGraphics theG) {
		super(null, CCIOUtil.classPath(CCGPUFluid.class,"shader/gradient.fp"));
		
		_myPressureTextureParameter = fragmentParameter("pressureTexture");
		_myVelocityTextureParameter = fragmentParameter("velocityTexture");
		_myHalfRdxParameter = fragmentParameter("halfrdx");
		
		load();
	}

	/**
	 * Set to velocity texture
	 * @param theVelocityTexture
	 */
	public void velocityTexture(final CCTexture2D theVelocityTexture) {
		texture(_myVelocityTextureParameter,theVelocityTexture.id());
	}
	

	public void pressureTexture(final CCTexture2D thePressureTextureTexture) {
		texture(_myPressureTextureParameter,thePressureTextureTexture.id());
	}
	
	public void halfRdx(final float theHalfRdx) {
		parameter(_myHalfRdxParameter, theHalfRdx);
	}
}
