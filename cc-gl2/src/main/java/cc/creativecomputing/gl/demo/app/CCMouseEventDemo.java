package cc.creativecomputing.gl.demo.app;

import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.gl.app.CCGLWindow;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2;

public class CCMouseEventDemo extends CCGLApp{

	
	private boolean _myIsMousePressed = false;
	private CCVector2 _myCursorPosition = new CCVector2();
	private CCVector2 _myMovePosition = new CCVector2();
	private CCVector2 _myEnterPosition = new CCVector2();
	private CCVector2 _myExitPosition = new CCVector2();
	
	@Override
	public void setup(CCGraphics g, CCGLTimer t) {
		window().mousePressEvents.add(event -> {_myIsMousePressed = true;});
		window().mouseReleaseEvents.add(event -> {_myIsMousePressed = false;});
		
		window().cursorPositionEvents.add(pos -> {_myCursorPosition = pos;});

		window().mouseMoveEvents.add(pos -> {_myMovePosition = pos;});
		window().mouseDragEvents.add(pos -> {_myMovePosition = pos;});

		window().mouseEnterEvents.add(pos -> {_myEnterPosition = pos;});
		window().mouseExitEvents.add(pos -> {_myExitPosition = pos;});
	}
	
	@Override
	public void update(CCGLTimer theTimer) {
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.ortho();
		g.color(1d);
		g.rect(_myCursorPosition.x - 20, _myCursorPosition.y - 20,40,40);
		if(_myIsMousePressed){
			g.color(1d,0d,0d);
			g.rect(_myMovePosition.x - 20, _myMovePosition.y - 20,20,40);
		}else{
			g.color(0d,0d,1d);
			g.rect(_myMovePosition.x - 20, _myMovePosition.y - 20,20,40);
		}

		g.color(1d,0d,0d);
		g.ellipse(_myEnterPosition, 40);
		g.color(0d,0d,1d);
		g.ellipse(_myExitPosition, 40);
		
	}
	
	public static void main(String[] args) {
		CCMouseEventDemo myDemo = new CCMouseEventDemo();
		myDemo.run();
	}
}
