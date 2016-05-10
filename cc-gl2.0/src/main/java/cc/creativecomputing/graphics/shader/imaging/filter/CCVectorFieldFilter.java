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
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
 
public class CCVectorFieldFilter extends CCImageFilter{

	private CCVectorField2D _myVectorField;
	private CCShaderBuffer _myOutput;
	
	@CCProperty(name = "line length", min = 1, max = 40f)
	private float _cLineLength = 1;
	
	@CCProperty(name = "downsample", min = 1, max = 16)
	private int _cDownSample = 1;
	
	public CCVectorFieldFilter(CCGraphics theGraphics, CCTexture2D theInput, int initDownSample) {
		super(theGraphics, theInput);
		_myVectorField = new CCVectorField2D (theInput.width(), theInput.height(), initDownSample);
		_myOutput = new CCShaderBuffer(theInput.width(), theInput.height());
		_myOutput.clear();
	}

	public int width() {
		return _myVectorField.width(); 
	}
	
	public int height() {
		return _myVectorField.height();
	}
	@Override
	public CCTexture2D output() {
		return _myOutput.attachment(0);
	}

	@Override
	public void update(float theDeltaTime) {
		_myVectorField.setDownSample (_cDownSample);
		_myVectorField.setLineLength (_cLineLength);
		_myOutput.beginDraw ();
		_myGraphics.clear ();
		_myGraphics.color (1f);
		_myVectorField.draw (_myGraphics, _myInput);
		_myOutput.endDraw ();
	}
	
	public void draw() {
		_myGraphics.pushAttribute();
		//_myGraphics.translate(0*_myVectorField.width()/2 , 0*_myVectorField.height()/2);
		_myVectorField.setDownSample (_cDownSample);
		_myVectorField.setLineLength (_cLineLength);
		_myVectorField.draw (_myGraphics, _myInput);
		_myGraphics.popAttribute();
	}

	public void lineLength(float theLength) {
		_cLineLength = theLength;	
	}
}
