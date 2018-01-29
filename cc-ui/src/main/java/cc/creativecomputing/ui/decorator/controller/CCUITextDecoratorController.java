/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.ui.decorator.controller;

import cc.creativecomputing.gl.app.CCGLKeyEvent;
import cc.creativecomputing.io.CCClipboard;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.CCUIInputEventType;
import cc.creativecomputing.ui.decorator.CCUITextDecorator;
import cc.creativecomputing.ui.event.CCUIWidgetEvent;
import cc.creativecomputing.ui.event.CCUIWidgetInteractionEvent;
import cc.creativecomputing.ui.widget.CCUIWidget;

/**
 * @author christianriekoff
 *
 */
public class CCUITextDecoratorController extends CCUIDecoratorController<CCUITextDecorator>{
	
	private boolean _myIsShiftPressed = false;
	
	private boolean _myIsValueText = false;
	
	private int _myStartCursorIndex = 0;
	private int _myCursorEndIndex = 0;
	
	private double _myMoveX = 0;
	
	
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.decorator.controller.CCUIDecoratorController#reset()
	 */
	@Override
	public void reset() {
		if(_myDecorator != null) {
			_myDecorator.showCursor(false);
			_myDecorator.isInSelection(false);
		}
	}
	
	public void valueText(boolean theIsValueText) {
		_myIsValueText = theIsValueText;
	}

	private boolean _myWasDragging = false;
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.decorator.controller.CCUIDecoratorController#onEvent(cc.creativecomputing.newui.CCUIWidgetEventType, cc.creativecomputing.math.CCVector2, cc.creativecomputing.math.CCVector2)
	 */
	@Override
	public void onEvent(CCUIWidgetEvent theEvent, CCUIWidget theWidget) {
		if(theEvent instanceof CCUIWidgetInteractionEvent) {
			CCUIWidgetInteractionEvent myEvent = (CCUIWidgetInteractionEvent)theEvent;
			
			switch(theEvent.type()) {
			case DOUBLE_CLICK:
				_myDecorator.showCursor(true);
				moveCursorToPosition(myEvent.transformedPosition());
				break;
			case PRESS:
				if(!_myDecorator.showCursor()) return;
				if(!_myIsShiftPressed)endSelection();
				moveCursorToPosition(myEvent.transformedPosition());
				_myWasDragging = false;
				break;
			case DRAGG:
				_myWasDragging = true;
				if(!_myDecorator.showCursor()) return;
				startSelection();
				moveCursorToPosition(myEvent.transformedPosition());
				break;
			case RELEASE:
			case RELEASE_OUTSIDE:
				break;
			case PRESS_OUTSIDE:
				_myDecorator.showCursor(false);
				endSelection();
				break;
			default:
			}
		}
	}
	
	private void startSelection() {
		if(!_myDecorator.isInSelection()) {
			_myDecorator.endCursorIndex(_myStartCursorIndex);
			_myDecorator.isInSelection(true);
		}
	}
	
	private void endSelection() {
		_myDecorator.isInSelection(false);
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.decorator.controller.CCUIDecoratorController#keyEvent(cc.creativecomputing.events.CCGLKeyEvent, cc.creativecomputing.newui.CCUIInputEventType)
	 */
	@Override
	public void keyEvent(CCGLKeyEvent theKeyEvent, CCUIInputEventType theEventType) {
		if(!_myDecorator.showCursor()) return;
		
		if(theEventType == CCUIInputEventType.PRESS && (theKeyEvent.isControlDown())) {
			switch(theKeyEvent.key) {
			case KEY_C:
				copySelectionToClipboard();
				break;
			case KEY_X:
				copySelectionToClipboard();
				removeSelection();
				break;
			case KEY_V:
				copySelectionFromClipboard();
				break;
			default:
			}
			return;
		}
		
		switch(theKeyEvent.key) {
		case KEY_LEFT_SHIFT:
		case KEY_RIGHT_SHIFT:
			if(theEventType == CCUIInputEventType.PRESS) {
				_myIsShiftPressed = true;
				startSelection();
			}
			if(theEventType == CCUIInputEventType.RELEASE)_myIsShiftPressed = false;
			break;
		default:
		}
		
		if(theEventType != CCUIInputEventType.PRESS)return;
		if(!_myDecorator.showCursor())return;
			
		switch(theKeyEvent.key) {
		
		case KEY_UP:
			if(!_myIsShiftPressed)endSelection();
			moveCursorUp();
			break;
		case KEY_DOWN:
			if(!_myIsShiftPressed)endSelection();
			moveCursorDown();
			break;
			
		}
	}
	
	private void moveCursorUp() {
		_myStartCursorIndex = _myDecorator.text().textGrid().upperIndex(_myMoveX, _myStartCursorIndex);
		_myDecorator.startCursorIndex(_myStartCursorIndex);
	}
	
	private void moveCursorDown() {
		_myStartCursorIndex = _myDecorator.text().textGrid().lowerIndex(_myMoveX, _myStartCursorIndex);
		_myDecorator.startCursorIndex(_myStartCursorIndex);
	}
	
	
	
	private void copySelectionToClipboard() {
		int myStartIndex = _myDecorator.startCursorIndex();
		int myEndIndex = _myDecorator.endCursorIndex();
		
		if(myStartIndex > myEndIndex) {
			int myTmp = myEndIndex;
			myEndIndex = myStartIndex;
			myStartIndex = myTmp;
		}
		
		String myCopiedText = _myDecorator.text().text(myStartIndex - 1, myEndIndex - 1);
		CCClipboard.instance().setData(myCopiedText);
	}
	
	private void copySelectionFromClipboard() {
		append(CCClipboard.instance().getStringData());
	}
	
	
	
	private void append(char theChar) {
		if(_myDecorator.isInSelection()) {
			removeSelection();
		}
		_myDecorator.text().append(_myStartCursorIndex - 1, theChar+"");
		moveCursorForward();
	}
	
	private void append(String theText) {
		if(_myDecorator.isInSelection()) {
			removeSelection();
		}
		_myDecorator.text().append(_myStartCursorIndex - 1, theText);
		moveCursorForward(theText.length());
	}
}
