package cc.creativecomputing.gl4;

import java.util.Map;

import cc.creativecomputing.math.CCVector2;




		
		//! Represents an OpenGL Framebuffer Object.
public class Fbo {
			protected int					mWidth, mHeight;
			 protected FboFormat				mFormat;
			 protected int				mId;
			 protected int				mMultisampleFramebufferId;
			
			 protected Map<Integer,Renderbuffer>	mAttachmentsBuffer; // map from attachment ID to Renderbuffer
			 protected Map<Integer,Renderbuffer>	mAttachmentsMultisampleBuffer; // map from attachment ID to Renderbuffer	
			 protected Map<Integer,TextureBaseRef>		mAttachmentsTexture; // map from attachment ID to Texture

			 protected String			mLabel; // debugging label

			 protected  boolean		mNeedsResolve, mNeedsMipmapUpdate;
			
			 protected static int		sMaxSamples, sMaxAttachments;

			//! Creates an FBO \a width pixels wide and \a height pixels high, using Fbo::Format \a format
			public static Fbo create( int width, int height,  FboFormat format ){
				return new Fbo( width, height, format ) ;
			}
			//! Creates an FBO \a width pixels wide and \a height pixels high, a color texture (with optional \a alpha channel), and optionally a \a depth buffer and \a stencil buffer
			public static Fbo create( int width, int height, boolean alpha, boolean depth = true, boolean stencil = false )
			{
			FboFormat format = new FboFormat();
			format.mColorTextureFormat = Format::getDefaultColorTextureFormat( alpha );
			format.mDepthBuffer = depth;
			format.mStencilBuffer = stencil;

			return new Fbo( width, height, format ) ;
			}
			

			//! Returns the width of the FBO in pixels
			public int				getWidth()  { return mWidth; }
			//! Returns the height of the FBO in pixels
			public int				getHeight()  { return mHeight; }
			//! Returns the size of the FBO in pixels
			public CCVector2			getSize()  { return CCVector2( mWidth, mHeight ); }
			//! Returns the bounding area of the FBO in pixels
			public Area			getBounds()  { return Area( 0, 0, mWidth, mHeight ); }
			//! Returns the aspect ratio of the FBO
			public float			getAspectRatio()  { return mWidth / (float)mHeight; }
			//! Returns the Fbo::Format of this FBO
			public  FboFormat	getFormat()  { return mFormat; }
			

			//! Returns a reference to the color Texture2d of the FBO (at \c GL_COLOR_ATTACHMENT0). Resolves multisampling and renders mipmaps if necessary. Returns an empty Ref if there is no Texture2d attached at \c GL_COLOR_ATTACHMENT0
			public Texture2dRef	getColorTexture();	
			//! Returns a reference to the depth Texture2d of the FBO. Resolves multisampling and renders mipmaps if necessary. Returns an empty Ref if there is no Texture2d as a depth attachment.
			public Texture2dRef	getDepthTexture();
			//! Returns a Texture2dRef attached at \a attachment (such as \c GL_COLOR_ATTACHMENT0). Resolves multisampling and renders mipmaps if necessary. Returns NULL if a Texture2d is not bound at \a attachment.
			public Texture2dRef	getTexture2d( GLenum attachment );
			//! Returns a TextureBaseRef attached at \a attachment (such as \c GL_COLOR_ATTACHMENT0). Resolves multisampling and renders mipmaps if necessary. Returns NULL if a Texture is not bound at \a attachment.
			public TextureBaseRef	getTextureBase( GLenum attachment );
			
			//! Binds the color texture associated with an Fbo to its target. Optionally binds to a multitexturing unit when \a textureUnit is non-zero. Optionally binds to a multitexturing unit when \a textureUnit is non-zero. \a attachment specifies which color buffer in the case of multiple attachments.
			public void 			bindTexture( int textureUnit = 0, GLenum attachment = GL_COLOR_ATTACHMENT0 );
			//! Unbinds the texture associated with an Fbo attachment
			public void			unbindTexture( int textureUnit = 0, GLenum attachment = GL_COLOR_ATTACHMENT0 );
			//! Binds the Fbo as the currently active framebuffer, meaning it will receive the results of all subsequent rendering until it is unbound
			public void 			bindFramebuffer( GLenum target = GL_FRAMEBUFFER );
			//! Unbinds the Fbo as the currently active framebuffer, restoring the primary context as the target for all subsequent rendering
			public static void 	unbindFramebuffer();
			//! Resolves internal Multisample FBO to attached Textures. Only necessary when not using getColorTexture() or getTexture(), which resolve automatically.
			public void			resolveTextures() ;

