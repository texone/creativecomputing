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

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.control.timeline.TrackData;
import cc.creativecomputing.control.timeline.point.BezierControlPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint.ControlPointType;
import cc.creativecomputing.control.timeline.point.HandleControlPoint;
import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.controlui.timeline.controller.CCZoomable;
import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.controller.TrackContext;
import cc.creativecomputing.controlui.timeline.controller.actions.RemoveControlPointAction;
import cc.creativecomputing.controlui.timeline.view.TimedContentView;
import cc.creativecomputing.controlui.timeline.view.track.SwingAbstractTrackView;
import cc.creativecomputing.controlui.timeline.view.track.SwingTrackView;
import cc.creativecomputing.controlui.util.UndoHistory;


/**
 * @author christianriekoff
 *
 */
public abstract class CCTrackDataController implements CCZoomable, TimedContentView{

	protected SwingAbstractTrackView _myTrackView;
    
    protected TrackContext _myTrackContext;
    
    protected TimelineController _myTimelineController;
    
    protected CCPropertyHandle<?> _myProperty;
	
	public CCTrackDataController(
		TrackContext theTrackContext, 
		CCPropertyHandle<?> theProperty
	) {
		_myTrackContext = theTrackContext;
		_myProperty = theProperty;
		if(_myTrackContext instanceof TimelineController)_myTimelineController = (TimelineController)theTrackContext;
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
    	_mySelectedPoints.clear();
    	trackData().clear();
    	_myTrackView.render();
    }
    
    public void clearSelection(){
    	for(ControlPoint myPoint:_mySelectedPoints){
    		myPoint.setSelected(false);
    	}
    	_mySelectedPoints.clear();
    	_myTrackView.render();
    }
    
    public void deleteSelection(){
    	for(ControlPoint myPoint:selectedPoints()){
			trackData().remove(myPoint);
		}
		clearSelection();
		view().render();
    }
    
    public List<ControlPoint> copySelection(){
    	List<ControlPoint> clipBoard = new ArrayList<>();
		for(ControlPoint myPoint:selectedPoints()){
			clipBoard.add(myPoint.clone());
		}
		return clipBoard;
    }
    
    public List<ControlPoint> cutSelection(){
    	List<ControlPoint> clipBoard = new ArrayList<>();
		for(ControlPoint myPoint:selectedPoints()){
			trackData().remove(myPoint);
			clipBoard.add(myPoint.clone());
		}
		clearSelection();
		view().render();
		return clipBoard;
    }
    
    public void paste(List<ControlPoint> thePoints, double theTime){
    	if(thePoints == null)return;
		if(thePoints.size() < 1)return;
		ControlPoint myFirstPoint = thePoints.get(0);
		for(ControlPoint myPoint:thePoints){
			ControlPoint myCopy = myPoint.clone();
			myCopy.time(myCopy.time() - myFirstPoint.time() + theTime);
			trackData().add(myCopy);
		}
		view().render();
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
    
    public void removePoint(Point2D theViewCoords) {
    	ControlPoint myNearestPoint = pickNearestPoint(theViewCoords);
    	if(myNearestPoint == null)return;
    	trackData().remove(myNearestPoint);
        _myTrackView.render();
    	UndoHistory.instance().apply(new RemoveControlPointAction(this, myNearestPoint));
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

    protected List<ControlPoint> _mySelectedPoints = new ArrayList<>();
    
    public void deactivate(){
    	for(ControlPoint myPoint:_mySelectedPoints){
    		myPoint.setSelected(false);
    	}
    	_mySelectedPoints.clear();
        _myTrackView.render();
    }
    
    public List<ControlPoint> selectedPoints(){
    	return _mySelectedPoints;
    }

    
    
//    public double distance(ControlPoint theNearest, Point2D theViewCoords) {
//    	if(this instanceof EventTrackController || this instanceof CCBlendableTrackController)
//    		return Math.abs(curveToViewSpace(theNearest).getX() - theViewCoords.getX());
//    	else 
//    		return curveToViewSpace(theNearest).distance(theViewCoords);
//    }
	
	public double distance(ControlPoint theNearest, Point2D theViewCoords) {
    	if(this instanceof CCEventTrackController || this instanceof CCBlendableTrackController)
    		return Math.abs(curveToViewSpace(theNearest).getX() - theViewCoords.getX());
    	else 
    		return curveToViewSpace(theNearest).distance(theViewCoords);
    }
}
