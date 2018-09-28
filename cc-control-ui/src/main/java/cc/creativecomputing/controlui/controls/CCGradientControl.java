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

import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.control.handles.CCGradientHandle;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.ui.widget.CCUIGradientWidget;
import cc.creativecomputing.ui.widget.CCUIWidget;

public class CCGradientControl extends CCValueControl<CCGradient, CCGradientHandle>{
	
	private CCUIGradientWidget _myGradientEditor = new CCUIGradientWidget(240,14);
	
	private CCGradient _myGradient;

	public CCGradientControl(CCGradientHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		addListener(theValue -> {
			_myGradient = ((CCGradient)theValue).clone();
			_myGradientEditor.gradient(_myGradient);
		});
		
		_myGradient = theHandle.value().clone();
 
        //Create the Button.
		_myGradientEditor.gradient(_myGradient);
		_myGradientEditor.changeEvents.add(theGradient -> {
			_myHandle.value(theGradient, false);
		});
	}
	
	@Override
	public CCGradient value() {
		return _myGradient;
	}

	@Override
	public void addToHorizontalPane(CCUIWidget thePane) {
		thePane.addChild(_myGradientEditor);
	}
}
