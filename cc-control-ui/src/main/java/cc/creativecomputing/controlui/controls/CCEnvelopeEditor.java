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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.timeline.CCTrackData;
import cc.creativecomputing.control.timeline.point.CCBezierControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint.CCControlPointType;
import cc.creativecomputing.controlui.timeline.tools.CCTimedContentView;
import cc.creativecomputing.controlui.timeline.tools.CCTools;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCBezierSpline;

public class CCEnvelopeEditor extends CCGLApp {

	private CCTrackData _myTrackData;
	
	private CCTools _myTools;
	
	public CCEnvelopeEditor(String theName) {
		
		CCTimedContentView myView = new CCTimedContentView(){

			@Override
			public double timeToViewX(double theTime) {return theTime * g.width();}
			
			@Override
			public double valueToViewY(double theValue) {return theValue * g.height();}

			@Override
			public double viewXToTime(double theViewX) {return theViewX / g.width();}

			@Override
			public double viewWidthToTime(double theViewWidth) {return theViewWidth / g.width();}

			@Override
			public CCControlPoint viewToCurveSpace(CCVector2 theViewCoords, boolean b) {
				return new CCControlPoint(theViewCoords.x / g.width(), theViewCoords.y / g.height());
			}

			@Override
			public CCVector2 curveToViewSpace(CCControlPoint myControlPoint) {
				return new CCVector2(myControlPoint.time() * g.width(), myControlPoint.value() * g.height());
			}

			@Override
			public CCControlPoint quantize(CCControlPoint myTargetPosition) {return myTargetPosition;}

			@Override
			public double viewTime() {return 1;}
			
		};
		
		
		_myTools = new CCTools(this, myView);
		
		
		keyReleaseEvents.add(_myTools::keyReleased);

		width = 200;
		height = 200;
		title = theName;

		scrollEvents.add(e ->{CCLog.info(e);});
		
		
// ICON_ERASER
// ICON_EDIT
// ICON_HAND
// ICON_GRID
// ICON_HAIRCROSS
// ICON_MAGNET
// ICON_LINE_GRAPH
		
//		keyPressEvents.add(e -> {
//			if(e.key == CCGLKey.KEY_LEFT_SHIFT || e.key == CCGLKey.KEY_RIGHT_SHIFT){
//				_myShiftPressed = true;
//			}
//		});
//		keyReleaseEvents.add(e -> {
//			if(e.key == CCGLKey.KEY_LEFT_SHIFT || e.key == CCGLKey.KEY_RIGHT_SHIFT){
//				_myShiftPressed = false;
//			}
//		});
//		
//		mouseReleaseEvents.add(e -> {
//			if(_myTrackData == null)return;
//			_mySelectedPoint = null;
//		});
//		mousePressEvents.add(e -> {
//			if(_myTrackData == null)return;
//			
//			_mySelectedPoint = selectPoint(e.x, e.y);
//		});
//		mouseClickEvents.add(e -> {
//			if(_myTrackData == null)return;
//			
//			if(e.clickCount != 2)return;
//			
//			CCControlPoint myPoint = selectPoint(e.x, e.y);
//			if(myPoint == null){
//				myPoint = mouseToTrack(e.x, e.y);
//				myPoint.type(CCControlPointType.LINEAR);
//				_myTrackData.add(myPoint);
//				return;
//			}
//			
//			_myTrackData.remove(myPoint);
//			_myHighlightedPoint = null;
//		});
//
//		mouseMoveEvents.add(e ->{
//			if(_myTrackData == null)return;
//			_myMousePoint.x = e.x;
//			_myMousePoint.y = e.y;
//			_myHighlightedPoint = selectPoint(e.x, e.y);
//		});
//		
//		mouseDragEvents.add(e -> {
//			if(_myTrackData == null)return;
//			if(_mySelectedPoint == null)return;
//			
//			_myTrackData.move(_mySelectedPoint, mouseToTrack(e.x, e.y));
//		});
	}
	
