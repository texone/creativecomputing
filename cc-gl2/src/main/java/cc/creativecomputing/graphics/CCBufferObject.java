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

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INVALID_ENUM;
import static org.lwjgl.opengl.GL11.GL_INVALID_OPERATION;
import static org.lwjgl.opengl.GL11.GL_INVALID_VALUE;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.GL_OUT_OF_MEMORY;
import static org.lwjgl.opengl.GL11.GL_STACK_OVERFLOW;
import static org.lwjgl.opengl.GL11.GL_STACK_UNDERFLOW;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_COPY;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_READ;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_COPY;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_STATIC_READ;
import static org.lwjgl.opengl.GL15.GL_STREAM_COPY;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15.GL_STREAM_READ;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15.glGetBufferSubData;
import static org.lwjgl.opengl.GL15.glMapBuffer;
import static org.lwjgl.opengl.GL15.glUnmapBuffer;
import static org.lwjgl.opengl.GL21.GL_PIXEL_PACK_BUFFER;
import static org.lwjgl.opengl.GL21.GL_PIXEL_UNPACK_BUFFER;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_INVALID_FRAMEBUFFER_OPERATION;
import static org.lwjgl.opengl.GL30.GL_TRANSFORM_FEEDBACK_BUFFER;
import static org.lwjgl.opengl.GL31.GL_COPY_READ_BUFFER;
import static org.lwjgl.opengl.GL31.GL_COPY_WRITE_BUFFER;
import static org.lwjgl.opengl.GL31.GL_TEXTURE_BUFFER;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL42.GL_ATOMIC_COUNTER_BUFFER;
import static org.lwjgl.opengl.GL43.GL_DISPATCH_INDIRECT_BUFFER;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL44.GL_QUERY_BUFFER;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;


public class CCBufferObject {
	
	/**
	 * Specifies the target to which the buffer object is bound.
	 * @author Riekoff
	 *
	 */
	public enum CCBufferTarget{
		/**
		 * Array buffers store vertex attributes such as color, 
		 * position, texture coordinates, or other custom attributes.
		 */
		ARRAY(GL_ARRAY_BUFFER), 
		/**
		 * Array buffers store vertex attributes such as color, 
		 * position, texture coordinates, or other custom attributes.
		 */
		COPY_READ(GL_COPY_READ_BUFFER), 
		/**
		 * Buffer used as the target for copies with glCopyBufferSubData.
		 */
		COPY_WRITE(GL_COPY_WRITE_BUFFER), 
		/**
		 * Index array buffer used for sourcing indices for glDrawElements, 
		 * glDrawRangeElements, and glDrawElementsInstanced.
		 */
		ELEMENT_ARRAY(GL_ELEMENT_ARRAY_BUFFER),
		/**
		 * Target buffer for pixel pack operations such as glReadPixels.
		 */
        PIXEL_PACK(GL_PIXEL_PACK_BUFFER), 
        /**
         * Source buffer for texture update functions such as glTexImage1D, 
         * glTexImage2D, glTexImage3D, glTexSubImage1D, glTexSubImage2D, and glTexSubImage3D.
         */
        PIXEL_UNPACK(GL_PIXEL_UNPACK_BUFFER),
        /**
         * Buffer accessible to shaders through texel fetches.
         */
		TEXTURE_BUFFER(GL_TEXTURE_BUFFER),
        /**
         * Buffer written to by a transform feedback vertex shader.
         */
		TRANSFORM_FEEDBACK_BUFFER(GL_TRANSFORM_FEEDBACK_BUFFER),
        /**
         * Uniform values accessible to shaders.
         */
		UNIFORM_BUFFER(GL_UNIFORM_BUFFER),
		/**
		 * Atomic counter storage
		 */
		ATOMIC_COUNTER_BUFFER(GL_ATOMIC_COUNTER_BUFFER),
		/**
		 * Indirect compute dispatch commands
		 */
		ISPATCH_INDIRECT_BUFFER(GL_DISPATCH_INDIRECT_BUFFER),
		/**
		 * Read-write storage for shaders
		 */
		SHADER_STORAGE_BUFFER(GL_SHADER_STORAGE_BUFFER),
		/**
		 * Query result buffer
		 */
		QUERY_BUFFER(GL_QUERY_BUFFER);
    
