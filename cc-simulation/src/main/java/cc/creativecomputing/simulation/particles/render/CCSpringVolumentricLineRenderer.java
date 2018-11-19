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

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.forces.springs.CCSpringForce;

public class CCSpringVolumentricLineRenderer extends CCParticleRenderer{
	
	private CCSpringForce _mySprings;
	private CCParticles _myParticles;
	
	@CCProperty(name = "shader")
	protected CCGLProgram _myShader;
	
	protected CCVBOMesh _myMesh;
	
	private boolean _myIncludeCaps;
	
	@CCProperty(name = "life time alpha")
	private CCEnvelope _cLifeTimeAlpha = new CCEnvelope();
	
	private int _myLifeTimeAlphaIndex;

	public CCSpringVolumentricLineRenderer(CCSpringForce theSprings, boolean theIncludeCaps){
		super("springs volumetric lines");
		_myShader = new CCGLProgram(
			CCNIOUtil.classPath(CCSpringVolumentricLineRenderer.class, "spring_lines_volumentric_vertex.glsl"),
			CCNIOUtil.classPath(CCSpringVolumentricLineRenderer.class, "spring_lines_volumentric_fragment.glsl")
		);
		
		_mySprings = theSprings;
		_myIncludeCaps = theIncludeCaps;
	}

	@Override
	public void setup(CCParticles theParticles) {
		_myLifeTimeAlphaIndex = theParticles.envelopeData().add(_cLifeTimeAlpha);
		_myParticles = theParticles;
		_myMesh = new CCVBOMesh(CCDrawMode.QUADS, _myParticles.size() * (_myIncludeCaps ? 12 : 4));
		
		for(int y = 0; y < _myParticles.height();y++) {
			for(int x = 0; x < _myParticles.width();x++) {
//				_myMesh.addTextureCoords(0, 0, 1);
//				_myMesh.addVertex(x,y,0);
//				_myMesh.addTextureCoords(0, 1, 0);
//				_myMesh.addVertex(x,y,0);
				
				if(_myIncludeCaps) {
					_myMesh.addTextureCoords(0, 0, 1);
					_myMesh.addTextureCoords(0, 0, 1);
					_myMesh.addTextureCoords(0, 0, 1);
					_myMesh.addTextureCoords(0, 0, 1);
	
					_myMesh.addTextureCoords(1, 1.0f, 1.0f, 1.0f, 1.0f);
					_myMesh.addTextureCoords(1, 1.0f,-1.0f, 1.0f, 0.0f);
					_myMesh.addTextureCoords(1, 0.0f,-1.0f, 0.5f, 0.0f);
					_myMesh.addTextureCoords(1, 0.0f, 1.0f, 0.5f, 1.0f);
	
					_myMesh.addVertex(x,y,0);
					_myMesh.addVertex(x,y,0);
					_myMesh.addVertex(x,y,0);
					_myMesh.addVertex(x,y,0);
				}

				_myMesh.addTextureCoords(0, 0, 1);
				_myMesh.addTextureCoords(0, 0, 1);
				_myMesh.addTextureCoords(0, 1, 0);
				_myMesh.addTextureCoords(0, 1, 0);
				
				_myMesh.addTextureCoords(1, 0.0f,  1.0f, 0.5f, 1.0f);
				_myMesh.addTextureCoords(1, 0.0f, -1.0f, 0.5f, 0.0f);
				_myMesh.addTextureCoords(1, 0.0f, -1.0f, 0.5f, 0.0f);
				_myMesh.addTextureCoords(1, 0.0f,  1.0f, 0.5f, 1.0f);

				_myMesh.addVertex(x,y,0);
				_myMesh.addVertex(x,y,0);
				_myMesh.addVertex(x,y,0);
				_myMesh.addVertex(x,y,0);
		
				if(_myIncludeCaps) {
					_myMesh.addTextureCoords(0, 1, 0);
					_myMesh.addTextureCoords(0, 1, 0);
					_myMesh.addTextureCoords(0, 1, 0);
					_myMesh.addTextureCoords(0, 1, 0);
					
					_myMesh.addTextureCoords(1, -1.0f, 1.0f, 0.0f, 1.0f);
					_myMesh.addTextureCoords(1, -1.0f,-1.0f, 0.0f, 0.0f);
					_myMesh.addTextureCoords(1, 0.0f,-1.0f, 0.5f, 0.0f);
					_myMesh.addTextureCoords(1, 0.0f, 1.0f, 0.5f, 1.0f);
	
					_myMesh.addVertex(x,y,0);
					_myMesh.addVertex(x,y,0);
					_myMesh.addVertex(x,y,0);
					_myMesh.addVertex(x,y,0);
				}
			}
		}
	}
	
	private double _myTime = 0;

	@Override
	public void update(CCAnimator theDeltaTime) {
		_myTime += theDeltaTime.deltaTime();
	}

	@Override
	public void display(CCGraphics g) {
		if(!_cIsActive)return;
		_cAttributes.start(g);
		for(int i = 0; i < _mySprings.numberOfSpringTextures();i++){
			_myShader.start();
			g.texture(0, _myParticles.dataBuffer().attachment(0));
			g.texture(1, _myParticles.dataBuffer().attachment(1));
			g.texture(2, _myParticles.dataBuffer().attachment(3));
			g.texture(3, _mySprings.idBuffer().attachment(0));
			g.texture(4, _myParticles.envelopeTexture());
			_myShader.uniform1i("positions", 0);
			_myShader.uniform1i("infos", 1);
			_myShader.uniform1i("colors", 2);
			_myShader.uniform1i("springs", 3);
			_myShader.uniform1i("lifeTimeBlends", 4);
			_myShader.uniform1f("lifeTimeID", _myLifeTimeAlphaIndex);
			_myShader.uniform1f("tanHalfFOV", CCMath.tan(g.camera().fov()) * g.height());
			_myShader.uniform1f("time", _myTime);
			_myShader.uniform1f("aspectRatio", g.aspectRatio());
			_myShader.uniform2f("offset", i % _mySprings.numberOfXBuffers() * _myParticles.width(), i / _mySprings.numberOfXBuffers() * _myParticles.height());
			_myMesh.draw(g);
			g.noTexture();
			_myShader.end();
		}
		_cAttributes.end(g);
	}

	@Override
	public CCVBOMesh mesh() {
		return _myMesh;
	}
}
