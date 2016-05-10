package cc.creativecomputing.gl4;

import java.util.Map;

import cc.creativecomputing.gl4.texture.GLTexture;

import com.jogamp.opengl.GL4bc;

/**
 * brief Defines the Format of the Fbo, which is passed in via create().
 * <p>
 * The default provides an 8-bit RGBA color texture attachment and a 24-bit depth renderbuffer attachment, multi-sampling and stencil disabled.
 * @author christianr
 *
 */
public class FboFormat {
		
		  
			//! Default constructor, sets the target to \c GL_TEXTURE_2D with an 8-bit color+alpha, a 24-bit depth texture, and no multisampling or mipmapping
	public FboFormat(){
		mColorTextureFormat = getDefaultColorTextureFormat( true );
		mColorTexture = true;
		
		mDepthBufferInternalFormat = getDefaultDepthInternalFormat();
		mDepthBuffer = true;
		mDepthTexture = false;
		
		mSamples = 0;
		mCoverageSamples = 0;
		mStencilBuffer = false;
	}

			//! Enables a color texture at \c GL_COLOR_ATTACHMENT0 with a Texture::Format of \a textureFormat, which defaults to 8-bit RGBA with no mipmapping. Disables a color renderbuffer.
	public FboFormat	colorTexture(  Texture::Format &textureFormat = getDefaultColorTextureFormat( true ) ) { mColorTexture = true; mColorTextureFormat = textureFormat; return this; }
			//! Disables both a color Texture and a color Buffer
	public FboFormat	disableColor() { mColorTexture = false; return this; }
			
			//! Enables a depth renderbuffer with an internal format of \a internalFormat, which defaults to \c GL_DEPTH_COMPONENT24. Disables a depth texture.
	public FboFormat	depthBuffer( int internalFormat = getDefaultDepthInternalFormat() ) { mDepthTexture = false; mDepthBuffer = true; mDepthBufferInternalFormat = internalFormat; return this; }
			//! Enables a depth texture with a format of \a textureFormat, which defaults to \c GL_DEPTH_COMPONENT24. Disables a depth renderbuffer.
	public FboFormat	depthTexture(  Texture::Format &textureFormat = getDefaultDepthTextureFormat()) { mDepthTexture = true; mDepthBuffer = false; mDepthTextureFormat = textureFormat; return this; }
			//! Disables both a depth Texture and a depth Buffer
	public FboFormat	disableDepth() { mDepthBuffer = false; return this; }
			
			//! Sets the number of MSAA samples. Defaults to none.
	public FboFormat samples( int samples ) { mSamples = samples; return this; }
			//! Sets the number of CSAA samples. Defaults to none.
	public FboFormat coverageSamples( int coverageSamples ) { mCoverageSamples = coverageSamples; return this; }
			//! Enables a stencil buffer. Defaults to false.
	public FboFormat stencilBuffer( boolean stencilBuffer = true ) { mStencilBuffer = stencilBuffer; return this; }

			//! Adds a Renderbuffer attachment \a buffer at \a attachmentPoint (such as \c GL_COLOR_ATTACHMENT0). Replaces any existing attachment at the same attachment point.
	public FboFormat	attachment( int attachmentPoint,  Renderbuffer buffer, Renderbuffer multisampleBuffer )
	{
	mAttachmentsBuffer.put(attachmentPoint, buffer);
	mAttachmentsMultisampleBuffer.put(attachmentPoint, multisampleBuffer);
	mAttachmentsTexture.remove( attachmentPoint );
	return this;
}
	
	
			//! Adds a Texture attachment \a texture at \a attachmentPoint (such as \c GL_COLOR_ATTACHMENT0). Replaces any existing attachment at the same attachment point.
	public FboFormat	attachment( int attachmentPoint,  GLTexture texture, Renderbuffer multisampleBuffer){
		mAttachmentsTexture.put(attachmentPoint, texture);
		mAttachmentsMultisampleBuffer.put(attachmentPoint, multisampleBuffer);
		mAttachmentsBuffer.remove( attachmentPoint );
		return this;
	}
			
			//! Sets the internal format for the depth buffer. Defaults to \c GL_DEPTH_COMPONENT24. Common options also include \c GL_DEPTH_COMPONENT16 and \c GL_DEPTH_COMPONENT32
	public void	setDepthBufferInternalFormat( int depthInternalFormat ) { mDepthBufferInternalFormat = depthInternalFormat; }
			//! Sets the number of samples used in MSAA-style antialiasing. Defaults to none, disabling multisampling. Note that not all implementations support multisampling.
	public void	setSamples( int samples ) { mSamples = samples; }
			//! Sets the number of coverage samples used in CSAA-style antialiasing. Defaults to none. Note that not all implementations support CSAA, and is currenlty Windows-only Nvidia. Ignored on OpenGL ES.
	public void	setCoverageSamples( int coverageSamples ) { mCoverageSamples = coverageSamples; }
			//! Sets the Color Texture::Format for use in the creation of the color texture.
	public void	setColorTextureFormat(  Texture::Format &format ) { mColorTextureFormat = format; }
			//! Enables or disables the creation of a depth buffer for the FBO.
	public void	enableDepthBuffer( boolean depthBuffer = true ) { mDepthBuffer = depthBuffer; }
			//! Enables or disables the creation of a stencil buffer.
	public void	enableStencilBuffer( boolean stencilBuffer = true ) { mStencilBuffer = stencilBuffer; }
			//! Removes a buffer or texture attached at \a attachmentPoint
	public void	removeAttachment( int attachmentPoint )
	{
	mAttachmentsBuffer.remove( attachmentPoint );
	mAttachmentsMultisampleBuffer.remove( attachmentPoint );	
	mAttachmentsTexture.remove( attachmentPoint );
}

