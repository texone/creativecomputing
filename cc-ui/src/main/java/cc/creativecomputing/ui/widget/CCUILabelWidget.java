/*  
 * Copyright (c) 2017  Christian Riekoff <christian@riekoff.com>  
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
package cc.creativecomputing.ui.widget;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCTextField;
import cc.creativecomputing.ui.decorator.CCUITextDecorator;

/**
 * @author christianriekoff
 *
 */
public class CCUILabelWidget extends CCUIWidget{
	
	@CCProperty(name = "textfield")
	protected CCTextField _myTextField;
	@CCProperty(name = "text decorator")
	private CCUITextDecorator _myTextDecorator;

	public CCUILabelWidget(CCFont<?> theFont, String theText) {
		_myTextField = new CCTextField(theFont, theText);
		_myForeground = _myTextDecorator = new CCUITextDecorator(_myTextField);
	}
	
	public CCTextField text() {
		return _myTextField;
	}
	
	@Override
	public double width() {
		return _myTextField.width() + _myInset * 2;
	}
	
	@Override
	public double height() {
		return _myTextField.height() + _myInset * 2;
	}
	
	@Override
	public void draw(CCGraphics g) {
		g.pushMatrix();
		g.applyMatrix(_myMatrix);
		_myTextField.position(_myInset, _myInset);
		if(_myBackground != null)_myBackground.draw(g, this);
		if(_myBorder != null)_myBorder.draw(g, this);
		if(_myForeground != null)_myForeground.draw(g, this);
		
		g.popMatrix();
	}
	
	@Override
	public String toString() {
		return _myTextField.text();
	}
}
