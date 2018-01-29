package cc.creativecomputing.uinano;
import String;
import nanogui.*;
import javax.swing.*;

/*
    src/example1.cpp -- C++ version of an example application that shows
    how to use the various widget classes. For a Python implementation, see
    '../python/example1.py'.

    NanoGUI was developed by Wenzel Jakob <wenzel.jakob@epfl.ch>.
    The widget drawing code is based on the NanoVG demo application
    by Mikko Mononen.

    All rights reserved. Use of this source code is governed by a
    BSD-style license that can be found in the LICENSE.txt file.
*/


// Includes for the GLTexture class.

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#if __GNUC__
//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#pragma GCC diagnostic ignored "-Wmissing-field-initializers"
///#endif
//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#if _WIN32
//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#pragma warning(push)
//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#pragma warning(disable: 4457 4456 4005 4312)
///#endif

///#define STB_IMAGE_IMPLEMENTATION

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#if _WIN32
//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#pragma warning(pop)
///#endif
//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#if _WIN32
//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#if APIENTRY
//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#undef APIENTRY
///#endif
///#endif

public class GLTexture implements java.io.Closeable
{
	@FunctionalInterface
	public interface handleType
	{
		std::unique_ptr<uint8_t[], void invoke(Object UnnamedParameter1)>;
	}

//C++ TO JAVA CONVERTER TODO TASK: The implementation of the following method could not be found:
//	GLTexture();
	public GLTexture(String textureName)
	{
		this.mTextureName = textureName;
		this.mTextureId = 0;
	}

	public GLTexture(String textureName, GLint textureId)
	{
		this.mTextureName = textureName;
		this.mTextureId = textureId;
	}

//C++ TO JAVA CONVERTER TODO TASK: Java has no equivalent to ' = delete':
//ORIGINAL LINE: GLTexture(const GLTexture& other) = delete;
//C++ TO JAVA CONVERTER TODO TASK: The implementation of the following method could not be found:
//	GLTexture(GLTexture other);
	public GLTexture(GLTexture & other) noexcept
	{
		this.mTextureName = std::move(other.mTextureName);
//C++ TO JAVA CONVERTER WARNING: The following line was determined to be a copy assignment (rather than a reference assignment) - this should be verified and a 'copyFrom' method should be created if it does not yet exist:
//ORIGINAL LINE: this.mTextureId = other.mTextureId;
		this.mTextureId.copyFrom(other.mTextureId);
		other.mTextureId = 0;
	}
//C++ TO JAVA CONVERTER TODO TASK: Java has no equivalent to ' = delete':
//ORIGINAL LINE: GLTexture& operator =(const GLTexture& other) = delete;
//C++ TO JAVA CONVERTER TODO TASK: The implementation of the following method could not be found:
//	GLTexture operator =(GLTexture other);
//C++ TO JAVA CONVERTER TODO TASK: Java has no equivalent to 'noexcept':
//ORIGINAL LINE: GLTexture& operator =(GLTexture&& other) noexcept
//C++ TO JAVA CONVERTER NOTE: This 'copyFrom' method was converted from the original copy assignment operator:
	public final GLTexture copyFrom(GLTexture & other)
	{
		mTextureName = std::move(other.mTextureName);
		std::swap(mTextureId, other.mTextureId);
		return this;
	}
//C++ TO JAVA CONVERTER TODO TASK: Java has no equivalent to 'noexcept':
//ORIGINAL LINE: ~GLTexture() noexcept
	public final void close()
	{
		if (mTextureId != null)
		{
			glDeleteTextures(1, mTextureId);
		}
	}

//C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
//ORIGINAL LINE: GLuint texture() const
	public final GLuint texture()
	{
		return mTextureId;
	}
//C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
//ORIGINAL LINE: const String& textureName() const
	public final String textureName()
	{
		return mTextureName;
	}

	/**
	*  Load a file in memory and create an OpenGL texture.
	*  Returns a handle type (an std::unique_ptr) to the loaded pixels.
	*/
	public final handleType load(String fileName)
	{
		if (mTextureId != null)
		{
			glDeleteTextures(1, mTextureId);
			mTextureId = 0;
		}
		int force_channels = 0;
		int w;
		int h;
		int n;
		handleType textureData = new handleType(stbi_load(fileName, w, h, n, force_channels), stbi_image_free);
		if (textureData == null)
		{
			throw new IllegalArgumentException("Could not load texture data from file " + fileName);
		}
		glGenTextures(1, mTextureId);
		glBindTexture(GL_TEXTURE_2D, mTextureId);
		GLint internalFormat = new GLint();
		GLint format = new GLint();
		switch (n)
		{
			case 1:
				internalFormat = GL_R8;
				format = GL_RED;
				break;
			case 2:
				internalFormat = GL_RG8;
				format = GL_RG;
				break;
			case 3:
				internalFormat = GL_RGB8;
				format = GL_RGB;
				break;
			case 4:
				internalFormat = GL_RGBA8;
				format = GL_RGBA;
				break;
			default:
				internalFormat = 0;
				format = 0;
				break;
		}
		glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, w, h, 0, format, GL_UNSIGNED_BYTE, textureData.get());
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		return textureData;
	}

	private String mTextureName;
	private GLuint mTextureId = new GLuint();
}