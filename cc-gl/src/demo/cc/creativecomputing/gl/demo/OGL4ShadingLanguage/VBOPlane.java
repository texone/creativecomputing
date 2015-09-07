package cc.creativecomputing.gl.demo.OGL4ShadingLanguage;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl4.GLBuffer;
import cc.creativecomputing.gl4.GLBuffer.GLBufferTarget;
import cc.creativecomputing.gl4.GLDataType;
import cc.creativecomputing.gl4.GLDrawMode;
import cc.creativecomputing.gl4.GLVertexArray;
import cc.creativecomputing.math.CCMath;

public class VBOPlane {

	private GLVertexArray _myVertexArray;
	private int _myNumberOfVertices = 0;
	private int _myFaces = 0;
	private int _myXres;
	private int _myYres;
	
	public VBOPlane(float theWidth, float theHeight, int theXres, int theYres, boolean theCenter){
		_myXres = theXres;
		_myYres = theYres;
		
		_myNumberOfVertices = theXres * theYres;
		_myFaces = (theXres - 1) * (theYres - 1) * 6;
		CCLog.info(_myFaces);
		
		float x0 = theCenter ? -theWidth / 2 : 0;
		float x1 = theCenter ?  theWidth / 2 : theWidth;

		float y0 = theCenter ? -theHeight / 2 : 0;
		float y1 = theCenter ?  theHeight / 2 : theHeight;
		
		FloatBuffer myVertices = FloatBuffer.allocate(_myNumberOfVertices * 3);
		FloatBuffer myNormals = FloatBuffer.allocate(_myNumberOfVertices * 3);
		FloatBuffer myTexCoords = FloatBuffer.allocate(_myNumberOfVertices * 2);
		for(int x = 0; x < theXres ;x++){
			for(int y = 0; y < theYres ;y++){
				myVertices.put(CCMath.map(x, 0, theXres - 1, x0, x1));
				myVertices.put(0);
				myVertices.put(CCMath.map(y, 0, theYres - 1, y0, y1));

				myNormals.put(0);
				myNormals.put(1);
				myNormals.put(0);

				myTexCoords.put(CCMath.map(x, 0, theXres - 1, 0, 1f));
				myTexCoords.put(CCMath.map(y, 0, theYres - 1, 0, 1f));
			}
		}
		myVertices.rewind();
		myNormals.rewind();
		myTexCoords.rewind();
		
		IntBuffer myInidices = IntBuffer.allocate(_myFaces);
		for(int x = 0; x < _myXres - 1; x++){
			for(int y = 0; y < _myYres - 1; y++){
				int myIndex0 = x * _myYres + y;
				myInidices.put(myIndex0);
				myInidices.put(myIndex0 + _myYres);
				myInidices.put(myIndex0 + 1);
				myInidices.put(myIndex0 + _myYres);
				myInidices.put(myIndex0 + _myYres + 1);
				myInidices.put(myIndex0 + 1);
				
				CCLog.info(myIndex0);
			}
		}
		myInidices.rewind();
		
		GLBuffer myVertexBuffer = new GLBuffer(GLBufferTarget.ARRAY);
		myVertexBuffer.bind();
		myVertexBuffer.data(myVertices);
		
		GLBuffer myNormalBuffer = new GLBuffer(GLBufferTarget.ARRAY);
		myNormalBuffer.bind();
		myNormalBuffer.data(myNormals);
		
		GLBuffer myTexCoordBuffer = new GLBuffer(GLBufferTarget.ARRAY);
		myTexCoordBuffer.bind();
		myTexCoordBuffer.data(myTexCoords);
		
		GLBuffer myElementBuffer = new GLBuffer(GLBufferTarget.ELEMENT_ARRAY);
		myElementBuffer.bind();
		myElementBuffer.data(myInidices);
		
		_myVertexArray = new GLVertexArray();
		_myVertexArray.bind();
		
		myVertexBuffer.bind();
		_myVertexArray.attributes(0, 3);
		
		myNormalBuffer.bind();
		_myVertexArray.attributes(1, 3);
		
		myTexCoordBuffer.bind();
		_myVertexArray.attributes(2, 2);

		myElementBuffer.bind();
		
		_myVertexArray.enableVertexAttribArray(0);
		_myVertexArray.enableVertexAttribArray(1);
		_myVertexArray.enableVertexAttribArray(2);
		
		_myVertexArray.unbind();
		myElementBuffer.unbind();
	}
	
	public void draw(){
		_myVertexArray.bind();
//		_myVertexArray.drawArrays(GLDrawMode.POINTS, 0, _myNumberOfVertices);
		_myVertexArray.drawElements(GLDrawMode.TRIANGLES, _myFaces, GLDataType.UNSIGNED_INT);
	}
}
