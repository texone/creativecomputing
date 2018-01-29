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

import static org.lwjgl.opengl.GL11.GL_COLOR_ARRAY;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NORMAL_ARRAY;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glColorPointer;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glNormalPointer;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glClientActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;

import java.util.List;

import cc.creativecomputing.graphics.CCBufferObject.CCBufferTarget;
import cc.creativecomputing.graphics.CCBufferObject.CCUsageFrequency;
import cc.creativecomputing.graphics.CCBufferObject.CCUsageTYPE;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.io.CCBufferUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

public class CCVBOMesh extends CCMesh{

    // Vertex Buffer Object Names
    private CCBufferObject _myVertexBuffer;	
    private boolean _myHasUpdatedVertices = true;
    private boolean _myHasVertices = false;
    
    private CCBufferObject[] _myTextureCoordBuffers = new CCBufferObject[8];
    private boolean[] _myHasUpdatedTextureCoords = new boolean[] {true, true, true, true, true, true, true, true};
    private boolean[] _myHasTextureCoords = new boolean[] {false, false, false, false, false, false, false, false};
    
    private CCBufferObject _myColorBuffer;
    private boolean _myHasUpdatedColors = true;
    private boolean _myHasColors = false;
    
    private CCBufferObject _myNormalBuffer;
    private boolean _myHasUpdatedNormals = true;
    private boolean _myHasNormals = false;
    
//    private CCBufferObject _myIndexBuffer;
    private boolean _myHasIndices = false;
//    private boolean _myHasUpdatedIndices = true;
//    
//    private boolean _myHasDefinedSize = false;
    
    /**
     * Create a new VBO Mesh, where draw mode is set to QUADS
     */
	public CCVBOMesh(){
    	super(CCDrawMode.QUADS);
	}
	
	public CCVBOMesh(final CCDrawMode theDrawMode){
    	super(theDrawMode);
    }
	
	public CCVBOMesh(final CCDrawMode theDrawMode, final int theNumberOfVertices){
		this(theDrawMode, theNumberOfVertices,3);
	}
	
	public CCVBOMesh(final CCDrawMode theDrawMode, final int theNumberOfVertices, final int theNumberOfVertexCoords){
		super(theDrawMode, theNumberOfVertices);
		
		_myVertexSize = theNumberOfVertexCoords;
		_myHasVertices = true;
	}
    
    public CCVBOMesh(
    	final List<CCVector3> theVertices,
    	final List<CCVector2> theTextureCoords,
    	final List<CCColor> theColors
    ){
    	super(theVertices, theTextureCoords, theColors);
    }
    
    public CCVBOMesh(
    	final CCDrawMode theDrawMode,
        final List<CCVector3> theVertices,
        final List<CCVector2> theTextureCoords,
        final List<CCColor> theColors
    ){
    	super(theDrawMode, theVertices, theTextureCoords, theColors);
    }
    
    
    
    //////////////////////////////////////////////////////
    //
    //  METHODS TO ADD VERTEX DATA
    //
    //////////////////////////////////////////////////////
    
    @Override
    public void prepareVertexData(int theNumberOfVertices, int theVertexSize){
    	_myNumberOfVertices = theNumberOfVertices;
    	_myVertexSize = theVertexSize;

    	if(_myVertices == null || _myVertices.limit() / _myVertexSize != _myNumberOfVertices) {
    		_myVertexSize = theVertexSize;
    		if(_myVertexBuffer == null) {
    			_myVertexBuffer = new CCBufferObject(_myNumberOfVertices * _myVertexSize * CCBufferUtil.SIZE_OF_FLOAT);
    		}else {
    			_myVertexBuffer.bufferData(_myNumberOfVertices * _myVertexSize * CCBufferUtil.SIZE_OF_FLOAT, CCUsageFrequency.DYNAMIC, CCUsageTYPE.DRAW);
    		}
    		_myVertices = _myVertexBuffer.data().asFloatBuffer();
    		_myVertices.rewind();
    	}
		_myHasUpdatedVertices = true;
		_myHasVertices = true;
    }
    
    public void vertices(CCShaderBuffer theShaderTexture){
    	vertices(theShaderTexture,0,0,theShaderTexture.width(), theShaderTexture.height());
    }
    
    public void vertices(CCShaderBuffer theShaderTexture, final int theID){
    	vertices(theShaderTexture,theID,0,0,theShaderTexture.width(), theShaderTexture.height());
    }
    
    public void vertices(CCShaderBuffer theShaderTexture, final int theX, final int theY, final int theWidth, final int theHeight) {
    	vertices(theShaderTexture, 0, theX, theY, theWidth, theHeight);
    }
    
