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

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.gl.app.CCGLWindow;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.CCUIInputEventType;
import cc.creativecomputing.ui.layout.CCUIPane;
import cc.creativecomputing.ui.widget.CCUIPanelWidget;
import cc.creativecomputing.ui.widget.CCUIWidget;

/**
 * @author christianriekoff
 *
 */
public class CCUIContext{
	public static CCTextureMapFont ICON_FONT = new CCTextureMapFont(CCCharSet.ENTYPO,CCNIOUtil.dataPath("entypo.ttf"), 20, 2, 2);
	
	private static int DOUBLE_CLICK_MILLIS = 1000;
	private long _myLastClickMillis = 0;
	
	private CCGLWindow _myApp;
	
	public CCUIPane _myPane;
	
	private CCUIWidget _myLastPressedWidget = null;
	
	public CCUIContext(CCGLWindow theApp, CCUIPane thePane) {
		_myApp = theApp;
		_myPane = thePane;
		_myApp.mousePressEvents.add(event ->{
			CCVector2 myLocalMouseCoord = new CCVector2();
			CCUIWidget myPressedWidget = _myPane.childAtPosition(mouseToScreen(event),myLocalMouseCoord);
			CCLog.info(myPressedWidget);
			if(_myLastPressedWidget != myPressedWidget) {
				if(_myLastPressedWidget != null) {
					_myLastPressedWidget.focusLost.proxy().event();;
				}
				
			}
			if(myPressedWidget != null) {
				CCGLMouseEvent myLocalEvent = new CCGLMouseEvent(event, myLocalMouseCoord.x, myLocalMouseCoord.y);
				myPressedWidget.mousePressed.proxy().event(myLocalEvent);
			}
			_myLastPressedWidget = myPressedWidget;
		});
		_myApp.mouseReleaseEvents.add(event -> {
			CCVector2 myLocalMouseCoord = new CCVector2();
			CCUIWidget myReleasedWidget = _myPane.childAtPosition(mouseToScreen(event), myLocalMouseCoord);
			CCGLMouseEvent myLocalEvent = new CCGLMouseEvent(event, myLocalMouseCoord.x, myLocalMouseCoord.y);
			if(myReleasedWidget != null && myReleasedWidget == _myLastPressedWidget) {
				myReleasedWidget.focusGained.proxy().event();
				myReleasedWidget.mouseReleased.proxy().event(myLocalEvent);
			}
			if(_myLastPressedWidget != null && myReleasedWidget != _myLastPressedWidget) {
				myReleasedWidget.mouseReleasedOutside.proxy().event(myLocalEvent);
			}
		});
		
		_myApp.mouseEnterEvents.add(pos ->{});
		_myApp.mouseExitEvents.add(pos ->{});
		
		_myApp.mouseMoveEvents.add(pos ->{
			checkMouseEvent(mouseToScreen(pos), CCUIInputEventType.MOVE);
		});
		_myApp.mouseDragEvents.add(pos ->{
			checkMouseEvent(mouseToScreen(pos), CCUIInputEventType.DRAGG);
		});
		
		_myApp.keyPressEvents.add((theKeyEvent) -> {
			if(_myLastPressedWidget != null)_myLastPressedWidget.keyPressed.proxy().event(theKeyEvent);
		});
		_myApp.keyReleaseEvents.add((theKeyEvent) ->{
			if(_myLastPressedWidget != null)_myLastPressedWidget.keyReleased.proxy().event(theKeyEvent);
		});
		_myApp.keyCharEvents.add(theChar ->{
			if(_myLastPressedWidget != null)_myLastPressedWidget.keyChar.proxy().event(theChar);
		});
	}
	
	public CCUIPane widget() {
		return _myPane;
	}
	
	
	
	public void checkMouseEvent(CCVector2 theVector, CCUIInputEventType theEventType) {
//		CCLog.info(_myWidget.clickedWidget(theVector));
	}
	
	public void checkMouseEvent(CCGLMouseEvent theEvent) {
	}
	
	/**
	 * Converts mouse coordinates to screen coordinates
	 * @param theEvent
	 * @return
	 */
	private CCVector2 mouseToScreen(double theX, double theY) {
		return new CCVector2(theX - _myApp.framebufferSize().x/2, _myApp.framebufferSize().y/2 - theY);
	}
	private CCVector2 mouseToScreen(CCVector2 thePosition) {
		return mouseToScreen(thePosition.x, thePosition.y);
	}
	private CCVector2 mouseToScreen(CCGLMouseEvent thePosition) {
		return mouseToScreen(thePosition.x, thePosition.y);
	}

	public void mouseClicked(CCGLMouseEvent theMouseEvent) {
		if(System.currentTimeMillis() - _myLastClickMillis > DOUBLE_CLICK_MILLIS) {
			checkMouseEvent(mouseToScreen(theMouseEvent), CCUIInputEventType.CLICK);
			_myLastClickMillis = System.currentTimeMillis();
			return;
		}
		
		checkMouseEvent(mouseToScreen(theMouseEvent), CCUIInputEventType.DOUBLE_CLICK);
	}
	
}
