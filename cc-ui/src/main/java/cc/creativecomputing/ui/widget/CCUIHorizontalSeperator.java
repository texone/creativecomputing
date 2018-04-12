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

import cc.creativecomputing.graphics.CCGraphics;

public class CCUIHorizontalSeperator extends CCUIWidget{

	public CCUIHorizontalSeperator(double theHeight){
		_myHeight = theHeight;
	}
	
	@Override
	public double width() {
		return _myParent.width() - _myParent.leftInset() - _myParent.rightInset();
	}
	
	@Override
	public void drawContent(CCGraphics g) {
		g.color(255);
		g.rect(0, -_myHeight / 2 - 2, width(), 4);
	}
}
