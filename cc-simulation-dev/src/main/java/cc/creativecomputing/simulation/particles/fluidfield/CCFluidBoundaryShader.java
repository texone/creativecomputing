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
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.io.CCNIOUtil;


/**
 * This applies pure neumann boundary conditions (see floPoissonBC.cg) to 
 * the pressure field once per iteration of the poisson-pressure jacobi 
 * solver.  Also no-slip BCs to velocity once per time step.
 * @author info
 *
 */
public class CCFluidBoundaryShader extends CCGLProgram{
	
	private String _myScaleParameter;
	private String _myTextureParameter;
	
	private int _myTextureUnit;
	private double _myScale;

	/**
	 * @param theG
	 * @param theVertexShaderFile
	 * @param theFragmentShaderFile
	 */
	public CCFluidBoundaryShader() {
		super(null, CCNIOUtil.classPath(CCFluid.class,"boundary.glsl"));
		_myScaleParameter = "scale";
		_myTextureParameter = "x";
	}
	
	public void scale(final float theScale) {
		_myScale = theScale;
	}

	public void texture(final int theTexture) {
		_myTextureUnit = theTexture;
	}
	
	@Override
	public void start() {
		super.start();
		uniform1i(_myTextureParameter, _myTextureUnit);
		uniform1f(_myScaleParameter, _myScale);
	}
}
