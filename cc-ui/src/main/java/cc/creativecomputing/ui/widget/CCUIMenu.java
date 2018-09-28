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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.core.CCEventManager.CCEvent;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.CCUIContext;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.yoga.CCYogaNode;

public class CCUIMenu extends CCUIWidget{
	
	public static CCUIWidgetStyle createDefaultStyle(){
		CCUIWidgetStyle myResult = new CCUIWidgetStyle();
		myResult.font(CCUIContext.FONT_20);
		myResult.background(new CCUIFillDrawable(new CCColor(0.3d)));
		myResult.itemSelectBackground(new CCColor(0.5d));
		return myResult;
	}
	
	private List<CCUIMenuItem> _myItems = new ArrayList<>();
	
	private boolean _myIsInClickMode = false;
	private boolean _myIsDragged = false;
	
	public final CCEventManager<CCUIMenuItem> clickItemEvents = new CCEventManager<>();
	
	private CCUIWidgetStyle _myItemStyle = new CCUIWidgetStyle();
	
	private CCUIMenu _mySubMenu = null;

	public CCUIMenu(CCUIWidgetStyle theStyle) {
		super(theStyle);
		_myItemStyle.font(theStyle.font());
		flexDirection(CCYogaFlexDirection.COLUMN);
		_myIsActive = false;
		
//		mouseClicked.add(event ->{
//			if(!_myIsInClickMode){
//				isActive(true);
//				_myIsInClickMode = true;
//			}else{
//				isActive(false);
//				parent().ifPresent(p -> p.removeChild(this));
//				_myIsInClickMode = false;
//			}
//		});
		
		mouseDragged.add(pos ->{
			handleHover(pos);
			_myIsDragged = true;
		});
		mouseMoved.add(pos -> {
			if(_mySubMenu != null) {
				_mySubMenu.handleHover(pos.subtract(_mySubMenu.left(), _mySubMenu.top()));
			}
			handleHover(pos);
			
			CCLog.info(pos,_mySubMenu);
		});

		mousePressed.add(event -> {
			_myIsDragged = true;
		});
		
		mouseReleased.add(event -> {
			CCLog.info("releaseddd");
//			if(!isActive())return;
			if(_myIsInClickMode)return;
			if(!(_myIsDragged))return;

			if(_mySubMenu != null) {
				for(CCUIMenuItem myItem:_mySubMenu.items()){
					if(myItem.checkBox() != null)myItem.checkBox().isSelected(false, true);
				}
				
				CCUIWidget myWidget = _mySubMenu.childAtPosition(new CCVector2(event.x, event.y).subtract(_mySubMenu.left(), _mySubMenu.top()));
				
				CCUIMenuItem mySelectedItem = myWidget instanceof CCUIMenuItem ? (CCUIMenuItem)myWidget : null;
				if(mySelectedItem != null){
					CCLog.info(mySelectedItem.text());
					mySelectedItem.mouseReleased.event(event);
					_mySubMenu.clickItemEvents.event(mySelectedItem);
					if(mySelectedItem.checkBox() != null)mySelectedItem.checkBox().isSelected(true, true);
				}
			}
			
			for(CCUIMenuItem myItem:items()){
				if(myItem.checkBox() != null)myItem.checkBox().isSelected(false, true);
			}
			
			CCUIWidget myWidget = childAtPosition(new CCVector2(event.x, event.y));
			CCUIMenuItem mySelectedItem = myWidget instanceof CCUIMenuItem ? (CCUIMenuItem)myWidget : null;
			if(mySelectedItem != null){
				mySelectedItem.mouseReleased.event(event);
				clickItemEvents.event(mySelectedItem);
				if(mySelectedItem.checkBox() != null)mySelectedItem.checkBox().isSelected(true, true);
			}
			isActive(false);
			parent().ifPresent(p -> p.removeChild(this));
		});
		mouseReleasedOutside.add(event ->{
			CCLog.info("released out");
			isActive(false);
			handleRelease(new CCVector2(event.x, event.y));
			_myLastSelectedItem = null;
			parent().ifPresent(p -> p.removeChild(this));
		});
	}
	
	@Override
	public boolean isInsideLocal(CCVector2 theVector) {
		if(_mySubMenu != null) {
			return super.isInsideLocal(theVector) || _mySubMenu.isInsideLocal(theVector.subtract(_mySubMenu.left(), _mySubMenu.top()));
		}
		return super.isInsideLocal(theVector);
	}
	
	public void subMenue(CCUIMenu theSubMenu) {
		_mySubMenu = theSubMenu;
	}
	