		private final int glId;
		  
		CCBufferTarget(final int theglID){
			glId = theglID;
		}
	}
	
	/**
	 * the frequency of buffer access (modification and usage)
	 * @author Riekoff
	 *
	 */
	public enum CCUsageFrequency{
		/**
		 * The data store contents will be modified once and used at most a few times.
		 */
		STREAM, 
		/**
		 * The data store contents will be modified once and used many times.
		 */
		STATIC,
		/**
		 * The data store contents will be modified repeatedly and used many times.
		 */
		DYNAMIC
    }
	
	/**
	 * the type of buffer access
	 * @author Riekoff
	 *
	 */
	public enum CCUsageTYPE{
		/**
		 * The data store contents are modified by the application, and used as 
		 * the source for GL drawing and image specification commands.
		 */
		DRAW, 
		/**
		 * The data store contents are modified by reading data from the GL, 
		 * and used to return that data when queried by the application.
		 */
		READ,
		/**
		 * The data store contents are modified by reading data from the GL, 
		 * and used as the source for GL drawing and image specification commands.
		 */
		COPY
    }

	private int[] _myBufferID;
	
	private int _mySize;
	
	private boolean _myIsMapped = false;
	
	private ByteBuffer _myData;
	
	public CCBufferObject(){
		_myBufferID = new int[1];
		_mySize = 0;
		glGenBuffers(_myBufferID);
	}
	
	public CCBufferObject(int theSize){
		this();
		bind(CCBufferTarget.ARRAY);
		bufferData(theSize, CCUsageFrequency.DYNAMIC, CCUsageTYPE.DRAW);
//		updateData();
	}
	
	public void updateData(){
		bind(CCBufferTarget.ARRAY);
		
		// Initialize data store of buffer object
		_myData = mapBuffer();
		
		if(_myData == null)
		switch(glGetError()){
		case GL_NO_ERROR:
			CCLog.error(" # NO ERROR REPORTED");
			break;
		case GL_INVALID_ENUM:
			CCLog.error(" # INVALID ENUMERATION REPORTED. check for errors in OPENGL calls with constants.");
			break;
		case GL_INVALID_VALUE:
			CCLog.error("# INVALID VALUE REPORTED. check for errors with passed values that are out of a defined range.");
			break;
		case GL_INVALID_OPERATION:
			CCLog.error("# INVALID OPERATION REPORTED. check for function calls that are invalid in the current graphics state.");
			break;
		case GL_STACK_OVERFLOW:
			CCLog.error("# STACK OVERFLOW REPORTED. check for errors in matrix operations");
			break;
		case GL_STACK_UNDERFLOW:
			CCLog.error("# STACK UNDERFLOW REPORTED. check for errors  in matrix operations");
			break;
		case GL_OUT_OF_MEMORY:
			CCLog.error("# OUT OF MEMORY. not enough memory to execute the commands");
			break;
		case GL_INVALID_FRAMEBUFFER_OPERATION:
			CCLog.error("# INVALID_FRAMEBUFFER_OPERATION. not enough memory to execute the commands");
			break;
		}
		if(_myData != null)_myData.order(ByteOrder.nativeOrder());
		
//		unmapBuffer(CCBufferTarget.ARRAY);
		// Unbind buffer; will be bound again in main loop
		unbind();
	}
	
	public ByteBuffer data(){
		if(_myData == null){
			bind(CCBufferTarget.ARRAY);
			_myData = mapBuffer();
		}
		unbind();
		if(_myData == null){
			
			_myData = ByteBuffer.allocateDirect(_mySize);
			glGetBufferSubData(_myCurrentTarget.glId, 0, _myData);
		}
		return _myData;
	}
	
