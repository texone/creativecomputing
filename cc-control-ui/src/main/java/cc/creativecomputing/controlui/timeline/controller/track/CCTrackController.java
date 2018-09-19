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

import cc.creativecomputing.control.timeline.CCSelection;
import cc.creativecomputing.control.timeline.CCTrack;
import cc.creativecomputing.control.timeline.CCTrackData;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCEventPoint;
import cc.creativecomputing.controlui.timeline.controller.CCTrackContext;
import cc.creativecomputing.controlui.timeline.tools.CCTimedContentView;
import cc.creativecomputing.controlui.timeline.tools.CCTimelineTool;
import cc.creativecomputing.controlui.timeline.tools.CCTimelineTools;
import cc.creativecomputing.gl.app.CCGLKeyEvent;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;


/**
 * @author christianriekoff
 *
 */
public abstract class CCTrackController extends CCTrackDataController implements CCTimedContentView{
	
	protected CCTrack _myTrack;
	
	private CCSelection _mySelection;
	
	protected CCGroupTrackController _myParent;
	
	protected CCTimelineTool _myActiveTool = null;
	
	protected boolean _myChangedValue = false;
	
	public CCTrackController(
		CCTrackContext theTrackContext, 
		CCTrack theTrack, 
		CCGroupTrackController theParent
	) {
		super(theTrackContext, theTrack.property());
		_myTrack = theTrack;
		_myParent = theParent;
	}
	
	public abstract CCTimelineTools[] tools();
	
	public abstract void setTool(CCTimelineTools theTool);
	
	public abstract CCTimelineTools activeTool();
	
	public CCControlPoint draggedPoint(){return null;}
	
	public boolean isParentOpen(){
		return _myParent == null || _myParent.isOpen();
	}
	
	public CCTrack track() {
		return _myTrack;
	}
	
	private void updateView(){
		if(_myTrackView == null)return;
		_myTrackView.mute(_myTrack.mute());
		_myTrackView.min(_myTrack.min());
		_myTrackView.max(_myTrack.max());
	}
	
	public void data(CCDataObject theData){
		_myTrack.data(theData);
		updateView();
	}
	
	public void trackData(CCTrack theTrack) {
		_myTrack = theTrack;
		updateView();
	}
	
	public void mute(boolean theIsMuted) {
		_myTrack.mute(theIsMuted);
		if(_myTrackView != null)_myTrackView.mute(theIsMuted);
	}
	
	public void min(double theMin) {
		_myTrack.min(theMin);
		if(_myTrackView != null)_myTrackView.min(theMin);
	}
	
	public void max(double theMax) {
		_myTrack.max(theMax);
		if(_myTrackView != null)_myTrackView.max(theMax);
	}
	
	public void muteGroup(boolean theIsMuted){
		_myParent.groupTrack().mute(theIsMuted);//.address(), theIsMuted);
	}
	
	public void viewValue(String theValue) {
		if(_myTrackView != null)_myTrackView.value(theValue);
	}
	
	public abstract void writeValue(double theTime);
	
	public void color(CCColor theColor) {
		// TODO fix color track
//		_myTimelineController.colorTrack(theColor, _myTrack.address());
	}
	
	@Override
	public CCTrackData trackData() {
		return _myTrack.trackData();
	}
	
	public void time(double theTime){
		double myValue = 0;
		
		if(_myTrack.property() != null){
			_myChangedValue = true;
			if(!track().mute()){
				_myTrack.property().fromDoubleValue(value(theTime), false);
			}
			myValue = value(theTime);//_myTrack.property().formatNormalizedValue(value(theTime));
			viewValue(_myTrack.property().valueString());
		}
		// @TODO check rendering
//		if(trackData().size() == 0 && _myTrackView != null)_myTrackView.render();
		if(track().mute()) return;
		if(trackData().size() == 0)return;
    	
		timeImplementation(theTime, myValue);
	}
	
	public abstract void timeImplementation(double theTime, double theValue);
	
	public double maxTime(){
		CCControlPoint myLastPoint = trackData().getLastPoint();
		if(myLastPoint == null)return 0;
		
		if(myLastPoint instanceof CCEventPoint) {
			return ((CCEventPoint)myLastPoint).endTime();
		}
		
		return myLastPoint.time();
		
	}
	
