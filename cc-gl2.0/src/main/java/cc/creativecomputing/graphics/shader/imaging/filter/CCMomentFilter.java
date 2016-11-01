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

public class CCMomentFilter extends CCImageFilter {

	@CCProperty(name = "threshold", min = 0, max = 1)
	private float _cThreshold = 0;
	
	@CCProperty(name = "exp", min = 0, max = 4)
	private float _cExp = 0;
	
	@CCProperty(name = "amplify", min = 0, max = 10)
	private float _cAmplify = 1;
	
	private CCShaderBuffer _myOutput;
	private CCGLProgram   _myShader;
	
	public CCMomentFilter(CCTexture2D theInput) {
		super(theInput);
		_myOutput = new CCShaderBuffer(theInput.width(), theInput.height());
		_myShader = new CCGLProgram (CCNIOUtil.classPath(this, "shader/imaging/filter/moment_vp.glsl"), CCNIOUtil.classPath(this, "shader/moment_fp.glsl"));
	}

	@Override
	public CCTexture2D output() {
		return _myOutput.attachment(0);
	}

	@Override
	public void display(CCGraphics g) {
		_myShader.start();
		_myShader.uniform1f("threshold", _cThreshold);
		_myShader.uniform1f("exp",       _cExp);
		_myShader.uniform1f("ampllify",  _cAmplify);
		
		_myOutput.beginDraw();
		g.clear();
		g.image(_myInput, 0,0);
		_myShader.end();
		_myOutput.endDraw();
		g.noTexture();
	}

}
