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
package cc.creativecomputing.graphics.shader.imaging;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCRenderBuffer;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCFrameBufferObjectAttributes;
import cc.creativecomputing.io.CCNIOUtil;

import com.jogamp.opengl.cg.CGparameter;

/**
 * @author christianriekoff
 * 
 */
public class CCGPUDepthOfField {

	private final CCGraphics _myGraphics;
	private final int _myWidth;
	private final int _myHeight;

	private CCGLProgram _myThirdShader;
	private CCGLProgram _myFourthShader;
	private CCGLProgram _myFifthShader;
	
//	private CCCGShader _mySceneShader;
	private CGparameter _myFocalDistanceParameter;
	private CGparameter _myFocalRangeParameter;
	
//	private CCCGShader _myDownSampleShader;

	private CCGPUSeperateGaussianBlur _myBlur;

	private CCRenderBuffer _mySceneTexture;
	private CCRenderBuffer _myFBO0;
	private CCRenderBuffer _myFBO1;
	private CCRenderBuffer _myFBO2;

	private float _myFocalDistance;
	private float _myFocalRange;
	
	private int _myDevider = 2;

	public CCGPUDepthOfField(final CCGraphics g, final int theWidth, final int theHeight) {
		_myGraphics = g;
		_myWidth = theWidth;
		_myHeight = theHeight;

//		_mySceneShader = new CCCGShader(
//			CCNIOUtil.classPath(this,"dof/scene.vp"), 
//			CCNIOUtil.classPath(this,"dof/scene.fp"));
//		_myFocalDistanceParameter = _mySceneShader.vertexParameter("focalDistance");
//		_myFocalRangeParameter = _mySceneShader.vertexParameter("focalRange");
//		_mySceneShader.load();
//		
//		_myDownSampleShader = new CCCGShader(
//			CCNIOUtil.classPath(this,"dof/downsample.vp"), 
//			CCNIOUtil.classPath(this,"dof/downsample.fp")
//		);
//		_myDownSampleShader.load();
//		
//		_myThirdShader = new CCGLProgram(
//			CCNIOUtil.classPath(this,"dof/third.vert"), 
//			CCNIOUtil.classPath(this,"dof/third.frag")
//		);
//		_myThirdShader.load();
//		_myFourthShader = new CCGLProgram(
//			CCNIOUtil.classPath(this,"dof/fourth.vert"), 
//			CCNIOUtil.classPath(this,"dof/fourth.frag")
//		);
//		_myFourthShader.load();
//		_myFifthShader = new CCGLProgram(
//			CCNIOUtil.classPath(this,"dof/fifth.vert"), 
//			CCNIOUtil.classPath(this,"dof/fifth.frag")
//		);
//		_myFifthShader.load();

		_myThirdShader.start();
		_myThirdShader.uniform1i("Width", _myWidth * 2);
		_myThirdShader.end();

		_myFourthShader.start();
		_myFourthShader.uniform1i("Height", _myHeight * 2);
		_myFourthShader.end();

		_myFifthShader.start();
		_myFifthShader.uniform1i("Tex0", 0);
		_myFifthShader.uniform1i("Tex1", 1);
		_myFifthShader.uniform1i("Tex2", 2);
		_myFifthShader.end();

		CCFrameBufferObjectAttributes myFrameBufferAtts = new CCFrameBufferObjectAttributes(2);
//		myFrameBufferAtts.samples(8);
		_mySceneTexture = new CCRenderBuffer(g, myFrameBufferAtts, _myWidth, _myHeight);
		_myFBO0 = new CCRenderBuffer(g, _myWidth / _myDevider, _myHeight / _myDevider);
		_myFBO1 = new CCRenderBuffer(g, _myWidth / _myDevider, _myHeight / _myDevider);
		_myFBO2 = new CCRenderBuffer(g, _myWidth / _myDevider, _myHeight / _myDevider);
		
		_myBlur = new CCGPUSeperateGaussianBlur(5, _myWidth , _myHeight);
		_myBlur.texture(_myFBO0.attachment(0));
	}
	
	public void focalDistance(final float theFocalDistance) {
		_myFocalDistance = theFocalDistance;
	}
	
