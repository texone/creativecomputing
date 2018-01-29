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

import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glMapBuffer;
import static org.lwjgl.opengl.GL15.glUnmapBuffer;
import static org.lwjgl.opengl.GL21.GL_PIXEL_PACK_BUFFER;

import java.nio.ByteBuffer;

public class CCPBO extends CCBufferObject {

	private int[] _myID = new int[1];

	public CCPBO(final int theDataSize) {
		super();
		bind(CCBufferTarget.PIXEL_PACK);
		bufferData(theDataSize, CCUsageFrequency.STATIC, CCUsageTYPE.READ);
		unbind();
	}

	public void beginUnpack() {
		bind(CCBufferTarget.PIXEL_UNPACK);
	}

	/**
	 * it is good idea to release PBOs after use. So that all pixel operations
	 * behave normal ways.
	 */
	public void endUnpack() {
		unbind();
	}

	public void beginPack() {
		bind(CCBufferTarget.PIXEL_PACK);
	}

	/**
	 * it is good idea to release PBOs after use. So that all pixel operations
	 * behave normal ways.
	 */
	public void endPack() {
		unbind();
	}

	public ByteBuffer mapReadBuffer() {
		glBindBuffer(GL_PIXEL_PACK_BUFFER, _myID[0]);
		ByteBuffer myResult = glMapBuffer(GL_PIXEL_PACK_BUFFER, GL_READ_ONLY);
		glBindBuffer(GL_PIXEL_PACK_BUFFER, 0);
		return myResult;
	}

	public void unmapReadBuffer() {
		glBindBuffer(GL_PIXEL_PACK_BUFFER, _myID[0]);
		glUnmapBuffer(GL_PIXEL_PACK_BUFFER);
		glBindBuffer(GL_PIXEL_PACK_BUFFER, 0);
	}

}
