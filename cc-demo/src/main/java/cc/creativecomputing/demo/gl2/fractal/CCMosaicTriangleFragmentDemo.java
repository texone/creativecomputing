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
package cc.creativecomputing.demo.gl2.fractal;


import java.nio.file.Path;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCTexture2DAsset;
import cc.creativecomputing.graphics.texture.CCTexture3D;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;

public class CCMosaicTriangleFragmentDemo extends CCGL2Adapter {
	
	@CCProperty(name = "tri shader")
	private CCGLProgram _myTriShader;
	
	@CCProperty(name = "octaves", min = 1, max = 10)
	private double _cOctaves = 1;
	@CCProperty(name = "scale", min = 0, max = 10)
	private double _cScale = 1;
	@CCProperty(name = "gain", min = 0, max = 1)
	private double _cGain = 1;
	@CCProperty(name = "lacunarity", min = 0, max = 10)
	private double _cLacunarity = 1;
	@CCProperty(name = "random octave 0", min = 0, max = 1)
	private double _cRandomOctave0 = 0;
	@CCProperty(name = "random octave 1", min = 0, max = 1)
	private double _cRandomOctave1 = 0;
	@CCProperty(name = "random octave blend", min = 0, max = 1)
	private double _cRandomOctaveBlend = 0;
	@CCProperty(name = "random offset", min = 0, max = 1)
	private double _cRandomOffset = 1;
	@CCProperty(name = "blend", min = 0, max = 1)
	private double _cBlend = 1;
	@CCProperty(name = "random blend", min = 0, max = 1)
	private double _cRandomBlend = 1;
	@CCProperty(name = "show texture")
	private boolean _cShowTexture = false;
	
	@CCProperty(name = "texture 0")
	private CCTexture2DAsset _myAsset0;
	@CCProperty(name = "texture 1")
	private CCTexture2DAsset _myAsset1;
	
	private CCTexture3D _myTexture;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myTriShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "tri_vertex.glsl"),
			CCNIOUtil.classPath(this, "tri_fragment.glsl")
		);
		
		List<Path> myFiles = CCNIOUtil.list(CCNIOUtil.classPath(this, "storyboard"), "jpg");
		_myTexture = new CCTexture3D(CCImageIO.newImage(myFiles.get(0)), myFiles.size());
		_myTexture.wrap(CCTextureWrap.REPEAT);
		_myTexture.textureFilter(CCTextureFilter.LINEAR);
		
		int i = 0;
		for(Path myPath:myFiles) {
			_myTexture.updateData(CCImageIO.newImage(myPath), i++);
			CCLog.info(i + myPath.toString());
//			if(i >= 11)break;
		}
		
		_myAsset0 = new CCTexture2DAsset(glContext());
		_myAsset1 = new CCTexture2DAsset(glContext());
	}

	@Override
	public void update(CCAnimator theAnimator) {
		
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		if(_myAsset0.value() == null)return;
		if(_myAsset1.value() == null)return;
		
		g.texture(0, _myAsset0.value());
		g.texture(1, _myAsset1.value());
		g.texture(2, _myTexture);
		
		_myTriShader.start();
		_myTriShader.uniform2f("resolution", g.width(), g.height());
		
		_myTriShader.uniform1f("octaves", _cOctaves);
		_myTriShader.uniform1f("scale", _cScale);
		_myTriShader.uniform1f("gain", _cGain);
		_myTriShader.uniform1f("lacunarity", _cLacunarity);
		_myTriShader.uniform1f("randomOctave0", _cRandomOctave0);
		_myTriShader.uniform1f("randomOctave1", _cRandomOctave1);
		_myTriShader.uniform1f("randomOctaveBlend", _cRandomOctaveBlend);
		_myTriShader.uniform1f("randomOffset", _cRandomOffset);
		_myTriShader.uniform1f("blend", _cBlend);
		_myTriShader.uniform1f("randomBlend", _cRandomBlend);
		
		_myTriShader.uniform1i("tex0", 0);
		_myTriShader.uniform1i("tex1", 1);
		_myTriShader.uniform1i("tex3d", 2);
		
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords2D(0,0);
		g.vertex(-g.width()/2, -g.height()/2);
		g.textureCoords2D(g.width(),0);
		g.vertex( g.width()/2, -g.height()/2);
		g.textureCoords2D(g.width(),g.height());
		g.vertex( g.width()/2,  g.height()/2);
		g.textureCoords2D(0,g.height());
		g.vertex(-g.width()/2,  g.height()/2);
		g.endShape();
		
		_myTriShader.end();
		
		g.noTexture();
		

		if(_cShowTexture)g.image(_myTexture, -_myTexture.width() / 2, -_myTexture.height() / 2);
	}
	
	public static void main(String[] args) {
		CCMosaicTriangleFragmentDemo demo = new CCMosaicTriangleFragmentDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(4400, 340);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
