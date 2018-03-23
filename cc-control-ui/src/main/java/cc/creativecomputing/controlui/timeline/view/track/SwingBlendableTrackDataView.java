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
package cc.creativecomputing.controlui.timeline.view.track;

import cc.creativecomputing.control.timeline.point.CCBezierControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.controlui.timeline.controller.track.CCBlendableTrackController;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2;

public abstract class SwingBlendableTrackDataView<ControllerType extends CCBlendableTrackController<?>> extends CCAbstractTrackDataView<ControllerType> {

	private SwingCurveTrackPopup _myToolChooserPopup;

	public SwingBlendableTrackDataView(CCTimelineController theTimelineController, ControllerType theTrackController) {
		super(theTimelineController, theTrackController);
		_myToolChooserPopup = new SwingCurveTrackPopup(theTrackController, theTimelineController);
	}

	@Override
	public void showPopUp(CCGLMouseEvent theEvent) {
		_myToolChooserPopup.show(theEvent);

	}

	public abstract void renderBlendData(CCGraphics g, CCControlPoint myFirstPoint, CCControlPoint mySecondPoint);

	private void renderBlendData(CCGraphics g) {
		if (_myController.trackData().size() == 0) {
			return;
		}

		CCControlPoint myMinPoint = _myController.trackData().floor(new CCControlPoint(_myTrackContext.lowerBound(), 0));
		if (myMinPoint == null) {
			myMinPoint = new CCControlPoint(_myTrackContext.lowerBound(), _myController.trackData().value(_myTrackContext.lowerBound()));
		}

		CCControlPoint myMaxPoint = _myController.trackData().ceiling(new CCControlPoint(_myTrackContext.upperBound(), 0));

		if (myMaxPoint == null) {
			myMaxPoint = new CCControlPoint(_myTrackContext.upperBound(), _myController.trackData().value(_myTrackContext.upperBound()));
		}
		myMaxPoint = _myController.trackData().getLastOnSamePosition(myMaxPoint);

		CCControlPoint myCurrentPoint = _myController.trackData().ceiling(new CCControlPoint(_myTrackContext.lowerBound(), 0));
		CCControlPoint myLastPoint = myMinPoint;
		while (myCurrentPoint != null && myCurrentPoint != myMaxPoint) {
			renderBlendData(g, myLastPoint, myCurrentPoint);
			myLastPoint = myCurrentPoint;
			myCurrentPoint = myCurrentPoint.next();
		}
		renderBlendData(g, myLastPoint, myMaxPoint);
	}
	
	private void adjustHandles(CCControlPoint thePoint) {
		if(!(thePoint instanceof CCBezierControlPoint))return;
		
		CCBezierControlPoint myPoint = (CCBezierControlPoint) thePoint;
		myPoint.inHandle().value(myPoint.inHandle().value() * 0.5);
		myPoint.outHandle().value(myPoint.outHandle().value() * 0.5);
	}

	private void drawBlendedCurve(CCGraphics g) {
		if (_myController.trackData().size() == 0) {
			CCVector2 p1 = _myController.curveToViewSpace(new CCControlPoint(0, _myController.value(0)));
			g.line(0, p1.y / 2);
			myPath.lineTo(getWidth(), p1.y / 2);

			g.color(_myFillColor);
			g.draw(myPath);
			return;
		}

		CCControlPoint myMinPoint = _myController.trackData().floor(new CCControlPoint(_myTrackContext.lowerBound(), 0));
		if (myMinPoint == null) {
			myMinPoint = new CCControlPoint(_myTrackContext.lowerBound(), _myController.trackData().value(_myTrackContext.lowerBound()) / 2);
		}
		CCVector2 p1 = _myController.curveToViewSpace(myMinPoint);

		GeneralPath myPath = new GeneralPath();
		myPath.moveTo(p1.x, p1.y);

		CCControlPoint myMaxPoint = _myController.trackData().ceiling(new CCControlPoint(_myTrackContext.upperBound(), 0));

		if (myMaxPoint == null) {
			myMaxPoint = new CCControlPoint(_myTrackContext.upperBound(), _myController.trackData().value(_myTrackContext.upperBound()) / 2);
		}
		myMaxPoint = _myController.trackData().getLastOnSamePosition(myMaxPoint);

		CCControlPoint myCurrentPoint = _myController.trackData().ceiling(new CCControlPoint(_myTrackContext.lowerBound(), 0));
		CCControlPoint myLastPoint = myMinPoint;
		while (myCurrentPoint != null && myCurrentPoint != myMaxPoint) {
			CCControlPoint c0 = myLastPoint.clone();
			c0.value(0.0);
			CCControlPoint c1 = myCurrentPoint.clone();
			c1.value(0.5);
			adjustHandles(c0);
			adjustHandles(c1);
			drawCurvePiece(c0, c1, myPath, true);
			myLastPoint = myCurrentPoint;
			myCurrentPoint = myCurrentPoint.next();
		}
		CCControlPoint c0 = myLastPoint.clone();
		c0.value(0.0);
		CCControlPoint c1 = myMaxPoint.clone();
		c1.value(0.0);
		adjustHandles(c0);
		adjustHandles(c1);
		drawCurvePiece(c0, c1, myPath, true);

		myPath.lineTo(this.getWidth(), this.getHeight());
		myPath.lineTo(0, this.getHeight());
		myPath.closePath();

		if (!_myController.track().mute()) {
			g.setPaint(new GradientPaint(0, 0, _myFillColor, 0, getHeight(), _myLineColor));
		} else {
			g.setPaint(new GradientPaint(0, 0, _myFillColor.brighter(), 0, getHeight(), _myLineColor.brighter()));
		}
		g.fill(myPath);

		g.color(_myLineColor);
		g.draw(myPath);
	}

	@Override
	public void renderData(CCGraphics g) {
		try {
		renderBlendData(g);
		drawBlendedCurve(g);

		// paint curve points
		g.strokeWeight(0.5);
		g.color(_myDotColor);
		CCControlPoint myCurrentPoint = _myController.trackData().getFirstPointAfter(_myTrackContext.lowerBound());

		while (myCurrentPoint != null) {
			if (myCurrentPoint.time() > _myTrackContext.upperBound()) {
				break;
			}

			
			g.color(_myDotColor);
			CCVector2 myUserPoint = _myController.curveToViewSpace(myCurrentPoint);
			if (myCurrentPoint.isSelected()) {
				g.color(Color.red);
			}

			g.point(myUserPoint);
			g.line(myUserPoint.x, height() / 2, myUserPoint.x, 0);
			g.color(_myDotColor);

			// g.drawString(myCurrentPoint.value() +"",
			// (int)myUserPoint.x, (int)myUserPoint.y);

//			if (myCurrentPoint.getType() == ControlPointType.BEZIER) {
//				BezierControlPoint myBezierPoint = (BezierControlPoint) myCurrentPoint;
//				
//				CCVector2 myUserHandle = _myController.curveToViewSpace(myBezierPoint.inHandle());
//				myUserHandle.setLocation(myUserHandle.x, myUserHandle.y / 2 + height() / 2);
//				line(myUserPoint, myUserHandle);
//				point(myUserHandle);
//
//				myUserHandle = _myController.curveToViewSpace(myBezierPoint.outHandle());
//				myUserHandle.setLocation(myUserHandle.x, myUserHandle.y / 2 + height() / 2);
//				line(myUserPoint, myUserHandle);
//				point(myUserHandle);
//			}
			myCurrentPoint = myCurrentPoint.next();
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
