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

import com.jogamp.opengl.GL2;

import cc.creativecomputing.core.CCAnimator;
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
	public void update(final CCAnimator theDeltaTime) {
	}
	
	@Override
	public void updateData(CCGraphics g) {
		_myMesh.colors(_myParticles.dataBuffer(),3);
		_myMesh.vertices(_myParticles.dataBuffer(),0);
	}

	@Override
	public void display(CCGraphics g){
		g.gl.glEnable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE);
		_myDisplayShader.start();
		_myDisplayShader.tangHalfFov(CCMath.tan(g.camera().fov()) * g.height());
		_myMesh.draw(g);
		_myDisplayShader.end();
		g.gl.glDisable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE) ;
	}
	
	public CCVBOMesh mesh(){
		return _myMesh;
	}
	
	public void pointSize(float thePointSize) {
		if(_myDisplayShader == null)_myDisplayShader = new CCDisplayShader();
		_myDisplayShader.pointSize(thePointSize);
	}
}
