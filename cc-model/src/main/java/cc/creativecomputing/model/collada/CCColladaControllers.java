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

import cc.creativecomputing.io.xml.CCDataElement;

/**
 * Provides a library in which to place <controller> elements.
 * @author christianriekoff
 *
 */
public class CCColladaControllers extends CCColladaLibrary<CCColladaController>{

	CCColladaControllers(List<CCDataElement> theControllersXML, CCColladaGeometries theGeometries) {
		for (CCDataElement myControllerXML : theControllersXML) {
			CCColladaController myController = new CCColladaController(myControllerXML, theGeometries);
			_myElementMap.put(myController.id(), myController);
			_myElementList.add(myController);
		}

	}
}
