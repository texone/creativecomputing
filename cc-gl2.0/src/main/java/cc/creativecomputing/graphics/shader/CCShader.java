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
package cc.creativecomputing.graphics.shader;

import java.nio.file.Path;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.io.CCNIOUtil;

import com.jogamp.opengl.GL;


public abstract class CCShader {
	
	protected boolean _myIsVertexShaderEnabled = false;
	protected final boolean _myIsVertexShaderSupported;
	protected boolean _myIsFragmentShaderEnabled = false;
	protected final boolean _myIsFragmentShaderSupported;
	
	protected String _myVertexEntry;
	protected String _myFragmentEntry;

	public CCShader(final Path theVertexShaderPath, final Path theFragmentShaderPath) {
		this(theVertexShaderPath,null,theFragmentShaderPath,null);
	}
	
	public CCShader(final Path[] theVertexShaderPath, final Path[] theFragmentShaderPath) {
		this(theVertexShaderPath,null,theFragmentShaderPath,null);
	}
	
	public CCShader(
		final Path theVertexShaderPath, final String theVertexEntry, 
		final Path theFragmentShaderPath, final String theFragmentEntry
	) {
		this(new Path[] {theVertexShaderPath}, theVertexEntry, new Path[] {theFragmentShaderPath},theFragmentEntry);
	}
	
	public CCShader(
		final Path[]theVertexShaderPath, final String theVertexEntry, 
		final Path[] theFragmentShaderPath, final String theFragmentEntry
	) {
		GL gl = CCGraphics.currentGL();
		String extensions = gl.glGetString(GL.GL_EXTENSIONS);
		
		_myIsVertexShaderSupported = extensions.indexOf("GL_ARB_vertex_shader") != -1;
		_myIsFragmentShaderSupported = extensions.indexOf("GL_ARB_fragment_shader") != -1;
		
		_myVertexEntry = theVertexEntry;
		_myFragmentEntry = theFragmentEntry;
		
		initShader();
		
		if(theVertexShaderPath != null && theVertexShaderPath[0] != null)loadVertexShader(theVertexShaderPath);
		if(theFragmentShaderPath != null && theFragmentShaderPath[0] != null)loadFragmentShader(theFragmentShaderPath);
	}
	
	/**
	 * Takes the given files and merges them to one String. 
	 * This method is used to combine the different shader sources and get rid of the includes
	 * inside the shader files.
	 * @param thePaths
	 * @return
	 */
	protected String buildSource(final Path...thePaths) {
		StringBuffer myBuffer = new StringBuffer();
		
		for(Path myPath:thePaths) {
			myBuffer.append(CCNIOUtil.loadString(myPath));
			myBuffer.append("\n");
		}
		
		return myBuffer.toString();
	}
	
	/**
	 * Overwrite this method for initialization steps that have to be done
	 * before loading and binding the shaders
	 */
	public abstract void initShader();

	public abstract void loadVertexShader(final Path...thePaths);

	public abstract void loadFragmentShader(final Path...thePath);

	public abstract void load();

	public abstract void start();

	public abstract void end();
}
