package cc.creativecomputing.gl4;

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
import static org.lwjgl.opengl.GL30.GL_MAP_FLUSH_EXPLICIT_BIT;
import static org.lwjgl.opengl.GL30.GL_MAP_INVALIDATE_BUFFER_BIT;
import static org.lwjgl.opengl.GL30.GL_MAP_INVALIDATE_RANGE_BIT;
import static org.lwjgl.opengl.GL30.GL_MAP_READ_BIT;
import static org.lwjgl.opengl.GL30.GL_MAP_UNSYNCHRONIZED_BIT;
import static org.lwjgl.opengl.GL30.GL_MAP_WRITE_BIT;
import static org.lwjgl.opengl.GL30.GL_TRANSFORM_FEEDBACK_BUFFER;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL30.glFlushMappedBufferRange;
import static org.lwjgl.opengl.GL30.glMapBufferRange;
import static org.lwjgl.opengl.GL31.GL_COPY_READ_BUFFER;
import static org.lwjgl.opengl.GL31.GL_COPY_WRITE_BUFFER;
import static org.lwjgl.opengl.GL31.GL_TEXTURE_BUFFER;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL31.glCopyBufferSubData;
import static org.lwjgl.opengl.GL40.GL_DRAW_INDIRECT_BUFFER;
import static org.lwjgl.opengl.GL42.GL_ATOMIC_COUNTER_BUFFER;
import static org.lwjgl.opengl.GL43.GL_DISPATCH_INDIRECT_BUFFER;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL43.glInvalidateBufferData;
import static org.lwjgl.opengl.GL43.glInvalidateBufferSubData;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.system.MemoryUtil;

/**
 * This class is bundeling opengls buffer functionality.
 * @author christianr
 *
 */
public class GLBuffer {
	
	/**
	 * Specifies the target buffer object. 
	 * @author christianr
	 *
	 */
	public enum GLBufferTarget{
		/**
		 * target that is used to set vertex array data pointers
		 * using vertex arrays. this is the target that you will 
		 * likely use most often
		 */
		ARRAY(GL_ARRAY_BUFFER),
		ATOMIC_COUNTER(GL_ATOMIC_COUNTER_BUFFER), 
		/**
		 * Used width COPY_WRITE to copy data between buffers
		 * without disturbing the OPENGLState or implying usage
		 * of any particular kind to OPENGL
		 */
		COPY_READ(GL_COPY_READ_BUFFER), 
		/**
		 * Used width COPY_READ to copy data between buffers
		 * without disturbing the OPENGLState or implying usage
		 * of any particular kind to OPENGL
		 */
		COPY_WRITE(GL_COPY_WRITE_BUFFER), 
		/**
		 * target used to store the parameters for drawing
		 */
		DRAW_INDIRECT(GL_DRAW_INDIRECT_BUFFER), 
		DISPATCH_INDIRECT(GL_DISPATCH_INDIRECT_BUFFER), 
		/**
		 * buffers bound to this target can contain vertex indices
		 * which are used by indexed draw commands.
		 */
		ELEMENT_ARRAY(GL_ELEMENT_ARRAY_BUFFER), 
		/**
		 * Used as the destination for OPENGL commands that read
		 * data from image objects such as textures or the framebuffer
		 */
		PIXEL_PACK(GL_PIXEL_PACK_BUFFER), 
		/**
		 * Opposite of PIXEL_PACK 
		 */
		PIXEL_UNPACK(GL_PIXEL_UNPACK_BUFFER), 
		SHADER_STORAGE(GL_SHADER_STORAGE_BUFFER), 
		/**
		 * Target to bind textures to buffers so that there data can 
		 * be directly read in shaders, this target allows manipulating
		 * this buffers, although they must still be bound to textures
		 * to make them accessible to shaders
		 */
		TEXTURE(GL_TEXTURE_BUFFER), 
		/**
		 * transform feedback is a facility in OPENGL whereby transformed
		 * vertices can be captured as they exit the vertex processing part
		 * of the pipeline (after the vertex or geometry shader if present)
		 * and some of there attributes written into buffer objects.
		 * This target provides a binding point for buffers that are used to 
		 * record this attributes.
		 */
		TRANSFORM_FEEDBACK(GL_TRANSFORM_FEEDBACK_BUFFER),
		/**
		 * Provides a binding point where 	buffers that will be used as uniform
		 * buffer objects maybe bound.
		 */
		UNIFORM(GL_UNIFORM_BUFFER);
		
