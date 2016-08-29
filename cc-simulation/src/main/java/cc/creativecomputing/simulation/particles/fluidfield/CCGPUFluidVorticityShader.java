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
 * vorticity computation.
 * @author info
 * RENDER TO VORTICITY TEXTURE
 * Render without border
 */
public class CCGPUFluidVorticityShader extends CCCGShader{

	private CGparameter _myVelocityTextureParameter;
	private CGparameter _myHalfRdxParameter;
	
	/**
	 * @param theG
	 * @param theVertexShaderFile
	 * @param theFragmentShaderFile
	 */
	public CCGPUFluidVorticityShader(CCGraphics theG) {
		super(null, CCIOUtil.classPath(CCGPUFluid.class,"shader/vorticity.fp"));
		
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
	
	public void gridScale(final float theGridScale) {
		parameter(_myHalfRdxParameter, 0.5f / theGridScale);
	}
}
