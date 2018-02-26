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

public class CCShaderSource {

	protected String _mySourceCode;
	
	protected CCShaderObject _myShaderObject;
	
	public CCShaderSource(CCShaderObject theShaderObject, String theSourceCode){
		_myShaderObject = theShaderObject;
		_mySourceCode = theSourceCode;
	}
	
	public CCShaderObject object(){
		return _myShaderObject;
	}
	
	public String sourceCode(){
		return _mySourceCode;
	}
	
	public void sourceCode(String theSourceCode){
		_mySourceCode = theSourceCode;
		_myShaderObject.update();
	}

	public String errorLog() {
		return _myShaderObject.errorLog();
	}
	
	public void save(){
	}
	
	public static void main(String[] args) {
	}
}
