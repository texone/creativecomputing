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
import cc.creativecomputing.gl.app.CCGLKeyEvent;
import cc.creativecomputing.gl.font.CCFont;
import cc.creativecomputing.gl.font.CCTextArea;
import cc.creativecomputing.gl.font.CCTextField;
import cc.creativecomputing.gl.font.CCTextField.CCTextListener;
import cc.creativecomputing.gl.font.CCTextFieldController;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.text.CCLineBreakMode;
import cc.creativecomputing.io.xml.property.CCXMLProperty;
import cc.creativecomputing.io.xml.property.CCXMLPropertyObject;
import cc.creativecomputing.ui.CCUI;
import cc.creativecomputing.ui.CCUIInputEventType;
import cc.creativecomputing.ui.decorator.CCUITextDecorator;
import cc.creativecomputing.ui.decorator.background.CCUIBackgroundDecorator;
import cc.creativecomputing.ui.decorator.controller.CCUITextDecoratorController;

/**
 * @author christianriekoff
 *
 */
public class CCUILabelWidget extends CCUIWidget{
	
	@CCProperty(name = "textfield")
	private CCTextField _myTextField;
	
	private CCUITextDecorator _myTextDecorator;

	public CCUILabelWidget(CCFont<?> theFont, String theText) {
		_myTextDecorator = new CCUITextDecorator();
		_myTextField = new CCTextField(theFont, theText);
	}
	
	public CCTextField text() {
		return _myTextDecorator.text();
	}
	
	public void valueText(boolean theIsValueBox) {
		_myTextController.valueText(theIsValueBox);
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.widget.CCUIWidget#setup(cc.creativecomputing.newui.CCUI)
	 */
	@Override
	public void setup(CCUI theUI, CCUIWidget theParent) {
		super.setup(theUI, theParent);
		
		_myTextDecorator.setup(theUI, this);
		onChangeText(_myTextDecorator.text());
		
		_myTextDecorator.text().events().add(this);
		_myTextController.append(this, _myTextDecorator);
		
		_myForeground  = _myTextDecorator;
	}
	
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.widget.CCUIWidget#keyEvent(cc.creativecomputing.events.CCKeyEvent, cc.creativecomputing.newui.CCUIInputEventType)
	 */
	@Override
	public void keyEvent(CCGLKeyEvent theKeyEvent, CCUIInputEventType theEventType) {
		super.keyEvent(theKeyEvent, theEventType);
		if(_myTextController != null)_myTextController.keyEvent(theKeyEvent, theEventType);
	}
	@Override
	public void keyCharEvent(char theChar) {
		super.keyCharEvent(theChar);
		if(_myTextController != null)_myTextController.keyCharEvent(theChar);
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.widget.CCUIWidget#draw(cc.creativecomputing.graphics.CCGraphics)
	 */
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
