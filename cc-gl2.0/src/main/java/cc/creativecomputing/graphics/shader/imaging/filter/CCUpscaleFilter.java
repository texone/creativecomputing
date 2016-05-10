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
	
	public CCUpscaleFilter(CCGraphics theGraphics, CCTexture2D theInput, int scale) {
		super(theGraphics, theInput);
		_myWidth = theInput.width()*scale;
		_myHeight = theInput.height()*scale;
		

		_myOutBuffer = new CCShaderBuffer (_myWidth,_myHeight);
		_myOutBuffer.attachment(0).textureFilter(CCTextureFilter.LINEAR);
		//_myOutBuffer.attachment(0).generateMipmaps(true);
		_myOutBuffer.clear();
	}
	
	public void update (float theDeltaTime) {
		_myOutBuffer.beginDraw();
		_myGraphics.clear();
		_myGraphics.color(1f);
		_myGraphics.image (_myInput, 0, 0, _myWidth, _myHeight);
		_myOutBuffer.endDraw();
	}
	
	public CCTexture2D output() {
		return _myOutBuffer.attachment(0);
	}
}	
