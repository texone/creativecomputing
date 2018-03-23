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
package cc.creativecomputing.demo.gl2.shader.volume;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.demo.gl2.shader.imaging.noise.CCTextureNoiseDemo;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCGLShaderUtil;
import cc.creativecomputing.graphics.texture.CCFrameBufferObjectAttributes;
import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.CCVolumeBuffer;
import cc.creativecomputing.io.CCNIOUtil;

public class CCVolumeBufferDemo extends CCGL2Adapter {
	
	@CCProperty(name = "volume x", min = -500, max = 500)
	private float _cX = 0;
	
	@CCProperty(name = "volume y", min = -500, max = 500)
	private float _cY = 0;
	
	@CCProperty(name = "volume z", min = -500, max = 500)
	private float _cZ = 0;
	
	@CCProperty(name = "volume width", min = 0, max = 500)
	private float _cWidth = 0;
	
	@CCProperty(name = "volume height", min = 0, max = 500)
	private float _cHeight = 0;
	
	@CCProperty(name = "volume depth", min = 0, max = 500)
	private float _cDepth = 0;
	
	@CCProperty(name = "color scale", min = 0, max = 1)
	private float _cColorScale = 0;
	
	private CCVolumeBuffer _myVolumeBuffer;
	
	@CCProperty(name = "volume fill")
	private CCGLProgram _myVolumeFillShader;
	@CCProperty(name = "volume draw")
	private CCGLProgram _myVolumeDrawShader;
	@CCProperty(name = "raymarcher")
	private CCGLProgram _myRayMarcher;
	private CCCameraController _myArcball;
	
	private double mouseX;
	private double mouseY;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		CCTextureAttributes myTextureAttributes = new CCTextureAttributes();
		myTextureAttributes.format(CCPixelFormat.RGBA);
		myTextureAttributes.internalFormat(CCPixelInternalFormat.RGBA16F);
		myTextureAttributes.pixelType(CCPixelType.FLOAT);
		
		CCFrameBufferObjectAttributes myAttributes = new CCFrameBufferObjectAttributes();
		_myVolumeBuffer = new CCVolumeBuffer(myAttributes, 32, 32, 32);
		
		_myVolumeFillShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "volumeFiller_vp.glsl"), 
			CCNIOUtil.classPath(this, "volumeFiller_fp.glsl")
		);
		
		_myVolumeDrawShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "volumeDrawer_vp.glsl"), 
			CCNIOUtil.classPath(this, "volumeDrawer_fp.glsl")
		);
		
		_myRayMarcher = new CCGLProgram(
				null, 
				CCNIOUtil.classPath(this, "raymarcher.glsl")
			);
		
		_myArcball = new CCCameraController(this, g, 100);
		
		mouseMoved().add(event -> {
			mouseX = event.x();
			mouseY = event.y();
		});
	}

	@Override
	public void update(CCAnimator theAnimator) {
		
	}
	
	@CCProperty(name = "march")
	private boolean _cMarch = false;

	@Override
	public void display(CCGraphics g) {
		_myVolumeBuffer.beginDraw(g);
		_myVolumeFillShader.start();
		_myVolumeFillShader.uniform3f("volumePosition", _cX, _cY, _cZ);
		_myVolumeFillShader.uniform3f("volumeScale", _cWidth, _cHeight, _cDepth);
		_myVolumeFillShader.uniform3f("checkPosition", mouseX - g.width()/2, g.height()/2 - mouseY, 0);
		_myVolumeFillShader.uniform1f("colorScale", _cColorScale);
		for (int z = 0; z < _myVolumeBuffer.depth(); z++) {
			// attach texture slice to FBO
			// render
			_myVolumeFillShader.uniform1f("z", (float)z/_myVolumeBuffer.depth());
			_myVolumeBuffer.drawSlice(g, z);
		}
		_myVolumeFillShader.end();
		_myVolumeBuffer.endDraw(g);
		
		g.clear();
		g.image(_myVolumeBuffer,0,0);
		
		_myArcball.camera().draw(g);
		g.texture(0,_myVolumeBuffer);
		_myVolumeDrawShader.start();
		_myVolumeDrawShader.uniform1i("cubeSampler", 0);
		g.beginShape(CCDrawMode.POINTS);
		for(float x = 0; x < _myVolumeBuffer.width();x++) {
			for(float y = 0; y < _myVolumeBuffer.height();y++) {
				for(float z = 0; z < _myVolumeBuffer.depth();z++) {
					float myX = x / _myVolumeBuffer.width();
					float myY = y / _myVolumeBuffer.width();
					float myZ = z / _myVolumeBuffer.width();
					g.textureCoords3D(0, myX, myY, myZ);
					g.vertex(
						_cX + myX * _cWidth,
						_cY + myY * _cHeight,
						_cZ + myZ * _cDepth
					);
				}
			}
		}
		g.endShape();
		_myVolumeDrawShader.end();
		g.noTexture();
		
		if(!_cMarch)return;
		
		g.texture(0,_myVolumeBuffer);
		_myRayMarcher.start();
		_myRayMarcher.uniform2f("iResolution", g.width(), g.height());
		_myRayMarcher.uniform1f("iTime", animator().time());
		_myRayMarcher.uniform1i("volume", 0);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex( g.width(),  g.height());
		g.vertex(0,  g.height());
		g.vertex(0, 0);
		g.vertex( g.width(), 0);
		g.endShape();
		_myRayMarcher.end();
		g.noTexture();
	}
	
	public static void main(String[] args) {
		CCVolumeBufferDemo demo = new CCVolumeBufferDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1680, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

