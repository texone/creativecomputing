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
 * Advection:
 * This advects a field by the moving velocity field.  This is 
 * used to advect both velocity and scalar values, such as mass.  The result
 * of applying this to the velocity field is a moving but divergent velocity
 * field.  The next few steps correct that divergence to give a divergence-
 * free velocity field.
 * RENDER TO VELOCITY TEXTURE
 * Render without border
 * @author info
 *
 */
public class CCGPUFluidAdvectShader extends CCCGShader{
	
	private CGparameter _myVelocityTextureParameter;
	private CGparameter _myTargetTextureParameter;
	private CGparameter _myRDXParameter;
	private CGparameter _myDissipationParameter;
	private CGparameter _myTimeStepParameter;
	private CGparameter _myDarkening;

	/**
	 * @param theG
	 * @param theVertexShaderFile
	 * @param theFragmentShaderFile
	 */
	public CCGPUFluidAdvectShader(CCGraphics theG) {
		super(null, CCIOUtil.classPath(CCGPUFluid.class,"shader/advect.fp"));
		_myRDXParameter = fragmentParameter("rdx");
		_myVelocityTextureParameter = fragmentParameter("velocityTexture");
		_myTargetTextureParameter = fragmentParameter("targetTexture");
		_myDissipationParameter = fragmentParameter("dissipation");
		_myTimeStepParameter = fragmentParameter("timeStep");
		_myDarkening = fragmentParameter("darkening");
		load();
	}
	
	/**
	 * set to velocity texture
	 * @param theVelocityTexture
	 */
	public void velocityTexture(final CCTexture2D theVelocityTexture) {
		texture(_myVelocityTextureParameter, theVelocityTexture.id());
	}
	
	/**
	 * set to velocity texture
	 * @param theTargetTexture
	 */
	public void targetTexture(final CCTexture2D theTargetTexture) {
		texture(_myTargetTextureParameter, theTargetTexture.id());
	}
	
	public void gridScale(final float theGridScale) {
		parameter(_myRDXParameter, 1 / theGridScale);
	}
	
	public void dissipation(final float theDissipation) {
		parameter(_myDissipationParameter, theDissipation);
	}

	public void timeStep(final float theTimeStep) {
		parameter(_myTimeStepParameter, theTimeStep);
	}
	
	public void darking(final float theDarking) {
		parameter(_myDarkening, theDarking);
	}
}
