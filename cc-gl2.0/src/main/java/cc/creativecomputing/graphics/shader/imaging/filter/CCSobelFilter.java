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
package cc.creativecomputing.graphics.shader.imaging.filter;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;

public class CCSobelFilter extends CCImageFilter {

	private CCShaderBuffer _myOutput;
	private CCGLProgram _myShader;
	
	@CCProperty(name = "shift_value")
	private boolean _cShift = true;
	
	public CCSobelFilter(CCTexture2D theInput) {
		super(theInput);
		_myOutput = new CCShaderBuffer(32, 3, 2, theInput.width(), theInput.height());
		_myOutput.clear();
		
		_myShader = new CCGLProgram (CCNIOUtil.classPath(this, "shader/sobel_vp.glsl"), CCNIOUtil.classPath(this, "shader/sobel_fp.glsl"));
	}

	public CCTexture2D input() {
		return _myInput;
	}
	
	@Override
	public CCTexture2D output() {
		return _myOutput.attachment(0);
	}
	
	public CCTexture2D outputBrightness() {
		return _myOutput.attachment(1);
	}

	@Override
	public void display(CCGraphics g) {
		g.texture(0, _myInput);	
		_myShader.start();
		_myShader.uniform1i ("IN0", 0);
		
		_myShader.uniform ("offset", _cShift);

		_myOutput.draw();
		_myShader.end();
		g.noTexture();
	}
}
