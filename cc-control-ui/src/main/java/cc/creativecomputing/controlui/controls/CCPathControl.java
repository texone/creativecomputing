package cc.creativecomputing.controlui.controls;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cc.creativecomputing.control.handles.CCPathHandle;
import cc.creativecomputing.control.handles.CCPropertyEditListener;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.control.handles.CCPropertyListener;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.io.CCNIOUtil;

public class CCPathControl extends CCValueControl<Path, CCPathHandle>{
	
	private JTextField _myTextField;
	
	private boolean _myIsInEdit = false;
	
	private JButton _myOpenButton;

	public CCPathControl(CCPathHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		theHandle.events().add(new CCPropertyListener<Path>() {
			
			@Override
			public void onChange(Path theValue) {
				try{
					if(_myHandle.value() == null)_myTextField.setText("");
					else _myTextField.setText(_myHandle.value().toString());
				}catch(Exception e){
					
				}
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
 

        String _myValue = theHandle.path() == null ? "" : theHandle.path().toString();
        _myTextField = new JTextField(_myValue);
        CCUIStyler.styleTextField(_myTextField, 100);
        
        _myOpenButton = new JButton("edit");
        _myOpenButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				Path myPath = CCNIOUtil.selectInput("");
				if(myPath == null)return;
				_myHandle.value(myPath, !_myIsInEdit);
				_myTextField.setText(myPath.toString());
			}
		});
        CCUIStyler.styleButton(_myOpenButton, 30, 15);
	}
	
	
	
	@Override
	public void addToComponent(JPanel thePanel, int theY, int theDepth) {
		thePanel.add(_myLabel, constraints(0, theY, GridBagConstraints.LINE_END,5, 5, 5, 5));
		thePanel.add(_myTextField, constraints(1, theY, GridBagConstraints.LINE_START,5, 15, 5, 5));
		thePanel.add(_myOpenButton, constraints(2, theY, GridBagConstraints.LINE_START,5, 15, 5, 5));
	}



	@Override
	public Path value() {
		return _myHandle.value();
	}
}
