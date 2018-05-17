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

import cc.creativecomputing.control.handles.CCStringPropertyHandle;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.ui.layout.CCUIGridPane;
import cc.creativecomputing.ui.widget.CCUITextFieldWidget;

public class CCStringControl extends CCValueControl<String, CCStringPropertyHandle>{
	
	private CCUITextFieldWidget _myTextField;

	public CCStringControl(CCStringPropertyHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		addListener(theValue -> {
			try{
				if(!_myHandle.value().equals(_myTextField.text())){
					_myTextField.text(_myHandle.value(), false);
				}
			}catch(Exception e){}
			
		});

        _myTextField = new CCUITextFieldWidget(theHandle.value());
        _myTextField.width(100);
        _myTextField.stretchWidth(true);
        
        _myTextField.changeEvents.add(text -> {
        	_myHandle.value(_myTextField.text(), !_myHandle.isInEdit());
        });
	}
	
	@Override
	public String value() {
		return _myTextField.textField().text();
	}
	
	@Override
	public void addToPane(CCUIGridPane thePane, int theY, int theDepth) {
		thePane.addChild(_myLabel, 0, theY);
		thePane.addChild(_myTextField, 1, theY);
	}
}
