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

import cc.creativecomputing.core.events.CCEvent;
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.layout.CCUIVerticalFlowPane;

public class CCUIMenu extends CCUIVerticalFlowPane{
	
	protected CCFont<?> _myFont;
	
	private List<CCUIMenuItem> _myItems = new ArrayList<>();

	public CCUIMenu(CCFont<?> theFont) {
		_myFont = theFont;
		_myIsActive = false;
		inset(5);
		space(5);
	}
	
	public void addSeparator(){
		addChild(new CCUIHorizontalSeperator(10));
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
	
	private CCUIMenuItem addItem(CCUIMenuItem theItem, CCEvent theEvent) {
		addChild(theItem);
		_myItems.add(theItem);
		if(theEvent == null)return theItem;
		theItem.mouseReleased.add(event -> {
			theEvent.event();
		});
		return theItem;
	}
	
	public CCUIMenuItem addItem(String theLabel){
		return addItem(new CCUIMenuItem(_myFont, theLabel), null);
	}
	
	public CCUIMenuItem addItem(String theLabel, CCEvent theEvent) {
		return addItem(new CCUIMenuItem(_myFont, theLabel), theEvent);
	}
	
	public CCUIMenuItem addItem(CCEntypoIcon theIcon, String theLabel, CCEvent theEvent) {
		return addItem(new CCUIMenuItem(theIcon, _myFont, theLabel), theEvent);
	}
	
	public CCUIMenuItem addItem(CCUICheckBox theCheckBox, String theLabel, CCEvent theEvent) {
		return addItem(new CCUIMenuItem(theCheckBox, _myFont, theLabel), theEvent);
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
