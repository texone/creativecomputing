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

import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.CCUIContext;
import cc.creativecomputing.ui.CCUIVerticalAlignment;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;

public class CCUIIconWidget extends CCUILabelWidget{
	
	public static CCUIWidgetStyle createDefaultStyle(){
		CCUIWidgetStyle myResult = new CCUIWidgetStyle();
		myResult.verticalAlignment(CCUIVerticalAlignment.CENTER);
		myResult.font(CCUIContext.ICON_FONT);
		myResult.background(new CCUIFillDrawable(new CCColor(0.3d)));
		myResult.inset(2);
		return myResult;
	}

	private CCEntypoIcon _myIcon;
	
	private boolean _myIsActive = true;
	
	private static CCUIWidgetStyle checkFont(CCUIWidgetStyle theStyle){
		theStyle.font(CCUIContext.ICON_FONT);
		return theStyle;
	}
	
	public CCUIIconWidget(CCUIWidgetStyle theStyle, CCEntypoIcon theIcon) {
		super(checkFont(theStyle), theIcon.text);
		_myMinWidth = _myWidth = CCUIContext.ICON_FONT.size();
		_myMinHeight = _myHeight = CCUIContext.ICON_FONT.size();
		
		_myIcon = theIcon;
	}
	
	public CCUIIconWidget(CCEntypoIcon theIcon){
		this(createDefaultStyle(), theIcon);
	}
	
	public void size(double theSize) {
		_myTextField.fontSize(theSize);
		_myWidth = theSize;
		_myHeight = theSize;
	}
	
	public boolean active() {
		return _myIsActive;
	}
	
	public void active(boolean theIsActive) {
		_myIsActive = theIsActive;
	}

	public void icon(CCEntypoIcon theIcon) {
		_myTextField.text(theIcon.text);
		_myIcon = theIcon;
	}
	
	public CCEntypoIcon icon() {
		return _myIcon;
	}
	
}