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
 * Poisson-pressure solver: By running this Jacobi Relaxation solver for 
 * multiple iterations, this solves for the pressure disturbance in the 
 * fluid given the divergence of the velocity.
 * 
 * render to pressure texture
 * render without border
 * @author info
 *
 */
public class CCGPUFluidDiffusionShader extends CCCGShader{

	private CGparameter _myTextureXParameter;
	private CGparameter _myTextureBParameter;
	
	private CGparameter _myAlphaParameter;
	private CGparameter _myRBetaParameter;
	
	/**
	 * @param theG
	 * @param theVertexShaderFile
	 * @param theFragmentShaderFile
	 */
	public CCGPUFluidDiffusionShader(CCGraphics theG) {
		super(null, CCIOUtil.classPath(CCGPUFluid.class,"shader/diffusion.fp"));
		_myTextureXParameter = fragmentParameter("x");
		_myTextureBParameter = fragmentParameter("b");
		_myAlphaParameter = fragmentParameter("alpha");
		_myRBetaParameter = fragmentParameter("rBeta"); 
		load();
	}
	
	public void textureX(CCTexture2D theTextureX) {
		texture(_myTextureXParameter,theTextureX.id());
	}
	
	public void textureB(CCTexture2D theTextureB) {
		texture(_myTextureBParameter,theTextureB.id());
	}
	
	public void alpha(final float theAlpha) {
		parameter(_myAlphaParameter, theAlpha);
	}

	public void rBeta(final float theRBeta) {
		parameter(_myRBetaParameter, theRBeta);
	}
}
