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
package cc.creativecomputing.graphics.shader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCPBO;
import cc.creativecomputing.graphics.texture.CCFrameBufferObject;
import cc.creativecomputing.graphics.texture.CCFrameBufferObjectAttributes;
import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.math.CCAABoundingRectangle;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

/**
 * @author christianriekoff
 *
 */
public class CCShaderBuffer extends CCFrameBufferObject{
	
	private static CCFrameBufferObjectAttributes floatAttributes(
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theNumberOfTextures
	) {
		CCTextureAttributes myTextureAttributes = new CCTextureAttributes();
		myTextureAttributes.pixelType(CCPixelType.FLOAT);
		myTextureAttributes.wrap(CCTextureWrap.CLAMP);
		myTextureAttributes.filter(CCTextureFilter.NEAREST);
		
		boolean myIs16Bit;
		
		switch(theNumberOfBits){
		case 16:
			myIs16Bit = true;
			break;
		case 32:
			myIs16Bit = false;
			break;
		default:
			throw new CCShaderException("The given number of bits is not supported. You can only create shader textures with 16 or 32 bit resolution.");
		}
		
		boolean _myIsNvidia = CCGraphics.currentGL().glGetString(GL.GL_VENDOR).startsWith("NVIDIA");
		
		CCPixelFormat _myFormat;
		CCPixelInternalFormat _myInternalFormat;
		
		switch(theNumberOfChannels){
		case 1:
			if(_myIsNvidia) {
				_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.FLOAT_R16_NV : CCPixelInternalFormat.FLOAT_R32_NV;
			} else {
				_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.LUMINANCE_FLOAT16_ATI : CCPixelInternalFormat.LUMINANCE_FLOAT32_ATI;
			}
			_myFormat = CCPixelFormat.LUMINANCE;
			break;
		case 2:
			if(_myIsNvidia) {
				_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.FLOAT_RG16_NV : CCPixelInternalFormat.FLOAT_RG32_NV;
			} else {
				_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.LUMINANCE_ALPHA_FLOAT16_ATI : CCPixelInternalFormat.LUMINANCE_ALPHA_FLOAT32_ATI;
			}
			_myFormat = CCPixelFormat.LUMINANCE_ALPHA;
			break;
		case 3:
			_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.RGB16F : CCPixelInternalFormat.RGB32F;
			_myFormat = CCPixelFormat.RGB;
			break;
		case 4:
			_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.RGBA16F : CCPixelInternalFormat.RGBA32F;
			_myFormat = CCPixelFormat.RGBA;
			break;
		default:
			throw new CCShaderException("The given number of channels is not supported. You can only create shader textures with 1,2,3 or 4 channels.");
		
		}
		
		myTextureAttributes.internalFormat(_myInternalFormat);
		myTextureAttributes.format(_myFormat);
		
		CCFrameBufferObjectAttributes myAttributes = new CCFrameBufferObjectAttributes(myTextureAttributes, theNumberOfTextures);
		myAttributes.enableDepthBuffer(false);
		return myAttributes;
	}
	
	private static CCFrameBufferObjectAttributes intAttributes(
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theNumberOfTextures
	) {
		CCTextureAttributes myTextureAttributes = new CCTextureAttributes();
		myTextureAttributes.pixelType(CCPixelType.INT);
		myTextureAttributes.wrap(CCTextureWrap.CLAMP);
		myTextureAttributes.filter(CCTextureFilter.NEAREST);
			
		boolean myIs16Bit;
		
			switch(theNumberOfBits){
			case 16:
				myIs16Bit = true;
				break;
			case 32:
				myIs16Bit = false;
				break;
			default:
				throw new CCShaderException("The given number of bits is not supported. You can only create shader textures with 16 or 32 bit resolution.");
			}
			
			CCPixelFormat _myFormat;
			CCPixelInternalFormat _myInternalFormat;
			
			switch(theNumberOfChannels){
			case 1:
				_myInternalFormat = CCPixelInternalFormat.LUMINANCE;
				_myFormat = CCPixelFormat.LUMINANCE_INTEGER;
				break;
			case 2:
				_myInternalFormat = CCPixelInternalFormat.LUMINANCE_ALPHA;;
				_myFormat = CCPixelFormat.LUMINANCE_ALPHA_INTEGER;
				break;
			case 3:
				_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.RGB16I : CCPixelInternalFormat.RGB32I;
				_myFormat = CCPixelFormat.RGB_INTEGER;
				break;
			case 4:
				_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.RGBA16I : CCPixelInternalFormat.RGBA32I;
				_myFormat = CCPixelFormat.RGBA_INTEGER;
				break;
			default:
				throw new CCShaderException("The given number of channels is not supported. You can only create shader textures with 1,2,3 or 4 channels.");
			
			}
			
			myTextureAttributes.internalFormat(_myInternalFormat);
			myTextureAttributes.format(_myFormat);
			
			CCFrameBufferObjectAttributes myAttributes = new CCFrameBufferObjectAttributes(myTextureAttributes, theNumberOfTextures);
			myAttributes.enableDepthBuffer(false);
			return myAttributes;
		}
	
