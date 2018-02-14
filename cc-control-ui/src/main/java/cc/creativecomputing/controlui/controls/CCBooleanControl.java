package cc.creativecomputing.controlui.controls;

import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.JToggleButton;

import cc.creativecomputing.control.handles.CCBooleanPropertyHandle;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.ui.widget.CCUICheckBoxWidget;

public class CCBooleanControl extends CCValueControl<Boolean, CCBooleanPropertyHandle>{
	
	private CCUICheckBoxWidget _myButton;
	
	private boolean _myIsSelected;
	
	private boolean _myTriggerEvent = true;
	
	public CCBooleanControl(CCBooleanPropertyHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		addListener(theValue ->{
			_myIsSelected = theValue;
			_myTriggerEvent = false;
			_myButton.setSelected(_myHandle.value());
			_myTriggerEvent = true;
		});
 
        //Create the Button.
		if(_myHandle.value() == null){
			_myHandle.value(false, true);
		}
        boolean _myValue = theHandle.value();
        _myButton = new CCUICheckBoxWidget(theHandle.name(), theHandle.value());
        CCUIStyler.styleButton(_myButton, 102, 13);
        _myButton.addChangeListener(theE -> {
        	if(!_myTriggerEvent)return;
        	_myHandle.value(_myButton.isSelected(), true);
		});
        _myButton.setSelected(_myValue);
	}
	
	@Override
	public Boolean value() {
		return _myIsSelected;
	}
	
	@Override
	public void addToComponent(JPanel thePanel, int theY, int theDepth) {
		thePanel.add(_myLabel, 	constraints(0, theY, GridBagConstraints.LINE_END, 	5, 5, 1, 5));
		thePanel.add(_myButton, constraints(1, theY, GridBagConstraints.LINE_START, 5, 4, 1, 5));
	}
}
