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

import cc.creativecomputing.control.handles.CCBooleanPropertyHandle;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.ui.layout.CCUIGridPane;
import cc.creativecomputing.ui.widget.CCUICheckBox;

public class CCBooleanControl extends CCValueControl<Boolean, CCBooleanPropertyHandle>{
	
	private CCUICheckBox _myCheckBox;
	
	public CCBooleanControl(CCBooleanPropertyHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		addListener(theValue ->{
			_myCheckBox.isSelected(_myHandle.value(), false);
		});
 
		if(_myHandle.value() == null){
			_myHandle.value(false, true);
		}
        boolean _myValue = theHandle.value();
        _myCheckBox = new CCUICheckBox();
       
        _myCheckBox.changeEvents.add(e -> {
        	_myHandle.value(_myCheckBox.isSelected(), true);
		});
        _myCheckBox.isSelected(_myValue, false);
	}
	
	@Override
	public Boolean value() {
		return _myCheckBox.isSelected();
	}
	
	@Override
	public void addToPane(CCUIGridPane thePane, int theY, int theDepth) {
		thePane.addChild(_myLabel, 0, theY);
		thePane.addChild(_myCheckBox, 1, theY);
	}
}