	private static CCFrameBufferObjectAttributes attributes(
		final CCPixelType theType,
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theNumberOfTextures
	){
		switch(theType){
		case INT:
			return intAttributes(theNumberOfBits, theNumberOfChannels, theNumberOfTextures);
		case FLOAT:
			return floatAttributes(theNumberOfBits, theNumberOfChannels, theNumberOfTextures);
		default:
			throw new CCFrameBufferObjectException("Unsupported pixeltype " + theType);
		}
	}
	
	private CCPBO[] _myPBO = new CCPBO[2];
	private int _myNumberOfChannels;
	private int _myNumberOfBits;
	
	public CCShaderBuffer(
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theNumberOfTextures, 
		final int theWidth, 
		final int theHeight, 
		final CCTextureTarget theTarget
	){
		this(
			CCPixelType.FLOAT,
			theNumberOfBits,
			theNumberOfChannels,
			theNumberOfTextures,
			theWidth,
			theHeight,
			theTarget
		);
	}
	
	public CCShaderBuffer(
		final CCPixelType theType,
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theNumberOfTextures, 
		final int theWidth, 
		final int theHeight, final CCTextureTarget theTarget
	){
		super(theTarget, attributes(theType, theNumberOfBits,theNumberOfChannels,theNumberOfTextures), theWidth, theHeight);
			
		_myNumberOfChannels = theNumberOfChannels;
		_myNumberOfBits = theNumberOfBits;
			
//			clear();
	}
	
	public CCShaderBuffer(
		final int theNumberOfBits, 
		final int theNumberOfChannels, final int theNumberOfTextures, 
		final int theWidth,
		final int theHeight
	){
		this(theNumberOfBits,theNumberOfChannels,theNumberOfTextures,theWidth,theHeight,CCTextureTarget.TEXTURE_RECT);
	}
	
	public CCShaderBuffer(
		final int theNumberOfBits, 
		final int theNumberOfChannels, final int theWidth, 
		final int theHeight, final CCTextureTarget theTarget
	){
		this(theNumberOfBits,theNumberOfChannels,1,theWidth,theHeight,theTarget);
	}
	
	public CCShaderBuffer(final int theWidth, final int theHeight, final CCTextureTarget theTarget){
		this(32,3,theWidth,theHeight,theTarget);
	}
	
	public CCShaderBuffer(
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theWidth, final int theHeight
	){
		this(theNumberOfBits,theNumberOfChannels,1,theWidth,theHeight,CCTextureTarget.TEXTURE_RECT);
	}
	
	public CCShaderBuffer(final int theWidth, final int theHeight){
		this(32,3,theWidth,theHeight,CCTextureTarget.TEXTURE_RECT);
	}
	
	public int numberOfChannels() {
		return _myNumberOfChannels;
	}
	
	public int numberOfBits() {
		return _myNumberOfBits;
	}
	
	public void beginOrtho2D(){
		GL2 gl = CCGraphics.currentGL();
		gl.glPushAttrib(GL2.GL_VIEWPORT_BIT);
		gl.glViewport(0, 0, _myWidth, _myHeight);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrtho(0, _myWidth, 0, _myHeight,-1,1);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.texture.CCFrameBufferObject#beginDraw()
	 */
	@Override
	public void beginDraw(CCGraphics g) {
		bindFBO();
		beginOrtho2D();
	}
	
//	public void beginDraw() {
//		bindFBO();
//		beginOrtho2D();
//	}
	
	public void beginDraw(CCGraphics g, int theAttachment) {
		bindFBO(theAttachment);
		beginOrtho2D();
	}

	public void endOrtho2D(){
		GL2 gl = CCGraphics.currentGL();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPopMatrix();

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPopMatrix();

		gl.glPopAttrib();
	}
	
	public void endDraw(CCGraphics g){
		endOrtho2D();
		releaseFBO();
	}
	

	
//	public void endDraw(){
//		endOrtho2D();
//		releaseFBO();
//	}
	
	public void drawQuad(double theX0, double theY0, double theX1, double theY1) {

		double myU0 = theX0;
		double myV0 = theY0;
		double myU1 = theX1;
		double myV1 = theY1;
		
		if(_myTarget == CCTextureTarget.TEXTURE_2D){
			myU0 /= _myWidth;
			myV0 /= _myHeight;
			myU1 /= _myWidth;
			myV1 /= _myHeight;
		}
		
		GL2 gl = CCGraphics.currentGL();
		
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2d(myU0, myV0);
		gl.glVertex2d(theX0, theY0);
		gl.glTexCoord2d(myU1, myV0);
		gl.glVertex2d(theX1, theY0);
		gl.glTexCoord2d(myU1, myV1);
		gl.glVertex2d(theX1, theY1);
		gl.glTexCoord2d(myU0, myV1);
		gl.glVertex2d(theX0, theY1);
		gl.glEnd();
	}
	
	public void drawQuad(CCGraphics g) {
		drawQuad(0,0,_myWidth, _myHeight);
	}
	
	public void draw(CCGraphics g,CCAABoundingRectangle theRectangle) {
		beginDraw(g);
		drawQuad(theRectangle.min().x, theRectangle.min().y, theRectangle.max().x, theRectangle.max().y);
		endDraw(g);
	}
	
	public void draw(CCGraphics g, double theX0, double theY0, double theX1, double theY1){
		beginDraw(g);
	
		GL2 gl = CCGraphics.currentGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
		drawQuad(theX0, theY0, theX1, theY1);
		
		endDraw(g);
	}
	
	public void draw(CCGraphics g){
		draw(g, 0, 0, _myWidth, _myHeight);
	}
	
	public void draw(CCGraphics g,int theAttachmentID, double theX0, double theY0, double theX1, double theY1){
		beginDraw(g,theAttachmentID);
	
		GL2 gl = CCGraphics.currentGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
		drawQuad(theX0, theY0, theX1, theY1);
		
		endDraw(g);
	}
	
	public void draw(CCGraphics g, int theAttachmentID){
		draw(g, theAttachmentID, 0, 0, _myWidth, _myHeight);
	}
	
	public void clear(CCGraphics g) {
		beginDraw(g);
		GL gl = CCGraphics.currentGL();
		gl.glClearStencil(0);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
		endDraw(g);
	}
	
	public void clear(CCGraphics g,int theAttachment){
		beginDraw(g,theAttachment);
		GL gl = CCGraphics.currentGL();
		gl.glClearStencil(0);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
		endDraw(g);
	}
	
	public FloatBuffer getData(){
		return getData(0, 0, 0, _myWidth, _myHeight);
	}
	
	public FloatBuffer getData(final int theX, final int theY, final int theWidth, final int theHeight){
		return getData(0, theX, theY, theWidth, theHeight);
	}
	
	public FloatBuffer getData(final int theAttachment){
		FloatBuffer myResult = FloatBuffer.allocate(_myWidth * _myHeight * _myNumberOfChannels);
		GL2 gl = CCGraphics.currentGL();
		gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, _myFrameBuffers[0]);
		gl.glReadBuffer(_myDrawBuffers[theAttachment]);
		gl.glReadPixels(0, 0, _myWidth, _myHeight,_myAttachments[theAttachment].format().glID,GL.GL_FLOAT,myResult);
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
		return myResult;
	}
	
