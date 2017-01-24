/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
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

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.control.timeline.TrackData;
import cc.creativecomputing.control.timeline.point.BezierControlPoint;
import cc.creativecomputing.control.timeline.point.ColorPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint.ControlPointType;
import cc.creativecomputing.control.timeline.point.ControlPoint.HandleType;
import cc.creativecomputing.control.timeline.point.HandleControlPoint;
import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.controlui.timeline.controller.TimelineTool;
import cc.creativecomputing.controlui.timeline.controller.ToolController;
import cc.creativecomputing.controlui.timeline.controller.TrackContext;
import cc.creativecomputing.controlui.timeline.controller.Zoomable;
import cc.creativecomputing.controlui.timeline.controller.actions.AddControlPointAction;
import cc.creativecomputing.controlui.timeline.controller.actions.MoveControlPointAction;
import cc.creativecomputing.controlui.timeline.controller.actions.RemoveControlPointAction;
import cc.creativecomputing.controlui.timeline.view.TimedContentView;
import cc.creativecomputing.controlui.timeline.view.track.SwingAbstractTrackView;
import cc.creativecomputing.controlui.timeline.view.track.SwingTrackView;
import cc.creativecomputing.controlui.util.UndoHistory;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCMath;


/**
 * @author christianriekoff
 *
 */
public abstract class TrackDataController implements Zoomable, TimedContentView{
	
    protected int _myMouseStartX;
    protected int _myMouseStartY;
    protected boolean _myAddedNewPoint = false;

    protected List<ControlPoint> _myDraggedPoints;

	protected SwingAbstractTrackView _myTrackView;
    
    protected TrackContext _myTrackContext;
    
    protected CCPropertyHandle<?> _myProperty;
	
	public TrackDataController(
		TrackContext theTrackContext, 
		CCPropertyHandle<?> theProperty
	) {
		_myTrackContext = theTrackContext;
		_myProperty = theProperty;
	}
	
	public TrackContext context() {
		return _myTrackContext;
	}
	
	public void view(SwingAbstractTrackView theView) {
		_myTrackView = theView;
	}
	
	public SwingAbstractTrackView view() {
		return _myTrackView;
	}
	
	public abstract TrackData trackData();
	
	public CCPropertyHandle<?> property() {
		return _myProperty;
	}
    
    public void reset() {
    	trackData().clear();
    	_myTrackView.render();
    }
    
    @Override
    public double viewXToTime(int theViewX, boolean theGetPos) {
    	try{
        return (double) theViewX / (double) _myTrackView.width() * (_myTrackContext.viewTime()) + (theGetPos ? _myTrackContext.lowerBound() : 0);
    	}catch(Exception e){
    		return 0;
    	}
    }

    @Override
    public int timeToViewX(double theCurveX) {
        return (int) ((theCurveX - _myTrackContext.lowerBound()) / (_myTrackContext.viewTime()) * _myTrackView.width());
    }
    
//    public abstract double viewXToTime(int theViewX);
//
//    public abstract int timeToViewX(double theCurveX);

    public abstract Point2D curveToViewSpace(ControlPoint thePoint);
    
    public abstract ControlPoint viewToCurveSpace(Point2D thePoint, boolean theGetPos);
    
    @Override
    public void setRange(double theLowerBound, double theUpperBound) {

		CCLog.info(theLowerBound + " : " + theUpperBound + " " + _myTrackView);
        if(_myTrackView != null)_myTrackView.render();
    }
    
    protected double pickRange() {
    	return SwingTrackView.PICK_RADIUS / _myTrackView.width() * (_myTrackContext.viewTime());
    }
	
	// picks the nearest point (could be null) and returns it in view space
    public ControlPoint pickNearestPoint(Point2D theViewCoords) {
        ControlPoint myPickCoords = viewToCurveSpace(theViewCoords, true);
        double myPickRange = pickRange();
        
        if(trackData() == null)return null;
        
        ArrayList<ControlPoint> myPoints = trackData().rangeList(
        	myPickCoords.time()-myPickRange,
        	myPickCoords.time()+myPickRange
        );
        
        if (myPoints.size()==0) {
            return null;
        }
        
        Point2D myCurrentPoint = curveToViewSpace(myPoints.get(0));
        ControlPoint myNearest = myPoints.get(0);
        double myMinDistance = myCurrentPoint.distance(theViewCoords);
        for (ControlPoint myPoint : myPoints) {
            myCurrentPoint = curveToViewSpace(myPoint);
            double myDistance = myCurrentPoint.distance(theViewCoords);
            if (myDistance < myMinDistance) {
                myNearest = myPoint;
                myMinDistance = myDistance;
            }
        }
        return myNearest; 
    }
    
