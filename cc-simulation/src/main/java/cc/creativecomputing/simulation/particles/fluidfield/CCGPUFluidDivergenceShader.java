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
 * Divergence of velocity: This computes how divergent the velocity field is
 * (how much in/out flow there is at every point).  Used as input to the 
 * Poisson solver, below.
 * @author info
 * RENDER TO DIVERGENCE TEXTURE
 * Render without border
 */
public class CCGPUFluidDivergenceShader extends CCCGShader{

	private CGparameter _myWTextureParameter;
	private CGparameter _myHalfRdxParameter;
	
	/**
	 * @param theG
	 * @param theVertexShaderFile
	 * @param theFragmentShaderFile
	 */
	public CCGPUFluidDivergenceShader(CCGraphics theG) {
		super(null, CCIOUtil.classPath(CCGPUFluid.class,"shader/divergence.fp"));
		
		_myWTextureParameter = fragmentParameter("w");
		_myHalfRdxParameter = fragmentParameter("halfrdx");
		
		load();
	}

	/**
	 * Set to velocity texture
	 * @param theWTexture
	 */
	public void velocityTexture(final CCTexture2D theWTexture) {
		texture(_myWTextureParameter, theWTexture.id());
	}
	
	public void halfRdx(final float theHalfRdx) {
		parameter(_myHalfRdxParameter, theHalfRdx);
	}
}
