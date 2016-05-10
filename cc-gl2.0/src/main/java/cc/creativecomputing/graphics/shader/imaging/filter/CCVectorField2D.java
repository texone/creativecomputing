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

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;

public class CCVectorField2D {

	CCVBOMesh _myMesh;
	CCGLProgram _myShader;
	int w, h;
	int _myInitDownSample;
	public float getLineLength() {
		return lineLength;
	}

	public void setLineLength(float lineLength) {
		this.lineLength = lineLength;
	}

	public int getDownSample() {
		return downSample;
	}

	public void setDownSample (int downSample) {
		this.downSample = downSample;
	}

	private float lineLength;
	private int downSample;
	
	//TODO: better distinction between mesh w/h and display w/h
	public int width() {
		return w * _myInitDownSample;
	}
	
	public int height() {
		return h* _myInitDownSample;
	}
	
	public CCVectorField2D (int w, int h, int initDownSample) {

		this.w = w / initDownSample;
		this.h = h / initDownSample;
		_myInitDownSample = initDownSample;
		lineLength = 4f;

		_myMesh = new CCVBOMesh(CCDrawMode.LINES, w*h*2);
		for (int x=0; x<w; x++) {
			for (int y=0; y<h; y++) {
				_myMesh.addVertex (x*_myInitDownSample, y*_myInitDownSample, 0);
				_myMesh.addVertex (x*_myInitDownSample, y*_myInitDownSample, 1);
			}
		}

		_myShader = new CCGLProgram (CCNIOUtil.classPath(this, "shader/vectorField2Lines_vp.glsl"), CCNIOUtil.classPath(this, "shader/vectorField2Lines_fp.glsl"));
	}

	public void draw (CCGraphics g, CCTexture2D field) {
		g.pushMatrix();
		g.translate(-width()/2, -height()/2);
		g.color(1f);
		g.texture(0,  field);
		_myShader.start ();
		_myShader.uniform1i ("field", 0);
		_myShader.uniform1f ("scale", lineLength);
		_myShader.uniform1i ("downSample", downSample);
		
		_myMesh.draw(g);
		_myShader.end();
		g.noTexture();
		g.popMatrix();
	}
}