    protected boolean _myHasAdd = false;
    
    public abstract ControlPoint createPointImpl(ControlPoint theCurveCoords);
    
    /**
     * 
     * 
     * @param theViewCoords
     */
    protected ControlPoint createPoint(Point2D theViewCoords) {
        ControlPoint myControlPoint = viewToCurveSpace(theViewCoords, true);
        myControlPoint = createPointImpl(myControlPoint);
        	
        trackData().add(myControlPoint);
        _myHasAdd = true;
        _myTrackView.render();
        return myControlPoint;
    }
    
    private void removePoint(Point2D theViewCoords) {
    	ControlPoint myNearestPoint = pickNearestPoint(theViewCoords);
    	if(myNearestPoint == null)return;
    	trackData().remove(myNearestPoint);
        _myTrackView.render();
    	UndoHistory.instance().apply(new RemoveControlPointAction(this, myNearestPoint));
    }

    public boolean isDragging() {
        return _myDraggedPoints != null;
    }
    
    public HandleControlPoint pickHandle(Point2D theViewCoords) {
        ControlPoint myCurveCoords = viewToCurveSpace(theViewCoords, true);
        
        if(trackData() == null)return null;
        
        ControlPoint myNextPoint = trackData().ceiling(myCurveCoords);
        ControlPoint myPreviousPoint = trackData().lower(myCurveCoords);
        
        if(myNextPoint != null) {
	        switch(myNextPoint.getType()) {
	        case BEZIER:
	        	HandleControlPoint myInputHandle = ((BezierControlPoint)myNextPoint).inHandle();
	        	
	        	if (curveToViewSpace(myInputHandle).distance(theViewCoords) < SwingTrackView.PICK_RADIUS) {
	                return myInputHandle;
	            }
	        	break;
			default:
				break;
	        }
	        
	        myNextPoint = trackData().higher(myNextPoint);
	        if(myNextPoint != null && myNextPoint.getType() == ControlPointType.BEZIER) {
	        	HandleControlPoint myInputHandle = ((BezierControlPoint)myNextPoint).inHandle();
	        	if (curveToViewSpace(myInputHandle).distance(theViewCoords) < SwingTrackView.PICK_RADIUS) {
	                return myInputHandle;
	            }
	        }
        }
        
        
        if(myPreviousPoint != null) {
	        switch(myPreviousPoint.getType()) {
	        case BEZIER:
	        	HandleControlPoint myOutputHandle = ((BezierControlPoint)myPreviousPoint).outHandle();
	        	
	        	if (curveToViewSpace(myOutputHandle).distance(theViewCoords) < SwingTrackView.PICK_RADIUS) {
	                return myOutputHandle;
	            }
	        	break;
	        case TIMED_EVENT:
	        	HandleControlPoint myTimedEnd = ((TimedEventPoint)myPreviousPoint).endPoint();
	        	
	        	if (Math.abs(curveToViewSpace(myTimedEnd).getX() - theViewCoords.getX()) < SwingTrackView.PICK_RADIUS) {
	                return myTimedEnd;
	            }
	        	break;
	        case COLOR:
	        	HandleControlPoint myColorHandle = ((ColorPoint)myPreviousPoint).endPoint();
	        	
	        	if (Math.abs(curveToViewSpace(myColorHandle).getX() - theViewCoords.getX()) < SwingTrackView.PICK_RADIUS) {
	                return myColorHandle;
	            }
	        	break;
			default:
				break;
	        }
	        
	        myPreviousPoint = trackData().lower(myPreviousPoint);
	        if(myPreviousPoint == null || myPreviousPoint.getType() != ControlPointType.BEZIER) {
	        	return null;
	        }
	        
	        HandleControlPoint myOutputHandle = ((BezierControlPoint)myPreviousPoint).outHandle();
	    	if (curveToViewSpace(myOutputHandle).distance(theViewCoords) < SwingTrackView.PICK_RADIUS) {
	            return myOutputHandle;
	        }
        }
    	
        return null;
    }
    
    protected List<ControlPoint> _myStartPoints;
    
    public abstract void dragPointImp(ControlPoint theDraggedPoint, ControlPoint myTargetPosition, ControlPoint theMovement, boolean theIsPressedShift);


    public void endDrag() {
        if(_myHasAdd) {
            _myHasAdd = false;
            UndoHistory.instance().apply(new AddControlPointAction(this, _myDraggedPoints.get(0)));
        }else {
            if(_myDraggedPoints != null) {
            	UndoHistory.instance().apply(new MoveControlPointAction(this, _myDraggedPoints, _myStartPoints,  _myDraggedPoints));
            }
        }
        _myDraggedPoints = null;
    }
    
