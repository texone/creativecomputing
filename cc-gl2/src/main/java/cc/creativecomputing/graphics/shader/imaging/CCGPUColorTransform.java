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
package cc.creativecomputing.graphics.shader.imaging;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMatrix4x4;

public class CCGPUColorTransform extends CCGLProgram{
	
	private CCMatrix4x4 _myMatrix4f = new CCMatrix4x4();

	public CCGPUColorTransform(final CCGraphics g) {
		super(null, CCNIOUtil.classPath(CCGPUColorTransform.class,"imaging/colortransform.fp"));
	}
	
	public void reset() {
		_myMatrix4f.setIdentity();
	}
	
	public void contrast(final float theContrast) {
		_myMatrix4f.translate(theContrast, theContrast, theContrast);
	}
	
	public void brightness(final float theBrightness) {
		_myMatrix4f.scaleLocal(theBrightness);
	}
	
	@Override
	public void start() {
		super.start();
		uniformMatrix4f("colorMatrix", _myMatrix4f);
	}
}
