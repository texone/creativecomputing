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
package cc.creativecomputing.graphics.texture;


/**
 * @author christianriekoff
 * 
 */
public class CCFrameBufferObjectAttributes{
	
	public static CCTextureAttributes createDefaultAttributes(){
		CCTextureAttributes myAttributes = new CCTextureAttributes();
		myAttributes.internalFormat(CCPixelInternalFormat.RGBA8);
		myAttributes.generateMipmaps(false);
		return myAttributes;
	}
	
	protected CCTextureAttributes[] _myTextureAttributes;
	
	protected CCPixelInternalFormat _myDepthInternalFormat;
	
	protected int _myNumberOfSamples;
	protected int _myCoverageSamples;
	
	protected boolean _myDepthBuffer;
	protected boolean _myStencilBuffer;
	
	protected int _myNumberOfColorBuffers;

	/**
	 * Default constructor, sets the target to \c GL_TEXTURE_2D with an 8-bit color+alpha, 
	 * a 24-bit depth texture, and no multisampling or mipmapping
	 */
	public CCFrameBufferObjectAttributes(final CCTextureAttributes theAttributes, final int theNumberOfAttachements) {
		_myTextureAttributes = new CCTextureAttributes[theNumberOfAttachements];
		for(int i = 0; i < theNumberOfAttachements;i++){
			_myTextureAttributes[i] = theAttributes;
		}
		
		_myDepthInternalFormat = CCPixelInternalFormat.DEPTH_COMPONENT24;
		
		_myNumberOfSamples = 0;
		_myCoverageSamples = 0;
		_myNumberOfColorBuffers = theNumberOfAttachements;
		_myDepthBuffer = true;
		_myStencilBuffer = false;
	}
	
	public CCFrameBufferObjectAttributes(final CCTextureAttributes...theAttributes){
		_myTextureAttributes = theAttributes;
		_myDepthInternalFormat = CCPixelInternalFormat.DEPTH_COMPONENT24;
		
		_myNumberOfSamples = 0;
		_myCoverageSamples = 0;
		_myNumberOfColorBuffers = theAttributes.length;
		_myDepthBuffer = true;
		_myStencilBuffer = false;
		
	}
	
	public CCFrameBufferObjectAttributes(final int theNumberOfAttachements){
		this(createDefaultAttributes(), theNumberOfAttachements);
	}
	
	public CCFrameBufferObjectAttributes() {
		this(1);
	}
	
	/**
	 * Returns an array with the texture attributes for the framebuffers attachments
	 * @return array with the texture attributes
	 */
	public CCTextureAttributes[] textureAttributes(){
		return _myTextureAttributes;
	}
	
	/**
	 * Returns the texture attributes for the given attachment
	 * @param theAttachment
	 * @return the texture attributes for the given attachment id
	 */
	public CCTextureAttributes textureAttributes(int theAttachmentID){
		return _myTextureAttributes[theAttachmentID];
	}

	/**
	 * Activates the given number of color buffers for the FBO.
	 * @param theNumberOfColorBuffers
	 */
	public void numberOfColorBuffers(final int theNumberOfColorBuffers) {
		_myNumberOfColorBuffers = theNumberOfColorBuffers;
	}

	/**
	 * Returns whether the FBO contains color buffers
	 * @return
	 */
	public int numberOfColorBuffers() {
		return _myNumberOfColorBuffers;
	}

	/**
	 * Enables or disables the creation of a depth buffer for the FBO.
	 * @param depthBuffer
	 */
	public void enableDepthBuffer(boolean depthBuffer) {
		_myDepthBuffer = depthBuffer;
	}

	/**
	 * Returns whether the FBO contains a depth buffer
	 * @return
	 */
	public boolean hasDepthBuffer() {
		return _myDepthBuffer;
	}

	/**
	 * Sets the GL internal format for the depth buffer. Defaults to {@link CCPixelInternalFormat#DEPTH_COMPONENT24}. 
	 * Common options also include {@link CCPixelInternalFormat#DEPTH_COMPONENT16} and {@link CCPixelInternalFormat#DEPTH_COMPONENT32}
	 * @param theDepthInternalFormat
	 */
	public void depthInternalFormat(CCPixelInternalFormat theDepthInternalFormat) {
		_myDepthInternalFormat = theDepthInternalFormat;
	}

	/**
	 * Returns the GL internal format for the depth buffer. Defaults to {@link CCPixelInternalFormat#DEPTH_COMPONENT24}
	 * @return
	 */
	public CCPixelInternalFormat depthInternalFormat() {
		return _myDepthInternalFormat;
	}

	/**
	 * Sets the number of samples used in MSAA-style antialiasing. 
	 * Defaults to none, disabling multisampling. Note that not all implementations support multisampling.
	 * @param theNumberOfSamples
	 */
	public void samples(int theNumberOfSamples) {
		_myNumberOfSamples = theNumberOfSamples;
	}

	/**
	 * Returns the number of samples used in MSAA-style antialiasing. Defaults to none, disabling multisampling.
	 * @return
	 */
	public int samples() {
		return _myNumberOfSamples;
	}

	/**
	 * Sets the number of coverage samples used in CSAA-style antialiasing. 
	 * Defaults to none. Note that not all implementations support CSAA, and is 
	 * currenlty Windows-only Nvidia.
	 * @param theCoverageSamples
	 */
	public void coverageSamples(int theCoverageSamples) {
		_myCoverageSamples = theCoverageSamples;
	}

	/**
	 * Returns the number of coverage samples used in CSAA-style antialiasing. Defaults to none.
	 * @return
	 */
	public int coverageSamples() {
		return _myCoverageSamples;
	}

}
