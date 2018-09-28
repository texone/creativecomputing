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

package cc.creativecomputing.ui.draw;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.yoga.CCYogaNode;

/**
 * @author christianriekoff
 *
 */
public class CCUIFillDrawable implements CCUIDrawable{
	
	@CCProperty(name = "color")
	private CCColor _myColor = new CCColor(1f);

	public CCUIFillDrawable() {
	}
	
	public CCUIFillDrawable(CCColor theColor) {
		this();
		_myColor = theColor.clone();
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.decorator.CCUIDecorator#draw(cc.creativecomputing.graphics.CCGraphics, cc.creativecomputing.newui.widget.CCUIWidget)
	 */
	@Override
	public void draw(CCGraphics g, CCYogaNode theWidget) {
		g.color(_myColor);
		g.rect(0, 0, theWidget.width(), theWidget.height());
	}

	public CCColor color() {
		return _myColor;
	}

}
