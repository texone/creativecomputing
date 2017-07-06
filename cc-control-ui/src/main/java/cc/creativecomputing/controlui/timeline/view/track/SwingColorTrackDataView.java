package cc.creativecomputing.controlui.timeline.view.track;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import cc.creativecomputing.control.timeline.point.BezierControlPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint.ControlPointType;
import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.controller.track.CCColorTrackController;
import cc.creativecomputing.math.CCColor;

public class SwingColorTrackDataView extends SwingAbstractTrackDataView<CCColorTrackController>{

	public SwingColorTrackDataView(SwingTrackDataRenderer theDataRenderer, TimelineController theTimelineController, CCColorTrackController theTrackController) {
		super(theTimelineController, theTrackController);
	}

	@Override
	public void showPopUp(MouseEvent theEvent) {
		// TODO Auto-generated method stub
		
	}
	
	private void drawGradient(Graphics2D g2d, ControlPoint myFirstPoint, ControlPoint mySecondPoint) {
    	if (myFirstPoint.equals(mySecondPoint)) {
    		return;
        }

        if (mySecondPoint == null) {
            mySecondPoint = new ControlPoint(_myTrackContext.upperBound(), myFirstPoint.value());
        }
        
        Point2D p1 = _myController.curveToViewSpace(myFirstPoint);
        Point2D p2 = _myController.curveToViewSpace(mySecondPoint);
        
        CCColorTrackController myColorTrackController = (CCColorTrackController)_myController;
        
        for(double x = p1.getX(); x <= p2.getX();x++){
        	double myTime = _myController.viewXToTime((int)x, true);
        	CCColor myColor = myColorTrackController.blend(myTime);
        	g2d.setColor(myColor.toAWTColor());
        	g2d.drawLine((int)x, 0, (int)x, height() / 2);
        }
        
//       
//        
//      
//
////        if(mySecondPoint.getType() == ControlPointType.CUBIC && mySecondPoint.hasNext()){
////        	ControlPoint myNextPoint = mySecondPoint.getNext();
////        	Point2D myp2 = _myController.curveToViewSpace(myNextPoint);
////        	thePath.quadTo(myX, myY, myp2.getX(), myp2.getY());
////        	return;
////        }
//        
////        if(theDrawInterval){
//        double myInterval = SwingTrackView.GRID_INTERVAL / getWidth() * (_myTrackContext.viewTime());
//        double myStart = myInterval * Math.floor(myFirstPoint.time() / myInterval);
//	
//        for (double step = myStart + myInterval; step < mySecondPoint.time(); step = step + myInterval) {
//        	double myValue = _myController.trackData().value(step);
//        	p1 = _myController.curveToViewSpace(new ControlPoint(step, myValue));
//        	thePath.lineTo(p1.getX(), p1.getY());
//        }
////        }
//        
//        thePath.lineTo(p2.getX(), p2.getY());     
    }
	
	private void renderGradients(Graphics2D g2d) {
    	if (_myController.trackData().size() == 0) {
    		return;
    	}
    	
         
    	ControlPoint myMinPoint = _myController.trackData().floor(new ControlPoint(_myTrackContext.lowerBound(), 0));
 		if(myMinPoint == null){
 			myMinPoint = new ControlPoint(
 				_myTrackContext.lowerBound(), 
 				_myController.trackData().value(_myTrackContext.lowerBound())
 			);
 		}
         
 		ControlPoint myMaxPoint = _myController.trackData().ceiling(new ControlPoint(_myTrackContext.upperBound(), 0));
 		
 		if(myMaxPoint == null){
 			myMaxPoint = new ControlPoint(
 				_myTrackContext.upperBound(), 
 				_myController.trackData().value(_myTrackContext.upperBound())
 			);
 		}
 		myMaxPoint = _myController.trackData().getLastOnSamePosition(myMaxPoint);
         
 		ControlPoint myCurrentPoint = _myController.trackData().ceiling(new ControlPoint(_myTrackContext.lowerBound(), 0));
 		ControlPoint myLastPoint = myMinPoint;
 		while (myCurrentPoint != null && myCurrentPoint != myMaxPoint) {
 			drawGradient(g2d, myLastPoint, myCurrentPoint);
         	myLastPoint = myCurrentPoint;
         	myCurrentPoint = myCurrentPoint.getNext();
 		}
 		drawGradient(g2d, myLastPoint, myMaxPoint);
    }
	
