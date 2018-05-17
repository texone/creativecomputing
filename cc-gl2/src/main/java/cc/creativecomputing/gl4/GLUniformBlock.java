package cc.creativecomputing.gl4;

import static org.lwjgl.opengl.GL31.GL_UNIFORM_BLOCK_ACTIVE_UNIFORMS;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BLOCK_DATA_SIZE;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_OFFSET;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_SIZE;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_TYPE;
import static org.lwjgl.opengl.GL31.glGetActiveUniformBlockiv;
import static org.lwjgl.opengl.GL31.glGetUniformIndices;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.gl4.GLBuffer.GLBufferTarget;

public class GLUniformBlock {
	
	private static class GLUniformBlockVariable{
		private int _myIndex;
		private int _myOffset;
		private int _mySize;
		private int _myType;
		
		private GLUniformBlockVariable(int theIndex, int theOffset, int theSize, int theType){
			_myIndex = theIndex;
			_myOffset = theOffset;
			_mySize = theSize;
			_myType = theType;
		}
	}

	private int _myID;
	private GLShaderProgram _myProgram;
	private GLBuffer _myUniformBuffer;
	
	private int _myNumberOfUniforms;
	private int _mySize;
	private Map<String, GLUniformBlockVariable> _myVariableLookUpMap = new HashMap<>();
	
	private ByteBuffer _myData;
	
	GLUniformBlock(int theID, GLShaderProgram theProgram, String...theUniformNames){
		_myID = theID;
		_myProgram = theProgram;
		
		_myNumberOfUniforms = activeUniformBlock( GL_UNIFORM_BLOCK_ACTIVE_UNIFORMS);
		_mySize = activeUniformBlock( GL_UNIFORM_BLOCK_DATA_SIZE);
		
		_myData = ByteBuffer.allocate(_mySize);
		
		IntBuffer myIndices = IntBuffer.allocate(_myNumberOfUniforms);
		glGetUniformIndices(_myProgram.id(), theUniformNames, myIndices);
		
		IntBuffer myOffsets = activeUniforms(GL_UNIFORM_OFFSET, _myNumberOfUniforms);
		IntBuffer mySizes = activeUniforms(GL_UNIFORM_SIZE, _myNumberOfUniforms);
		IntBuffer myTypes = activeUniforms(GL_UNIFORM_TYPE, _myNumberOfUniforms);
		
		for(int i = 0; i < myIndices.capacity();i++){
			_myVariableLookUpMap.put(
				theUniformNames[i], 
				new GLUniformBlockVariable(
					myIndices.get(i),
					myOffsets.get(i),
					mySizes.get(i),
					myTypes.get(i)
				)
			);
		}
	}
	
	private int activeUniformBlock(int theInfo){
		IntBuffer myResult = IntBuffer.allocate(1);
		glGetActiveUniformBlockiv(_myProgram.id(), _myID, theInfo, myResult);
		return myResult.get(0);
	}
	
	private IntBuffer activeUniforms(int theInfo, int theSize){
		IntBuffer myResult = IntBuffer.allocate(theSize);
		glGetActiveUniformBlockiv(_myProgram.id(), _myID, theInfo, myResult);
		return myResult;
	}
	
	/**
	 * Size of the storage to hold the uniforms block data
	 * @return Size of the storage to hold the uniforms block data
	 */
	public int dataSize(){
		return _mySize;
	}
	
	/**
	 * Number of uniforms in this block
	 * @return Number of uniforms in this block
	 */
	public int numberOfUniforms(){
		return _myNumberOfUniforms;
	}
	
	public void updateData(){
		_myData.rewind();
		_myUniformBuffer.data(_myData);
		_myUniformBuffer.bindBufferBase(GLBufferTarget.UNIFORM, _myID);
	}
}
