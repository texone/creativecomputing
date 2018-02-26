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
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.ui.layout.CCUIHorizontalFlowPane;

public class CCUIMenuItem extends CCUIHorizontalFlowPane{

	private String _myToolTip = "";
	
	private CCUIMenuItem(){
		inset(5);
		space(5);
	}
	
	public CCUIMenuItem(CCFont<?> theFont, String theText){
		this();
		addChild(new CCUIIconWidget(CCEntypoIcon.OFF));
		addChild(new CCUILabelWidget(theFont, theText));
	}
	
	public CCUIMenuItem(CCEntypoIcon theIcon, CCFont<?> theFont, String theText){
		this();
		addChild(new CCUIIconWidget(theIcon));
		addChild(new CCUILabelWidget(theFont, theText));
	}
	
	public CCUIMenuItem(CCUICheckBox theCheckBox, CCFont<?> theFont, String theText){
		this();
		addChild(theCheckBox);
		addChild(new CCUILabelWidget(theFont, theText));
	}
	
	public CCUICheckBox checkBox(){
		CCUIWidget myWidget = _myChildren.get(0);
		if(myWidget instanceof CCUICheckBox)return (CCUICheckBox)myWidget;
		return null;
	}
	
	public String text(){
		return ((CCUILabelWidget)_myChildren.get(1)).text().text();
	}

	public void toolTipText(String theToolTip) {
		_myToolTip = theToolTip;
	}
	
	public String toolTip(){
		return _myToolTip;
	}
}
