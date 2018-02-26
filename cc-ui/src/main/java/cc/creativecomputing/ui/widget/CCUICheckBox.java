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

import cc.creativecomputing.core.events.CCBooleanEvent;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.graphics.font.CCEntypoIcon;

public class CCUICheckBox extends CCUIIconWidget{
	
	private boolean _myIsSelected = true;
	
	public CCListenerManager<CCBooleanEvent> changeEvents = CCListenerManager.create(CCBooleanEvent.class); 

	public CCUICheckBox(boolean theIsSelected) {
		super(CCEntypoIcon.ICON_CHECK);
		isSelected(theIsSelected, false);
		mouseReleased.add(event -> {
			isSelected(!_myIsSelected, true);
		});
		
		inset(2);
	}
	
	public CCUICheckBox(){
		this(false);
	}

	public void isSelected(boolean theIsSelected, boolean theSendEvents){
		if(theIsSelected == _myIsSelected)return;
		_myIsSelected = theIsSelected;
		_myTextField.text(_myIsSelected ? CCEntypoIcon.ICON_CHECK.text : "");
		
		if(theSendEvents)changeEvents.proxy().event(theIsSelected);
	}
	
	public boolean isSelected(){
		return _myIsSelected;
	}
}
