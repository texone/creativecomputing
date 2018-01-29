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
package cc.creativecomputing.ui.widget;

import cc.creativecomputing.io.xml.property.CCXMLProperty;
import cc.creativecomputing.io.xml.property.CCXMLPropertyObject;
import cc.creativecomputing.ui.CCUI;

/**
 * @author christianriekoff
 *
 */
@CCXMLPropertyObject(name = "panel_widget")
public class CCUIPanelWidget extends CCUIWidget{
	
	@CCXMLProperty(name = "label", optional = true)
	private CCUIWidget _myLabel;
	
	public CCUIPanelWidget(int theWidth, int theHeight) {
		super(theWidth, theHeight);
		_myLabel = new CCUIWidget(0,0);
		addChild(_myLabel);
	}
	
	private CCUIPanelWidget() {
		
	}
	
	@Override
	public void setup(CCUI theUI, CCUIWidget theParent) {
		if(_myLabel != null) {
			_myLabel.width(width());
			_myLabel.height(height());
			addChild(_myLabel);
		}
		
		super.setup(theUI, this);
	}
	
	public CCUIWidget label() {
		return _myLabel;
	}
}