	private void handleRelease(CCVector2 pos) {
		if(_myLastSelectedItem != null) {
			CCVector2 worldPos =worldTransform().transform(pos);
			CCYogaNode myChild =  _myLastSelectedItem.childAtPosition(worldPos);
			
			if(myChild != null && myChild instanceof CCUIMenu) {
				((CCUIMenu)myChild).handleRelease(myChild.worldInverseTransform().transform(worldPos));
				CCYogaNode myWidget = myChild.childAtPosition(worldPos);
				if(myWidget == null) {
				}
//				CCUIMenuItem mySelectedItem = myWidget instanceof CCUIMenuItem ? (CCUIMenuItem)myWidget : null;
//				CCLog.info(mySelectedItem);
//				if(mySelectedItem != null){
//					mySelectedItem.
//					clickItemEvents.event(mySelectedItem);
//					if(mySelectedItem.checkBox() != null)mySelectedItem.checkBox().isSelected(true, true);
//				}
			}else {
				_myLastSelectedItem.mouseReleased.event(null);
			}
		}
	}
	
	private void handleHover(CCVector2 pos){
		for(CCUIMenuItem myItem:items()){
			myItem.background().color().set(style().itemBackground());
		}
		CCUIWidget myWidget = childAtPosition(pos);
		CCUIMenuItem mySelectedItem = myWidget instanceof CCUIMenuItem ? (CCUIMenuItem)myWidget : null;
		if(mySelectedItem != null){
			mySelectedItem.background().color().set(style().itemSelectBackground());
			
			
			if(mySelectedItem != _myLastSelectedItem) {
				if(_myLastSelectedItem != null)
					_myLastSelectedItem.onOut.event();
			
				if(mySelectedItem != null){
					mySelectedItem.onOver.event();
					
				}
				_myLastSelectedItem = mySelectedItem;
			}
		}
		if(_myLastSelectedItem != null) {
			_myLastSelectedItem.background().color().set(style().itemSelectBackground());
			CCVector2 worldPos =worldTransform().transform(pos);
			CCYogaNode myChild =  _myLastSelectedItem.childAtPosition(worldPos);
			if(myChild != null && myChild instanceof CCUIMenu) {
				((CCUIMenu)myChild).handleHover(myChild.worldInverseTransform().transform(worldPos));
			}
		}
		
	}
	
//	@Override
//	public boolean isEndNode() {
//		return true;
//	}
	
	public CCUIMenu(){
		this(createDefaultStyle());
	}
	
	public void reset(){
		_myIsInClickMode = false;
		_myIsDragged = false;
	}
	
	private CCUIMenuItem _myLastSelectedItem = null;
	
	@Override
	public void isActive(boolean theIsActive) {
		super.isActive(theIsActive);
	}
	
	public CCUIHorizontalSeperator addSeparator(){
		CCUIHorizontalSeperator myResult = new CCUIHorizontalSeperator();
		addChild(myResult);
		return myResult;
	}
	
	public CCUIWidget childAtPosition(CCVector2 thePosition) {
		for(CCYogaNode myWidget:this) {
			CCVector2 myTransformedVector = myWidget.localInverseTransform().transform(thePosition);
			boolean myIsInside = myWidget.isInsideLocal(myTransformedVector);
			if(myIsInside) {
				return (CCUIWidget)myWidget;
			}
		}
		return null;
	}
	
	private CCUIMenuItem addItem(CCUIMenuItem theItem, CCEvent<CCGLMouseEvent> theEvent) {
		addChild(theItem);
		_myItems.add(theItem);
		if(theEvent == null)return theItem;
		theItem.mouseReleased.add(event -> {
			theEvent.event(event);
		});
		return theItem;
	}
	
	public CCUIMenuItem addItem(String theLabel){
		return addItem(new CCUIMenuItem(_myItemStyle, theLabel), null);
	}
	
	public CCUIMenuItem addItem(String theLabel, CCEvent<CCGLMouseEvent> theEvent) {
		return addItem(new CCUIMenuItem(_myItemStyle, theLabel), theEvent);
	}
	
	public CCUIMenuItem addItem(CCEntypoIcon theIcon, String theLabel, CCEvent<CCGLMouseEvent> theEvent) {
		return addItem(new CCUIMenuItem(theIcon, _myItemStyle, theLabel), theEvent);
	}
	
	public CCUIMenuItem addItem(CCUICheckBox theCheckBox, String theLabel, CCEvent<CCGLMouseEvent> theEvent) {
		return addItem(new CCUIMenuItem(theCheckBox, _myItemStyle, theLabel), theEvent);
	}
	
	public CCUIMenuItem addItem(String theLabel, CCUIMenu theMenue) {
		return addItem(new CCUIMenuItem(theMenue, _myItemStyle, theLabel), null);
		
	}
	
	public List<CCUIMenuItem> items(){
		return _myItems;
	}

	public void removeAll() {
		_myItems.clear();
		removeAllChildren();
		updateMatrices();
	}
}