	/**
	 * <p>
	 * mapBuffer maps to the client's address space the entire data store of the buffer 
	 * object currently bound to target. The data can then be directly read and/or written 
	 * relative to the returned pointer, depending on the specified access policy. If the
	 *  GL is unable to map the buffer object's data store, glMapBuffer generates an error 
	 *  and returns NULL. This may occur for system-specific reasons, such as low virtual 
	 *  memory availability.</p>
	 *  <p>
	 *  If a mapped data store is accessed in a way inconsistent with the specified access 
	 *  policy, no error is generated, but performance may be negatively impacted and system 
	 *  errors, including program termination, may result. Unlike the usage parameter of 
	 *  {@linkplain #bufferData(CCBufferTarget, int, FloatBuffer)}, access is not a hint, 
	 *  and does in fact constrain the usage of the mapped 
	 *  data store on some GL implementations. In order to achieve the highest performance 
	 *  available, a buffer object's data store should be used in ways consistent with both 
	 *  its specified usage and access parameters.</p>
	 *  
	 * @param theTarget Specifies the target buffer object being mapped.
	 * @return
	 */
	public ByteBuffer mapBuffer(){
		_myIsMapped = true;
		ByteBuffer myResult = glMapBuffer(_myCurrentTarget.glId, GL_WRITE_ONLY);
		return myResult;
	}
	
	/**
	 * A mapped data store must be unmapped with <b>unmapBuffer </b>
	 * before its buffer object is used. Otherwise an error will be generated by any GL 
	 * command that attempts to dereference the buffer object's data store. When a data store 
	 * is unmapped, the pointer to its data store becomes invalid. <b>unmapBuffer </b> returns 
	 * <code>true</code> unless the data store contents have become corrupt during the time 
	 * the data store was mapped. This can occur for system-specific reasons that affect 
	 * the availability of graphics memory, such as screen mode changes. In such situations, 
	 * <code>false</code> is returned and the data store contents are undefined. An application 
	 * must detect this rare condition and reinitialize the data store.
	 * <p>
	 * A buffer object's mapped data store is automatically unmapped when the buffer object 
	 * is deleted or its data store is recreated with glBufferData
	 * @param theTarget
	 * @return <code>true</code> unless the data store contents have become corrupt
	 */
	public boolean unmapBuffer(){
		_myIsMapped = false;
		return glUnmapBuffer(_myCurrentTarget.glId);
	}
	
	/**
	 * Use this method to check whether the buffer is mapped or not.
	 * @see #mapBuffer(CCBufferTarget)
	 * @see #unmapBuffer(CCBufferTarget)
	 * @return <code>true</code> when the buffer is mapped otherwise false
	 */
	public boolean isMapped(){
		return _myIsMapped;
	}
	
	/**
	 * Returns the GL id of this buffer
	 * @return the gl id
	 */
	public int id(){
		return _myBufferID[0];
	}
	
	private CCBufferTarget _myCurrentTarget;
	/**
	 * lets you create or use a named buffer object. Calling glBindBuffer 
	 * with target set to ARRAY, ELEMENT_ARRAY, PIXEL_PACK or PIXEL_UNPACK 
	 * and buffer set to the name of the new buffer object binds the buffer 
	 * object name to the target. When a buffer object is bound to a target, 
	 * the previous binding for that target is automatically broken.
	 * @param theTarget Specifies the target to which the buffer object is bound.
	 */
	public void bind(CCBufferTarget theTarget){
		_myCurrentTarget = theTarget;
		glBindBuffer(theTarget.glId, _myBufferID[0]);
	}
	
	public void unbind(){
		glBindBuffer(_myCurrentTarget.glId, 0);
	}
	
