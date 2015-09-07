package cc.creativecomputing.controlui.timeline.view.track;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SwingEventTrackDialog extends JDialog implements ActionListener, PropertyChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 139787124989150690L;
	private JTextField _myTextField;


	/** Creates the reusable dialog. */
	public SwingEventTrackDialog() {
		super();

		setTitle("Edit Event");

		
		// Make this dialog display it.
		setContentPane(new JPanel());

		// Handle window closing correctly.
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				
			}
		});

		// Ensure the text field always gets the first focus.
		addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent ce) {
			}
		});

	}

	


	/** This method clears the dialog and hides it. */
	public void clearAndHide() {
		_myTextField.setText(null);
		setVisible(false);
	}




	@Override
	public void propertyChange(PropertyChangeEvent theEvt) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void actionPerformed(ActionEvent theE) {
		// TODO Auto-generated method stub
		
	}
}