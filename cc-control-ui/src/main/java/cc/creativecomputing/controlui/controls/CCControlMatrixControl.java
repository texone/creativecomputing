package cc.creativecomputing.controlui.controls;

import java.awt.GridBagConstraints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import cc.creativecomputing.control.CCControlMatrix;
import cc.creativecomputing.control.handles.CCControlMatrixHandle;
import cc.creativecomputing.controlui.CCControlComponent;

public class CCControlMatrixControl extends CCValueControl<CCControlMatrix, CCControlMatrixHandle>{

	private JButton _myButton;
	
	private CCControlMatrixEditor _myMatrixFrame;

	public CCControlMatrixControl(CCControlMatrixHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		addListener(theValue -> {
			_myHandle.value(theValue, false);
//			_myMatrixFrame.track().trackData(value().curve());
			_myMatrixFrame.render();
			_myMatrixFrame.repaint();
		});
		
		_myMatrixFrame = new CCControlMatrixEditor(theHandle.path().toString(), theHandle.value());
		_myMatrixFrame.setSize(300, 300);
		_myMatrixFrame.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent theE) {
			}
			
			@Override
			public void windowOpened(WindowEvent e) {
				_myMatrixFrame.render();
			}
		});
        
        _myButton = new JButton("edit");
        _myButton.addActionListener(theE -> {
//        	_myMatrixFrame.track().trackData(value().curve());				
        	_myMatrixFrame.setVisible(true);
		});
        CCUIStyler.styleButton(_myButton, 30, 15);
 
	}
	
	@Override
	public void addToComponent(JPanel thePanel, int theY, int theDepth) {
		thePanel.add(_myLabel, 	constraints(0, theY, GridBagConstraints.LINE_END, 	5, 5, 1, 5));
		thePanel.add(_myButton, constraints(1, theY, GridBagConstraints.LINE_START, 5, 4, 1, 5));
	}

	@Override
	public CCControlMatrix value() {
		return _myHandle.value();
	}
}
