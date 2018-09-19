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

import com.jogamp.opengl.GL2;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCGLWriteDataShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.simulation.particles.CCParticles;

/**
 * @author christianriekoff
 *
 */
public class CCParticleGroupRenderer extends CCParticleRenderer{
	
	@CCProperty(name = "progress alpha")
	private CCEnvelope _cLifeTimeAlpha = new CCEnvelope();

	protected CCVBOMesh _myMesh;
	
	protected CCParticles _myParticles;
	@CCProperty(name = "shader")
	protected CCGLProgram _myShader;
	
	protected float _myPointsize = 1;
	
	private CCShaderBuffer _myEvelopeData;
	
	private CCGLWriteDataShader _myWriteDataShader;
	
	public CCParticleGroupRenderer(Path theVertexShader, Path theFragmentShader) {
		_myShader = new CCGLProgram(theVertexShader, theFragmentShader);
		
		_myWriteDataShader = new CCGLWriteDataShader();
		_myEvelopeData = new CCShaderBuffer(100,1);
	}
	
	public CCParticleGroupRenderer() {
		this(
			CCNIOUtil.classPath(CCDisplayShader.class, "group_display_vertex.glsl"),
			CCNIOUtil.classPath(CCDisplayShader.class, "group_display_fragment.glsl")
		);
	}
	
	public void setup(CCParticles theParticles) {
		_myParticles = theParticles;
		_myMesh = new CCVBOMesh(CCDrawMode.POINTS, _myParticles.size());
		_myMesh.prepareVertexData(3);
		
		for(int y = 0; y < _myParticles.height();y++) {
			for(int x = 0; x < _myParticles.width();x++) {
				_myMesh.addVertex(x,y,0);
			}
		}
	}
	
	public void pointSize(float thePointSize){
		_myPointsize = thePointSize;
	}
	
	@Override
	public void update(CCAnimator theAnimator) {}
	
	@Override
	public void preDisplay(CCGraphics g) {}

	public void display(CCGraphics g){
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
		
		g.gl.glEnable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE);
		_myShader.start();
		g.texture(0, _myParticles.dataBuffer().attachment(0));
		g.texture(1, _myParticles.dataBuffer().attachment(1));
		g.texture(3, _myParticles.dataBuffer().attachment(3));
		g.texture(4, _myEvelopeData.attachment(0));
		_myShader.uniform1i("positions", 0);
		_myShader.uniform1i("infos", 1);
		_myShader.uniform1i("colors", 3);
		_myShader.uniform1i("lifeTimeBlends", 4);
		_myShader.uniform1f("pointSize", _myPointsize);
		_myShader.uniform1f("tanHalfFOV", CCMath.tan(g.camera().fov()) * g.height());
		_myMesh.draw(g);
		g.noTexture();
		_myShader.end();
		g.gl.glDisable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE) ;
	}
	
	public CCVBOMesh mesh(){
		return _myMesh;
	}
	
	public void pointSizeClamp(final float theMinPointSize, final float theMaxPointSize) {
//		if(_myDisplayShader == null)_myDisplayShader = new CCGPUDisplayShader();
//		_myDisplayShader.minPointSize(theMinPointSize);
//		_myDisplayShader.maxPointSize(theMinPointSize);
	}
}