		public int glID;
		
		GLBufferTarget(int theGLID){
			glID = theGLID;
		}
	}
	
	/**
	 * The frequency of gl data access
	 * @author christianr
	 *
	 */
	public enum GLDataAccesFrequency{
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
	 * The nature of gl data access
	 * @author christianr
	 *
	 */
	public enum GLDataAccesNature{
		/**
		 * The data store contents are modified by the application, and used as the source for GL drawing and image specification commands.
		 */
		DRAW,
		/**
		 * The data store contents are modified by reading data from the GL, and used to return that data when queried by the application.
		 */
		READ,
		/**
		 * The data store contents are modified by reading data from the GL, and used as the source for GL drawing and image specification commands.
		 */
		COPY,
		
		RAW
	}
	
	private int _myID;
	private long _mySize;
	private GLBufferTarget _myTarget;

	public GLBuffer(GLBufferTarget theTarget){
		_myID = glGenBuffers();
		_myTarget = theTarget;
		bind();
		unbind();
	}
	
	public int id(){
		return _myID;
	}
	
	public void target(GLBufferTarget theTarget){
		_myTarget = theTarget;
	}
	
	/**
	 * Bind the buffer to the given target
	 * @param theTarget
	 */
	public void bind(){
		glBindBuffer(_myTarget.glID, _myID);
	}
	
	public void unbind(){
		glBindBuffer(_myTarget.glID, 0);
	}
	
	public void allocate(
		long theBytes, 
		GLDataAccesFrequency theAccessFrequency, 
		GLDataAccesNature theAccessNature
	){
	    glBufferData(GL_ARRAY_BUFFER, theBytes, getAcessFrequencyNature(theAccessFrequency, theAccessNature));
	}
	
	public void allocate(long theBytes){
		allocate(theBytes, GLDataAccesFrequency.STATIC, GLDataAccesNature.DRAW);
	}
	
	@SuppressWarnings("incomplete-switch")
	private int getAcessFrequencyNature(GLDataAccesFrequency theAccessFrequency, GLDataAccesNature theAccessNature){
		switch(theAccessFrequency){
		case DYNAMIC:
			switch(theAccessNature){
			case COPY: return GL_DYNAMIC_COPY;
			case DRAW: return GL_DYNAMIC_DRAW;
			case READ: return GL_DYNAMIC_READ;
			}
		case STATIC:
			switch(theAccessNature){
			case COPY: return GL_STATIC_COPY;
			case DRAW: return GL_STATIC_DRAW;
			case READ: return GL_STATIC_READ;
			}
		case STREAM:
			switch(theAccessNature){
			case COPY: return GL_STREAM_COPY;
			case DRAW: return GL_STREAM_DRAW;
			case READ: return GL_STREAM_READ;
			}
		}
		return 0;
	}
	
	/**
	 * Creates and initializes a buffer object's data store. Creates a new data store for the buffer object currently bound to target. 
	 * Any pre-existing data store is deleted. The new data store is created with the specified size in bytes and usage. If data is not <code>null</code>, 
	 * the data store is initialized with data from this Buffer. In its initial state, the new data store is not mapped, it has a <code>null</code> mapped pointer, 
	 * and its mapped access is GL_READ_WRITE.
	 * 
	 * usage is a hint to the GL implementation as to how a buffer object's data store will be accessed. This enables the GL implementation to make more intelligent 
	 * decisions that may significantly impact buffer object performance. It does not, however, constrain the actual usage of the data store. usage can be broken down 
	 * into two parts: first, the frequency of access (modification and usage), and second, the nature of that access.
	 * @param theSize Specifies the size in bytes of the buffer object's new data store.
	 * @param theData Specifies a Buffer with data that will be copied into the data store for initialization, or null if no data is to be copied.
	 * @param theAccessFrequency The frequency of gl data access
	 * @param theAccessNature The nature of gl data access
	 */
	public void data(
		ByteBuffer theData, 
		GLDataAccesFrequency theAccessFrequency,
		GLDataAccesNature theAccessNature
	){
		glBufferData(_myTarget.glID, theData, getAcessFrequencyNature(theAccessFrequency, theAccessNature));
	}
	
	public void data(
		ShortBuffer theData, 
		GLDataAccesFrequency theAccessFrequency,
		GLDataAccesNature theAccessNature
	){
		glBufferData(_myTarget.glID, theData, getAcessFrequencyNature(theAccessFrequency, theAccessNature));
	}
	
