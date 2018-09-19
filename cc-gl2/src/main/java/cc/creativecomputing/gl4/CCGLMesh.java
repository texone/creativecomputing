package cc.creativecomputing.gl4;

import java.util.Optional;

import cc.creativecomputing.gl4.GLBuffer.GLBufferTarget;

public class CCGLMesh {

	private GLVertexArray VAO;
	private Optional<GLBuffer> EBO = Optional.empty();
	private GLBuffer[] VBO;
	
	private int _myNumberOfIndices = 0;
	private int _myNumberOfValues = 0;
	private GLDrawMode _myDrawMode = GLDrawMode.TRIANGLES;
	
	public CCGLMesh() {
		VAO = new GLVertexArray();
		VBO = new GLBuffer[GLVertexArray.maxVertexAttribs()];
	}
	
	/**
	 * 
	 * @param theIndex
	 *            Specifies the index of the generic vertex attribute to be
	 *            modified.
	 * @param theSize
	 *            Specifies the number of components per generic vertex
	 *            attribute. Must be 1, 2, 3, 4. The initial value is 4.
	 * @param theValues
	 */
	public void data(int theIndex, int theSize, float...theValues) {
		_myNumberOfValues = theValues.length / theSize;
		if(VBO[theIndex] == null) {
			VBO[theIndex] = new GLBuffer(GLBufferTarget.ARRAY);
		}
		VAO.bind();
		
		VBO[theIndex].bind();
		VBO[theIndex].data(theValues);

	    VAO.attributes(theIndex, theSize, GLDataType.FLOAT, false, 0, 0);
	    VAO.enableVertexAttribArray(theIndex);
	    // note that this is allowed, the call to glVertexAttribPointer 
	    // registered VBO as the vertex attribute's bound vertex buffer 
	    // object so afterwards we can safely unbind
	    VBO[theIndex].unbind(); 
	    VAO.unbind();
	}
	
	public void indices(int...theIndices) {
		_myNumberOfIndices = theIndices.length;
		
		if(!EBO.isPresent()) {
			EBO = Optional.of(new GLBuffer(GLBufferTarget.ELEMENT_ARRAY));
		}
		VAO.bind();
	    EBO.get().bind();
	    EBO.get().data(theIndices);

	    // remember: do NOT unbind the EBO while a VAO is active as the bound element buffer object IS stored in the VAO; keep the EBO bound.
	    //glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

	    // You can unbind the VAO afterwards so other VAO calls won't accidentally modify this VAO, but this rarely happens. Modifying other
	    // VAOs requires a call to glBindVertexArray anyways so we generally don't unbind VAOs (nor VBOs) when it's not directly necessary.
	    VAO.unbind();

	    EBO.get().unbind();
	}
	
	public void draw() {
		VAO.bind();
		if(EBO.isPresent()) {
			VAO.drawElements(_myDrawMode, _myNumberOfIndices, GLDataType.UNSIGNED_INT);
		}else {
			VAO.drawArrays(_myDrawMode, 0, _myNumberOfValues);
		}
		VAO.unbind();
	}
	
}
