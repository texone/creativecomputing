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
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint.CCControlPointType;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.gl.app.CCGLKey;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCBezierSpline;

public class CCEnvelopeEditor extends CCGLApp {

	private CCTrackData _myTrackData;
	
	private CCControlPoint _mySelectedPoint = null;
	private CCControlPoint _myHighlightedPoint = null;
	
	private boolean _myShiftPressed = false;
	
	private CCVector2 _myMousePoint = new CCVector2();
	
	public CCEnvelopeEditor(String theName) {
		
		width = 200;
		height = 200;
		title = theName;
		
		keyPressEvents.add(e -> {
			if(e.key == CCGLKey.KEY_LEFT_SHIFT || e.key == CCGLKey.KEY_RIGHT_SHIFT){
				_myShiftPressed = true;
			}
		});
		keyReleaseEvents.add(e -> {
			if(e.key == CCGLKey.KEY_LEFT_SHIFT || e.key == CCGLKey.KEY_RIGHT_SHIFT){
				_myShiftPressed = false;
			}
		});
		
		mouseReleaseEvents.add(e -> {
			if(_myTrackData == null)return;
			_mySelectedPoint = null;
		});
		mousePressEvents.add(e -> {
			if(_myTrackData == null)return;
			
			_mySelectedPoint = selectPoint(e.x, e.y);
		});
		mouseClickEvents.add(e -> {
			if(_myTrackData == null)return;
			
			if(e.clickCount != 2)return;
			
			CCControlPoint myPoint = selectPoint(e.x, e.y);
			if(myPoint == null){
				myPoint = mouseToTrack(e.x, e.y);
				myPoint.type(CCControlPointType.LINEAR);
				_myTrackData.add(myPoint);
				return;
			}
			
			_myTrackData.remove(myPoint);
			_myHighlightedPoint = null;
		});

		mouseMoveEvents.add(e ->{
			if(_myTrackData == null)return;
			_myMousePoint.x = e.x;
			_myMousePoint.y = e.y;
			_myHighlightedPoint = selectPoint(e.x, e.y);
		});
		
		mouseDragEvents.add(e -> {
			if(_myTrackData == null)return;
			if(_mySelectedPoint == null)return;
			
			_myTrackData.move(_mySelectedPoint, mouseToTrack(e.x, e.y));
		});
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
	}
	

	public static final int CURVE_POINT_SIZE = 10;
	
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
		
		CCControlPointType myPreviousType = CCControlPointType.LINEAR;
		for(CCControlPoint myPoint:myPoints){
			switch(myPreviousType){
			case STEP:
				g.vertex(myPoint.time(), myPoint.previous().value());
				g.vertex(myPoint.time(), 1d);
				g.vertex(myPoint.time(), myPoint.value());
				g.vertex(myPoint.time(), 1d);
				break;
			case LINEAR:
				g.vertex(myPoint.time(), myPoint.value());
				g.vertex(myPoint.time(), 1d);
				break;
			}
			myPreviousType = myPoint.type();
			
		}
		g.endShape();
		

		g.color(1d,0.5);
		g.beginShape(CCDrawMode.LINE_STRIP);
		
		myPreviousType = CCControlPointType.LINEAR;
		for(CCControlPoint myPoint:myPoints){
			switch(myPreviousType){
			case STEP:
				g.vertex(myPoint.time(), myPoint.previous().value());
				g.vertex(myPoint.time(), myPoint.value());
				break;
			case LINEAR:
				g.vertex(myPoint.time(), myPoint.value());
				break;
			}
			myPreviousType = myPoint.type();
		}
		g.endShape();
		
		g.color(1d);
		g.pointSize(CURVE_POINT_SIZE);
		g.beginShape(CCDrawMode.POINTS);
		for(CCControlPoint myPoint0:myRangeList){
			g.vertex(myPoint0.time(), myPoint0.value());
		}
		
		g.color(CCColor.RED);
		if(_myHighlightedPoint != null){
			g.vertex(_myHighlightedPoint.time(), _myHighlightedPoint.value());
		}
		g.endShape();
		
		g.popMatrix();
		g.popAttribute();
	}

	public static void main(String[] args) {
		CCEnvelopeEditor myDemo = new CCEnvelopeEditor("spline editor");
		myDemo.width = 1800;
		myDemo.height = 1000;
		
		CCBezierSpline mySpline = new CCBezierSpline();
		mySpline.beginEditSpline();
		for(int i = 0; i < 10; i++){
			mySpline.addPoint(new CCVector3(CCMath.random(), CCMath.random()));
		}
		mySpline.endEditSpline();
		myDemo._myTrackData = new CCTrackData();
		myDemo._myTrackData.add(new CCControlPoint(-0.1, CCMath.random(), CCControlPointType.LINEAR));
		for(int i = 0; i < 10;i++){
			myDemo._myTrackData.add(new CCControlPoint(CCMath.random(), CCMath.random(), CCControlPointType.LINEAR));
			myDemo._myTrackData.add(new CCControlPoint(CCMath.random(), CCMath.random(), CCControlPointType.STEP));
		}
		myDemo._myTrackData.add(new CCControlPoint(1.1, CCMath.random(), CCControlPointType.LINEAR));
		CCGLApplicationManager myApplicationManager = new CCGLApplicationManager(myDemo);
		myApplicationManager.run();
	}
}
