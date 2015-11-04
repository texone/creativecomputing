package cc.creativecomputing.controlui.controls;

import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import cc.creativecomputing.control.handles.CCPropertyEditListener;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.control.handles.CCPropertyListener;
import cc.creativecomputing.control.handles.CCStringPropertyHandle;
import cc.creativecomputing.controlui.CCControlComponent;

public class CCStringControl extends CCValueControl<String, CCStringPropertyHandle>{
	
	private JTextField _myTextField;
	
	private boolean _myTriggerEvent = true;
	
	private boolean _myIsInEdit = false;

	public CCStringControl(CCStringPropertyHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		theHandle.events().add(new CCPropertyListener<String>() {
			
			@Override
			public void onChange(String theValue) {
				try{
					_myTriggerEvent = false;
					if(!_myHandle.value().equals(_myTextField.getText())){
						_myTextField.setText(_myHandle.value());
					}
				}catch(Exception e){
					
				}
				_myTriggerEvent = true;
			}
		});
		
		theHandle.editEvents().add(new CCPropertyEditListener() {
			
			@Override
			public void endEdit(CCPropertyHandle<?> theProperty) {
				_myIsInEdit = false;
			}
			
			@Override
			public void beginEdit(CCPropertyHandle<?> theProperty) {
				_myIsInEdit = true;
			}
		});
 
        //Create the Button.

        String _myValue = theHandle.value();
        _myTextField = new JTextField(_myValue);
        CCUIStyler.styleTextField(_myTextField, 100);
        _myTextField.setHorizontalAlignment(JTextField.LEFT);
        _myTextField.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent theE) {
				if(!_myTriggerEvent)return;
				_myHandle.value(_myTextField.getText(), !_myIsInEdit);
			}
			
			@Override
			public void insertUpdate(DocumentEvent theE) {
				if(!_myTriggerEvent)return;
				_myHandle.value(_myTextField.getText(), !_myIsInEdit);
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
		thePanel.add(_myTextField, constraints(1, theY, GridBagConstraints.LINE_START,5, 15, 5, 5));
	}
}