	private CCControlPoint selectPoint(double theX, double theY){
		for(CCControlPoint myPoint:_myTrackData){
			CCVector2 myMouseCoord = trackToMouse(myPoint);
			double x0 = myMouseCoord.x - CURVE_POINT_SIZE / 2;
			double y0 = myMouseCoord.y - CURVE_POINT_SIZE / 2;
			if(
				theX >= x0 && 
				theX <= x0 + CURVE_POINT_SIZE && 
				theY >= y0 && 
				theY <= y0 + CURVE_POINT_SIZE
			){
				return myPoint;
			}
		}
		
		return null;
	}
	
	private CCControlPoint mouseToTrack(double theX, double theY){
		return new CCControlPoint(
			CCMath.saturate(CCMath.norm(theX, 0, width)),
			CCMath.saturate(CCMath.norm(theY, 0, height))
		);
	}
	
	private CCVector2 trackToMouse(CCControlPoint thePoint){
		return new CCVector2(
			CCMath.blend(0, width, thePoint.time()), 
			CCMath.blend(0, height, thePoint.value())
		);
	}
	
	public void trackData(CCTrackData theData){
		_myTrackData = theData;
		_myTools.trackData(_myTrackData);
	}
	

	public static final int CURVE_POINT_SIZE = 10;
	
	private void drawPath(List<CCControlPoint> myPoints, CCGraphics g, boolean theDrawFill){
		CCControlPointType myPreviousType = CCControlPointType.LINEAR;
		for(CCControlPoint myPoint:myPoints){
			switch(myPreviousType){
			case STEP:
				g.vertex(myPoint.time(), myPoint.previous().value());
				if(theDrawFill)g.vertex(myPoint.time(), 1d);
				g.vertex(myPoint.time(), myPoint.value());
				if(theDrawFill)g.vertex(myPoint.time(), 1d);
				break;
			case LINEAR:
				g.vertex(myPoint.time(), myPoint.value());
				if(theDrawFill)g.vertex(myPoint.time(), 1d);
				break;
			case BEZIER:
				if(myPoint.previous() == null){
					continue;
				}
				CCControlPoint myPrevPoint = myPoint.previous();
				
				double x1 = myPrevPoint.time();
				double y1 = myPrevPoint.value();
				
				double x2 = myPrevPoint.time();
				double y2 = myPrevPoint.value();
				if(myPrevPoint.type() == CCControlPointType.BEZIER){
					CCBezierControlPoint myPrevBezierPoint = (CCBezierControlPoint)myPoint.previous();
					x2 = CCMath.min(myPrevBezierPoint.outHandle().time(), myPoint.time());
					y2 = myPrevBezierPoint.outHandle().value();
				}
				
				double x3 = myPoint.time();
				double y3 = myPoint.value();
				if(myPoint.type() == CCControlPointType.BEZIER){
					CCBezierControlPoint myBezierPoint = (CCBezierControlPoint)myPoint;
					x3 = CCMath.max(myBezierPoint.inHandle().time(), myPrevPoint.time());
					y3 = myBezierPoint.inHandle().value();
				}
				double x4 = myPoint.time();
				double y4 = myPoint.value();
				
				for(double i = 0; i < g.bezierDetail();i++){
					double t = i / g.bezierDetail();
					double myX = CCMath.bezierPoint(x1, x2, x3, x4, t);
					double myY = CCMath.bezierPoint(y1, y2, y3, y4, t);
					g.vertex(myX, myY);
					if(theDrawFill)g.vertex(myX, 1d);
				}
				
				break;
			}
			myPreviousType = myPoint.type();
			
		}
	}
	
