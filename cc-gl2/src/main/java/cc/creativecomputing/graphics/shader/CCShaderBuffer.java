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

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_VENDOR;
import static org.lwjgl.opengl.GL11.GL_VIEWPORT_BIT;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearStencil;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushAttrib;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengl.GL11.glTexCoord2d;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glVertex2d;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

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
		
		boolean _myIsNvidia = glGetString(GL_VENDOR).startsWith("NVIDIA");
		
		CCPixelFormat _myFormat;
		CCPixelInternalFormat _myInternalFormat = null;
		
		switch(theNumberOfChannels){
		case 1:
			if(_myIsNvidia) {
				_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.FLOAT_R16_NV : CCPixelInternalFormat.FLOAT_R32_NV;
			} else {
//				_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.LUMINANCE_FLOAT16_ATI : CCPixelInternalFormat.LUMINANCE_FLOAT32_ATI;
			}
			_myFormat = CCPixelFormat.LUMINANCE;
			break;
		case 2:
			if(_myIsNvidia) {
				_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.FLOAT_RG16_NV : CCPixelInternalFormat.FLOAT_RG32_NV;
			} else {
//				_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.LUMINANCE_ALPHA_FLOAT16_ATI : CCPixelInternalFormat.LUMINANCE_ALPHA_FLOAT32_ATI;
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
				_myInternalFormat = CCPixelInternalFormat.LUMINANCE_ALPHA;
                _myFormat = CCPixelFormat.LUMINANCE_ALPHA;
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
		
		glPushAttrib(GL_VIEWPORT_BIT);
		glViewport(0, 0, _myWidth, _myHeight);

		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0, _myWidth, 0, _myHeight,-1,1);

		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glLoadIdentity();
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
		
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();

		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();

		glPopAttrib();
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
		
		
		
		glBegin(GL_QUADS);
		glTexCoord2d(myU0, myV0);
		glVertex2d(theX0, theY0);
		glTexCoord2d(myU1, myV0);
		glVertex2d(theX1, theY0);
		glTexCoord2d(myU1, myV1);
		glVertex2d(theX1, theY1);
		glTexCoord2d(myU0, myV1);
		glVertex2d(theX0, theY1);
		glEnd();
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
	
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
		drawQuad(theX0, theY0, theX1, theY1);
		
		endDraw(g);
	}
	
	public void draw(CCGraphics g){
		draw(g, 0, 0, _myWidth, _myHeight);
	}
	
	public void draw(CCGraphics g,int theAttachmentID, double theX0, double theY0, double theX1, double theY1){
		beginDraw(g,theAttachmentID);
	
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
		drawQuad(theX0, theY0, theX1, theY1);
		
		endDraw(g);
	}
	
	public void draw(CCGraphics g, int theAttachmentID){
		draw(g, theAttachmentID, 0, 0, _myWidth, _myHeight);
	}
	
	public void clear(CCGraphics g) {
		beginDraw(g);
		
		glClearStencil(0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
		endDraw(g);
	}
	
	public void clear(CCGraphics g,int theAttachment){
		beginDraw(g,theAttachment);
		
		glClearStencil(0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
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
		
		glBindFramebuffer(GL_FRAMEBUFFER, _myFrameBuffers[0]);
		glReadBuffer(_myDrawBuffers[theAttachment]);
		glReadPixels(0, 0, _myWidth, _myHeight,_myAttachments[theAttachment].format().glID,GL_FLOAT,myResult);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		return myResult;
	}
	
	public FloatBuffer getData(final int theAttachment, final int theX, final int theY, final int theWidth, final int theHeight) {
		FloatBuffer myResult = FloatBuffer.allocate(theWidth * theHeight * _myNumberOfChannels);
		
		
		
		glBindFramebuffer(GL_FRAMEBUFFER, _myFrameBuffers[0]);
		glReadBuffer(_myDrawBuffers[theAttachment]);
		glReadPixels(theX, theY, theWidth, theHeight,_myAttachments[theAttachment].format().glID,GL_FLOAT,myResult);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		
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
		
		
		glBindFramebuffer(GL_FRAMEBUFFER, _myFrameBuffers[0]);
		glReadBuffer(_myDrawBuffers[theAttachment]);
		
		_myPBO[i % 2].beginPack();
		glReadPixels(theX, theY, theWidth, theHeight,_myAttachments[theAttachment].format().glID,GL_FLOAT,0);
		_myPBO[i % 2].endPack();
		
		ByteBuffer myResult = _myPBO[(i + 1) % 2].mapReadBuffer();
		myResult.order(ByteOrder.LITTLE_ENDIAN);
		_myPBO[(i + 1) % 2].unmapReadBuffer();
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		i++;
		return myResult.asFloatBuffer();
	}
	
	/**
	 * Read data from a floatbuffer
	 * @param theData
	 */
	public void loadData(final FloatBuffer theData){
		theData.rewind();
		
		glEnable(_myTarget.glID);
		glBindTexture(_myTarget.glID,_myAttachments[0].id());
		glTexImage2D(_myTarget.glID,0,_myAttachments[0].internalFormat().glID,_myWidth,_myHeight,0,_myAttachments[0].format().glID,GL_FLOAT,theData);
		glBindTexture(_myTarget.glID,0);
		glDisable(_myTarget.glID);
	}

}
