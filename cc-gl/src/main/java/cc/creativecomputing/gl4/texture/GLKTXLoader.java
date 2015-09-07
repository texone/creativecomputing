package cc.creativecomputing.gl4.texture;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.gl4.GLPixelDataFormat;
import cc.creativecomputing.gl4.GLPixelDataInternalFormat;
import cc.creativecomputing.gl4.GLPixelDataType;

import com.jogamp.opengl.GL4;

public class GLKTXLoader {
	private static class GLKTXHeader {

		public static final int HEADER_LENGTH = 64;

		public static final byte[] FILE_IDENTIFIER = new byte[] { (byte) 0xAB, 0x4B, 0x54, 0x58, 0x20, 0x31, 0x31, (byte) 0xBB, 0x0D, 0x0A,
				0x1A, 0x0A };

		int endianness;
		int gltype;
		int gltypesize;
		int glformat;
		int glinternalformat;
		int glbaseinternalformat;
		int pixelwidth;
		int pixelheight;
		int pixeldepth;
		int arrayelements;
		int faces;
		int miplevels;
		int keypairbytes;

		private boolean byteOrderNative = false;

		private void read(ReadableByteChannel theChannel) {
			ByteBuffer buf = ByteBuffer.allocate(HEADER_LENGTH);
			buf.order(ByteOrder.nativeOrder());
			try {
				theChannel.read(buf);
				buf.rewind();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			// Check file identifier
			byte[] magic = new byte[FILE_IDENTIFIER.length];
			buf.get(magic);
			if (!Arrays.equals(magic, FILE_IDENTIFIER)) {
				throw new RuntimeException("Input doesn't start with KTX file identifier");
			}

			// Check endianness and, if necessary, flip the buffer's endianness
			endianness = buf.getInt();
			if (endianness == 0x04030201) {
				// Endianness OK
				byteOrderNative = true;
			} else if (endianness == 0x01020304) {
				// Endianness Reversed
				byteOrderNative = false;
			} else {
				throw new RuntimeException(String.format("Endianness field has an unexpected value: %08x", endianness));
			}

			ByteOrder byteOrder = buf.order();
			if (!byteOrderNative) {
				if (byteOrder == ByteOrder.BIG_ENDIAN) {
					byteOrder = ByteOrder.LITTLE_ENDIAN;
				} else {
					byteOrder = ByteOrder.BIG_ENDIAN;
				}
				buf.order(byteOrder);
			}

			gltype = buf.getInt();
			gltypesize = buf.getInt();
			if (gltypesize != 1 && gltypesize != 2 && gltypesize != 4) {
				throw new RuntimeException("glTypeSize not supported: " + gltypesize);
			}

			glformat = buf.getInt();
			glinternalformat = buf.getInt();
			glbaseinternalformat = buf.getInt();
			pixelwidth = buf.getInt();
			pixelheight = buf.getInt();
			pixeldepth = buf.getInt();
			if (pixelwidth < 0 || pixelheight < 0 || pixeldepth < 0) {
				throw new RuntimeException(String.format("Invalid number of pixel dimensions: %dx%dx%d", pixelwidth, pixelheight,
						pixeldepth));
			}
			arrayelements = buf.getInt();
			if (arrayelements < 0) {
				throw new RuntimeException(String.format("Invalid number of array elements: %d", arrayelements));
			}
			faces = buf.getInt();
			if (faces != 0 && faces != 1 && faces != 6) {
				throw new RuntimeException(String.format("Invalid number of faces: %d", faces));
			}
			miplevels = buf.getInt();
			if (miplevels < 0) {
				throw new RuntimeException(String.format("Invalid number of mipmap levels: %d", miplevels));
			}
			keypairbytes = buf.getInt();
			if (keypairbytes < 0) {
				throw new RuntimeException(String.format("Invalid key/value byte size: %d", keypairbytes));
			}
		}
	}

	// union keyvaluepair
	// {
	// unsigned int size;
	// unsigned char rawbytes[4];
	// };
	
