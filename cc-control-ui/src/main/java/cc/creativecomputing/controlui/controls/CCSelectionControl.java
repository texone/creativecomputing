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

import cc.creativecomputing.control.CCSelection;
import cc.creativecomputing.control.handles.CCSelectionPropertyHandle;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.core.CCEventManager.CCEvent;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.CCUIVerticalAlignment;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.ui.layout.CCUIGridPane;
import cc.creativecomputing.ui.layout.CCUIGridPane.CCUITableEntry;
import cc.creativecomputing.ui.widget.CCUIDropDownWidget;

public class CCSelectionControl extends CCValueControl<CCSelection, CCSelectionPropertyHandle>{
	
	private CCUIDropDownWidget _myDropDown;
	
	private CCSelection _myValue;
	
	private CCEvent<CCSelection> _myChangeListener;
	private CCEvent<CCSelection> _myAddListener;

	public CCSelectionControl(CCSelectionPropertyHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		_myHandle.value().changeEvents.add(_myChangeListener = theSelection -> {
			_myDropDown.selectedItem(theSelection.value(), false);
		});
		_myHandle.value().addEvents.add(_myAddListener = theSelection -> {
			_myDropDown.removeAllItems();
			for(String myEnum:_myValue.values()){
				_myDropDown.addItem(myEnum);
	        }
		});
 
		addListener(theValue -> {
			_myValue = theValue;
			_myDropDown.selectedItem(_myHandle.value().value(), false);
		});

        _myValue = theHandle.value();
        
        _myDropDown = new CCUIDropDownWidget(CCUIConstants.DEFAULT_FONT);
        _myDropDown.inset(4);
		_myDropDown.width(100);
        _myDropDown.verticalAlignment(CCUIVerticalAlignment.CENTER);
        
		CCUIFillDrawable myBackground = new CCUIFillDrawable(new CCColor(0.3d));
		_myDropDown.background(myBackground);
		_myDropDown.menue().background(myBackground);
		
		_myDropDown.itemSelectBackground(new CCUIFillDrawable(new CCColor(0.5d)));
		_myDropDown.itemBackground(new CCUIFillDrawable(new CCColor(0.3d)));
        for(String myEnum:_myValue.values()){
        		_myDropDown.addItem(myEnum);
        }
        _myDropDown.changeEvents.add(e -> {
			if(e == null)return;
			_myHandle.value().value(e);	
		});
        _myDropDown.selectedItem(_myHandle.value().value(), false);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		_myHandle.value().changeEvents.remove(_myChangeListener);
		_myHandle.value().addEvents.remove(_myAddListener);
	}
	
	@Override
	public CCSelection value() {
		return _myValue;
	}
	
	@Override
	public void addToPane(CCUIGridPane thePane, int theY, int theDepth) {
		CCUITableEntry myEntry = new CCUITableEntry();
		myEntry.column = 0;
		myEntry.row = theY;
		thePane.addChild(_myLabel, myEntry);
		
		myEntry.column = 1;
		thePane.addChild(_myDropDown, myEntry);
	}
}
