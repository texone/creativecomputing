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

import cc.creativecomputing.io.xml.CCXMLElement;

/**
 * <p>
 * Lucerne University of Applied Sciences and Arts <a href="http://www.hslu.ch">http://www.hslu.ch</a>
 * </p>
 * 
 * <p>
 * This source is free; you can redistribute it and/or modify it under the terms of the GNU General Public License and
 * by naming of the originally author
 * </p>
 * 
 * <p>
 * this class is a helper-class to map the source-tag inside the Geometry-tag
 * </p>
 * 
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @author christianriekoff
 * @version 1.0
 */
class CCColladaSource extends CCColladaSubTag {

	private float[][] _myPointMatrix; // format: [index][x,y,z or x,y]
	private String _myID;
	private String[] _myStringValues;
	private int _myStride;

	CCColladaSource(CCXMLElement theSourceXML) {
		_myID = theSourceXML.attribute("id");
		CCXMLElement myAccessorXML = theSourceXML.child("technique_common/accessor");
		String myType = myAccessorXML.child("param").attribute("type");
		
		if(myType.contains("float")) {
			String[] myFloatArray = theSourceXML.child("float_array").content().split("\\s");
			int myCount = myAccessorXML.intAttribute("count");
			_myStride = myAccessorXML.intAttribute("stride",1);

			_myPointMatrix = new float[myCount][_myStride];

			for (int i = 0, k = 0; i < myCount; i++) {
				for (int j = 0; j < _myStride; j++, k++) {
					_myPointMatrix[i][j] = Float.parseFloat(myFloatArray[k]);
				}
			}
		}else if(myType.equals("name")) {
			_myStringValues = theSourceXML.child("Name_array").content().split("\\s");
		}
		

	}
	
	public int stride(){
		return _myStride;
	}

	/**
	 * 
	 * @return returns position-Points in the format format: [index][x,y,z or x,y]
	 */
	float[][] pointMatrix() {
		return _myPointMatrix;
	}
	
	String[] stringValues() {
		return _myStringValues;
	}

	/**
	 * @return the ID
	 */
	String id() {
		return _myID;
	}

	String source() {
		return _myID;
	}
}
