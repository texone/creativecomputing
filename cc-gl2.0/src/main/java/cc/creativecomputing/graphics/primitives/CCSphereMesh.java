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
package cc.creativecomputing.graphics.primitives;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

public class CCSphereMesh extends CCVBOMesh{
	
	private CCVector3 _myCenter;
	private double _myRadius;
	private int _myResolution;
	
	public CCSphereMesh(final CCVector3 theCenter, final double theRadius, final int theResolution){
		super(CCDrawMode.TRIANGLES, ((theResolution/2 + 1) * theResolution));
		_myCenter = theCenter;
		_myRadius = theRadius;
		_myResolution = theResolution;
		
		_myResolution = CCMath.max(_myResolution, 4);
		
		prepareNormalData();
		
		createSphere(theResolution/2, theResolution);
	}
	
	public CCSphereMesh(final double theRadius, final int theResolution){
		this(new CCVector3(), theRadius, theResolution);
	}
	
	private void createSphere(int myPointRows, int myPointsPerRow){
		List<Integer> myIndices = new ArrayList<Integer>();  

		int i,j;
		double x,y,z;
		
		double myTheta;
		double myPhi;

		for (i = 0; i <= myPointRows; i++){
			for (j = 0; j < myPointsPerRow; j++){
				myTheta = (double)i / (double)(myPointRows);
				myPhi = (double)j / (double)(myPointsPerRow - 1);
				
				x = CCMath.sin(myTheta * CCMath.PI) * CCMath.cos(myPhi * CCMath.TWO_PI);
				y = CCMath.sin(myTheta * CCMath.PI) * CCMath.sin(myPhi * CCMath.TWO_PI);
				z = CCMath.cos(myTheta * CCMath.PI);
				
				addNormal(x,y,z);
				addVertex(
//					_myRadius * x + _myCenter.x(),
//					_myRadius * y + _myCenter.y(),
//					_myRadius * z + _myCenter.z()
					_myRadius * x,
					_myRadius * y,
					_myRadius * z
				
				);
				addTextureCoords(myPhi,myTheta);
			}
		}

		//create the index array:
		for (i = 1; i <= myPointRows; i++){
			for (j = 0; j < (myPointsPerRow-1); j++){
				myIndices.add((i-1) * myPointsPerRow + j);
				myIndices.add(i     * myPointsPerRow + j);
				myIndices.add((i-1) * myPointsPerRow + j + 1);

				myIndices.add((i-1) * myPointsPerRow +j + 1);
				myIndices.add((i)   * myPointsPerRow +j);
				myIndices.add((i)   * myPointsPerRow +j + 1);
			}
		}
		indices(myIndices);
	}
	
	public CCVector3 center() {
		return _myCenter;
	}
	
	public void draw(CCGraphics g){
		
		g.pushMatrix();
		g.translate(_myCenter);
		super.draw(g);
//		g.ellipse(0,0,0, 100);
		g.popMatrix();
	}
}
