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
import cc.creativecomputing.controlui.timeline.controller.track.CCCurveTrackController;
import cc.creativecomputing.math.CCMath;

public class SwingCurveTrackDataView extends SwingAbstractTrackDataView<CCCurveTrackController>{
	
    private SwingCurveTrackPopup _myToolChooserPopup;

	public SwingCurveTrackDataView(TimelineController theTimelineController, CCCurveTrackController theTrackController) {
		super(theTimelineController, theTrackController);
    	_myToolChooserPopup = new SwingCurveTrackPopup(theTrackController, theTimelineController);
	}
	
	@Override
	public void showPopUp(MouseEvent theEvent) {
		_myToolChooserPopup.show(SwingCurveTrackDataView.this, theEvent.getX(), theEvent.getY());
	}
	
	private void drawCurve(Graphics2D g2d) {
        if (_myController.trackData().size() == 0) {
        	GeneralPath myPath = new GeneralPath();
        	Point2D p1 = _myController.curveToViewSpace(new ControlPoint(0,_myController.value(0)));
            myPath.moveTo(0, p1.getY());
            myPath.lineTo(getWidth(), p1.getY());
            
            g2d.setColor(_myFillColor);
            g2d.draw(myPath);
            return;
        }
        
        ControlPoint myMinPoint = _myController.trackData().floor(new ControlPoint(_myTrackContext.lowerBound(), 0));
		if(myMinPoint == null){
			myMinPoint = new ControlPoint(
				_myTrackContext.lowerBound(), 
				_myController.trackData().value(_myTrackContext.lowerBound())
			);
		}
        Point2D p1 = _myController.curveToViewSpace(myMinPoint);
		
        GeneralPath myPath = new GeneralPath();
        myPath.moveTo(p1.getX(), p1.getY());
        
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
        	drawCurvePiece(myLastPoint, myCurrentPoint, myPath, false);
        	myLastPoint = myCurrentPoint;
        	myCurrentPoint = myCurrentPoint.getNext();
		}
        drawCurvePiece(myLastPoint, myMaxPoint, myPath, false);
        
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
		drawCurve(g2d);

		// paint curve points
		BasicStroke myThinStroke = new BasicStroke(0.5f);
		g2d.setStroke(myThinStroke);
		g2d.setColor(_myDotColor);
		ControlPoint myCurrentPoint = _myController.trackData().getFirstPointAt(_myTrackContext.lowerBound());

		while (myCurrentPoint != null) {
			if (myCurrentPoint.time() > _myTrackContext.upperBound()) {
				break;
			}
			Point2D myUserPoint = _myController.curveToViewSpace(myCurrentPoint);
			if(myCurrentPoint.isSelected()){
				g2d.setColor(Color.red);
			}else{
				g2d.setColor(_myDotColor);
			}
			point(myUserPoint);
					
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
		
		double myTime = _myTimelineController.transportController().time();
		int myViewX = _myController.timeToViewX(myTime) ;

		if (myViewX >= 0 && myViewX <= getWidth()) {
			
			ControlPoint myDraggedPoint = _myController.draggedPoint();
				
			if(_myIsMousePressed && myDraggedPoint != null){
				double myValue = myDraggedPoint.value();
				Point2D myPoint = _myController.curveToViewSpace(myDraggedPoint);
				g.drawString(
					_myController.property().valueString(),
					(int)myPoint.getX() + 10, 
					(int) _myController.curveToViewSpace(new ControlPoint(myTime, myValue * (1 - 12f/getHeight()))).getY()
				);
			}else{
				double myValue = _myController.value(myTime);
				g.drawString(
					_myController.property().valueString(), 
					myViewX + 10, 
					(int) _myController.curveToViewSpace(new ControlPoint(myTime, myValue * (1 - 12f/getHeight()))).getY()
				);
			}
		}
		
		if(_myIsMousePressed){
			ControlPoint myDraggedPoint = _myController.draggedPoint();
			if(myDraggedPoint != null){
				Point2D myPoint = _myController.curveToViewSpace(myDraggedPoint);
				g.drawLine(0, (int)myPoint.getY(), getWidth(), (int)myPoint.getY());
				g.drawLine((int)myPoint.getX(), 0, (int)myPoint.getX(), getHeight());
			}
			if(_myController.selectionStart() != null){
				int x = (int)CCMath.min(_myController.selectionStart().getX(),_myController.selectionEnd().getX());
				int y = (int)CCMath.min(_myController.selectionStart().getY(),_myController.selectionEnd().getY());
				int width = (int)CCMath.abs(_myController.selectionEnd().getX() - _myController.selectionStart().getX());
				int height = (int)CCMath.abs(_myController.selectionEnd().getY() - _myController.selectionStart().getY());
				g.setColor(new Color(0.15f, 0.15f, 0.15f, 0.05f));
				g.fillRect(x, y, width, height);
				g.setColor(Color.red);
				g.drawRect(x, y, width, height);
			}
		}
	}
}
