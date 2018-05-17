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
import java.nio.IntBuffer;
import java.nio.file.Path;

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
	public CCImage createImage(
		final Path theFile, 
		CCPixelInternalFormat theInternalFormat, CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		try(MemoryStack stack  = MemoryStack.stackPush()){
			IntBuffer myWidth = stack.mallocInt(1);
			IntBuffer myHeight = stack.mallocInt(1);
			IntBuffer myChannels = stack.mallocInt(1);
			
			ByteBuffer myData = stbi_load(theFile.toString(), myWidth, myHeight, myChannels, 0);
			
			if(theInternalFormat == null){
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
			}
			
			CCImage myImage = new CCImage(
				myWidth.get(0), myHeight.get(0), 0, 
				theInternalFormat, thePixelFormat, CCPixelType.UNSIGNED_BYTE, 
				false, false, 
				myData, null
			);
			myImage.mustFlipVertically(true);
			return myImage;
		}
	}

	@Override
	public CCImage createImage(
		final InputStream theStream, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		return null;
	}

	@Override
	public CCImage createImage(
		final URL theUrl, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		
		try {
			final InputStream myStream = theUrl.openStream();
			try {
				return createImage(myStream, theInternalFormat, thePixelFormat, theFileSuffix);
			} finally {
				myStream.close();
			}
		} catch (IOException e) {
			throw new CCImageException(e);
		}
	}
	
	@Override
	public boolean write(final Path thePath, final CCImage theData, final double theQuality) throws CCImageException {
			
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
