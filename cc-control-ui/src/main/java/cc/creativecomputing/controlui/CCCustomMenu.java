package cc.creativecomputing.controlui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class CCCustomMenu extends JMenu {

	public CCCustomMenu(final String theName) {
		super(theName);
	}

	public JMenuItem addCommand(final String theCommand, final ActionListener theActionListener, int theMnemonicKey, int theAccelerator) {
		JMenuItem myMenuItem = new JMenuItem(theCommand);
		myMenuItem.addActionListener(theActionListener);
		if(theAccelerator >= 0)myMenuItem.setAccelerator(KeyStroke.getKeyStroke(theAccelerator, ActionEvent.META_MASK));
		if(theMnemonicKey >= 0)myMenuItem.setMnemonic(theMnemonicKey);
		add(myMenuItem);
		
		revalidate();
		repaint();
		
		return myMenuItem;
	}
	public JCheckBoxMenuItem addCheckBoxCommand(final String theCommand, final ActionListener theActionListener, int theMnemonicKey, int theAccelerator, boolean theDefault) {
		JCheckBoxMenuItem myMenuItem = new JCheckBoxMenuItem(theCommand);
		myMenuItem.setSelected(theDefault);
		myMenuItem.addActionListener(theActionListener);
		myMenuItem.setAccelerator(KeyStroke.getKeyStroke(theAccelerator, ActionEvent.META_MASK));
		myMenuItem.setMnemonic(theMnemonicKey);
		add(myMenuItem);
		
		revalidate();
		repaint();
		
		return myMenuItem;
	}
	
}
