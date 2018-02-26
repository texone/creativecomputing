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
import cc.creativecomputing.graphics.shape.CCRoundedRectangleOutline;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.widget.CCUIWidget;

/**
 * @author christianriekoff
 *
 */
public class CCUIStrokeDrawable implements CCUIDrawable{
	
	private CCRoundedRectangleOutline _myRoundedRectangle;
	
	@CCProperty(name = "weight")
	public double weight = 1;
	
	@CCProperty(name = "color")
	public CCColor color = new CCColor(1f);
	
	@CCProperty(name = "radius")
	public  double cornerRadius = 0;

	/**
	 * @param theID
	 */
	public CCUIStrokeDrawable() {
		_myRoundedRectangle = new CCRoundedRectangleOutline();
	}
	
	public CCUIStrokeDrawable(CCColor theColor, double theWeight, double theCornerRadius) {
		this();
		color = theColor;
		weight = theWeight;
		cornerRadius = theCornerRadius;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.ui.decorator.CCUIDecorator#draw(cc.creativecomputing.graphics.CCGraphics, cc.creativecomputing.ui.widget.CCUIWidget)
	 */
	@Override
	public void draw(CCGraphics g, CCUIWidget theWidget) {
		g.strokeWeight(weight);
		_myRoundedRectangle.color().set(color);
		_myRoundedRectangle.radius(cornerRadius);
		_myRoundedRectangle.position(0, -theWidget.height());
		_myRoundedRectangle.size(theWidget.width(), theWidget.height());
		_myRoundedRectangle.draw(g);
	}

	
}
