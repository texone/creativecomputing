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
 * Divergence of velocity: This computes how divergent the velocity field is
 * (how much in/out flow there is at every point).  Used as input to the 
 * Poisson solver, below.
 * @author info
 * RENDER TO DIVERGENCE TEXTURE
 * Render without border
 */
public class CCFluidDivergenceShader extends CCGLProgram{

	private String _myWTextureParameter;
	private String _myHalfRdxParameter;
	
	private int _myWTextureUnit;
	private double _myRdx;
	
	/**
	 * @param theG
	 * @param theVertexShaderFile
	 * @param theFragmentShaderFile
	 */
	public CCFluidDivergenceShader() {
		super(null, CCNIOUtil.classPath(CCFluid.class,"divergence.glsl"));
		
		_myWTextureParameter = "w";
		_myHalfRdxParameter = "halfrdx";
	}

	/**
	 * Set to velocity texture
	 * @param theWTexture
	 */
	public void velocityTexture(final int theWTexture) {
		_myWTextureUnit = theWTexture;
	}
	
	public void halfRdx(final double theHalfRdx) {
		_myRdx = theHalfRdx;
	}
	
	@Override
	public void start() {
		super.start();
		uniform1i(_myWTextureParameter, _myWTextureUnit);
		uniform1f(_myHalfRdxParameter, _myRdx);
	}
}
