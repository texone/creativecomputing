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
import cc.creativecomputing.io.xml.property.CCXMLProperty;
import cc.creativecomputing.io.xml.property.CCXMLPropertyObject;
import cc.creativecomputing.math.CCColor;

/**
 * @author christianriekoff
 *
 */
public class CCUIGradient {

	@CCProperty(name = "left_top")
	private CCColor _myLeftTop;

	@CCProperty(name = "right_top")
	private CCColor _myRightTop;

	@CCProperty(name = "left_bottom")
	private CCColor _myLeftBottom;

	@CCProperty(name = "right_bottom")
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
	
	@CCProperty(name = "left")
	public void left(CCColor theColor) {
		_myLeftTop.set(theColor);
		_myLeftBottom.set(theColor);
	}
	
	@CCProperty(name = "right")
	public void right(CCColor theColor) {
		_myRightTop.set(theColor);
		_myRightBottom.set(theColor);
	}
	
	@CCProperty(name = "top")
	public void top(CCColor theColor) {
		_myLeftTop.set(theColor);
		_myRightTop.set(theColor);
	}
	
	@CCProperty(name = "bottom")
	public void bottom(CCColor theColor) {
		_myLeftBottom.set(theColor);
		_myRightBottom.set(theColor);
	}
}
