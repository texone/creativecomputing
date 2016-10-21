package cc.creativecomputing.controlui.controls;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.handles.CCEnvelopeHandle;
import cc.creativecomputing.control.handles.CCPropertyListener;
import cc.creativecomputing.control.handles.CCSplineHandle;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.math.spline.CCSpline;

public class CCSplineControl extends CCValueControl<CCSpline, CCSplineHandle>{

	private JButton _myButton;
	
	private CCSplineEditor _myCurveFrame;

	public CCSplineControl(CCSplineHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		theHandle.events().add(theValue -> {
			_myHandle.value((CCSpline)theValue, false);
		});
		
		_myCurveFrame = new CCSplineEditor(theHandle.name());
		_myCurveFrame.setSize(300, 100);
		_myCurveFrame.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent theE) {
//				value().currentCurve(_myEnvelopeCurve);
			}
		});
        
        _myButton = new JButton("edit");
        _myButton.addActionListener(theE -> {
        	_myCurveFrame.spline(value());				
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
	public CCSpline value() {
		// TODO Auto-generated method stub
		return _myHandle.value();
	}
}
