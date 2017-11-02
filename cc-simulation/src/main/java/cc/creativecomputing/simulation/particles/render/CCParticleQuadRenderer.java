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

import java.nio.file.Path;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.simulation.particles.CCParticles;

/**
 * @author christianriekoff
 *
 */
public class CCParticleQuadRenderer extends CCParticleRenderer{

	protected CCVBOMesh _myMesh;
	
	protected CCParticles _myParticles;
	
	protected CCGLProgram _myShader;
	
	protected float _myPointsize = 1;
	
	public CCParticleQuadRenderer(Path theVertexShader, Path theFragmentShader) {
		_myShader = new CCGLProgram(theVertexShader, theFragmentShader);
	}
	
	public CCParticleQuadRenderer() {
		this(
			CCNIOUtil.classPath(CCDisplayShader.class, "quad_display_vertex.glsl"),
			CCNIOUtil.classPath(CCDisplayShader.class, "quad_display_fragment.glsl")
		);
	}
	
	public void setup(CCParticles theParticles) {
		_myParticles = theParticles;
		_myMesh = new CCVBOMesh(CCDrawMode.QUADS, _myParticles.size() * 4);
		_myMesh.prepareVertexData(3);
		
		for(int y = 0; y < _myParticles.height();y++) {
			for(int x = 0; x < _myParticles.width();x++) {
				_myMesh.addVertex(x,y,0);
				_myMesh.addTextureCoords(0, -1, -1);
				_myMesh.addVertex(x,y,0);
				_myMesh.addTextureCoords(0,  1, -1);
				_myMesh.addVertex(x,y,0);
				_myMesh.addTextureCoords(0,  1,  1);
				_myMesh.addVertex(x,y,0);
				_myMesh.addTextureCoords(0, -1,  1);
			}
		}
	}
	
	public void pointSize(float thePointSize){
		_myPointsize = thePointSize;
	}
	
	@Override
	public void update(CCAnimator theAnimator) {}
	
	@Override
	public void updateData(CCGraphics g) {}

	public void display(CCGraphics g){
		_myShader.start();
		g.texture(0, _myParticles.dataBuffer().attachment(0));
		g.texture(1, _myParticles.dataBuffer().attachment(1));
		g.texture(2, _myParticles.dataBuffer().attachment(2));
		g.texture(3, _myParticles.dataBuffer().attachment(3));
		_myShader.uniform1i("positions", 0);
		_myShader.uniform1i("infos", 1);
		_myShader.uniform1i("velocities", 2);
		_myShader.uniform1i("colors", 3);
		_myShader.uniform1f("pointSize", _myPointsize);
		_myMesh.draw(g);
		g.noTexture();
		_myShader.end();
	}
	
	public CCVBOMesh mesh(){
		return _myMesh;
	}
}
