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

package cc.creativecomputing.ui;

import cc.creativecomputing.io.xml.property.CCXMLProperty;
import cc.creativecomputing.io.xml.property.CCXMLPropertyObject;

/**
 * @author christianriekoff
 *
 */
public class CCUISpacing {

	@CCXMLProperty(name = "left", node = false, optional = true)
	private float _myLeft;

	@CCXMLProperty(name = "right", node = false, optional = true)
	private float _myRight;

	@CCXMLProperty(name = "top", node = false, optional = true)
	private float _myTop;

	@CCXMLProperty(name = "bottom", node = false, optional = true)
	private float _myBottom;
	
	public CCUISpacing(float theLeft, float theRight, float theTop, float theBottom) {
		_myLeft = theLeft;
		_myRight = theRight;
		_myTop = theTop;
		_myBottom = theBottom;
	}
	
	public CCUISpacing() {
		this(0,0,0,0);
	}
	
	public float left() {
		return _myLeft;
	}
	
	public float right() {
		return _myRight;
	}
	
	public float top() {
		return _myTop;
	}
	
	public float bottom() {
		return _myBottom;
	}
}
