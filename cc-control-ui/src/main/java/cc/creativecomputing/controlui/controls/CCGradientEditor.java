package cc.creativecomputing.controlui.controls;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.control.CCGradientPoint;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public class CCGradientEditor extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8492868055099589466L;
	
	private class ShowColorChooserAction  {
		
		private JColorChooser _myColorChooser;
	    private JDialog _myDialog;
	    private Color _myLastColor = new Color(0,0,0,0);

	    ShowColorChooserAction(Component theFrame, JColorChooser theColorChooser) {
	        _myColorChooser = theColorChooser;
	        

	        // Choose whether dialog is modal or modeless
	        boolean modal = false;

	        // Create the dialog that contains the chooser
	        _myDialog = JColorChooser.createDialog(
	        	theFrame, 
	        	"", 
	        	modal,
	            theColorChooser, 
	            null, 
	            new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent theE) {
						setColor(_myLastColor);
					}
				}
	        );
	    }

	    public void open() {
	    	try{
		    	_myLastColor = _myGradient.get(_mySelectedPoint).color().toAWTColor();
		        _myColorChooser.setColor(_myLastColor);
	        }catch(Exception e){
	        	
	        }
	        // Show dialog
	        _myDialog.setVisible(true);

	        // Disable the action; to enable the action when the dialog is closed, see
	        // Listening for OK and Cancel Events in a JColorChooser Dialog
	        setEnabled(false);
	        CCGradientEditor.this.setEnabled(true);
	    }
	};

	private int _mySelectedPoint;

	private CCGradient _myGradient = new CCGradient();
	/** The polygon used for the markers */
	private Polygon poly = new Polygon();

	/** The x position of the gradient bar */
	private int x;
	/** The y position of the gradient bar */
	private int y;
	/** The width of the gradient bar */
	private int width;
	/** The height of the gradient bar */
	private int barHeight;
	
	public static interface GradientListener{
		public void onChange(CCGradient theGradient);
	}

	private CCListenerManager<GradientListener> _myEvents = CCListenerManager.create(GradientListener.class);
	
	private JColorChooser _myColorChooser;

	private ShowColorChooserAction _myAction;

	/**
	 * Create a new editor for gradients
	 *
	 */
	public CCGradientEditor() {
//		setLayout(null);

		x = 5;
		y = 5;
		barHeight = 5;

		_myColorChooser = CCUIStyler.createColorChooser(new Color(0,0,0));
		_myColorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent theE) {
				Color myColor = _myColorChooser.getColor();
				setColor(myColor);
			}
		});
		
		_myAction = new ShowColorChooserAction(this, _myColorChooser);

		poly.addPoint(0, 0);
		poly.addPoint(4, 8);
		poly.addPoint(-4, 8);

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				selectPoint(e.getX(), e.getY());
				repaint(0);
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					if(_mySelectedPoint != -1){
						if(e.isShiftDown())delPoint();
						else editPoint();
					}else{
						System.out.println("ADD POINT");
						double myPos = CCMath.norm(e.getX(), x, x + width);
						addPoint(myPos);
						movePoint(e.getX(), e.getY());
					}
				}
			}
		});

		addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				movePoint(e.getX(), e.getY());
				repaint(0);
			}

			public void mouseMoved(MouseEvent e) {
			}
		});
	}
	
	public void gradient(CCGradient theGradient){
		_myGradient = theGradient;
		repaint();
	}

	/**
	 * @see javax.swing.JComponent#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		Component[] components = getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(enabled);
		}
	}

	/**
	 * Add a _myGradientener that will be notified on change of this editor
	 * 
	 * @param _myGradientener The _myGradientener to be notified on change of
	 *            this editor
	 */
	public CCListenerManager<GradientListener> events() {
		return _myEvents;
	}

	/**
	 * Fire an update to all _myGradienteners
	 */
	private void fireUpdate() {
		_myEvents.proxy().onChange(_myGradient);
	}

	private boolean checkPoint(int mx, int my, CCGradientPoint pt) {
		int dx = (int) Math.abs((x + (width * pt.position())) - mx);
		int dy = Math.abs((y + barHeight + 7) - my);

		if ((dx < 5) && (dy < 7)) {
			return true;
		}

		return false;
	}

	/**
	 * Add a new control point
	 */
	private void addPoint(double thePosition) {
		CCGradientPoint point = new CCGradientPoint(thePosition, CCColor.WHITE.clone());
		_myGradient.add(point);
		_mySelectedPoint = _myGradient.indexOf(point);
		repaint(0);

		fireUpdate();
	}
	

	/**
	 * Edit the currently selected control point
	 *
	 */
	private void editPoint() {
		if (_mySelectedPoint == -1) {
			return;
		}
		_myColorChooser.setColor(_myGradient.get(_mySelectedPoint).color().toAWTColor());
		_myAction.open();
	}
	
	private void setColor(Color theColor){
		if(theColor == null)return;
		_myGradient.get(_mySelectedPoint).color().set(theColor);
		repaint(0);
		fireUpdate();
	}

	/**
	 * Select the control point at the specified mouse coordinate
	 * 
	 * @param mx The mouse x coordinate
	 * @param my The mouse y coordinate
	 */
	private void selectPoint(int mx, int my) {
		if (!isEnabled()) {
			_mySelectedPoint = -1;
			return;
		}
		if(_myGradient.size() == 0){
			_mySelectedPoint = -1;
			return;
		}

		for (int i = 1; i < _myGradient.size() - 1; i++) {
			if (checkPoint(mx, my,  _myGradient.get(i))) {
				_mySelectedPoint =  i;
				return;
			}
		}
		if (checkPoint(mx, my,  _myGradient.get(0))) {
			_mySelectedPoint =  0;
			return;
		}
		if (checkPoint(mx, my,  _myGradient.get(_myGradient.size() - 1))) {
			_mySelectedPoint =  _myGradient.size() - 1;
			return;
		}

		_mySelectedPoint = -1;
	}

	/**
	 * Delete the currently selected point
	 */
	private void delPoint() {
		if (!isEnabled()) {
			return;
		}

		if (_mySelectedPoint == -1) {
			return;
		}
		if (_myGradient.indexOf(_mySelectedPoint) == 0) {
			return;
		}
		if (_myGradient.indexOf(_mySelectedPoint) == _myGradient.size() - 1) {
			return;
		}

		_myGradient.remove(_mySelectedPoint);
		repaint(0);
		fireUpdate();
	}

	/**
	 * Move the current point to the specified mouse location
	 * 
	 * @param mx The x coordinate of the mouse
	 * @param my The y coordinate of teh mouse
	 */
	private void movePoint(int mx, int my) {
		if (!isEnabled()) {
			return;
		}

		if (_mySelectedPoint == -1) {
			return;
		}

		float newPos = (mx - x) / (float) width;
		newPos = Math.min(1, newPos);
		newPos = Math.max(0, newPos);

		_myGradient.get(_mySelectedPoint).position(newPos);
		Collections.sort(_myGradient);
		fireUpdate();
	}

	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g1d) {
		setBackground(getParent().getBackground());
		super.paintComponent(g1d);

		Graphics2D g = (Graphics2D) g1d;
		width = getWidth() - 30;

		for(int i = 0; i <= width;i++){
			double blend = (double)i / width;
			g.setColor(_myGradient.color(blend).toAWTColor());
			g.drawLine(i + x, y, i + x, y + barHeight - 1);
		}

		g.setColor(Color.black);
		g.drawRect(x, y, width, barHeight - 1);

		for (int i = 0; i < _myGradient.size(); i++) {
			CCGradientPoint pt =  _myGradient.get(i);
			g.translate(x + (width * pt.position()), y + barHeight);
			g.setColor(pt.color().toAWTColor());
			g.fillPolygon(poly);
			g.setColor(Color.black);
			g.drawPolygon(poly);

			if (i == _mySelectedPoint) {
				g.drawLine(-4, 10, 4, 10);
			}
			g.translate(-x - (width * pt.position()), -y - barHeight);
		}
	}

	/**
	 * Simple test case for the gradient painter
	 * 
	 * @param argv The arguments supplied at the command line
	 */
	public static void main(String[] argv) {
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		panel.setLayout(null);
		frame.setContentPane(panel);

		CCGradientEditor editor = new CCGradientEditor();
		editor.setBounds(0, 0, 270, 100);
		panel.add(editor);
		frame.setSize(300, 200);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		frame.setVisible(true);
	}
}