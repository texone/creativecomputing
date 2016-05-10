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
import cc.creativecomputing.graphics.texture.CCTexture2D;

public abstract class CCImageFilter {
	
	protected CCTexture2D _myInput;
	protected CCGraphics _myGraphics;
	
	public CCImageFilter(CCGraphics theGraphics, CCTexture2D theInput){
		_myGraphics = theGraphics;
		_myInput = theInput;
	}
	
	public abstract CCTexture2D output();
	
	public abstract void update(float theDeltaTime);
	
	public void setInput (CCTexture2D theInput) {
		_myInput = theInput;
	}
}
