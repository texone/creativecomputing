package cc.creativecomputing.gl.app;

import cc.creativecomputing.core.util.CCBitMask;

import static org.lwjgl.glfw.GLFW.*;

public class CCGLKeyEvent {

	public final CCGLKey key;
	
	public final CCGLAction action;
	
	private final CCBitMask _myMod;
	
	public final int scanCode;
	
	public CCGLKeyEvent(int theKey, int theScancode, int theAction, int theMods){
		key = CCGLKey.key(theKey);
		action = CCGLAction.action(theAction);
		scanCode = theScancode;
		_myMod = new CCBitMask(theMods);
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
