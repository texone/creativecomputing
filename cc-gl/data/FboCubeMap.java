package cc.creativecomputing.gl4;

public class FboCubeMap {
	//! Helper class for implementing dynamic cube mapping
	class FboCubeMap : public Fbo {
	  public:
		struct Format : private Fbo::Format {
			// Default constructor. Enables a depth RenderBuffer and a color CubeMap
			Format();
			
			//! Sets the TextureCubeMap format for the default CubeMap.
			Format&							textureCubeMapFormat( const TextureCubeMap::Format &format )	{ mTextureCubeMapFormat = format; return *this; }
			//! Returns the TextureCubeMap format for the default CubeMap.
			const TextureCubeMap::Format&	getTextureCubeMapFormat() const { return mTextureCubeMapFormat; }
			
			//! Disables a depth Buffer
			Format&	disableDepth() { mDepthBuffer = false; return *this; }

			//! Sets the debugging label associated with the Fbo. Calls glObjectLabel() when available.
			Format&	label( const std::string &label ) { setLabel( label ); return *this; }
			
		  protected:
			gl::TextureCubeMap::Format	mTextureCubeMapFormat;
			
			friend class FboCubeMap;
		};
	  
		static FboCubeMapRef	create( int32_t faceWidth, int32_t faceHeight, const Format &format = Format() );
		
		//! Binds a face of the Fbo as the currently active framebuffer. \a faceTarget expects values in the \c GL_TEXTURE_CUBE_MAP_POSITIVE_X family.
		void 	bindFramebufferFace( GLenum faceTarget, GLint level = 0, GLenum target = GL_FRAMEBUFFER, GLenum attachment = GL_COLOR_ATTACHMENT0 );
		//! Returns the view matrix appropriate for a given face (in the \c GL_TEXTURE_CUBE_MAP_POSITIVE_X family) looking from the position \a eyePos
		mat4	calcViewMatrix( GLenum face, const vec3 &eyePos );

		//! Returns a TextureCubeMapRef attached at \a attachment (default \c GL_COLOR_ATTACHMENT0). Resolves multisampling and renders mipmaps if necessary. Returns NULL if a TextureCubeMap is not bound at \a attachment.
		TextureCubeMapRef	getTextureCubeMap( GLenum attachment = GL_COLOR_ATTACHMENT0 );
		
	  protected:
		FboCubeMap( int32_t faceWidth, int32_t faceHeight, const Format &format, const TextureCubeMapRef &textureCubeMap );
	  
		TextureCubeMapRef		mTextureCubeMap;
	};
}
