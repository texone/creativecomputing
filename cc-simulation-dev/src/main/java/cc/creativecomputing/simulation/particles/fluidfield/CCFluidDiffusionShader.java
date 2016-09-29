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
 * Poisson-pressure solver: By running this Jacobi Relaxation solver for 
 * multiple iterations, this solves for the pressure disturbance in the 
 * fluid given the divergence of the velocity.
 * 
 * render to pressure texture
 * render without border
 * @author info
 *
 */
public class CCFluidDiffusionShader extends CCGLProgram{

	private String _myTextureXParameter;
	private String _myTextureBParameter;
	
	private String _myAlphaParameter;
	private String _myRBetaParameter;
	
	private int _myTextureXUnit;
	private int _myTextureBUnit;
	
	private double _myAlpha;
	private double _myRBeta;
	
	/**
	 * @param theG
	 * @param theVertexShaderFile
	 * @param theFragmentShaderFile
	 */
	public CCFluidDiffusionShader() {
		super(null, CCNIOUtil.classPath(CCFluid.class,"diffusion.glsl"));
		_myTextureXParameter = "x";
		_myTextureBParameter = "b";
		_myAlphaParameter = "alpha";
		_myRBetaParameter = "rBeta";
	}
	
	public void textureX(int theTextureX) {
		_myTextureXUnit = theTextureX;
	}
	
	public void textureB(int theTextureB) {
		_myTextureBUnit = theTextureB;
	}
	
	public void alpha(final double theAlpha) {
		_myAlpha = theAlpha;
	}

	public void rBeta(final double theRBeta) {
		_myRBeta = theRBeta;
	}
	
	@Override
	public void start() {
		super.start();
		uniform1i(_myTextureXParameter, _myTextureXUnit);
		uniform1i(_myTextureBParameter, _myTextureBUnit);
		uniform1f(_myAlphaParameter, _myAlpha);
		uniform1f(_myRBetaParameter, _myRBeta);
	}
}
