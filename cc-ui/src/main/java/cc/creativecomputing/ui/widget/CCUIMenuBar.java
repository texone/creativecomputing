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

import cc.creativecomputing.math.CCColor;

public class CCUIMenuBar extends CCUIWidget{
	
	public CCColor background = new CCColor(50);
	public CCColor selectBackground = new CCColor(100);
	
	public CCColor separatorColor = new CCColor(100);
	
	public double separatorWeight = 2;
	
	public double separatorHeight = 10;
	
	public CCUIMenuBar(CCUIWidgetStyle theMenueStyle){
		super(theMenueStyle);
		flexDirection(CCYogaFlexDirection.ROW);
	}
	
	public CCUIMenuBar(){
		this(CCUIMenu.createDefaultStyle());
	}
	
	public void add(String theTitle, CCUIMenu theMenue){
		CCUIDropDownWidget myDropDown = new CCUIDropDownWidget(style(), theTitle, theMenue);
		myDropDown.adjustLabelBySelection(false);
		myDropDown.showIcon(false);
		myDropDown.flex(0.);
		myDropDown.margin(CCYogaEdge.RIGHT, 10);
		addChild(myDropDown);
//		theMenue.translation().set(0, -height());
	}
}
