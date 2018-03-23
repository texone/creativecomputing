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
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCTextAlign;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import net.objecthunter.exp4j.ExpressionBuilder;

public class CCUIValueBox extends CCUITextFieldWidget{

	private CCUIIconWidget _myIcon;
	
	private double _myValue;
	private double _myMin;
	private double _myMax;
	
	private int _myDigits;
	
	private int _myChangeSize = 3;
	private double _myLastY;
	private double _myStartValue;
	private int _myCaretPosition;
	private double _myFactor;

	public CCEventManager<Double> changeEvents = new CCEventManager<>();
	
	private boolean _myIsShiftDown = false;
	private boolean _myIsAltDown = false;
	private boolean _myIsSuperDown = false;
	private boolean _myIsCtrlDown = false;
	
	public CCUIValueBox(CCFont<?> theFont, double theValue, double theMin, double theMax, int theDigits) {
		super(theFont, theValue + "");
		
		_myMin = theMin;
		_myMax = theMax;
		_myDigits = theDigits;
		value(theValue, true);
		
		_myTextField.align(CCTextAlign.RIGHT);
		
		super.changeEvents.add(text ->{
			double myValue = _myValue;
			try {
				value(new ExpressionBuilder(_myTextField.text()).build().evaluate(), true);
			} catch (Exception e) {
				value(myValue, true);
			}
		});
		
		keyPressed.add(event ->{
			switch(event.key) {
			case KEY_LEFT_SHIFT:
			case KEY_RIGHT_SHIFT:
				_myIsShiftDown = true;
				break;
			case KEY_LEFT_ALT:
			case KEY_RIGHT_ALT:
				_myIsAltDown = true;
				break;
			case KEY_LEFT_SUPER:
			case KEY_RIGHT_SUPER:
				_myIsSuperDown = true;
				break;
			case KEY_LEFT_CONTROL:
			case KEY_RIGHT_CONTROL:
				_myIsCtrlDown = true;
				break;
			}
		});
		
		keyReleased.add(event ->{
			switch(event.key) {
			case KEY_LEFT_SHIFT:
			case KEY_RIGHT_SHIFT:
				_myIsShiftDown = false;
				break;
			case KEY_LEFT_ALT:
			case KEY_RIGHT_ALT:
				_myIsAltDown = false;
				break;
			case KEY_LEFT_SUPER:
			case KEY_RIGHT_SUPER:
				_myIsSuperDown = false;
				break;
			case KEY_LEFT_CONTROL:
			case KEY_RIGHT_CONTROL:
				_myIsCtrlDown = false;
				break;
			}
		});
		
		
		mousePressed.add(e -> {
			_myLastY = e.y;
			_myStartValue = _myValue;
			
			_myFactor = 1;
		});

		mouseDragged.add(e -> {
			_myFactor = 1;
			if(_myIsShiftDown) {
				_myFactor = 0.1;
				if(_myIsCtrlDown) {
					_myFactor = 0.01;
				}
			}
			if(_myIsAltDown) {
				_myFactor = 10.;
				if(_myIsCtrlDown) {
					_myFactor = 100;
				}
			}
			double myChange = (int)(e.y - _myLastY);
			_myLastY += myChange;
//		
			double myVal = _myValue;
			value(_myValue + myChange * _myFactor, true);
		});

		_myIcon = new CCUIIconWidget(CCEntypoIcon.ICON_TRIANGLE_LEFT);
	}
	
	public CCUIValueBox(CCFont<?> theFont, double theValue) {
		this(theFont, theValue, -Float.MAX_VALUE, Float.MAX_VALUE, 2);
	}
	
	@Override
	public CCVector2 textPosition() {
		CCVector2 myResult = super.textPosition();
		myResult.x -=  _myIcon.width();
		return myResult;
	}
	
	private void updateCaret() {
		_myCaretPosition = _myTextController.startIndex();
		String myText = _myTextField.text();
		if (!myText.contains(".")) return;
		int myDotIndex = myText.indexOf(".");
		
		if (_myCaretPosition > myDotIndex + 1) {
			int myPow = _myCaretPosition - myDotIndex - 1;
			_myFactor = CCMath.pow(10d, -myPow);
		} else {
			int myPow = CCMath.max(0, myDotIndex - _myCaretPosition);
			_myFactor = CCMath.pow(10d, myPow);
		}
		
	}
	
	private void changeby(double theChange) {
		updateCaret();
		value(_myValue + theChange * _myFactor, true);
	}
	
	public void value(double theValue, boolean theSendEvents) {
		if (theValue == _myValue) {
			_myTextField.text(CCFormatUtil.nd(_myValue, _myDigits));
			return;
		}

		// _myValue = CCMath.quantize(theValue, _myStepSize);
		_myValue = CCMath.constrain(theValue, _myMin, _myMax);

		if(theSendEvents)changeEvents.event(_myValue);

		_myTextField.text(CCFormatUtil.nd(_myValue, _myDigits));
	}

	public double value() {
		return _myValue;
	}

	@Override
	public void drawContent(CCGraphics g) {
		super.drawContent(g);
		g.color(255);
		_myIcon.text().fontSize(_myTextField.fontSize());
		_myIcon.text().position().set(width() - _myIcon.width(), -_myIcon.height()   , 0);
		_myIcon.text().draw(g);
		
//		g.triangle(width() - height() / 4 - _myInset, -height() / 2, width() - _myInset, -height() * 0.25, width() - _myInset, -height() * 0.75);
		
	}
}
