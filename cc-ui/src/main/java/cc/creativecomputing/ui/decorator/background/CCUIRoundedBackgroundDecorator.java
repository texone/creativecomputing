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
import cc.creativecomputing.graphics.shape.CCRoundedRectangle;
import cc.creativecomputing.io.xml.property.CCXMLProperty;
import cc.creativecomputing.io.xml.property.CCXMLPropertyObject;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.widget.CCUIWidget;

/**
 * @author christianriekoff
 *
 */
@CCXMLPropertyObject(name=CCUIRoundedBackgroundDecorator.ID)
public class CCUIRoundedBackgroundDecorator extends CCUIBackgroundDecorator{
	
	public final static String ID = "rounded_background";
	
	private CCRoundedRectangle _myRoundedRectangle;
	
	@CCXMLProperty(name = "color")
	private CCColor _myColor = new CCColor(1f);
	
	@CCProperty(name = "radius")
	private  double _myCornerRadius = 5;

	/**
	 * @param theID
	 */
	public CCUIRoundedBackgroundDecorator(CCColor theColor, double theRadius) {
		this();
		_myColor = theColor;
		_myCornerRadius = theRadius;
	}
	
	public CCUIRoundedBackgroundDecorator(){
		super("fill");
		_myRoundedRectangle = new CCRoundedRectangle();
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.decorator.CCUIDecorator#draw(cc.creativecomputing.graphics.CCGraphics, cc.creativecomputing.newui.widget.CCUIWidget)
	 */
	@Override
	public void draw(CCGraphics g, CCUIWidget theWidget) {
		_myRoundedRectangle.color().set(_myColor);
		_myRoundedRectangle.gradientColor().set(_myColor);
		_myRoundedRectangle.radius(_myCornerRadius);
		_myRoundedRectangle.position(0, 0);
		_myRoundedRectangle.size(theWidget.width(), theWidget.height());
		_myRoundedRectangle.draw(g);
	}

}
