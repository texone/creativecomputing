package cc.creativecomputing.gl.app;

import cc.creativecomputing.core.util.CCBitMask;

import static org.lwjgl.glfw.GLFW.*;

public class CCGLMouseEvent {

	public final CCGLMouseButton button;
	
	public final CCGLAction action;
	
	private final CCBitMask _myMod;
	
	public final double time;
	
	public final double x;
	
	public final double y;
	
	public final int screenX;
	
	public final int screenY;
	
	public int clickCount = 0;
	
	public CCGLMouseEvent( int theButton, int theAction, int theMods, double theX, double theY, int theScreenX, int theScreenY){
		button = CCGLMouseButton.button(theButton);
		action = CCGLAction.action(theAction);
		_myMod = new CCBitMask(theMods);
		time = glfwGetTime();
		x = theX;
		y = theY;
		screenX = theScreenX;
		screenY = theScreenY;
	}
	
	public CCGLMouseEvent(CCGLMouseEvent theEvent, double theX, double theY, int theScreenX, int theScreenY) {
		button = theEvent.button;
		action = theEvent.action;
		_myMod = theEvent._myMod;
		time = theEvent.time;
		x = theX;
		y = theY;
		screenX = theScreenX;
		screenY = theScreenY;
	}
	
	public boolean isShiftDown(){
		return _myMod.isFlagSet(GLFW_MOD_SHIFT);
	}
	
	public boolean isAltDown(){
		return _myMod.isFlagSet(GLFW_MOD_ALT);
	}
	
	public boolean isControlDown(){
		return _myMod.isFlagSet(GLFW_MOD_CONTROL);
	}
	
	public boolean isSuperDown(){
		return _myMod.isFlagSet(GLFW_MOD_SUPER);
	}
}
