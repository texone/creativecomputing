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
 * by nameing of the originally author
 * </p>
 * 
 * <p>
 * this class maps the Material -tag
 * </p>
 * 
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @author christianriekoff
 * @version 1.0
 */
class CCColladaMaterial extends CCColladaElement{

	private CCColladaEffect _myEffect;

	CCColladaMaterial(CCXMLElement theMaterialXML, CCColladaEffects theEffectsLib) {
		super(theMaterialXML);

		CCXMLElement _myInstanceEffectXML = theMaterialXML.child("instance_effect");
		String myEffectID = _myInstanceEffectXML.attribute("url").substring(1);
		_myEffect = theEffectsLib.element(myEffectID);

	}

	/**
	 * 
	 * @return the Effect that maps to the Material
	 */
	CCColladaEffect effect() {
		return _myEffect;
	}

	public String toString() {
		return "Material ID '" + _myID + "' with " + _myEffect + " ";
	}

}