    public void vertices(CCShaderBuffer theShaderTexture, final int theID, final int theX, final int theY, final int theWidth, final int theHeight) {
    	if(_myVertexBuffer == null)_myVertexBuffer = new CCBufferObject();
    	
    	_myVertexSize = theShaderTexture.numberOfChannels();
    	_myNumberOfVertices = theWidth * theHeight;
    	
		_myVertexBuffer.copyDataFromTexture(theShaderTexture, theID, theX, theY, theWidth, theHeight);
    	
    	_myHasVertices = true;
		_myHasUpdatedVertices = false;
    }
	
	public CCBufferObject vertexBuffer() {
		prepareVertexData(_myVertexSize);
		return _myVertexBuffer;
	}
    
    //////////////////////////////////////////////////////
    //
    //  METHODS TO ADD NORMAL DATA
    //
    //////////////////////////////////////////////////////
	
	@Override
	public void prepareNormalData(int theNumberOfVertices){
		_myNumberOfVertices = theNumberOfVertices;
		
    	if(_myNormalBuffer == null || _myNormals.limit() / 3 != _myNumberOfVertices) {
    		_myNumberOfVertices = theNumberOfVertices;
    		_myNormalBuffer = new CCBufferObject(_myNumberOfVertices * 3 * CCBufferUtil.SIZE_OF_FLOAT);
    		_myNormals = _myNormalBuffer.data().asFloatBuffer();
    		_myNormals.rewind();
    	}
		_myHasNormals = true;
    	_myHasUpdatedNormals = true;
    }
    
    public void normals(CCShaderBuffer theShaderTexture){
    	normals(theShaderTexture,0,0,theShaderTexture.width(), theShaderTexture.height());
    }
    
    public void normals(CCShaderBuffer theShaderTexture, final int theID){
    	normals(theShaderTexture,theID,0,0,theShaderTexture.width(), theShaderTexture.height());
    }
    
    public void normals(CCShaderBuffer theShaderTexture, final int theX, final int theY, final int theWidth, final int theHeight) {
    	normals(theShaderTexture, 0, theX, theY, theWidth, theHeight);
    }
    
    /**
     * 
     * @param theShaderTexture
     * @param theID
     * @param theX
     * @param theY
     * @param theWidth
     * @param theHeight
     */
    public void normals(CCShaderBuffer theShaderTexture, final int theID, final int theX, final int theY, final int theWidth, final int theHeight) {    	
    	if(_myNormalBuffer == null)_myNormalBuffer = new CCBufferObject();
		
		_myNormalBuffer.copyDataFromTexture(theShaderTexture, theID, theX, theY, theWidth, theHeight);
    	
    	_myHasNormals = true;
		_myHasUpdatedNormals = false;
    }
    
    //////////////////////////////////////////////////////
    //
    //  METHODS TO ADD TEXTURE COORD DATA
    //
    //////////////////////////////////////////////////////
	
    @Override
    public void prepareTextureCoordData(int theNumberOfVertices, int theLevel, int theTextureCoordSize){
    	_myNumberOfVertices = theNumberOfVertices;
    	_myTextureCoordSize[theLevel] = theTextureCoordSize;
    	if(_myTextureCoordBuffers[theLevel] == null || _myTextureCoords[theLevel].limit() / _myTextureCoordSize[theLevel] != _myNumberOfVertices) {
    		if(_myTextureCoordBuffers[theLevel] == null) {
    			_myTextureCoordBuffers[theLevel] = new CCBufferObject(_myNumberOfVertices * theTextureCoordSize * CCBufferUtil.SIZE_OF_FLOAT);
    		}else {
    			_myTextureCoordBuffers[theLevel].bufferData(_myNumberOfVertices * theTextureCoordSize * CCBufferUtil.SIZE_OF_FLOAT, CCUsageFrequency.DYNAMIC, CCUsageTYPE.DRAW);
    		}

    		_myTextureCoords[theLevel] = _myTextureCoordBuffers[theLevel].data().asFloatBuffer();
	    	_myTextureCoords[theLevel].rewind();
	    }
    	
    	_myHasTextureCoords[theLevel] = true;
    	_myHasUpdatedTextureCoords[theLevel] = true;
    }
	
	//////////////////////////////////////////////////
	//
	// METHODS TO ADD COLOR DATA
	//
	//////////////////////////////////////////////////
	
	@Override
	public void prepareColorData(int theNumberOfVertices){
		_myNumberOfVertices = theNumberOfVertices;
		
    	if(_myColorBuffer == null || _myColors.limit() / 4 != _myNumberOfVertices){
    		_myColorBuffer = new CCBufferObject(_myNumberOfVertices * 4 * CCBufferUtil.SIZE_OF_FLOAT);
    		_myColors = _myColorBuffer.data().asFloatBuffer();
    		_myColors.rewind();
    	}
    	
    	_myHasColors = true;
		_myHasUpdatedColors = true;
    }
    
