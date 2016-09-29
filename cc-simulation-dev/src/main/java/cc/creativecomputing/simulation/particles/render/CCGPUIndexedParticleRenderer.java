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

import javax.media.opengl.GL2;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.simulation.particles.CCParticles;

/**
 * @author christianriekoff
 *
 */
public class CCGPUIndexedParticleRenderer extends CCGPUParticleRenderer{

	protected CCVBOMesh _myMesh;
	
	protected CCParticles _myParticles;
	
	protected CCGLSLShader _myShader;
	
	protected float _myPointsize = 1;
	
	public CCGPUIndexedParticleRenderer(String theVertexShader, String theFragmentShader) {
		_myShader = new CCGLSLShader(theVertexShader, theFragmentShader);
		_myShader.load();
	}
	
	public CCGPUIndexedParticleRenderer() {
		this(
			CCIOUtil.classPath(CCGPUDisplayShader.class, "shader/indexed/indexed_display_vertex.glsl"),
			CCIOUtil.classPath(CCGPUDisplayShader.class, "shader/indexed/indexed_display_fragment.glsl")
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
	
	public void update(final float theDeltaTime) {
//		_myMesh.vertices(_myParticles.positions());
	}

	public void draw(CCGraphics g){
		g.gl.glEnable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE);
		_myShader.start();
		g.texture(0, _myParticles.dataBuffer().attachment(0));
		g.texture(1, _myParticles.dataBuffer().attachment(1));
		g.texture(3, _myParticles.dataBuffer().attachment(3));
		_myShader.uniform1i("positions", 0);
		_myShader.uniform1i("infos", 1);
		_myShader.uniform1i("colors", 3);
		_myShader.uniform1f("pointSize", _myPointsize);
		_myShader.uniform1f("tanHalfFOV", CCMath.tan(g.camera().fov()) * g.height);
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
