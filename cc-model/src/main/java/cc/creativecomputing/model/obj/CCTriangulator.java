package cc.creativecomputing.model.obj;

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


import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.CCVector4;

public class CCTriangulator{
	
	private List<CCVector3> _myVertices;
	private List<CCVector3> _myNormals;
	private List<CCColor> _myColors;
	private List<CCVector4> _myTextureCoords;

	public CCTriangulator() {
		super();
		_myVertices = new ArrayList<CCVector3>();
		_myNormals = new ArrayList<CCVector3>();
		_myTextureCoords = new ArrayList<CCVector4>();
		_myColors = new ArrayList<CCColor>();
	}

//	@Override
//	public void begin(int theMode) {
//	}
//
//	@Override
//	public void combineData(
//		double[] theCoords, Object[] theInputData,
//		float[] theWeight, Object[] theOutputData, Object theUserData
//	) {
//	}
//
//	@Override
//	public void edgeFlagData(final boolean theArg0, Object theData) {
//	}
//
//	@Override
//	public void end() {
//	}
//
//	@Override
//	public void errorData(final int theErrorNumber, Object theUserData) {
//	}
//
//	@Override
//	public void vertex(final Object theVertexData) {
//		if (theVertexData instanceof double[]) {
//			double[] myVertexData = (double[]) theVertexData;
//			
//			_myVertices.add(
//				new CCVector3(
//					(float)myVertexData[VERTEX_X],
//					(float)myVertexData[VERTEX_Y],
//					(float)myVertexData[VERTEX_Z]
//				)
//			);
//			
//			if(_myHasTextureData) {
//				_myTextureCoords.add(
//					new CCVector4(
//						(float)myVertexData[TEXTURE_S],
//						(float)myVertexData[TEXTURE_T],
//						(float)myVertexData[TEXTURE_R],
//						(float)myVertexData[TEXTURE_Q]
//					)
//				);
//			}
//			
//			if(_myHasNormalData) {
//				_myNormals.add(
//					new CCVector3(
//						(float)myVertexData[NORMAL_X],
//						(float)myVertexData[NORMAL_Y],
//						(float)myVertexData[NORMAL_Z]
//					)
//				);
//			}
//			
//			if(_myHasColorData) {
//				_myColors.add(
//					new CCColor(
//						(float)myVertexData[COLOR_R],
//						(float)myVertexData[COLOR_G],
//						(float)myVertexData[COLOR_B],
//						(float)myVertexData[COLOR_A]
//					)
//				);
//			}
//
//		} else {
//			throw new RuntimeException("TessCallback vertex() data not understood");
//		}
//	}
	
	public void addTriangleVertices(final CCVector3 the1, final CCVector3 the2, final CCVector3 the3){
		_myVertices.add(the1.clone());
		_myVertices.add(the2.clone());
		_myVertices.add(the3.clone());
	}
	
	public void addTriangleNormals(final CCVector3 the1, final CCVector3 the2, final CCVector3 the3){
		_myNormals.add(the1.clone());
		_myNormals.add(the2.clone());
		_myNormals.add(the3.clone());
	}
	
	public void addTriangleTextureCoords(final CCVector4 the1, final CCVector4 the2, final CCVector4 the3){
		_myTextureCoords.add(the1.clone());
		_myTextureCoords.add(the2.clone());
		_myTextureCoords.add(the3.clone());
	}
	
	public void addTriangleTextureCoords(final CCVector2 the1, final CCVector2 the2, final CCVector2 the3){
		_myTextureCoords.add(new CCVector4(the1));
		_myTextureCoords.add(new CCVector4(the2));
		_myTextureCoords.add(new CCVector4(the3));
	}
	
	public void addTriangleColors(final CCColor the1, final CCColor the2, final CCColor the3){
		_myColors.add(the1.clone());
		_myColors.add(the2.clone());
		_myColors.add(the3.clone());
	}

	public List<CCVector3> vertices(){
		return _myVertices;
	}

	public List<CCVector3> normals(){
		return _myNormals;
	}

	public List<CCVector4> textureCoords(){
		return _myTextureCoords;
	}

	public List<CCColor> colors(){
		return _myColors;
	}
	
	public void reset() {
		_myVertices = new ArrayList<CCVector3>();
		_myNormals = new ArrayList<CCVector3>();
		_myTextureCoords = new ArrayList<CCVector4>();
		_myColors = new ArrayList<CCColor>();
	}
}
