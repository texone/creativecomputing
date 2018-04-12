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
package cc.creativecomputing.controlui.timeline.controller.track;

import cc.creativecomputing.control.timeline.CCTrack;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.controlui.timeline.controller.CCTrackContext;
import cc.creativecomputing.controlui.timeline.tools.CCBlendableCurveTool;
import cc.creativecomputing.controlui.timeline.tools.CCBlendableTool;
import cc.creativecomputing.core.CCBlendable;

@SuppressWarnings({"unchecked"})
public abstract class CCBlendableTrackController<Type extends CCBlendable<Type>> extends CCDoubleTrackController{
	

	public CCBlendableTrackController(CCTrackContext theTrackContext, CCTrack theTrack, CCGroupTrackController theParent) {
		super(theTrackContext, theTrack, theParent);
		_myCreateTool = new CCBlendableTool<>(this);
		_myCurveTool = new CCBlendableCurveTool(this);
		_myActiveTool = _myCreateTool;
	}
	
	@Override
	public void applyValue(CCControlPoint thePoint, Object theValue) {
		thePoint.blendable((Type)_myProperty.value());
	}

//	@Override
//	public ControlPoint createPointImpl(ControlPoint theCurveCoords) {
//		ControlPoint myResult = super.createPointImpl(theCurveCoords);
//		myResult.blendable((Type)_myProperty.value());
//		return myResult;
//	}
	
	public abstract Type createDefault();
	
	
	
	public Type blend(double theTime){
		if (trackData().size() == 0) {
			return createDefault();
		}
		
		CCControlPoint mySample = trackData().createSamplePoint(theTime);
		CCControlPoint myLower = trackData().lower(mySample);
		CCControlPoint myCeiling = trackData().ceiling(mySample);
		
		if(myLower != null)myLower.value(0);
		if(myCeiling != null)myCeiling.value(1);

		if (myLower == null && myCeiling == null) {
			return createDefault();
		}

		if (myLower == null && myCeiling != null) {
			return ((Type)myCeiling.blendable()).clone();
		}
		
		if(myCeiling == null && myLower != null){
			return ((Type)myLower.blendable()).clone();
		}

		myLower = trackData().getLastOnSamePosition(myLower);
		
		double myBlend = myCeiling.interpolateValue(theTime, trackData());
		Type blend0 = (Type)myLower.blendable();
		Type blend1 = (Type)myCeiling.blendable();
		return blend0.blend(blend1, myBlend);
		
	}

	@Override
	public void timeImplementation(double theTime, double theValue) {
		_myProperty.valueCasted(blend(theTime), false);
	}

}
