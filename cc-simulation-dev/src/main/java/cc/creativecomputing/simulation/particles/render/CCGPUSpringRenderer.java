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

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.forces.springs.CCGPUSprings;

public class CCGPUSpringRenderer extends CCGPUParticleRenderer{
	
	private CCGPUSprings _mySprings;
	private CCParticles _myParticles;
	
	protected CCGLSLShader _myShader;
	
	protected CCVBOMesh _myMesh;

	public CCGPUSpringRenderer(CCGPUSprings theSprings){
		_myShader = new CCGLSLShader(
			CCIOUtil.classPath(CCGPUDisplayShader.class, "shader/springs/spring_display_vertex.glsl"),
			CCIOUtil.classPath(CCGPUDisplayShader.class, "shader/springs/spring_display_fragment.glsl")
		);
		_myShader.load();
		
		_mySprings = theSprings;
	}

	@Override
	public void setup(CCParticles theParticles) {
		_myParticles = theParticles;
		_myMesh = new CCVBOMesh(CCDrawMode.LINES, _myParticles.size() * 4);
		_myMesh.prepareVertexData(3);
		
		for(int y = 0; y < _myParticles.height();y++) {
			for(int x = 0; x < _myParticles.width();x++) {
				_myMesh.addTextureCoords(0, 0, 1);
				_myMesh.addVertex(x,y,0);
				_myMesh.addTextureCoords(0, 1, 0);
				_myMesh.addVertex(x,y,0);
				_myMesh.addTextureCoords(0, 0, 2);
				_myMesh.addVertex(x,y,0);
				_myMesh.addTextureCoords(0, 2, 0);
				_myMesh.addVertex(x,y,0);
			}
		}
	}

	@Override
	public void update(float theDeltaTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(CCGraphics g) {
		for(int i = 0; i < _mySprings.numberOfSpringTextures();i++){
			_myShader.start();
			g.texture(0, _myParticles.dataBuffer().attachment(0));
			g.texture(1, _myParticles.dataBuffer().attachment(1));
			g.texture(2, _myParticles.dataBuffer().attachment(3));
			g.texture(3, _mySprings.idBuffer(i).attachment(0));
			_myShader.uniform1i("positions", 0);
			_myShader.uniform1i("infos", 1);
			_myShader.uniform1i("colors", 2);
			_myShader.uniform1i("springs", 3);
			_myShader.uniform1f("tanHalfFOV", CCMath.tan(g.camera().fov()) * g.height);
			_myMesh.draw(g);
			g.noTexture();
			_myShader.end();
		}
	}

	@Override
	public CCVBOMesh mesh() {
		return _myMesh;
	}
}
