package cc.creativecomputing.controlui.controls;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
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
	        
	        _myDialog = JColorChooser.createDialog(
	        	theFrame, 
	        	"", 
	        	false,
	            theColorChooser, 
	            null, 
	            theE -> {setColor(_myLastColor);}
	        );
	    }

	    public void open() {
	    	try{
	    		_myLastColor = _myGradient.get(_mySelectedPoint).color().toAWTColor();
	    		_myColorChooser.setColor(_myLastColor);
	        }catch(Exception e){
	        	
	        }
	        _myDialog.setVisible(true);

	        setEnabled(false);
	        CCGradientEditor.this.setEnabled(true);
	    }
	}

    private int _mySelectedPoint;

	private CCGradient _myGradient = new CCGradient();
	/** The polygon used for the markers */
	private Polygon poly = new Polygon();

	/** The x position of the gradient bar */
	private int _myX;
	/** The y position of the gradient bar */
	private int _myY;
	/** The width of the gradient bar */
	private int _myWidth;
	/** The height of the gradient bar */
	private int _myHeight;
	
	public interface GradientListener{
		void onChange(CCGradient theGradient);
	}

	private CCListenerManager<GradientListener> _myEvents = CCListenerManager.create(GradientListener.class);
	
	private JColorChooser _myColorChooser;

	private ShowColorChooserAction _myAction;

	/**
	 * Create a new editor for gradients
	 *
	 */
	public CCGradientEditor() {
		_myX = 5;
		_myY = 5;
		_myHeight = 5;

		_myColorChooser = CCUIStyler.createColorChooser(new Color(0,0,0));
		_myColorChooser.getSelectionModel().addChangeListener(theE ->{
			Color myColor = _myColorChooser.getColor();
			setColor(myColor);
		});
		
		_myAction = new ShowColorChooserAction(this, _myColorChooser);

		poly.addPoint(0, 0);
		poly.addPoint(4, 8);
		poly.addPoint(-4, 8);

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				selectPoint(e);
				repaint(0);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() != 1) return;
				
				if(_mySelectedPoint != -1){
					if(e.isShiftDown())delPoint();
					else editPoint();
				}else{
					double myPos = CCMath.norm(e.getX(), _myX, _myX + _myWidth);
					addPoint(myPos);
					movePoint(e);
				}
			}
		});

		addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				movePoint(e);
				repaint(0);
			}

			public void mouseMoved(MouseEvent e) {}
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

	public CCListenerManager<GradientListener> events() {
		return _myEvents;
	}

	/**
	 * Fire an update to all _myGradienteners
	 */
	private void fireUpdate() {
		_myEvents.proxy().onChange(_myGradient);
	}

	private boolean checkPoint(MouseEvent theE, CCGradientPoint pt) {
		int dx = (int) Math.abs((_myX + (_myWidth * pt.position())) - theE.getX());
		int dy = Math.abs((_myY + _myHeight + 7) - theE.getY());

        return (dx < 5) && (dy < 7);

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
	private void selectPoint(MouseEvent theE) {
		if (!isEnabled()) {
			_mySelectedPoint = -1;
			return;
		}
		if(_myGradient.size() == 0){
			_mySelectedPoint = -1;
			return;
		}

		for (int i = 1; i < _myGradient.size() - 1; i++) {
			if (checkPoint(theE,  _myGradient.get(i))) {
				_mySelectedPoint =  i;
				return;
			}
		}
		if (checkPoint(theE,  _myGradient.get(0))) {
			_mySelectedPoint =  0;
			return;
		}
		if (checkPoint(theE,  _myGradient.get(_myGradient.size() - 1))) {
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
	private void movePoint(MouseEvent theE) {
		if (!isEnabled()) {
			return;
		}

		if (_mySelectedPoint == -1) {
			return;
		}

		float newPos = (theE.getX() - _myX) / (float) _myWidth;
		newPos = CCMath.saturate(newPos);

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
		_myWidth = getWidth() - 25;

		for(int i = 0; i <= _myWidth;i++){
			double blend = (double)i / _myWidth;
			g.setColor(_myGradient.interpolate(blend).toAWTColor());
			g.drawLine(i + _myX, _myY, i + _myX, _myY + _myHeight - 1);
		}

		g.setColor(Color.DARK_GRAY);
		g.drawLine(_myX, _myY - 3, _myX, _myY);
		g.drawLine(_myX + _myWidth/2, _myY - 3, _myX + _myWidth/2, _myY);
		g.drawLine(_myX + _myWidth, _myY - 3, _myX + _myWidth, _myY);
		
		g.drawLine(_myX + _myWidth/4, _myY - 2, _myX + _myWidth/4, _myY);
		g.drawLine(_myX + _myWidth/4 * 3, _myY - 2, _myX + _myWidth/4 * 3, _myY);

		g.drawLine(_myX + _myWidth/8, _myY - 1, _myX + _myWidth/8, _myY);
		g.drawLine(_myX + _myWidth/8 * 3, _myY - 1, _myX + _myWidth/8 * 3, _myY);
		g.drawLine(_myX + _myWidth/8 * 5, _myY - 1, _myX + _myWidth/8 * 5, _myY);
		g.drawLine(_myX + _myWidth/8 * 7, _myY - 1, _myX + _myWidth/8 * 7, _myY);

		g.setColor(Color.DARK_GRAY);
		for (int i = 0; i < _myGradient.size(); i++) {
			CCGradientPoint pt =  _myGradient.get(i);
			g.translate(_myX + (_myWidth * pt.position()), _myY + _myHeight);
			g.setColor(pt.color().toAWTColor());
			g.fillPolygon(poly);
			g.setColor(Color.black);
			g.drawPolygon(poly);

			if (i == _mySelectedPoint) {
				g.drawLine(-4, 10, 4, 10);
			}
			g.translate(-_myX - (_myWidth * pt.position()), -_myY - _myHeight);
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