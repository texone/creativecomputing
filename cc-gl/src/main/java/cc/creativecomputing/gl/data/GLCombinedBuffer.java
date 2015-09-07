package cc.creativecomputing.gl.data;

import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.data.CCCombinedBufferData;
import cc.creativecomputing.data.CCFloatBufferData;
import cc.creativecomputing.gl4.GLBuffer;
import cc.creativecomputing.gl4.GLBuffer.GLBufferTarget;

public class GLCombinedBuffer {
	
	public static class GLBufferData{
		private String _myName;
		private int _myID;
		private int _myStride;
		private GLBuffer _myBuffer;
		
		private GLBufferData(String theName, GLBuffer theData, int theStride){
			_myName = theName;
			_myBuffer = theData;
			_myStride = theStride;
		}
		
		private GLBufferData(int theID, GLBuffer theData, int theStride){
			_myID = theID;
			_myBuffer = theData;
			_myStride = theStride;
		}
		
		public String name(){
			return _myName;
		}
		
		public int id(){
			return _myID;
		}
		
		public GLBuffer buffer(){
			return _myBuffer;
		}
		
		public int stride(){
			return _myStride;
		}
	}
	
	 /** Buffer data holding buffers and number of coordinates per vertex */
    protected GLBufferData[] _myIndexAccess = new GLBufferData[CCCombinedBufferData.MAX_NUMBER_DATA];
    
    protected Map<String, GLBufferData> _myBufferMap = new HashMap<String, GLBufferData>();
    
    protected GLBuffer _myElementBuffer = null;
    protected int _myNumberOfIndices = 0;
    protected int _myNumberOfVertices = 0;
	
	public GLCombinedBuffer(CCCombinedBufferData theData){
		_myNumberOfVertices = theData.numberOfVertices();
		
		for(String myKey:theData.nameAccess().keySet()){
			CCFloatBufferData myData = theData.nameAccess().get(myKey).data();
			GLBuffer myBuffer = new GLBuffer(GLBufferTarget.ARRAY);
			myBuffer.bind();
			myBuffer.data(myData.buffer());
			_myBufferMap.put(myKey, new GLBufferData(myKey, myBuffer, myData.valuesPerTuple()));
			myBuffer.unbind();
		}
		
		for(int i = 0; i < _myIndexAccess.length;i++){
			if(theData.indexAccess()[i] == null)continue;
			CCFloatBufferData myData = theData.indexAccess()[i].data();
			
			GLBuffer myBuffer = new GLBuffer(GLBufferTarget.ARRAY);
			myBuffer.bind();
			myBuffer.data(myData.buffer());
			_myIndexAccess[i] = new GLBufferData(i, myBuffer, myData.valuesPerTuple());
			myBuffer.unbind();
		}
		
		if(theData.indices() != null){
			_myElementBuffer = new GLBuffer(GLBufferTarget.ELEMENT_ARRAY);
			_myElementBuffer.bind();
			_myElementBuffer.data(theData.indexBuffer());
			_myNumberOfIndices = theData.numberOfIndices();
			_myElementBuffer.unbind();
		}
	}
	
	public int numberOfIndices(){
		return _myNumberOfIndices;
	}
	
	public int numberOfVertices(){
		return _myNumberOfVertices;
	}
	
	public boolean hasIndices(){
		return _myElementBuffer != null;
	}
	
	public GLBuffer elementBuffer(){
		return _myElementBuffer;
	}
	
	public Map<String, GLBufferData> bufferDataMap(){
		return _myBufferMap;
	}
	
	public GLBufferData[] indexAccess(){
		return _myIndexAccess;
	}
}
