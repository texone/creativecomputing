package cc.creativecomputing.gl4;

import cc.creativecomputing.math.CCVector2;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GL4bc;

/**
 * Represents an OpenGL Renderbuffer, used primarily in conjunction with FBOs.
 * @author christianr
 *
 */
public class Renderbuffer {
	
	private int					mWidth, mHeight;
	private int				mId;
	private GLPixelDataInternalFormat				mInternalFormat;
	private int					mSamples, mCoverageSamples;
	private String			mLabel; // debug label
  
	//! Create a Renderbuffer \a width pixels wide and \a heigh pixels high, with an internal format of \a internalFormat, defaulting to GL_RGBA8, MSAA samples \a msaaSamples, and CSAA samples \a coverageSamples

	public  static Renderbuffer create( int width, int height, GLPixelDataInternalFormat internalFormat, int msaaSamples, int coverageSamples){
		return new Renderbuffer( width, height, internalFormat, msaaSamples, coverageSamples );
	}
	
	public  static Renderbuffer create( int width, int height, GLPixelDataInternalFormat internalFormat, int msaaSamples){
		return new Renderbuffer( width, height, internalFormat, msaaSamples, 0 );
	}
	
	public  static Renderbuffer create( int width, int height, GLPixelDataInternalFormat internalFormat){
		return new Renderbuffer( width, height, internalFormat, 0, 0 );
	}
	
	public  static Renderbuffer create( int width, int height){
		return new Renderbuffer( width, height, GLPixelDataInternalFormat.RGBA8, 0, 0 );
	}

	


	//! Returns the width of the Renderbuffer in pixels
	public int		getWidth()  { return mWidth; }
	//! Returns the height of the Renderbuffer in pixels
	public int		getHeight()  { return mHeight; }
	//! Returns the size of the Renderbuffer in pixels
	public CCVector2	getSize()  { return new CCVector2( mWidth, mHeight ); }
	//! Returns the bounding area of the Renderbuffer in pixels
//	public CCRec	getBounds()  { return Area( 0, 0, mWidth, mHeight ); }
	//! Returns the aspect ratio of the Renderbuffer
	public float	getAspectRatio()  { return mWidth / (float)mHeight; }

	//! Returns the ID of the Renderbuffer
	public int	getId()  { return mId; }
	//! Returns the internal format of the Renderbuffer
	public GLPixelDataInternalFormat	getInternalFormat()  { return mInternalFormat; }
	//! Returns the number of samples used in MSAA-style antialiasing. Defaults to none, disabling multisampling
	public int		getSamples()  { return mSamples; }
	//! Returns the number of coverage samples used in CSAA-style antialiasing. Defaults to none.
	public int		getCoverageSamples()  { return mCoverageSamples; }

	//! Returns the debugging label associated with the Renderbuffer.
	public  String	getLabel()  { return mLabel; }
	//! Sets the debugging label associated with the Renderbuffer. Calls glObjectLabel() when available.
	public void				setLabel(  String label ){
		mLabel = label;
	}

  
	//! Create a Renderbuffer \a width pixels wide and \a heigh pixels high, with an internal format of \a internalFormat, MSAA samples \a msaaSamples, and CSAA samples \a coverageSamples
	private Renderbuffer( int width, int height, GLPixelDataInternalFormat internalFormat, int msaaSamples, int coverageSamples ){
		mWidth = width;
		mHeight = height;
		mInternalFormat = internalFormat;
		mSamples = msaaSamples;
		mCoverageSamples = coverageSamples;
		
		GL4bc gl = GLGraphics.currentGL();
		boolean csaaSupported = gl.isExtensionAvailable("GL_NV_framebuffer_multisample_coverage");

		gl.glGenRenderbuffers( 1, GLBufferUtil.intBuffer() );
		mId = GLBufferUtil.intBuffer().get(0);

		if( mSamples > Fbo.getMaxSamples() )
			mSamples = Fbo.getMaxSamples();

		if( ! csaaSupported )
			mCoverageSamples = 0;

//		gl::ScopedRenderbuffer rbb( GL_RENDERBUFFER, mId );


		if( mCoverageSamples > 0) // create a CSAA buffer
			gl.glRenderbufferStorageMultisampleCoverageNV( GL4.GL_RENDERBUFFER, mCoverageSamples, mSamples, mInternalFormat.glID(), mWidth, mHeight );
		else
	
			if( mSamples > 0 )
				gl.glRenderbufferStorageMultisample( GL4.GL_RENDERBUFFER, mSamples, mInternalFormat.glID(), mWidth, mHeight );
			else
				gl.glRenderbufferStorage( GL4.GL_RENDERBUFFER, mInternalFormat.glID(), mWidth, mHeight );
	
	}
  
	private void	init( int aWidth, int aHeight, GLPixelDataInternalFormat internalFormat, int msaaSamples, int coverageSamples );
  
	
}