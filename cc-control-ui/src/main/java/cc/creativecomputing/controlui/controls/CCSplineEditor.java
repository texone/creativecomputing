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
import cc.creativecomputing.math.spline.CCCatmulRomSpline;
import cc.creativecomputing.math.spline.CCLinearSpline;
import cc.creativecomputing.math.spline.CCSpline;

public class CCSplineEditor extends CCGLApp {

	private CCSpline _mySpline;
	
	private CCVector3 _mySelectedPoint = null;
	private CCVector3 _myHighlightedPoint = null;
	
	private boolean _myShiftPressed = false;
	
	private CCVector2 _myMousePoint = new CCVector2();
	
	public CCSplineEditor(String theName) {
		
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
			if(_mySpline == null)return;
			_mySelectedPoint = null;
		});
		mousePressEvents.add(e -> {
			if(_mySpline == null)return;
			
			_mySelectedPoint = selectPoint(e.x, e.y);
		});
		mouseClickEvents.add(e -> {
			if(_mySpline == null)return;
			
			if(e.clickCount != 2)return;
			
			CCVector3 myPoint = selectPoint(e.x, e.y);
			if(myPoint == null){
				_mySpline.beginEditSpline();
				_mySpline.addPoint(new CCVector3(mouseToRelative(e.x, e.y)));
				_mySpline.endEditSpline();
				return;
			}
			if(_mySpline instanceof CCBezierSpline){
				int myIndex = _mySpline.points().indexOf(myPoint);
				switch(myIndex % 3){
				case 0:
					if(myIndex > 0){
						_mySpline.points().remove(myIndex - 1);
						_mySpline.points().remove(myIndex - 1);
						_mySpline.points().remove(myIndex - 1);
					}else{
						_mySpline.points().remove(0);
						_mySpline.points().remove(0);
						_mySpline.points().remove(0);
					}
					break;
				case 1:
					myPoint.set(_mySpline.points().get(myIndex - 1));
					break;
				case 2:
					myPoint.set(_mySpline.points().get(myIndex + 1));
					break;
				}
			}else{
				_mySpline.beginEditSpline();
				_mySpline.points().remove(myPoint);
				_mySpline.endEditSpline();
			}
		});

		mouseMoveEvents.add(e ->{
			if(_mySpline == null)return;
			_myMousePoint.x = e.x;
			_myMousePoint.y = e.y;
			_myHighlightedPoint = selectPoint(e.x, e.y);
		});
		
		mouseDragEvents.add(e -> {
			if(_mySpline == null)return;
			if(_mySelectedPoint == null)return;
			
			CCVector3 myNewPos = mouseToRelative(e.x, e.y);
			CCVector3 myMotion = myNewPos.subtract(_mySelectedPoint);
			_mySelectedPoint.set(myNewPos);
			
			if(_mySpline instanceof CCBezierSpline){
				int myIndex = _mySpline.points().indexOf(_mySelectedPoint);
				switch(myIndex % 3){
				case 0:
					if(myIndex > 0){
						_mySpline.points().get(myIndex - 1).addLocal(myMotion);
					}
					if(myIndex < _mySpline.points().size() - 1){
						_mySpline.points().get(myIndex + 1).addLocal(myMotion);
					}
					break;
				case 1:
					if(_myShiftPressed && myIndex > 1){
						CCVector3 myAnchor = _mySpline.points().get(myIndex - 1);
						CCVector3 myControl2 = _mySpline.points().get(myIndex - 2);
						CCVector3 myDifference = _mySelectedPoint.subtract(myAnchor);
						myControl2.set(myAnchor.subtract(myDifference));
					}
					break;
				case 2:
					if(_myShiftPressed && myIndex <  _mySpline.points().size() - 2){
						CCVector3 myAnchor = _mySpline.points().get(myIndex + 1);
						CCVector3 myControl2 = _mySpline.points().get(myIndex + 2);
						CCVector3 myDifference = _mySelectedPoint.subtract(myAnchor);
						myControl2.set(myAnchor.subtract(myDifference));
					}
					break;
				}
			}
		});
	}
	
	private CCVector3 mouseToRelative(double theX, double theY){
		return new CCVector3(
			CCMath.saturate(CCMath.norm(theX, 10, width - 10)),
			CCMath.saturate(CCMath.norm(theY, 10, height - 10))
		);
	}
	
	private CCVector3 relativeToMouse(CCVector3 theRelative){
		return new CCVector3(
			CCMath.blend(10, width - 10, theRelative.x), 
			CCMath.blend(10, height - 10, theRelative.y)
		);
	}
	
	public void spline(CCSpline theSpline){
		_mySpline = theSpline;
	}
	
	private CCVector3 selectPoint(double theX, double theY){
		for(CCVector3 myPoint:_mySpline.points()){
			CCVector3 myMouseCoord = relativeToMouse(myPoint);
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
	public static final int CURVE_POINT_SIZE = 10;
	
	private CCVector3 mouseSplinePoint(int i){
		return relativeToMouse(_mySpline.points().get(i));
	}
	
	@Override
	public void display(CCGraphics g) {
		if(_mySpline == null)return;
		if(_mySpline.points().size() <= 0)return;

		g.clear();
		g.pushAttribute();
		g.pushMatrix();
		g.ortho();
		if(_mySpline instanceof CCLinearSpline){
			g.color(CCColor.LIGHT_GRAY);
			g.beginShape(CCDrawMode.LINE_STRIP);
			for(int i = 0; i < _mySpline.points().size();i++){
				CCVector3 myPoint = mouseSplinePoint(i);
				g.vertex(myPoint.x, myPoint.y);
			}
			g.endShape();
		}else if(_mySpline instanceof CCCatmulRomSpline || _mySpline instanceof CCBezierSpline){
			g.color(CCColor.LIGHT_GRAY);
			g.beginShape(CCDrawMode.LINE_STRIP);
			for(double i = 0; i <= _mySpline.points().size() * 100;i++){
				double myBlend = i / (_mySpline.points().size() * 100);
				CCVector3 myPoint = relativeToMouse(_mySpline.interpolate(myBlend));
				g.vertex(myPoint.x, myPoint.y);
			}
			g.endShape();
		}
		
		if(_mySpline instanceof CCBezierSpline){
			g.color(CCColor.GRAY);
			g.beginShape(CCDrawMode.LINES);
			for(int i = 1; i < _mySpline.points().size();i+=3){
				CCVector3 myPoint = mouseSplinePoint(i-1);
				CCVector3 myPoint1 = mouseSplinePoint(i);
				CCVector3 myPoint2 = mouseSplinePoint(i + 1);
				CCVector3 myPoint3 = mouseSplinePoint(i + 2);

				g.vertex(myPoint.x, myPoint.y); 
				g.vertex(myPoint1.x, myPoint1.y);
				g.vertex(myPoint2.x, myPoint2.y);
				g.vertex(myPoint3.x, myPoint3.y);
			}
			g.endShape();
		}
			
		g.pointSize(CURVE_POINT_SIZE);
		g.beginShape(CCDrawMode.POINTS);
		g.color(CCColor.WHITE);
		for(CCVector3 myPoint:_mySpline.points()){
			CCVector3 mySplinePoint = relativeToMouse(myPoint);
			g.vertex(mySplinePoint.x, mySplinePoint.y);
		}

		g.color(CCColor.RED);
		if(_myHighlightedPoint != null)g.vertex(relativeToMouse(_myHighlightedPoint));
		g.endShape();
		g.popMatrix();
		g.popAttribute();
	}

	public static void main(String[] args) {
		CCSplineEditor myDemo = new CCSplineEditor("spline editor");
		myDemo.width = 1800;
		myDemo.height = 1000;
		
		CCBezierSpline mySpline = new CCBezierSpline();
		mySpline.beginEditSpline();
		for(int i = 0; i < 10; i++){
			mySpline.addPoint(new CCVector3(CCMath.random(), CCMath.random()));
		}
		mySpline.endEditSpline();
		myDemo._mySpline = mySpline;
		CCGLApplicationManager myApplicationManager = new CCGLApplicationManager(myDemo);
		myApplicationManager.run();
	}
}