	public void data(
		IntBuffer theData, 
		GLDataAccesFrequency theAccessFrequency,
		GLDataAccesNature theAccessNature
	){
		glBufferData(_myTarget.glID, theData, getAcessFrequencyNature(theAccessFrequency, theAccessNature));
	}
	
	public void data(
		LongBuffer theData, 
		GLDataAccesFrequency theAccessFrequency,
		GLDataAccesNature theAccessNature
	){
		glBufferData(_myTarget.glID, theData, getAcessFrequencyNature(theAccessFrequency, theAccessNature));
	}
	
	public void data(
		FloatBuffer theData, 
		GLDataAccesFrequency theAccessFrequency,
		GLDataAccesNature theAccessNature
	){
		glBufferData(_myTarget.glID, theData, getAcessFrequencyNature(theAccessFrequency, theAccessNature));
	}
	
	public void data(
		DoubleBuffer theData, 
		GLDataAccesFrequency theAccessFrequency,
		GLDataAccesNature theAccessNature
	){
		glBufferData(_myTarget.glID, theData, getAcessFrequencyNature(theAccessFrequency, theAccessNature));
	}
	
	public void data(
		byte[] theData, 
		GLDataAccesFrequency theAccessFrequency,
		GLDataAccesNature theAccessNature
	){
		glBufferData(_myTarget.glID, MemoryUtil.memAlloc(theData.length), getAcessFrequencyNature(theAccessFrequency, theAccessNature));
	}
	
	public void data(
		short[] theData, 
		GLDataAccesFrequency theAccessFrequency,
		GLDataAccesNature theAccessNature
	){
		glBufferData(_myTarget.glID, theData, getAcessFrequencyNature(theAccessFrequency, theAccessNature));
	}
	
	public void data(
		int[] theData, 
		GLDataAccesFrequency theAccessFrequency,
		GLDataAccesNature theAccessNature
	){
		glBufferData(_myTarget.glID, theData, getAcessFrequencyNature(theAccessFrequency, theAccessNature));
	}
	
	public void data(
		long[] theData, 
		GLDataAccesFrequency theAccessFrequency,
		GLDataAccesNature theAccessNature
	){
		glBufferData(_myTarget.glID, theData, getAcessFrequencyNature(theAccessFrequency, theAccessNature));
	}
	
	public void data(
		float[] theData, 
		GLDataAccesFrequency theAccessFrequency,
		GLDataAccesNature theAccessNature
	){
		glBufferData(_myTarget.glID, theData, getAcessFrequencyNature(theAccessFrequency, theAccessNature));
	}
	
	public void data(
		double[] theData, 
		GLDataAccesFrequency theAccessFrequency,
		GLDataAccesNature theAccessNature
	){
		glBufferData(_myTarget.glID, theData, getAcessFrequencyNature(theAccessFrequency, theAccessNature));
	}
	
	public void data(ByteBuffer theData){
		data(theData, GLDataAccesFrequency.STATIC, GLDataAccesNature.DRAW);
	}
	
	public void data(byte...theData){
		data(theData, GLDataAccesFrequency.STATIC, GLDataAccesNature.DRAW);
	}
	
	public void data(ShortBuffer theData){
		data(theData, GLDataAccesFrequency.STATIC, GLDataAccesNature.DRAW);
	}
	
	public void data(short...theData){
		data(theData, GLDataAccesFrequency.STATIC, GLDataAccesNature.DRAW);
	}
	
	public void data(IntBuffer theData){
		data(theData, GLDataAccesFrequency.STATIC, GLDataAccesNature.DRAW);
	}
	
	public void data(int...theData){
		data(theData, GLDataAccesFrequency.STATIC, GLDataAccesNature.DRAW);
	}
	
	public void data(LongBuffer theData){
		data(theData, GLDataAccesFrequency.STATIC, GLDataAccesNature.DRAW);
	}
	
	public void data(long...theData){
		data(theData, GLDataAccesFrequency.STATIC, GLDataAccesNature.DRAW);
	}
	
	public void data(FloatBuffer theData){
		data(theData, GLDataAccesFrequency.STATIC, GLDataAccesNature.DRAW);
	}
	
	public void data(float...theData){
		data(theData, GLDataAccesFrequency.STATIC, GLDataAccesNature.DRAW);
	}
	
	public void data(DoubleBuffer theData){
		data(theData, GLDataAccesFrequency.STATIC, GLDataAccesNature.DRAW);
	}
	
