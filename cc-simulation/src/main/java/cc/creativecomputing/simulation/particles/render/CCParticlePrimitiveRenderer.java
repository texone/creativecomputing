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

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticles;

/**
 * @author christianriekoff
 *
 */
public class CCParticlePrimitiveRenderer extends CCParticleRenderer{

	public static enum CCParticlePrimitive{
		QUAD,
		TRIANGLE,
		BOX,
		LINE,
		PYRAMIDE
	}
	
	protected CCVBOMesh _myMesh;
	
	protected CCParticles _myParticles;
	
	@CCProperty(name = "shader")
	protected CCGLProgram _myShader;
	
	@CCProperty(name = "point size", min = 0, max = 20)
	protected double _myPointsize = 1;
	
	@CCProperty(name = "scale")
	protected CCEnvelope _myScaleEnvelope = new CCEnvelope();
	
	private final CCParticlePrimitive _myPrimitive;
	
	public CCParticlePrimitiveRenderer(Path theVertexShader, Path theFragmentShader, CCParticlePrimitive thePrimitive) {
		_myShader = new CCGLProgram(theVertexShader, theFragmentShader);
		_myPrimitive = thePrimitive;
	}
	
	public CCParticlePrimitiveRenderer(CCParticlePrimitive thePrimitive) {
		this(
			CCNIOUtil.classPath(CCDisplayShader.class, "quad_display_vertex.glsl"),
			CCNIOUtil.classPath(CCDisplayShader.class, "quad_display_fragment.glsl"),
			thePrimitive
		);

		_myEnvelopes.add(_myScaleEnvelope);
	}
	
	public void setup(CCParticles theParticles) {
		_myParticles = theParticles;
		switch(_myPrimitive){
		case LINE:
			_myMesh = new CCVBOMesh(CCDrawMode.LINES, _myParticles.size() * 2);
			_myMesh.prepareVertexData(3);
			
			for(int y = 0; y < _myParticles.height();y++) {
				for(int x = 0; x < _myParticles.width();x++) {
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, 0, 0, -1);
					_myMesh.addNormal(0,1,0);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, 0, 0,  1);
					_myMesh.addNormal(0,1,0);
				}
			}
			break;
		case TRIANGLE:
			_myMesh = new CCVBOMesh(CCDrawMode.TRIANGLES, _myParticles.size() * 3);
			_myMesh.prepareVertexData(3);
			