    public void colors(CCShaderBuffer theShaderTexture){
    	colors(theShaderTexture,0,0,theShaderTexture.width(), theShaderTexture.height());
    }
    
    public void colors(CCShaderBuffer theShaderTexture, final int theID){
    	colors(theShaderTexture,theID,0,0,theShaderTexture.width(), theShaderTexture.height());
    }
    
    public void colors(CCShaderBuffer theShaderTexture, final int theX, final int theY, final int theWidth, final int theHeight) {
    	colors(theShaderTexture, 0, theX, theY, theWidth, theHeight);
    }
    
    public void colors(CCShaderBuffer theShaderTexture, final int theID, final int theX, final int theY, final int theWidth, final int theHeight) {
    	if(_myColorBuffer == null)_myColorBuffer = new CCBufferObject();
    	
    	_myColorBuffer.copyDataFromTexture(theShaderTexture, theID, theX, theY, theWidth, theHeight);
    	
    	_myHasColors = true;
    	_myHasUpdatedColors = false;
    }
    
//    public void vertices(CCShaderBuffer theShaderTexture, final int theID, final int theX, final int theY, final int theWidth, final int theHeight) {
//    	if(_myVertexBuffer == null)_myVertexBuffer = new CCBufferObject();
//    	
//    	_myVertexSize = theShaderTexture.numberOfChannels();
//    	_myNumberOfVertices = theWidth * theHeight;
//    	
//		_myVertexBuffer.copyDataFromTexture(theShaderTexture, theID, theX, theY, theWidth, theHeight);
//    	
//    	_myHasVertices = true;
//		_myHasUpdatedVertices = false;
//    }
	
	@Override
	public void enable(){
		// Enable Pointers
		for(int i = 0; i < _myHasTextureCoords.length;i++) {
	    	if(_myHasTextureCoords[i]){
	    		if(_myHasUpdatedTextureCoords[i]) {
	    			_myTextureCoordBuffers[i].bind(CCBufferTarget.ARRAY);
//	    			_myTextureCoordBuffers[i].bufferData();
	    			_myTextureCoordBuffers[i].unbind();
	        		_myHasUpdatedTextureCoords[i] = false;
	    		}
	    		_myTextureCoordBuffers[i].bind(CCBufferTarget.ARRAY);
	    		glClientActiveTexture(GL_TEXTURE0 + i);
	    		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
	    		glTexCoordPointer(_myTextureCoordSize[i], GL_FLOAT, 0, 0);
	    	}
		}
    	if(_myHasColors){
    		if(_myHasUpdatedColors) {
    			_myColorBuffer.bind(CCBufferTarget.ARRAY);
//    			_myColorBuffer.bufferData();
    			_myColorBuffer.unbind();
        		_myHasUpdatedColors = false;
    		}
    		_myColorBuffer.bind(CCBufferTarget.ARRAY);
    		glEnableClientState(GL_COLOR_ARRAY);
    		glColorPointer(4, GL_FLOAT, 0, 0);
    	}
    	if(_myHasNormals){
    		if(_myHasUpdatedNormals) {
    			_myNormalBuffer.bind(CCBufferTarget.ARRAY);
//    			_myNormalBuffer.bufferData();
    			_myNormalBuffer.unbind();
    			_myHasUpdatedNormals = false;
    		}
    		_myNormalBuffer.bind(CCBufferTarget.ARRAY);
    		glEnableClientState(GL_NORMAL_ARRAY);
    		glNormalPointer(GL_FLOAT, 0, 0);
    	}
    	if(_myHasVertices){
    		if(_myHasUpdatedVertices) {
    			_myVertexBuffer.bind(CCBufferTarget.ARRAY);
//    			_myVertexBuffer.bufferData();
    			_myVertexBuffer.unbind();
    			_myHasUpdatedVertices = false;
    		}
    		_myVertexBuffer.bind(CCBufferTarget.ARRAY);
    		glEnableClientState(GL_VERTEX_ARRAY);
 	   		glVertexPointer(_myVertexSize, GL_FLOAT, 0, 0);
    	}
    	if(_myHasIndices) {
    		
    	}	
    	
//    	if(_myDrawMode == CCGraphics.POINTS && g._myDrawTexture){
//			glEnable(GL_POINT_SPRITE);
//			glTexEnvi(GL_POINT_SPRITE, GL_COORD_REPLACE, GL_TRUE); 
//		}
	}
	
	public void drawArray(CCGraphics g){
    	// Draw All Of The Triangles At Once
    	if(_myIndices == null){
    		glDrawArrays(_myDrawMode.glID, 0, _myNumberOfVertices);
    	}else{
    		glDrawElements(_myDrawMode.glID, _myIndices);
    	}
    }
	
	@Override
	public void disable(){
		super.disable();
        
        glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
}
