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

import cc.creativecomputing.io.xml.property.CCXMLProperty;
import cc.creativecomputing.io.xml.property.CCXMLPropertyObject;
import cc.creativecomputing.math.CCColor;

/**
 * @author christianriekoff
 *
 */
@CCXMLPropertyObject(name = "ui_gradient")
public class CCUIGradient {

	@CCXMLProperty(name = "left_top", optional = true)
	private CCColor _myLeftTop;

	@CCXMLProperty(name = "right_top", optional = true)
	private CCColor _myRightTop;

	@CCXMLProperty(name = "left_bottom", optional = true)
	private CCColor _myLeftBottom;

	@CCXMLProperty(name = "right_bottom", optional = true)
	private CCColor _myRightBottom;
	
	public CCUIGradient() {
		_myLeftTop = new CCColor();
		_myLeftBottom = new CCColor();
		_myRightTop = new CCColor();
		_myRightBottom = new CCColor();
	}
	
	public CCColor leftTop() {
		return _myLeftTop;
	}
	
	public CCColor rightTop() {
		return _myRightTop;
	}
	
	public CCColor leftBottom() {
		return _myLeftBottom;
	}
	
	public CCColor rightBottom() {
		return _myRightBottom;
	}
	
	@CCXMLProperty(name = "left", optional = true)
	public void left(CCColor theColor) {
		_myLeftTop.set(theColor);
		_myLeftBottom.set(theColor);
	}
	
	@CCXMLProperty(name = "right", optional = true)
	public void right(CCColor theColor) {
		_myRightTop.set(theColor);
		_myRightBottom.set(theColor);
	}
	
	@CCXMLProperty(name = "top", optional = true)
	public void top(CCColor theColor) {
		_myLeftTop.set(theColor);
		_myRightTop.set(theColor);
	}
	
	@CCXMLProperty(name = "bottom", optional = true)
	public void bottom(CCColor theColor) {
		_myLeftBottom.set(theColor);
		_myRightBottom.set(theColor);
	}
}
