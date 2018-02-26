package cc.creativecomputing.graphics.font;

import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.events.CCStringEvent;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLKeyEvent;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.gl.app.CCGLWindow;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCTextField.CCPlacedTextChar;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCTextFieldController {

	private CCTextField _myTextField;
	
	private CCGraphics g;
	
	private CCGLWindow _myWindow;
	
	public CCListenerManager<CCStringEvent> changeEvents = CCListenerManager.create(CCStringEvent.class);
	
	public CCTextFieldController(CCTextField theTextField){
		_myTextField = theTextField;
	}
	
	public CCTextFieldController(CCTextField theTextField, CCGLWindow theWindow){
		_myTextField = theTextField;
		_myWindow = theWindow;
		_myWindow.mouseMoveEvents.add(pos -> {});
		
		_myWindow.mousePressEvents.add(this::mousePress);
		_myWindow.mouseDragEvents.add(this::mouseDrag);
		
		_myWindow.keyPressEvents.add(this::keyPress);
		_myWindow.keyRepeatEvents.add(this::keyPress);
		
		_myWindow.keyCharEvents.add(this::keyChar);
		
		_myWindow.drawEvents.add((gr)->{
			if(g == null)g = gr;
		});
	}
	
	public void mousePress(CCGLMouseEvent theEvent) {
		int cursorIndex = cursorIndex(mouseCoords(theEvent.x, theEvent.y));
		moveCursorTo(cursorIndex, theEvent.isShiftDown());
	}
	
	public void mouseDrag(CCVector2 thePosition) {
		int cursorIndex = cursorIndex(mouseCoords(thePosition.x, thePosition.y));
		moveCursorTo(cursorIndex, true);
	}
	
	public void keyPress(CCGLKeyEvent theEvent){
		switch(theEvent.key){
		case KEY_LEFT:
			moveCursorLeft(theEvent.isShiftDown());
			break;
		case KEY_RIGHT:
			moveCursorRight(theEvent.isShiftDown());
			break;
			
		case KEY_DELETE:
		case KEY_BACKSPACE:
			delete();
			break;
			
		case KEY_C:
			if(theEvent.isControlDown() || theEvent.isSuperDown())copySelectionToClipboard();
			break;
		case KEY_X:
			if(theEvent.isControlDown() || theEvent.isSuperDown()) {
				copySelectionToClipboard();
				delete();
			}
			break;
		case KEY_V:
			if(theEvent.isControlDown() || theEvent.isSuperDown())pasteClipboard();
			break;
		case KEY_ENTER:
			changeEvents.proxy().event(_myTextField.text());
			break;
		}
	}
	
	public void keyChar(char theChar) {
		if(_myIsValueText) {
			switch(theChar) {
			case '-':
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case '.':
				append(theChar + "");
				break;
			case '\n':
//				_myDecorator.showCursor(false);
//				endSelection();
				break;
			}
		}else {
//			if(_myDecorator.text().font().canDisplay(theChar)) {
				append(theChar + "");
//			}
		}
	}
	
	private void append(String theString) {
		if(_myStartIndex != _myEndIndex) {
			delete();
		}
		StringBuffer myText = new StringBuffer(_myTextField.text());
		myText.insert(_myStartIndex, theString);
		_myTextField.text(myText.toString());
		moveCursorTo(_myStartIndex + theString.length(),false);
	}
	
	
	private void copySelectionToClipboard() {
		int myStartIndex = _myStartIndex;
		int myEndIndex = _myEndIndex;
		
		if(myStartIndex > myEndIndex) {
			int myTmp = myEndIndex;
			myEndIndex = myStartIndex;
			myStartIndex = myTmp;
		}
		String myText = _myTextField.text().substring(myStartIndex, myEndIndex);
		
		if(_myWindow != null)_myWindow.clipboardString(myText);
		
	}
	
	private void pasteClipboard() {
		if(_myWindow != null)append(_myWindow.clipboardString());
	}
	
	private void delete() {
		StringBuffer myText = new StringBuffer(_myTextField.text());
		if(_myStartIndex == _myEndIndex){
			if(_myStartIndex == 0)return;
			moveCursorLeft(false);
			myText.deleteCharAt(_myStartIndex);
		}else{
			int myStartIndex = _myStartIndex;
			int myEndIndex = _myEndIndex;
			
			if(myStartIndex > myEndIndex) {
				int myTmp = myEndIndex;
				myEndIndex = myStartIndex;
				myStartIndex = myTmp;
			}
			
			myText.delete(myStartIndex, myEndIndex);
			moveCursorTo(myStartIndex, false);
		}
		_myTextField.text(myText.toString());
	}
	
	private void moveCursorTo(int theIndex, boolean thePressShift){
		_myStartIndex = CCMath.clamp(theIndex, 0, _myTextField.charGrid().size());
		if(!thePressShift){
			_myEndIndex = _myStartIndex;
		}
	}
	
	private void moveCursorLeft(boolean thePressShift){
		moveCursorTo(_myStartIndex-1, thePressShift);
	}
	
	private void moveCursorRight(boolean thePressShift){
		moveCursorTo(_myStartIndex+1, thePressShift);
	}
	
	private CCVector2 mouseCoords(double theX, double theY){
		if(g == null) {
			return new CCVector2(
				theX - _myTextField.position().x,
				theY - _myTextField.position().y
			);
		}
		return new CCVector2(
			theX - g.width()/2 - _myTextField.position().x,
			g.height()/2 - theY - _myTextField.position().y
		);
	}

	private int _myCursorIndex = -1;
	
	private int _myStartIndex = -1;
	private int _myEndIndex = -1;
	
	private int cursorIndex(CCVector2 thePosition){
		for(int i = 0; i < _myTextField.charGrid().size();i++){
			CCPlacedTextChar myChar = _myTextField.charGrid().get(i);
			CCPlacedTextChar myNextChar = i < _myTextField.charGrid().size() - 1 ? _myTextField.charGrid().get(i + 1) : null;
			if(
				thePosition.y > myChar.y + _myTextField.descent() && 
				thePosition.y < myChar.y + _myTextField.ascent()
			){
				if(
					thePosition.x > myChar.x && 
					thePosition.x < myChar.x + myChar.width
				){
					if(myChar.x + myChar.width - thePosition.x < thePosition.x - myChar.x){
						return i+1;
					}
					return i;
				}else if(thePosition.x < 0 && myChar.x == 0){
					return i;
				}else if((myNextChar == null || myChar.x > myNextChar.x) && thePosition.x > myChar.x){
					if(thePosition.x < myChar.x - myChar.width / 2) {
						return i;
					}
					return i + 1;
				}
			}
		}
		return -1;
	}
	
	public void draw(CCGraphics g){
		g.pushAttribute();
		g.color(1d);
		g.beginShape(CCDrawMode.LINE_LOOP);
		g.vertex(_myTextField.boundMin().x, _myTextField.boundMin().y);
		g.vertex(_myTextField.boundMax().x, _myTextField.boundMin().y);
		g.vertex(_myTextField.boundMax().x, _myTextField.boundMax().y);
		g.vertex(_myTextField.boundMin().x, _myTextField.boundMax().y);
		g.endShape();
		
		
		g.pushMatrix();
		g.translate(_myTextField.position());
//		g.ellipse(myMouseX, myMouseY, 20);
		
		g.color(100);
		g.beginShape(CCDrawMode.QUADS);
		
		int myStart = _myStartIndex > _myEndIndex ? _myEndIndex : _myStartIndex;
		int myEnd = _myStartIndex > _myEndIndex ? _myStartIndex : _myEndIndex;
		
		for(int i = myStart; i < myEnd;i++){
			CCPlacedTextChar myChar = _myTextField.charGrid().get(i);
			g.vertex(myChar.x, myChar.y + _myTextField.ascent());
			g.vertex(myChar.x, myChar.y + _myTextField.descent());
			g.vertex(myChar.x + myChar.width, myChar.y + _myTextField.descent());
			g.vertex(myChar.x + myChar.width, myChar.y + _myTextField.ascent());
		}
		g.endShape();
		
		g.beginShape(CCDrawMode.LINES);
		g.color(255);
		for(CCPlacedTextChar myChar:_myTextField.charGrid()){
			g.vertex(myChar.x, myChar.y + _myTextField.ascent());
			g.vertex(myChar.x, myChar.y + _myTextField.descent());
		}
		if(_myStartIndex > 0){
			g.color(0,255,0);
			CCPlacedTextChar myChar = _myTextField.charGrid().get(_myStartIndex);
			g.vertex(myChar.x, myChar.y + _myTextField.ascent());
			g.vertex(myChar.x, myChar.y + _myTextField.descent());
		}
		if(_myEndIndex > 0){
			g.color(255,0,0);
			CCPlacedTextChar myChar = _myTextField.charGrid().get(_myEndIndex);
			g.vertex(myChar.x, myChar.y + _myTextField.ascent());
			g.vertex(myChar.x, myChar.y + _myTextField.descent());
		}
		g.endShape();
		g.popMatrix();

		g.popAttribute();
	}
	
	private void startSelection() {
	}
	
	private void endSelection() {
	}
	
	private boolean _myIsShiftPressed = false;
	private boolean _myIsValueText = false;
	
	public void keyEvent(CCGLKeyEvent theKeyEvent) {
//		if(_myCursorIndex < 0) return;
//		
//		if(theKeyEvent.action == CCGLAction.PRESS && (theKeyEvent.isControlDown())) {
//			switch(theKeyEvent.key) {
//			case KEY_C:
//				copySelectionToClipboard();
//				break;
//			case KEY_X:
//				copySelectionToClipboard();
//				removeSelection();
//				break;
//			case KEY_V:
//				copySelectionFromClipboard();
//				break;
//			default:
//			}
//			return;
//		}
//		
//		switch(theKeyEvent.key) {
//		case KEY_LEFT_SHIFT:
//		case KEY_RIGHT_SHIFT:
//			if(theKeyEvent.action == CCGLAction.PRESS) {
//				_myIsShiftPressed = true;
//				startSelection();
//			}
//			if(theKeyEvent.action == CCGLAction.RELEASE)_myIsShiftPressed = false;
//			break;
//		default:
//		}
//		
//		if(theKeyEvent.action != CCGLAction.PRESS)return;
//			
//		switch(theKeyEvent.key) {

//		case KEY_UP:
//			if(!_myIsShiftPressed)endSelection();
//			moveCursorUp();
//			break;
//		case KEY_DOWN:
//			if(!_myIsShiftPressed)endSelection();
//			moveCursorDown();
//			break;
//		case KEY_BACKSPACE:
//		case KEY_DELETE:
//			delete();
//			break;
//			
//		}
	}

	public int startIndex() {
		return _myStartIndex;
	}

	public int endIndex() {
		return _myEndIndex;
	}
	
	
}
