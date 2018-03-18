/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.simulation.particles.render;

import java.nio.file.Path;

import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.io.CCNIOUtil;


public class CCDisplayShader extends CCGLProgram{
	
	String _myPointSizeParameter;
	String _myMinPointSizeParameter;
	String _myMaxPointSizeParameter;
	
	String _myTangHalfFovParameter;
	
	private double _myPointSize;
	private double _myTanHalfFov;
	
	public CCDisplayShader(final Path theVertexFile, final Path theFragmentFile) {
		super(theVertexFile, theFragmentFile);
		
		_myPointSizeParameter = "_uPointSize";
		
		_myTangHalfFovParameter = "tanHalfFov";
		
		pointSize(1f);
	}
	
	public CCDisplayShader(){
		this(
			CCNIOUtil.classPath(CCDisplayShader.class, "display_vertex.glsl"),
			CCNIOUtil.classPath(CCDisplayShader.class, "display_fragment.glsl")
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
		super.start();
		uniform1f(_myPointSizeParameter, _myPointSize);
		uniform1f(_myTangHalfFovParameter, _myTanHalfFov);
	}
}
