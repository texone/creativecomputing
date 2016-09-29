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
 * vorticity computation.
 * @author info
 * RENDER TO VORTICITY TEXTURE
 * Render without border
 */
public class CCFluidVorticityShader extends CCGLProgram{

	private String _myVelocityTextureParameter;
	private String _myHalfRdxParameter;
	
	private double _myHalfRdx;
	private int _myVelocityTextureUnit;
	
	/**
	 * @param theG
	 * @param theVertexShaderFile
	 * @param theFragmentShaderFile
	 */
	public CCFluidVorticityShader() {
		super(null, CCNIOUtil.classPath(CCFluid.class,"vorticity.glsl"));
		
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
	
	public void gridScale(final double theGridScale) {
		_myHalfRdx = theGridScale;
	}
	
	@Override
	public void start() {
		super.start();
		uniform1i(_myVelocityTextureParameter, _myVelocityTextureUnit);
		uniform1f(_myHalfRdxParameter, 0.5f / _myHalfRdx);
	}
}
