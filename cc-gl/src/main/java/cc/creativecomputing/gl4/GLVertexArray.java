package cc.creativecomputing.gl4;

import java.nio.Buffer;
import java.nio.IntBuffer;

import cc.creativecomputing.gl4.GLBuffer.GLBufferTarget;

import com.jogamp.common.nio.PointerBuffer;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;

public class GLVertexArray {

	private IntBuffer _myVertexArray;

	public GLVertexArray() {
		_myVertexArray = IntBuffer.allocate(1);
		GL4 gl = GLGraphics.currentGL();
		gl.glGenVertexArrays(1, _myVertexArray);
	}

	/**
	 * binds the vertex array object.
	 */
	public void bind() {
		GL4 gl = GLGraphics.currentGL();
		gl.glBindVertexArray(_myVertexArray.get(0));
	}

	public void unbind() {
		GL4 gl = GLGraphics.currentGL();
		gl.glBindVertexArray(0);
	}
	

	
	/**
	 * Enables the generic vertex attribute array specified by index.   {@linkplain #disableVertexAttribArray(int)} 
	 * disables the generic vertex attribute array specified by index. By default, all client-side capabilities are 
	 * disabled, including all generic vertex attribute arrays. If enabled, the values in the generic vertex attribute 
	 * array will be accessed and used for rendering when calls are made to vertex array commands such as 
	 * {@linkplain GLVertexArray#drawArrays(GLDrawMode, int, int)}, {@linkplain GLVertexArray#drawElements(GLDrawMode, int, GLType, int)}.
	 * @param theIndex
	 */
	public void enableVertexAttribArray(int theIndex){
		GLGraphics.currentGL().glEnableVertexAttribArray(theIndex);
	}
	
	/**
	 * @see #enableVertexAttribArray(int)
	 * @param theIndex
	 */
	public void disableVertexAttribArray(int theIndex){
		GLGraphics.currentGL().glDisableVertexAttribArray(theIndex);
	}

	/**
	 * Specify the location and data format of the array of generic vertex
	 * attributes at index index to use when rendering. size specifies the
	 * number of components per attribute and must be 1, 2, 3, 4. type specifies
	 * the data type of each component, and stride specifies the byte stride
	 * from one attribute to the next, allowing vertices and attributes to be
	 * packed into a single array or stored in separate arrays.
	 * <p>
	 * For <code>attributes</code>, if normalized is set to <code>true</code>,
	 * it indicates that values stored in an integer format are to be mapped to
	 * the range [-1,1] (for signed values) or [0,1] (for unsigned values) when
	 * they are accessed and converted to floating point. Otherwise, values will
	 * be converted to floats directly without normalization.
	 * <p>
	 * If pointer is not <code>null</code>, a non-zero named buffer object must
	 * be bound to the {@linkplain GLBufferTarget#ARRAY} target, otherwise an
	 * error is generated. pointer is treated as a byte offset into the buffer
	 * object's data store. The buffer object binding (GL_ARRAY_BUFFER_BINDING)
	 * is saved as generic vertex attribute array state
	 * (GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING) for index index.
	 * <p>
	 * When a generic vertex attribute array is specified, size, type,
	 * normalized, stride, and pointer are saved as vertex array state, in
	 * addition to the current vertex array buffer object binding.
	 * <p>
	 * To enable and disable a generic vertex attribute array, call
	 * {@linkplain #enable()} and {@linkplain #disable()} with index. If
	 * enabled, the generic vertex attribute array is used when glDrawArrays,
	 * glMultiDrawArrays, glDrawElements, glMultiDrawElements, or
	 * glDrawRangeElements is called.
	 * 
	 * @param theIndex
	 *            Specifies the index of the generic vertex attribute to be
	 *            modified.
	 * @param theSize
	 *            Specifies the number of components per generic vertex
	 *            attribute. Must be 1, 2, 3, 4. The initial value is 4.
	 * @param theType
	 *            Specifies the data type of each component in the array.
	 * @param theIsNormalized
	 *            specifies whether fixed-point data values should be normalized
	 *            <code>true</code> or converted directly as fixed-point values
	 *            <code>false</code> when they are accessed.
	 * @param theStride
	 *            Specifies the byte offset between consecutive generic vertex
	 *            attributes. If stride is 0, the generic vertex attributes are
	 *            understood to be tightly packed in the array. The initial
	 *            value is 0.
	 */
	public void attributes(int theIndex, int theSize, GLDataType theType, boolean theIsNormalized, int theStride) {
		GL4 gl = GLGraphics.currentGL();
		gl.glVertexAttribPointer(theIndex, theSize, theType.glID(), theIsNormalized, theStride, 0);
	}

