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
package cc.creativecomputing.graphics;

import java.nio.ByteBuffer;

import com.jogamp.opengl.GL2;

public class CCPBO {

	private int[] _myID = new int[1];
	
	public CCPBO(final int theDataSize){
		GL2 gl = CCGraphics.currentGL();
		gl.glGenBuffers(1, _myID,0);
		gl.glBindBuffer(GL2.GL_PIXEL_PACK_BUFFER, _myID[0]);
		gl.glBufferData(GL2.GL_PIXEL_PACK_BUFFER, theDataSize, null, GL2.GL_STATIC_READ);
        gl.glBindBuffer(GL2.GL_PIXEL_PACK_BUFFER, 0);
	}
	
	public void beginUnpack(){
		GL2 gl = CCGraphics.currentGL();
        gl.glBindBuffer(GL2.GL_PIXEL_UNPACK_BUFFER, _myID[0]);
	}
	
	/**
	 * it is good idea to release PBOs after use.
	 * So that all pixel operations behave normal ways.
	 */
	public void endUnpack(){
		GL2 gl = CCGraphics.currentGL();
		gl.glBindBuffer(GL2.GL_PIXEL_UNPACK_BUFFER, 0);
	}
	
	public void beginPack(){
		GL2 gl = CCGraphics.currentGL();
        gl.glBindBuffer(GL2.GL_PIXEL_PACK_BUFFER, _myID[0]);
	}
	
	/**
	 * it is good idea to release PBOs after use.
	 * So that all pixel operations behave normal ways.
	 */
	public void endPack(){
		GL2 gl = CCGraphics.currentGL();
		gl.glBindBuffer(GL2.GL_PIXEL_PACK_BUFFER, 0);
	}
	
	public ByteBuffer mapBuffer(){
		GL2 gl = CCGraphics.currentGL();
		return gl.glMapBuffer(GL2.GL_PIXEL_UNPACK_BUFFER, GL2.GL_WRITE_ONLY);
	}
	
	public ByteBuffer mapReadBuffer() {
		GL2 gl = CCGraphics.currentGL();
		gl.glBindBuffer(GL2.GL_PIXEL_PACK_BUFFER, _myID[0]);
		ByteBuffer myResult =  gl.glMapBuffer(GL2.GL_PIXEL_PACK_BUFFER, GL2.GL_READ_ONLY);
		gl.glBindBuffer(GL2.GL_PIXEL_PACK_BUFFER, 0);
		return myResult;
	}
	
	public void unmapReadBuffer() {
		GL2 gl = CCGraphics.currentGL();
		gl.glBindBuffer(GL2.GL_PIXEL_PACK_BUFFER, _myID[0]);
		gl.glUnmapBuffer(GL2.GL_PIXEL_PACK_BUFFER);
		gl.glBindBuffer(GL2.GL_PIXEL_PACK_BUFFER, 0);
	}
	
	
	@Override
	public void finalize(){
		GL2 gl = CCGraphics.currentGL();
		gl.glDeleteBuffers(1, _myID,0);
	}
}
