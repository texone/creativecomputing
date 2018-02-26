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
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cc.creativecomputing.control.handles.CCPathHandle;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.ui.layout.CCUIGridPane;

public class CCPathControl extends CCValueControl<Path, CCPathHandle>{
	
	private JTextField _myTextField;
	
	private JButton _myOpenButton;

	public CCPathControl(CCPathHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		addListener(theValue -> {
			try{
				if(_myHandle.value() == null)_myTextField.setText("");
				else _myTextField.setText(_myHandle.value().toString());
			}catch(Exception e){
					
			}
			
		});

        String _myValue = theHandle.path() == null ? "" : theHandle.path().toString();
        _myTextField = new JTextField(_myValue);
        
        _myOpenButton = new JButton("edit");
        _myOpenButton.addActionListener(theE -> {
        	Path myOldPath = _myHandle.value();
        	Path myPath;
        	if(myOldPath != null && myOldPath.getParent() != null){
        		myPath = CCNIOUtil.selectInput("", myOldPath, _myHandle.extensions());
			}else{
				myPath = CCNIOUtil.selectInput("", null, _myHandle.extensions());
			}
        	if(myPath == null)return;
        	_myHandle.value(myPath, !_myHandle.isInEdit());
        	_myTextField.setText(myPath.toString());
			
		});
	}
	
	
	
	@Override
	public void addToPane(CCUIGridPane thePane, int theY, int theDepth) {
		thePanel.add(_myLabel, constraints(0, theY, GridBagConstraints.LINE_END,5, 5, 1, 5));
		thePanel.add(_myTextField, constraints(1, theY, GridBagConstraints.LINE_START,5, 5, 1, 5));
		thePanel.add(_myOpenButton, constraints(2, theY, GridBagConstraints.LINE_START,5, 5, 1, 5));
	}

	@Override
	public Path value() {
		return _myHandle.value();
	}
}
