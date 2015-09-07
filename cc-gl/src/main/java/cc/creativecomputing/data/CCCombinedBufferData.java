

package cc.creativecomputing.data;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * BufferData contains all the commonly used buffers for rendering a mesh.
 */
public class CCCombinedBufferData {
	
	public static final int MAX_NUMBER_DATA = 64;
	
	public static class CCBufferDataWithAttribute{
		private String _myName;
		private int _myID;
		private CCFloatBufferData _myData;
		
		private CCBufferDataWithAttribute(String theName, CCFloatBufferData theData){
			_myName = theName;
			_myData = theData;
		}
		
		private CCBufferDataWithAttribute(int theID, CCFloatBufferData theData){
			_myID = theID;
			_myData = theData;
		}
		
		public CCFloatBufferData data(){
			return _myData;
		}
		
		public CCBufferDataWithAttribute clone(){
			CCBufferDataWithAttribute myClone = new CCBufferDataWithAttribute(_myName,_myData.clone());
			myClone._myID = _myID;
			return myClone;
		}
	}

    /** Number of vertices represented by this data. */
    protected int _myNumberOfVertices;

    /** Buffer data holding buffers and number of coordinates per vertex */
    protected CCBufferDataWithAttribute[] _myIndexAccess = new CCBufferDataWithAttribute[MAX_NUMBER_DATA];
    
    protected Map<String, CCBufferDataWithAttribute> _myNameAccess = new HashMap<String, CCCombinedBufferData.CCBufferDataWithAttribute>();

    /** Index data. */
    protected CCIndexBufferData<?> _myIndices;
    protected int _myNumberOfIndices;
    
    public CCCombinedBufferData(int theVertexCount){
    	_myNumberOfVertices = theVertexCount;
    }
    
    public CCBufferDataWithAttribute[] indexAccess(){
    	return _myIndexAccess;
    }
    
    public Map<String, CCBufferDataWithAttribute> nameAccess(){
    	return _myNameAccess;
    }

    /**
     * Gets the vertex count.
     * 
     * @return the vertex count
     */
    public int numberOfVertices() {
        return _myNumberOfVertices;
    }

    /**
     * Gets the buffer.
     * 
     * @return the buffer
     */
    public FloatBuffer buffer(int theID) {
    	CCBufferDataWithAttribute myData = _myIndexAccess[theID];
    	if(myData == null)return null;
        return myData._myData.buffer();
    }
    
    public FloatBuffer buffer(String theName){
    	CCBufferDataWithAttribute myData = _myNameAccess.get(theName);
    	if(myData == null)return null;
        return myData._myData.buffer();
    }

    /**
     * Gets the buffer data.
     * 
     * @return the buffer data
     */
    public CCFloatBufferData bufferData(int theID) {
    	CCBufferDataWithAttribute myData = _myIndexAccess[theID];
    	if(myData == null)return null;
        return myData._myData;
    }

    /**
     * Gets the buffer data.
     * 
     * @return the buffer data
     */
    public CCFloatBufferData bufferData(String theName) {
    	CCBufferDataWithAttribute myData = _myNameAccess.get(theName);
    	if(myData == null)return null;
        return myData._myData;
    }

    /**
     * 
     * @param theID
     * @param theBuffer
     * @param theStride
     */
    public void buffer(int theID, final FloatBuffer theBuffer, int theStride) {
        if (theBuffer == null) {
        	bufferData(theID, null);
        } else {
        	bufferData(theID, new CCFloatBufferData(theBuffer, theStride));
        }
    }

    /**
     * 
     * @param theName
     * @param theBuffer
     * @param theStride
     */
    public void buffer(String theName, final FloatBuffer theBuffer, int theStride) {
        if (theBuffer == null) {
        	bufferData(theName, null);
        } else {
        	bufferData(theName, new CCFloatBufferData(theBuffer, theStride));
        }
    }

    /**
     * 
     * @param theID
     * @param theBufferData
     */
    public void bufferData(int theID, final CCFloatBufferData theBufferData) {
       _myIndexAccess[theID] = new CCBufferDataWithAttribute(theID, theBufferData);
    }
    
    public void bufferData(String theName, final CCFloatBufferData theBufferData) {
        _myNameAccess.put(theName, new CCBufferDataWithAttribute(theName, theBufferData));
    }
    
    public void allocateData(int theID, int theStride){
    	_myIndexAccess[theID] = new CCBufferDataWithAttribute(theID, new CCFloatBufferData(_myNumberOfVertices * theStride, theStride));
    }
    
    public void allocateData(String theName, int theStride){
    	_myNameAccess.put(theName, new CCBufferDataWithAttribute(theName, new CCFloatBufferData(_myNumberOfVertices * theStride, theStride)));
    }

    /**
     * Gets the index buffer.
     * 
     * @return the index buffer
     */
    public Buffer indexBuffer() {
        if (_myIndices == null) {
            return null;
        }
        return _myIndices.buffer();
    }

    /**
     * Sets the index buffer.
     * 
     * @param theIndices
     *            the new index buffer
     */
    public void indices(final IntBuffer theIndices) {
        if (theIndices == null) {
            _myIndices = null;
        } else {
            _myIndices = new CCIntBufferData(theIndices);
        }
    }

    /**
     * Sets the index buffer.
     * 
     * @param theIndices
     *            the new index buffer
     */
    public void indices(final ShortBuffer theIndices) {
        if (theIndices == null) {
            _myIndices = null;
        } else {
            _myIndices = new CCShortBufferData(theIndices);
        }
    }

    /**
     * Sets the index buffer.
     * 
     * @param theIndices
     *            the new index buffer
     */
    public void indices(final ByteBuffer theIndices) {
        if (theIndices == null) {
            _myIndices = null;
        } else {
            _myIndices = new CCByteBufferData(theIndices);
        }
    }
    
    public void allocateIndices(int theNumberOfIdices){
    	_myNumberOfIndices = theNumberOfIdices;
    	if(theNumberOfIdices <= 0)return;
    	_myIndices = new CCIntBufferData(_myNumberOfIndices);
    }

    /**
     * Gets the indices.
     * 
     * @return the indices
     */
    public CCIndexBufferData<?> indices() {
        return _myIndices;
    }

    /**
     * Sets the indices
     * 
     * @param theIndices
     *            the new indices
     */
    public void indices(final CCIndexBufferData<?> theIndices) {
        _myIndices = theIndices;
    }

    /**
     * Gets the index lengths.
     * 
     * @return the index lengths
     */
    public int numberOfIndices() {
        return _myNumberOfIndices;
    }

	public void rewindBuffer() {
		for(CCBufferDataWithAttribute myData:_myIndexAccess){
			if(myData == null)continue;
			myData._myData._myBuffer.rewind();
		}
		for(String myName:_myNameAccess.keySet()){
        	_myNameAccess.get(myName)._myData._myBuffer.rewind();
        }
	}

    public CCCombinedBufferData clone() {
        final CCCombinedBufferData myClone = new CCCombinedBufferData(_myNumberOfVertices);
        for(int i = 0; i < _myIndexAccess.length;i++){
			if(_myIndexAccess[i] == null)continue;
			myClone._myIndexAccess[i] = _myIndexAccess[i].clone();
		}
        
        for(String myName:_myNameAccess.keySet()){
        	myClone._myNameAccess.put(myName, _myNameAccess.get(myName).clone());
        }

        if (_myIndices != null) {
        	myClone._myIndices = _myIndices.clone();
        }

        return myClone;
    }

}
