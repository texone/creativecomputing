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
package cc.creativecomputing.graphics.texture;

import java.nio.FloatBuffer;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;

import com.jogamp.opengl.GL2;

/**
 * class to represent a 3D volume (fbo and associated textures)
 * 
 * @author christianriekoff
 * 
 */
public class CCVolumeBuffer extends CCTexture3D {

	// private BlendMode m_blendMode;

	private CCFrameBuffer _myFrameBuffer;

	public CCVolumeBuffer(CCFrameBufferObjectAttributes theAttributes, int width, int height, int depth) {
		super(theAttributes.textureAttributes(0), width, height, depth);

		// create fbo
		_myFrameBuffer = new CCFrameBuffer();

		// create textures

		// attach slice 0 of first texture to fbo for starters
		_myFrameBuffer.bind();
		_myFrameBuffer.attachTexture(GL2.GL_TEXTURE_3D, _myTextureIDs[0], GL2.GL_COLOR_ATTACHMENT0, 0, 0);
		CCFrameBuffer.disable();
	}

	public void drawSlice(CCGraphics g, int theZ) {
		_myFrameBuffer.attachTexture(GL2.GL_TEXTURE_3D, _myTextureIDs[0], GL2.GL_COLOR_ATTACHMENT0, 0, theZ);
		float z = (theZ + 0.5f) / (float) _myDepth;
		g.clear();
		g.color(0f);
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords(0.0f, 0.0f, z);
		g.vertex(0.0f, 0.0f);
		g.textureCoords(1.0f, 0.0f, z);
		g.vertex(1.0f, 0.0f);
		g.textureCoords(1.0f, 1.0f, z);
		g.vertex(1.0f, 1.0f);
		g.textureCoords(0.0f, 1.0f, z);
		g.vertex(0.0f, 1.0f);
		g.endShape();
	}
	
	public void drawSlicePoints(CCGraphics g, int theZ) {
		_myFrameBuffer.attachTexture(GL2.GL_TEXTURE_3D, _myTextureIDs[0], GL2.GL_COLOR_ATTACHMENT0, 0, theZ);
		float z = (theZ + 0.5f) / (float) _myDepth;
		g.beginShape(CCDrawMode.POINTS);
		for(float x = 0; x < _myWidth;x++) {
			for(float y = 0; y < _myHeight;y++) {
				float myX = (x + 0f) / _myWidth;
				float myY = (y + 0f) / _myHeight;
				
				g.textureCoords(0, myX, myY, z);
				g.vertex(myX,myY,z);
			}
		}
		g.endShape();
	}

	public void drawSlices(CCGraphics g) {
		for (int z = 0; z < _myDepth; z++) {
			// attach texture slice to FBO
			// render
			drawSlice(g, z);
		}
	}

	// enum BlendMode { BLEND_NONE = 0, BLEND_ADDITIVE };

	public void bindFBO() {
		_myFrameBuffer.bind();
	}

	public void unbindFBO() {
		CCFrameBuffer.disable();
	}

	public void beginDraw(CCGraphics g) {
		g.pushAttribute();
		bindFBO();
		g.beginOrtho(0,1,0,1,0,1);

		g.viewport(0, 0, _myWidth, _myHeight);
		g.noDepthTest();
	}

	public void endDraw(CCGraphics g) {
		g.endOrtho();
		unbindFBO();
		g.popAttribute();
	}

	public CCFrameBuffer getFBO() {
		return _myFrameBuffer;
	}

	public FloatBuffer getData(){
		FloatBuffer myResult = FloatBuffer.allocate(size() * _myFormat.numberOfChannels);
		for(int myZSlide = 0; myZSlide < _myDepth;myZSlide++) {
			_myFrameBuffer.data(myResult, this, 0, 0, myZSlide);
		}
		myResult.rewind();
		return myResult;
	}
}
