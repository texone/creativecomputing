package cc.creativecomputing.controlui.timeline.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;

public class CCTextInputDialog extends JDialog implements ActionListener, PropertyChangeListener {
	
	public static interface CCTextChangeListener{
		public void onChangeText(String theText);
	}
	
	private JTextField _myTextField;

	private JOptionPane _myOptionPane;

	private String _myCommandString;
	private String _myCancelString = "Cancel";
	
	private CCListenerManager<CCTextInputDialog.CCTextChangeListener> _myEvents = CCListenerManager.create(CCTextInputDialog.CCTextChangeListener.class);

	/** Creates the reusable dialog. */
	public CCTextInputDialog(String theTitle, String theCommand) {
		super();

		setTitle(theTitle);
		_myCommandString = theCommand;


		_myTextField = new JTextField(10);

		// Create an array of the text and components to be displayed.
		String msgString1 = "Specify the time to insert in seconds.";
		Object[] array = { msgString1, _myTextField };

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
		_myTextField.addActionListener(this);

		// Register an event handler that reacts to option pane state
		// changes.
		_myOptionPane.addPropertyChangeListener(this);
	}
	
	public CCListenerManager<CCTextInputDialog.CCTextChangeListener> events(){
		return _myEvents;
	}

	/** This method handles events for the text field. */
	public void actionPerformed(ActionEvent e) {
		_myOptionPane.setValue(_myCommandString);
	}

	/** This method reacts to state changes in the option pane. */
	public void propertyChange(PropertyChangeEvent e) {
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
			_myEvents.proxy().onChangeText(_myTextField.getText());		
		} 
		clearAndHide();
	}

	/** This method clears the dialog and hides it. */
	public void clearAndHide() {
		CCLog.info("clear and hide");
		_myTextField.setText(null);
		setVisible(false);
	}
}