    public double distance(ControlPoint theNearest, Point2D theViewCoords) {
    	if(this instanceof EventTrackController || this instanceof ColorTrackController)
    		return Math.abs(curveToViewSpace(theNearest).getX() - theViewCoords.getX());
    	else 
    		return curveToViewSpace(theNearest).distance(theViewCoords);
    }
    
    private boolean _mySnap = false;
    
    private static int SnapRange = 10;
    
    private BezierControlPoint makeBezier(ControlPoint thePoint){
    	if(thePoint.getType() == ControlPointType.BEZIER)return (BezierControlPoint)thePoint;
    	trackData().remove(thePoint);
    	BezierControlPoint myResult = new BezierControlPoint(thePoint.time(), thePoint.value());
    	HandleControlPoint myInHandle = new HandleControlPoint(myResult,HandleType.BEZIER_IN_HANDLE, myResult.time(), myResult.value());
    	myResult.inHandle(myInHandle);
		HandleControlPoint myOutHandle = new HandleControlPoint(myResult,HandleType.BEZIER_OUT_HANDLE, myResult.time(), myResult.value());
		myResult.outHandle(myOutHandle);
    	trackData().add(myResult);
    	return myResult;
    }
    
    private BezierControlPoint _myFloorBezier;
    private BezierControlPoint _myCeilBezier;
    
    public void mousePressed(MouseEvent e, ToolController theToolController) {
		_myMouseStartX = e.getX();
		_myMouseStartY = e.getY();
		_mySnap = true;
		
		Point2D myViewCoords = new Point2D.Double(e.getX(), e.getY());
			
		if (e.isAltDown()) {
			_myTrackContext.zoomController().startDrag(myViewCoords);
			return;
		}
		
		ControlPoint myControlPoint = pickNearestPoint(myViewCoords);
		ControlPoint myHandle = pickHandle(myViewCoords);
		if (myHandle != null) {
			_myDraggedPoints = new ArrayList<ControlPoint>();
			_myDraggedPoints.add(myHandle);
		} else if (myControlPoint != null  && distance(myControlPoint, myViewCoords) < SwingTrackView.PICK_RADIUS){
			_myDraggedPoints = new ArrayList<ControlPoint>();
			_myDraggedPoints.add(myControlPoint);
		} else {
			ControlPoint myClick = viewToCurveSpace(myViewCoords, true);
			ControlPoint myFloor = trackData().floor(myClick);
			ControlPoint myCeil = trackData().ceiling(myClick);
			
			if(theToolController != null && theToolController.toolMode() == TimelineTool.CURVE){
				if(myFloor != null && myCeil != null){
					_myFloorBezier = makeBezier(myFloor);
					_myCeilBezier = makeBezier(myCeil);
				}else{
					_myFloorBezier = null;
					_myCeilBezier = null;
				}
			}else{
				if(e.getClickCount() == 2){
					_myAddedNewPoint = true;
					_myDraggedPoints = new ArrayList<ControlPoint>();
					_myDraggedPoints.add(createPoint(myViewCoords));
				}else{
					if(myFloor != null && myCeil != null){	
						_myDraggedPoints = new ArrayList<ControlPoint>();
						_myDraggedPoints.add(myFloor);
						_myDraggedPoints.add(myCeil);
					}
				}
			}
		}
		
		if(!_myHasAdd && _myDraggedPoints != null) {
			 _myStartPoints = new ArrayList<ControlPoint>();
			 for(ControlPoint myDraggedPoint:_myDraggedPoints){
				 _myStartPoints.add(myDraggedPoint.clone());
			 }
        }
	}
    
    public void startEdit(){
    	
    }
    
    public void endEdit(){
    	
    }

	public void mouseReleased(MouseEvent e, ToolController theToolController) {

		if (e.isAltDown()) {
			_myTrackContext.zoomController().endDrag();
			return;
		}
		
		if (e.getClickCount() == 2 && e.getX() == _myMouseStartX && e.getY() == _myMouseStartY && !_myAddedNewPoint) {
			Point2D myViewCoords = new Point2D.Double(e.getX(), e.getY());
			removePoint(myViewCoords);
		} else {
			endDrag();
		}

		_myAddedNewPoint = false;
	}
	
	private static float MAX_DRAG_X = 100;

