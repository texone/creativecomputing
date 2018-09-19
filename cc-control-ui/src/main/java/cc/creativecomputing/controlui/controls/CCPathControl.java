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

import java.nio.file.Path;

import cc.creativecomputing.control.handles.CCPathHandle;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.ui.widget.CCUIFileWidget;
import cc.creativecomputing.ui.widget.CCUIWidget;
import cc.creativecomputing.yoga.CCYogaNode.CCYogaEdge;

public class CCPathControl extends CCValueControl<Path, CCPathHandle>{
	
	private CCUIFileWidget _myFileWidget;

	public CCPathControl(CCPathHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		addListener(theValue -> {
			try{
//				if(_myHandle.value() == null)_myTextField.text("");
//				else _myTextField.text(_myHandle.value().toString());
			}catch(Exception e){
					
			}
			
		});

        String _myValue = theHandle.path() == null ? "" : theHandle.path().toString();
        _myFileWidget = new CCUIFileWidget();
        _myFileWidget.flex(1);
        _myFileWidget.padding(CCYogaEdge.ALL, 4);
	}

	@Override
	public void addToHorizontalPane(CCUIWidget thePane) {
		thePane.addChild(_myFileWidget);
	}

	@Override
	public Path value() {
		return _myHandle.value();
	}
}
