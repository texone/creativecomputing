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

public class CCSurfaceFilter extends CCImageFilter{
	
	private CCGLProgram _myShader;
	
	@CCProperty(name = "depth", min = -1, max = 1)
	private float _cDepth = 1f;
	
	private CCShaderBuffer _myOutput;
	private CCTexture2D    _myInput2;
	
	public CCSurfaceFilter (CCGraphics theGraphics, CCTexture2D theInput, CCTexture2D theInput2) {
		super (theGraphics, theInput);
		_myShader = new CCGLProgram (CCNIOUtil.classPath(this, "shader/surface_vp.glsl"), CCNIOUtil.classPath(this, "shader/surface_fp.glsl"));
		
		// the refraction matrix specifies the output size
		_myOutput = new CCShaderBuffer(theInput2.width(), theInput2.height());
		_myInput2 = theInput2;
		
		// scale the refraction target texture to output size
		_myInput  = theInput;
		
		System.out.println("refract: "+theInput2.width()+" "+theInput2.height()+", text: "+theInput.width()+" "+theInput.height());
	}
	
	public void update (float theDeltaTime) {

		_myShader.start();
		_myGraphics.texture (0, _myInput);	
		_myGraphics.texture (1, _myInput2);	
		
		_myShader.uniform1i ("TEXTURE", 0);
		_myShader.uniform1i ("REFRACT", 1);		
		_myShader.uniform1f ("depth", _cDepth);
		_myShader.uniform1f ("scaleX", (float)_myInput.width()  / _myInput2.width());
		_myShader.uniform1f ("scaleY", (float)_myInput.height() / _myInput2.height());
		
		_myOutput.draw();
		
		_myShader.end();
		_myGraphics.noTexture();
	}

	@Override
	public CCTexture2D output() {
		return _myOutput.attachment(0);
	}
}
