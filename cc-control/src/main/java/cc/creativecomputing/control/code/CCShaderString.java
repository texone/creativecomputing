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
package cc.creativecomputing.control.code;

public class CCShaderString extends CCShaderSource{

	protected String _mySourceCode;
	private boolean _myIsUpdated = true;
	
	public CCShaderString(CCShaderObject theShaderObject, String theSourceCode){
		super(theShaderObject);
		_mySourceCode = theSourceCode;
	}
	
	@Override
	public String sourceCode(){
		_myIsUpdated = false;
		return _mySourceCode;
	}
	
	@Override
	public boolean isUpdated() {
		return _myIsUpdated;
	}
	
	public void sourceCode(String theSourceCode){
		_myIsUpdated = true;
		_mySourceCode = theSourceCode;
		_myShaderObject.update();
	}
}
