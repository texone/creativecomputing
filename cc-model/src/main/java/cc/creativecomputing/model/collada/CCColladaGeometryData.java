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
package cc.creativecomputing.model.collada;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.io.xml.CCDataElement;

/**
 * @author christianriekoff
 *
 */
public abstract class CCColladaGeometryData extends CCColladaSubTag {
	
	static class CCColladaGeometryInput{
		
		private String _mySemantic;
		private int _myOffset;
		private CCColladaSource _mySource;
		private FloatBuffer _myBuffer;
		
		private CCColladaGeometryInput(String theSemantic, int theOffset, CCColladaSource theSource ){
			_mySemantic = theSemantic;
			_myOffset = theOffset;
			_mySource = theSource;
		}
		
		CCColladaSource source(){
			return _mySource;
		}
		
		void buffer(FloatBuffer theBuffer){
			_myBuffer = theBuffer;
		}
		
		FloatBuffer buffer(){
			return _myBuffer;
		}
		
		int offset(){
			return _myOffset;
		}
		
		String semantic(){
			return _mySemantic;
		}
	}

	protected CCDataElement _myDataXML;
	protected String _myMaterialSymbol;
	protected CCColladaMaterial _myMaterial;
	protected CCColladaSource _myVertexSource; // pointmatrice
	protected CCColladaSource _myNormalSource;
	protected int[] _myPointIndices;// format: [index][offset]
	protected int _myStride;
	
	protected int _myNumberOfVertices;
	protected int _myPrimitiveSize;
	
	protected Map<String, CCColladaGeometryInput> _myInputMap = new HashMap<>();

	CCColladaGeometryData(CCDataElement theDataXML, HashMap<String, CCColladaSource> theSources, CCColladaVertices theVertices, int thePrimitivSize){
		_myDataXML = theDataXML;
		_myMaterialSymbol = _myDataXML.attribute("material");
		
		// extract p-Tag to matrix
		List<CCDataElement> myInputsXML = theDataXML.children("input");
		_myStride = myInputsXML.size();
		int myCount = Integer.parseInt(theDataXML.attribute("count"));
		_myNumberOfVertices = myCount * thePrimitivSize;
		
		_myPrimitiveSize = thePrimitivSize;
		
		List<CCColladaGeometryInput> myInputs = handleInputs(myInputsXML, theSources, theVertices);
		readInputs(theDataXML, myInputs);
	}
	
	public void readInputs(CCDataElement theDataXML, List<CCColladaGeometryInput> theInputs){
		String[] myPArray = theDataXML.child("p").content().split(" ");
		
		_myPointIndices = new int[myPArray.length];
		
		for (int i = 0; i < myPArray.length;i++) {
			_myPointIndices[i] = Integer.parseInt(myPArray[i]);
		}
		
		for(CCColladaGeometryInput myInput : theInputs){
			myInput._myBuffer = FloatBuffer.allocate(_myNumberOfVertices * myInput._mySource.stride());
			float[][] myPoints = myInput._mySource.pointMatrix();
			for(int i = 0; i < _myNumberOfVertices; i++){
				int myIndex = _myPointIndices[i * _myStride + myInput._myOffset];
				myInput._myBuffer.put(myPoints[myIndex]);
			}
			myInput._myBuffer.rewind();
			_myInputMap.put(myInput._mySemantic, myInput);
		}
	}
	
	public int numberOfVertices() {
		return _myNumberOfVertices;
	}
	
	public int stride(){
		return _myStride;
	}
	
	public int positionOffset(){
		return  _myInputMap.get("POSITION")._myOffset;
	}
	
	public FloatBuffer positions() {
		return _myInputMap.get("POSITION")._myBuffer;
	}
	
	public FloatBuffer normals() {
		return _myInputMap.get("NORMAL")._myBuffer;
	}
	
	public boolean hasNormals() {
		return _myInputMap.containsKey("NORMAL");
	}
	
	public FloatBuffer texCoords() {
		if(!_myInputMap.containsKey("TEXCOORD"))return null;
		return _myInputMap.get("TEXCOORD")._myBuffer;
	}
	
	public boolean hasTexCoords() {
		return _myInputMap.containsKey("TEXCOORD");
	}
	
	List<CCColladaGeometryInput> handleInputs(List<CCDataElement> theInputsXML, HashMap<String, CCColladaSource> theSources, CCColladaVertices theVertices) {
		List<CCColladaGeometryInput> myInputs = new ArrayList<>();
		for (CCDataElement myTriangleInputXML : theInputsXML) {
			String mySementic = myTriangleInputXML.attribute("semantic");
			switch(mySementic){
			case "VERTEX":
				for(String myVertexSementic:theVertices.sementics()){
					String mySource = theVertices.source(myVertexSementic);
					CCColladaGeometryInput myInput = new CCColladaGeometryInput(
						myVertexSementic,
						Integer.parseInt(myTriangleInputXML.attribute("offset")),
						(CCColladaSource)theSources.get(mySource)
					);
					myInputs.add(myInput);
				}
				break;
			default:
				CCColladaGeometryInput myInput = new CCColladaGeometryInput(
					mySementic,
					Integer.parseInt(myTriangleInputXML.attribute("offset")),
					(CCColladaSource)theSources.get(myTriangleInputXML.attribute("source").substring(1))
				);
				myInputs.add(myInput);
				break;
			}
		}
		return myInputs;
	}
	
	/**
	 * @return the material. It depends of the Runtime if it is null or not
	 */
	CCColladaMaterial material() {
		return _myMaterial;
	}

	/**
	 * invoke that method after the Object is initiated. Because of cyclic dependencies at runtime its not possible to set
	 * Material at initiating-time
	 * 
	 * @param instance of Material
	 */
	void material(CCColladaMaterial theMaterial) {
		_myMaterial = theMaterial;
	}

	/**
	 * @return the pointIndexMatrix in the format: [index][offset]
	 */
	int[] pointIndexMatrix() {
		return _myPointIndices;
	}

	/**
	 * 
	 * @return the Alias-Name of Material-ID (the Material-symbol)
	 */
	String source() {
		return _myMaterialSymbol;
	}

	/**
	 * 
	 * @return generates a Random-ID because the xml-Tag does'nt contain a defined ID
	 */
	String id() {
		return this.hashCode() + "_" + _myMaterialSymbol;
	}
	
	public int primitiveSize(){
		return _myPrimitiveSize;
	}
}
