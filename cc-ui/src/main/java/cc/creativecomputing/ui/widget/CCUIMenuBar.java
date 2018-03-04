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

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.ui.layout.CCUIHorizontalFlowPane;

public class CCUIMenuBar extends CCUIHorizontalFlowPane{

	public CCFont<?> _myFont;
	public CCUIMenuBar(CCFont<?> theFont){
		super();
		_myFont = theFont;
		space(30);
	}
	
	public void add(String theTitle, CCUIMenu theMenue){
		CCUIDropDownWidget myDropDown = new CCUIDropDownWidget(_myFont, theTitle, theMenue);
		myDropDown.adjustLabelBySelection(false);
		myDropDown.showIcon(false);
		myDropDown.itemSelectBackground(new CCUIFillDrawable(new CCColor(100)));
		theMenue.background(new CCUIFillDrawable(new CCColor(50)));
		addChild(myDropDown);
		theMenue.translation().set(0, -height());
	}
}
