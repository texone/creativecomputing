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

import cc.creativecomputing.graphics.shader.imaging.CCImageFilter;
import cc.creativecomputing.graphics.shader.util.CCRingStructure;
import cc.creativecomputing.graphics.texture.CCTexture2D;

public abstract class CCImageFilterFIR extends CCImageFilter{
	
	protected CCTexture2D _myLatestInput;
	public CCRingStructure _myInput;
	protected CCRingStructure _myOutput;
	protected int _myTimesteps;
	
	public CCImageFilterFIR (CCTexture2D theInput, int n){
		super(theInput);
		_myTimesteps = n;
		_myLatestInput = theInput;
		_myInput  = new CCRingStructure (theInput.width(), theInput.height(), _myTimesteps);
		_myOutput = new CCRingStructure (theInput.width(), theInput.height(), _myTimesteps);
	}

	@Override
	public CCTexture2D output () {
		return output(0);
	}
	public CCTexture2D output (int n) {
		return _myOutput.getData(n).attachment(0);
	}
	public CCTexture2D input () {
		return output(0);
	}
	public CCTexture2D input (int n) {
		return _myInput.getData(n).attachment(0);
	}
}
