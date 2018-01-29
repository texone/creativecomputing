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

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCGLWriteDataShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.forces.CCForce;

/**
 * @author christianriekoff
 *
 */
public class CCParticleTrailRenderer extends CCParticleRenderer{
	
	@CCProperty(name = "life time alpha")
	private CCEnvelope _cLifeTimeAlpha = new CCEnvelope();

	protected CCVBOMesh _myMesh;
	protected CCVBOMesh _myWriteMesh;
	
	protected CCParticles _myParticles;
	
	@CCProperty(name = "shader")
	protected CCGLProgram _myShader;
	
	protected float _myPointsize = 1;
	
	private CCShaderBuffer _myTrailData;
	
	private CCGLProgram _myWriteDataShader;
	
	private final int _myTrailLength;
	
	private int _myIndex = 0;
	
	public CCParticleTrailRenderer(Path theVertexShader, Path theFragmentShader, int theTrailLength) {
		_myShader = new CCGLProgram(theVertexShader, theFragmentShader);
		_myTrailLength = theTrailLength;
		_myWriteDataShader = new CCGLProgram(
			CCNIOUtil.classPath(CCDisplayShader.class, "write_list_vertex.glsl"),
			CCNIOUtil.classPath(CCDisplayShader.class, "write_list_fragment.glsl")
		);
	}
	
	public CCParticleTrailRenderer(int theTrailLength) {
		this(
			CCNIOUtil.classPath(CCDisplayShader.class, "trails_vertex.glsl"),
			CCNIOUtil.classPath(CCDisplayShader.class, "trails_fragment.glsl"),
			theTrailLength
		);
	}
	
	public void setup(CCParticles theParticles) {
		_myParticles = theParticles;
		_myMesh = new CCVBOMesh(CCDrawMode.LINES, _myParticles.size() *( _myTrailLength - 1) * 2);
		_myMesh.prepareVertexData(3);
		_myWriteMesh = new CCVBOMesh(CCDrawMode.POINTS, _myParticles.size());
		_myWriteMesh.prepareVertexData(3);
		
		for(int y = 0; y < _myParticles.height();y++) {
			for(int x = 0; x < _myParticles.width();x++) {
				for(int i = 0; i < _myTrailLength - 1;i++){
					_myMesh.addVertex(x,y,i);
					_myMesh.addVertex(x,y,i + 1);
				}

				_myWriteMesh.addVertex(x,y,0);
			}
		}
		_myTrailData = new CCShaderBuffer(theParticles.width() * _myTrailLength,theParticles.height());
	}
	
	public void pointSize(float thePointSize){
		_myPointsize = thePointSize;
	}
	
	public CCTexture2D trailData(){
		return _myTrailData.attachment(0);
	}
	
	@Override
	public void update(CCAnimator theAnimator) {}
	
	@Override
	public void updateData(CCGraphics g) {}

	boolean y0 = true;
	int i = 0;
	public void display(CCGraphics g){
		if(y0){
			_myTrailData.clear(g);
			y0 = false;
		}
		_myTrailData.beginDraw(g);
		CCLog.info(_myTrailData.width(), _myTrailData.height());
//		g.clear();
		g.pushAttribute();
		g.noBlend();
		g.pointSize(1);
		_myWriteDataShader.start();
		g.texture(0, _myParticles.dataBuffer().attachment(0));
		_myWriteDataShader.uniform1i("positions", 0);
		_myWriteDataShader.uniform1f("listSize", 20);
		
		_myWriteDataShader.uniform1f("listIndex", i % _myTrailLength);
		CCLog.info(i % _myTrailLength);
		i++;
		_myWriteMesh.draw(g);
		g.noTexture();
		_myWriteDataShader.end();
		g.popAttribute();
		_myTrailData.endDraw(g);
		
		g.gl.glEnable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE);
		_myShader.start();
		g.texture(0, _myParticles.dataBuffer().attachment(0));
		g.texture(1, _myParticles.dataBuffer().attachment(1));
		g.texture(3, _myParticles.dataBuffer().attachment(3));
		g.texture(4, _myTrailData.attachment(0));
		_myShader.uniform1i("positions", 0);
		_myShader.uniform1i("infos", 1);
		_myShader.uniform1i("colors", 3);
		_myShader.uniform1i("trails", 4);
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
