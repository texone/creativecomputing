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

package cc.creativecomputing.ui;

import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.gl.app.CCGLWindow;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.layout.CCUIPane;
import cc.creativecomputing.ui.widget.CCUIWidget;

/**
 * @author christianriekoff
 *
 */
public class CCUIContext{
	public static CCTextureMapFont ICON_FONT = new CCTextureMapFont(CCCharSet.ENTYPO,CCNIOUtil.dataPath("entypo.ttf"), 20, 2, 2);
	
	private CCGLWindow _myApp;
	
	public CCUIPane _myPane;
	
	private CCUIWidget _myLastPressedWidget = null;
	
	private CCUIWidget _myOverlay = null;
	
	public CCUIContext(CCGLWindow theApp, CCUIPane thePane) {
		_myApp = theApp;
		_myPane = thePane;
		
		_myApp.mousePressEvents.add(this::mousePressed);
		_myApp.mouseReleaseEvents.add(this::mouseReleased);
		_myApp.mouseClickEvents.add(this::mouseClicked);
		
		_myApp.mouseEnterEvents.add(pos ->{});
		_myApp.mouseExitEvents.add(pos ->{});
		
		_myApp.mouseMoveEvents.add(this::mouseMoved);
		_myApp.mouseDragEvents.add(this::mouseDragged);
		
		_myApp.keyPressEvents.add((theKeyEvent) -> {
			if(_myLastPressedWidget != null)_myLastPressedWidget.keyPressed.event(theKeyEvent);
		});
		_myApp.keyReleaseEvents.add((theKeyEvent) ->{
			if(_myLastPressedWidget != null)_myLastPressedWidget.keyReleased.event(theKeyEvent);
		});
		_myApp.keyCharEvents.add(theChar ->{
			if(_myLastPressedWidget != null)_myLastPressedWidget.keyChar.event(theChar);
		});
	}
	
	private void mousePressed(CCGLMouseEvent event){
		if(_myOverlay != null && _myOverlay.isActive()){
			CCVector2 myLocalPos = _myOverlay.worldInverseTransform().transform(mouseToScreen(event.x, event.y));
			if(_myOverlay.isInsideLocal(myLocalPos)){
				CCGLMouseEvent myLocalEvent = new CCGLMouseEvent(event, myLocalPos.x, myLocalPos.y);
				_myOverlay.mousePressed.event(myLocalEvent);
			}else{
				_myOverlay.focusLost.event();
			}
			return;
		}
		//_myPane.localInverseTransform().transform(new CCVector2(theX - _myApp.framebufferSize().x/2, _myApp.framebufferSize().y/2 - theY));
		CCVector2 myScreenPos = mouseToScreen(event);
		CCUIWidget myPressedWidget = _myPane.childAtPosition(myScreenPos);
		if(_myLastPressedWidget != myPressedWidget) {
			if(_myLastPressedWidget != null) {
				_myLastPressedWidget.focusLost.event();
			}
			
		}
		if(myPressedWidget != null) {
			if(myPressedWidget.overlayWidget() != null)myPressedWidget = myPressedWidget.overlayWidget();
			CCVector2 myLocalPos = myPressedWidget.worldInverseTransform().transform(mouseToScreen(event.x, event.y));
			CCGLMouseEvent myLocalEvent = new CCGLMouseEvent(event, myLocalPos.x, myLocalPos.y);
			myPressedWidget.mousePressed.event(myLocalEvent);

			if(myPressedWidget.overlayWidget() != null){
				_myOverlay = myPressedWidget = myPressedWidget.overlayWidget();
			}
		}
		_myLastPressedWidget = myPressedWidget;
	}
	