	@Override
	public void display(CCGraphics g) {
		if(_myTrackData == null)return;
		if(_myTrackData.size() <= 0)return;

		g.clear();
		g.pushAttribute();
		g.pushMatrix();
		g.ortho();
		g.scale(g.width(), g.height());
		
		List<CCControlPoint> myRangeList = _myTrackData.rangeList(0, 1);
		CCControlPoint myFirstPointOut = _myTrackData.getLastPointBefore(0);
		CCControlPoint myFirstPointIn = _myTrackData.getFirstPointAfter(0);
		CCControlPoint myLastPointOut = _myTrackData.getFirstPointAfter(1.);
		CCControlPoint myLastPointIn = _myTrackData.getLastPointBefore(1.);
		
		g.color(1d,0.25);
		
		List<CCControlPoint> myPoints = new ArrayList<>(); 

		if(myFirstPointOut != null){
			myPoints.add(myFirstPointOut);
		}else if(myFirstPointIn != null){
			myPoints.add(new CCControlPoint(0, myFirstPointIn.value()));
		}
		myPoints.addAll(myRangeList);
		if(myLastPointOut != null){
			myPoints.add(myLastPointOut);
		}else if(myLastPointIn != null){
			myPoints.add(new CCControlPoint(1, myLastPointIn.value()));
		}
		
		g.beginShape(CCDrawMode.TRIANGLE_STRIP);
		drawPath(myPoints, g, true);
		g.endShape();
		

		g.color(1d,0.5);
		g.beginShape(CCDrawMode.LINE_STRIP);
		drawPath(myPoints, g, false);
		g.endShape();
		
		if(_myTools.isCurve()){
			g.color(1d,0.25);
			g.beginShape(CCDrawMode.LINES);
			for(CCControlPoint myPoint0:myRangeList){
				if(myPoint0.type() == CCControlPointType.BEZIER){
					CCBezierControlPoint myBezierControlPoint = (CCBezierControlPoint)myPoint0;
					g.vertex(myPoint0.time(), myPoint0.value());
					g.vertex(myBezierControlPoint.inHandle().time(), myBezierControlPoint.inHandle().value());
					g.vertex(myPoint0.time(), myPoint0.value());
					g.vertex(myBezierControlPoint.outHandle().time(), myBezierControlPoint.outHandle().value());
				}
			}
			g.endShape();
		}
		
		g.pointSize(CURVE_POINT_SIZE);
		g.beginShape(CCDrawMode.POINTS);
		for(CCControlPoint myPoint0:myRangeList){
			g.color(1d);
			g.vertex(myPoint0.time(), myPoint0.value());
			if(myPoint0.type() == CCControlPointType.BEZIER && _myTools.isCurve()){

				g.color(1d,0.5);
				CCBezierControlPoint myBezierControlPoint = (CCBezierControlPoint)myPoint0;
				g.vertex(myBezierControlPoint.inHandle().time(), myBezierControlPoint.inHandle().value());
				g.vertex(myBezierControlPoint.outHandle().time(), myBezierControlPoint.outHandle().value());
			}
		}
		
		g.color(CCColor.RED);
		for(CCControlPoint myPoint:_myTools.selection()){
			g.vertex(myPoint.time(), myPoint.value());
		}
		g.endShape();
		g.clearDepthBuffer();
		_myTools.drawTimeSpace(g);
		
		g.popMatrix();
		
		g.clearDepthBuffer();
		_myTools.drawViewSpace(g);
		g.popAttribute();
	}

	public static void main(String[] args) {
		CCEnvelopeEditor myDemo = new CCEnvelopeEditor("envelope editor");
		myDemo.width = 900;
		myDemo.height = 500;
		
		CCBezierSpline mySpline = new CCBezierSpline();
		mySpline.beginEditSpline();
		for(int i = 0; i < 10; i++){
			mySpline.addPoint(new CCVector3(CCMath.random(), CCMath.random()));
		}
		mySpline.endEditSpline();
		CCTrackData myTrackData = new CCTrackData();
		myTrackData.add(new CCControlPoint(-0.1, CCMath.random(), CCControlPointType.LINEAR));
		for(int i = 0; i < 10;i++){
//			myTrackData.add(new CCControlPoint(CCMath.random(), CCMath.random(), CCControlPointType.LINEAR));
//			myTrackData.add(new CCControlPoint(CCMath.random(), CCMath.random(), CCControlPointType.STEP));
			double myTime = CCMath.random();
			CCBezierControlPoint myBezier = new CCBezierControlPoint(myTime, CCMath.random());
			myBezier.inHandle().time(myTime - 0.1);
			myBezier.outHandle().time(myTime + 0.1);
			myTrackData.add(myBezier);
		}
		myTrackData.add(new CCControlPoint(1.1, CCMath.random(), CCControlPointType.LINEAR));
		myDemo.trackData(myTrackData);
		CCGLApplicationManager myApplicationManager = new CCGLApplicationManager(myDemo);
		myApplicationManager.run();
	}
}
