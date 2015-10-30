package cc.creativecomputing.controlui.controls;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import cc.creativecomputing.control.timeline.point.BezierControlPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint.ControlPointType;
import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.controller.arrange.CCClipTrackObject;
import cc.creativecomputing.controlui.timeline.controller.track.CurveTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.TrackController;
import cc.creativecomputing.controlui.timeline.view.track.SwingTrackDataRenderer;
import cc.creativecomputing.controlui.timeline.view.track.SwingTrackDataView;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCMath;

public class CCClipTrackDataRenderer extends SwingTrackDataRenderer{

	private CCClipTrackObject _myClipTrack;
	
	public CCClipTrackDataRenderer(CCClipTrackObject theClipTrack){
		_myClipTrack = theClipTrack;
	}
	
	private void drawCurvePiece(CurveTrackController theController, SwingTrackDataView theView, ControlPoint myFirstPoint, ControlPoint mySecondPoint, GeneralPath thePath, double theStartTime, double theEndTime) {
        if (myFirstPoint.equals(mySecondPoint)) {
            return;
        }

        if (mySecondPoint == null) {
            mySecondPoint = new ControlPoint(theView.context().upperBound(), myFirstPoint.value());
        }
        
        boolean myIsBezier = false;
        Point2D p1 = theView.controller().curveToViewSpace(myFirstPoint, theStartTime);
        Point2D p2 = theView.controller().curveToViewSpace(mySecondPoint, theStartTime);
        double myA1X = p1.getX();
        double myA1Y = p1.getY();
        double myA2X = p2.getX();
        double myA2Y = p2.getY();
        double myX = p2.getX();
        double myY = p2.getY();
        
        if(mySecondPoint.getType() == ControlPointType.STEP){
        	thePath.lineTo(myA2X, myA1Y);
        	thePath.lineTo(myA2X, myA2Y);
        	return;
        }
        
        if(mySecondPoint.getType() == ControlPointType.BEZIER){
        	myIsBezier = true;
        	BezierControlPoint myBezier2Point = (BezierControlPoint)mySecondPoint;
        	Point2D myHandle = theView.controller().curveToViewSpace(myBezier2Point.inHandle(), theStartTime);
        	myA2X = myHandle.getX();
        	myA2Y = myHandle.getY();
        	
        }
        if(myFirstPoint.getType() == ControlPointType.BEZIER){
        	myIsBezier = true;
        	BezierControlPoint myBezier1Point = (BezierControlPoint)myFirstPoint;
        	Point2D myHandle = theView.controller().curveToViewSpace(myBezier1Point.outHandle(), theStartTime);
        	myA1X = myHandle.getX();
        	myA1Y = myHandle.getY();
    	}
        if(myIsBezier){
        	thePath.curveTo(myA1X, myA1Y, myA2X, myA2Y, myX, myY);
        	return;
        }
        
        if(mySecondPoint.getType() == ControlPointType.LINEAR){
        	thePath.lineTo(myX, myY);
        	return;
        }

//        if(mySecondPoint.getType() == ControlPointType.CUBIC && mySecondPoint.hasNext()){
//        	ControlPoint myNextPoint = mySecondPoint.getNext();
//        	Point2D myp2 = _myController.curveToViewSpace(myNextPoint);
//        	thePath.quadTo(myX, myY, myp2.getX(), myp2.getY());
//        	return;
//        }
        
//        if(theDrawInterval){
//	        double myInterval = TrackView.GRID_INTERVAL / theView.getWidth() * (theView.context().viewTime());
//	        double myStart = myInterval * Math.floor(myFirstPoint.time() / myInterval);
//	
//	        for (double step = myStart + myInterval; step < mySecondPoint.time(); step = step + myInterval) {
//	            double myValue = theController.trackData().value(step);
//	            p1 = theView.controller().curveToViewSpace(new ControlPoint(step, myValue));
//	            thePath.lineTo(p1.getX(), p1.getY());
//	        }
//        }
        
        thePath.lineTo(p2.getX(), p2.getY());     
    }
	
