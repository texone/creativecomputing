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
package cc.creativecomputing.image.format;


import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImageWrite.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Path;
import java.util.Optional;

import org.lwjgl.system.MemoryStack;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCImageException;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelInternalFormat;
import cc.creativecomputing.image.CCPixelType;
import cc.creativecomputing.io.CCNIOUtil;

public class CCSTBFormat implements CCImageFormat {
	
	@Override
	public CCImage createImage(final Path theFile, boolean theFlipVertically){
		try(MemoryStack stack  = MemoryStack.stackPush()){
			IntBuffer myWidth = stack.mallocInt(1);
			IntBuffer myHeight = stack.mallocInt(1);
			IntBuffer myChannels = stack.mallocInt(1);
			stbi_set_flip_vertically_on_load(theFlipVertically);
			ByteBuffer myData = stbi_load(theFile.toString(), myWidth, myHeight, myChannels, 0);
			if(myData == null)throw new CCImageException("Could not load image: " + theFile.toString());
			

			CCPixelFormat thePixelFormat = null;
			CCPixelInternalFormat theInternalFormat = null;
			switch(myChannels.get(0)){
			case 1:
				theInternalFormat = CCPixelInternalFormat.LUMINANCE;
				thePixelFormat = CCPixelFormat.LUMINANCE;
				break;
			case 2:
				theInternalFormat = CCPixelInternalFormat.LUMINANCE_ALPHA;
				thePixelFormat = CCPixelFormat.LUMINANCE_ALPHA;
				break;
			case 3:
				theInternalFormat = CCPixelInternalFormat.RGB;
				thePixelFormat = CCPixelFormat.RGB;
				break;
			case 4:
				theInternalFormat = CCPixelInternalFormat.RGBA;
				thePixelFormat = CCPixelFormat.RGBA;
				break;
			}
			
			CCImage myImage = new CCImage(
				myWidth.get(0), myHeight.get(0), 0, 
				theInternalFormat, thePixelFormat, CCPixelType.UNSIGNED_BYTE, 
				false, false, 
				myData, null
			);
			return myImage;
		}
	}

	@Override
	public CCImage createImage(final InputStream theStream){
		throw new CCImageException("STB from url is not supported");
	}

	@Override
	public CCImage createImage(final URL theUrl){
		
		try {
			final InputStream myStream = theUrl.openStream();
			try {
				return createImage(myStream);
			} finally {
				myStream.close();
			}
		} catch (IOException e) {
			throw new CCImageException(e);
		}
	}
	
	@Override
	public boolean write(final Path thePath, final CCImage theData, final double theQuality) throws CCImageException {
		String myExtension = CCNIOUtil.fileExtension(thePath);
		Optional<ByteBuffer> myByteBuffer = Optional.empty();
		Optional<FloatBuffer> myFloatBuffer = Optional.empty();
		if(theData.buffer() instanceof ByteBuffer){
			myByteBuffer = Optional.of((ByteBuffer)theData.buffer());
		}else if(theData.buffer() instanceof ShortBuffer){
		
		}else if(theData.buffer() instanceof IntBuffer){
			
		}else if(theData.buffer() instanceof FloatBuffer){
			myFloatBuffer = Optional.of((FloatBuffer)theData.buffer());
		}else if(theData.buffer() instanceof DoubleBuffer){
			
		}
		switch(myExtension) {
		case "png":
			myByteBuffer.ifPresent(
				b -> stbi_write_png(
					thePath.toString(), 
					theData.width(), 
					theData.height(), 
					theData.pixelFormat().
					numberOfChannels, 
					b, 4//int stride_in_bytes
				)
			);
			break;
		case "bmp":
			myByteBuffer.ifPresent(
				b -> stbi_write_bmp(
					thePath.toString(), 
					theData.width(), 
					theData.height(), 
					theData.pixelFormat().numberOfChannels, 
					b
				)
			);
	     	break;
		case "tga":
			myByteBuffer.ifPresent(
				b -> stbi_write_tga(
					thePath.toString(), 
					theData.width(), 
					theData.height(), 
					theData.pixelFormat().numberOfChannels, 
					b
				)
			);
			break;
		case "jpg":
			myFloatBuffer.ifPresent(
				b -> stbi_write_jpg(
					thePath.toString(), 
					theData.width(), 
					theData.height(), 
					theData.pixelFormat().numberOfChannels, 
					b, 
					(int)(theQuality * 100)
				)
			);
	     	break;
		case "hdr":
			myFloatBuffer.ifPresent(
				b -> stbi_write_hdr(
					thePath.toString(), 
					theData.width(), 
					theData.height(), 
					theData.pixelFormat().numberOfChannels, 
					b
				)
			);
	    	break;
		}
		return false;
	}
	
	public static void main(String[] args) {
		try(MemoryStack stack  = MemoryStack.stackPush()){
			IntBuffer x = stack.mallocInt(1);
			IntBuffer y = stack.mallocInt(1);
			IntBuffer n = stack.mallocInt(1);
			
			stbi_info(CCNIOUtil.dataPath("waltz.png").toString(), x, y, n);
			
			stbi_is_16_bit(CCNIOUtil.dataPath("waltz.tga").toString());
			CCLog.info(x.get(0),y.get(0),n.get(0),stbi_is_16_bit(CCNIOUtil.dataPath("waltz.jpg").toString()));
			long time = System.nanoTime();
			ByteBuffer myData = stbi_load(CCNIOUtil.dataPath("waltz.jpg").toString(), x, y, n, 0);
//			stbi_write_
//			time = System.nanoTime() - time;
			CCLog.info(x.get(),y.get(),n.get(), time / 10e9d);
//			return null;
		}
	}
}