	/**
	 * Returns the value of the track at the given time
	 * @param theTime time where to get the value
	 * @return value at the given time
	 */
	public double value(double theTime) {
		if(trackData().size() == 0){
			if(_myTrack.property() == null)return 0;
			return _myTrack.property().normalizedValue();
		}
		return CCMath.blend(track().min(), track().max(), trackData().value(theTime)) ;
    }
    
//	/**
//	 * Similar to the {@link #value(double)} method but scales the output
//	 * within the min max range of the track
//	 * @param theTime time where to get the value
//	 * @return scaled value at the given time
//	 */
//    public double scaledValue() {
//    	return CCMath.blend(_myTrack.minValue(), _myTrack.maxValue(), value(theTime));
//    }

	@Override
    public CCVector2 curveToViewSpace(CCControlPoint thePoint) {
        CCVector2 myResult = new CCVector2();
        myResult.x = timeToViewX(thePoint.time());
        myResult.y = (int) ((1 - thePoint.value()) * (_myTrackView.height() - 5) + 2.5); // reverse y axis
        return myResult;
    }
    
    
    public CCVector2 curveToViewSpace(CCControlPoint thePoint, double theTimeOffset) {
        CCVector2 myResult = new CCVector2();
        myResult.x = timeToViewX(thePoint.time() + theTimeOffset);
        myResult.y = (int) ((1 - thePoint.value()) * (_myTrackView.height() - 5) + 2.5); 
        return myResult;
    }
    
    public CCControlPoint viewToCurveSpace(CCVector2 thePoint, boolean theGetPos) {
        CCControlPoint myResult = new CCControlPoint();
        
        myResult.time(theGetPos ? viewXToTime(thePoint.x) : viewWidthToTime(thePoint.x));
        double myValue = CCMath.constrain(1 - (thePoint.y - 2.5) / (_myTrackView.height() - 5),0,1);
//        myValue = _myTrack.property() != null ? _myTrack.property().formatNormalizedValue(myValue) : myValue;
        myResult.value(myValue);
        return myResult;
    }
    
    @Override
    public double valueToViewY(double theValue) {
    	return theValue * _myTrackView.height();
    }
    
    @Override
    public double viewTime() {
    	return viewWidthToTime(_myTrackView.width());
    }
    
    @Override
    public CCControlPoint quantize(CCControlPoint myTargetPosition) {
    	return myTargetPosition;
    }
    
    public void selection(CCSelection theSelection) {
	    _mySelection = theSelection;
    }
    
    public CCSelection selection() {
    	return _mySelection;
    }
    
    public void mousePressed(CCGLMouseEvent e) {
    	CCVector2 myViewCoords = new CCVector2(e.x, e.y);
				
    	if (e.isAltDown()) {
    		_myTrackContext.zoomController().startDrag(myViewCoords);
    		return;
    	}
				
    	_myTrackContext.activeTrack(this);
    	_myActiveTool.mousePressed(e);
	}


	public void mouseReleased(CCGLMouseEvent e) {
		if (e.isAltDown()) {
			_myTrackContext.zoomController().endDrag();
			return;
		}
//		_myTimelineController.transportController().time(viewXToTime(e.getX(), true));
		_myActiveTool.mouseReleased(e);
	}
	
	public void mouseMoved(CCVector2 e){
		_myActiveTool.mouseMoved(e);
//		CCVector2 myViewCoords = new CCVector2.Double(e.getX(), e.getY());
//
//		ControlPoint myNearest = pickNearestPoint(myViewCoords);
//        ControlPoint myTensionHandle = pickHandle(myViewCoords);
//
//		if (myNearest != null && distance(myNearest,myViewCoords) < SwingTrackView.PICK_RADIUS || myTensionHandle != null) {
//			if(this instanceof EventTrackController){
//				_myTrackView.moveRangeCursor();
//			}else{
//				_myTrackView.selectCursor();
//			}
//				
//		} else {
//			_myTrackView.defaultCursor();
//		}
	}
	
	public void keyPressed(CCGLKeyEvent e) {
		_myActiveTool.keyPressed(e);
	}
	
	public void keyReleased(CCGLKeyEvent e) {
		_myActiveTool.keyReleased(e);
	}

	public void mouseDragged(CCVector2 e) {
		_myActiveTool.mouseDragged(e);
	}

}
