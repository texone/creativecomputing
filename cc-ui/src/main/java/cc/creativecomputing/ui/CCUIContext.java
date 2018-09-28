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

import java.util.Optional;

import cc.creativecomputing.core.CCEventManager.CCEvent;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.gl.app.CCGLWindow;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.widget.CCUIWidget;
import cc.creativecomputing.yoga.CCYogaNode;

/**
 * @author christianriekoff
 *
 */
public class CCUIContext extends CCUIWidget{
	public static CCTextureMapFont ICON_FONT = new CCTextureMapFont(CCCharSet.ENTYPO,CCNIOUtil.dataPath("entypo.ttf"), 20, 2, 2);
	
//	public static CCFont<?> FONT_20 = new CCTextureMapFont(CCCharSet.REDUCED, CCNIOUtil.dataPath("Lato/Lato-Regular.ttf"), 20, 2, 2);
//	public static CCFont<?> FONT_30 = new CCTextureMapFont(CCCharSet.REDUCED, CCNIOUtil.dataPath("Lato/Lato-Regular.ttf"), 30, 2, 2);
//	public static CCFont<?> FONT_40 = new CCTextureMapFont(CCCharSet.REDUCED, CCNIOUtil.dataPath("Lato/Lato-Regular.ttf"), 40, 2, 2);
	public static CCFont<?> FONT_20 = new CCTextureMapFont(CCCharSet.REDUCED, CCNIOUtil.dataPath("Roboto_Mono/RobotoMono-Medium.ttf"), 20, 2, 2);
	public static CCFont<?> FONT_30 = new CCTextureMapFont(CCCharSet.REDUCED, CCNIOUtil.dataPath("Roboto_Mono/RobotoMono-Regular.ttf"), 30, 2, 2);
	public static CCFont<?> FONT_40 = new CCTextureMapFont(CCCharSet.REDUCED, CCNIOUtil.dataPath("Roboto_Mono/RobotoMono-Regular.ttf"), 40, 2, 2);
	
	private CCGLWindow _myApp;
	private CCYogaNode _myLastPressedNode = null;
	private CCYogaNode _myLastHoverNode = null;
	
	private Optional<CCYogaNode> _myOverlay = Optional.empty();
	
	private CCYogaFlexDirection _myDirection;
	
	public CCUIContext(CCGLWindow theApp, CCYogaFlexDirection theDirection) {
		_myApp = theApp;
		_myDirection = theDirection;
		
		flexDirection(theDirection);
		
		CCLog.info(FONT_20,FONT_30,FONT_40);
		
		_myApp.mousePressEvents.add(this::mousePressed);
		_myApp.mouseReleaseEvents.add(this::mouseReleased);
		_myApp.mouseClickEvents.add(this::mouseClicked);
		
		_myApp.mouseEnterEvents.add(pos ->{});
		_myApp.mouseExitEvents.add(pos ->{});
		
		_myApp.mouseMoveEvents.add(this::mouseMoved);
		_myApp.mouseDragEvents.add(this::mouseDragged);
		
		_myApp.keyPressEvents.add((theKeyEvent) -> {
			if(_myLastPressedNode != null)_myLastPressedNode.keyPressed.event(theKeyEvent);
		});
		_myApp.keyReleaseEvents.add((theKeyEvent) ->{
			if(_myLastPressedNode != null)_myLastPressedNode.keyReleased.event(theKeyEvent);
		});
		_myApp.keyCharEvents.add(theChar ->{
			if(_myLastPressedNode != null)_myLastPressedNode.keyChar.event(theChar);
		});
		
		_myApp.windowSizeEvents.add(size -> {
			 calculateLayout(size.x, size.y, _myDirection);
		 });
		
		_myApp.scrollEvents.add(e ->{
			if(_myLastHoverNode == null)return;
			_myLastHoverNode.scrollEvents.event(e);
		});
		_myApp.updateEvents.add(this::update);
		calculateLayout();
	}

	public void calculateLayout() {
		calculateLayout(_myApp.windowSize().x, _myApp.windowSize().y, _myDirection);
	}
	
	private CCGLMouseEvent toLocalEvent(CCGLMouseEvent theEvent, CCVector2 theLocalPos) {
		return new CCGLMouseEvent(
			theEvent, 
			theLocalPos.x, 
			theLocalPos.y, 
			_myApp.position().x + (int)theEvent.x, 
			_myApp.position().y + (int)theEvent.y
		);
	}
	
	private boolean handleOverlay(CCEvent<CCYogaNode> theEvent) {
		if(!_myOverlay.isPresent())return false;
		if(!_myOverlay.get().isActive())return false;
		
		theEvent.event(_myOverlay.get());
		
		return true;
	}
	
