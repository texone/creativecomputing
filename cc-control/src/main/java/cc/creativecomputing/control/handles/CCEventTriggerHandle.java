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

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMethod;
import cc.creativecomputing.io.data.CCDataObject;

public class CCEventTriggerHandle extends CCPropertyHandle<Object>{
	
	private CCMethod<CCProperty> _myMethod;
	
	private CCTriggerProgress _myProgress;
	
	protected CCEventTriggerHandle(CCObjectPropertyHandle theParent, CCMethod<CCProperty> theMethod) {
		super(theParent, theMethod);
		_myMethod = theMethod;
		_myProgress = new CCTriggerProgress();
	}
	
	public CCTriggerProgress progress(){
		return _myProgress;
	}

	@Override
	public double normalizedValue() {
		return 0;
	}

	@Override
	public String valueString() {
		return "";
	}
	
	@Override
	public void value(Object theValue, boolean theOverWrite) {}
	
	private boolean _myDoTrigger = false;
	
	public void trigger(){
		_myDoTrigger = true;
	}
	
	public void interrupt(){
		
	}
	
	@Override
	public void update(double theDeltaTime) {
		if(!_myDoTrigger)return;
		if(_myMethod.type() == CCTriggerProgress.class){
			_myMethod.trigger(_myProgress);
		}else{
			_myMethod.trigger();
		}
		_myDoTrigger = false;
	}

	@Override
	public CCDataObject data(){
		CCDataObject myResult = new CCDataObject();
		return myResult;
	}
	
	@Override
	public void data(CCDataObject theData){}
}
