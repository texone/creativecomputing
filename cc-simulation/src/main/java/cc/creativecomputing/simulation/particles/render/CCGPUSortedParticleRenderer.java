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

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.simulation.particles.CCGPUParticleSort;
import cc.creativecomputing.simulation.particles.CCParticles;

public class CCGPUSortedParticleRenderer extends CCGPUIndexedParticleRenderer{

	private CCGPUParticleSort _mySort;
	private CCGraphics _myGraphics;
		
	public CCGPUSortedParticleRenderer(CCGraphics theGraphics) {
		super(
			CCIOUtil.classPath(CCGPUDisplayShader.class, "shader/sorted/sorted_display_vertex.glsl"),
			CCIOUtil.classPath(CCGPUDisplayShader.class, "shader/sorted/sorted_display_fragment.glsl")
		);
		_myGraphics = theGraphics;
	}
		
	@Override
	public void setup(CCParticles theParticles) {
		super.setup(theParticles);
		_mySort = new CCGPUParticleSort(_myGraphics, theParticles);
	}
		
	public CCGPUParticleSort sort(){
		return _mySort;
	}
		
	@Override
	public void update(float theDeltaTime) {
		_mySort.update(theDeltaTime);
	}
		
	@Override
	public void draw(CCGraphics g) {
		g.gl.glEnable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE);
		_myShader.start();
		g.texture(0, _myParticles.dataBuffer().attachment(0));
		g.texture(1, _myParticles.dataBuffer().attachment(1));
		g.texture(2, _mySort.indices().attachment(0));
		_myShader.uniform1i("positions", 0);
		_myShader.uniform1i("infos", 1);
		_myShader.uniform1i("indices", 2);
		_myShader.uniform1f("tanHalfFOV", CCMath.tan(g.camera().fov()) * g.height);
		_myShader.uniform1f("pointSize", _myPointsize);
		_myMesh.draw(g);
		g.noTexture();
		_myShader.end();
		g.gl.glDisable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE) ;
	}
}
