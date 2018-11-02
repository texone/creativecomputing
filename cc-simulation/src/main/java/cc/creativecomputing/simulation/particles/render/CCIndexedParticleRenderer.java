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
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCDataBuffer;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCGLWriteDataShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.time.CCDate;
import cc.creativecomputing.simulation.particles.CCParticles;

/**
 * @author christianriekoff
 *
 */
public class CCIndexedParticleRenderer extends CCParticleRenderer{
	
	@CCProperty(name = "life time alpha")
	private CCEnvelope _cLifeTimeAlpha = new CCEnvelope();
	@CCProperty(name = "shader")
	protected CCGLProgram _myShader;
	
	private CCDataBuffer _myEvelopeData;

	protected CCVBOMesh _myMesh;
	
	protected CCParticles _myParticles;
	
	@CCProperty(name = "pointsize", min = 0, max = 10)
	protected float _cPointsize = 1;
	
	public CCIndexedParticleRenderer(Path theVertexShader, Path theFragmentShader) {
		super("points indexed");
		_myShader = new CCGLProgram(theVertexShader, theFragmentShader);
		
		_myEvelopeData = new CCDataBuffer(100,_cLifeTimeAlpha);
	}
	
	public CCIndexedParticleRenderer() {
		this(
			CCNIOUtil.classPath(CCDisplayShader.class, "indexed_display_vertex.glsl"),
			CCNIOUtil.classPath(CCDisplayShader.class, "indexed_display_fragment.glsl")
		);
	}
	
	public CCTexture2D envelopeData() {
		return _myEvelopeData.attachment(0);
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
		_cPointsize = thePointSize;
	}
	
	@Override
	public void update(CCAnimator theAnimator) {}
	

	@Override
	public void preDisplay(CCGraphics g) {
		if(!_cIsActive)return;
		_myEvelopeData.preDisplay(g);
	}
	
	public void display(CCGraphics g){
		if(!_cIsActive)return;
		_cAttributes.start(g);
		g.programPointSize();
		_myShader.start();
		startShaderEvents.proxy().start(_myShader, g);
		g.texture(0, _myParticles.dataBuffer().attachment(0));
		g.texture(1, _myParticles.dataBuffer().attachment(1));
		g.texture(3, _myParticles.dataBuffer().attachment(3));
		g.texture(4, _myEvelopeData.attachment(0));
		_myShader.uniform1i("positions", 0);
		_myShader.uniform1i("infos", 1);
		_myShader.uniform1i("colors", 3);
		_myShader.uniform1i("lifeTimeBlends", 4);
		_myShader.uniform1f("pointSize", _cPointsize);
		_myShader.uniform1f("tanHalfFOV", CCMath.tan(g.camera().fov()) * g.height());
		g.color(255);
		_myMesh.draw(g);
		g.noTexture();
		_myShader.end();
		g.noProgramPointSize();
		_cAttributes.end(g);
	}
	
	public CCMesh mesh(){
		return _myMesh;
	}
	
	public void pointSizeClamp(final float theMinPointSize, final float theMaxPointSize) {
//		if(_myDisplayShader == null)_myDisplayShader = new CCGPUDisplayShader();
//		_myDisplayShader.minPointSize(theMinPointSize);
//		_myDisplayShader.maxPointSize(theMinPointSize);
	}
}
