package cc.creativecomputing.controlui.timeline.view.transport;

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

import cc.creativecomputing.control.timeline.point.MarkerPoint;


class SwingRulerMarkerDialog extends JDialog implements ActionListener, PropertyChangeListener {
		/**
	 * 
	 */
	private static final long serialVersionUID = 139787124989150690L;
	private String typedText = null;
		private JTextField _myTextField;

		private JOptionPane optionPane;

		private String btnString1 = "Add";
		private String btnString2 = "Cancel";
		
		private MarkerPoint _myMarkerPoint;
		
		private SwingRulerView _myRulerView;

		/** Creates the reusable dialog. */
		public SwingRulerMarkerDialog(SwingRulerView theRulerView, String aWord) {
			super();

			_myRulerView = theRulerView;
			setTitle("Edit Marker");

			_myTextField = new JTextField(10);

			// Create an array of the text and components to be displayed.
			String msgString1 = "Set the name of the marker.";
			Object[] array = { msgString1, _myTextField };

			// Create an array specifying the number of dialog buttons
			// and their text.
			Object[] options = { btnString1, btnString2 };

			// Create the JOptionPane.
			optionPane = new JOptionPane(
				array, 
				JOptionPane.QUESTION_MESSAGE,
				JOptionPane.YES_NO_OPTION, 
				null, 
				options, 
				options[0]
			);

			// Make this dialog display it.
			setContentPane(optionPane);

			// Handle window closing correctly.
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					/*
					 * Instead of directly closing the window, we're going to
					 * change the JOptionPane's value property.
					 */
					optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
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
			optionPane.addPropertyChangeListener(this);
		}

		/** This method handles events for the text field. */
		public void actionPerformed(ActionEvent e) {
			optionPane.setValue(btnString1);
		}

		/** This method reacts to state changes in the option pane. */
		public void propertyChange(PropertyChangeEvent e) {
			String prop = e.getPropertyName();

			if (isVisible()
					&& (e.getSource() == optionPane)
					&& (JOptionPane.VALUE_PROPERTY.equals(prop) || JOptionPane.INPUT_VALUE_PROPERTY
							.equals(prop))) {
				Object value = optionPane.getValue();

				if (value == JOptionPane.UNINITIALIZED_VALUE) {
					// ignore reset
					return;
				}

				// Reset the JOptionPane's value.
				// If you don't do this, then if the user
				// presses the same button next time, no
				// property change event will be fired.
				optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

				if (btnString1.equals(value)) {
					typedText = _myTextField.getText();
					if(_myMarkerPoint != null) {
						_myMarkerPoint.name(typedText);
						_myRulerView.render();
					}
//					_myTransportController.addMarkerFromMouse(typedText, _myMouseX);
						// we're done; clear and dismiss the dialog
					clearAndHide();
					
				} else { 
					typedText = null;
					clearAndHide();
				}
			}
		}
		
		public void marker(MarkerPoint theMarkerPoint) {
			_myMarkerPoint = theMarkerPoint;
			_myTextField.setText(_myMarkerPoint.name());
		}

		/** This method clears the dialog and hides it. */
		public void clearAndHide() {
			_myTextField.setText(null);
			setVisible(false);
		}
	}