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
public class CCFluidAdvectShader extends CCGLProgram{
	
	private String _myVelocityTextureParameter;
	private String _myTargetTextureParameter;
	private String _myRDXParameter;
	private String _myDissipationParameter;
	private String _myTimeStepParameter;
	private String _myDarkeningParameter;
	
	private int _myVelocityTextureUnit;
	private int _myTargetTextureUnit;
	
	private double _myGridScale;
	private double _myDissipation;
	private double _myTimeStep;
	private double _myDarking;

	/**
	 * @param theG
	 * @param theVertexShaderFile
	 * @param theFragmentShaderFile
	 */
	public CCFluidAdvectShader() {
		super(null, CCNIOUtil.classPath(CCFluid.class,"advect.glsl"));
		_myRDXParameter = "rdx";
		_myVelocityTextureParameter = "velocityTexture";
		_myTargetTextureParameter = "targetTexture";
		_myDissipationParameter = "dissipation";
		_myTimeStepParameter = "timeStep";
		_myDarkeningParameter = "darkening";
	}
	
	/**
	 * set to velocity texture
	 * @param theVelocityTexture
	 */
	public void velocityTexture(final int theVelocityTexture) {
		_myVelocityTextureUnit = theVelocityTexture;
	}
	
	/**
	 * set to velocity texture
	 * @param theTargetTexture
	 */
	public void targetTexture(final int theTargetTexture) {
		_myTargetTextureUnit = theTargetTexture;
	}
	
	public void gridScale(final double theGridScale) {
		_myGridScale = theGridScale;
	}
	
	public void dissipation(final double theDissipation) {
		_myDissipation = theDissipation;
	}

	public void timeStep(final double theTimeStep) {
		_myTimeStep = theTimeStep;
	}
	
	public void darking(final double theDarking) {
		_myDarking = theDarking;
	}
	
	@Override
	public void start() {
		super.start();
		uniform1i(_myVelocityTextureParameter, _myVelocityTextureUnit);
		uniform1i(_myTargetTextureParameter, _myTargetTextureUnit);
		uniform1f(_myRDXParameter, 1 / _myGridScale);
		uniform1f(_myDissipationParameter, _myDissipation);
		uniform1f(_myTimeStepParameter, _myTimeStep);
		uniform1f(_myDarkeningParameter, _myDarking);
	}
}
