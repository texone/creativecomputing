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

import cc.creativecomputing.control.handles.CCColorPropertyHandle;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.layout.CCUIGridPane;
import cc.creativecomputing.ui.layout.CCUIGridPane.CCUITableEntry;
import cc.creativecomputing.ui.widget.CCUIColorPicker;

public class CCColorControl extends CCValueControl<CCColor, CCColorPropertyHandle>{
	

	private CCUIColorPicker _myColorPicker;
	
	private CCColor _myLastColor = new CCColor();

    private CCColor _myColor;

	public CCColorControl(CCColorPropertyHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);

		_myColor = theHandle.value().clone();
		_myColorPicker = new CCUIColorPicker(CCUIConstants.DEFAULT_FONT, _myColor, 100, 14);
		addListener(theValue -> {
			_myColor = theValue;
			_myColorPicker.color(_myColor, false);
		});
		
 
		_myColorPicker.changeEvents.add(c -> {
			_myColor = new CCColor(c);
			_myHandle.value(_myColor, _myHandle.isInEdit());
		});
	}
	
	@Override
	public CCColor value() {
		return _myColor;
	}
	
	@Override
	public void addToPane(CCUIGridPane thePane, int theY, int theDepth) {
		CCUITableEntry myEntry = new CCUITableEntry();
		myEntry.column = 0;
		myEntry.row = theY;
		thePane.addChild(_myLabel, myEntry);
		
		myEntry.column = 1;
		thePane.addChild(_myColorPicker, myEntry);
	}
}