	public void mouseDragged(MouseEvent e, ToolController theToolController) {
		
		if(e.isAltDown()) {
			_myTrackContext.zoomController().performDrag(new Point2D.Double(e.getX(), e.getY()), _myTrackView.width());
			return;
		}
		
//		int targetX = e.getX();
//		int targetY = e.getY();
		int myMovX = _myMouseStartX - e.getX();
		int myMovY = _myMouseStartY - e.getY();
		_mySnap = _mySnap && (CCMath.abs(myMovX) < SnapRange || CCMath.abs(myMovY) < SnapRange);
		
		if(_mySnap){
			if(CCMath.abs(myMovX) > CCMath.abs(myMovY)){
				myMovY = 0;
			}else{
				myMovX = 0;
			}
		}
		
		ControlPoint myCurveMovement;
		if(myMovY > 0){
			myCurveMovement = viewToCurveSpace(new Point2D.Double(-myMovX, myMovY), false);
			myCurveMovement.value(1 - myCurveMovement.value());
		}else{
			myCurveMovement = viewToCurveSpace(new Point2D.Double(-myMovX, -myMovY), false);
			myCurveMovement.value( myCurveMovement.value() - 1);
		}
		
		if(theToolController != null && theToolController.toolMode() == TimelineTool.CURVE && _myFloorBezier != null && _myCeilBezier != null){
			if(CCMath.abs(myMovX) > CCMath.abs(myMovY)){
				float myXBlend = CCMath.saturate(CCMath.norm(CCMath.abs(myMovX), 0, MAX_DRAG_X));
				if(myMovX < 0){
					_myFloorBezier.outHandle().value(_myFloorBezier.value());
					_myFloorBezier.outHandle().time(CCMath.blend(_myFloorBezier.time(), _myCeilBezier.time(), myXBlend));

					_myCeilBezier.inHandle().value(CCMath.blend(_myCeilBezier.value(), _myFloorBezier.value(), myXBlend));
					_myCeilBezier.inHandle().time(_myCeilBezier.time());
				}else{
					_myFloorBezier.outHandle().value(CCMath.blend(_myFloorBezier.value(), _myCeilBezier.value(), myXBlend));
					_myFloorBezier.outHandle().time(_myFloorBezier.time());

					_myCeilBezier.inHandle().value(_myCeilBezier.value());
					_myCeilBezier.inHandle().time(CCMath.blend(_myCeilBezier.time(), _myFloorBezier.time(), myXBlend));
				}
			}else{
				float myYBlend = (float)CCMath.saturate(CCMath.norm(CCMath.abs(myCurveMovement.value()), 0, 0.25));
				if(myCurveMovement.value() < 0){
					_myFloorBezier.outHandle().value(_myFloorBezier.value());
					_myFloorBezier.outHandle().time(CCMath.blend(_myFloorBezier.time(), _myCeilBezier.time(), myYBlend));

					_myCeilBezier.inHandle().value(_myCeilBezier.value());
					_myCeilBezier.inHandle().time(CCMath.blend(_myCeilBezier.time(), _myFloorBezier.time(), myYBlend));
				}else{
					_myFloorBezier.outHandle().value(CCMath.blend(_myFloorBezier.value(), _myCeilBezier.value(), myYBlend));
					_myFloorBezier.outHandle().time(_myFloorBezier.time());
					
					_myCeilBezier.inHandle().value(CCMath.blend(_myCeilBezier.value(), _myFloorBezier.value(), myYBlend));
					_myCeilBezier.inHandle().time(_myCeilBezier.time());

				}
			}
		}else{
			if(_myDraggedPoints == null)return;
			if(_myStartPoints == null)return;
			
			for(int i = 0; i < _myDraggedPoints.size();i++){
				ControlPoint myStartPoint = _myStartPoints.get(i).clone();
				ControlPoint myTarget = new ControlPoint(
					myStartPoint.time() + myCurveMovement.time(), 
					CCMath.saturate(myStartPoint.value() + myCurveMovement.value())
				);
				ControlPoint myDraggedPoint = _myDraggedPoints.get(i);
				
				boolean myPressedShift = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK;
	
		        dragPointImp(myDraggedPoint, myTarget, myCurveMovement, myPressedShift);
		        
			}
		}
        _myTrackView.render();
	}
	
	public ControlPoint draggedPoint(){
		if(_myDraggedPoints == null || _myDraggedPoints.size() == 0)return null;
		return _myDraggedPoints.get(0);
	}
	
	public void mouseMoved(MouseEvent e, ToolController theToolController) {
		Point2D myViewCoords = new Point2D.Double(e.getX(), e.getY());

		ControlPoint myNearest = pickNearestPoint(myViewCoords);
        ControlPoint myTensionHandle = pickHandle(myViewCoords);

        
		if (myNearest != null && distance(myNearest,myViewCoords) < SwingTrackView.PICK_RADIUS || myTensionHandle != null) {
			if(this instanceof EventTrackController){
				_myTrackView.moveRangeCursor();
			}else{
				_myTrackView.selectCursor();
			}
				
		} else {
			_myTrackView.defaultCursor();
		}
	}
}
