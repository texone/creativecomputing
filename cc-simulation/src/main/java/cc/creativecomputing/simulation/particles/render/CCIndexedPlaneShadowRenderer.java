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
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.simulation.particles.CCParticles;

/**
 * @author christianriekoff
 *
 */
public class CCIndexedPlaneShadowRenderer extends CCParticleRenderer{

	protected CCVBOMesh _myMesh;
	
	protected CCParticles _myParticles;
	
	protected CCShaderBuffer _myIndexTexture;
	
	protected CCGLProgram _myShader;
	
	protected CCGraphics g;
	
	protected float _myPointsize = 5;
	
	public CCIndexedPlaneShadowRenderer(CCGraphics theGraphics, Path theVertexShader, Path theFragmentShader) {
		super("points indexed shadow");
		_myShader = new CCGLProgram(theVertexShader, theFragmentShader);
		g = theGraphics;
	}
	
	public CCIndexedPlaneShadowRenderer(CCGraphics theGraphics) {
		this(
			theGraphics,
			CCNIOUtil.classPath(CCDisplayShader.class, "indexed_display_vertex.glsl"),
			CCNIOUtil.classPath(CCDisplayShader.class, "indexed_display_fragment.glsl")
		);
	}
	
	public void setup(CCParticles theParticles) {
		_myParticles = theParticles;
		_myMesh = new CCVBOMesh(CCDrawMode.POINTS, _myParticles.size());
		_myMesh.prepareVertexData(3);
		_myIndexTexture = new CCShaderBuffer(_myParticles.width(), _myParticles.height());
		
		_myIndexTexture.beginDraw(g);
		g.clear();
		_myParticles.initValueShader().start();
		g.beginShape(CCDrawMode.POINTS);
		for(int y = 0; y < _myParticles.height();y++) {
			for(int x = 0; x < _myParticles.width();x++) {
				g.textureCoords2D(0, (float)x, (float)y);
				g.vertex(x,y);
				_myMesh.addVertex(x,y,0);
			}
		}
		g.endShape();
		
		_myParticles.initValueShader().end();
		_myIndexTexture.endDraw(g);
	}
	
	public void pointSize(float thePointSize){
		_myPointsize = thePointSize;
	}
	
	@Override
	public void update(CCAnimator theAnimator) {}
	
	@Override
	public void preDisplay(CCGraphics g) {}

	public void display(CCGraphics g){
		if(!_cIsActive)return;
		_cAttributes.start(g);
		g.programPointSize();
		_myShader.start();
		g.texture(0, _myParticles.dataBuffer().attachment(0));
		g.texture(1, _myParticles.dataBuffer().attachment(1));
		_myShader.uniform1i("positions", 0);
		_myShader.uniform1i("infos", 1);
		_myShader.uniform1f("tanHalfFOV", CCMath.tan(g.camera().fov()) * g.height());
		_myMesh.draw(g);
		g.noTexture();
		_myShader.end();
		g.noProgramPointSize();
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
