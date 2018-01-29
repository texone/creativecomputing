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

/**
 * @author christianriekoff
 *
 */
public class CCUIException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6290570168984165353L;

	public CCUIException() {
		super();
	}

	public CCUIException(String theArg0, Throwable theArg1) {
		super(theArg0, theArg1);
	}

	public CCUIException(String theArg0) {
		super(theArg0);
	}

	public CCUIException(Throwable theArg0) {
		super(theArg0);
	}

}
