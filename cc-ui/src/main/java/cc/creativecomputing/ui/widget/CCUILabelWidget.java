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
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.CCUIContext;
import cc.creativecomputing.yoga.CCYogaNode;

/**
 * @author christianriekoff
 *
 */
public class CCUILabelWidget extends CCUIWidget{
	
	public static CCUIWidgetStyle createDefaultStyle(){
		CCUIWidgetStyle myResult = new CCUIWidgetStyle();
		myResult.font(CCUIContext.FONT_20);
		return myResult;
	}
	
	@CCProperty(name = "textfield")
	protected CCTextField _myTextField;
	
	protected CCYogaNode _myTextFieldNode;

	public CCUILabelWidget(CCUIWidgetStyle theStyle, String theText) {
		super(theStyle);
		flexDirection(CCYogaFlexDirection.ROW);
		alignItems(CCYogaAlign.CENTER);
		_myTextField = new CCTextField(theStyle.font(), theText);
		_myTextFieldNode = new CCYogaNode();
		_myTextFieldNode.minWidth(_myTextField.width());
		_myTextFieldNode.minHeight(_myTextField.height());
		_myTextFieldNode.debugInfo("text field node", CCColor.WHITE);
		addChild(_myTextFieldNode);

		style(theStyle);
	}
	
	@Override
	public boolean isEndNode() {
		return true;
	}
	
	public CCYogaNode textFieldNode() {
		return _myTextFieldNode;
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
		_myTextFieldNode.minWidth(_myTextField.width());
		_myTextFieldNode.minHeight(_myTextField.height());
	}
	
	@Override
	public void update(CCGLTimer theTimer) {
		super.update(theTimer);
//		
//		if(_myStyle != null && _myStyle.font() != _myTextField.font()){
//			_myTextField.font(_myStyle.font());
//		}
//		_myTextField.position(0, 0);
	}
	
	@Override
	public String toString() {
		return _myTextField.text();
	}
}
