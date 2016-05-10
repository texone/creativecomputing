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
import cc.creativecomputing.graphics.shader.util.CCRingStructure;
import cc.creativecomputing.graphics.texture.CCTexture2D;

public abstract class CCImageFilterFIR {
	
	protected CCTexture2D _myLatestInput;
	public CCRingStructure _myInput;
	protected CCRingStructure _myOutput;
	
	protected CCGraphics _myGraphics;
	protected int _myTimesteps;
	
	public CCImageFilterFIR (CCGraphics theGraphics, CCTexture2D theInput, int n){
		_myTimesteps = n;
		_myGraphics = theGraphics;
		_myLatestInput = theInput;
		_myInput  = new CCRingStructure (theInput.width(), theInput.height(), _myTimesteps);
		_myOutput = new CCRingStructure (theInput.width(), theInput.height(), _myTimesteps);
	}
	
	
	public abstract void update(float theDeltaTime);

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
