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

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;

public class CCUpscaleFilter extends CCImageFilter{
	
	CCShaderBuffer _myOutBuffer;
	int _myWidth, _myHeight;
	
	public CCUpscaleFilter(CCTexture2D theInput, int scale) {
		super(theInput);
		_myWidth = theInput.width()*scale;
		_myHeight = theInput.height()*scale;
		

		_myOutBuffer = new CCShaderBuffer (_myWidth,_myHeight);
		_myOutBuffer.attachment(0).textureFilter(CCTextureFilter.LINEAR);
	}
	
	@Override
	public void display (CCGraphics g) {
		_myOutBuffer.beginDraw(g);
		g.clear();
		g.color(1f);
		g.image (_myInput, 0, 0, _myWidth, _myHeight);
		_myOutBuffer.endDraw(g);
	}
	
	@Override
	public CCTexture2D output() {
		return _myOutBuffer.attachment(0);
	}
}	
