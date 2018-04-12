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
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCTextField;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.ui.draw.CCUITextDrawable;

/**
 * @author christianriekoff
 *
 */
public class CCUILabelWidget extends CCUIWidget{
	
	@CCProperty(name = "textfield")
	protected CCTextField _myTextField;
	@CCProperty(name = "text decorator")
	private CCUITextDrawable _myTextDecorator;

	public CCUILabelWidget(CCFont<?> theFont, String theText) {
		_myTextField = new CCTextField(theFont, theText);
		_myForeground = _myTextDecorator = new CCUITextDrawable();
	}
	
	public CCTextField text() {
		return _myTextField;
	}
	
	@Override
	public double width() {
		return CCMath.max(_myTextField.width(), _myWidth) + _myLeftInset + _myRightInset;
	}
	
	@Override
	public double height() {
		return CCMath.max(_myTextField.height(), _myHeight) + _myTopInset + _myBottomInset;
	}
	
	@Override
	public void update(CCGLTimer theTimer) {
		super.update(theTimer);
		_myTextField.position(0, _myBottomInset);
	}
	
	@Override
	public String toString() {
		return _myTextField.text();
	}
}
