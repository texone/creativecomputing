/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.controlui.timeline.view;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import cc.creativecomputing.core.logging.CCLog;

public class CCTextInputDialog extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 823514954709391337L;

	public interface CCTextChangeListener{
		void onChangeText(String theText);
	}
	
	private JTextField _myTextField;

	private JOptionPane _myOptionPane;

	private String _myCommandString;
	private String _myCancelString = "Cancel";

	/** Creates the reusable dialog. */
	public CCTextInputDialog(String theTitle, String theMessage, String theCommand, CCTextChangeListener theEvent) {
		super();

		setTitle(theTitle);
		_myCommandString = theCommand;


		_myTextField = new JTextField(10);

		// Create an array of the text and components to be displayed.
		Object[] array = { theMessage, _myTextField };

		// Create an array specifying the number of dialog buttons
		// and their text.
		Object[] options = { _myCommandString, _myCancelString };

		// Create the JOptionPane.
		_myOptionPane = new JOptionPane(
			array, 
			JOptionPane.QUESTION_MESSAGE,
			JOptionPane.YES_NO_OPTION, 
			null, 
			options, 
			options[0]
		);

		// Make this dialog display it.
		setContentPane(_myOptionPane);

		// Handle window closing correctly.
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				/*
				 * Instead of directly closing the window, we're going to
				 * change the JOptionPane's value property.
				 */
				_myOptionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
			}
		});

		// Ensure the text field always gets the first focus.
		addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent ce) {
				_myTextField.requestFocusInWindow();
			}
		});

		// Register an event handler that puts the text into the option
		// pane.
		_myTextField.addActionListener(e -> {
			_myOptionPane.setValue(_myCommandString);
		});

		// Register an event handler that reacts to option pane state
		// changes.
		_myOptionPane.addPropertyChangeListener(e -> {
			if(!isVisible())return;
			if(e.getSource() != _myOptionPane)return;
			
			String prop = e.getPropertyName();
			CCLog.info(prop);
			if(!(JOptionPane.VALUE_PROPERTY.equals(prop) || JOptionPane.INPUT_VALUE_PROPERTY.equals(prop)))return;

			Object myValue = _myOptionPane.getValue();

			if (myValue == JOptionPane.UNINITIALIZED_VALUE) return;

			// Reset the JOptionPane's value.
			// If you don't do this, then if the user
			// presses the same button next time, no
			// property change event will be fired.
			_myOptionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

			if (myValue.equals(_myCommandString)) {
				theEvent.onChangeText(_myTextField.getText());		
			} 
			clearAndHide();
			
		});
	}
	
	
	public CCTextInputDialog location(int theX, int theY) {
		setLocation(theX, theY);
		return this;
	}
	
	public CCTextInputDialog size(int theWidth, int theHeight) {
		setSize( 300, 200);
		return this;
	}
	
	public void open() {
		setVisible(true);
	}
	

	/** This method clears the dialog and hides it. */
	public void clearAndHide() {
		_myTextField.setText(null);
		setVisible(false);
	}
}