	public void attributes(int theIndex, int theSize, GLDataType theType, boolean theIsNormalized, int theStride, long theOffset) {
		GL4 gl = GLGraphics.currentGL();
		gl.glVertexAttribPointer(theIndex, theSize, theType.glID(), theIsNormalized, theStride, theOffset);
	}

	public void attributes(int theIndex, int theSize, int theStride, long theOffset) {
		GL4 gl = GLGraphics.currentGL();
		gl.glVertexAttribPointer(theIndex, theSize, GLDataType.FLOAT.glID(), false, theStride, theOffset);
	}

	/**
	 * 
	 * @param theIndex
	 * @param theSize
	 */
	public void attributes(int theIndex, int theSize) {
		GL4 gl = GLGraphics.currentGL();
		gl.glVertexAttribPointer(theIndex, theSize, GLDataType.FLOAT.glID(), false, 0, 0);
	}

	/**
	 * For <code>attributesI</code>, only the integer types
	 * {@linkplain GLDataType#BYTE}, {@linkplain GLDataType#UNSIGNED_BYTE},
	 * {@linkplain GLDataType#SHORT}, {@linkplain GLDataType#UNSIGNED_SHORT},
	 * {@linkplain GLDataType#INT}, {@linkplain GLDataType#UNSIGNED_INT} are accepted.
	 * Values are always left as integer values.
	 * 
	 * @see #attributes(int, int, GLDataType, boolean, int, Buffer)
	 * @param theIndex
	 *            Specifies the index of the generic vertex attribute to be
	 *            modified.
	 * @param theSize
	 *            Specifies the number of components per generic vertex
	 *            attribute. Must be 1, 2, 3, 4. The initial value is 4.
	 * @param theType Specifies the data type of each component in the array.must be one
	 * 				of {@linkplain GLDataType#BYTE}, {@linkplain GLDataType#UNSIGNED_BYTE},
	 * 			{@linkplain GLDataType#SHORT}, {@linkplain GLDataType#UNSIGNED_SHORT},
	 * 			{@linkplain GLDataType#INT}, {@linkplain GLDataType#UNSIGNED_INT}
	 * @param theStride
	 * @param theBuffer
	 */
	public void attributesI(int theIndex, int theSize, GLDataType theType, int theStride) {
		GL4 gl = GLGraphics.currentGL();
		gl.glVertexAttribIPointer(theIndex, theSize, theType.glID(), theStride, 0);
	}

	/**
	 * specifies state for a generic vertex attribute array associated with a
	 * shader attribute variable declared with 64-bit double precision
	 * components. type must be {@linkplain GLDataType#DOUBLE}. index, size, and
	 * stride behave as described for {@linkplain #attributes} and
	 * {@linkplain #attributesI}.
	 * 
	 * @see #attributesI(int, int, GLDataType, int, Buffer)
	 * @param theIndex
	 *            Specifies the index of the generic vertex attribute to be
	 *            modified.
	 * @param theSize
	 *            Specifies the number of components per generic vertex
	 *            attribute. Must be 1, 2, 3, 4. The initial value is 4.
	 * @param theType
	 *            Specifies the data type of each component in the array. 
	 * @param theStride
	 */
	public void attributesL(int theIndex, int theSize, GLDataType theType, int theStride, Buffer theBuffer) {
		GL4 gl = GLGraphics.currentGL();
		gl.glVertexAttribLPointer(theIndex, theSize, theType.glID(), theStride, 0);
	}

