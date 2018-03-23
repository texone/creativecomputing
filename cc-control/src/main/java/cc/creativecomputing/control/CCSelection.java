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
package cc.creativecomputing.control;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCEventManager;

public class CCSelection {

	private List<String> _myValues = new ArrayList<String>();
	
	private String _myValue = "";
	
	public CCEventManager<CCSelection> changeEvents = new CCEventManager<>();
	public CCEventManager<CCSelection> addEvents = new CCEventManager<>();
	
	public CCSelection(List<String> theValues){
		_myValues.addAll(theValues);
	}
	
	public CCSelection(){
		
	}
	
	public String value(){
		return _myValue;
	}
	
	public void add(String theValue){
		if(_myValue.equals(""))_myValue = theValue;
		_myValues.add(theValue);
		addEvents.event(this);
	}
	
	public List<String> values(){
		return _myValues;
	}
	
	public void value(String theValue){
		if(theValue != null && theValue.equals(_myValue))return;
		_myValue = theValue;
		changeEvents.event(this);
	}
}
