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
package cc.creativecomputing.simulation.particles.render;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.simulation.particles.CCParticles;

public class CCGPUParticleDataDebugRenderer {
	
	@CCControl(name = "draw positions")
	private boolean _cDrawPositions = false;
	
	@CCControl(name = "scale positions", min = 0, max = 1000)
	private float _cScalePositions = 0;
	
	@CCControl(name = "draw infos")
	private boolean _cDrawInfos = false;
	
	@CCControl(name = "scale infos", min = 0, max = 100)
	private float _cScaleInfos = 0;

	private CCParticles _myParticles;
	
	private CCGLSLShader _myScaleShader;
	
	public CCGPUParticleDataDebugRenderer(CCParticles theParticles){
		_myParticles = theParticles;
		
		_myScaleShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "shader/debug/debug_display_vertex.glsl"),
			CCIOUtil.classPath(this, "shader/debug/debug_display_fragment.glsl")
		);
		_myScaleShader.load();
	}
	
	public void draw(CCGraphics g){
		if(_cDrawPositions)drawData(g, _myParticles.dataBuffer().attachment(0), 0, _cScalePositions);
		if(_cDrawPositions)drawData(g, _myParticles.dataBuffer().attachment(1), _myParticles.width(), _cScaleInfos);
	}
	
	public void drawData(CCGraphics g, CCTexture2D theData, int theX, float theScale){
		
		g.pushMatrix();
		g.translate(-g.width/2, -g.height/2);
		g.texture(0,theData);
		_myScaleShader.start();
		_myScaleShader.uniform1i("data", 0);
		_myScaleShader.uniform1f("scale", theScale);
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords(0, 0, 0);
		g.vertex(theX,0);
		g.textureCoords(0, theData.width(), 0);
		g.vertex(theX + theData.width(),0);
		g.textureCoords(0, theData.width(), theData.height());
		g.vertex(theX + theData.width(), theData.height());
		g.textureCoords(0, 0, theData.height());
		g.vertex(theX, theData.height());
		g.endShape();
		_myScaleShader.end();
		g.noTexture();
		g.popMatrix();
	}
}
