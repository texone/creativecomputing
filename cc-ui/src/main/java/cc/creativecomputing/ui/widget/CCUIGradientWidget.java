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
package cc.creativecomputing.ui.widget;

import java.util.Collections;

import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.control.CCGradientPoint;
import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCUIGradientWidget extends CCUIWidget {
	
	private static double X_SELECT_RANGE = 10;
	private static double POINT_RADIUS = 5;

    private int _mySelectedPoint;

	private CCGradient _myGradient = new CCGradient();

	private CCUIColorWheel _myColorWheel;
	
	public CCEventManager<CCGradient> changeEvents = new CCEventManager<>();

	boolean myClose = false;
	/**
	 * Create a new editor for gradients
	 *
	 */
	public CCUIGradientWidget(double theWidth, double theHeight) {
		super(theWidth, theHeight);

		_myOverlay = _myColorWheel = new CCUIColorWheel(200);
		_myColorWheel.isActive(false);
		_myColorWheel.translation().set(-_myColorWheel.width() / 2 + width() / 2, _myColorWheel.height() / 2  - height() / 2);

		_myColorWheel.mouseReleasedOutside.add(event ->{
			_myColorWheel.isActive(false);
			myClose = true;
		});
		_myColorWheel.changeEvents.add(c -> {
			color(c);
		});
		_myColorWheel.mouseClicked.add(e ->{
			if(!_myColorWheel.isInsideLocal(new CCVector2(e.x,e.y)))
				_myColorWheel.isActive(false);
		});
		
		mousePressed.add(event -> {
			selectPoint(event);
		});
		
		mouseClicked.add(event -> {
			if(myClose) {
				myClose = false;
				return;
			}
//			if (event.clickCount != 1) return;
			if(_mySelectedPoint != -1){
				if(event.isShiftDown())delPoint();
				else editPoint();
			}else{
				double myPos = CCMath.norm(event.x, 0, width());
				addPoint(myPos);
				movePoint(new CCVector2(event.x, event.y));
			}
		});
		
		mouseDragged.add(pos -> {
			movePoint(pos);
		});
	}
	
	public void gradient(CCGradient theGradient){
		_myGradient = theGradient;
	}

	private boolean checkPoint(CCGLMouseEvent theE, CCGradientPoint thePoint) {
		double dx = CCMath.abs((width() * thePoint.position()) - theE.x);
        return (dx < X_SELECT_RANGE) && theE.y  < 0 && theE.y > -height();
    }

	/**
	 * Add a new control point
	 */
	private void addPoint(double thePosition) {
		CCGradientPoint point = new CCGradientPoint(thePosition, CCColor.WHITE.clone());
		_myGradient.add(point);
		_mySelectedPoint = _myGradient.indexOf(point);
		changeEvents.event(_myGradient);
	}
	

	/**
	 * Edit the currently selected control point
	 *
	 */
	private void editPoint() {
		CCLog.info(_mySelectedPoint);
		if (_mySelectedPoint == -1) {
			return;
		}
		_myColorWheel.setFromColor(_myGradient.get(_mySelectedPoint).color());
		_myOverlay.isActive(true);
	}
	
	private void color(CCColor theColor){
		if(theColor == null)return;
		_myGradient.get(_mySelectedPoint).color().set(theColor);
		changeEvents.event(_myGradient);
	}

	/**
	 * Select the control point at the specified mouse coordinate
	 * 
	 * @param mx The mouse x coordinate
	 * @param my The mouse y coordinate
	 */
	private void selectPoint(CCGLMouseEvent theE) {
		if(_myGradient.size() == 0){
			_mySelectedPoint = -1;
			return;
		}

		for (int i = 1; i < _myGradient.size() - 1; i++) {
			if (checkPoint(theE,  _myGradient.get(i))) {
				_mySelectedPoint =  i;
				return;
			}
		}
		if (checkPoint(theE,  _myGradient.get(0))) {
			_mySelectedPoint =  0;
			return;
		}
		if (checkPoint(theE,  _myGradient.get(_myGradient.size() - 1))) {
			_mySelectedPoint =  _myGradient.size() - 1;
			return;
		}

		_mySelectedPoint = -1;
	}

	/**
	 * Delete the currently selected point
	 */
	private void delPoint() {
		if (_mySelectedPoint == -1) {
			return;
		}
		if (_myGradient.indexOf(_mySelectedPoint) == 0) {
			return;
		}
		if (_myGradient.indexOf(_mySelectedPoint) == _myGradient.size() - 1) {
			return;
		}

		_myGradient.remove(_mySelectedPoint);
	}

	/**
	 * Move the current point to the specified mouse location
	 * 
	 * @param mx The x coordinate of the mouse
	 * @param my The y coordinate of teh mouse
	 */
	private void movePoint(CCVector2 theE) {

		if (_mySelectedPoint == -1) {
			return;
		}

		double newPos = theE.x  /  width();
		newPos = CCMath.saturate(newPos);

		_myGradient.get(_mySelectedPoint).position(newPos);
		Collections.sort(_myGradient);
		changeEvents.event(_myGradient);
	}

	@Override
	public void drawContent(CCGraphics g) {
//		width() = getWidth() - 25;

		g.beginShape(CCDrawMode.LINES);
		for(int i = 0; i <= width();i++){
			double blend = (double)i / width();
			g.color(_myGradient.color(blend));
			g.vertex(i, 0);
			g.vertex(i, -height() - 1);
		}
		g.endShape();

		g.color(CCColor.DARK_GRAY);
		
		for (int i = 0; i < _myGradient.size(); i++) {
			CCGradientPoint pt =  _myGradient.get(i);

			g.color(pt.color());
			g.ellipse((width() * pt.position()), -height() / 2, POINT_RADIUS);
			g.color(0);
			g.ellipse((width() * pt.position()), -height() / 2, 0, POINT_RADIUS, POINT_RADIUS,  true);
			
//			if (i == _mySelectedPoint) {
//				g.line(-4, 10, 4, 10);
//			}
		}
	}
}
