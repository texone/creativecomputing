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
package cc.creativecomputing.controlui.controls;

import cc.creativecomputing.control.handles.CCEnumPropertyHandle;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.ui.layout.CCUIGridPane;
import cc.creativecomputing.ui.layout.CCUIGridPane.CCUITableEntry;
import cc.creativecomputing.ui.widget.CCUIDropDownWidget;

public class CCEnumControl extends CCValueControl<Enum<?>, CCEnumPropertyHandle>{
	
	private CCUIDropDownWidget _myDropDown;

	public CCEnumControl(CCEnumPropertyHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
 
		addListener( theValue -> {
			_myDropDown.selectedItem(_myHandle.value().name(), false);
		});
        
        _myDropDown = new CCUIDropDownWidget();
		_myDropDown.width(100);
		_myDropDown.stretchWidth(true);
		
        for(Enum<?> myEnum:_myHandle.enumConstants()){
        	_myDropDown.addItem(myEnum.name());
        }
        
        _myDropDown.changeEvents.add(e -> {
        	if(e == null)return;
        	_myHandle.valueCasted(e, !_myHandle.isInEdit());
		});
        if(_myHandle.value() != null){
        	_myDropDown.selectedItem(_myHandle.value().name(), false);
        }
	}
	
	@Override
	public Enum<?> value() {
		return _myHandle.value();
	}
	
	@Override
	public void addToPane(CCUIGridPane thePane, int theY, int theDepth) {
		thePane.addChild(_myLabel, 0, theY);
		thePane.addChild(_myDropDown, 1, theY);
	}
}
