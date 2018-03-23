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
import java.awt.Insets;

import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.controlui.CCPropertyPopUp;
import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.core.CCEventManager.CCEvent;
import cc.creativecomputing.gl.app.CCGLMouseButton;
import cc.creativecomputing.ui.CCUIHorizontalAlignment;
import cc.creativecomputing.ui.CCUIVerticalAlignment;
import cc.creativecomputing.ui.widget.CCUILabelWidget;

public abstract class CCValueControl<Type, Handle extends CCPropertyHandle<Type>> implements CCControl{
	
	protected Handle _myHandle;
	
	protected CCUILabelWidget _myLabel;
	
	protected CCPropertyPopUp _myPopUp;
	
	protected CCControlComponent _myControlComponent;

	public CCValueControl(Handle theHandle, CCControlComponent theControlComponent){
		
		_myHandle = theHandle;
		_myControlComponent = theControlComponent;
		
		_myPopUp = new CCPropertyPopUp(theHandle, _myControlComponent);
		
        //Create the label.
		_myLabel = new CCUILabelWidget(CCUIConstants.DEFAULT_FONT, _myHandle.name());
		_myLabel.horizontalAlignment (CCUIHorizontalAlignment.RIGHT);
		_myLabel.verticalAlignment(CCUIVerticalAlignment.CENTER);
		
		_myLabel.mousePressed.add(e -> {
			if(e.isAltDown()){
				_myHandle.restoreDefault();
				return;
			}
			if(e.isControlDown()){
				_myHandle.restorePreset();
				return;
			}
			if(e.button == CCGLMouseButton.BUTTON_RIGHT){
				_myPopUp.isActive(true);
			}
		});
	}

	public Handle property() {
		return _myHandle;
	}
	
	private CCEvent<Type> _myListener = null;
	
	public void addListener(CCEvent<Type> theListener){
		_myHandle.changeEvents.add(_myListener = theListener);
	}
	
	public void dispose() {
		if(_myListener != null)_myHandle.changeEvents.remove(_myListener);
	}
	
	
	protected GridBagConstraints constraints(int theX, int theY, int theWidth, int theAnchor, int theTop, int theLeft, int theBottom, int theRight){
		GridBagConstraints myResult = new GridBagConstraints();
		myResult.gridx = theX;
		myResult.gridy = theY;
		myResult.gridwidth = theWidth;
		myResult.insets = new Insets(theTop, theLeft, theBottom, theRight);
		myResult.anchor = theAnchor;
		return myResult;
	}
	
	protected GridBagConstraints constraints(int theX, int theY, int theAnchor, int theTop, int theLeft, int theBottom, int theRight){
		return constraints(theX, theY, 1, theAnchor,  theTop, theLeft, theBottom, theRight);
	}
	
	public abstract Type value();
	
}