	public void data(double...theData){
		data(theData, GLDataAccesFrequency.STATIC, GLDataAccesNature.DRAW);
	}
	
	/**
	 * Redefines some or all of the data store for the buffer object. Data starting at byte 
	 * offset offset and extending for size bytes is copied to the data store from the memory pointed to by data. An 
	 * error is thrown if offset and size together define a range beyond the bounds of the buffer object's data store.
	 * @param theOffset Specifies the offset into the buffer object's data store where data replacement will begin, measured in bytes.
	 * @param theSize Specifies the size in bytes of the data store region being replaced
	 * @param theData Specifies a pointer to the new data that will be copied into the data store.
	 */
	public void subData(long theOffset, ByteBuffer theData){
		glBufferSubData(_myTarget.glID, theOffset, theData);
	}

	public void subData(long theOffset, ShortBuffer theData){
		glBufferSubData(_myTarget.glID, theOffset, theData);
	}

	public void subData(long theOffset, IntBuffer theData){
		glBufferSubData(_myTarget.glID, theOffset, theData);
	}

	public void subData(long theOffset, LongBuffer theData){
		glBufferSubData(_myTarget.glID, theOffset, theData);
	}

	public void subData(long theOffset, FloatBuffer theData){
		glBufferSubData(_myTarget.glID, theOffset, theData);
	}

	public void subData(long theOffset, DoubleBuffer theData){
		glBufferSubData(_myTarget.glID, theOffset, theData);
	}
	
	public void subData(long theOffset, float[] theData){
		subData(theOffset, MemoryUtil.memAllocFloat(theData.length).put(theData));
	}
	
	/**
	 * 
	 * @param theOffset
	 * @param theSize
	 * @return
	 */
	public ByteBuffer getData(int theOffset, long theSize){
		bind();
		ByteBuffer myResult = ByteBuffer.allocate((int)theSize);
		glGetBufferSubData(_myTarget.glID, theOffset, myResult);
		unbind();
		
		return myResult;
	}
	
	public ByteBuffer getData(){
		return getData(0, _mySize);
	}
	
	/**
	 * TODO add clear methods
	 */
	public void clear(float theValue0, float theValue1, float theValue2, float theValue3){
		bind();
//		glClearBufferData(_myTarget.glID, arg1, arg2, arg3, arg4);
	}
	
	/**
	 * 
	 * @param theSource
	 * @param theSourceID
	 * @param theDestinationID
	 * @param theReadOffset
	 * @param theWriteOffset
	 * @param theSize
	 */
	public void copy(
		GLBuffer theSource, 
		int theReadOffset,
		int theWriteOffset,
		int theSize
	){
		glBindBuffer(GLBufferTarget.COPY_READ.glID, theSource._myID);
		glBindBuffer(GLBufferTarget.COPY_WRITE.glID, _myID);
		
		glCopyBufferSubData(
			GLBufferTarget.COPY_READ.glID, 
			GLBufferTarget.COPY_WRITE.glID, 
			theReadOffset, 
			theWriteOffset, 
			theSize
		);
		
		glBindBuffer(GLBufferTarget.COPY_READ.glID, 0);
		glBindBuffer(GLBufferTarget.COPY_WRITE.glID, 0);
	}
	
	/**
	 * maps to the client's address space the entire data store of this buffer. 
	 * The data can then be directly read and/or written relative to the returned Buffer, 
	 * depending on the specified access policy. If the GL is unable to map the buffer 
	 * object's data store, map generates an error and returns <code>null</code>. 
	 * This may occur for system-specific reasons, such as low virtual memory availability. 
	 * If no error occurs, the returned pointer will have an alignment of at least 
	 * GL_MIN_MAP_BUFFER_ALIGNMENT basic machine units. The value of GL_MIN_MAP_BUFFER_ALIGNMENT 
	 * can be retrieved by calling glGet with pname set to GL_MIN_MAP_BUFFER_ALIGNMENT and must 
	 * be a power of two that is at least 64.
	 * <p>
	 * If a mapped data store is accessed in a way inconsistent with the specified access 
	 * policy, no error is generated, but performance may be negatively impacted and system 
	 * errors, including program termination, may result. Unlike the usage parameter of 
	 * data, access is not a hint, and does in fact constrain the usage of the mapped data 
	 * store on some GL implementations. In order to achieve the highest performance available, 
	 * a buffer object's data store should be used in ways consistent with both its specified 
	 * usage and access parameters.
	 * <p>
	 * A mapped data store must be unmapped with glUnmapBuffer before its buffer object is 
	 * used. Otherwise an error will be generated by any GL command that attempts to 
	 * dereference the buffer object's data store. When a data store is unmapped, the pointer 
	 * to its data store becomes invalid. unmap returns <code>true</code> unless the data store 
	 * contents have become corrupt during the time the data store was mapped. This can occur 
	 * for system-specific reasons that affect the availability of graphics memory, such as 
	 * screen mode changes. In such situations, <code>false</code> is returned and the data 
	 * store contents are undefined. An application must detect this rare condition and reinitialize 
	 * the data store.
	 * <p>
	 * A buffer object's mapped data store is automatically unmapped when the buffer object is 
	 * deleted or its data store is recreated with data	.
	 * @param theAccesMode
	 * @return
	 */
	public ByteBuffer map(GLAccesMode theAccesMode){
		bind();
		ByteBuffer myResult = glMapBuffer(_myTarget.glID, theAccesMode.glID());
//		unbind();
		return myResult;
	}
	
