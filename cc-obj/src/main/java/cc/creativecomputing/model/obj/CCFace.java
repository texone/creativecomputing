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
package cc.creativecomputing.model.obj;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;


/**
 * the face class saves the indices of the vertices that define
 * a 3d polygon. you can look at the size of the face, and query for 
 * the index of a vertex or the vertex directly.
 * @author texone
 *
 */
public class CCFace {

	public static int LINE=0;
	public static int POLYGON=1;

	public int iType=POLYGON;
	
	private List<Integer> _myVertexIndices;
	private List<Integer> _myTextureIndices;
	private List<Integer> _myNormalIndices;
	
	private final CCModel _myParentModel;

	public CCFace(final CCModel theParentModel) {
		_myParentModel = theParentModel;
		_myVertexIndices = new ArrayList<Integer>();
		_myTextureIndices = new ArrayList<Integer>();
		_myNormalIndices = new ArrayList<Integer>();
	}

	public int size(){
		return _myVertexIndices.size();
	}
	
	/**
	 * Returns the 
	 * @param theIndex
	 * @return
	 */
	public int vertexIndex(final int theIndex){
		return _myVertexIndices.get(theIndex);
	}
	
	public CCVector3 vertex(final int theIndex){
		return _myParentModel.vertices().get(_myVertexIndices.get(theIndex));
	}
	
	public int textureIndex(final int theIndex){
		return _myTextureIndices.get(theIndex);		
	}
	
	public CCVector2 textureCoords(final int theIndex){
		return _myParentModel.textureCoords().get(_myTextureIndices.get(theIndex));
	}
	
	public int normalIndex(final int theIndex){
		return _myNormalIndices.get(theIndex);
	}
	
	public CCVector3 normal(final int theIndex){
		return _myParentModel.normals().get(_myNormalIndices.get(theIndex));
	}

	public List<Integer> vertexIndices() {
		return _myVertexIndices;
	}

	public List<Integer> textureIndices() {
		return _myTextureIndices;
	}

	public List<Integer> normalIndices() {
		return _myNormalIndices;
	}
}
