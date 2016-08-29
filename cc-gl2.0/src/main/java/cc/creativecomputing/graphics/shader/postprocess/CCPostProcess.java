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
package cc.creativecomputing.graphics.shader.postprocess;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCCamera;
import cc.creativecomputing.graphics.CCGraphics;

public class CCPostProcess {

	private CCGeometryBuffer _myGeometryBuffer;

	private CCGraphics _myGraphics;
	
	private List<CCPostProcessEffect> _myEffects = new ArrayList<CCPostProcessEffect>();

	public CCPostProcess(CCGraphics g, int width, int height) {
		_myGeometryBuffer = new CCGeometryBuffer(g, width, height);
		_myGraphics = g;
	}
	
	public void addEffect(CCPostProcessEffect theEffect) {
		theEffect.initialize(_myGeometryBuffer.data().width(), _myGeometryBuffer.data().height());
		_myEffects.add(theEffect);
	}
	
	public CCCamera camera(){
		return _myGeometryBuffer.camera();
	}

	public void beginDraw() {
		_myGeometryBuffer.beginDraw();
		_myGraphics.noBlend();
	}

	public void endDraw() {
		_myGeometryBuffer.endDraw();
		
		for(CCPostProcessEffect myEffect:_myEffects) {
			myEffect.apply(_myGeometryBuffer, _myGraphics);
		}
	}

	public CCGeometryBuffer geometryBuffer() {
		return _myGeometryBuffer;
	}
};
