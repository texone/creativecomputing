package cc.creativecomputing.controlui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import cc.creativecomputing.core.util.CCFormatUtil;

public class CCProgressWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4436530082291804538L;
	private JLabel _myLabel;
	private JProgressBar _myProgressBar;

	public CCProgressWindow() {
		super();
		setSize(500, 150);
		_myLabel = new JLabel();
		_myLabel.setText("Count : 0");

		add(BorderLayout.NORTH, _myLabel);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		_myProgressBar = new JProgressBar(0, 500);
		add(BorderLayout.SOUTH, _myProgressBar);
		add(BorderLayout.CENTER, new JLabel("Progress..."));
	}

	public void progress(double theProgress) {
		if (!isVisible())
			setVisible(true);
		
		try {
			_myLabel.setText("Progress : " + CCFormatUtil.ndc(theProgress * 100, 2) + " %");
			_myProgressBar.setValue((int) (theProgress * 500));
		} catch (Exception e) {

		}
	}

	public static void main(String[] args) {
		CCProgressWindow myWindow = new CCProgressWindow();

		Thread t = new Thread(new Runnable() {
			public void run() {
				myWindow.setVisible(true);
			}
		});
		t.start();
		for (int i = 0; i <= 500; i++) {
			myWindow.progress(i / 500f);

			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
