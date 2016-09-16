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
 * vorticity confinement force computation.
 * @author info
 * RENDER TO velocity TEXTURE
 * Render without border
 */
public class CCFluidVorticityForceShader extends CCGLProgram{
	
	private String _myVorticityTextureParameter;
	private String _myVelocityTextureParameter;
	private String _myHalfRdxParameter;
	private String _myDXScaleParameter;
	private String _myDeltaTimeParameter;
	
	private int _myVelocityTextureUnit;
	private int _myVorticityTextureUnit;
	private double _myHalfRdx;
	private double _myDeltaTime;
	
	/**
	 * @param theG
	 * @param theVertexShaderFile
	 * @param theFragmentShaderFile
	 */
	public CCFluidVorticityForceShader() {
		super(null, CCNIOUtil.classPath(CCFluid.class,"vorticityforce.glsl"));
		
		_myVorticityTextureParameter = "vorticityTexture";
		_myVelocityTextureParameter = "velocityTexture";
		_myHalfRdxParameter = "halfrdx";
		_myDXScaleParameter = "dxscale";
		_myDeltaTimeParameter = "deltaTime";
	}
	
	public void vorticityTexture(final int theVorticityTexture) {
		_myVorticityTextureUnit = theVorticityTexture;
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
	
	public void deltaTime(final float theDeltaTime) {
		_myDeltaTime = theDeltaTime;
	}
	
	@Override
	public void start() {
		super.start();
		uniform1i(_myVorticityTextureParameter, _myVorticityTextureUnit);
		uniform1i(_myVelocityTextureParameter, _myVelocityTextureUnit);
		uniform1f(_myHalfRdxParameter, 0.5f / _myHalfRdx);
		uniform2f(_myDXScaleParameter, _myHalfRdx, _myHalfRdx);
		uniform1f(_myDeltaTimeParameter, _myDeltaTime);
	}
}
