package cc.creativecomputing.gl.data;

import cc.creativecomputing.gl.data.GLCombinedBuffer.GLBufferData;
import cc.creativecomputing.gl4.GLBuffer;
import cc.creativecomputing.gl4.GLDataType;
import cc.creativecomputing.gl4.GLDrawMode;
import cc.creativecomputing.gl4.GLException;
import cc.creativecomputing.gl4.GLShaderProgram;
import cc.creativecomputing.gl4.GLShaderProgram.GLAttributeInfo;
import cc.creativecomputing.gl4.GLVertexArray;

/**
 * 
 * @author christianr
 *
 */
public class GLMesh {
	private GLDrawMode _myDrawMode;
	
	private GLVertexArray _myVertexArray;
	
	private int _myNumberOfIndices = -1;
	private int _myNumberOfVertices;
	
	private GLBuffer _myElementBuffer = null;
	
	public GLMesh(GLDrawMode theDrawMode, GLCombinedBuffer theGLCombinedBuffer, GLShaderProgram theShader){
		_myDrawMode = theDrawMode;
		
		_myVertexArray = new GLVertexArray();
		_myVertexArray.bind();
		
		for(GLAttributeInfo myInfo:theShader.attributes()){
			GLBufferData myData = theGLCombinedBuffer.bufferDataMap().get(myInfo.name());
			
			if(myData == null)
				throw new GLException(
					"the given shader programm requires an attribute with the name: " + myInfo.name() + 
					" which is not provided by the given combined buffer."
				);
			
			myData.buffer().bind();
			_myVertexArray.attributes(myInfo.location(), myData.stride());
			_myVertexArray.enableVertexAttribArray(myInfo.location());
		}
		_myVertexArray.unbind();

		if(theGLCombinedBuffer.hasIndices()){
			_myNumberOfIndices = theGLCombinedBuffer.numberOfIndices();
			_myElementBuffer = theGLCombinedBuffer.elementBuffer();
		}
	}
	
	public GLMesh(GLDrawMode theDrawMode, GLCombinedBuffer theGLCombinedBuffer){
		
		_myDrawMode = theDrawMode;
		
		_myVertexArray = new GLVertexArray();
		_myVertexArray.bind();
		
		for(int i = 0; i < theGLCombinedBuffer.indexAccess().length;i++){
			GLBufferData myData = theGLCombinedBuffer.indexAccess()[i];
			if(myData == null)continue;
			
			myData.buffer().bind();
			_myVertexArray.attributes(i, myData.stride());
			_myVertexArray.enableVertexAttribArray(i);
		}
		
		if(theGLCombinedBuffer.hasIndices()){
			_myNumberOfIndices = theGLCombinedBuffer.numberOfIndices();
			theGLCombinedBuffer.elementBuffer().bind();
			_myElementBuffer = theGLCombinedBuffer.elementBuffer();
			_myVertexArray.unbind();
			theGLCombinedBuffer.elementBuffer().unbind();
		}else{
			_myNumberOfIndices = -1;
			_myVertexArray.unbind();
		}
	}
	
	public void draw() {
		_myVertexArray.bind();
		if(_myElementBuffer != null){
			_myElementBuffer.bind();
			_myVertexArray.drawElements(_myDrawMode, _myNumberOfIndices, GLDataType.UNSIGNED_INT);
			_myElementBuffer.unbind();
		}else{
			_myVertexArray.drawArrays(_myDrawMode, 0, _myNumberOfVertices);
		}
		_myVertexArray.unbind();
	}
}
