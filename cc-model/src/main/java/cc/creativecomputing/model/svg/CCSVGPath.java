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
package cc.creativecomputing.model.svg;

import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCLinearSpline;

public class CCSVGPath extends CCSVGElement{
	
	GeneralPath _myPath;

	private List<CCLinearSpline>_myContours = null;
	private float _myFlatness = 1;

	public CCSVGPath(CCSVGGroup theParent) {
		super(theParent);
	}
	
	public GeneralPath path(){
		return _myPath;
	}
	
	/**
	 * _myFlatness
	 * @param theFlatness
	 */
	public void flatness(float theFlatness){
		_myFlatness = theFlatness;
	}
	
	private void createContours(){
		_myContours = new ArrayList<>();
		if(_myPath == null)return;
		
		PathIterator myIterator = _myPath.getPathIterator(null, _myFlatness);
		
		CCLinearSpline myContour = null;
		float[] myCoords = new float[2];
		while(!myIterator.isDone()){
			int mySegmentType = myIterator.currentSegment(myCoords);// +";"+myCoords[0]+";"+myCoords[1]
			if(mySegmentType == PathIterator.SEG_MOVETO){
				if(myContour != null){
					myContour.endEditSpline();
					_myContours.add(myContour);
				}
				myContour = new CCLinearSpline(false);
				myContour.beginEditSpline();
			}
			myContour.addPoint(new CCVector3(myCoords[0],myCoords[1],0));
			if(mySegmentType == PathIterator.SEG_CLOSE){
				myContour.endEditSpline();
				_myContours.add(myContour);
				myContour = null;
			}
			
//			_myVectors.add();
			myIterator.next();
		}
		if(myContour == null)return;
		
		myContour.endEditSpline();
		_myContours.add(myContour);
	}
	
	public List<CCLinearSpline> contours(){
		if(_myContours == null){
			createContours();
		}
		return _myContours;
	}

}
