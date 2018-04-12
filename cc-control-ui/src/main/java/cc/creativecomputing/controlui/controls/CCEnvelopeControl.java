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

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.handles.CCEnvelopeHandle;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.ui.layout.CCUIGridPane;
import cc.creativecomputing.ui.layout.CCUIGridPane.CCUITableEntry;
import cc.creativecomputing.ui.widget.CCUIIconWidget;

public class CCEnvelopeControl extends CCValueControl<CCEnvelope, CCEnvelopeHandle>{

	private CCUIIconWidget _myButton;
	
	private CCEnvelopeEditor _myCurveFrame;

	public CCEnvelopeControl(CCEnvelopeHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		addListener(theValue -> {
			_myHandle.value(theValue, false);
		});
		
		_myCurveFrame = new CCEnvelopeEditor(theHandle.path().toString());
		_myCurveFrame.width = 300;
		_myCurveFrame.height = 300;
		
		_myButton = new CCUIIconWidget(CCEntypoIcon.ICON_EDIT);
		_myButton.background(new CCUIFillDrawable(new CCColor(0.3d)));
		_myButton.inset(2);
		_myButton.mouseReleased.add(event -> {
			_myCurveFrame.trackData(value().curve());				
        	CCControlApp.appManager.add(_myCurveFrame);
        	_myCurveFrame.show();
		});
 
	}
	
	@Override
	public void addToPane(CCUIGridPane thePane, int theY, int theDepth) {
		CCUITableEntry myEntry = new CCUITableEntry();
		myEntry.column = 0;
		myEntry.row = theY;
		thePane.addChild(_myLabel, myEntry);
		
		myEntry.column = 1;
		thePane.addChild(_myButton, myEntry);
	}

	@Override
	public CCEnvelope value() {
		return _myHandle.value();
	}
}
