package cc.creativecomputing.gl.app;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author christianr
 *
 */
public enum CCGLMouseButton {
	BUTTON_1(GLFW_MOUSE_BUTTON_1),
	BUTTON_2(GLFW_MOUSE_BUTTON_2),
	BUTTON_3(GLFW_MOUSE_BUTTON_3),
	BUTTON_4(GLFW_MOUSE_BUTTON_4),
	BUTTON_5(GLFW_MOUSE_BUTTON_5),
	BUTTON_6(GLFW_MOUSE_BUTTON_6),
	BUTTON_7(GLFW_MOUSE_BUTTON_7),
	BUTTON_8(GLFW_MOUSE_BUTTON_8),
	BUTTON_LAST(GLFW_MOUSE_BUTTON_LAST),
	BUTTON_LEFT(GLFW_MOUSE_BUTTON_LEFT),
	BUTTON_MIDDLE(GLFW_MOUSE_BUTTON_MIDDLE),
	BUTTON_RIGHT(GLFW_MOUSE_BUTTON_RIGHT);
	

	private final int _myID;

	private CCGLMouseButton(int theID) {
		_myID = theID;
	}

	public int id() {
		return _myID;
	}
	
	private static CCGLMouseButton[] buttons;
	
	public static CCGLMouseButton button(int theCode){
		if(buttons == null){
			buttons = new CCGLMouseButton[20];
			for(CCGLMouseButton myButton:values()){
				buttons[myButton._myID] = myButton;
			}
		}
		if(theCode < 0)return null;
		if(theCode >= buttons.length) return null;
		CCGLMouseButton myResult = buttons[theCode];
		if(myResult == null)return null;
		return myResult;
	}
}
