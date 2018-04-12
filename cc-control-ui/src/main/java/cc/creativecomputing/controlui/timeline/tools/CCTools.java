package cc.creativecomputing.controlui.timeline.tools;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.timeline.CCTrackData;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLCursorShape;
import cc.creativecomputing.gl.app.CCGLKeyEvent;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.gl.app.CCGLWindow;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCTools extends ArrayList<CCTimelineTool> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1485914514090600980L;
	
	private class CCMultiTool extends CCTimelineTool{
		
		private CCTimelineTool _myCurrentTool;

		public CCMultiTool(CCTimedContentView theController) {
			super(true, theController);
		}
		
		@Override
		public void mousePressed(CCGLMouseEvent theEvent) {
			super.mousePressed(theEvent);
			
			CCControlPoint myControlPoint = pickNearestPoint(_myPressViewCoords);

			if (myControlPoint == null || !isInRange(myControlPoint,_myPressViewCoords)) {
				CCControlPoint myCurvePoint = _myDataView.viewToCurveSpace(new CCVector2(theEvent.x, theEvent.y),true);
				double myCurveValue = _myTrackData.value(myCurvePoint.time());
				if(_myDataView.valueToViewY(CCMath.abs(myCurveValue - myCurvePoint.value())) < 10){
					_myCurrentTool = _myCurveTool;
				}else{
					_myCurrentTool = _mySelectTool;
				}
			}else{
				_myCurrentTool = _myMoveTool;
			}
			
			_myCurrentTool.mousePressed(theEvent);
			CCLog.info(_myCurrentTool);
		}
		
		@Override
		public void mouseReleased(CCGLMouseEvent theEvent) {
			super.mouseReleased(theEvent);
			
			if(theEvent.clickCount == 2){
				_myCreateTool.mousePressed(theEvent);
				_myCreateTool.mouseReleased(theEvent);
				return;
			}
			
			if(_myMovX == 0 && _myMovY == 0 && _myCurrentTool != _mySelectTool){
				_mySelectTool.mousePressed(theEvent);
				_mySelectTool.mouseReleased(theEvent);
			}
			
			if(_myCurrentTool == null)return;
			_myCurrentTool.mouseReleased(theEvent);
			CCLog.info(_myCurrentTool, _myMovX, _myMovY);
		}
		
		@Override
		public void mouseDragged(CCVector2 theEvent) {
			super.mouseDragged(theEvent);
			if(_myCurrentTool == null)return;
			_myCurrentTool.mouseDragged(theEvent);
		}
		
		@Override
		public void mouseMoved(CCVector2 theEvent) {
			super.mouseMoved(theEvent);
			if(_myCurrentTool == null)return;
			_myCurrentTool.mouseMoved(theEvent);
		}
		
		@Override
		public void drawViewSpace(CCGraphics g) {
			if(_myCurrentTool == null)return;
			_myCurrentTool.drawViewSpace(g);
		}
	}
	
	private CCMultiTool _myMultiTool;
	private CCMoveTool _myMoveTool;
	private CCSelectTool _mySelectTool;
	private CCCurveTool _myCurveTool;
	private CCCreateTool _myCreateTool;
	private CCTimelineTool _myCurrentTool;

	private CCGLWindow _myWindow;

	public CCTools(CCGLWindow theWindow, CCTimedContentView theView) {
		_myWindow = theWindow;

		add(_myMultiTool = new CCMultiTool(theView));
		add(_mySelectTool = new CCSelectTool(theView));
		add(_myCurveTool = new CCCurveTool(theView));
		add(_myCreateTool = new CCCreateTool(theView,_mySelectTool));
		add(_myMoveTool = new CCMoveTool(theView, _mySelectTool));

		for (CCTimelineTool myTool : this) {
			myTool.hoverEvents.add(e -> {
				_myWindow.cursor(CCGLCursorShape.HAND);
			});
			myTool.endHoverEvents.add(e -> {
				_myWindow.cursor(CCGLCursorShape.ARROW);
			});
		}
		setTool(_myMultiTool);
	}
	
	public List<CCControlPoint> selection(){
		return _mySelectTool.selection();
	}

	public boolean isCurve() {
		return _myCurrentTool == _myCurveTool;
	}

	private void setTool(CCTimelineTool theTool) {
		if (_myCurrentTool != null) {
			_myWindow.keyPressEvents.remove(_myCurrentTool.keyPressed);
			_myWindow.keyReleaseEvents.remove(_myCurrentTool.keyReleased);
			_myWindow.keyCharEvents.remove(_myCurrentTool.keyChar);
			// CCLog.info(_myCurrentTool::mousePressed);
			_myWindow.mouseReleaseEvents.remove(_myCurrentTool.mouseReleased);
			_myWindow.mousePressEvents.remove(_myCurrentTool.mousePressed);
			_myWindow.mouseMoveEvents.remove(_myCurrentTool.mouseMoved);
			_myWindow.mouseDragEvents.remove(_myCurrentTool.mouseDragged);
		}

		_myCurrentTool = theTool;

		_myWindow.keyPressEvents.add(_myCurrentTool.keyPressed);
		_myWindow.keyReleaseEvents.add(_myCurrentTool.keyReleased);
		_myWindow.keyCharEvents.add(_myCurrentTool.keyChar);

		_myWindow.mouseReleaseEvents.add(_myCurrentTool.mouseReleased);
		_myWindow.mousePressEvents.add(_myCurrentTool.mousePressed);
		_myWindow.mouseMoveEvents.add(_myCurrentTool.mouseMoved);
		_myWindow.mouseDragEvents.add(_myCurrentTool.mouseDragged);
	}

	public void keyReleased(CCGLKeyEvent e) {
		switch (e.key) {
		case KEY_0:
			CCLog.info("MULTI");
			setTool(_myMultiTool);
			break;
		case KEY_1:
			CCLog.info("CREATE");
			setTool(_myCreateTool);
			break;
		case KEY_2:
			CCLog.info("MOVE");
			setTool(_myMoveTool);
			break;
		case KEY_3:
			CCLog.info("SELECT");
			setTool(_mySelectTool);
			break;
		case KEY_4:
			CCLog.info("CURVE");
			setTool(_myCurveTool);
			break;
		default:
			break;
		}
	}

	public void trackData(CCTrackData theTrackData) {
		for (CCTimelineTool myTool : this) {
			myTool.trackData(theTrackData);
		}
	}

	public void drawViewSpace(CCGraphics g) {
		_myCurrentTool.drawViewSpace(g);
	}

	public void drawTimeSpace(CCGraphics g) {
		// TODO Auto-generated method stub

	}
}