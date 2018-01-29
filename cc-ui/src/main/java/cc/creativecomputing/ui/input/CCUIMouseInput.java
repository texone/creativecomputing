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
package cc.creativecomputing.ui.input;

import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.gl.app.CCGLWindow;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.CCUI;
import cc.creativecomputing.ui.CCUIInputEventType;
import cc.creativecomputing.ui.widget.CCUIWidget;

/**
 * @author christianriekoff
 *
 */
public class CCUIMouseInput extends CCUIInput{
	private static int DOUBLE_CLICK_MILLIS = 1000;
	private long _myLastClickMillis = 0;
	
	private CCGLWindow _myApp;
	
	public CCUIMouseInput(CCGLWindow theApp, CCUI theUI) {
		super(theUI);
		_myApp = theApp;
		_myApp.mousePressEvents.add(event ->{
			_myUI.checkEvent(mouseToScreen(event), CCUIInputEventType.PRESS);
		});
		_myApp.mouseReleaseEvents.add(event -> {
			_myUI.checkEvent(mouseToScreen(event), CCUIInputEventType.RELEASE);
		});
		
		_myApp.mouseEnterEvents.add(pos ->{});
		_myApp.mouseExitEvents.add(pos ->{});
		
		_myApp.mouseMoveEvents.add(pos ->{
			_myUI.checkEvent(mouseToScreen(pos), CCUIInputEventType.MOVE);
		});
		_myApp.mouseDragEvents.add(pos ->{
			_myUI.checkEvent(mouseToScreen(pos), CCUIInputEventType.DRAGG);
		});
		
		_myApp.keyPressEvents.add((theKeyEvent) -> {
			for(CCUIWidget myWidget:_myUI.widgets()) {
				myWidget.keyEvent(theKeyEvent, CCUIInputEventType.PRESS);
			}
		});
		_myApp.keyReleaseEvents.add((theKeyEvent) ->{
			for(CCUIWidget myWidget:_myUI.widgets()) {
				myWidget.keyEvent(theKeyEvent, CCUIInputEventType.RELEASE);
			}
		});
		_myApp.keyCharEvents.add(theChar ->{
			for(CCUIWidget myWidget:_myUI.widgets()) {
				myWidget.keyCharEvent(theChar);
			}
		});
	}
	
	/**
	 * Converts mouse coordinates to screen coordinates
	 * @param theEvent
	 * @return
	 */
	private CCVector2 mouseToScreen(double theX, double theY) {
		return new CCVector2(theX - _myApp.windowSize().x/2, _myApp.windowSize().y/2 - theY);
	}
	private CCVector2 mouseToScreen(CCVector2 thePosition) {
		return mouseToScreen(thePosition.x, thePosition.y);
	}
	private CCVector2 mouseToScreen(CCGLMouseEvent thePosition) {
		return mouseToScreen(thePosition.x, thePosition.y);
	}

	public void mouseClicked(CCGLMouseEvent theMouseEvent) {
		if(System.currentTimeMillis() - _myLastClickMillis > DOUBLE_CLICK_MILLIS) {
			_myUI.checkEvent(mouseToScreen(theMouseEvent), CCUIInputEventType.CLICK);
			_myLastClickMillis = System.currentTimeMillis();
			return;
		}
		
		_myUI.checkEvent(mouseToScreen(theMouseEvent), CCUIInputEventType.DOUBLE_CLICK);
	}
	
}