	private void mouseReleased(CCGLMouseEvent event){
		if(_myOverlay != null && _myOverlay.isActive()){
			CCVector2 myLocalPos = _myOverlay.worldInverseTransform().transform(mouseToScreen(event.x, event.y));
			CCGLMouseEvent myLocalEvent = new CCGLMouseEvent(event, myLocalPos.x, myLocalPos.y);
			if(_myOverlay.isInsideLocal(myLocalEvent.x, myLocalEvent.y)){
				_myOverlay.mouseReleased.event(myLocalEvent);
			}else{
				_myOverlay.mouseReleasedOutside.event(myLocalEvent);
			}
			return;
		}

		CCVector2 myScreenPos = mouseToScreen(event);
		CCUIWidget myReleasedWidget = _myPane.childAtPosition(myScreenPos);
		if(myReleasedWidget != null && myReleasedWidget == _myLastPressedWidget) {
			CCVector2 myLocalPos = myReleasedWidget.worldInverseTransform().transform(myScreenPos);
			CCGLMouseEvent myLocalEvent = new CCGLMouseEvent(event, myLocalPos.x, myLocalPos.y);
			myReleasedWidget.focusGained.event();
			myReleasedWidget.mouseReleased.event(myLocalEvent);
		}
		if(_myLastPressedWidget != null && myReleasedWidget != _myLastPressedWidget) {
			CCVector2 myLocalPos = _myLastPressedWidget.worldInverseTransform().transform(myScreenPos);
			CCGLMouseEvent myLocalEvent = new CCGLMouseEvent(event, myLocalPos.x, myLocalPos.y);
			_myLastPressedWidget.mouseReleasedOutside.event(myLocalEvent);
		}
	}
	
	private void mouseClicked(CCGLMouseEvent event){
		if(_myOverlay != null && _myOverlay.isActive()){
			CCVector2 myLocalPos = _myOverlay.worldInverseTransform().transform(mouseToScreen(event.x, event.y));
			CCGLMouseEvent myLocalEvent = new CCGLMouseEvent(event, myLocalPos.x, myLocalPos.y);
			if(_myOverlay.isInsideLocal(myLocalEvent.x, myLocalEvent.y)){
				_myOverlay.mouseClicked.event(myLocalEvent);
			}
			return;
		}
		
		if(_myLastPressedWidget == null) return;

		CCVector2 myLocalPos = _myLastPressedWidget.worldInverseTransform().transform(mouseToScreen(event));
		CCGLMouseEvent myLocalEvent = new CCGLMouseEvent(event, myLocalPos.x, myLocalPos.y);
		_myLastPressedWidget.mouseClicked.event(myLocalEvent);
		
		if(_myLastPressedWidget.overlayWidget() != null){
			_myOverlay = _myLastPressedWidget.overlayWidget();
		}
	}
	
	private void mouseMoved(CCVector2 pos){
		if(_myOverlay != null && _myOverlay.isActive()){
			CCVector2 myLocalPos = _myOverlay.worldInverseTransform().transform(mouseToScreen(pos));
			if(_myOverlay.isInsideLocal(myLocalPos)){
				_myOverlay.mouseMoved.event(myLocalPos);
			}
			return;
		}
		
		CCUIWidget myMoveWidget = _myPane.childAtPosition(mouseToScreen(pos));
		if(myMoveWidget == null)return;
		CCVector2 myLocalPos = myMoveWidget.worldInverseTransform().transform(mouseToScreen(pos));
		myMoveWidget.mouseMoved.event(myLocalPos);
	}
	
	private void mouseDragged(CCVector2 pos){
		if(_myOverlay != null && _myOverlay.isActive()){
			CCVector2 myLocalPos = _myOverlay.worldInverseTransform().transform(mouseToScreen(pos));
			if(_myOverlay.isInsideLocal(myLocalPos)){
				_myOverlay.mouseDragged.event(myLocalPos);
			}
			return;
		}
		if(_myLastPressedWidget == null)return;
		CCVector2 myLocalPos = _myLastPressedWidget.worldInverseTransform().transform( mouseToScreen(pos));
		_myLastPressedWidget.mouseDragged.event(myLocalPos);
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
	
}
