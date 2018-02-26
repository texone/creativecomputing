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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;

import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.control.handles.CCGradientPropertyHandle;
import cc.creativecomputing.controlui.CCControlComponent;

public class CCGradientControl extends CCValueControl<CCGradient, CCGradientPropertyHandle>{
	
	private CCGradientEditor _myGradientEditor = new CCGradientEditor();
	
	private CCGradient _myGradient;

	static final Dimension SMALL_BUTTON_SIZE = new Dimension(100,15);

	public CCGradientControl(CCGradientPropertyHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		_myHandle.events().add(theValue -> {
			_myGradient = ((CCGradient)theValue).clone();
			_myGradientEditor.gradient(_myGradient);
		});
		
		_myGradient = theHandle.value().clone();
 
        //Create the Button.
		_myGradientEditor.gradient(_myGradient);
		_myGradientEditor.events().add(theGradient -> {
			_myHandle.value(theGradient, false);
		});
	}
	
	@Override
	public CCGradient value() {
		return _myGradient;
	}
	
	private JPanel _myPanel;
	
	@Override
	public void addToComponent(JPanel thePanel, int theY, int theDepth) {
		thePanel.add(_myLabel,  constraints(0, theY, GridBagConstraints.LINE_END, 5, 5, 1, 5));
		
		_myPanel = thePanel;
		_myGradientEditor.setPreferredSize(new Dimension(202,20));
		_myGradientEditor.setBackground(_myPanel.getBackground());
		JPanel myPanel = new JPanel();
		myPanel.setBackground(new Color(255,0,0));
		thePanel.add(_myGradientEditor,  constraints(1, theY, 2, GridBagConstraints.LINE_START,0, 0, 0, 0));
//		thePanel.add(_myColorPanel, myConstraints);
	}
}
