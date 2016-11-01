package cc.creativecomputing.gl.app.events;

import cc.creativecomputing.gl.app.events.CCKeyEvent.CCKeyCode;

public class CCKeySimpleInfo implements CCKeyListener{
	
	public boolean isPressed;
	
	public CCKeyCode keyCode;

	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		isPressed = true;
		keyCode = theKeyEvent.keyCode();
	}

	@Override
	public void keyReleased(CCKeyEvent theKeyEvent) {
		isPressed = false;
	}

	@Override
	public void keyTyped(CCKeyEvent theKeyEvent) {
		// TODO Auto-generated method stub
		
	}

}
