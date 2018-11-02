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

import java.nio.file.Path;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCGLWriteDataShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.simulation.particles.CCParticles;

/**
 * @author christianriekoff
 *
 */
public class CCQuadRenderer extends CCParticleRenderer{

	@CCProperty(name = "life time alpha")
	private CCEnvelope _cLifeTimeAlpha = new CCEnvelope();
	@CCProperty(name = "gradient")
	private CCGradient _cGradient = new CCGradient();

	protected CCMesh _myMesh;
	
	private CCParticles _myParticles;
	
	protected CCShaderBuffer _myIndexTexture;
	
	@CCProperty(name = "shader")
	private CCGLProgram _myShader;
	
	private CCGLWriteDataShader _myWriteDataShader;
	
	private CCShaderBuffer _myEvelopeData;
	private CCShaderBuffer _myGradientData;
	
	@CCProperty(name = "point size", min = 0, max = 10)
	private double _cPointSize = 5;
	
	private boolean _myFadeOut = false;
	
	public CCQuadRenderer(
		Path theVertexPath,
		Path theFragmentPath
	) {
		super("quad");
		_myShader = new CCGLProgram(theVertexPath,theFragmentPath);
		
		_myWriteDataShader = new CCGLWriteDataShader();
		_myEvelopeData = new CCShaderBuffer(100,1);
		_myGradientData = new CCShaderBuffer(100,1);
	}
	
	public CCQuadRenderer() {
		this(
			CCNIOUtil.classPath(CCQuadRenderer.class,"quad_vertex.glsl"), 
			CCNIOUtil.classPath(CCQuadRenderer.class,"quad_fragment.glsl")
		);
	}
	public void setup(CCParticles theParticles) {
		_myParticles = theParticles;
		_myMesh = new CCMesh(CCDrawMode.QUADS, _myParticles.size() * 4);
		_myMesh.prepareVertexData(4);
		_myMesh.prepareTextureCoordData(0, 2);
		
		for(int x = 0; x < _myParticles.width();x++) {
			for(int y = 0; y < _myParticles.height();y++) {
				_myMesh.addVertex(x,y,-1,-1);
				_myMesh.addVertex(x,y, 1,-1);
				_myMesh.addVertex(x,y, 1, 1);
				_myMesh.addVertex(x,y,-1, 1);
				
				_myMesh.addTextureCoords(0,0);
				_myMesh.addTextureCoords(1,0);
				_myMesh.addTextureCoords(1,1);
				_myMesh.addTextureCoords(0,1);
			}
		}
	}
	
	@Override
	public void update(CCAnimator theAnimator) {}
	
	@Override
	public void preDisplay(CCGraphics g) {}
	
	public void fadeOut(boolean theFadeOut) {
		_myFadeOut = theFadeOut;
	}

	public void display(CCGraphics g){
		if(!_cIsActive)return;
		_cAttributes.start(g);
		_myEvelopeData.beginDraw(g);
		g.clear();
		g.pushAttribute();
		g.noBlend();
		g.pointSize(1);
		_myWriteDataShader.start();
		g.beginShape(CCDrawMode.POINTS);
		for(int i = 0; i < 100; i++){
			double myVal = _cLifeTimeAlpha.value(i / 100d);
			g.textureCoords4D(0, myVal, myVal, myVal, 1d);
			g.vertex(i + 0.5, 0.5);
		}
		g.endShape();
		_myWriteDataShader.end();
		g.popAttribute();
		_myEvelopeData.endDraw(g);
		
		_myGradientData.beginDraw(g);
		g.clear();
		g.pushAttribute();
		g.noBlend();
		g.pointSize(1);
		_myWriteDataShader.start();
		g.beginShape(CCDrawMode.POINTS);
		for(int i = 0; i < 100; i++){
			CCColor myVal = _cGradient.color(i / 100d);
			g.textureCoords4D(0, myVal.r, myVal.g, myVal.b, 1d);
			g.vertex(i + 0.5, 0.5);
		}
		g.endShape();
		_myWriteDataShader.end();
		g.popAttribute();
		_myGradientData.endDraw(g);
		
		_myShader.start();
		g.texture(0, _myParticles.dataBuffer().attachment(0));
		g.texture(1, _myParticles.dataBuffer().attachment(1));
		g.texture(3, _myParticles.dataBuffer().attachment(3));
		g.texture(4, _myEvelopeData.attachment(0));
		g.texture(5, _myGradientData.attachment(0));
		_myShader.uniform1i("positions", 0);
		_myShader.uniform1i("infos", 1);
		_myShader.uniform1i("pointSprite", 2);
		_myShader.uniform1i("colors", 3);
		_myShader.uniform1i("lifeTimeBlends", 4);
		_myShader.uniform1i("gradient", 5);
		_myShader.uniform1f("tanHalfFOV", CCMath.tan(g.camera().fov()) * g.height());
		_myShader.uniform1f("pointSize", _cPointSize);
		_myShader.uniform1f("aspectRatio", g.width() / (float)g.height());
		_myShader.uniform1f("alpha", _myFadeOut ? 0 : 1);
		_myMesh.draw(g);
		g.noTexture();
		_myShader.end();
		_cAttributes.end(g);
	}
	
	public CCMesh mesh(){
		return _myMesh;
	}
}