	public void attribute1f(int theIndex, float theV0) {
		GLGraphics.currentGL().glVertexAttrib1f(theIndex, theV0);
	}

	public void attribute2f(int theIndex, float theV0, float theV1) {
		GLGraphics.currentGL().glVertexAttrib2f(theIndex, theV0, theV1);
	}

	public void attribute3f(int theIndex, float theV0, float theV1, float theV2) {
		GLGraphics.currentGL().glVertexAttrib3f(theIndex, theV0, theV1, theV2);
	}

	public void attribute4f(int theIndex, float theV0, float theV1, float theV2, float theV3) {
		GLGraphics.currentGL().glVertexAttrib4f(theIndex, theV0, theV1, theV2, theV3);
	}

	public void attribute1i(int theIndex, int theV0) {
		GLGraphics.currentGL().glVertexAttribI1i(theIndex, theV0);
	}

	public void attribute2i(int theIndex, int theV0, int theV1) {
		GLGraphics.currentGL().glVertexAttribI2i(theIndex, theV0, theV1);
	}

	public void attribute3i(int theIndex, int theV0, int theV1, int theV2) {
		GLGraphics.currentGL().glVertexAttribI3i(theIndex, theV0, theV1, theV2);
	}

	public void attribute4i(int theIndex, int theV0, int theV1, int theV2, int theV3) {
		GLGraphics.currentGL().glVertexAttribI4i(theIndex, theV0, theV1, theV2, theV3);
	}

	public void attribute1d(int theIndex, double theV0) {
		GLGraphics.currentGL().glVertexAttribL1d(theIndex, theV0);
	}

	public void attribute2d(int theIndex, double theV0, double theV1) {
		GLGraphics.currentGL().glVertexAttribL2d(theIndex, theV0, theV1);
	}

	public void attribute3d(int theIndex, double theV0, double theV1, double theV2) {
		GLGraphics.currentGL().glVertexAttribL3d(theIndex, theV0, theV1, theV2);
	}

	public void attribute4d(int theIndex, double theV0, double theV1, double theV2, double theV3) {
		GLGraphics.currentGL().glVertexAttribL4d(theIndex, theV0, theV1, theV2, theV3);
	}

	/**
	 * Modifies the rate at which generic vertex attributes advance when
	 * rendering multiple instances of primitives in a single draw call. If
	 * @param theDivisor is zero, the attribute at slot index advances once per vertex. 
	 * If @param theDivisor is non-zero, the attribute advances once per divisor instances of
	 * the set(s) of vertices being rendered. An attribute is referred to as
	 * instanced if its GL_VERTEX_ATTRIB_ARRAY_DIVISOR value is non-zero.
	 * 
	 * @param theIndex must be less than the value of GL_MAX_VERTEX_ATTRIBS.
	 *  
	 * {@link http://www.opengl.org/sdk/docs/man/xhtml/glVertexAttribDivisor.xml}
	 */

	public void attributeDivisor(int theIndex, int theDivisor) {
		GL4 gl = GLGraphics.currentGL();
		gl.glVertexAttribDivisor(theIndex, theDivisor);
	}

	/**
	 * Enables the generic vertex attribute array specified by index
	 * 
	 * @param theIndex
	 *            Specifies the index of the generic vertex attribute to be
	 *            enabled.
	 */
	public void enable(int theIndex) {
		GL4 gl = GLGraphics.currentGL();
		gl.glEnableVertexAttribArray(theIndex);
	}

	/**
	 * Disables the generic vertex attribute array specified by index
	 * 
	 * @param theIndex
	 *            Specifies the index of the generic vertex attribute to be
	 *            enabled.
	 */
	public void disable(int theIndex) {
		GL4 gl = GLGraphics.currentGL();
		gl.glDisableVertexAttribArray(theIndex);
	}

