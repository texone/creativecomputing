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
package cc.creativecomputing.graphics.shader.postprocess;

import java.nio.file.Path;

import cc.creativecomputing.graphics.CCCamera;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCRenderBuffer;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCFrameBufferObjectAttributes;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMatrix4x4;

/**
 * @author christianriekoff
 *
 */
public class CCGeometryBuffer extends CCGLProgram{
	
	public static enum CCGeometryBufferLayer{
		POSITION,
		NORMAL,
		COLOR,
		DEPTH,
		OFF
	}
	
	protected CCRenderBuffer _myRenderTexture;
	
	private CCGraphics _myGraphics;
	
	private int _myWidth;
	private int _myHeight;
	
	public CCGeometryBuffer(CCGraphics g, int theWidth, int theHeight, Path theVertexShader, Path theFragmentShader) {
		super(theVertexShader, theFragmentShader);
		_myGraphics = g;
		
		CCTextureAttributes myTextureAttributes = new CCTextureAttributes();
		myTextureAttributes.internalFormat(CCPixelInternalFormat.RGBA32F);
		myTextureAttributes.filter(CCTextureFilter.LINEAR);
		myTextureAttributes.wrap(CCTextureWrap.CLAMP);
		
		CCFrameBufferObjectAttributes myAttributes = new CCFrameBufferObjectAttributes(myTextureAttributes,4);
		myAttributes.enableDepthBuffer(true);
//		myAttributes.samples(8);
		
		_myRenderTexture = new CCRenderBuffer( myAttributes, theWidth, theHeight);
		
		_myWidth = theWidth;
		_myHeight = theHeight;
	}
	
	public CCGeometryBuffer(CCGraphics g, int theWidth, int theHeight) {
		this(
			g, 
			theWidth, 
			theHeight, 
			CCNIOUtil.classPath(CCGeometryBuffer.class, "geometrybuffer_vertex.glsl"), 
			CCNIOUtil.classPath(CCGeometryBuffer.class, "geometrybuffer_fragment.glsl")
		);
	}
	
	public CCCamera camera(){
		return _myRenderTexture.camera();
	}
	
	public void beginDraw(CCGraphics g) {
		_myRenderTexture.beginDraw(g);
		
	}
	
	@Override
	public void start(){
		super.start();
		uniform1f( "near", _myGraphics.camera().near());
		uniform1f( "far", _myGraphics.camera().far() );
		uniform1i("colorTexture", 0);
		updateMatrix();
	}
	
	public void updateMatrix(){
		uniformMatrix4f("inverseView", inverseView());
	}
	
	public CCMatrix4x4 inverseView(){
		_myRenderTexture.camera().updateProjectionInfos();
		return _myRenderTexture.camera().viewMatrix().invert();
	}
	
	public void endDraw(CCGraphics g) {
		_myRenderTexture.endDraw(g);
	}

	public CCRenderBuffer data() {
		return _myRenderTexture;
	}
	
	public int width(){
		return _myWidth;
	}
	
	public int height(){
		return _myHeight;
	}
	
	public CCTexture2D colors(){
		return _myRenderTexture.attachment(0);
	}
	
	public CCTexture2D normals(){
		return _myRenderTexture.attachment(1);
	}
	
	public CCTexture2D positions(){
		return _myRenderTexture.attachment(2);
	}
	
	public CCTexture2D depth(){
		return _myRenderTexture.attachment(3);
	}
	
	
//	public CCTexture2D depth(){
//		return _myRenderTexture.depthTexture();
//	}
}