	private void drawBlendedCurve(Graphics2D g2d) {
        if (_myController.trackData().size() == 0) {
        	GeneralPath myPath = new GeneralPath();
        	Point2D p1 = _myController.curveToViewSpace(new ControlPoint(0,_myController.value(0)));
            myPath.moveTo(0, p1.getY() / 2);
            myPath.lineTo(getWidth(), p1.getY() / 2);
            
            g2d.setColor(_myFillColor);
            g2d.draw(myPath);
            return;
        }
        
        ControlPoint myMinPoint = _myController.trackData().floor(new ControlPoint(_myTrackContext.lowerBound(), 0));
		if(myMinPoint == null){
			myMinPoint = new ControlPoint(
				_myTrackContext.lowerBound(), 
				_myController.trackData().value(_myTrackContext.lowerBound()) / 2
			);
		}
        Point2D p1 = _myController.curveToViewSpace(myMinPoint);
		
        GeneralPath myPath = new GeneralPath();
        myPath.moveTo(p1.getX(), p1.getY());
        
        ControlPoint myMaxPoint = _myController.trackData().ceiling(new ControlPoint(_myTrackContext.upperBound(), 0));
		
		if(myMaxPoint == null){
			myMaxPoint = new ControlPoint(
				_myTrackContext.upperBound(), 
				_myController.trackData().value(_myTrackContext.upperBound()) / 2
			);
		}
		myMaxPoint = _myController.trackData().getLastOnSamePosition(myMaxPoint);
        
        ControlPoint myCurrentPoint = _myController.trackData().ceiling(new ControlPoint(_myTrackContext.lowerBound(), 0));
        ControlPoint myLastPoint = myMinPoint;
        while (myCurrentPoint != null && myCurrentPoint != myMaxPoint) {
        	ControlPoint c0 = myLastPoint.clone();
        	c0.value(0.0);
        	ControlPoint c1 = myCurrentPoint.clone();
        	c1.value(0.5);
        	drawCurvePiece(c0, c1, myPath, true);
        	myLastPoint = myCurrentPoint;
        	myCurrentPoint = myCurrentPoint.getNext();
		}
        ControlPoint c0 = myLastPoint.clone();
    	c0.value(0.0);
    	ControlPoint c1 = myMaxPoint.clone();
    	c1.value(0.0);
        drawCurvePiece(c0, c1, myPath, true);
        
        myPath.lineTo(this.getWidth(), this.getHeight());
        myPath.lineTo(0, this.getHeight());
        myPath.closePath();

	   
        if(!_myController.track().mute()){
	        g2d.setPaint(new GradientPaint(0, 0, _myFillColor, 0, getHeight(), _myLineColor));
        }else{
	        g2d.setPaint(new GradientPaint(0, 0, _myFillColor.brighter(), 0, getHeight(), _myLineColor.brighter()));
        }
        g2d.fill(myPath);
    	
        
        g2d.setColor(_myLineColor);
        g2d.draw(myPath);
    }
	
	@Override
	public void renderData(Graphics2D g2d) {
		renderGradients(g2d);
    	drawBlendedCurve(g2d);

		// paint curve points
		BasicStroke myThinStroke = new BasicStroke(0.5f);
		g2d.setStroke(myThinStroke);
		g2d.setColor(_myDotColor);
		ControlPoint myCurrentPoint = _myController.trackData().getFirstPointAt(_myTrackContext.lowerBound());

		while (myCurrentPoint != null) {
			if (myCurrentPoint.time() > _myTrackContext.upperBound()) {
				break;
			}
			
			CCColor myColor = (CCColor)myCurrentPoint.blendable();
			if(myColor != null){
				g2d.setColor(myColor.toAWTColor());
			}
			
			Point2D myUserPoint = _myController.curveToViewSpace(myCurrentPoint);
			if(myCurrentPoint.isSelected()){
				g2d.setColor(Color.red);
			}
			
			point(myUserPoint);
			line(new Point2D.Double(myUserPoint.getX(), height() / 2), new Point2D.Double(myUserPoint.getX(), 0));
			g2d.setColor(_myDotColor);
			
			//g2d.drawString(myCurrentPoint.value() +"", (int)myUserPoint.getX(), (int)myUserPoint.getY());

			if (myCurrentPoint.getType() == ControlPointType.BEZIER) {
				BezierControlPoint myBezierPoint = (BezierControlPoint) myCurrentPoint;
				
				Point2D myUserHandle = _myController.curveToViewSpace(myBezierPoint.inHandle());
				line(myUserPoint, myUserHandle);
				point(myUserHandle);

				myUserHandle = _myController.curveToViewSpace(myBezierPoint.outHandle());
				line(myUserPoint, myUserHandle);
				point(myUserHandle);
			}
			myCurrentPoint = myCurrentPoint.getNext();
		}
    }
	
	@Override
	public void drawTimelineInfos(Graphics g) {
		// TODO Auto-generated method stub
		
	}
}
