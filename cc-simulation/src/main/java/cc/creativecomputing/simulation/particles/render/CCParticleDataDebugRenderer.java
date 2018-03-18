/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.simulation.particles.render;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.simulation.particles.CCParticles;

public class CCParticleDataDebugRenderer {
	
	@CCProperty(name = "draw positions")
	private boolean _cDrawPositions = false;
	
	@CCProperty(name = "scale positions", min = 0, max = 1000)
	private double _cScalePositions = 0;
	
	@CCProperty(name = "draw infos")
	private boolean _cDrawInfos = false;
	
	@CCProperty(name = "scale infos", min = 0, max = 100)
	private double _cScaleInfos = 0;

	private CCParticles _myParticles;
	
	private CCGLProgram _myScaleShader;
	
	public CCParticleDataDebugRenderer(CCParticles theParticles){
		_myParticles = theParticles;
		
		_myScaleShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "debug_display_vertex.glsl"),
			CCNIOUtil.classPath(this, "debug_display_fragment.glsl")
		);
	}
	
	
	
	public void draw(CCGraphics g){
		if(_cDrawPositions)drawData(g, _myParticles.dataBuffer().attachment(0), 0, _cScalePositions);
		if(_cDrawInfos)drawData(g, _myParticles.dataBuffer().attachment(1), _myParticles.width(), _cScaleInfos);
	}
	
	public void drawData(CCGraphics g, CCTexture2D theData, int theX, double theScale){
		
		g.pushMatrix();
		g.translate(-g.width()/2, -g.height()/2);
		g.texture(0,theData);
		_myScaleShader.start();
		_myScaleShader.uniform1i("data", 0);
		_myScaleShader.uniform1f("scale", theScale);
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords2D(0, 0, 0);
		g.vertex(theX,0);
		g.textureCoords2D(0, theData.width(), 0);
		g.vertex(theX + theData.width(),0);
		g.textureCoords2D(0, theData.width(), theData.height());
		g.vertex(theX + theData.width(), theData.height());
		g.textureCoords2D(0, 0, theData.height());
		g.vertex(theX, theData.height());
		g.endShape();
		_myScaleShader.end();
		g.noTexture();
		g.popMatrix();
	}
}