	public static GLTexture load(Path thePath){
		try {
			return load(FileChannel.open(thePath, StandardOpenOption.READ));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static GLTexture load(ReadableByteChannel theInputChannel){
		if(theInputChannel == null)return null;
		
		GLKTXHeader myHeader = new GLKTXHeader();
		myHeader.read(theInputChannel);
//	    GLuint temp = 0;
//	    GLuint retval = 0;
//	    
//	    size_t data_start, data_end;
//	    unsigned char * data;
//	    GLTextureTarget target = GL_NONE;

	    if (myHeader.endianness == 0x04030201) {
	        // No swap needed
	    } else if (myHeader.endianness == 0x01020304){
	        // Swap needed
	    	myHeader.endianness            = Integer.reverseBytes(myHeader.endianness);
	    	myHeader.gltype                = Integer.reverseBytes(myHeader.gltype);
	    	myHeader.gltypesize            = Integer.reverseBytes(myHeader.gltypesize);
	    	myHeader.glformat              = Integer.reverseBytes(myHeader.glformat);
	    	myHeader.glinternalformat      = Integer.reverseBytes(myHeader.glinternalformat);
	    	myHeader.glbaseinternalformat  = Integer.reverseBytes(myHeader.glbaseinternalformat);
	        myHeader.pixelwidth            = Integer.reverseBytes(myHeader.pixelwidth);
	        myHeader.pixelheight           = Integer.reverseBytes(myHeader.pixelheight);
	        myHeader.pixeldepth            = Integer.reverseBytes(myHeader.pixeldepth);
	        myHeader.arrayelements         = Integer.reverseBytes(myHeader.arrayelements);
	        myHeader.faces                 = Integer.reverseBytes(myHeader.faces);
	        myHeader.miplevels             = Integer.reverseBytes(myHeader.miplevels);
	        myHeader.keypairbytes          = Integer.reverseBytes(myHeader.keypairbytes);
	    }
	    
	    GLTextureTarget target = null;
	    

	    // Guess target (texture type)
	    if (myHeader.pixelheight == 0){
	        if (myHeader.arrayelements == 0){
	            target = GLTextureTarget.TEXTURE_1D;
	        } else {
	            target = GLTextureTarget.TEXTURE_1D_ARRAY;
	        }
	    } else if (myHeader.pixeldepth == 0) {
	        if (myHeader.arrayelements == 0){
	            if (myHeader.faces == 0 || myHeader.faces == 1){
	                target = GLTextureTarget.TEXTURE_2D;
	            } else {
	                target = GLTextureTarget.TEXTURE_CUBE_MAP;
	            }
	        }else {
	            if (myHeader.faces == 0){
	                target = GLTextureTarget.TEXTURE_2D_ARRAY;
	            } else {
	                target = GLTextureTarget.TEXTURE_CUBE_MAP_ARRAY;
	            }
	        }
	    }else{
	        target = GLTextureTarget.TEXTURE_3D;
	    }

	    if(target == null)throw new RuntimeException("Couldn't figure out target");
	    if(myHeader.pixelwidth == 0)throw new RuntimeException("Texture has no width???");
	    if(myHeader.pixelheight == 0 && myHeader.pixeldepth != 0)throw new RuntimeException("Texture has depth but no height???");

	    ByteBuffer data = null;
	    
	    if(theInputChannel instanceof FileChannel){
	    	try {
		    	FileChannel myFileChannel = (FileChannel)theInputChannel;
			    data = ByteBuffer.allocateDirect((int)(myFileChannel.size() - myFileChannel.position()));
		    	myFileChannel.read(data);
		    	data.rewind();
	    	} catch (IOException e) {
				throw new RuntimeException(e);
			}
	    }

	    if (myHeader.miplevels == 0){
	        myHeader.miplevels = 1;
	    }
	    
	    GLPixelDataInternalFormat myInternalFormat = GLPixelDataInternalFormat.fromGLID(myHeader.glinternalformat);
	    GLPixelDataFormat myFormat = GLPixelDataFormat.fromGLID(myHeader.glformat);
	    GLPixelDataType myType = GLPixelDataType.fromGLID(myHeader.gltype);
	    
	    CCLog.info(myInternalFormat + " ; " + myFormat + " ; " + myType + " ; " + Integer.toHexString(myHeader.glinternalformat));
	   

	    GL4 gl = GLGraphics.currentGL();
	    GLTexture myResult = null;
	    
	    switch (target){
	        case TEXTURE_1D:
	        	GLTexture1D myTexture1D = new GLTexture1D(myHeader.miplevels, myInternalFormat, myHeader.pixelwidth);
	        	myTexture1D.texSubImage1D(0, 0, myHeader.pixelwidth, myFormat, myType, data);
	        	myResult = myTexture1D;
	        	break;
	        case TEXTURE_2D:
	        	GLTexture2D myTexture2D = new GLTexture2D(myHeader.miplevels, myInternalFormat, myHeader.pixelwidth, myHeader.pixelheight);
	            
	            {
	                int height = myHeader.pixelheight;
	                int width = myHeader.pixelwidth;
	                gl.glPixelStorei(GL4.GL_UNPACK_ALIGNMENT, 1);
	                for (int i = 0; i < myHeader.miplevels; i++){
	                	myTexture2D.texSubImage2D(i, 0, 0, width, height, myFormat, myType, data);
//	                    ptr += height * calculate_stride(h, width, 1);
	                    
	                    height >>= 1;
	                    width >>= 1;
	                    if (height <= 0)
	                        height = 1;
	                    if (width <= 0) 
	                        width = 1;
	                }
	            }
	            myResult = myTexture2D;
	            break;
	        case TEXTURE_3D:
	        	GLTexture3D myTexture3D = new GLTexture3D(myHeader.miplevels, myInternalFormat, myHeader.pixelwidth, myHeader.pixelheight, myHeader.pixeldepth);
	        	myTexture3D.texSubImage3D(0, 0, 0, 0, myHeader.pixelwidth, myHeader.pixelheight, myHeader.pixeldepth, myFormat, myType, data);
	        	myResult = myTexture3D;
	        	break;
	        case TEXTURE_1D_ARRAY:
	        	GLTexture1DArray myTexture1DArray = new GLTexture1DArray(myHeader.miplevels, myInternalFormat, myHeader.pixelwidth, myHeader.arrayelements);
	        	myTexture1DArray.texSubImage(0, 0, 0, myHeader.pixelwidth, myHeader.arrayelements, myFormat, myType, data);
	        	myResult = myTexture1DArray;
	        	break;
	        case TEXTURE_2D_ARRAY:
	        	GLTexture2DArray myTexture2DArray = new GLTexture2DArray(myHeader.miplevels, myInternalFormat, myHeader.pixelwidth, myHeader.pixelheight, myHeader.arrayelements);
	        	myTexture2DArray.texSubImage(0, 0, 0, 0, myHeader.pixelwidth, myHeader.pixelheight, myHeader.arrayelements, myFormat, myType, data);
	        	myResult = myTexture2DArray;
	        	break;
//	        case TEXTURE_CUBE_MAP:
//	            glTexStorage2D(GL_TEXTURE_CUBE_MAP, myHeader.miplevels, myHeader.glinternalformat, myHeader.pixelwidth, myHeader.pixelheight);
//	            // glTexSubImage3D(GL_TEXTURE_CUBE_MAP, 0, 0, 0, 0, myHeader.pixelwidth, myHeader.pixelheight, myHeader.faces, myHeader.glformat, myHeader.gltype, data);
//	            {
//	                int face_size = calculate_face_size(h);
//	                for (int i = 0; i < myHeader.faces; i++)
//	                {
//	                    glTexSubImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, 0, 0, myHeader.pixelwidth, myHeader.pixelheight, myHeader.glformat, myHeader.gltype, data + face_size * i);
//	                }
//	            }
//	            break;
//	        case TEXTURE_CUBE_MAP_ARRAY:
//	            glTexStorage3D(GL_TEXTURE_CUBE_MAP_ARRAY, myHeader.miplevels, myHeader.glinternalformat, myHeader.pixelwidth, myHeader.pixelheight, myHeader.arrayelements);
//	            glTexSubImage3D(GL_TEXTURE_CUBE_MAP_ARRAY, 0, 0, 0, 0, myHeader.pixelwidth, myHeader.pixelheight, myHeader.faces * myHeader.arrayelements, myHeader.glformat, myHeader.gltype, data);
//	            break;
//	        default:                                               // Should never happen
//	            goto fail_target;
	    }

	    if (myHeader.miplevels == 1){
	    	myResult.generateMipMaps();
	    }
	    return myResult;
	}
}
