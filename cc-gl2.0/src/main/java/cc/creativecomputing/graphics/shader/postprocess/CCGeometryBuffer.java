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
public class CCGeometryBuffer {

	public static final int POSITIONS = 0;
	public static final int NORMALS = 1;
	public static final int COLORS = 2;
	
	protected CCRenderBuffer _myRenderTexture;
	protected CCGLProgram _myShader;
	
	private CCGraphics _myGraphics;
	
	private int _myWidth;
	private int _myHeight;
	
	public CCGeometryBuffer(CCGraphics g, int theWidth, int theHeight) {
		_myGraphics = g;
		
		CCTextureAttributes myTextureAttributes = new CCTextureAttributes();
		myTextureAttributes.internalFormat(CCPixelInternalFormat.RGBA32F);
		myTextureAttributes.filter(CCTextureFilter.NEAREST);
		myTextureAttributes.wrap(CCTextureWrap.CLAMP);
		
		CCFrameBufferObjectAttributes myAttributes = new CCFrameBufferObjectAttributes(myTextureAttributes,3);
		myAttributes.enableDepthBuffer(true);
		
		_myRenderTexture = new CCRenderBuffer( myAttributes, theWidth, theHeight);
		
		_myShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "geometrybuffer_vertex.glsl"),
			CCNIOUtil.classPath(this, "geometrybuffer_fragment.glsl")
		);
		
		_myWidth = theWidth;
		_myHeight = theHeight;
	}
	
	public CCCamera camera(){
		return _myRenderTexture.camera();
	}
	
	public void beginDraw(CCGraphics g) {
		_myRenderTexture.beginDraw(g);
		_myShader.start();
		_myShader.uniform1f( "near", _myGraphics.camera().near());
		_myShader.uniform1f( "far", _myGraphics.camera().far() );
		_myShader.uniform1i("colorTexture", 0);
		updateMatrix();
	}
	
	public void updateMatrix(){
		_myShader.uniformMatrix4f("inverseView", inverseView());
	}
	
	public CCMatrix4x4 inverseView(){
		_myRenderTexture.camera().updateProjectionInfos();
		return _myRenderTexture.camera().viewMatrix().invert();
	}
	
	public void endDraw(CCGraphics g) {
		_myShader.end();
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
	
	public CCTexture2D positions(){
		return _myRenderTexture.attachment(0);
	}
	
	public CCTexture2D normals(){
		return _myRenderTexture.attachment(1);
	}
	
	public CCTexture2D colors(){
		return _myRenderTexture.attachment(2);
	}
	
	public CCTexture2D depth(){
		return _myRenderTexture.depthTexture();
	}
}
