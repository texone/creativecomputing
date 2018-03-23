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

import java.util.List;

import cc.creativecomputing.control.timeline.point.CCBezierControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint.CCControlPointType;
import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.controlui.timeline.controller.CCTrackContext;
import cc.creativecomputing.controlui.timeline.controller.CCTransportController;
import cc.creativecomputing.controlui.timeline.controller.CCTransportController.CCRulerInterval;
import cc.creativecomputing.controlui.timeline.controller.track.CCTrackController;
import cc.creativecomputing.controlui.timeline.view.transport.CCRulerView;
import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.gl.app.CCGLMouseButton;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shape.CCPath2D;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.widget.CCUIWidget;

public abstract class CCAbstractTrackDataView<ControllerType extends CCTrackController> extends CCUIWidget {

	public interface SwingTrackDataViewListener {
		void onRender(CCGraphics theG2D);
	}

	protected CCColor _myLineColor = CCUIConstants.LINE_COLOR;
	protected CCColor _myFillColor = CCUIConstants.FILL_COLOR;
	protected CCColor _myDotColor = CCUIConstants.DOT_COLOR;

	protected ControllerType _myController;
	protected CCTimelineController _myTimelineController;
	protected CCTrackContext _myTrackContext;

	private CCEventManager<CCGraphics> renderEvents = new CCEventManager<>();



	protected boolean _myIsMousePressed = false;

	protected CCGLMouseEvent _myMouseEvent = null;

	private CCTrackMenu<ControllerType> _myToolChooserPopup;

	public CCAbstractTrackDataView(CCTimelineController theTimelineController, ControllerType theTrackController) {
		_myTimelineController = theTimelineController;
		_myTrackContext = theTrackController.context();
		_myController = theTrackController;

		mousePressed.add(e -> {
			_myMouseEvent = e;
			boolean myIsRightClick = e.button == CCGLMouseButton.BUTTON_3 || (e.isControlDown() && e.button == CCGLMouseButton.BUTTON_1);

			if (myIsRightClick) {
				showPopUp(e);
			} else if (e.button == CCGLMouseButton.BUTTON_1) {
				_myIsMousePressed = true;
				_myController.mousePressed(e);
			}
		});

		mouseReleased.add(e -> {
			_myMouseEvent = e;
			if (e.button == CCGLMouseButton.BUTTON_1) {
				_myIsMousePressed = false;
				_myController.mouseReleased(e);
			}
		});

		mouseDragged.add(pos -> {
			if (_myMouseEvent.isAltDown() && !_myIsEnvelope) {
				_myTrackContext.zoomController().performDrag(pos, width());
				return;
			}
			_myController.mouseDragged(pos);
		});

		mouseMoved.add(pos -> {
			_myController.mouseMoved(pos);
		});
		
//			@Override
//			public void mouseExited(CCGLMouseEvent e) {
//				_myMouseEvent = null;
//				renderInfo();
//			}

		keyPressed.add(e -> {
			_myController.keyPressed(e);
		});	

		keyReleased.add(e -> {
			_myController.keyReleased(e);
		});

	}

	public void showPopUp(CCGLMouseEvent theEvent) {
		_myToolChooserPopup.show(theEvent);
	}

	public CCTrackController controller() {
		return _myController;
	}

	public CCTrackContext context() {
		return _myTrackContext;
	}

	public CCColor fillColor() {
		return _myFillColor;
	}

	public CCColor lineColor() {
		return _myLineColor;
	}

	public void color(CCColor theCCColor) {
		_myDotColor = theCCColor;
		_myLineColor = theCCColor.brighter(0.5);
		_myFillColor = theCCColor.brighter(0.25);
		_myLineColor.a = 0.5;
		_myFillColor.a = 0.5;
	}

