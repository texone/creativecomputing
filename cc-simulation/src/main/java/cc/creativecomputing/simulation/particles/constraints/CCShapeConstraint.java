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
package cc.creativecomputing.simulation.particles.constraints;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCGLSetDataShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

/**
 * Use the height
 * @author Christian Riekoff
 *
 */
public class CCShapeConstraint extends CCConstraint{
	
	private List<List<CCVector2>> _myPaths = new ArrayList<>();
	
	private CCShaderBuffer _myLookUpBuffer;
	private CCShaderBuffer _myCurveBuffer;
	
	private CCGLSetDataShader _myDataShader;
	
	private CCVector3 _myScale;
	private CCVector3 _myOffset;
	
	private String _myCurveTextureParameter;
	private String _myLookUpTextureParameter;
	
	private String _myScaleParameter;
	private String _myOffsetParameter;
	
	public CCShapeConstraint(
		final CCVector3 theScale, final CCVector3 theOffset,
		final int theWidth, int theHeight
	) {
		super("shapeConstraint", 1, 1, 1);
		

		_myLookUpBuffer = new CCShaderBuffer(theWidth, theHeight);
		_myCurveBuffer = new CCShaderBuffer(theWidth, 22);
		
		_myDataShader = new CCGLSetDataShader();
		
		_myScale = new CCVector3(theScale);
		_myOffset = new CCVector3(theOffset);

		_myCurveTextureParameter = parameter("curveTexture");
		_myLookUpTextureParameter = parameter("lookUpTexture");
		
		_myScaleParameter = parameter("scale");
		_myOffsetParameter = parameter("offset");
	}
	
	public List<List<CCVector2>> paths(){
		return _myPaths;
	}
	
	public CCShaderBuffer curveBuffer(){
		return _myCurveBuffer;
	}
	
	public CCShaderBuffer lookUpBuffer(){
		return _myLookUpBuffer;
	}
	
	@Override
	public void setShader(CCGLProgram theProgram) {
		super.setShader(theProgram);
		_myShader.setTextureUniform(_myCurveTextureParameter, _myCurveBuffer.attachment(0));
		_myShader.setTextureUniform(_myLookUpTextureParameter, _myLookUpBuffer.attachment(0));
	}
	
	@Override
	public void setUniforms() {
		super.setUniforms();
		_myShader.uniform3f(_myScaleParameter, _myScale);
		_myShader.uniform3f(_myOffsetParameter, _myOffset);
	}
	
	public CCVector3 textureScale() {
		return _myScale;
	}
	
	public CCVector3 textureOffset() {
		return _myOffset;
	}
	
	@Override
	public void preDisplay(CCGraphics g) {
		_myLookUpBuffer.beginDraw();
		_myDataShader.start();
		g.clear();
		g.noBlend();
		for(int i = 1; i < _myPaths.size();i++){
			List<CCVector2> myPath0 = _myPaths.get(i - 1);
			List<CCVector2> myPath1 = _myPaths.get(i);
			g.beginShape(CCDrawMode.TRIANGLE_STRIP);
			for(int x = 0; x < myPath0.size();x++){
				g.textureCoords1D(i);
				g.vertex(myPath0.get(x));
				g.textureCoords1D(i);
				g.vertex(myPath1.get(x));
			}
			g.endShape();
		}
		_myDataShader.end();
		_myLookUpBuffer.endDraw();
		
		_myCurveBuffer.beginDraw();
		_myDataShader.start();
		g.clear();
		g.noBlend();
		for(int i = 1; i < _myPaths.size() - 1;i++){
			List<CCVector2> myPath0 = _myPaths.get(i);
			g.beginShape(CCDrawMode.POINTS);
			for(int x = 0; x < myPath0.size();x++){
				g.textureCoords1D(myPath0.get(x).y);
				g.vertex(x + 0.5, i - 1 + 0.5);
			}
			g.endShape();
		}
		_myDataShader.end();
		_myCurveBuffer.endDraw();
	}
	
	public static void main(String[] args) {
		CCLog.info(new CCVector3(1,0,0).dot(new CCVector3(1,0,0)));
	}
}
