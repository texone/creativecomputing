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
package cc.creativecomputing.graphics.shader.util;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;

public class CCRingStructure {
	
	private List<CCShaderBuffer> data;
	private List<CCStatistics>statistics;

	public int iterator = 0;
	private int nTimeSteps;
	private int _myWidth;
	private int _myHeight;

	public CCRingStructure (int w, int h, int n) {
		nTimeSteps = n;
		_myWidth = w;
		_myHeight = h;
		
		data = new ArrayList<CCShaderBuffer>(nTimeSteps);
		statistics = new ArrayList<CCStatistics>(nTimeSteps);
		
		for (int i=0; i<nTimeSteps; i++) {
			CCShaderBuffer tmp = new CCShaderBuffer (32, 3, 2, w, h);
			tmp.attachment(0).textureFilter(CCTextureFilter.LINEAR);
			tmp.clear ();
			data.add (0, tmp);
			statistics.add (0, new CCStatistics());
		}
	}
	
	public int nTimeSteps() {
		return nTimeSteps;
	}
	
	public int width() {
		return _myWidth;
	}
	
	public int height() {
		return _myHeight;
	}

	public void pushInput (CCGraphics g, CCTexture2D input) {
		rShift(); 
		getData(0).beginDraw();
		g.clear();
		g.image (input, 0, 0, _myWidth, _myHeight);
		getData(0).endDraw();
		g.noTexture();
	}
	
	public void pushInput (CCGraphics g, CCTexture2D input, CCStatistics theStatistics) {
		rShift();
		getData(0).beginDraw();
		g.clear();
		g.image (input, 0, 0, _myWidth, _myHeight);
		getData(0).endDraw();
		getStatistics(0).max  (theStatistics.max ());
		getStatistics(0).mean (theStatistics.mean());
		getStatistics(0).sum  (theStatistics.sum ());
	}
	
	public CCShaderBuffer current () {
		iterator = (iterator+1) % nTimeSteps;
		CCShaderBuffer ret = data.get(iterator);
		return ret;
	}
	public void rShift () {
		iterator = (iterator+1) % nTimeSteps;
	}
	public CCShaderBuffer getData (int pos) {
		pos = (iterator-pos+nTimeSteps) % nTimeSteps;
		return data.get(pos);
	}
	public CCStatistics getStatistics (int pos) {
		pos = (iterator-pos+nTimeSteps) % nTimeSteps;
		return statistics.get(pos);
	}
}
