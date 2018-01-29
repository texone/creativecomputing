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

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.io.xml.property.CCXMLProperty;
import cc.creativecomputing.io.xml.property.CCXMLPropertyObject;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.CCUI;
import cc.creativecomputing.ui.widget.CCUIWidget;

/**
 * @author christianriekoff
 *
 */
@CCXMLPropertyObject(name=CCUITextureBackgroundDecorator.ID)
public class CCUITextureBackgroundDecorator extends CCUIBackgroundDecorator{
	
	public final static String ID = "texture_background";
	
	private CCUITexture _myTexture;
	
	@CCXMLProperty(name = "color", optional = true)
	private CCColor _myColor = new CCColor(1f);
	
	@CCXMLProperty(name = "texture")
	private String _myTextureFile;
	/**
	 * @param theID
	 */
	public CCUITextureBackgroundDecorator() {
		super("fill");
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.decorator.CCUIDecorator#setup(cc.creativecomputing.newui.CCUI, cc.creativecomputing.newui.widget.CCUIWidget)
	 */
	@Override
	public void setup(CCUI theUI, CCUIWidget theWidget) {
		_myTexture = theUI.texture(_myTextureFile);
		_myTexture.setup(theUI);
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.decorator.CCUIDecorator#draw(cc.creativecomputing.graphics.CCGraphics, cc.creativecomputing.newui.widget.CCUIWidget)
	 */
	@Override
	public void draw(CCGraphics g, CCUIWidget theWidget) {
		g.color(_myColor);
		_myTexture.dimension(theWidget.width(), theWidget.height());
		_myTexture.draw(g);
	}

}
