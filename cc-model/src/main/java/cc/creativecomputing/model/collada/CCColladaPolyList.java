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
import java.util.HashMap;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.io.xml.CCDataElement;

/**
 * Provides the information needed to for a mesh to bind vertex attributes together and then organize 
 * those vertices into individual triangles.
 * <p>
 * The <triangles> element declares the binding of geometric primitives and vertex attributes for a <mesh> element.
 * The vertex array information is supplied in distinct attribute arrays that are then indexed by the <triangles> element.
 * <p>
 * Each triangle described by the mesh has three vertices. The first triangle is formed from the first, second, and third 
 * vertices. The second triangle is formed from the fourth, fifth, and sixth vertices, and so on.
 * 
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @author christianriekoff
 * @version 1.0
 */
public class CCColladaPolyList extends CCColladaGeometryData {
	

	CCColladaPolyList(CCDataElement triangles, HashMap<String, CCColladaSource> theSources, CCColladaVertices theVertices) {
		super(triangles, theSources, theVertices, 3, CCDrawMode.TRIANGLES);
	}

	@Override
	public void readInputs(CCDataElement theDataXML, List<CCColladaGeometryInput> theInputs){
		String[] myPArray = theDataXML.child("p").content().split(" ");
		
		_myPointIndices = new int[myPArray.length];
		
		for (int i = 0; i < myPArray.length;i++) {
			_myPointIndices[i] = Integer.parseInt(myPArray[i]);
		}
		
		String[] myVCountArray = theDataXML.child("vcount").content().split("\\s");
		
		int[] myCountArray = new int[myVCountArray.length];
		
		for (int i = 0; i < myVCountArray.length;i++) {
			myCountArray[i] = Integer.parseInt(myVCountArray[i]);
		}

		for(CCColladaGeometryInput myInput : theInputs){
			int myLength = 0;
			for(int myVCount:myCountArray){
				if(myVCount == 3)myLength += 3;
				else if(myVCount == 4)myLength += 6;
				else myLength += myVCount;
			}
			myInput.buffer(FloatBuffer.allocate(myLength * myInput.source().stride()));
			float[][] myPoints = myInput.source().pointMatrix();
			int i = 0;
			for(int myVCount:myCountArray){
				if(myVCount == 3){
					int myIndex0 = _myPointIndices[i * _myStride + myInput.offset()];
					i++;
					int myIndex1 = _myPointIndices[i * _myStride + myInput.offset()];
					i++;
					int myIndex2 = _myPointIndices[i * _myStride + myInput.offset()];
					i++;
					myInput.buffer().put(myPoints[myIndex0]);
					myInput.buffer().put(myPoints[myIndex1]);
					myInput.buffer().put(myPoints[myIndex2]);
				}else if(myVCount == 4){
					int myIndex0 = _myPointIndices[i * _myStride + myInput.offset()];
					i++;
					int myIndex1 = _myPointIndices[i * _myStride + myInput.offset()];
					i++;
					int myIndex2 = _myPointIndices[i * _myStride + myInput.offset()];
					i++;
					int myIndex3 = _myPointIndices[i * _myStride + myInput.offset()];
					i++;

					myInput.buffer().put(myPoints[myIndex0]);
					myInput.buffer().put(myPoints[myIndex1]);
					myInput.buffer().put(myPoints[myIndex2]);

					myInput.buffer().put(myPoints[myIndex0]);
					myInput.buffer().put(myPoints[myIndex2]);
					myInput.buffer().put(myPoints[myIndex3]);
				}else {
					for(int j = 0; j < myVCount;j++) {
						int myIndex0 = _myPointIndices[i * _myStride + myInput.offset()];
						i++;
						myInput.buffer().put(myPoints[myIndex0]);
					}
				}
			}
			
			myInput.buffer().rewind();
			_myInputMap.put(myInput.semantic(), myInput);
		}
	}
}