	private void mousePressed(CCGLMouseEvent event) {
		if (handleOverlay(o -> {
			CCVector2 myLocalPos = o.worldInverseTransform().transform(mouseToScreen(event.x, event.y));
			CCLog.info(myLocalPos, o.width(), o.height());
			if (o.isInsideLocal(myLocalPos)) {
				o.mousePressed.event(toLocalEvent(event, myLocalPos));
			} else {
				o.focusLost.event();
			}
		})) {
			return;
		}

		// _myPane.localInverseTransform().transform(new CCVector2(theX -
		// _myApp.framebufferSize().x/2, _myApp.framebufferSize().y/2 - theY));
		CCVector2 myScreenPos = mouseToScreen(event);
		CCYogaNode myPressedNode = childAtPosition(myScreenPos);

		if (_myLastPressedNode != myPressedNode) {
			if (_myLastPressedNode != null) {
				_myLastPressedNode.focusLost.event();
			}
		}
		
		if (myPressedNode != null) {
			CCVector2 myLocalPos = myPressedNode.worldInverseTransform().transform(mouseToScreen(event.x, event.y));
			myPressedNode.mousePressed.event(toLocalEvent(event, myLocalPos));
			myPressedNode.overlay().ifPresent(o -> {});
			if (myPressedNode.overlay().isPresent() && myPressedNode.overlay().get().isActive()) {
				
				_myOverlay = myPressedNode.overlay();
				myPressedNode = _myOverlay.get();
			}
		}
		_myLastPressedNode = myPressedNode;
	}
	
	private void mouseReleased(CCGLMouseEvent event){
		CCLog.info("->mouseReleased");
		if(handleOverlay(o -> {
			CCVector2 myLocalPos = o.worldInverseTransform().transform(mouseToScreen(event.x, event.y));
			CCLog.info(o.isInsideLocal(myLocalPos),myLocalPos,o.width(), o.height());
			if(o.isInsideLocal(myLocalPos)){
				CCLog.info("released");
				o.mouseReleased.event(toLocalEvent(event, myLocalPos));
			}else{
				CCLog.info("released outside");
				o.mouseReleasedOutside.event(toLocalEvent(event, myLocalPos));
			}
		})) {
			CCLog.info("->return mouseReleased");
			return;
		}

		CCLog.info("->post mouseReleased");
		CCVector2 myScreenPos = mouseToScreen(event);
		CCYogaNode myReleasedNode = childAtPosition(myScreenPos);
		
		if(myReleasedNode != null && myReleasedNode == _myLastPressedNode) {
			CCVector2 myLocalPos = myReleasedNode.worldInverseTransform().transform(myScreenPos);
			myReleasedNode.focusGained.event();
			myReleasedNode.mouseReleased.event(toLocalEvent(event, myLocalPos));
		}
		if(_myLastPressedNode != null && myReleasedNode != _myLastPressedNode) {
			CCVector2 myLocalPos = _myLastPressedNode.worldInverseTransform().transform(myScreenPos);
			_myLastPressedNode.mouseReleasedOutside.event(toLocalEvent(event, myLocalPos));
			CCLog.info("released outside");
		}
	}
	
	private void mouseClicked(CCGLMouseEvent event){
		CCLog.info("-> mouseClicked");
		if(handleOverlay(o -> {
			CCVector2 myLocalPos = o.worldInverseTransform().transform(mouseToScreen(event.x, event.y));
			if(o.isInsideLocal(myLocalPos)){
				o.mouseClicked.event(toLocalEvent(event, myLocalPos));
			}
		})) {
			return;
		}
		CCLog.info("-> post mouseClicked", _myLastPressedNode);
		
		if(_myLastPressedNode == null) return;

		CCVector2 myLocalPos = _myLastPressedNode.worldInverseTransform().transform(mouseToScreen(event));
		_myLastPressedNode.mouseClicked.event(toLocalEvent(event, myLocalPos));
		
		if(_myLastPressedNode.overlay().isPresent()) {
			_myOverlay = _myLastPressedNode.overlay();
		}
	}
	
	@Override
	public void display(CCGraphics g) {
		super.display(g);
	}
	
	private void mouseMoved(CCVector2 pos){
		if(handleOverlay(o -> {
			CCVector2 myLocalPos = o.worldInverseTransform().transform(mouseToScreen(pos));
			if(o.isInsideLocal(myLocalPos)){
				o.mouseMoved.event(myLocalPos);
			}
		})) {
			return;
		}

		CCYogaNode myMoveWidget = childAtPosition(mouseToScreen(pos));
		if(myMoveWidget != _myLastHoverNode) {
			if(_myLastHoverNode != null)_myLastHoverNode.onOut.event();
			if(myMoveWidget != null)myMoveWidget.onOver.event();
			
		}
		_myLastHoverNode = myMoveWidget;
		if(myMoveWidget == null)return;
		CCVector2 myLocalPos = myMoveWidget.worldInverseTransform().transform(mouseToScreen(pos));
		myMoveWidget.mouseMoved.event(myLocalPos);
	}
	
	private void mouseDragged(CCVector2 pos){
		if(handleOverlay(o -> {
			CCVector2 myLocalPos = o.worldInverseTransform().transform(mouseToScreen(pos));
			o.mouseDragged.event(myLocalPos);
		})) {
			return;
		}
		
		if(_myLastPressedNode == null)return;
		CCVector2 myLocalPos = _myLastPressedNode.worldInverseTransform().transform( mouseToScreen(pos));
		_myLastPressedNode.mouseDragged.event(myLocalPos);
	}
	
	/**
	 * Converts mouse coordinates to screen coordinates
	 * @param theEvent
	 * @return
	 */
	private CCVector2 mouseToScreen(double theX, double theY) {
//		return new CCVector2(theX - _myApp.framebufferSize().x/2, _myApp.framebufferSize().y/2 - theY);
		return new CCVector2(theX, theY);
	}
	private CCVector2 mouseToScreen(CCVector2 thePosition) {
		return mouseToScreen(thePosition.x, thePosition.y);
	}
	private CCVector2 mouseToScreen(CCGLMouseEvent thePosition) {
		return mouseToScreen(thePosition.x, thePosition.y);
	}
	
}
