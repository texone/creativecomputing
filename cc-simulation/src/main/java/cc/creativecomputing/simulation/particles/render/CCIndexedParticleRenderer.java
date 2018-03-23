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

import com.jogamp.opengl.GL2;

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.core.CCAnimator;
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
import cc.creativecomputing.simulation.particles.forces.CCForce;

/**
 * @author christianriekoff
 *
 */
public class CCIndexedParticleRenderer extends CCParticleRenderer{
	
	@CCProperty(name = "life time alpha")
	private CCEnvelope _cLifeTimeAlpha = new CCEnvelope();

	protected CCVBOMesh _myMesh;
	
	protected CCParticles _myParticles;
	
	protected CCGLProgram _myShader;
	
	protected float _myPointsize = 1;
	
	private CCShaderBuffer _myEvelopeData;
	
	private CCGLWriteDataShader _myWriteDataShader;
	
	public CCIndexedParticleRenderer(Path theVertexShader, Path theFragmentShader) {
		_myShader = new CCGLProgram(theVertexShader, theFragmentShader);
		
		_myWriteDataShader = new CCGLWriteDataShader();
		_myEvelopeData = new CCShaderBuffer(100,1);
	}
	
	public CCIndexedParticleRenderer() {
		this(
			CCNIOUtil.classPath(CCDisplayShader.class, "indexed_display_vertex.glsl"),
			CCNIOUtil.classPath(CCDisplayShader.class, "indexed_display_fragment.glsl")
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
	public void updateData(CCGraphics g) {}

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
