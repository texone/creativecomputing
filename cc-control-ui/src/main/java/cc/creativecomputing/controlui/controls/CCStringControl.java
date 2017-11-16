package cc.creativecomputing.controlui.controls;

import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import cc.creativecomputing.control.handles.CCStringPropertyHandle;
import cc.creativecomputing.controlui.CCControlComponent;

public class CCStringControl extends CCValueControl<String, CCStringPropertyHandle>{
	
	private JTextField _myTextField;
	
	private boolean _myTriggerEvent = true;

	public CCStringControl(CCStringPropertyHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		addListener( theValue -> {
			try{
				_myTriggerEvent = false;
				if(!_myHandle.value().equals(_myTextField.getText())){
					_myTextField.setText(_myHandle.value());
				}
			}catch(Exception e){}
			_myTriggerEvent = true;
			
		});
 
        //Create the Button.

        String _myValue = theHandle.value();
        _myTextField = new JTextField(_myValue);
        CCUIStyler.styleTextField(_myTextField, 185);
        _myTextField.setHorizontalAlignment(JTextField.LEFT);
        _myTextField.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent theE) {
				if(!_myTriggerEvent)return;
				_myHandle.value(_myTextField.getText(), !_myHandle.isInEdit());
			}
			
			@Override
			public void insertUpdate(DocumentEvent theE) {
				if(!_myTriggerEvent)return;
				_myHandle.value(_myTextField.getText(), !_myHandle.isInEdit());
			}
			
			@Override
			public void changedUpdate(DocumentEvent theE) {
			}
		});
	}
	
	@Override
	public String value() {
		return _myTextField.getText();
	}
	
	@Override
	public void addToComponent(JPanel thePanel, int theY, int theDepth) {
		thePanel.add(_myLabel, constraints(0, theY, GridBagConstraints.LINE_END,5, 5, 5, 5));
		thePanel.add(_myTextField, constraints(1, theY, 2, GridBagConstraints.LINE_START,5, 6, 5, 5));
	}
}
