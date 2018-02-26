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
package cc.creativecomputing.control.handles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.io.data.CCDataArray;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCBezierSpline;
import cc.creativecomputing.math.spline.CCCatmulRomSpline;
import cc.creativecomputing.math.spline.CCLinearSpline;
import cc.creativecomputing.math.spline.CCSpline;

public class CCSplineHandle extends CCPropertyHandle<CCSpline>{
	
	
	protected CCSplineHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
	}
	
	@Override
	public CCDataObject data() {
		CCDataObject myResultData = super.data();
		CCSpline mySpline = value();
		if(mySpline instanceof CCBezierSpline){
			myResultData.put("type", "bezier");
		}else if(mySpline instanceof CCCatmulRomSpline){
			myResultData.put("type", "catmulrom");
			myResultData.put("curveTension", ((CCCatmulRomSpline)mySpline).curveTension());
		}else if(mySpline instanceof CCLinearSpline){
			myResultData.put("type", "linear");
		}
		myResultData.put("closed", mySpline.isClosed());
		CCDataArray myPointsData = myResultData.createArray("points");
		for(CCVector3 myPoint:mySpline.points()){
			CCDataObject myPointData = myPointsData.createObject();
			myPointData.put("x", myPoint.x);
			myPointData.put("y", myPoint.y);
			myPointData.put("z", myPoint.z);
		}

		CCLog.info("save:" + mySpline.points().size());
		return myResultData;
	}
	
	@Override
	public void data(CCDataObject theData) {
		
		CCDataArray myPointsData = theData.getArray("points");
		List<CCVector3> myPoints = new ArrayList<>();
		for(int i = 0; i < myPointsData.size();i++){
			CCDataObject myPointData = myPointsData.getObject(i);
			
			myPoints.add(
				new CCVector3(
					myPointData.getDouble("x"),
					myPointData.getDouble("y"),
					myPointData.getDouble("z")
				)
			);
		}
		CCSpline mySpline = value();
		CCLog.info("load:" + mySpline.points().size());
		mySpline.points().clear();
		mySpline.points().addAll(myPoints);
		mySpline.endEditSpline();
		CCLog.info("load:" + myPoints.size() + ":" + mySpline.points().size());
	}
	
	@Override
	public double normalizedValue() {
		return 0;
	}

	@Override
	public String valueString() {
		return null;
	}
}
