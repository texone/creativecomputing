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
import cc.creativecomputing.ui.draw.CCUIFillDrawable;

public class CCUIHorizontalSeperator extends CCUIWidget{
	
	public static CCUIWidgetStyle createDefaultStyle() {
		CCUIWidgetStyle myResult = new CCUIWidgetStyle();
		myResult.background(new CCUIFillDrawable(CCColor.WHITE));
		return myResult;
	}
	
	public CCUIHorizontalSeperator(){
		super(createDefaultStyle());
		minHeight(2);
		margin(CCYogaEdge.VERTICAL, 4);
	}
	
}
