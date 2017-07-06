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
 * this class maps the Image -tag
 * </p>
 * 
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @author Christian Riekoff
 * @version 1.0
 */
class CCColladaImage {

	private String _myFileName;
	private String _myID;

	CCColladaImage(CCDataElement theImageXML) {
		_myID = theImageXML.attribute("id");
		_myFileName = theImageXML.child("init_from").content();

	}

	/**
	 * the ID of the Image-tag
	 * 
	 * @return
	 */
	String id() {
		return _myID;
	}

	/**
	 * 
	 * @return the Filename of the picture
	 */
	String fileName() {
		return _myFileName;
	}

	@Override
	public String toString() {
		return "Image-Filename '" + _myFileName + "'";
	}

}