			//! Returns the ID of the framebuffer. For antialiased FBOs this is the ID of the output multisampled FBO
			public int		getId()  { if( mMultisampleFramebufferId ) return mMultisampleFramebufferId; else return mId; }

			//! For antialiased FBOs this returns the ID of the mirror FBO designed for multisampled writing. Returns 0 otherwise.
			public int		getMultisampleId()  { return mMultisampleFramebufferId; }
			//! Returns the resolve FBO, which is the same value as getId() without multisampling
			public int		getResolveId()  { return mId; }

			//! Marks multisampling framebuffer and mipmaps as needing updates. Not generally necessary to call directly.
			public void		markAsDirty();

			//! Copies to FBO \a dst from \a srcArea to \a dstArea using filter \a filter. \a mask allows specification of color (\c GL_COLOR_BUFFER_BIT) and/or depth(\c GL_DEPTH_BUFFER_BIT). Calls glBlitFramebufferEXT() and is subject to its constraints and coordinate system.
			public void		blitTo(  Fbo &dst,  Area &srcArea,  Area &dstArea, GLenum filter = GL_NEAREST, GLbitfield mask = GL_COLOR_BUFFER_BIT ) ;
			//! Copies to the screen from Area \a srcArea to \a dstArea using filter \a filter. \a mask allows specification of color (\c GL_COLOR_BUFFER_BIT) and/or depth(\c GL_DEPTH_BUFFER_BIT). Calls glBlitFramebufferEXT() and is subject to its constraints and coordinate system.
			public void		blitToScreen(  Area &srcArea,  Area &dstArea, GLenum filter = GL_NEAREST, GLbitfield mask = GL_COLOR_BUFFER_BIT ) ;
			//! Copies from the screen from Area \a srcArea to \a dstArea using filter \a filter. \a mask allows specification of color (\c GL_COLOR_BUFFER_BIT) and/or depth(\c GL_DEPTH_BUFFER_BIT). Calls glBlitFramebufferEXT() and is subject to its constraints and coordinate system.
			public void		blitFromScreen(  Area &srcArea,  Area &dstArea, GLenum filter = GL_NEAREST, GLbitfield mask = GL_COLOR_BUFFER_BIT );


			//! Returns the maximum number of samples the graphics card is capable of using per pixel in MSAA for an Fbo
			public static int	getMaxSamples();
			//! Returns the maximum number of color attachments the graphics card is capable of using for an Fbo
			public static int	getMaxAttachments();
			
			//! Returns the debugging label associated with the Fbo.
			public  std::string&	getLabel()  { return mLabel; }
			//! Sets the debugging label associated with the Fbo. Calls glObjectLabel() when available.
			public void				setLabel(  std::string &label );
			
			//! Returns a copy of the pixels in \a attachment within \a area (cropped to the bounding rectangle of the attachment) as an 8-bit per channel Surface. \a attachment ignored on ES 2.
			public Surface8u		readPixels8u(  Area &area, GLenum attachment = GL_COLOR_ATTACHMENT0 ) ;

			


			 protected Fbo( int width, int height,  Format &format );
		 
			 protected void		init();
			 protected void		initMultisamplingSettings( boolean *useMsaa, boolean *useCsaa, Format *format );
			 protected void		prepareAttachments(  Format &format, boolean multisampling );
			 protected void		attachAttachments();
			 protected void		initMultisample(  Format &format );
			 protected void		updateMipmaps( GLenum attachment ) ;
			 protected boolean		checkStatus( class FboExceptionInvalidSpecification *resultExc );
			 protected void		setDrawBuffers( int fbId,  Map<GLenum,Renderbuffer> &attachmentsBuffer,  Map<GLenum,TextureBaseRef> &attachmentsTexture );

			 
		};