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
package cc.creativecomputing.controlui.timeline.controller.tools;

import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.controlui.timeline.controller.track.CCTrackController;
import cc.creativecomputing.gl.app.CCGLKey;
import cc.creativecomputing.gl.app.CCGLKeyEvent;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.math.CCVector2;

public class CCTimelineTool<ControllerType extends CCTrackController> {
	
    protected double _myPressX;
    protected double _myPressY;
    
    protected double _myMovX;
    protected double _myMovY;
    
    protected CCVector2 _myPressViewCoords;
    protected CCControlPoint _myPressCurveCoords;

    protected CCVector2 _myViewCoords;
    protected CCControlPoint _myCurveCoords;
    protected CCControlPoint _myCurveMovement;

    protected boolean _mySnap = false;
    
    protected ControllerType _myController;

	public CCTimelineTool(boolean theSnap, ControllerType theController){
		_mySnap = theSnap;
		_myController = theController;
	}
	
	protected CCGLKey _myKeyCode;
	
	protected boolean _myIsShiftDown = false;
	protected boolean _myIsAltDown = false;
	protected boolean _myIsCTRLDown = false;
	protected boolean _myIsSuperDown = false;
	
	public void keyPressed(CCGLKeyEvent e) {
		_myKeyCode = e.key;
		switch (e.key) {
		case KEY_LEFT_SHIFT:
		case KEY_RIGHT_SHIFT:
			_myIsShiftDown = true;
			break;
		case KEY_LEFT_ALT:
		case KEY_RIGHT_ALT:
			_myIsAltDown = true;
			break;
		case KEY_LEFT_CONTROL:
		case KEY_RIGHT_CONTROL:
			_myIsCTRLDown = true;
			break;
		case KEY_LEFT_SUPER:
		case KEY_RIGHT_SUPER:
			_myIsSuperDown = true;
			break;

		default:
			break;
		}
	}
	
	public void keyReleased(CCGLKeyEvent e) {
		_myKeyCode = null;
	}
	
	public void mousePressed(CCGLMouseEvent theEvent){
		_myPressX = theEvent.x;
		_myPressY = theEvent.y;
		
		_myPressViewCoords = new CCVector2(theEvent.x, theEvent.y);
		_myPressCurveCoords = _myController.viewToCurveSpace(_myPressViewCoords, true);
	}
	
	private void updateMotion(CCVector2 theEvent){
		_myMovX = _myPressX - theEvent.x;
		_myMovY = _myPressY - theEvent.y;
		
		if(_myKeyCode == CCGLKey.KEY_X) {
			_myMovY = 0;
		}
		if(_myKeyCode == CCGLKey.KEY_Y) {
			_myMovX = 0;
		}

		_myViewCoords = new CCVector2(theEvent.x, theEvent.y);
		_myCurveCoords = _myController.viewToCurveSpace(_myViewCoords, true);
		
		if(_myMovY > 0){
			_myCurveMovement = _myController.viewToCurveSpace(new CCVector2(-_myMovX, _myMovY), false);
			_myCurveMovement.value(1 - _myCurveMovement.value());
		}else{
			_myCurveMovement = _myController.viewToCurveSpace(new CCVector2(-_myMovX, -_myMovY), false);
			_myCurveMovement.value( _myCurveMovement.value() - 1);
		}
	} 
	
	public void mouseMoved(CCVector2 theEvent){
		_myViewCoords = new CCVector2(theEvent.x, theEvent.y);
		_myCurveCoords = _myController.viewToCurveSpace(_myViewCoords, true);
	}
	
	public void mouseDragged(CCVector2 theEvent){
		updateMotion(theEvent);
	}
	
	public void mouseReleased(CCGLMouseEvent theEvent){
		_myKeyCode = null;
	}
}
