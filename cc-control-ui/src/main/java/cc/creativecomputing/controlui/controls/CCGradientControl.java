package cc.creativecomputing.controlui.controls;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.control.handles.CCColorPropertyHandle;
import cc.creativecomputing.control.handles.CCGradientPropertyHandle;
import cc.creativecomputing.control.handles.CCPropertyEditListener;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.control.handles.CCPropertyListener;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.controlui.controls.CCGradientEditor.GradientListener;
import cc.creativecomputing.math.CCColor;

public class CCGradientControl extends CCValueControl<CCGradient, CCGradientPropertyHandle>{
	
	private CCGradientEditor _myGradientEditor = new CCGradientEditor();
	private CCGradient _myLastGradient = new CCGradient();
	
	private CCGradient _myGradient;
	
	private JColorChooser _myColorChooser;
	
	private boolean _myTriggerEvent = true;
	private boolean _myIsInEdit = false;

	static final Dimension SMALL_BUTTON_SIZE = new Dimension(100,15);

	public CCGradientControl(CCGradientPropertyHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		theHandle.events().add(new CCPropertyListener<CCGradient>() {
			
			@Override
			public void onChange(CCGradient theValue) {
//				_myGradient = theValue;
//				_myGradientEditor.gradient(_myGradient);
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
		
		_myGradient = theHandle.value().clone();
 
        //Create the Button.
		_myGradientEditor.gradient(_myGradient);
		_myGradientEditor.events().add(new GradientListener() {
			
			@Override
			public void onChange(CCGradient theGradient) {
				_myHandle.value(theGradient, false);
			}
		});
	}
	
	@Override
	public CCGradient value() {
		return _myGradient;
	}
	
	private JPanel _myPanel;
	
	@Override
	public void addToComponent(JPanel thePanel, int theY, int theDepth) {
		_myPanel = thePanel;
		_myPanel.setBackground(new Color(255,0,0));
		_myGradientEditor.setPreferredSize(new Dimension(300,40));
		JPanel myPanel = new JPanel();
		myPanel.setBackground(new Color(255,0,0));
		thePanel.add(_myGradientEditor,  constraints(0, theY, 3, GridBagConstraints.LINE_START,0, 0, 0, 0));
//		thePanel.add(_myColorPanel, myConstraints);
	}
}
