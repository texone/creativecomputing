package cc.creativecomputing.controlui.controls;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.controlui.PropertyPopUp;

public abstract class CCValueControl<Type, Handle extends CCPropertyHandle<Type>> implements CCControl{
	
	protected Handle _myHandle;
	
	protected JLabel _myLabel;
	
	protected PropertyPopUp _myPopUp;
	
	protected CCControlComponent _myControlComponent;

	public CCValueControl(Handle theHandle, CCControlComponent theControlComponent){
		
		_myHandle = theHandle;
		_myControlComponent = theControlComponent;
		
		_myPopUp = new PropertyPopUp(theHandle, _myControlComponent);
		
        //Create the label.
		_myLabel = new JLabel(_myHandle.name(), JLabel.LEFT);
		
		_myLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3){
					_myPopUp.show(_myLabel, e.getX(), e.getY());
				}
			}
		});
		
		CCUIStyler.styleLabel(_myLabel);
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
