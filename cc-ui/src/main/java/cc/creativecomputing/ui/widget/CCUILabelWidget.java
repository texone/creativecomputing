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

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.font.CCTextField;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.ui.CCUIContext;
import cc.creativecomputing.ui.CCUIHorizontalAlignment;
import cc.creativecomputing.ui.CCUIVerticalAlignment;

/**
 * @author christianriekoff
 *
 */
public class CCUILabelWidget extends CCUIWidget{
	
	public static CCUIWidgetStyle createDefaultStyle(){
		CCUIWidgetStyle myResult = new CCUIWidgetStyle();
		myResult.font(CCUIContext.FONT_20);
		myResult.horizontalAlignment(CCUIHorizontalAlignment.LEFT);
		myResult.verticalAlignment(CCUIVerticalAlignment.CENTER);
		myResult.inset(4);
		return myResult;
	}
	
	@CCProperty(name = "textfield")
	protected CCTextField _myTextField;

	public CCUILabelWidget(CCUIWidgetStyle theStyle, String theText) {
		super(theStyle);
		_myTextField = new CCTextField(theStyle.font(), theText);
		
		style(theStyle);
	}
	
	public CCUILabelWidget(String theText){
		this(createDefaultStyle(), theText);
	}
	
	public CCTextField textField() {
		return _myTextField;
	}
	
	public String text(){
		return _myTextField.text();
	}
	
	public void text(String theText){
		_myTextField.text(theText);
	}
	
	@Override
	public double width() {
		return CCMath.max(_myTextField.width(), _myWidth) + _myStyle.leftInset() + _myStyle.rightInset();
	}
	
	@Override
	public double height() {
		return CCMath.max(_myTextField.height(), _myHeight) + _myStyle.topInset() + _myStyle.bottomInset();
	}
	
	@Override
	public double minWidth() {
		return CCMath.max(_myTextField.width(), _myMinWidth) + _myStyle.leftInset() + _myStyle.rightInset();
	}
	
	@Override
	public double minHeight() {
		return CCMath.max(_myTextField.height(), _myMinHeight) + _myStyle.topInset() + _myStyle.bottomInset();
	}
	
	@Override
	public void update(CCGLTimer theTimer) {
		super.update(theTimer);
		
		if(_myStyle != null && _myStyle.font() != _myTextField.font()){
			_myTextField.font(_myStyle.font());
		}
		_myTextField.position(0, _myStyle.bottomInset());
	}
	
	@Override
	public String toString() {
		return _myTextField.text();
	}
}
