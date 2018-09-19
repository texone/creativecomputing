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

import cc.creativecomputing.control.handles.CCEventTriggerHandle;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.controlui.CCProgressWindow;
import cc.creativecomputing.ui.widget.CCUILabelWidget;
import cc.creativecomputing.ui.widget.CCUIWidget;

public class CCEventTriggerControl extends CCValueControl<Object, CCEventTriggerHandle>{
	
	private CCUILabelWidget _myButton;
	
	private CCProgressWindow _myProgressWindow;
	
	public CCEventTriggerControl(CCEventTriggerHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
 
        //Create the Button.

        _myButton = new CCUILabelWidget("bang");
        _myButton.mouseReleased.add(theE -> {
			_myHandle.trigger();
		});
        
        _myHandle.progress().startEvents.add((o)->_myProgressWindow = new CCProgressWindow());
        _myHandle.progress().startEvents.add((o)->_myProgressWindow = new CCProgressWindow());
        _myHandle.progress().progressEvents.add(p -> _myProgressWindow.progress(p));
        _myHandle.progress().endEvents.add((o)->_myProgressWindow.setVisible(false));
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
	
	@Override
	public Object value() {
		return null;
	}
	
	@Override
	public void addToHorizontalPane(CCUIWidget thePane) {
		thePane.addChild(_myButton);
	}
}