	public void drawCurvePiece(CCControlPoint myFirstPoint, CCControlPoint mySecondPoint, List<CCVector2> thePath) {
		if (myFirstPoint.equals(mySecondPoint)) {
			return;
		}

		if (mySecondPoint == null) {
			mySecondPoint = new CCControlPoint(_myTrackContext.upperBound(), myFirstPoint.value());
		}

		boolean myIsBezier = false;
		CCVector2 p1 = _myController.curveToViewSpace(myFirstPoint);
		CCVector2 p2 = _myController.curveToViewSpace(mySecondPoint);
		double myA1X = p1.x;
		double myA1Y = p1.y;
		double myA2X = p2.x;
		double myA2Y = p2.y;
		double myX = p2.x;
		double myY = p2.y;

		if (mySecondPoint.type() == CCControlPointType.STEP) {
			thePath.add(new CCVector2(myA2X, myA1Y));
			thePath.add(new CCVector2(myA2X, myA2Y));
			return;
		}

		if (mySecondPoint.type() == CCControlPointType.BEZIER) {
			myIsBezier = true;
			CCBezierControlPoint myBezier2Point = (CCBezierControlPoint) mySecondPoint;
			CCVector2 myHandle = _myController.curveToViewSpace(myBezier2Point.inHandle());
			myA2X = myHandle.x;
			myA2Y = myHandle.y;

		}
		if (myFirstPoint.type() == CCControlPointType.BEZIER) {
			myIsBezier = true;
			CCBezierControlPoint myBezier1Point = (CCBezierControlPoint) myFirstPoint;
			CCVector2 myHandle = _myController.curveToViewSpace(myBezier1Point.outHandle());
			myA1X = myHandle.x;
			myA1Y = myHandle.y;
		}
		if (myIsBezier) {
			for(int i = 0; i < 20;i++){
				double myBlend = CCMath.norm(i, 0, 20);
				thePath.add(new CCVector2(
					CCMath.bezierPoint(p1.x, myA1X, myA2X, myX, myBlend),
					CCMath.bezierPoint(p1.y, myA1Y, myA2Y, myY, myBlend)
				));
			}
			return;
		}

		if (mySecondPoint.type() == CCControlPointType.LINEAR) {
			thePath.add(new CCVector2(myX, myY));
			return;
		}

		// if(mySecondPoint.type() == CCControlPointType.CUBIC &&
		// mySecondPoint.hasNext()){
		// ControlPoint myNextPoint = mySecondPoint.getNext();
		// CCVector2 myp2 = _myController.curveToViewSpace(myNextPoint);
		// thePath.quadTo(myX, myY, myp2.x, myp2.y);
		// return;
		// }

		// if(theDrawInterval){
		double myInterval = CCTrackView.GRID_INTERVAL / width() * (_myTrackContext.viewTime());
		double myStart = myInterval * Math.floor(myFirstPoint.time() / myInterval);

		for (double step = myStart + myInterval; step < mySecondPoint.time(); step = step + myInterval) {
			double myValue = _myController.trackData().value(step);
			p1 = _myController.curveToViewSpace(new CCControlPoint(step, myValue));
			thePath.add(new CCVector2(p1.x, p1.y));
		}
		// }

		thePath.add(new CCVector2(p2.x, p2.y));
	}

	// private void drawGridLines(Graphics g) {
	// double myNumberOfLines = (_myController.viewTime()) /
	// TrackView.GRID_INTERVAL;
	// int myIntervalFactor = 1;
	// if (myNumberOfLines > MAX_GRID_LINES) {
	// myIntervalFactor =  (myNumberOfLines / MAX_GRID_LINES + 1);
	// }
	// double myStart = TrackView.GRID_INTERVAL *
	// (Math.floor(_myController.lowerBound() / TrackView.GRID_INTERVAL));
	// for (double step = myStart; step <= _myController.upperBound(); step =
	// step + myIntervalFactor * TrackView.GRID_INTERVAL) {
	// double myX = (step - _myController.lowerBound()) /
	// (_myController.viewTime()) * width();
	// g.color(new CCColor(0.9f, 0.9f, 0.9f));
	// g.line( myX, 0,  myX, this.height());
	// }
	// }


	private boolean _myIsEnvelope = false;

	public void isEnvelope(boolean theDrawGrid) {
		_myIsEnvelope = theDrawGrid;
	}

//	public void point(CCVector2 thePoint) {
//		g.rect(
//			 thePoint.x - CCUIConstants.CURVE_POINT_SIZE / 2,
//			 thePoint.y - CCUIConstants.CURVE_POINT_SIZE / 2, 
//			CCUIConstants.CURVE_POINT_SIZE,
//			CCUIConstants.CURVE_POINT_SIZE
//		);
//	}


	public abstract void renderData(CCGraphics g);

	private double _myLastWidth = 0;
	private double _myLastHeight = 0;

	// does a full rendering of the function. we only need to do that if we're
	// visible and we edit points or
	// zoom in and out...
	private void renderImplementation(CCGraphics g) {

		if (width() <= 0 || height() <= 0)
			return;

		if (!_myController.isParentOpen())
			return;

		if (width() != _myLastWidth || height() != _myLastHeight || g == null) {
			_myLastWidth = width();
			_myLastHeight = height();
		}

		if (g == null)
			return;
		
		// paint background
		g.color(255);
		g.rect(0, 0, width(), height());

		// paint curve
		g.strokeWeight(1.5f);

		renderData(g);
	}