	/**
	 * indicates that the returned pointer may be used to read buffer object data.
	 * No GL error is generated if the pointer is used to query a mapping which excludes this flag, 
	 * but the result is undefined and system errors (possibly including program termination) may occur.
	 */
	public static final int MAP_READ_BIT = GL_MAP_READ_BIT;
	
	/**
	 * indicates that the returned pointer may be used to modify buffer object data. 
	 * No GL error is generated if the pointer is used to modify a mapping which excludes this flag, 
	 * but the result is undefined and system errors (possibly including program termination) may occur.
	 */
	public static final int MAP_WRITE_BIT = GL_MAP_WRITE_BIT;
	
	/**
	 * indicates that the previous contents of the specified range may 
	 * be discarded. Data within this range are undefined with the exception of subsequently written data. 
	 * No GL error is generated if sub- sequent GL operations access unwritten data, but the result is 
	 * undefined and system errors (possibly including program termination) may occur. This flag may 
	 * not be used in combination with MAP_READ_BIT.
	 */
	public static final int MAP_INVALIDATE_RANGE_BIT = GL_MAP_INVALIDATE_RANGE_BIT;
	
	/**
	 * indicates that the previous contents of the entire buffer may 
	 * be discarded. Data within the entire buffer are undefined with the exception of subsequently 
	 * written data. No GL error is generated if subsequent GL operations access unwritten data, but 
	 * the result is undefined and system errors (possibly including program termination) may occur. 
	 * This flag may not be used in combination with MAP_READ_BIT.
	 */
	public static final int MAP_INVALIDATE_BUFFER_BIT = GL_MAP_INVALIDATE_BUFFER_BIT;
	
	/**
	 * indicates that one or more discrete subranges of the mapping may 
	 * be modified. When this flag is set, modifications to each subrange must be explicitly flushed by 
	 * calling glFlushMappedBufferRange. No GL error is set if a subrange of the mapping is modified 
	 * and not flushed, but data within the corresponding subrange of the buffer are undefined. This 
	 * flag may only be used in conjunction with MAP_WRITE_BIT. When this option is selected, 
	 * flushing is strictly limited to regions that are explicitly indicated with calls to 
	 * glFlushMappedBufferRange prior to unmap; if this option is not selected glUnmapBuffer will 
	 * automatically flush the entire mapped range when called.
	 */
	public static final int MAP_FLUSH_EXPLICIT_BIT = GL_MAP_FLUSH_EXPLICIT_BIT;
	
	/**
	 * indicates that the GL should not attempt to synchronize pending 
	 * operations on the buffer prior to returning from glMapBufferRange. No GL error is generated if 
	 * pending operations which source or modify the buffer overlap the mapped region, but the result of 
	 * such previous and any subsequent operations is undefined.
	 */
	public static final int MAP_UNSYNCHRONIZED_BIT = GL_MAP_UNSYNCHRONIZED_BIT;
	
