/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
package cc.creativecomputing.simulation.particles.forces;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCTesselator;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCVector2;

/**
 * @author christian riekoff
 *
 */
public class CCPathFollow extends CCTextureForceField{
	
	public static class CCPathFollowPath{
		
		private List<CCVector2> _myPoints = new ArrayList<CCVector2>();
		private List<CCVector2> _myDirections = new ArrayList<CCVector2>(); 
		
		private float _myContourForce = 0.1f;
		private float _myAreaForce = 1f;
		
		private boolean _myHasChanged = true;
		
		private float _myContourWeight = 1f;
		
		public CCPathFollowPath() {
		}
		
		public void clear() {
			_myPoints.clear();
			_myDirections.clear();
			
			_myHasChanged = true;
		}
		
		public void contourWeight(float theContourWeight){
			_myContourWeight = theContourWeight;
		}
		
		public void contourForce(final float theContourForce) {
			_myContourForce = theContourForce;
		}
		
		public void areaForce(final float theAreaForce) {
			_myAreaForce = theAreaForce;
		}
		
		public void addPoint(final CCVector2 thePoint) {
			_myPoints.add(thePoint);
		}
		
		public void draw(CCTesselator theTesselator) {
			if(_myHasChanged) {
				for(int i = 0; i < _myPoints.size() - 1;i++) {
					CCVector2 myPoint1 = _myPoints.get(i);
					CCVector2 myPoint2 = _myPoints.get(i + 1);
					CCVector2 myDirection  = myPoint2.subtract(myPoint1).normalizeLocal();
					
					_myDirections.add(myDirection);
				}
				_myDirections.add(_myDirections.get(_myDirections.size()-1).clone());
			}
			
			
			theTesselator.beginContour();
			for(int i = 0; i < _myPoints.size();i++) {
				CCVector2 myDirection = _myDirections.get(i);
				CCVector2 myPoint = _myPoints.get(i);
				
				theTesselator.normal(myDirection.y * _myAreaForce, -myDirection.x * _myAreaForce, 0);
				theTesselator.vertex(myPoint.x - myDirection.y, myPoint.y + myDirection.x);
			}
			for(int i = _myPoints.size() - 1; i >= 0;i--) {
				CCVector2 myDirection = _myDirections.get(i);
				CCVector2 myPoint = _myPoints.get(i);
				
				theTesselator.normal(-myDirection.y * _myAreaForce, myDirection.x * _myAreaForce, 0);
				theTesselator.vertex(myPoint.x + myDirection.y, myPoint.y - myDirection.x);
			}
			theTesselator.endContour();
		}
		
		public void drawContour(CCGraphics g) {
			g.color(255,0,0);
			g.beginShape(CCDrawMode.TRIANGLE_STRIP);
			for(int i = 0; i < _myPoints.size();i++) {
				CCVector2 myDirection = _myDirections.get(i);
				CCVector2 myPoint = _myPoints.get(i);
				
				g.normal(myDirection.x * _myContourForce, myDirection.y * _myContourForce, 0);
				g.vertex(myPoint.x - myDirection.y * _myContourWeight, myPoint.y + myDirection.x * _myContourWeight);
				g.vertex(myPoint.x + myDirection.y * _myContourWeight, myPoint.y - myDirection.x * _myContourWeight);
			}
			g.endShape();
		}
	}
	
	private CCShaderBuffer _myPathForceFieldTexture;
	
	private CCGLProgram _myContourShader;
	
	private CCTesselator _myTesselator;
	
	private List<CCPathFollowPath> _myPaths = new ArrayList<>();

	/**
	 * @param theTexture
	 * @param theTextureScale
	 * @param theTextureOffset
	 */
	public CCPathFollow(int theWidth, int theHeight, CCVector2 theTextureScale, CCVector2 theTextureOffset) {
		super(null, theTextureScale, theTextureOffset);
		_myPathForceFieldTexture = new CCShaderBuffer(theWidth, theHeight);
		_myTexture = _myPathForceFieldTexture.attachment(0);
		
		_myContourShader = new CCGLProgram(
			CCNIOUtil.classPath(this,"CCPathFollowContourVertex.glsl"), 
			CCNIOUtil.classPath(this,"CCPathFollowContourFragment.glsl")
		);
		
		_myTesselator = new CCTesselator();
	}
	
	public CCPathFollow( int theWidth, int theHeight){
		this(theWidth, theHeight, new CCVector2(1f,1f), new CCVector2(theWidth / 2, theHeight / 2));
	}
	
	public void addPath(CCPathFollowPath thePath){
		_myPaths.add(thePath);
	}
	
	@Override
	public void preDisplay(CCGraphics g) {
		_myPathForceFieldTexture.beginDraw(g);
		g.clearColor(0);
		g.clear();
		_myContourShader.start();
		_myTesselator.beginPolygon();
		_myTesselator.beginContour();
		for (int i = 400; i >= 0; i -= 5) {
			_myTesselator.normal(0, 1, 0);
			_myTesselator.vertex(i, 0);
		}
//		for (int i = 0; i <= 400; i += 50) {
//			_myTesselator.normal(0, -1, 0);
//			_myTesselator.vertex(0, i);
//		}
		for (int i = 0; i <= 400; i += 5) {
			_myTesselator.normal(0, -1, 0);
			_myTesselator.vertex(i, 400);
		}
//		for (int i = 400; i >= 0; i -= 5) {
//			_myTesselator.normal(0, 1, 0);
//			_myTesselator.vertex(0, i);
//		}
		_myTesselator.endContour();
		for(CCPathFollowPath myPath:_myPaths){
			_myTesselator.beginContour();
			myPath.draw(_myTesselator);
			_myTesselator.endContour();
		}
		_myTesselator.endPolygon();
		for(CCPathFollowPath myPath:_myPaths){
			myPath.drawContour(g);
		}
		
		_myContourShader.end();
		_myPathForceFieldTexture.endDraw(g);
	}
}
