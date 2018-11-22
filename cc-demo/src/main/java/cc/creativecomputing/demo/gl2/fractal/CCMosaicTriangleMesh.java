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
package cc.creativecomputing.demo.gl2.fractal;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCTriangle2;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

/**
 * @author christianriekoff
 *
 */
public class CCMosaicTriangleMesh{
	
	@CCProperty(name = "texture random", min = 0, max = 1)
	private double _cTextureRandom = 0;
	
	@CCProperty(name = "texture random add", min = 0, max = 1)
	private double _cTextureRandomAdd = 0;
		
	@CCProperty(name = "use texture random")
	private boolean _cUseTextureRandom = true;
	
	@CCProperty(name = "saturation random", min = 0, max = 1)
	private double _cSaturationRandom = 0;
	
	@CCProperty(name = "texture center", min = 0, max = 1)
	private double textureCenter = 0;
	
	@CCProperty(name = "level", min = 0, max = 6)
	private double _cLevel = 0;
	
	@CCProperty(name = "level randomness", min = 0, max = 1)
	private double _cLevelRandomness = 0;
	
	@CCProperty(name = "level random blend", min = 0, max = 1)
	private double _cLevelRandomBlend = 0;
	
	@CCProperty(name = "alpha", min = 0, max = 1)
	private double _cAlpha = 0;
		
	@CCProperty(name = "alpha randomness", min = 0, max = 10)
	private double _cAlphaRandomness = 0;
		
	@CCProperty(name = "saturation", min = 0, max = 1)
	private double _cSaturation = 1;
	
	private CCMesh _myMesh;
	
	private CCShaderBuffer _myTargetsBuffer;
	private CCTexture2D _myModulationTexture;
	
	private CCGLProgram _myInitValueShader;
	
	private CCGraphics _myGraphics;
	
	private int _myDataWidth;
	private int _myDataHeight;
	
	private int _mySubdevisions;
	
	private CCTexture2D _myTexture0;
	private CCTexture2D _myTexture1;
	
	private List<CCTriangle2> _myTriangles = new ArrayList<CCTriangle2>();

	@CCProperty(name = "fractal shader")
	private CCGLProgram _myTriangleFractalShader;

