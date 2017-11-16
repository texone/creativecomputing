package demos;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CCDirectControlUI extends JFrame {

	public CCDirectControlUI() {

		setLayout(null);
		setSize(300, 300);
		setDefaultCloseOperation(CCDirectControlUI.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	public class CCDragPanel extends JPanel {

		private volatile int draggedAtX, draggedAtY;

		public CCDragPanel(String text) {
			// super(text);
			setDoubleBuffered(false);
			// setMargin(new Insets(0, 0, 0, 0));
			setSize(25, 25);
			setPreferredSize(new Dimension(25, 25));
			setBackground(Color.BLACK);

			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					draggedAtX = e.getX();
					draggedAtY = e.getY();
				}
			});

			addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					setLocation(e.getX() - draggedAtX + getLocation().x, e.getY() - draggedAtY + getLocation().y);
				}
			});
		}
	}

	public void addComponent(JComponent theComponent) {
		CCDragPanel myDragPanel = new CCDragPanel("");
		myDragPanel.add(theComponent);
		getContentPane().add(myDragPanel);
	}

	public static void main(String[] args) {
		CCDirectControlUI frame = new CCDirectControlUI();
		frame.addComponent(new JButton("1"));
		frame.addComponent(new JButton("2"));
		frame.addComponent(new JButton("3"));
		frame.setVisible(true);
	}

}