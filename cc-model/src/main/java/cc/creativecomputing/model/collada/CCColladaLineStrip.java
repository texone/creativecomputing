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
 * <p>
 * Lucerne University of Applied Sciences and Arts <a href="http://www.hslu.ch">http://www.hslu.ch</a>
 * </p>
 * 
 * <p>
 * This source is free; you can redistribute it and/or modify it under the terms of the GNU General Public License and
 * by nameing of the originally author
 * </p>
 * 
 * <p>
 * helperclass for lines-tag inside Geometry-tag (contains one or more Lines). It maps all points and their orders
 * </p>
 * 
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @version 1.0
 */
class CCColladaLineStrip extends CCColladaGeometryData {

	CCColladaLineStrip(CCDataElement theLinesXML, HashMap<String, CCColladaSource> theSourcesMap, CCColladaVertices theVertices) {
		super(theLinesXML, theSourcesMap, theVertices, 2, CCDrawMode.LINE_STRIP);
		
	}
	
	@Override
	public void readInputs(CCDataElement theLinesXML, List<CCColladaGeometryInput> theInputs){
		String[] myPArray = theLinesXML.child("p").content().split(" ");
		
		_myNumberOfVertices = myPArray.length;
		_myPointIndices = new int[myPArray.length];
		
		for (int i = 0; i < myPArray.length;i++) {
			_myPointIndices[i] = Integer.parseInt(myPArray[i]);
		}
		
		for(CCColladaGeometryInput myInput : theInputs){
			myInput.buffer(FloatBuffer.allocate(_myNumberOfVertices * myInput.source().stride()));
			float[][] myPoints = myInput.source().pointMatrix();
			for(int i = 0; i < _myNumberOfVertices; i++){
				int myIndex = _myPointIndices[i * _myStride + myInput.offset()];
				myInput.buffer().put(myPoints[myIndex]);
			}
			myInput.buffer().rewind();
			_myInputMap.put(myInput.semantic(), myInput);
		}
	}

//	@Override
//	public String toString() {
//		String s = "";
//		s += "Lines from Geometry " + _myDataXML.parent().parent().attribute("id") + ":\n";
//		s += "uses Material symbol alias '" + source() + "'\n";
//		s += (_myMaterial == null) ? "material (still) not mapped \n" : "uses material-ID" + _myMaterial.id() + "\n";
//		s += "has vertex-Points: \n";
//		float[][] points = vertexSource().pointMatrix();
//		int[][] indexes = pointIndexMatrix();
//		for (int i = 0, j = 0; j < indexes.length; i++) {
//			s += "Line_" + i + "\n";
//			for (int k = 0; k < 2; k++, j++) {
//				int index = indexes[j][offsetVertex()];
//				s += "Point " + k + ": x=" + points[index][0] + ",y=" + points[index][1] + ",z=" + points[index][2] + "\n";
//
//			}
//		}
//
//		return s;
//	}
}
