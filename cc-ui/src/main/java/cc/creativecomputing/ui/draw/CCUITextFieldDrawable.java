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
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCTextField;
import cc.creativecomputing.graphics.font.CCTextField.CCPlacedTextChar;
import cc.creativecomputing.graphics.font.CCTextFieldController;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.widget.CCUITextFieldWidget;
import cc.creativecomputing.ui.widget.CCUIWidget;
import cc.creativecomputing.yoga.CCYogaNode;
import cc.creativecomputing.yoga.CCYogaNode.CCYogaEdge;

/**
 * @author christianriekoff
 */
public class CCUITextFieldDrawable implements CCUIDrawable{

	@CCProperty(name = "color")
	private CCColor _myColor = new CCColor(1f);
	
	@CCProperty(name = "background_color")
	private CCColor _myBackgroundColor = new CCColor(0.5f);

	private boolean _myShowCursor = false;
	private boolean _myIsInSelection = false;
	
	private int _myCursorStartIndex = 0;
	private int _myCursorEndIndex = 0;
	
	private CCTextFieldController _myController;
	
	public CCUITextFieldDrawable(CCUIWidget theWidget, CCTextFieldController theController) {
		_myController = theController;
		
		theWidget.mousePressed.add(_myController::mousePress);
		theWidget.mouseReleased.add(event -> {});
		theWidget.mouseClicked.add(_myController::mouseClicked);
		theWidget.mouseDragged.add(_myController::mouseDrag);
		
		theWidget.focusLost.add(e -> showCursor(false));
		theWidget.focusGained.add(e -> showCursor(true));
		
		theWidget.keyPressed.add(_myController::keyPress);
		theWidget.keyChar.add(_myController::keyChar);
		theWidget.keyChar.add(theChar -> {});
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
	public void draw(CCGraphics g, CCYogaNode theWidget) {
	
		if(!(theWidget instanceof CCUITextFieldWidget))return;

		CCTextField myText = ((CCUITextFieldWidget)theWidget).textField();
		g.pushMatrix();
		double myTX = 0;
		switch(myText.align()) {
		case RIGHT:
			myTX = theWidget.width() - theWidget.layoutPadding(CCYogaEdge.RIGHT);
			break;
		case LEFT:
			myTX = theWidget.layoutPadding(CCYogaEdge.LEFT);
			break;
		}
		g.translate(myTX,theWidget.layoutPadding(CCYogaEdge.TOP) + myText.ascent());
		
		g.color(_myBackgroundColor);
		g.beginShape(CCDrawMode.QUADS);
		
		int myStart = _myController.startIndex() > _myController.endIndex() ? _myController.endIndex() : _myController.startIndex();
		int myEnd = _myController.startIndex() > _myController.endIndex() ? _myController.startIndex() : _myController.endIndex();
		myStart = CCMath.clamp(myStart, 0,  myText.charGrid().size());
		myEnd = CCMath.clamp(myEnd, 0,  myText.charGrid().size());
		for(int i = myStart; i < myEnd;i++){
			CCPlacedTextChar myChar = myText.charGrid().get(i);
			g.vertex(myChar.x, myChar.y - myText.ascent());
			g.vertex(myChar.x, myChar.y - myText.descent());
			g.vertex(myChar.x + myChar.width, myChar.y - myText.descent());
			g.vertex(myChar.x + myChar.width, myChar.y - myText.ascent());
		}
		g.endShape();
		
		if(_myShowCursorBlink) {
			g.strokeWeight(2);
			g.color(255);
			double myX = 0;
			double myY = 0;
			g.beginShape(CCDrawMode.LINES);
			if(myText.charGrid().size() == 0) {
				
			}else {
				if(_myController.startIndex() >= 0){
					if(_myController.startIndex() > 0 && _myController.startIndex() >= myText.charGrid().size()) {
						CCPlacedTextChar myChar = myText.charGrid().get(_myController.startIndex() - 1);
						myX = myChar.x + myChar.width;
						myY = myChar.y;
					}else {
						CCPlacedTextChar myChar = myText.charGrid().get(_myController.startIndex());
						myX = myChar.x;
						myY = myChar.y;
					}
				}
			}
			g.vertex(myX, myY - myText.ascent());
			g.vertex(myX, myY - myText.descent());

			g.endShape();
		}


		g.color(_myColor);
		myText.draw(g);
		g.popMatrix();
	}

}