			//! Returns the GL internal format for the depth buffer. Defaults to \c GL_DEPTH_COMPONENT24.
	public int	getDepthBufferInternalFormat()  { return mDepthBufferInternalFormat; }
			//! Returns the Texture::Format for the default color texture at GL_COLOR_ATTACHMENT0.
	public  Texture::FboFormat	getColorTextureFormat()  { return mColorTextureFormat; }
			//! Returns the Texture::Format for the depth texture.
	public  Texture::FboFormat	getDepthTextureFormat()  { return mDepthTextureFormat; }
			//! Returns the number of samples used in MSAA-style antialiasing. Defaults to none, disabling multisampling.
	public int		getSamples()  { return mSamples; }
			//! Returns the number of coverage samples used in CSAA-style antialiasing. Defaults to none. MSW only.
	public int		getCoverageSamples()  { return mCoverageSamples; }
			//! Returns whether the FBO contains a Texture at GL_COLOR_ATTACHMENT0
	public boolean	hasColorTexture()  { return mColorTexture; }
			//! Returns whether the FBO has a Renderbuffer as a depth attachment.
	public boolean	hasDepthBuffer()  { return mDepthBuffer; }
			//! Returns whether the FBO has a Renderbuffer as a stencil attachment.
	public boolean	hasStencilBuffer()  { return mStencilBuffer; }
			
			//! Returns the default color Texture::Format for this platform
	public static Texture::Format	getDefaultColorTextureFormat( boolean alpha = true ){
		
	}
			//! Returns the default depth Texture::Format for this platform
	public static Texture::Format	getDefaultDepthTextureFormat(){
		
	}
			//! Returns the default internalFormat for a color Renderbuffer for this platform

	
	public static GLPixelDataInternalFormat			getDefaultColorInternalFormat( ){
		return GLPixelDataInternalFormat.RGBA8;
	}
			//! Returns the default internalFormat for a depth Renderbuffer for this platform
	public static GLPixelDataInternalFormat			getDefaultDepthInternalFormat(){
		return GLPixelDataInternalFormat.DEPTH_COMPONENT24;
	}
			// Returns the +stencil complement of a given internalFormat; ie GL_DEPTH_COMPONENT24 -> GL_DEPTH24_STENCIL8, as well as appropriate pixelDataType for glTexImage2D
	public static void				getDepthStencilFormats( GLPixelDataInternalFormat depthInternalFormat, GLPixelDataInternalFormat resultInternalFormat, GLPixelDataType resultPixelDataType ){
		switch( depthInternalFormat ) {
		
				case DEPTH24_STENCIL8:
					
					resultInternalFormat = GLPixelDataInternalFormat.DEPTH24_STENCIL8; 
					resultPixelDataType = GLPixelDataType.UNSIGNED_INT_24_8;
				break;
				case DEPTH32F_STENCIL8:
					resultInternalFormat = GLPixelDataInternalFormat.DEPTH32F_STENCIL8; resultPixelDataType = GLPixelDataType.FLOAT_32_UNSIGNED_INT_24_8_REV;
				break;
				case DEPTH_COMPONENT24:
					resultInternalFormat = GLPixelDataInternalFormat.DEPTH24_STENCIL8; resultPixelDataType = GLPixelDataType.UNSIGNED_INT_24_8;
				break;
				case DEPTH_COMPONENT32F:
					resultInternalFormat = GLPixelDataInternalFormat.DEPTH32F_STENCIL8; resultPixelDataType = GLPixelDataType.FLOAT_32_UNSIGNED_INT_24_8_REV;
				break;		
			}
	}

			//! Returns the debugging label associated with the Fbo.
	public  String	getLabel()  { return mLabel; }
			//! Sets the debugging label associated with the Fbo. Calls glObjectLabel() when available.
	public void				setLabel(  String &label );
			//! Sets the debugging label associated with the Fbo. Calls glObjectLabel() when available.
	public FboFormat				label(  String &label ) { setLabel( label ); return this; }
			
		 
	 protected int			mDepthBufferInternalFormat;
	 protected int				mSamples, mCoverageSamples;
	 protected boolean			mColorTexture, mDepthTexture;
	 protected boolean			mDepthBuffer;
	 protected boolean			mStencilBuffer;
	 protected Texture::Format	mColorTextureFormat, mDepthTextureFormat;
			 protected String		mLabel; // debug label

			
			 protected Map<Integer,Renderbuffer>	mAttachmentsBuffer;
			 protected Map<Integer,Renderbuffer>	mAttachmentsMultisampleBuffer;
			 protected Map<Integer,GLTexture>		mAttachmentsTexture;

}
