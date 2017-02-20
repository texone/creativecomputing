/*  
 * Copyright (c) 2011 Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.controlui.timeline.controller.track;

import java.awt.geom.Point2D;

import cc.creativecomputing.control.timeline.Track;
import cc.creativecomputing.control.timeline.point.BezierControlPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint.ControlPointType;
import cc.creativecomputing.control.timeline.point.HandleControlPoint;
import cc.creativecomputing.controlui.timeline.controller.CurveToolController;
import cc.creativecomputing.controlui.timeline.controller.TrackContext;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCMath;

/**
 * @author christianriekoff
 *
 */
public abstract class CurveTrackController extends TrackController{

	protected CurveToolController _myCurveTool;
	/**
	 * @param theTimelineController
	 * @param theTrack
	 * @param theParent
	 */
	public CurveTrackController(
		TrackContext theTrackContext, 
		CurveToolController theCurveTool, 
		Track theTrack, 
		GroupTrackController theParent
	) {
		super(theTrackContext, theCurveTool, theTrack, theParent);
		_myCurveTool = theCurveTool;
		
		if(theTrack.property() == null)return;
		
		theTrack.property().events().add(theValue -> {
			for(ControlPoint myPoint:_mySelectedPoints){
				applyValue(myPoint, null);
			}
			_myTrackView.render();
			}
		);
	}
	
	public abstract void applyValue(ControlPoint thePoint, Object theValue);

	/* (non-Javadoc)
     * @see de.artcom.timeline.controller.TrackDataController#createPoint(de.artcom.timeline.model.points.ControlPoint)
     */
    @Override
    public abstract ControlPoint createPointImpl(ControlPoint theCurveCoords);
    
    private void moveOppositeHandle(HandleControlPoint theMovedHandle, HandleControlPoint theHandleToMove) {
    	ControlPoint myCenter = theMovedHandle.parent();
    	Point2D myPoint = new Point2D.Double(
    		theMovedHandle.time() - myCenter.time(), 
    		theMovedHandle.value() - myCenter.value()
    	);
    	theHandleToMove.time(myCenter.time() -  myPoint.getX());
    	theHandleToMove.value(Math.max(0, Math.min(myCenter.value() -  myPoint.getY(),1)));
    }
    
    /* (non-Javadoc)
     * @see de.artcom.timeline.controller.TrackDataController#dragPointImp(java.awt.geom.Point2D, boolean)
     */
    @Override
    public void dragPointImp(ControlPoint theDraggedPoint, ControlPoint myTargetPosition, ControlPoint theMovement, boolean theIsPressedShift) {
    	if (theDraggedPoint.getType().equals(ControlPointType.HANDLE)) {
            // first get next point:
        	HandleControlPoint myHandle = (HandleControlPoint)theDraggedPoint;
            ControlPoint myParent = ((HandleControlPoint)theDraggedPoint).parent();
//            ControlPoint myCurveCoords = myTargetPosition;
            ControlPoint myPreviousPoint = myParent.getPrevious();
            
            switch (myHandle.handleType()) {
			case BEZIER_IN_HANDLE:
				if (myPreviousPoint == null)return;
				ControlPoint myPoint = _myTrackContext.quantize(myTargetPosition);
				
				double time = CCMath.min(myParent.time(), myPoint.time());
				
//				if(myPreviousPoint.getType() == ControlPointType.BEZIER) {
//					HandleControlPoint myOutHandle = ((BezierControlPoint)myPreviousPoint).outHandle();
//					time = CCMath.max(time, myOutHandle.getTime());
//				}else {
					time = CCMath.max(myPoint.time(), myPreviousPoint.time());
//				}
				
				theDraggedPoint.time(CCMath.constrain(myPoint.time(), myPreviousPoint.time(), myParent.time()));
				theDraggedPoint.value(myPoint.value());
				
				if(theIsPressedShift) {
					HandleControlPoint myOutHandle = ((BezierControlPoint)myParent).outHandle();
					moveOppositeHandle(myHandle,myOutHandle);
				}
				
				break;
			case BEZIER_OUT_HANDLE:
				myPoint = _myTrackContext.quantize(myTargetPosition);
				
				time = CCMath.max(myParent.time(), myPoint.time());
				
				ControlPoint myNextPoint = myParent.getNext();
				
				if(myNextPoint != null) {
//					if(myNextPoint.getType() == ControlPointType.BEZIER) {
//						HandleControlPoint myInHandle = ((BezierControlPoint)myNextPoint).inHandle();
//						time = CCMath.min(time, myInHandle.getTime());
//					}else {
						time = CCMath.min(time, myNextPoint.time());
//					}
				}
				
				theDraggedPoint.time(time);
				theDraggedPoint.value(myPoint.value());
				
				if(theIsPressedShift) {
					HandleControlPoint myInHandle = ((BezierControlPoint)myParent).inHandle();
					moveOppositeHandle(myHandle,myInHandle);
				}
				break;
			default:
				break;
			}
            trackData().move(myParent,myParent);
        } else {
            ControlPoint myPoint = _myTrackContext.quantize(myTargetPosition);
            
            double myValueChange = myPoint.value() - theDraggedPoint.value();
            
            trackData().move(theDraggedPoint, _myTrackContext.quantize(myPoint));

            switch(theDraggedPoint.getType()) {
            case BEZIER:
            	BezierControlPoint myBezierPoint = (BezierControlPoint)theDraggedPoint;
            	myBezierPoint.inHandle().value(myBezierPoint.inHandle().value() + myValueChange);
            	myBezierPoint.outHandle().value(myBezierPoint.outHandle().value() + myValueChange);
            	break;
			default:
				break;
            }
           if(_myTrack.property() == null)return;
           
            _myTrack.property().fromNormalizedValue(myPoint.value(), false);
            viewValue(_myTrack.property().valueString());
        }
    }
}
