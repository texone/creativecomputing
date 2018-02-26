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

import cc.creativecomputing.core.events.CCListenerManager;

public class CCSelection {
	
	public interface CCSelectionListener{
		void onChange(String theValue);
		
		void onChangeValues(CCSelection theSelection);
	}

	private List<String> _myValues = new ArrayList<String>();
	
	private String _myValue = "";
	
	private CCListenerManager<CCSelectionListener> _myEvents = CCListenerManager.create(CCSelectionListener.class);
	
	public CCSelection(List<String> theValues){
		_myValues.addAll(theValues);
	}
	
	public CCSelection(){
		
	}
	
	public CCListenerManager<CCSelectionListener> events(){
		return _myEvents;
	}
	
	public String value(){
		return _myValue;
	}
	
	public void add(String theValue){
		if(_myValue.equals(""))_myValue = theValue;
		_myValues.add(theValue);
		_myEvents.proxy().onChangeValues(this);
	}
	
	public List<String> values(){
		return _myValues;
	}
	
	public void value(String theValue){
		if(theValue != null && theValue.equals(_myValue))return;
		_myValue = theValue;
		_myEvents.proxy().onChange(theValue);
	}
}
