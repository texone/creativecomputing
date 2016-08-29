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

/**
 * @author info
 *
 */
public class CCGPUFluidUpdateOffsetsShader extends CCCGShader{

	/**
	 * @param theG
	 * @param theVertexShaderFile
	 * @param theFragmentShaderFile
	 */
	public CCGPUFluidUpdateOffsetsShader(CCGraphics theG, String theVertexShaderFile, String theFragmentShaderFile) {
		super(theVertexShaderFile, theFragmentShaderFile);
		// TODO Auto-generated constructor stub
	}

}
