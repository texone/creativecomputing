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
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.ui.widget.CCUIWidget;

/**
 * @author christianriekoff
 *
 */
public class CCUIGradientDrawable implements CCUIDrawable{
	
	
	@CCProperty(name = "gradient")
	private CCUIGradient _myGradient = new CCUIGradient();

	/**
	 * @param theID
	 */
	public CCUIGradientDrawable() {
	}
	
	public CCUIGradient gradient() {
		return _myGradient;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.decorator.CCUIDecorator#draw(cc.creativecomputing.graphics.CCGraphics, cc.creativecomputing.newui.widget.CCUIWidget)
	 */
	@Override
	public void draw(CCGraphics g, CCUIWidget theWidget) {
		g.beginShape(CCDrawMode.QUADS);
		g.color(_myGradient.leftTop());
		g.vertex(0,0);
		g.color(_myGradient.rightTop());
		g.vertex(theWidget.width(), 0);
		g.color(_myGradient.rightBottom());
		g.vertex(theWidget.width(), -theWidget.height());
		g.color(_myGradient.leftBottom());
		g.vertex(0, -theWidget.height());
		g.endShape();
	}

}