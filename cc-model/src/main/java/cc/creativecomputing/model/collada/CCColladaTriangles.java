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

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.io.xml.CCDataElement;

/**
 * Provides the information needed to for a mesh to bind vertex attributes together and then organize 
 * those vertices into individual triangles.
 * <p>
 * The <triangles> element declares the binding of geometric primitives and vertex attributes for a <mesh> element.
 * The vertex array information is supplied in distinct attribute arrays that are then indexed by the <triangles> element.
 * <p>
 * Each triangle described by the mesh has three vertices. The first triangle is formed from the first, second, and third 
 * vertices. The second triangle is formed from the fourth, fifth, and sixth vertices, and so on.
 * 
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @author christianriekoff
 * @version 1.0
 */
public class CCColladaTriangles extends CCColladaGeometryData {
	

	CCColladaTriangles(CCDataElement triangles, HashMap<String, CCColladaSource> theSources, CCColladaVertices theVertices) {
		super(triangles, theSources, theVertices, 3, CCDrawMode.TRIANGLES);
	}

}
