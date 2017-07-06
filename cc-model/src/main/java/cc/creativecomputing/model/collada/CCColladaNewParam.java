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
 * this class is a helper-class to map the newparam-tag inside effect-tag
 * </p>
 * 
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @version 1.0
 */
class CCColladaNewParam extends CCColladaSubTag {

	private String _myNextSource;
	private String _myID;

	CCColladaNewParam(CCDataElement theNewParamXML) {
		_myID = theNewParamXML.attribute("sid");

		CCDataElement initfrom = theNewParamXML.child("surface/init_from");
		if (initfrom != null)
			_myNextSource = initfrom.content();

		CCDataElement src = theNewParamXML.child("sampler2D/source");
		if (src != null)
			_myNextSource = src.content();

	}

	/**
	 * @return the next Information Tag
	 */
	String source() {
		return _myNextSource;
	}

	/**
	 * @return the ID
	 */
	String id() {
		return _myID;
	}

}
