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
import cc.creativecomputing.graphics.shader.imaging.CCImageFilter;
import cc.creativecomputing.graphics.texture.CCTexture2D;
 
public class CCVectorFieldFilter extends CCImageFilter{

	private CCVectorField2D _myVectorField;
	private CCShaderBuffer _myOutput;
	
	@CCProperty(name = "line length", min = 1, max = 40f)
	private float _cLineLength = 1;
	
	@CCProperty(name = "downsample", min = 1, max = 16)
	private int _cDownSample = 1;
	
	public CCVectorFieldFilter(CCTexture2D theInput, int initDownSample) {
		super(theInput);
		_myVectorField = new CCVectorField2D (theInput.width(), theInput.height(), initDownSample);
		_myOutput = new CCShaderBuffer(theInput.width(), theInput.height());
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
	public void display(CCGraphics g) {
		_myVectorField.setDownSample (_cDownSample);
		_myVectorField.setLineLength (_cLineLength);
		_myOutput.beginDraw (g);
		g.clear ();
		g.color (1f);
		_myVectorField.draw (g, _myInput);
		_myOutput.endDraw (g);
	}
	
	public void draw(CCGraphics g) {
		g.pushAttribute();
		//g.translate(0*_myVectorField.width()/2 , 0*_myVectorField.height()/2);
		_myVectorField.setDownSample (_cDownSample);
		_myVectorField.setLineLength (_cLineLength);
		_myVectorField.draw (g, _myInput);
		g.popAttribute();
	}

	public void lineLength(float theLength) {
		_cLineLength = theLength;	
	}
}
