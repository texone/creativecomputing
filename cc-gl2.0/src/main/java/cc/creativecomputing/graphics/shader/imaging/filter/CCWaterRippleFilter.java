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
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;

public class CCWaterRippleFilter extends CCImageFilterFIR {
	
	@CCProperty(name = "damping", min = 0.0f, max = 1f)
	private float _cDamping = 1f;
	
	@CCProperty(name = "wave speed", min = 0.0f, max = 6f)
	private float _cSpeed = 1f;
	
	private CCGLProgram _myShader0;
	
	public CCWaterRippleFilter (CCGraphics theGraphics, CCTexture2D theInput) {
		super (theGraphics, theInput, 3);
		_myShader0 = new CCGLProgram(
			CCNIOUtil.classPath(this, "shader/water_propagate_vp.glsl"), 
			CCNIOUtil.classPath(this, "shader/water_propagate_fp.glsl")
		);
	}
	
	public void update(float theDeltaTime) {
		_myInput.pushInput (_myGraphics, _myLatestInput);
	
		_myShader0.start();
		
		_myGraphics.clear();
		
		_myGraphics.texture (0, _myInput.getData(0).attachment(0));	
		_myGraphics.texture (1, _myOutput.getData(0).attachment(0));	
		_myGraphics.texture (2, _myOutput.getData(1).attachment(0));	
		
		_myShader0.uniform1i ("IN0", 0);
		_myShader0.uniform1i ("OUT1", 1);
		_myShader0.uniform1i ("OUT2", 2);
			
		_myShader0.uniform1f ("damping", _cDamping);
		_myShader0.uniform1f ("speed", _cSpeed);
			
		_myOutput.rShift();
		_myOutput.getData(0).draw();
		
		_myShader0.end();
		_myGraphics.noTexture();
	}
}
