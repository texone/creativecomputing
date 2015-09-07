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

import java.util.List;

import cc.creativecomputing.io.xml.CCXMLElement;

/**
 * Provides a library in which to place <visual_scene> elements.
 * @author christianriekoff
 *
 */
public class CCColladaScenes extends CCColladaLibrary<CCColladaScene>{

	CCColladaScenes(CCColladaLoader theLoader, List<CCXMLElement> theScenesXML) {
		
		for (CCXMLElement mySceneXML : theScenesXML) {
			CCColladaScene myScene = new CCColladaScene(theLoader, mySceneXML);
			_myElementMap.put(myScene.id(), myScene);
			_myElementList.add(myScene);
		}
	}
}