	private void drawTransportInfos(CCGraphics g) {
		// draw loop if existent
		if (_myTimelineController == null)
			return;

		if (_myTimelineController.transportController().doLoop()) {
			CCVector2 myLowerCorner = _myController.curveToViewSpace(new CCControlPoint(_myTimelineController.transportController().loopStart(), 1));
			CCVector2 myUpperCorner = _myController.curveToViewSpace(new CCControlPoint(_myTimelineController.transportController().loopEnd(), 0));

			g.color(new CCColor(0.15f, 0.15f, 0.15f, 0.05f));
			g.rect( myLowerCorner.x,  myLowerCorner.y,  myUpperCorner.x -  myLowerCorner.x,  myUpperCorner.y);
			g.color(new CCColor(0.8f, 0.8f, 0.8f));
			g.line( myLowerCorner.x, height() + 1,  myLowerCorner.x, 0);
			g.line( myUpperCorner.x, height() + 1,  myUpperCorner.x, 0);
		}

		double myTime = _myTimelineController.transportController().time();
		int myViewX = _myController.timeToViewX(myTime);

		if (myViewX >= 0 && myViewX <= width()) {
			g.color(new CCColor(0.1f, 0.1f, 0.1f, 0.5f));
			g.line(myViewX, 0, myViewX, height());
		}
	}

	public abstract void drawTimelineInfos(CCGraphics g);

	private void drawTimelineBack(CCGraphics g) {
		if (_myTimelineController == null) {
			return;
		}
		CCTransportController myTransportController = _myTimelineController.transportController();
		CCRulerInterval ri = myTransportController.rulerInterval();

		double myStart = ri.interval() * (Math.floor(myTransportController.lowerBound() / ri.interval()));

		for (double step = myStart; step <= myTransportController.upperBound(); step = step + ri.interval()) {

			int myX = myTransportController.timeToViewX(step);
			if (myX < 0)
				continue;

			g.color(CCRulerView.STEP_COLOR);
			g.strokeWeight(CCRulerView.THIN_STROKE);
			g.line(myX, 0, myX, height());

			g.color(CCRulerView.SUB_STEP_COLOR);
			g.strokeWeight(CCRulerView.THIN_STROKE);

			for (int i = 1; i < _myTimelineController.drawRaster(); i++) {
				myX = myTransportController.timeToViewX(step + ri.interval() * i / _myTimelineController.drawRaster());
				g.line(myX, 0, myX, height());
			}

		}

		CCControlPoint myCurrentPoint = _myTimelineController.transportController().trackData().getFirstPointAfter(_myTrackContext.lowerBound());

		while (myCurrentPoint != null) {
			if (myCurrentPoint.time() > _myTrackContext.upperBound())
				break;

			int myMarkerX = _myController.timeToViewX(myCurrentPoint.time());

			g.color(new CCColor(1f, 0f, 0f));
			g.line(myMarkerX, 0, myMarkerX, height());

			myCurrentPoint = myCurrentPoint.next();
		}
	}

	public void drawContent(CCGraphics g) {

		drawTimelineBack(g);

		if (_myIsEnvelope) {

			for (int i = 0; i <= 8; i++) {
				double myX =  CCMath.map(i, 0, 8, 0, width());
				double myY =  CCMath.map(i, 0, 8, 0, height());

				if (i % 2 == 0)
					g.color(CCColor.GRAY);
				else
					g.color(CCRulerView.STEP_COLOR);

				g.strokeWeight(CCRulerView.THIN_STROKE);
				g.line(myX, 0, myX, height());
				g.line(0, myY, width(), myY);
			}
		}

		renderImplementation(g);

		drawTransportInfos(g);
		if (_myTimelineController != null) {
			drawTimelineInfos(g);
		}

		// paint selection
		if (_myController.selection() != null) {

			CCVector2 myLowerCorner = _myController.curveToViewSpace(new CCControlPoint(_myController.selection().start, 1));
			CCVector2 myUpperCorner = _myController.curveToViewSpace(new CCControlPoint(_myController.selection().end, 0));

			g.color(CCUIConstants.SELECTION_COLOR);
			g.rect( myLowerCorner.x,  myLowerCorner.y,  myUpperCorner.x -  myLowerCorner.x,  myUpperCorner.y);

			g.color(CCUIConstants.SELECTION_BORDER_COLOR);
			g.line( myLowerCorner.x, height(),  myLowerCorner.x, 0);
			g.line( myUpperCorner.x,  myUpperCorner.y,  myUpperCorner.x, 0);
		}
	}

	public void update() {
		updateUI();
	}

	private void updateUI() {
		// TODO Auto-generated method stub
		
	}

	
}
