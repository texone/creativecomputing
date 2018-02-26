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
package cc.creativecomputing.control.handles;

import cc.creativecomputing.control.code.CCShaderSource;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.io.data.CCDataObject;

public class CCShaderSourceHandle extends CCPropertyHandle<CCShaderSource>{
	
	protected CCShaderSourceHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
	}
	
	@Override
	public CCDataObject data() {
		CCDataObject myResult = super.data();
		CCShaderSource myShaderObject = value();
		if(!myShaderObject.object().saveInFile()){
			myResult.put("source", myShaderObject.sourceCode());
		}
		return myResult;
	}
	
	@Override
	public void data(CCDataObject theData) {
		CCShaderSource myShaderObject = value();
		if(!myShaderObject.object().saveInFile()){
			if(theData.containsKey("source")){	
				myShaderObject.sourceCode(theData.getString("source"));
			}else{
				myShaderObject.sourceCode("");
			}
		}
		onChange();
	}
	
	@Override
	public double normalizedValue() {
		return 0;
	}

	@Override
	public String valueString() {
		return null;
	}
}
