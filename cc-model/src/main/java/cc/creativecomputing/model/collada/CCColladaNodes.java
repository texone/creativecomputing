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

import java.util.Collection;
import java.util.List;

import cc.creativecomputing.io.xml.CCDataElement;


/**
 * Provides a library in which to place <node> elements.
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @author christianriekoff
 * @version 1.0
 */
public class CCColladaNodes extends CCColladaLibrary<CCColladaSceneNode>{

	CCColladaNodes(CCColladaLoader theLoader, List<CCDataElement> theNodesXML, boolean theUseNameKey) {
		for (CCDataElement myNodeXML : theNodesXML) {
			CCColladaSceneNode myNode = new CCColladaSceneNode(theLoader, null, myNodeXML, theUseNameKey);
			put(myNode.id(), myNode);
			_myElementList.add(myNode);
		}
	}

	/**
	 * 
	 * @return all nodes that has been found in the xml-File
	 */
	Collection<CCColladaSceneNode> nodes() {
		return values();
	}

}
