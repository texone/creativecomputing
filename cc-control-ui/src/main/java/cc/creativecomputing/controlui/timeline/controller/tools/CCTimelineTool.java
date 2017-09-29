package cc.creativecomputing.controlui.timeline.controller.tools;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.controlui.timeline.controller.track.CCTrackController;

public class CCTimelineTool<ControllerType extends CCTrackController> {
	
    protected int _myPressX;
    protected int _myPressY;
    
    protected int _myMovX;
    protected int _myMovY;
    
    protected Point2D _myPressViewCoords;
    protected ControlPoint _myPressCurveCoords;

    protected Point2D _myViewCoords;
    protected ControlPoint _myCurveCoords;
    protected ControlPoint _myCurveMovement;

    protected boolean _mySnap = false;
    
    protected ControllerType _myController;

	public CCTimelineTool(boolean theSnap, ControllerType theController){
		_mySnap = theSnap;
		_myController = theController;
	}
	
	protected int _myKeyCode;
	
	public void keyPressed(KeyEvent e) {
		_myKeyCode = e.getKeyCode();
	}
	
	public void keyReleased(KeyEvent e) {
		_myKeyCode = -1;
	}
	
	public void mousePressed(MouseEvent theEvent){
		_myPressX = theEvent.getX();
		_myPressY = theEvent.getY();
		
		_myPressViewCoords = new Point2D.Double(theEvent.getX(), theEvent.getY());
		_myPressCurveCoords = _myController.viewToCurveSpace(_myPressViewCoords, true);
	}
	
	private void updateMotion(MouseEvent theEvent){
		_myMovX = _myPressX - theEvent.getX();
		_myMovY = _myPressY - theEvent.getY();
		
		if(_myKeyCode == KeyEvent.VK_X) {
			_myMovY = 0;
		}
		if(_myKeyCode == KeyEvent.VK_Y) {
			_myMovX = 0;
		}

		_myViewCoords = new Point2D.Double(theEvent.getX(), theEvent.getY());
		_myCurveCoords = _myController.viewToCurveSpace(_myViewCoords, true);
		
		if(_myMovY > 0){
			_myCurveMovement = _myController.viewToCurveSpace(new Point2D.Double(-_myMovX, _myMovY), false);
			_myCurveMovement.value(1 - _myCurveMovement.value());
		}else{
			_myCurveMovement = _myController.viewToCurveSpace(new Point2D.Double(-_myMovX, -_myMovY), false);
			_myCurveMovement.value( _myCurveMovement.value() - 1);
		}
	} 
	
	public void mouseMoved(MouseEvent theEvent){
		_myViewCoords = new Point2D.Double(theEvent.getX(), theEvent.getY());
		_myCurveCoords = _myController.viewToCurveSpace(_myViewCoords, true);
	}
	
	public void mouseDragged(MouseEvent theEvent){
		updateMotion(theEvent);
	}
	
	public void mouseReleased(MouseEvent theEvent){
		
	}
}