	public FloatBuffer getData(final int theAttachment, final int theX, final int theY, final int theWidth, final int theHeight) {
		FloatBuffer myResult = FloatBuffer.allocate(theWidth * theHeight * _myNumberOfChannels);
		
		GL2 gl = CCGraphics.currentGL();
		
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, _myFrameBuffers[0]);
		gl.glReadBuffer(_myDrawBuffers[theAttachment]);
		gl.glReadPixels(theX, theY, theWidth, theHeight,_myAttachments[theAttachment].format().glID,GL.GL_FLOAT,myResult);
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
		
//		String myError = checkError(gl);
//		if(myError != null)throw new CCFrameBufferObjectException(myError);
		return myResult;
	}
	
	private int i = 0;
	
	public FloatBuffer getPBOData(final int theAttachment) {
		return getPBOData(theAttachment, 0, 0, _myWidth, _myHeight);
	}
	
	/**
	 * @param theAttachment
	 * @param theX
	 * @param theY
	 * @param theWidth
	 * @param theHeight
	 * @return
	 */
	public FloatBuffer getPBOData(final int theAttachment, final int theX, final int theY, final int theWidth, final int theHeight) {
		if(_myPBO[0] == null){
			_myPBO[0] = new CCPBO(_myNumberOfChannels * theWidth * theHeight * (_myNumberOfBits == 16 ? 2 : 4));
			_myPBO[1] = new CCPBO(_myNumberOfChannels * theWidth * theHeight * (_myNumberOfBits == 16 ? 2 : 4));
		}
		
		GL2 gl = CCGraphics.currentGL();
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, _myFrameBuffers[0]);
		gl.glReadBuffer(_myDrawBuffers[theAttachment]);
		
		_myPBO[i % 2].beginPack();
		gl.glReadPixels(theX, theY, theWidth, theHeight,_myAttachments[theAttachment].format().glID,GL.GL_FLOAT,0);
		_myPBO[i % 2].endPack();
		
		ByteBuffer myResult = _myPBO[(i + 1) % 2].mapReadBuffer();
		myResult.order(ByteOrder.LITTLE_ENDIAN);
		_myPBO[(i + 1) % 2].unmapReadBuffer();
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
		i++;
		return myResult.asFloatBuffer();
	}
	
	/**
	 * Read data from a floatbuffer
	 * @param theData
	 */
	public void loadData(final FloatBuffer theData){
		theData.rewind();
		GL gl = CCGraphics.currentGL();
		gl.glEnable(_myTarget.glID);
		gl.glBindTexture(_myTarget.glID,_myAttachments[0].id());
		gl.glTexImage2D(_myTarget.glID,0,_myAttachments[0].internalFormat().glID,_myWidth,_myHeight,0,_myAttachments[0].format().glID,GL.GL_FLOAT,theData);
		gl.glBindTexture(_myTarget.glID,0);
		gl.glDisable(_myTarget.glID);
	}

}