			for(int y = 0; y < _myParticles.height();y++) {
				for(int x = 0; x < _myParticles.width();x++) {
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, 0, -1, 0);
					_myMesh.addNormal(0,0,1);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0,  -0.707, 0.707, 0);
					_myMesh.addNormal(0,0,1);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0,  0.707,  0.707, 0);
					_myMesh.addNormal(0,0,1);
				}
			}
			break;
		case QUAD:
			_myMesh = new CCVBOMesh(CCDrawMode.QUADS, _myParticles.size() * 4);
			_myMesh.prepareVertexData(3);
			
			for(int y = 0; y < _myParticles.height();y++) {
				for(int x = 0; x < _myParticles.width();x++) {
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, -1, -1, 0);
					_myMesh.addNormal(0,0,1);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0,  1, -1, 0);
					_myMesh.addNormal(0,0,1);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0,  1,  1, 0);
					_myMesh.addNormal(0,0,1);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, -1,  1, 0);
					_myMesh.addNormal(0,0,1);
				}
			}
			break;
		case PYRAMIDE:
			_myMesh = new CCVBOMesh(CCDrawMode.TRIANGLES, _myParticles.size() * 12);
			_myMesh.prepareVertexData(3);
			
			CCVector3 myNormal01 = CCVector3.normal(
				new CCVector3(0, -1, -0.707), 
				new CCVector3( -0.707, 0.707, -0.707), 
				new CCVector3(0,  0, 1)
			);
			CCVector3 myNormal02 = CCVector3.normal(
				new CCVector3(0, -1, -0.707), 
				new CCVector3(-0.707, 0.707, -0.707), 
				new CCVector3(0,  0, 1)
			);
			CCVector3 myNormal03 = CCVector3.normal(
				new CCVector3(0, -1, -0.707), 
				new CCVector3(0,  0, 1), 
				new CCVector3(0.707,  0.707, -0.707)
				);
			
			for(int y = 0; y < _myParticles.height();y++) {
				for(int x = 0; x < _myParticles.width();x++) {
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, 0, -1, -0.707);
					_myMesh.addNormal(0,0,1);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0,  -0.707, 0.707, -0.707);
					_myMesh.addNormal(0,0,1);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0,  0.707,  0.707, -0.707);
					_myMesh.addNormal(0,0,1);
					

					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, 0, -1, -0.707);
					_myMesh.addNormal(myNormal01);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0,  -0.707, 0.707, -0.707);
					_myMesh.addNormal(myNormal01);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0,  0,  0, 1);
					_myMesh.addNormal(myNormal01);
					
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, 0, -1, -0.707);
					_myMesh.addNormal(myNormal02);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0,  -0.707, 0.707, -0.707);
					_myMesh.addNormal(myNormal02);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0,  0,  0, 1);
					_myMesh.addNormal(myNormal02);
					

					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, 0, -1, -0.707);
					_myMesh.addNormal(myNormal03);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0,  0,  0, 1);
					_myMesh.addNormal(myNormal03);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0,  0.707,  0.707, -0.707);
					_myMesh.addNormal(myNormal03);
				}
			}
			break;
		case BOX:
			_myMesh = new CCVBOMesh(CCDrawMode.QUADS, _myParticles.size() * 24);
			_myMesh.prepareVertexData(3);
			
			for(int y = 0; y < _myParticles.height();y++) {
				for(int x = 0; x < _myParticles.width();x++) {
					//front
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, -1, -1, -1);
					_myMesh.addNormal(0,0,1);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0,  1, -1, -1);
					_myMesh.addNormal(0,0,1);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0,  1,  1, -1);
					_myMesh.addNormal(0,0,1);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, -1,  1, -1);
					_myMesh.addNormal(0,0,1);
					
					//back
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, -1, -1, 1);
					_myMesh.addNormal(0,0,-1);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0,  1, -1, 1);
					_myMesh.addNormal(0,0,-1);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0,  1,  1, 1);
					_myMesh.addNormal(0,0,-1);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, -1,  1, 1);
					_myMesh.addNormal(0,0,-1);
					
					//left
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0,-1, -1, -1);
					_myMesh.addNormal(1,0,0);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0,-1,  1, -1);
					_myMesh.addNormal(1,0,0);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0,-1,  1,  1);
					_myMesh.addNormal(1,0,0);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0,-1, -1,  1);
					_myMesh.addNormal(1,0,0);
					
					//right
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, 1, -1, -1);
					_myMesh.addNormal(-1,0,0);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, 1,  1, -1);
					_myMesh.addNormal(-1,0,0);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, 1,  1,  1);
					_myMesh.addNormal(-1,0,0);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, 1, -1,  1);
					_myMesh.addNormal(-1,0,0);

					
					//top
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, -1, -1, -1);
					_myMesh.addNormal(0,1,0);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, 1, -1,  -1);
					_myMesh.addNormal(0,1,0);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, 1, -1,   1);
					_myMesh.addNormal(0,1,0);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, -1,-1,   1);
					_myMesh.addNormal(0,1,0);
					
					//bottom
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, -1, 1, -1);
					_myMesh.addNormal(0,-1,0);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, 1, 1,  -1);
					_myMesh.addNormal(0,-1,0);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, 1, 1,   1);
					_myMesh.addNormal(0,-1,0);
					
					_myMesh.addVertex(x,y,0);
					_myMesh.addTextureCoords(0, -1,1,   1);
					_myMesh.addNormal(0,-1,0);
				}
			}
			break;
		}
		
		
		
	}
	
	public void pointSize(float thePointSize){
		_myPointsize = thePointSize;
	}
	
	@Override
	public void update(CCAnimator theAnimator) {}

	public void display(CCGraphics g){
		_myShader.start();
		g.texture(0, _myParticles.dataBuffer().attachment(0));
		g.texture(1, _myParticles.dataBuffer().attachment(1));
		g.texture(2, _myParticles.dataBuffer().attachment(2));
		g.texture(3, _myParticles.dataBuffer().attachment(3));
		g.texture(4, _myEnvelopeData.attachment(0));
		_myShader.uniform1i("positions", 0);
		_myShader.uniform1i("infos", 1);
		_myShader.uniform1i("velocities", 2);
		_myShader.uniform1i("colors", 3);
		_myShader.uniform1i("lifeTimeBlends", 4);
		_myShader.uniform1f("pointSize", _myPointsize);
		_myShader.uniform3f("cameraPosition", g.camera().position());
		_myMesh.draw(g);
		g.noTexture();
		_myShader.end();
	}
	
	public CCVBOMesh mesh(){
		return _myMesh;
	}
}
