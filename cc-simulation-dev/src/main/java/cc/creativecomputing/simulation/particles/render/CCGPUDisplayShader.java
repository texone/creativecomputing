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
package cc.creativecomputing.simulation.particles.render;

import java.nio.file.Path;

import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.io.CCNIOUtil;


public class CCGPUDisplayShader extends CCGLProgram{
	
	String _myPointSizeParameter;
	String _myMinPointSizeParameter;
	String _myMaxPointSizeParameter;
	
	String _myTangHalfFovParameter;
	
	private double _myPointSize;
	private double _myTanHalfFov;
	
	public CCGPUDisplayShader(final Path theVertexFile, final Path theFragmentFile) {
		super(theVertexFile, theFragmentFile);
		
		_myPointSizeParameter = "_uPointSize";
		
		_myTangHalfFovParameter = "tanHalfFov";
		
		pointSize(1f);
	}
	
	public CCGPUDisplayShader(){
		this(
			CCNIOUtil.classPath(CCGPUDisplayShader.class, "display_vertex.glsl"),
			CCNIOUtil.classPath(CCGPUDisplayShader.class, "display_fragment.glsl")
		);
		
		
	}
	
	public void pointSize(final double thePointSize) {
		_myPointSize = thePointSize;
	}
	
	public void tangHalfFov(final double theTangHalfFov) {
		_myTanHalfFov = theTangHalfFov;
	}
	
	@Override
	public void start() {
		uniform1f(_myPointSizeParameter, _myPointSize);
		uniform1f(_myTangHalfFovParameter, _myTanHalfFov);
	}
}
