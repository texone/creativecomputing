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
import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.CCUIHorizontalAlignment;
import cc.creativecomputing.ui.CCUIVerticalAlignment;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.ui.layout.CCUIGridPane;
import cc.creativecomputing.ui.layout.CCUIGridPane.CCUITableEntry;
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

        String myValue = theHandle.value();
        _myTextField = new CCUITextFieldWidget(CCUIConstants.DEFAULT_FONT, myValue);
        _myTextField.horizontalAlignment(CCUIHorizontalAlignment.LEFT);
        _myTextField.verticalAlignment(CCUIVerticalAlignment.CENTER);
        _myTextField.background(new CCUIFillDrawable(new CCColor(0.3d)));
        _myTextField.width(100);
        _myTextField.inset(4);
        
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
		
		CCUITableEntry myEntry = new CCUITableEntry();
		myEntry.column = 0;
		myEntry.row = theY;
		thePane.addChild(_myLabel, myEntry);
		
		myEntry.column = 1;
		thePane.addChild(_myTextField, myEntry);
	}
}
