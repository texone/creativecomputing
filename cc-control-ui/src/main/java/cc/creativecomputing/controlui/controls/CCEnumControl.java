package cc.creativecomputing.controlui.controls;

import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import cc.creativecomputing.control.handles.CCEnumPropertyHandle;
import cc.creativecomputing.controlui.CCControlComponent;

public class CCEnumControl extends CCValueControl<Enum<?>, CCEnumPropertyHandle>{
	
	private JComboBox<Enum<?>> _myEnums;
	
	private Enum<?> _myValue;
	
	private boolean _myTriggerEvent = true;
	static final Dimension SMALL_BUTTON_SIZE = new Dimension(100,15);

	public CCEnumControl(CCEnumPropertyHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
 
		addListener( theValue -> {
			_myValue = theValue;
			_myTriggerEvent = false;
			_myEnums.setSelectedItem(_myHandle.value());
			_myTriggerEvent = true;
		});

        _myValue = theHandle.value();
        
        _myEnums = new JComboBox<Enum<?>>();
        CCUIStyler.styleCombo(_myEnums);
        for(Enum<?> myEnum:_myHandle.enumConstants()){
        	_myEnums.addItem(myEnum);
        }
        _myEnums.addItemListener(the -> {
        	if(!_myTriggerEvent)return;
        	if(_myEnums.getSelectedItem() == null)return;
        	_myHandle.value((Enum<?>)_myEnums.getSelectedItem(), !_myHandle.isInEdit());
		});
        _myEnums.setSelectedItem(_myHandle.value());
	}
	
	@Override
	public Enum<?> value() {
		return _myValue;
	}
	
	@Override
	public void addToComponent(JPanel thePanel, int theY, int theDepth) {
		thePanel.add(_myLabel, constraints(0,theY, GridBagConstraints.LINE_END, 	5, 5, 1, 5));
		thePanel.add(_myEnums, constraints(1,theY, GridBagConstraints.LINE_START,	5, 0, 1, 5));
	}
}
