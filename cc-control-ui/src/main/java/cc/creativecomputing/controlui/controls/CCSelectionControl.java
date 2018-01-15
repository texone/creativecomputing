package cc.creativecomputing.controlui.controls;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import cc.creativecomputing.control.CCSelection;
import cc.creativecomputing.control.CCSelection.CCSelectionListener;
import cc.creativecomputing.control.handles.CCSelectionPropertyHandle;
import cc.creativecomputing.controlui.CCControlComponent;

public class CCSelectionControl extends CCValueControl<CCSelection, CCSelectionPropertyHandle>{
	
	private JComboBox<String> _mySelection;
	
	private CCSelection _myValue;
	
	private boolean _myTriggerEvent = true;
	static final Dimension SMALL_BUTTON_SIZE = new Dimension(100,15);
	
	private CCSelectionListener _mySelectionListener;

	public CCSelectionControl(CCSelectionPropertyHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		_myHandle.value().events().add(_mySelectionListener = new CCSelectionListener() {
			
			@Override
			public void onChangeValues(CCSelection theSelection) {
				_mySelection.removeAllItems();
				for(String myEnum:_myValue.values()){
		        	_mySelection.addItem(myEnum);
		        }
			}
			
			@Override
			public void onChange(String theValue) {
				_myTriggerEvent = false;
				 _mySelection.setSelectedItem(theValue);
				 _myTriggerEvent = true;
			}
		});
 
		addListener(theValue -> {
			_myValue = theValue;
			_myTriggerEvent = false;
			_mySelection.setSelectedItem(_myHandle.value());
			_myTriggerEvent = true;
		});

        _myValue = theHandle.value();
        
        _mySelection = new JComboBox<String>();
        CCUIStyler.styleCombo(_mySelection);
        for(String myEnum:_myValue.values()){
        	_mySelection.addItem(myEnum);
        }
        _mySelection.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent theE) {
				if(!_myTriggerEvent)return;
				if(_mySelection.getSelectedItem() == null)return;
				_myHandle.value().value((String)_mySelection.getSelectedItem());
				
			}
		});
        _mySelection.setSelectedItem(_myHandle.value());
	}
	
	@Override
	public void dispose() {
		super.dispose();
		_myHandle.value().events().remove(_mySelectionListener);
	}
	
	@Override
	public CCSelection value() {
		return _myValue;
	}
	
	
	
	@Override
	public void addToComponent(JPanel thePanel, int theY, int theDepth) {
		thePanel.add(_myLabel, constraints(0,theY, GridBagConstraints.LINE_END, 	15,  5, 1, 5));
		thePanel.add(_mySelection, constraints(1,theY, GridBagConstraints.LINE_START,	15, 15, 1, 5));
	}
}
