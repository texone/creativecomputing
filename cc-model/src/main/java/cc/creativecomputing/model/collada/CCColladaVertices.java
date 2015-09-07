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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.io.xml.CCXMLElement;

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
 * this class is a Helperclass to map the vertice-tag inside geometry-tag
 * </p>
 * 
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @version 1.0
 */
class CCColladaVertices extends CCColladaSubTag {
	
	public static final String POSITION = "POSITION";
	public static final String NORMAL = "NORMAL";

	private String _myID;
	private Map<String, String> _mySourceMap = new HashMap<String, String>();
	private List<String> _mySementics = new ArrayList<>();

	CCColladaVertices(CCXMLElement theVerticesXML) {
		_myID = theVerticesXML.attribute("id");
		// analyze the vertices-Tag, search for semantic POSITION (only that is interesting)
		for (CCXMLElement myInputXML : theVerticesXML.children("input")) {
			_mySementics.add(myInputXML.attribute("semantic"));
			_mySourceMap.put(myInputXML.attribute("semantic"), myInputXML.attribute("source").substring(1));
		}

	}
	
	List<String> sementics(){
		return _mySementics;
	}
	
	String source(String theSementic) {
		return _mySourceMap.get(theSementic);
	};
	
	String source() {
		return _mySourceMap.get(POSITION);
	};
	
	boolean hasSource(String theSemantic) {
		return _mySourceMap.containsKey(theSemantic);
	}

	@Override
	String id() {
		return _myID;
	}

}
