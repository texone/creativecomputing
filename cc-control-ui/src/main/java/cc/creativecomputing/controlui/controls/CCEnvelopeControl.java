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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.handles.CCEnvelopeHandle;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.CCUIHorizontalAlignment;
import cc.creativecomputing.ui.CCUIVerticalAlignment;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.ui.layout.CCUIGridPane;
import cc.creativecomputing.ui.layout.CCUIGridPane.CCUITableEntry;
import cc.creativecomputing.ui.widget.CCUILabelWidget;

public class CCEnvelopeControl extends CCValueControl<CCEnvelope, CCEnvelopeHandle>{

	private CCUILabelWidget _myButton;
	
	private CCEnvelopeEditor _myCurveFrame;

	public CCEnvelopeControl(CCEnvelopeHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		addListener(theValue -> {
			_myHandle.value(theValue, false);
        	_myCurveFrame.track().trackData(value().curve());
			_myCurveFrame.render();
			_myCurveFrame.repaint();
		});
		
		_myCurveFrame = new CCEnvelopeEditor(theHandle.path().toString());
		_myCurveFrame.setSize(300, 300);
		_myCurveFrame.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent theE) {
			}
			
			@Override
			public void windowOpened(WindowEvent e) {
				_myCurveFrame.render();
			}
		});
        
        _myButton = new CCUILabelWidget(CCUIConstants.DEFAULT_FONT, "edit");
        _myButton.horizontalAlignment(CCUIHorizontalAlignment.LEFT);
        _myButton.verticalAlignment(CCUIVerticalAlignment.CENTER);
        _myButton.background(new CCUIFillDrawable(new CCColor(0.3d)));
        _myButton.width(100);
        _myButton.inset(4);
        
        _myButton.mouseReleased.add(theE -> {
        	_myCurveFrame.track().trackData(value().curve());				
        	_myCurveFrame.setVisible(true);
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
