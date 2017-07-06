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

import java.util.HashMap;
import java.util.List;

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
 * this class maps the Library_Images -tag
 * </p>
 * 
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @author christianriekoff
 * @version 1.0
 */
class CCColladaImages {

	private HashMap<String, CCColladaImage> _myImageMap = new HashMap<String, CCColladaImage>();

	CCColladaImages(List<CCDataElement> images) {
		for (CCDataElement img : images) {
			CCColladaImage i = new CCColladaImage(img);
			_myImageMap.put(i.id(), i);
		}

	}

	/**
	 * 
	 * @param theID of the Image-Tag
	 * @return a mapped Image
	 */
	CCColladaImage image(String theID) {
		return _myImageMap.get(theID);
	}

}
