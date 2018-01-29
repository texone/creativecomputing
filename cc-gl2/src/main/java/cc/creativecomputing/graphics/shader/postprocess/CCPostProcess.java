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

import java.util.LinkedHashMap;
import java.util.Map;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCCamera;
import cc.creativecomputing.graphics.CCGraphics;

public class CCPostProcess {
	
	private enum CCPostProcessDebug{
		POSITIONS,
		NORMALS,
		COLORS,
		DEPTH
	}

	private CCGeometryBuffer _myGeometryBuffer;

	private CCGraphics _myGraphics;
	
	@CCProperty(name = "effects")
	private Map<String,CCPostProcessEffect> _myEffects = new LinkedHashMap<>();
	
	@CCProperty(name = "draw mode")
	private CCPostProcessDebug _myDrawMode = CCPostProcessDebug.COLORS;
	
	private CCSSAO _mySSAO;

	public CCPostProcess(CCGraphics g, int width, int height) {
		_myGeometryBuffer = new CCGeometryBuffer(g, width, height);
		_myGraphics = g;
		
//		addEffect(_mySSAO = new CCSSAO());
	}
	
	public CCPostProcess(CCGraphics g){
		this(g, g.width(), g.height());
	}
	
	public void addEffect(CCPostProcessEffect theEffect) {
		theEffect.initialize(_myGeometryBuffer.data().width(), _myGeometryBuffer.data().height());
		_myEffects.put(theEffect.name(), theEffect);
	}
	
	public CCCamera camera(){
		return _myGeometryBuffer.camera();
	}

	public void beginDraw(CCGraphics g) {
		_myGeometryBuffer.beginDraw(g);
		_myGraphics.noBlend();
	}

	public void endDraw(CCGraphics g) {
		_myGeometryBuffer.endDraw(g);
		
		for(CCPostProcessEffect myEffect:_myEffects.values()) {
			myEffect.apply(_myGeometryBuffer, _myGraphics);
		}
	}

	public CCGeometryBuffer geometryBuffer() {
		return _myGeometryBuffer;
	}
	
	public void display(CCGraphics g){
		switch(_myDrawMode){
		case COLORS:
			g.image(_myGeometryBuffer.colors(), 0,0);
			break;
		case POSITIONS:
			g.image(_myGeometryBuffer.positions(), 0,0);
			break;
		case NORMALS:
			g.image(_myGeometryBuffer.normals(), 0,0);
			break;
		case DEPTH:
			g.image(_myGeometryBuffer.depth(), 0,0);
			break;
		}
	}
}
