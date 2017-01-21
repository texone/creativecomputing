package cc.creativecomputing.controlui.controls;

import java.awt.GridBagConstraints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.handles.CCEnvelopeHandle;
import cc.creativecomputing.controlui.CCControlComponent;

public class CCEnvelopeControl extends CCValueControl<CCEnvelope, CCEnvelopeHandle>{

	private JButton _myButton;
	
	private CCEnvelopeEditor _myCurveFrame;

	public CCEnvelopeControl(CCEnvelopeHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		theHandle.events().add(theValue -> {
			_myHandle.value((CCEnvelope)theValue, false);
		});
		
		_myCurveFrame = new CCEnvelopeEditor(theHandle.name());
		_myCurveFrame.setSize(300, 300);
		_myCurveFrame.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent theE) {
//				value().currentCurve(_myEnvelopeCurve);
			}
		});
        
        _myButton = new JButton("edit");
        _myButton.addActionListener(theE -> {
        	_myCurveFrame.track().trackData(value().curve());				
        	_myCurveFrame.setVisible(true);
		});
        CCUIStyler.styleButton(_myButton, 30, 15);
 
	}
	
	@Override
	public void addToComponent(JPanel thePanel, int theY, int theDepth) {
		thePanel.add(_myLabel, 	constraints(0, theY, GridBagConstraints.LINE_END, 	5, 5, 1, 5));
		thePanel.add(_myButton, constraints(1, theY, GridBagConstraints.LINE_START, 5, 4, 1, 5));
	}

	@Override
	public CCEnvelope value() {
		// TODO Auto-generated method stub
		return _myHandle.value();
	}
}