	public void focalRange(final float theFocalRange) {
		_myFocalRange = theFocalRange;
	}

	public void begin() {

		/* First pass: scene rendering */
		_mySceneTexture.beginDraw();
		
//		_mySceneShader.start();
//		_mySceneShader.parameter(_myFocalDistanceParameter, _myFocalDistance);
//		_mySceneShader.parameter(_myFocalRangeParameter, _myFocalRange);
	}

	public void end() {
//		_mySceneShader.end();
		_mySceneTexture.endDraw();

		/* Second pass: downsampling */
		_myFBO0.beginDraw();
		_myGraphics.clear();
		_myGraphics.texture(_mySceneTexture.attachment(0));
//		_myDownSampleShader.start();
		_myGraphics.gl.glViewport(0, 0, _myWidth /_myDevider, _myHeight / _myDevider);
		drawQuad(_myWidth / _myDevider, _myHeight / _myDevider);
//		_myDownSampleShader.end();
		_myGraphics.noTexture();
		_myFBO0.endDraw();

		/* Third pass: Gaussian filtering along the X axis */
		_myFBO1.beginDraw();
		_myGraphics.clear();
		_myGraphics.texture(_myFBO0.attachment(0));
		_myThirdShader.start();
////		_myBlur.start();
		drawQuad(_myWidth / _myDevider, _myHeight / _myDevider);
		_myThirdShader.end();
////		_myBlur.end();
////		_myBlur.flipKernel();
		_myGraphics.noTexture();
		_myFBO1.endDraw();

		/* Fourth pass: Gaussian filtering along the Y axis */
		_myFBO2.beginDraw();
		_myGraphics.clear();
		_myGraphics.texture(_myFBO1.attachment(0));
		_myFourthShader.start();
//		_myBlur.start();
		drawQuad(_myWidth / _myDevider, _myHeight / _myDevider);
		_myFourthShader.end();
//		_myBlur.end();
//		_myBlur.flipKernel();
		_myGraphics.noTexture();
		_myFBO2.endDraw();
		

		/* Fifth pass: final compositing */
		_myGraphics.clear();
		_myGraphics.texture(0, _mySceneTexture.attachment(0));
		_myGraphics.texture(1, _myFBO2.attachment(0));
		_myGraphics.texture(2, _mySceneTexture.attachment(1));
		_myFifthShader.start();

		_myGraphics.beginShape(CCDrawMode.QUADS);
		_myGraphics.textureCoords2D(0, 0.0f, 0.0f);
		_myGraphics.textureCoords2D(1, 0.0f, 0.0f);
		_myGraphics.vertex(-_myWidth / 2, -_myHeight / 2);
		_myGraphics.textureCoords2D(0, 1.0f, 0.0f);
		_myGraphics.textureCoords2D(1, 1.0f, 0.0f);
		_myGraphics.vertex(_myWidth / 2, -_myHeight / 2);
		_myGraphics.textureCoords2D(0, 1.0f, 1.0f);
		_myGraphics.textureCoords2D(1, 1.0f, 1.0f);
		_myGraphics.vertex(_myWidth / 2, _myHeight / 2);
		_myGraphics.textureCoords2D(0, 0.0f, 1.0f);
		_myGraphics.textureCoords2D(1, 0.0f, 1.0f);
		_myGraphics.vertex(-_myWidth / 2, _myHeight / 2);
		_myGraphics.endShape();

		_myFifthShader.end();
		_myGraphics.noTexture();
	}

	private void drawQuad(final int theWidth, final int theHeight) {
		_myGraphics.beginShape(CCDrawMode.QUADS);
		_myGraphics.textureCoords2D(0.0f, 0.0f);
		_myGraphics.vertex(-theWidth / 2, -theHeight / 2);
		_myGraphics.textureCoords2D(1.0f, 0.0f);
		_myGraphics.vertex(theWidth / 2, -theHeight / 2);
		_myGraphics.textureCoords2D(1.0f, 1.0f);
		_myGraphics.vertex(theWidth / 2, theHeight / 2);
		_myGraphics.textureCoords2D(0.0f, 1.0f);
		_myGraphics.vertex(-theWidth / 2, theHeight / 2);
		_myGraphics.endShape();
	}
}
