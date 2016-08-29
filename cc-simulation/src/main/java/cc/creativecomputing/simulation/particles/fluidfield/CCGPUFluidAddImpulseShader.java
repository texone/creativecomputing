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
import cc.creativecomputing.math.CCVector2f;

import com.jogamp.opengl.cg.CGparameter;

/**
 * @author info
 *
 */
public class CCGPUFluidAddImpulseShader extends CCCGShader{
	

	private CGparameter _myWindowDimensionParameter;
	private CGparameter _myPositionParameter;
	private CGparameter _myColorParameter;
	
	private CGparameter _myRadiusParameter;
	
	private CGparameter _myBaseTextureParameter;

	/**
	 * @param theG
	 * @param theVertexShaderFile
	 * @param theFragmentShaderFile
	 */
	public CCGPUFluidAddImpulseShader(CCGraphics theG) {
		super(null, CCIOUtil.classPath(CCGPUFluid.class,"shader/addimpulse.fp"));
		_myWindowDimensionParameter = fragmentParameter("windowDimension");
		_myPositionParameter = fragmentParameter("position");
		_myColorParameter = fragmentParameter("color");
		_myRadiusParameter = fragmentParameter("radius");
		_myBaseTextureParameter = fragmentParameter("baseTexture");
		load();
	}

	public void position(final CCVector2f thePosition) {
		parameter(_myPositionParameter, thePosition);
	}
	
	public void windowDimension(final float theWidth, final float theHeight) {
		parameter(_myWindowDimensionParameter, theWidth, theHeight);
	}
	
	public void color(final float theRed, final float theGreen, final float theBlue) {
		parameter(_myColorParameter, theRed, theGreen, theBlue, 1f);
	}
	
	public void radius(final float theRadius) {
		parameter(_myRadiusParameter, theRadius);
	}
	
	public void baseTexture(final CCTexture2D theBaseTexture) {
		texture(_myBaseTextureParameter, theBaseTexture.id());
	}
}
