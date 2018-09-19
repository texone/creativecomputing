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

import cc.creativecomputing.app.modules.CCAnimator;
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
	public void preDisplay(CCGraphics g) {
		_myMesh.colors(_myParticles.dataBuffer(),3);
		_myMesh.vertices(_myParticles.dataBuffer(),0);
	}

	@Override
	public void display(CCGraphics g){
//		g.gl.glEnable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE);
		_myDisplayShader.start();
		_myDisplayShader.tangHalfFov(CCMath.tan(g.camera().fov()) * g.height());
		_myMesh.draw(g);
		_myDisplayShader.end();
//		g.gl.glDisable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE) ;
	}
	
	public CCVBOMesh mesh(){
		return _myMesh;
	}
	
	public void pointSize(float thePointSize) {
		if(_myDisplayShader == null)_myDisplayShader = new CCDisplayShader();
		_myDisplayShader.pointSize(thePointSize);
	}
}
