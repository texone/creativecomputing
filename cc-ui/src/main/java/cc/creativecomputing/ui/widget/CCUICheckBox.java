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

import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.CCUIContext;
import cc.creativecomputing.ui.CCUIHorizontalAlignment;
import cc.creativecomputing.ui.CCUIVerticalAlignment;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;

public class CCUICheckBox extends CCUIIconWidget{
	
	public static CCUIWidgetStyle createDefaultStyle(){
		CCUIWidgetStyle myResult = new CCUIWidgetStyle();
		myResult.font(CCUIContext.ICON_FONT);
		myResult.background(new CCUIFillDrawable(new CCColor(0.3d)));
		myResult.horizontalAlignment(CCUIHorizontalAlignment.LEFT);
		myResult.verticalAlignment(CCUIVerticalAlignment.CENTER);
		return myResult;
	}
	
	private boolean _myIsSelected = true;
	
	public CCEventManager<Boolean> changeEvents = new CCEventManager<>(); 
	
	private CCEntypoIcon _myActiveIcon;
	private CCEntypoIcon _myInactiveIcon;

	public CCUICheckBox(CCUIWidgetStyle theStyle, CCEntypoIcon theactiveIcon, CCEntypoIcon theInactiveIcon, boolean theIsSelected) {
		super(theStyle,theactiveIcon);
		_myActiveIcon = theactiveIcon;
		_myInactiveIcon = theInactiveIcon;
		isSelected(theIsSelected, false);
		mouseReleased.add(event -> {
			isSelected(!_myIsSelected, true);
		});
	}
	
	public CCUICheckBox(CCUIWidgetStyle theStyle, boolean theIsSelected) {
		this(theStyle, CCEntypoIcon.ICON_CHECK, CCEntypoIcon.OFF, theIsSelected);
	}
	
	public CCUICheckBox(boolean theIsSelected) {
		this(createDefaultStyle(), CCEntypoIcon.ICON_CHECK, CCEntypoIcon.OFF, theIsSelected);
	}
	
	public CCUICheckBox(CCUIWidgetStyle theStyle){
		this(theStyle,false);
	}
	
	public CCUICheckBox(){
		this(createDefaultStyle());
	}

	public void isSelected(boolean theIsSelected, boolean theSendEvents){
		if(theIsSelected == _myIsSelected)return;
		_myIsSelected = theIsSelected;
		_myTextField.text(_myIsSelected ? _myActiveIcon.text : _myInactiveIcon.text);
		
		if(theSendEvents)changeEvents.event(theIsSelected);
	}
	
	public boolean isSelected(){
		return _myIsSelected;
	}
}