	/**
	 * maps all or part of the data store of a buffer object into the client's address space. 
	 * offset and length indicate the range of data in the buffer object that is to be mapped, 
	 * in terms of basic machine units. access is a bitfield containing flags which describe the 
	 * requested mapping. These flags are described below.
	 * <p>
	 * If no error occurs, a pointer to the beginning of the mapped range is returned once all 
	 * pending operations on that buffer have completed, and may be used to modify and/or query
	 * the corresponding range of the buffer, according to the following flag bits set in access:
	 * <ul>
	 * <li> {@linkplain #MAP_READ_BIT}
	 * <li> {@linkplain #MAP_WRITE_BIT}
	 * </ul>
	 * Furthermore, the following optional flag bits in access may be used to modify the mapping:
	 * <ul>
	 * <li>{@linkplain #MAP_INVALIDATE_RANGE_BIT}</li>
	 * <li>{@linkplain #MAP_INVALIDATE_BUFFER_BIT}</li>
	 * <li>{@linkplain #MAP_FLUSH_EXPLICIT_BIT}</li>
	 * <li>{@linkplain #MAP_UNSYNCHRONIZED_BIT}</li>
	 * </ul>
	 * <p>
	 * If an error occurs, glMapBufferRange returns a NULL pointer. If no error occurs, the returned pointer will 
	 * reflect an alignment of at least GL_MIN_MAP_BUFFER_ALIGNMENT basic machine units. The value of 
	 * GL_MIN_MAP_BUFFER_ALIGNMENT can be retrieved by calling glGet with pname set to GL_MIN_MAP_BUFFER_ALIGNMENT 
	 * and must be a power of two that is at least 64. Subtracting offset from this returned pointed will always 
	 * produce a multiple of GL_MIN_MAP_BUFFER_ALINMENT.
	 */
	public ByteBuffer mapRange(int theOffset, int theLength, int theAccess){
		bind();
		ByteBuffer myResult = glMapBufferRange(_myTarget.glID, theOffset, theLength, theAccess);
//		unbind();
		return myResult;
	}
	
	/**
	 * A mapped data store must be unmapped with unmap before its buffer object is used. Otherwise an error will 
	 * be generated by any GL command that attempts to dereference the buffer object's data store. When a data store 
	 * is unmapped, the pointer to its data store becomes invalid. unmap returns <code>true</code> unless the data 
	 * store contents have become corrupt during the time the data store was mapped. This can occur for system-specific 
	 * reasons that affect the availability of graphics memory, such as screen mode changes. In such situations,
	 * <code>false</code> is returned and the data store contents are undefined. An application must detect this rare 
	 * condition and reinitialize the data store.
	 * <p>
	 * A buffer object's mapped data store is automatically unmapped when the buffer object is deleted or its data store 
	 * is recreated with data.
	 * @return
	 */
	public boolean unmap(){
		bind();
		boolean myResult = glUnmapBuffer(_myTarget.glID);
		unbind();	
		return myResult;
	}
	
	/**
	 * Indicates that modifications have been made to a range of a mapped buffer. The buffer must previously 
	 * have been mapped with {@linkplain #MAP_FLUSH_EXPLICIT_BIT} flag. offset and length indicate the modified subrange of the mapping,
	 * in basic units. The specified subrange to flush is relative to the start of the currently mapped range of the buffer. 
	 * flushMappedRange may be called multiple times to indicate distinct subranges of the mapping which require flushing.
	 * @param theOffset Specifies the start of the buffer subrange, in basic machine units.
	 * @param theLength Specifies the length of the buffer subrange, in basic machine units.
	 */
	public void flushMappedRange(int theOffset, int theLength){
		bind();
		glFlushMappedBufferRange(_myTarget.glID, theOffset, theLength);
	}
	
	// TODO add documentation
	public void invalidateData(){
		glInvalidateBufferData(_myID);
	}
	
	// TODO add documentation
	public void invalidateSubData(int theOffset, int theLength){
		glInvalidateBufferSubData(_myID, theOffset, theLength);
	}
	
	/**
	 * Binds the buffer object buffer to the binding point at index index of the array of targets specified by target. 
	 * Each target represents an indexed array of buffer binding points, as well as a single general binding point that 
	 * can be used by other buffer manipulation functions such as glBindBuffer or glMapBuffer. In addition to binding 
	 * buffer to the indexed buffer binding target, glBindBufferBase also binds buffer to the generic buffer binding
	 * point specified by target.
	 * @param theTarget
	 * @param theIndex
	 */
	public void bindBufferBase(GLBufferTarget theTarget, int theIndex){
		switch(theTarget){
		case ATOMIC_COUNTER:
		case TRANSFORM_FEEDBACK:
		case UNIFORM:
		case SHADER_STORAGE:
			break;
		default:
			throw new GLException("Unsupported target for bindBufferBase");
		}
		bind();
		glBindBufferBase(theTarget.glID, theIndex, _myID);
		unbind();
	}
	
	@Override
	protected void finalize() {
		glDeleteBuffers(_myID);
	}
}
