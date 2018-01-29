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
import cc.creativecomputing.ui.CCUIInputEventType;
import cc.creativecomputing.ui.decorator.CCUIDecorator;
import cc.creativecomputing.ui.event.CCUIWidgetEvent;
import cc.creativecomputing.ui.event.CCUIWidgetEventListener;
import cc.creativecomputing.ui.widget.CCUIWidget;

/**
 * @author christianriekoff
 *
 */
public abstract class CCUIDecoratorController<DecoratorType extends CCUIDecorator> implements CCUIWidgetEventListener{
	
	protected CCUIWidget _myWidget;
	protected DecoratorType _myDecorator;
	
	public void append(CCUIWidget theWidget, DecoratorType theDecorator) {
		if(_myWidget != null)_myWidget.removeListener(this);
		_myWidget = theWidget;
		_myWidget.addListener(this);
		_myDecorator = theDecorator;
		reset();
	}
	
	public void reset() {
		
	}

	public void onEvent(CCUIWidgetEvent theEvent, CCUIWidget theWidget) {
		
	}
	
	public void keyEvent(CCGLKeyEvent theKeyEvent, CCUIInputEventType theEventType) {
		
	}
}
