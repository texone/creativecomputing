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
import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.controller.track.CCBlendableTrackController;

public abstract class SwingBlendableTrackDataView<ControllerType extends CCBlendableTrackController<?>> extends SwingAbstractTrackDataView<ControllerType> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8117392771151385531L;
	private SwingCurveTrackPopup _myToolChooserPopup;

	public SwingBlendableTrackDataView(TimelineController theTimelineController, ControllerType theTrackController) {
		super(theTimelineController, theTrackController);
		_myToolChooserPopup = new SwingCurveTrackPopup(theTrackController, theTimelineController);
	}

	@Override
	public void showPopUp(MouseEvent theEvent) {
		_myToolChooserPopup.show(SwingBlendableTrackDataView.this, theEvent);

	}

	public abstract void renderBlendData(Graphics2D g2d, ControlPoint myFirstPoint, ControlPoint mySecondPoint);

	private void renderBlendData(Graphics2D g2d) {
		if (_myController.trackData().size() == 0) {
			return;
		}

		ControlPoint myMinPoint = _myController.trackData().floor(new ControlPoint(_myTrackContext.lowerBound(), 0));
		if (myMinPoint == null) {
			myMinPoint = new ControlPoint(_myTrackContext.lowerBound(), _myController.trackData().value(_myTrackContext.lowerBound()));
		}

		ControlPoint myMaxPoint = _myController.trackData().ceiling(new ControlPoint(_myTrackContext.upperBound(), 0));

		if (myMaxPoint == null) {
			myMaxPoint = new ControlPoint(_myTrackContext.upperBound(), _myController.trackData().value(_myTrackContext.upperBound()));
		}
		myMaxPoint = _myController.trackData().getLastOnSamePosition(myMaxPoint);

		ControlPoint myCurrentPoint = _myController.trackData().ceiling(new ControlPoint(_myTrackContext.lowerBound(), 0));
		ControlPoint myLastPoint = myMinPoint;
		while (myCurrentPoint != null && myCurrentPoint != myMaxPoint) {
			renderBlendData(g2d, myLastPoint, myCurrentPoint);
			myLastPoint = myCurrentPoint;
			myCurrentPoint = myCurrentPoint.getNext();
		}
		renderBlendData(g2d, myLastPoint, myMaxPoint);
	}
	
	private void adjustHandles(ControlPoint thePoint) {
		if(!(thePoint instanceof BezierControlPoint))return;
		
		BezierControlPoint myPoint = (BezierControlPoint) thePoint;
		myPoint.inHandle().value(myPoint.inHandle().value() * 0.5);
		myPoint.outHandle().value(myPoint.outHandle().value() * 0.5);
	}

	private void drawBlendedCurve(Graphics2D g2d) {
		if (_myController.trackData().size() == 0) {
			GeneralPath myPath = new GeneralPath();
			Point2D p1 = _myController.curveToViewSpace(new ControlPoint(0, _myController.value(0)));
			myPath.moveTo(0, p1.getY() / 2);
			myPath.lineTo(getWidth(), p1.getY() / 2);

			g2d.setColor(_myFillColor);
			g2d.draw(myPath);
			return;
		}

		ControlPoint myMinPoint = _myController.trackData().floor(new ControlPoint(_myTrackContext.lowerBound(), 0));
		if (myMinPoint == null) {
			myMinPoint = new ControlPoint(_myTrackContext.lowerBound(), _myController.trackData().value(_myTrackContext.lowerBound()) / 2);
		}
		Point2D p1 = _myController.curveToViewSpace(myMinPoint);

		GeneralPath myPath = new GeneralPath();
		myPath.moveTo(p1.getX(), p1.getY());

		ControlPoint myMaxPoint = _myController.trackData().ceiling(new ControlPoint(_myTrackContext.upperBound(), 0));

		if (myMaxPoint == null) {
			myMaxPoint = new ControlPoint(_myTrackContext.upperBound(), _myController.trackData().value(_myTrackContext.upperBound()) / 2);
		}
		myMaxPoint = _myController.trackData().getLastOnSamePosition(myMaxPoint);

		ControlPoint myCurrentPoint = _myController.trackData().ceiling(new ControlPoint(_myTrackContext.lowerBound(), 0));
		ControlPoint myLastPoint = myMinPoint;
		while (myCurrentPoint != null && myCurrentPoint != myMaxPoint) {
			ControlPoint c0 = myLastPoint.clone();
			c0.value(0.0);
			ControlPoint c1 = myCurrentPoint.clone();
			c1.value(0.5);
			adjustHandles(c0);
			adjustHandles(c1);
			drawCurvePiece(c0, c1, myPath, true);
			myLastPoint = myCurrentPoint;
			myCurrentPoint = myCurrentPoint.getNext();
		}
		ControlPoint c0 = myLastPoint.clone();
		c0.value(0.0);
		ControlPoint c1 = myMaxPoint.clone();
		c1.value(0.0);
		adjustHandles(c0);
		adjustHandles(c1);
		drawCurvePiece(c0, c1, myPath, true);

		myPath.lineTo(this.getWidth(), this.getHeight());
		myPath.lineTo(0, this.getHeight());
		myPath.closePath();

		if (!_myController.track().mute()) {
			g2d.setPaint(new GradientPaint(0, 0, _myFillColor, 0, getHeight(), _myLineColor));
		} else {
			g2d.setPaint(new GradientPaint(0, 0, _myFillColor.brighter(), 0, getHeight(), _myLineColor.brighter()));
		}
		g2d.fill(myPath);

		g2d.setColor(_myLineColor);
		g2d.draw(myPath);
	}

	@Override
	public void renderData(Graphics2D g2d) {
		try {
		renderBlendData(g2d);
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

			
			g2d.setColor(_myDotColor);
			Point2D myUserPoint = _myController.curveToViewSpace(myCurrentPoint);
			if (myCurrentPoint.isSelected()) {
				g2d.setColor(Color.red);
			}

			point(myUserPoint);
			line(new Point2D.Double(myUserPoint.getX(), height() / 2), new Point2D.Double(myUserPoint.getX(), 0));
			g2d.setColor(_myDotColor);

			// g2d.drawString(myCurrentPoint.value() +"",
			// (int)myUserPoint.getX(), (int)myUserPoint.getY());

//			if (myCurrentPoint.getType() == ControlPointType.BEZIER) {
//				BezierControlPoint myBezierPoint = (BezierControlPoint) myCurrentPoint;
//				
//				Point2D myUserHandle = _myController.curveToViewSpace(myBezierPoint.inHandle());
//				myUserHandle.setLocation(myUserHandle.getX(), myUserHandle.getY() / 2 + height() / 2);
//				line(myUserPoint, myUserHandle);
//				point(myUserHandle);
//
//				myUserHandle = _myController.curveToViewSpace(myBezierPoint.outHandle());
//				myUserHandle.setLocation(myUserHandle.getX(), myUserHandle.getY() / 2 + height() / 2);
//				line(myUserPoint, myUserHandle);
//				point(myUserHandle);
//			}
			myCurrentPoint = myCurrentPoint.getNext();
		}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void drawTimelineInfos(Graphics g) {
		// TODO Auto-generated method stub

	}
}
