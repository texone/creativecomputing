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

import java.awt.GridBagConstraints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import cc.creativecomputing.control.handles.CCSplineHandle;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.math.spline.CCSpline;

public class CCSplineControl extends CCValueControl<CCSpline, CCSplineHandle>{

	private JButton _myButton;
	
	private CCSplineEditor _myCurveFrame;

	public CCSplineControl(CCSplineHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		addListener(theValue -> {
			_myHandle.value(theValue, false);
		});
		
		_myCurveFrame = new CCSplineEditor(theHandle.name());
		_myCurveFrame.setSize(300, 300);
		_myCurveFrame.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent theE) {
//				value().currentCurve(_myEnvelopeCurve);
			}
		});
        
        _myButton = new JButton("edit");
        _myButton.addActionListener(theE -> {
        	_myCurveFrame.spline(value());				
        	_myCurveFrame.setVisible(true);
		});
        CCUIStyler.styleButton(_myButton, 30, 15);
 
	}
	
	@Override
	public void addToComponent(JPanel thePanel, int theY, int theDepth) {
		thePanel.add(_myLabel, 	constraints(0, theY, GridBagConstraints.LINE_END, 	5, 5, 1, 5));
		thePanel.add(_myButton, constraints(1, theY, GridBagConstraints.LINE_START, 5, 4, 1, 5));
	}

	@Override
	public CCSpline value() {
		return _myHandle.value();
	}
}
