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
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCTextField;
import cc.creativecomputing.graphics.font.CCTextFieldController;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.CCUIContext;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.ui.draw.CCUITextFieldDrawable;

/**
 * @author christianriekoff
 *
 */
public class CCUITextFieldWidget extends CCUILabelWidget{
	
	public static CCUIWidgetStyle createDefaultStyle(){
		CCUIWidgetStyle myResult = new CCUIWidgetStyle();
		myResult.font(CCUIContext.FONT_20);
		myResult.background(new CCUIFillDrawable(new CCColor(0.2d)));
		return myResult;
	}
	
	private CCUITextFieldDrawable _myTextDecorator;
	
	protected CCTextFieldController _myTextController;
	
	public CCEventManager<String> changeEvents = new CCEventManager<>();

	public CCUITextFieldWidget(CCUIWidgetStyle theStyle, String theText) {
		super(theStyle,theText);
		_myTextController = new CCTextFieldController(_myTextField);
		
		_myTextController.changeEvents.add(text -> {
			_myTextDecorator.showCursor(false);
			changeEvents.event(text);
		});
		
//		if(theStyle.foreground() != null && theStyle.foreground() instanceof CCUITextFieldDrawable){
			_myTextDecorator = new CCUITextFieldDrawable(this, _myTextController);
//		}
		//_myForeground = 
		
//		mousePressed.add(_myTextController::mousePress);
//		mouseReleased.add(event -> {});
//		
//		focusLost.add(e -> _myTextDecorator.showCursor(false));
//		focusGained.add(e -> _myTextDecorator.showCursor(true));
//		
//		keyPressed.add(_myTextController::keyPress);
//		keyChar.add(_myTextController::keyChar);
//		keyChar.add(theChar -> {});
		
		minWidth(_myTextField.width());
		minHeight(_myTextField.height());
		
		style(theStyle);
	}
	
	public CCUITextFieldWidget(String theText){
		this(createDefaultStyle(), theText);
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
	
	public void valueText(boolean theIsValueBox) {
//		_myTextController.valueText(theIsValueBox);
	}
	
	public CCVector2 textPosition() {
		double myAdd = 0;
		switch(_myTextField.align()) {
		case RIGHT:
			myAdd = width();
			break;
		case CENTER:
			myAdd = width() / 2;
			break;
		default:
			break;
		}
		return new CCVector2(myAdd, _myTextField.ascent());
	}
	
	@Override
	public void update(CCGLTimer theTimer) {
		super.update(theTimer);
//		
//		if(_myTextField.font() != _myStyle.font()){
//			_myTextField.font(_myStyle.font());
//			root().updateMatrices();
//		}
//		
		_myTextDecorator.update(theTimer);
//		_myTextField.position().set(textPosition());
	}

	@Override
	public void displayContent(CCGraphics g) {
		super.displayContent(g);
		_myTextDecorator.draw(g, this);
	}
}