	/**
	 * Specifies multiple geometric primitives with very few subroutine calls.
	 * Instead of calling a GL procedure to pass each individual vertex, normal,
	 * texture coordinate, edge flag, or color, you can prespecify separate
	 * arrays of vertices, normals, and colors and use them to construct a
	 * sequence of primitives with a single call to <code>drawArrays()</code>.
	 * <p>
	 * When <code>drawArrays()</code> is called, it uses count sequential
	 * elements from each enabled array to construct a sequence of geometric
	 * primitives, beginning with element first. mode specifies what kind of
	 * primitives are constructed and how the array elements construct those
	 * primitives.
	 * <p>
	 * Vertex attributes that are modified by <code>drawArrays()</code> have an
	 * unspecified value after <code>drawArrays()</code> returns. Attributes
	 * that aren't modified remain well defined.
	 * 
	 * @param theDrawMode Specifies what kind of primitives to render.
	 * @param theFirst Specifies the starting index in the enabled arrays.
	 * @param theCount Specifies the number of elements to be rendered.
	 */
	public void drawArrays(GLDrawMode theDrawMode, int theFirst, int theCount) {
		GL4 gl = GLGraphics.currentGL();
		gl.glDrawArrays(theDrawMode.glID(), theFirst, theCount);
	}
	
	// TODO add documentation
	public void drawArraysInstanced(GLDrawMode theDrawMode, int theFirst, int theCount, int thePrimitivCount){
		GLGraphics.currentGL().glDrawArraysInstanced(GL.GL_TRIANGLES, theFirst, theCount, thePrimitivCount);
	}

	/**
	 * Specifies multiple geometric primitives with very few subroutine calls.
	 * Instead of calling a GL function to pass each individual vertex, normal,
	 * texture coordinate, edge flag, or color, you can prespecify separate
	 * arrays of vertices, normals, and so on, and use them to construct a
	 * sequence of primitives with a single call to <code>drawElements()</code>.
	 * <p>
	 * When <code>drawElements()</code> is called, it uses count sequential
	 * elements from an enabled array, starting at indices to construct a
	 * sequence of geometric primitives. mode specifies what kind of primitives
	 * are constructed and how the array elements construct these primitives. If
	 * more than one array is enabled, each is used.
	 * <p>
	 * Vertex attributes that are modified by glDrawElements have an unspecified
	 * value after <code>drawElements()</code> returns. Attributes that aren't
	 * modified maintain their previous values.
	 * 
	 * @param theDrawMode
	 *            Specifies what kind of primitives to render.
	 * @param theCount
	 *            Specifies the number of elements to be rendered.
	 * @param theType
	 *            Specifies the type of the values in indices. Must be one of
	 *            {@linkplain GLDataType#UNSIGNED_BYTE},
	 *            {@linkplain GLDataType#UNSIGNED_SHORT} or
	 *            {@linkplain GLDataType#UNSIGNED_INT}.
	 * @param theIndices
	 *            Specifies a pointer to the location where the indices are
	 *            stored.
	 * 
	 */
	public void drawElements(GLDrawMode theDrawMode, int theCount, GLDataType theType) {
		GLGraphics.currentGL().glDrawElements(theDrawMode.glID(), theCount, theType.glID(), 0);
	}

	/**
	 * behaves identically to {@linkplain #drawElements()} except that the ith
	 * element transferred by the corresponding draw call will be taken from
	 * element indices[i] + basevertex of each enabled array. If the resulting
	 * value is larger than the maximum value representable by type, it is as if
	 * the calculation were upconverted to 32-bit unsigned integers (with
	 * wrapping on overflow conditions). The operation is undefined if the sum
	 * would be negative.
	 * 
	 * @param theDrawMode
	 *            Specifies what kind of primitives to render.
	 * @param theCount
	 *            Specifies the number of elements to be rendered.
	 * @param theType
	 *            Specifies the type of the values in indices. Must be one of
	 * @param theIndices
	 *            Specifies a pointer to the location where the indices are
	 *            stored.
	 * @param theBaseVertex
	 *            Specifies a constant that should be added to each element of
	 *            indices when chosing elements from the enabled vertex arrays.
	 */
	public void drawElements(GLDrawMode theDrawMode, int theCount, GLDataType theType, int theBaseVertex) {
		GLGraphics.currentGL().glDrawElementsBaseVertex(theDrawMode.glID(), theCount, theType.glID(), 0, theBaseVertex);
	}