	private int glUsage(CCUsageFrequency theUsageFrequency, CCUsageTYPE theUsageType){
		switch(theUsageFrequency){
		case STREAM:
			switch (theUsageType) {
			case DRAW:
				return GL_STREAM_DRAW;
			case READ:
				return GL_STREAM_READ;
			case COPY:
				return GL_STREAM_COPY;
			}
		case STATIC:
			switch (theUsageType) {
			case DRAW:
				return GL_STATIC_DRAW;
			case READ:
				return GL_STATIC_READ;
			case COPY:
				return GL_STATIC_COPY;
			}
		case DYNAMIC:
			switch (theUsageType) {
			case DRAW:
				return GL_DYNAMIC_DRAW;
			case READ:
				return GL_DYNAMIC_READ;
			case COPY:
				return GL_DYNAMIC_COPY;
			}
		}
		return 0;
	}
	
	/**
	 * Creates a new data store for the buffer object currently bound to target. 
	 * Any pre-existing data store is deleted. The new data store is created with 
	 * the specified size in bytes and usage. If data is not NULL, the data store 
	 * is initialized with data from this pointer. In its initial state, the new 
	 * data store is not mapped, it has a NULL mapped pointer, and its mapped access 
	 * is GL_READ_WRITE.
	 * @param theTarget Specifies the target buffer object.
	 * @param theSize Specifies the size in bytes of the buffer object's new data store.
	 * @param theData Specifies a pointer to data that will be copied into the data store for initialization, or NULL if no data is to be copied.
	 * @param theUsageFrequency Specifies the expected usage frequency of the data store.
	 * @param theUsageType Specifies the expected usage type of the data store.
	 */
	public void bufferData(int theSize, CCUsageFrequency theUsageFrequency, CCUsageTYPE theUsageType){
		_mySize = theSize;
		glBufferData(_myCurrentTarget.glId, theSize, glUsage(theUsageFrequency, theUsageType));
	}
	
	public void bufferData(ByteBuffer theData, CCUsageFrequency theUsageFrequency, CCUsageTYPE theUsageType){
		if(theData != null)theData.rewind();
		_mySize = theData.capacity();
		glBufferData(_myCurrentTarget.glId, theData, glUsage(theUsageFrequency, theUsageType));
	}
	
	public void bufferData(ByteBuffer theData){
		bufferData(theData, CCUsageFrequency.DYNAMIC, CCUsageTYPE.DRAW);
	}
	
	public void bufferData(int theSize){
		bufferData(theSize, CCUsageFrequency.DYNAMIC, CCUsageTYPE.DRAW);
	}
	
	public void bufferData(){
		if(_myData == null) bufferData(_mySize, CCUsageFrequency.DYNAMIC, CCUsageTYPE.DRAW);
		else bufferData(_myData, CCUsageFrequency.DYNAMIC, CCUsageTYPE.DRAW);
	}

	public void bufferSubData(CCBufferTarget theTarget, int theOffset, ByteBuffer theData){
		if(theData != null)theData.rewind();
		_mySize = theData.limit();
		glBufferSubData(theTarget.glId, theOffset, theData);
	}
	
	public void copyDataFromTexture(CCShaderBuffer theShaderTexture, final int theID, final int theX, final int theY, final int theWidth, final int theHeight){
    	int myNewBufferSize = theWidth * theHeight * theShaderTexture.numberOfChannels() * 4;
		if(myNewBufferSize != _mySize){
	    	bind(CCBufferTarget.ARRAY);	
	    	bufferData(
	    		myNewBufferSize, 
	    		CCUsageFrequency.STREAM,
	    		CCUsageTYPE.COPY
	    	);
			unbind();
			_mySize = myNewBufferSize;
		}
    	
    	theShaderTexture.bindBuffer();
    	// bind buffer object to pixel pack buffer
    	bind(CCBufferTarget.PIXEL_PACK);	
    	glReadBuffer(GL_COLOR_ATTACHMENT0 + theID);
    	
    	// read from frame buffer to buffer object
    	glReadPixels(theX, theY, theWidth,theHeight, theShaderTexture.attachment(theID).format().glID, GL_FLOAT, 0);

    	unbind();
    	
    	theShaderTexture.unbindBuffer();
	}
	
	@Override
	protected void finalize() {
		glDeleteBuffers(_myBufferID);
	}
}
