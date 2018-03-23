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
package cc.creativecomputing.controlui.controls;

import cc.creativecomputing.control.timeline.point.CCBezierControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCTimedEventPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint.CCControlPointType;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.controlui.timeline.controller.arrange.CCClipTrackObject;
import cc.creativecomputing.controlui.timeline.controller.track.CCCurveTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCTrackController;
import cc.creativecomputing.controlui.timeline.view.track.CCAbstractTrackDataView;
import cc.creativecomputing.controlui.timeline.view.track.CCTrackDataRenderer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCClipTrackDataRenderer extends CCTrackDataRenderer{

	private CCClipTrackObject _myClipTrack;
	
	public CCClipTrackDataRenderer(CCClipTrackObject theClipTrack){
		_myClipTrack = theClipTrack;
	}
	
	private void drawCurvePiece(CCCurveTrackController theController, CCAbstractTrackDataView<?> theView, CCControlPoint myFirstPoint, CCControlPoint mySecondPoint, GeneralPath thePath, double theStartTime, double theEndTime) {
        if (myFirstPoint.equals(mySecondPoint)) {
            return;
        }

        if (mySecondPoint == null) {
            mySecondPoint = new CCControlPoint(theView.context().upperBound(), myFirstPoint.value());
        }
        
        boolean myIsBezier = false;
        CCVector2 p1 = theView.controller().curveToViewSpace(myFirstPoint, theStartTime);
        CCVector2 p2 = theView.controller().curveToViewSpace(mySecondPoint, theStartTime);
        double myA1X = p1.x;
        double myA1Y = p1.y;
        double myA2X = p2.x;
        double myA2Y = p2.y;
        double myX = p2.x;
        double myY = p2.y;
        
        if(mySecondPoint.type() == CCControlPointType.STEP){
        	thePath.lineTo(myA2X, myA1Y);
        	thePath.lineTo(myA2X, myA2Y);
        	return;
        }
        
        if(mySecondPoint.type() == CCControlPointType.BEZIER){
        	myIsBezier = true;
        	CCBezierControlPoint myBezier2Point = (CCBezierControlPoint)mySecondPoint;
        	CCVector2 myHandle = theView.controller().curveToViewSpace(myBezier2Point.inHandle(), theStartTime);
        	myA2X = myHandle.x;
        	myA2Y = myHandle.y;
        	
        }
        if(myFirstPoint.type() == CCControlPointType.BEZIER){
        	myIsBezier = true;
        	CCBezierControlPoint myBezier1Point = (CCBezierControlPoint)myFirstPoint;
        	CCVector2 myHandle = theView.controller().curveToViewSpace(myBezier1Point.outHandle(), theStartTime);
        	myA1X = myHandle.x;
        	myA1Y = myHandle.y;
    	}
        if(myIsBezier){
        	thePath.curveTo(myA1X, myA1Y, myA2X, myA2Y, myX, myY);
        	return;
        }
        
        if(mySecondPoint.type() == CCControlPointType.LINEAR){
        	thePath.lineTo(myX, myY);
        	return;
        }

//        if(mySecondPoint.getType() == ControlPointType.CUBIC && mySecondPoint.hasNext()){
//        	ControlPoint myNextPoint = mySecondPoint.getNext();
//        	CCVector2 myp2 = _myController.curveToViewSpace(myNextPoint);
//        	thePath.quadTo(myX, myY, myp2.x, myp2.y);
//        	return;
//        }
        
//        if(theDrawInterval){
//	        double myInterval = TrackView.GRID_INTERVAL / theView.getWidth() * (theView.context().viewTime());
//	        double myStart = myInterval * Math.floor(myFirstPoint.time() / myInterval);
//	
//	        for (double step = myStart + myInterval; step < mySecondPoint.time(); step = step + myInterval) {
//	            double myValue = theController.trackData().value(step);
//	            p1 = theView.controller().curveToViewSpace(new ControlPoint(step, myValue));
//	            thePath.lineTo(p1.x, p1.y);
//	        }
//        }
        
        thePath.lineTo(p2.x, p2.y);     
    }
	
	private void drawCurve(CCCurveTrackController _myController, CCAbstractTrackDataView<?> theView, CCGraphics g, CCTimedEventPoint theEvent) {
    	
        if (_myController.trackData().size() == 0) {
        	double myLowerBound = CCMath.max(theEvent.time(), theView.context().lowerBound());
        	double myUpperBound = CCMath.min(theEvent.endTime(), theView.context().upperBound());
        	GeneralPath myPath = new GeneralPath();
        	CCVector2 p1 = theView.controller().curveToViewSpace(new CCControlPoint(myLowerBound,_myController.value(0)));
        	CCVector2 p2 = theView.controller().curveToViewSpace(new CCControlPoint(myUpperBound,_myController.value(0)));
            myPath.moveTo(p1.x, p1.y);
            myPath.lineTo(p2.x, p2.y);
            
            g.color(theView.fillColor());
            g.draw(myPath);
            return;
        }
        
        CCControlPoint myMinPoint = new CCControlPoint(
        	theView.context().lowerBound() - theEvent.time(), 
        	_myController.trackData().value(theView.context().lowerBound() - theEvent.time())
		);
        CCVector2 p1 = theView.controller().curveToViewSpace(new CCControlPoint(myMinPoint.time(), myMinPoint.value()), theEvent.time());

        GeneralPath myPath = new GeneralPath();
        myPath.moveTo(p1.x, p1.y);
        CCControlPoint myMaxPoint = _myController.trackData().ceiling(new CCControlPoint(theView.context().upperBound() - theEvent.time() + theEvent.contentOffset(), 0));
		
		if(myMaxPoint == null){
			myMaxPoint = new CCControlPoint(
				theView.context().upperBound() - theEvent.time(), 
				_myController.trackData().value(theView.context().upperBound() - theEvent.time() + theEvent.contentOffset())
			);
		}
		myMaxPoint = _myController.trackData().getLastOnSamePosition(myMaxPoint);
        
        CCControlPoint myCurrentPoint = _myController.trackData().ceiling(new CCControlPoint(theView.context().lowerBound() - theEvent.time() + theEvent.contentOffset(), 0));
        CCControlPoint myLastPoint = myMinPoint;
        while (myCurrentPoint != null && myCurrentPoint != myMaxPoint) {
        	drawCurvePiece(_myController, theView, myLastPoint, myCurrentPoint, myPath, theEvent.time() + theEvent.contentOffset(), theEvent.endTime() + theEvent.contentOffset());
        	myLastPoint = myCurrentPoint;
        	myCurrentPoint = myCurrentPoint.next();
		}
        drawCurvePiece(_myController, theView,myLastPoint, myMaxPoint, myPath, theEvent.time() + theEvent.contentOffset(), theEvent.endTime() + theEvent.contentOffset());
        
//        myPath.lineTo(theView.getWidth(), theView.getHeight());
//        myPath.lineTo(0, theView.getHeight());
//        myPath.closePath();

//        g2d.setColor(_myFillColor);
//        g2d.setPaint(new GradientPaint(0, 0, _myFillColor, 0, getHeight(), _myLineColor));
//        g2d.fill(myPath);
        
        g.setColor(theView.lineColor());
        g.draw(myPath);
    }
	
	@Override
	public void renderTimedEvent(CCTimedEventPoint theTimedEvent, CCAbstractTrackDataView<?> theView, CCGraphics g) {
		if(theTimedEvent.content() == null || theTimedEvent.content().value() == null) {
			return;
		}
		
		CCTimelineController myTimelineController =_myClipTrack.timelineController(theTimedEvent.content().value().toString());
		myTimelineController.view();
		if(myTimelineController != null){
			for(CCTrackController myTrackController:myTimelineController.trackController()){
				if(myTrackController instanceof CCCurveTrackController){
					drawCurve((CCCurveTrackController)myTrackController, theView, g, theTimedEvent);
				}
			}
		}
		
		CCVector2 myPos = theView.controller().curveToViewSpace(new CCControlPoint(theTimedEvent.time(),1));
		CCVector2 myEndPos = theView.controller().curveToViewSpace(new CCControlPoint(theTimedEvent.endTime(),1));
		double width = myEndPos.x - myPos.x;
		g.color(0,0,0,100);
		
		FontMetrics myMetrix = theG2d.getFontMetrics();
		String myString = theTimedEvent.content().value().toString();
		int myIndex = myString.length() - 1;
		StringBuffer myText = new StringBuffer();
		while(myIndex >= 0 && myMetrix.stringWidth(myText.toString() + myString.charAt(myIndex)) < width - 5){
			myText.insert(0, myString.charAt(myIndex));
			myIndex--;
		}
		theG2d.drawString(myText.toString(), (int) myPos.x + 5, (int) myPos.y + 15);
	}
	
	
}
