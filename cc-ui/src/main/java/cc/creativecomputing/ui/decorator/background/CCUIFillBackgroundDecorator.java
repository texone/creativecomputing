/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.ui.decorator.background;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shape.CCRectangle;
import cc.creativecomputing.io.xml.property.CCXMLPropertyObject;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.widget.CCUIWidget;

/**
 * @author christianriekoff
 *
 */
public class CCUIFillBackgroundDecorator extends CCUIBackgroundDecorator{
	
	public final static String ID = "fill_background";
	
	private CCRectangle _myRectangle;
	
	@CCProperty(name = "color")
	private CCColor _myColor = new CCColor(1f);

	/**
	 * @param theID
	 */
	public CCUIFillBackgroundDecorator() {
		super("fill");
		_myRectangle = new CCRectangle();
	}
	
	public CCUIFillBackgroundDecorator(CCColor theColor) {
		this();
		_myColor = theColor;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.decorator.CCUIDecorator#draw(cc.creativecomputing.graphics.CCGraphics, cc.creativecomputing.newui.widget.CCUIWidget)
	 */
	@Override
	public void draw(CCGraphics g, CCUIWidget theWidget) {
		_myRectangle.color().set(_myColor);
		_myRectangle.position(0, -theWidget.height());
		_myRectangle.size(theWidget.width(), theWidget.height());
		_myRectangle.draw(g);
	}

}