	public CCMosaicTriangleMesh(final CCGraphics g, final List<CCTriangle2> theTriangles, final int theSubdivisions) {
		_myGraphics = g;
		
		_mySubdevisions = theSubdivisions;
		
		int myNumberOfTriangles = theTriangles.size() * CCMath.pow(4, _mySubdevisions - 1);
		
		int myNumberOfFractalTriangles = 0;
		for(int i = 0; i < _mySubdevisions;i++){
			myNumberOfFractalTriangles += theTriangles.size() * CCMath.pow(4, i);
		}
		int myMeshRes = (int)CCMath.sqrt(myNumberOfFractalTriangles) + 1;
		
		_myDataWidth = myMeshRes * 3;
		_myDataHeight = myMeshRes;
		
		_myInitValueShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "write_values_vertex.glsl"),
			CCNIOUtil.classPath(this, "write_values_fragment.glsl")
		);
		
		_myTargetsBuffer = new CCShaderBuffer(32, 3, 3, _myDataWidth, _myDataHeight);
		
		_myTriangles = theTriangles;

		List<Integer> myIndices = new ArrayList<Integer>();
		for(int i = 0; i < myNumberOfTriangles; i++) {
			myIndices.add(i * 3 + 0);
			myIndices.add(i * 3 + 1);
			myIndices.add(i * 3 + 2);
		}
		
		_myMesh = new CCMesh(CCDrawMode.TRIANGLES,  myNumberOfTriangles * 3);
		_myMesh.prepareVertexData(3);
		_myMesh.prepareTextureCoordData(0, 4);
		_myMesh.prepareTextureCoordData(1, 4);
		_myMesh.prepareTextureCoordData(2, 4);
		_myMesh.prepareTextureCoordData(3, 4);
		_myMesh.indices(myIndices);

		allocate(g, _mySubdevisions);
		
		_myTriangleFractalShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "triangles2_vertex.glsl"),
			CCNIOUtil.classPath(this, "triangles2_fragment.glsl")
		);
	}
	
	public void modulationTexture(CCTexture2D theTexture){
		_myModulationTexture = theTexture;
	}
	
	public void texture0(CCTexture2D theTexture) {
		_myTexture0 = theTexture;
		_myTexture0.wrap(CCTextureWrap.REPEAT);
	}
	
	public void texture1(CCTexture2D theTexture) {
		_myTexture1 = theTexture;
		_myTexture1.wrap(CCTextureWrap.REPEAT);
	}
	
	private int _myIndex = 0;
	
	private void subdevide(double x1, double y1, double x2, double y2, double x3, double y3, int level, int[][][] theTexCoords) {
		theTexCoords[level - 1][0][0] = _myIndex % _myDataWidth;
		theTexCoords[level - 1][0][1] = _myIndex / _myDataWidth;
		_myIndex++;
		theTexCoords[level - 1][1][0] = _myIndex % _myDataWidth;
		theTexCoords[level - 1][1][1] = _myIndex / _myDataWidth;
		_myIndex++;
		theTexCoords[level - 1][2][0] = _myIndex % _myDataWidth;
		theTexCoords[level - 1][2][1] = _myIndex / _myDataWidth;
		_myIndex++;
		
		double myRandomX = CCMath.random(-1,1);
		double myRandomY = CCMath.random(-1,1);
		double myRandomZ = CCMath.random(1);
		
		double myCenterX = (x1 + x2 + x3) / 3;
		double myCenterY = (y1 + y2 + y3) / 3;
		
		double myRandomScale = 1;//CCMath.random(0.5f, 1.5f);
		x1 = (x1 - myCenterX) * myRandomScale + myCenterX;
		x2 = (x2 - myCenterX) * myRandomScale + myCenterX;
		x3 = (x3 - myCenterX) * myRandomScale + myCenterX;

		y1 = (y1 - myCenterY) * myRandomScale + myCenterY;
		y2 = (y2 - myCenterY) * myRandomScale + myCenterY;
		y3 = (y3 - myCenterY) * myRandomScale + myCenterY;
		
		_myGraphics.textureCoords3D(0, x1, y1, 0);
		_myGraphics.textureCoords3D(1, myRandomX, myRandomY, myRandomZ);
		_myGraphics.textureCoords3D(2, myCenterX, myCenterY, 0);
		_myGraphics.vertex(theTexCoords[level - 1][0][0], theTexCoords[level - 1][0][1]);
		_myGraphics.textureCoords3D(0, x2, y2, 0);
		_myGraphics.textureCoords3D(1, myRandomX, myRandomY, myRandomZ);
		_myGraphics.textureCoords3D(2, myCenterX, myCenterY, 0);
		_myGraphics.vertex(theTexCoords[level - 1][1][0], theTexCoords[level - 1][1][1]);
		_myGraphics.textureCoords3D(0, x3, y3, 0);
		_myGraphics.textureCoords3D(1, myRandomX, myRandomY, myRandomZ);
		_myGraphics.textureCoords3D(2, myCenterX, myCenterY, 0);
		_myGraphics.vertex(theTexCoords[level - 1][2][0], theTexCoords[level - 1][2][1]);
		
		if(level == 1){// || (divisions < 4 && CCMath.random() > 0.7f)) {
			CCVector3 myPosition = new CCVector3(x1,y1);
			CCVector3 myPosition2 = new CCVector3(x2,y2);
			CCVector3 myPosition3 = new CCVector3(x3,y3);
			
			double myRandom1 = CCMath.random();
			double myRandom2 = CCMath.random();
			
			_myMesh.addTextureCoords(0, theTexCoords[0][0][0], theTexCoords[0][0][1], theTexCoords[1][0][0], theTexCoords[1][0][1]);
			_myMesh.addTextureCoords(1, theTexCoords[2][0][0], theTexCoords[2][0][1], theTexCoords[3][0][0], theTexCoords[3][0][1]);
			_myMesh.addTextureCoords(2, theTexCoords[4][0][0], theTexCoords[4][0][1], theTexCoords[5][0][0], theTexCoords[5][0][1]);
			_myMesh.addTextureCoords(3, theTexCoords[6][0][0], theTexCoords[6][0][1], myRandom1, myRandom2);
			_myMesh.addVertex(myPosition);
			
			_myMesh.addTextureCoords(0, theTexCoords[0][1][0], theTexCoords[0][1][1], theTexCoords[1][1][0], theTexCoords[1][1][1]);
			_myMesh.addTextureCoords(1, theTexCoords[2][1][0], theTexCoords[2][1][1], theTexCoords[3][1][0], theTexCoords[3][1][1]);
			_myMesh.addTextureCoords(2, theTexCoords[4][1][0], theTexCoords[4][1][1], theTexCoords[5][1][0], theTexCoords[5][1][1]);
			_myMesh.addTextureCoords(3, theTexCoords[6][1][0], theTexCoords[6][1][1], myRandom1, myRandom2);
			_myMesh.addVertex(myPosition2);
			
			_myMesh.addTextureCoords(0, theTexCoords[0][2][0], theTexCoords[0][2][1], theTexCoords[1][2][0], theTexCoords[1][2][1]);
			_myMesh.addTextureCoords(1, theTexCoords[2][2][0], theTexCoords[2][2][1], theTexCoords[3][2][0], theTexCoords[3][2][1]);
			_myMesh.addTextureCoords(2, theTexCoords[4][2][0], theTexCoords[4][2][1], theTexCoords[5][2][0], theTexCoords[5][2][1]);
			_myMesh.addTextureCoords(3, theTexCoords[6][2][0], theTexCoords[6][2][1], myRandom1, myRandom2);
			_myMesh.addVertex(myPosition3);
			return;
		}
		
		double x12 = (x1 + x2) / 2;
		double y12 = (y1 + y2) / 2;
		
		double x23 = (x2 + x3) / 2;
		double y23 = (y2 + y3) / 2;
		
		double x31 = (x3 + x1) / 2;
		double y31 = (y3 + y1) / 2;
		
		subdevide(x1,  y1,  x12, y12, x31, y31, level - 1, theTexCoords);
		subdevide(x12, y12, x23, y23, x31, y31, level - 1, theTexCoords);
		subdevide(x12, y12, x2,  y2,  x23, y23, level - 1, theTexCoords);
		subdevide(x23, y23, x3,  y3,  x31, y31, level - 1, theTexCoords);
	}
	
	public void allocate(CCGraphics g, int theSubdivision) {
		_myTargetsBuffer.beginDraw(g);
		_myGraphics.clearColor(0);
		_myGraphics.clear();
		_myInitValueShader.start();
		_myGraphics.beginShape(CCDrawMode.POINTS);
		
		for(CCTriangle2 myTriangle:_myTriangles) {
			subdevide(
				myTriangle.a().x, myTriangle.a().y,
				myTriangle.b().x, myTriangle.b().y,
				myTriangle.c().x, myTriangle.c().y,
				theSubdivision,
				new int[8][3][2]
			);
		}

		_myGraphics.endShape();
		_myInitValueShader.end();
		_myTargetsBuffer.endDraw(g);
	}
	
	private CCVector2 _myTextureSize = new CCVector2();
	
	public void textureSize(double theWidth, double theHeight){
		_myTextureSize.set(theWidth, theHeight);
	}
	
	public void draw(CCGraphics g) {
		
		g.texture(0, _myTargetsBuffer.attachment(0));
		g.texture(1, _myTargetsBuffer.attachment(1));
		g.texture(2, _myTargetsBuffer.attachment(2));
		g.texture(3, _myTexture0);
		g.texture(4, _myTexture1);
		
		if(_myModulationTexture != null)g.texture(6, _myModulationTexture);
		
		_myTriangleFractalShader.start();
		_myTriangleFractalShader.uniform2f("textureSize", _myTextureSize);
		_myTriangleFractalShader.uniform1i("textureCoordsTexture", 0);
		_myTriangleFractalShader.uniform1i("randomTexture", 1);
		_myTriangleFractalShader.uniform1i("centerTextureCoordsTexture", 2);
		_myTriangleFractalShader.uniform1i("textureSampler0", 3);
		_myTriangleFractalShader.uniform1i("textureSampler1", 4);
		_myTriangleFractalShader.uniform1i("blendTexture", 5);
		_myTriangleFractalShader.uniform1i("modSampler", 6);
		_myTriangleFractalShader.uniform1f("textureRandom", _cTextureRandom);
		_myTriangleFractalShader.uniform1f("textureRandomAdd", _cTextureRandomAdd);
		_myTriangleFractalShader.uniform1f("useTextureRandom", _cUseTextureRandom ? 1 : 0);
		_myTriangleFractalShader.uniform1f("textureCenter", textureCenter);
		_myTriangleFractalShader.uniform1f("level", _cLevel / 6);
		_myTriangleFractalShader.uniform1f("maxLevel", _mySubdevisions - 1f);
		_myTriangleFractalShader.uniform1f("levelRandomness", _cLevelRandomness);
		_myTriangleFractalShader.uniform1f("levelRandomBlend", _cLevelRandomBlend);
		_myTriangleFractalShader.uniform1f("alpha", _cAlpha);
		_myTriangleFractalShader.uniform1f("alphaRandomness", _cAlphaRandomness);
		_myTriangleFractalShader.uniform1f("saturationRandom", _cSaturationRandom);
		_myTriangleFractalShader.uniform1f("saturation", _myModulationTexture != null ? _cSaturation : 0);
		_myMesh.draw(g);
		_myTriangleFractalShader.end();
		g.noTexture();
		
//		g.color(255);
//		g.image(_myBlendTexture, 0,0);
	}
}
