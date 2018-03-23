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

package cc.creativecomputing.ui.widget;

import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCTextField;
import cc.creativecomputing.graphics.font.CCTextFieldController;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.draw.CCUITextFieldDrawable;

/**
 * @author christianriekoff
 *
 */
public class CCUITextFieldWidget extends CCUIWidget{
	
	@CCProperty(name = "textfield")
	protected CCTextField _myTextField;
	
	private CCUITextFieldDrawable _myTextDecorator;
	
	protected CCTextFieldController _myTextController;
	
	public CCEventManager<String> changeEvents = new CCEventManager<>();

	public CCUITextFieldWidget(CCFont<?> theFont, String theText) {
		_myTextField = new CCTextField(theFont, theText);
		_myTextController = new CCTextFieldController(_myTextField);
		
		_myTextController.changeEvents.add(text -> {
			_myTextDecorator.showCursor(false);
			changeEvents.event(text);
		});
		_myForeground = _myTextDecorator = new CCUITextFieldDrawable(_myTextController);
		
		mousePressed.add(_myTextController::mousePress);
		mouseReleased.add(event -> {});
		
		focusLost.add(e ->{_myTextDecorator.showCursor(false);});
		focusGained.add(e ->{_myTextDecorator.showCursor(true);});
		
		keyPressed.add(_myTextController::keyPress);
		keyChar.add(_myTextController::keyChar);
		keyChar.add(theChar -> {});

		inset(2);
	}
	
	public CCTextField textField() {
		return _myTextField;
	}
	
	public String text() {
		return _myTextField.text();
	}
	
	public void text(String theText, boolean theSendEvents) {
		_myTextField.text(theText);
		if(theSendEvents)changeEvents.event(theText);
	}
	
	@Override
	public double width() {
		return CCMath.max(_myWidth,_myTextField.width()) + _myInset * 2;
	}
	
	@Override
	public double height() {
		return _myTextField.height() + _myInset * 2;
	}
	
	public void valueText(boolean theIsValueBox) {
//		_myTextController.valueText(theIsValueBox);
	}
	
	public CCVector2 textPosition() {
		double myAdd = 0;
		switch(_myTextField.align()) {
		case RIGHT:
			myAdd = width() - inset() * 2;
			break;
		case CENTER:
			myAdd = (width() - inset() * 2) / 2;
			break;
		}
		return new CCVector2(inset() + myAdd, - _myTextField.ascent() - inset());
	}
	
	@Override
	public void update(CCGLTimer theTimer) {
		super.update(theTimer);
		
		_myTextDecorator.update(theTimer);
		
		
		_myTextField.position().set(textPosition());
	}

}