	private void drawCurve(CurveTrackController _myController, SwingTrackDataView theView, Graphics2D g, double theStartTime, double theEndTime) {
    	
        if (_myController.trackData().size() == 0) {
        	double myLowerBound = CCMath.max(theStartTime, theView.context().lowerBound());
        	double myUpperBound = CCMath.min(theEndTime, theView.context().upperBound());
        	GeneralPath myPath = new GeneralPath();
        	Point2D p1 = theView.controller().curveToViewSpace(new ControlPoint(myLowerBound,_myController.value(0)));
        	Point2D p2 = theView.controller().curveToViewSpace(new ControlPoint(myUpperBound,_myController.value(0)));
            myPath.moveTo(p1.getX(), p1.getY());
            myPath.lineTo(p2.getX(), p2.getY());
            
            g.setColor(theView.fillColor());
            g.draw(myPath);
            return;
        }
        
        ControlPoint myMinPoint = new ControlPoint(
        	theView.context().lowerBound() - theStartTime, 
        	_myController.trackData().value(theView.context().lowerBound() - theStartTime)
		);
        Point2D p1 = theView.controller().curveToViewSpace(new ControlPoint(myMinPoint.time(), myMinPoint.value()), theStartTime);

        CCLog.info(myMinPoint + ":" + p1);
        GeneralPath myPath = new GeneralPath();
        myPath.moveTo(p1.getX(), p1.getY());
        
        ControlPoint myMaxPoint = _myController.trackData().ceiling(new ControlPoint(theView.context().upperBound() - theStartTime, 0));
		
		if(myMaxPoint == null){
			myMaxPoint = new ControlPoint(
				theView.context().upperBound() - theStartTime, 
				_myController.trackData().value(theView.context().upperBound() - theStartTime)
			);
		}
		myMaxPoint = _myController.trackData().getLastOnSamePosition(myMaxPoint);
        
        ControlPoint myCurrentPoint = _myController.trackData().ceiling(new ControlPoint(theView.context().lowerBound() - theStartTime, 0));
        ControlPoint myLastPoint = myMinPoint;
        while (myCurrentPoint != null && myCurrentPoint != myMaxPoint) {
        	drawCurvePiece(_myController, theView, myLastPoint, myCurrentPoint, myPath, theStartTime, theEndTime);
        	myLastPoint = myCurrentPoint;
        	myCurrentPoint = myCurrentPoint.getNext();
		}
        drawCurvePiece(_myController, theView,myLastPoint, myMaxPoint, myPath, theStartTime, theEndTime);
        
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
	public void renderTimedEvent(TimedEventPoint theTimedEvent, SwingTrackDataView theView, Graphics2D theG2d) {
		if(theTimedEvent.content() == null || theTimedEvent.content().value() == null) {
			return;
		}
		
		CCLog.info("draw object");
		
		TimelineController myTimelineController =_myClipTrack.timelineController(theTimedEvent.content().value().toString());
		myTimelineController.view();
		if(myTimelineController != null){
			for(TrackController myTrackController:myTimelineController.trackController()){
				if(myTrackController instanceof CurveTrackController){
					drawCurve((CurveTrackController)myTrackController, theView, theG2d, theTimedEvent.time(), theTimedEvent.endTime());
				}
			}
		}
		
		Point2D myPos = theView.controller().curveToViewSpace(new ControlPoint(theTimedEvent.time(),1));
		Point2D myEndPos = theView.controller().curveToViewSpace(new ControlPoint(theTimedEvent.endTime(),1));
		double width = myEndPos.getX() - myPos.getX();
		theG2d.setColor(new Color(0,0,0,100));
		
		FontMetrics myMetrix = theG2d.getFontMetrics();
		String myString = theTimedEvent.content().value().toString();
		int myIndex = myString.length() - 1;
		StringBuffer myText = new StringBuffer();
		while(myIndex >= 0 && myMetrix.stringWidth(myText.toString() + myString.charAt(myIndex)) < width - 5){
			myText.insert(0, myString.charAt(myIndex));
			myIndex--;
		}
		theG2d.drawString(myText.toString(), (int) myPos.getX() + 5, (int) myPos.getY() + 15);
	}
	
	
}