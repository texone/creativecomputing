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

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.simulation.particles.CCParticles;

/**
 * @author christianriekoff
 *
 */
public class CCParticlePointRenderer extends CCParticleRenderer{

	protected CCVBOMesh _myMesh;
	
	private CCParticles _myParticles;
	private CCDisplayShader _myDisplayShader;
	
	public CCParticlePointRenderer() {
		_myDisplayShader = new CCDisplayShader();
	}
	
	@Override
	public void setup(CCParticles theParticles) {
		_myParticles = theParticles;
		
		_myMesh = new CCVBOMesh(CCDrawMode.POINTS, _myParticles.size());
	}
	
	@Override
	public void update(final CCGLTimer theDeltaTime) {
	}
	
	@Override
	public void updateData(CCGraphics g) {
		_myMesh.colors(_myParticles.dataBuffer(),3);
		_myMesh.vertices(_myParticles.dataBuffer(),0);
	}

	@Override
	public void display(CCGraphics g){
		GL11.glEnable(GL20.GL_VERTEX_PROGRAM_POINT_SIZE);
		_myDisplayShader.start();
		_myDisplayShader.tangHalfFov(CCMath.tan(g.camera().fov()) * g.height());
		_myMesh.draw(g);
		_myDisplayShader.end();
		GL11.glDisable(GL20.GL_VERTEX_PROGRAM_POINT_SIZE) ;
	}
	
	public CCVBOMesh mesh(){
		return _myMesh;
	}
	
	public void pointSize(float thePointSize) {
		if(_myDisplayShader == null)_myDisplayShader = new CCDisplayShader();
		_myDisplayShader.pointSize(thePointSize);
	}
}
