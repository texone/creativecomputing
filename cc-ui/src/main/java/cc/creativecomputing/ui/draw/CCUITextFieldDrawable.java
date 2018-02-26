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

package cc.creativecomputing.ui.draw;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCTextField;
import cc.creativecomputing.graphics.font.CCTextField.CCPlacedTextChar;
import cc.creativecomputing.graphics.font.CCTextFieldController;
import cc.creativecomputing.graphics.font.text.CCLineBreakMode;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.widget.CCUITextFieldWidget;
import cc.creativecomputing.ui.widget.CCUIWidget;

/**
 * @author christianriekoff
 */
public class CCUITextFieldDrawable implements CCUIDrawable{
	
	
	
	@CCProperty(name = "color")
	private CCColor _myColor = new CCColor(1f);
	
	@CCProperty(name = "background_color")
	private CCColor _myBackgroundColor = new CCColor(0.1f);
	
	@CCProperty(name="linebreak")
	private CCLineBreakMode _myLineBreakMode = CCLineBreakMode.NONE;
	
	
	
	
	private boolean _myShowCursor = false;
	private boolean _myIsInSelection = false;
	
	private int _myCursorStartIndex = 0;
	private int _myCursorEndIndex = 0;
	
	private CCTextFieldController _myController;
	
	public CCUITextFieldDrawable(CCTextFieldController theController) {
		_myController = theController;
	}
	
	public boolean showCursor() {
		return _myShowCursor;
	}
	
	public void showCursor(boolean theShowCursor) {
		_myShowCursor = theShowCursor;
	}
	
	public boolean isInSelection() {
		return _myIsInSelection;
	}
	
	public void isInSelection(boolean theIsInSelection) {
		_myIsInSelection = theIsInSelection;
	}
//	
//	public CCVector2 cursorPosition() {
//		return _myText.position(_myCursorStartIndex);
//	}
	
	public int startCursorIndex() {
		return _myCursorStartIndex;
	}
	
	public void startCursorIndex(int theIndex) {
		_myCursorStartIndex = theIndex;
	}
	
	public int endCursorIndex() {
		return _myCursorEndIndex;
	}
	
	public void endCursorIndex(int theEndCursorIndex) {
		_myCursorEndIndex = theEndCursorIndex;
	}
	
	private boolean _myShowCursorBlink = false;

	public void update(CCGLTimer theTimer) {
		_myShowCursorBlink = theTimer.time() % 1 > 0.5 && _myShowCursor;
	}
	
	@Override
	public void draw(CCGraphics g, CCUIWidget theWidget) {
	
		if(!(theWidget instanceof CCUITextFieldWidget))return;

		CCTextField _myText = ((CCUITextFieldWidget)theWidget).textField();
		g.pushMatrix();
		g.translate(_myText.position());
		
		g.color(_myBackgroundColor);
		g.beginShape(CCDrawMode.QUADS);
		
		int myStart = _myController.startIndex() > _myController.endIndex() ? _myController.endIndex() : _myController.startIndex();
		int myEnd = _myController.startIndex() > _myController.endIndex() ? _myController.startIndex() : _myController.endIndex();
		
		for(int i = myStart; i < myEnd;i++){
			CCPlacedTextChar myChar = _myText.charGrid().get(i);
			g.vertex(myChar.x, myChar.y + _myText.ascent());
			g.vertex(myChar.x, myChar.y + _myText.descent());
			g.vertex(myChar.x + myChar.width, myChar.y + _myText.descent());
			g.vertex(myChar.x + myChar.width, myChar.y + _myText.ascent());
		}
		g.endShape();
		
		
		
		if(_myShowCursorBlink) {
			g.strokeWeight(2);
			g.color(255);
			double myX = 0;
			double myY = 0;
			g.beginShape(CCDrawMode.LINES);
			if(_myText.charGrid().size() == 0) {
				
			}else {
				if(_myController.startIndex() >= 0){
					if(_myController.startIndex() > 0 && _myController.startIndex() >= _myText.charGrid().size()) {
						CCPlacedTextChar myChar = _myText.charGrid().get(_myController.startIndex() - 1);
						myX = myChar.x + myChar.width;
						myY = myChar.y;
					}else {
						CCPlacedTextChar myChar = _myText.charGrid().get(_myController.startIndex());
						myX = myChar.x;
						myY = myChar.y;
					}
				}
			}
			g.vertex(myX, myY + _myText.ascent());
			g.vertex(myX, myY + _myText.descent());

			g.endShape();
		}

		
		g.popMatrix();
		g.color(_myColor);
		_myText.draw(g);
	}

}
