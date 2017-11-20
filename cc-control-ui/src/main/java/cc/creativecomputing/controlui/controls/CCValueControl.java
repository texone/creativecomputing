package cc.creativecomputing.controlui.controls;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.control.handles.CCPropertyListener;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.controlui.CCPropertyPopUp;
import cc.creativecomputing.core.logging.CCLog;

public abstract class CCValueControl<Type, Handle extends CCPropertyHandle<Type>> implements CCControl{
	
	protected Handle _myHandle;
	
	protected JLabel _myLabel;
	
	protected CCPropertyPopUp _myPopUp;
	
	protected CCControlComponent _myControlComponent;

	public CCValueControl(Handle theHandle, CCControlComponent theControlComponent){
		
		_myHandle = theHandle;
		_myControlComponent = theControlComponent;
		
		_myPopUp = new CCPropertyPopUp(theHandle, _myControlComponent);
		
        //Create the label.
		_myLabel = new JLabel(_myHandle.name(), JLabel.LEFT);
		
		_myLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.isAltDown()){
					_myHandle.restoreDefault();
					return;
				}
				if(e.isControlDown()){
					_myHandle.restorePreset();
					return;
				}
				if(e.getButton() == MouseEvent.BUTTON3){
					_myPopUp.show(_myLabel, e.getX(), e.getY());
				}
			}
		});
		
		CCUIStyler.styleLabel(_myLabel);
	}
	
	private CCPropertyListener<Type> _myListener = null;
	
	public void addListener(CCPropertyListener<Type> theListener){
		_myHandle.events().add(_myListener = theListener);
	}
	
	public void dispose() {
		if(_myListener != null)_myHandle.events().remove(_myListener);
	}
	
	
	protected GridBagConstraints constraints(int theX, int theY, int theWidth, int theAnchor, int theTop, int theLeft, int theBottom, int theRight){
		GridBagConstraints myResult = new GridBagConstraints();
		myResult.gridx = theX;
		myResult.gridy = theY;
		myResult.gridwidth = theWidth;
		myResult.insets = new Insets(theTop, theLeft, theBottom, theRight);
		myResult.anchor = theAnchor;
		return myResult;
	}
	
	protected GridBagConstraints constraints(int theX, int theY, int theAnchor, int theTop, int theLeft, int theBottom, int theRight){
		return constraints(theX, theY, 1, theAnchor,  theTop, theLeft, theBottom, theRight);
	}
	
	public abstract Type value();
	
}
