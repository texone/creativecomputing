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
package cc.creativecomputing.ui;

import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.math.CCVector1;
import cc.creativecomputing.math.CCVector2;

/**
 * @author christianriekoff
 *
 */
public class CCUIPropertyObject {

	protected Map<String, Object> _myProperties = new HashMap<String, Object>();
	
	public CCVector1 property1f(String theProperty) {
		return (CCVector1)_myProperties.get(theProperty);
	}
	
	public CCVector2 property2f(String theProperty) {
		return (CCVector2)_myProperties.get(theProperty);
	}
}