	/**
	 * Specifies multiple sets of geometric primitives with very few subroutine
	 * calls. Instead of calling a GL procedure to pass each individual vertex,
	 * normal, texture coordinate, edge flag, or color, you can prespecify
	 * separate arrays of vertices, normals, and colors and use them to
	 * construct a sequence of primitives with a single call to
	 * <code>drawArrays()</code>.
	 * <p>
	 * This method behaves identically to
	 * {@linkplain #drawArrays(GLDrawMode, int, int)} except that
	 * thePrimitiveCount separate ranges of elements are specified instead.
	 * <p>
	 * When glMultiDrawArrays is called, it uses count sequential elements from
	 * each enabled array to construct a sequence of geometric primitives,
	 * beginning with element first. mode specifies what kind of primitives are
	 * constructed, and how the array elements construct those primitives.
	 * <p>
	 * Vertex attributes that are modified by <code>multiDrawArrays</code> have
	 * an unspecified value after <code>multiDrawArrays</code> returns.
	 * Attributes that aren't modified remain well defined.
	 * 
	 * @param theDrawMode
	 *            Specifies what kind of primitives to render.
	 * @param theFirst
	 *            Points to an array of starting indices in the enabled arrays.
	 * @param theCount
	 *            Points to an array of the number of indices to be rendered.
	 * @param thePrimitiveCount
	 *            Specifies the size of the first and count
	 */
	public void multiDrawArrays(GLDrawMode theDrawMode, IntBuffer theFirst, IntBuffer theCount, int thePrimitiveCount) {
		GL4 gl = GLGraphics.currentGL();
		gl.glMultiDrawArrays(theDrawMode.glID(), theFirst, theCount, thePrimitiveCount);
	}

	/**
	 * Specifies multiple sets of geometric primitives with very few subroutine
	 * calls. Instead of calling a GL function to pass each individual vertex,
	 * normal, texture coordinate, edge flag, or color, you can prespecify
	 * separate arrays of vertices, normals, and so on, and use them to
	 * construct a sequence of primitives with a single call to
	 * <code>multiDrawElements</code>.
	 * <p>
	 * <code>multiDrawElements</code> is identical in operation to
	 * {@linkplain #drawElements(GLDrawMode, int, GLDataType, Buffer)} except that
	 * primcount separate lists of elements are specified.
	 * <p>
	 * Vertex attributes that are modified by <code>multiDrawElements</code>
	 * have an unspecified value after <code>multiDrawElements</code> returns.
	 * Attributes that aren't modified maintain their previous values.
	 * 
	 * @param theDrawMode
	 *            Specifies what kind of primitives to render.
	 * @param theCount
	 *            Points to an array of the elements counts.
	 * @param theType
	 *            Specifies the type of the values in indices. Must be one of
	 *            {@linkplain GLDataType#UNSIGNED_BYTE},
	 *            {@linkplain GLDataType#UNSIGNED_SHORT} or
	 *            {@linkplain GLDataType#UNSIGNED_INT}.
	 * @param theIndices
	 *            Specifies a pointer to the location where the indices are
	 *            stored.
	 * @param thePrimitiveCount
	 *            Specifies the size of the count array.
	 */
	public void multiDrawElements(GLDrawMode theDrawMode, IntBuffer theCount, GLDataType theType, PointerBuffer theIndices, int thePrimitiveCount) {
		GL4 gl = GLGraphics.currentGL();
		gl.glMultiDrawElements(theDrawMode.glID(), theCount, theType.glID(), theIndices, thePrimitiveCount);
	}

	@Override
	protected void finalize() {
		GL4 gl = GLGraphics.currentGL();
		gl.glDeleteVertexArrays(1, _myVertexArray);
	}

}
