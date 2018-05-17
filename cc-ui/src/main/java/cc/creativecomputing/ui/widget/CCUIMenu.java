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
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.CCUIContext;
import cc.creativecomputing.ui.CCUIHorizontalAlignment;
import cc.creativecomputing.ui.CCUIVerticalAlignment;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.ui.layout.CCUIVerticalFlowPane;

public class CCUIMenu extends CCUIVerticalFlowPane{
	
	public static CCUIWidgetStyle createDefaultStyle(){
		CCUIWidgetStyle myResult = new CCUIWidgetStyle();
		myResult.font(CCUIContext.FONT_20);
		myResult.horizontalAlignment(CCUIHorizontalAlignment.LEFT);
		myResult.verticalAlignment(CCUIVerticalAlignment.CENTER);
		myResult.background(new CCUIFillDrawable(new CCColor(0.3d)));
		myResult.inset(4);
		myResult.itemSelectBackground(new CCColor(0.5d));
		return myResult;
	}
	
	private List<CCUIMenuItem> _myItems = new ArrayList<>();
	
	private boolean _myIsInClickMode = false;
	private boolean _myIsDragged = false;
	
	public final CCEventManager<CCUIMenuItem> clickItemEvents = new CCEventManager<>();

	public CCUIMenu(CCUIWidgetStyle theStyle) {
		super(theStyle);
		_myIsActive = false;
		space(5);
		
		mouseClicked.add(event ->{
			if(!_myIsInClickMode){
				isActive(true);
				_myIsInClickMode = true;
			}else{
				isActive(false);
				_myIsInClickMode = false;
			}
		});
		
		mouseDragged.add(pos ->{
			handleHover(pos);
			_myIsDragged = true;
		});
		mouseMoved.add(this::handleHover);

		
		mouseReleased.add(event -> {
			if(!isActive())return;
			if(!(_myIsDragged || _myIsInClickMode))return;

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
		});
		mouseReleasedOutside.add(event ->{
			isActive(false);
		});
	}
	
	public CCUIMenu(){
		this(createDefaultStyle());
	}
	
	public void reset(){
		_myIsInClickMode = false;
		_myIsDragged = false;
	}
	
	private void handleHover(CCVector2 pos){
		for(CCUIMenuItem myItem:items()){
			myItem.background().color().set(style().itemBackground());
		}
		CCUIWidget myWidget = childAtPosition(pos);
		CCUIMenuItem mySelectedItem = myWidget instanceof CCUIMenuItem ? (CCUIMenuItem)myWidget : null;
		if(mySelectedItem != null){
			mySelectedItem.background().color().set(style().itemSelectBackground());
		}
	}
	
	public void addSeparator(){
		addChild(new CCUIHorizontalSeperator(style().separatorStyle()));
	}
	
	public CCUIWidget childAtPosition(CCVector2 thePosition) {
		for(CCUIWidget myWidget:_myChildren) {
			CCVector2 myTransformedVector = myWidget.localInverseTransform().transform(thePosition);
			boolean myIsInside = myWidget.isInsideLocal(myTransformedVector);
			if(myIsInside) {
				return myWidget;
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
		return addItem(new CCUIMenuItem(style(), theLabel), null);
	}
	
	public CCUIMenuItem addItem(String theLabel, CCEvent<CCGLMouseEvent> theEvent) {
		return addItem(new CCUIMenuItem(style(), theLabel), theEvent);
	}
	
	public CCUIMenuItem addItem(CCEntypoIcon theIcon, String theLabel, CCEvent<CCGLMouseEvent> theEvent) {
		return addItem(new CCUIMenuItem(theIcon, style(), theLabel), theEvent);
	}
	
	public CCUIMenuItem addItem(CCUICheckBox theCheckBox, String theLabel, CCEvent<CCGLMouseEvent> theEvent) {
		return addItem(new CCUIMenuItem(theCheckBox, style(), theLabel), theEvent);
	}
	
	public void addItem(String theLabel, CCUIMenu myQuantizeMenue) {
		// TODO Auto-generated method stub
		
	}
	
	public List<CCUIMenuItem> items(){
		return _myItems;
	}

	public void removeAll() {
		_myItems.clear();
		_myChildren.clear();
		updateMatrices();
	}
}
