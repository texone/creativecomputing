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
 * This applies pure neumann boundary conditions (see floPoissonBC.cg) to 
 * the pressure field once per iteration of the poisson-pressure jacobi 
 * solver.  Also no-slip BCs to velocity once per time step.
 * @author info
 *
 */
public class CCGPUFluidBoundaryShader extends CCCGShader{
	
	private CGparameter _myScaleParameter;
	private CGparameter _myTextureParameter;

	/**
	 * @param theG
	 * @param theVertexShaderFile
	 * @param theFragmentShaderFile
	 */
	public CCGPUFluidBoundaryShader(CCGraphics theG) {
		super(null, CCIOUtil.classPath(CCGPUFluid.class,"shader/boundary.fp"));
		_myScaleParameter = fragmentParameter("scale");
		_myTextureParameter = fragmentParameter("x");
		load();
	}
	
	public void scale(final float theScale) {
		parameter(_myScaleParameter, theScale);
	}

	public void texture(final CCTexture2D theTexture) {
		texture(_myTextureParameter,theTexture.id());
	}
}
