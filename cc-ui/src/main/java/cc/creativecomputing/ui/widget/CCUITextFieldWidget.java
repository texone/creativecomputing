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
package cc.creativecomputing.ui.widget;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCTextField;
import cc.creativecomputing.graphics.font.CCTextFieldController;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.ui.CCUI;
import cc.creativecomputing.ui.decorator.CCUITextFieldDecorator;

/**
 * @author christianriekoff
 *
 */
public class CCUITextFieldWidget extends CCUIWidget{
	
	
	
	@CCProperty(name = "textfield")
	private CCTextField _myTextField;
	
	private CCUITextFieldDecorator _myTextDecorator;
	
	private CCTextFieldController _myTextController;

	public CCUITextFieldWidget(CCFont<?> theFont, String theText) {
		_myTextField = new CCTextField(theFont, theText);
		_myTextController = new CCTextFieldController(_myTextField);
		_myForeground = _myTextDecorator = new CCUITextFieldDecorator(_myTextField, _myTextController);
		
		mousePressed.add(_myTextController::mousePress);
		mouseReleased.add(event -> {});
		
		focusLost.add(() ->{_myTextDecorator.showCursor(false);});
		focusGained.add(() ->{_myTextDecorator.showCursor(true);});
		
		keyPressed.add(_myTextController::keyPress);
		keyChar.add(_myTextController::keyChar);
		keyChar.add(theChar -> {CCLog.info(" key", theChar);});

		inset(2);
	}
	
	public CCTextField text() {
		return _myTextField;
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
	
	@Override
	public void update(CCGLTimer theTimer) {
		super.update(theTimer);
		
		
		_myTextDecorator.update(theTimer);
	}
	
	@Override
	public void draw(CCGraphics g) {
		g.pushMatrix();
		g.applyMatrix(_myMatrix);
		
		if(_myBackground != null)_myBackground.draw(g, this);
		if(_myBorder != null)_myBorder.draw(g, this);
		if(_myForeground != null)_myForeground.draw(g, this);
		
		g.popMatrix();
	}
}
