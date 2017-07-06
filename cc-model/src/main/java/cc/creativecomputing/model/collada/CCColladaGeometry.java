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
import java.util.HashSet;
import java.util.List;

import cc.creativecomputing.io.xml.CCDataElement;

/**
 * Describes the visual shape and appearance of an object in a scene.
 * <p>
 * The <geometry> element categorizes the declaration of geometric information. Geometry is a branch 
 * of mathematics that deals with the measurement, properties, and relationships of points, lines, 
 * angles, surfaces, and solids. The <geometry> element contains a declaration of a mesh, convex mesh, or spline.
 * <p>
 * There are many forms of geometric description. Computer graphics hardware has been normalized, primarily, 
 * to accept vertex position information with varying degrees of attribution (color, normals, etc.). 
 * Geometric descriptions provide this vertex data with relative directness or efficiency. Some of the more 
 * common forms of geometry are:
 * <ul>
 * <li>B-Spline</li>
 * <li>Bezier</li>
 * <li>Mesh</li>
 * <li>NURBS</li>
 * <li>Patch</li>
 * </ul>
 * This is by no means an exhaustive list. Currently, COLLADA supports only polygonal meshes and splines.
 * 
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @author christianriekoff
 * @version 1.0
 */
public class CCColladaGeometry extends CCColladaElement{

	private List<CCColladaTriangles> _myTriangles;
	private List<CCColladaPolyList> _myPolyList;
	private List<CCColladaGeometryData> _myDatas = new ArrayList<>();
	private HashSet<CCColladaLines> _myLinesSet;

	CCColladaGeometry(CCDataElement theGeometryXML) {
		super(theGeometryXML);

		CCDataElement myMeshXML = theGeometryXML.child("mesh");

		// dump the source-Tags
		HashMap<String, CCColladaSource> mySourceMap = new HashMap<String, CCColladaSource>();
		for (CCDataElement mySourceXML : myMeshXML.children("source")) {
			CCColladaSource mySource = new CCColladaSource(mySourceXML);
			mySourceMap.put(mySource.id(), mySource);
		}
		// dump the vertice-tag to the sources
		CCColladaVertices myVertices = new CCColladaVertices(myMeshXML.child("vertices"));

		List<CCDataElement> myShapes;

		// dump triangles
		if ((myShapes = myMeshXML.children("triangles")).size() != 0) {
			_myTriangles = new ArrayList<CCColladaTriangles>();
			for (CCDataElement myTrianglesXML : myShapes) {
				CCColladaTriangles myTriangles = new CCColladaTriangles(myTrianglesXML, mySourceMap, myVertices);
				_myTriangles.add(myTriangles);
				_myDatas.add(myTriangles);
			}
		}
		if ((myShapes = myMeshXML.children("polylist")).size() != 0) {
			_myPolyList = new ArrayList<>();
			for (CCDataElement myTrianglesXML : myShapes) {
				CCColladaPolyList myTriangles = new CCColladaPolyList(myTrianglesXML, mySourceMap, myVertices);
				_myPolyList.add(myTriangles);
				_myDatas.add(myTriangles);
			}
		}
		// dump lines
		if ((myShapes = myMeshXML.children("lines")).size() != 0) {
			_myLinesSet = new HashSet<CCColladaLines>();
			for (CCDataElement myLineXML : myShapes) {
				CCColladaLines myLines = new CCColladaLines(myLineXML, mySourceMap, myVertices);
				_myLinesSet.add(myLines);
				_myDatas.add(myLines);
			}

		}
	}
	
	CCColladaGeometryData geometryData(int theIndex){
		return _myDatas.get(0);
	}

	/**
	 * sets all to the Lines and Triangles its materials
	 * 
	 * @param theMaterialLib
	 * @param theBindMap in the format Hashmap&lt;materialSymbol, materialID&gt;
	 */
	void bindMaterials(CCColladaMaterials theMaterialLib, HashMap<String, String> theBindMap) {
		if (_myTriangles != null) {
			for (CCColladaTriangles myTriangles : _myTriangles) {
				String matSymbol = myTriangles.source();
				String matID = theBindMap.get(matSymbol);
				CCColladaMaterial m = theMaterialLib.element(matID);
				myTriangles.material(m);
			}
		}
		if (_myLinesSet != null) {
			for (CCColladaLines lines : _myLinesSet) {
				String matSymbol = lines.source();
				String matID = theBindMap.get(matSymbol);
				CCColladaMaterial m = theMaterialLib.element(matID);
				lines.material(m);
			}
		}

	}

	/**
	 * returns if any triangles found in xml-File, null if not
	 * @see CCColladaTriangles
	 * @return
	 */
	public List<CCColladaTriangles> triangles() {
		return _myTriangles;
	}
	
	public List<CCColladaGeometryData> data(){
		return _myDatas;
	}

	/**
	 * returns if any lines found in xml-File, null if not
	 * 
	 * @return
	 */
	HashSet<CCColladaLines> lines() {
		return _myLinesSet;
	}

	@Override
	public String toString() {

		String s = "";
		s += "Geometry " + _myID + " contains: \n";
		if (_myTriangles != null)
			for (CCColladaTriangles t : _myTriangles)
				s += "a set of " + t + "\n";
		if (_myLinesSet != null)
			for (CCColladaLines t : _myLinesSet)
				s += "a set of " + t + "\n";
		return s;

	}

}
