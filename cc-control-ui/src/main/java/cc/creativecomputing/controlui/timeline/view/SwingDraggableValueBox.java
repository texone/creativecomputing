package cc.creativecomputing.controlui.timeline.view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;

import cc.creativecomputing.controlui.timeline.view.track.SwingTrackControlView;
import cc.creativecomputing.core.util.CCFormatUtil;


public class SwingDraggableValueBox extends JTextField implements MouseListener, MouseMotionListener{
	
	public static interface ChangeValueListener{
		public void changeValue(double theValue);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int _myStep;
	private int _myMaxSteps;
	private double _myStepSize;
	
	private double _myMin;
	
	private int _myChangeSize = 2;
	private int _myLastY;
	
	private List<ChangeValueListener> _myListener = new ArrayList<ChangeValueListener>();

	public SwingDraggableValueBox(double theValue, double theMin, double theMax, double theStepSize){
		_myMin = theMin;
		_myStep = (int)((theValue - theMin)/theStepSize);
		_myMaxSteps = (int)((theMax - theMin)/theStepSize) + 1;
		_myStepSize = theStepSize;
		setEditable(true);
		setHorizontalAlignment(JTextField.RIGHT);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		setText(value() + "");
	}
	
	public void addListener(ChangeValueListener theListener){
		_myListener.add(theListener);
	}
	
	public void removeListener(ChangeValueListener theListener){
		_myListener.remove(theListener);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int myChange = e.getY() - _myLastY;
		int steps = myChange / _myChangeSize;
		
		_myLastY += steps * _myChangeSize;
		_myStep -= steps;
		
		_myStep = Math.max(_myStep, 0);
		_myStep = Math.min(_myStep, _myMaxSteps);
		
		double myValue = value();
		
		for(ChangeValueListener myListener:_myListener){
			myListener.changeValue(myValue);
		}
		
		setText(CCFormatUtil.nd(myValue, 2));
	}
	
	public void value(double theValue){
		_myStep = (int)((theValue - _myMin)/_myStepSize);
	}
	
	public double value(){
		return _myStep * _myStepSize + _myMin;
	}

	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		_myLastY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}
