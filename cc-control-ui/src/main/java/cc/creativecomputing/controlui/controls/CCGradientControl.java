package cc.creativecomputing.controlui.controls;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;

import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.control.handles.CCGradientPropertyHandle;
import cc.creativecomputing.control.handles.CCPropertyEditListener;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.controlui.CCControlComponent;

public class CCGradientControl extends CCValueControl<CCGradient, CCGradientPropertyHandle>{
	
	private CCGradientEditor _myGradientEditor = new CCGradientEditor();
	
	private CCGradient _myGradient;
	
	@SuppressWarnings("unused")
	private boolean _myIsInEdit = false;

	static final Dimension SMALL_BUTTON_SIZE = new Dimension(100,15);

	public CCGradientControl(CCGradientPropertyHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		theHandle.events().add(theValue -> {
			_myGradient = ((CCGradient)theValue).clone();
			_myGradientEditor.gradient(_myGradient);
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
		_myGradientEditor.events().add(theGradient -> {
			_myHandle.value(theGradient, false);
		});
	}
	
	@Override
	public CCGradient value() {
		return _myGradient;
	}
	
	private JPanel _myPanel;
	
	@Override
	public void addToComponent(JPanel thePanel, int theY, int theDepth) {
		thePanel.add(_myLabel,  constraints(0, theY, GridBagConstraints.LINE_END, 5, 5, 1, 5));
		
		_myPanel = thePanel;
		_myGradientEditor.setPreferredSize(new Dimension(202,20));
		_myGradientEditor.setBackground(_myPanel.getBackground());
		JPanel myPanel = new JPanel();
		myPanel.setBackground(new Color(255,0,0));
		thePanel.add(_myGradientEditor,  constraints(1, theY, 2, GridBagConstraints.LINE_START,0, 0, 0, 0));
//		thePanel.add(_myColorPanel, myConstraints);
	}
}
