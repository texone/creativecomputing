package cc.creativecomputing.controlui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JTextField;

import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.math.CCMath;


public class CCSwingDraggableValueBox extends JTextField implements MouseListener, MouseMotionListener{
	
	public static interface CCChangeValueBoxListener{
		public void changeValue(double theValue);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double _myValue;
	private double _myMin;
	private double _myMax;
	
	private double _myStepSize;
	
	
	private int _myChangeSize = 3;
	private int _myLastY;
	
	private CCListenerManager<CCChangeValueBoxListener> _myListener = CCListenerManager.create(CCChangeValueBoxListener.class);

	public CCSwingDraggableValueBox(double theValue, double theMin, double theMax, double theStepSize){
		_myMin = theMin;
		_myMax = theMax;
		_myStepSize = theStepSize;
		setEditable(true);
		setHorizontalAlignment(JTextField.RIGHT);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				try{
					value(Double.parseDouble(getText()));
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
       addKeyListener(new KeyAdapter() {
        	@Override
        	public void keyReleased(KeyEvent e) {
        		double factor = 1;
        		if(e.isShiftDown())factor = 0.1;
        		if(e.isAltDown())factor = 10;
        		switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					value(_myValue + _myStepSize * factor);
					break;
				case KeyEvent.VK_DOWN:
					value(_myValue - _myStepSize * factor);
					break;

				default:
					break;
				}
        	}
		});
		
		setText(value() + "");
	}
	
	public CCListenerManager<CCChangeValueBoxListener> changeEvents(){
		return _myListener;
	}
	

	@Override
	public void mouseDragged(MouseEvent e) {
		double factor = 1;
		if(e.isShiftDown())factor = 0.1;
		if(e.isAltDown())factor = 10;
		
		int myChange = e.getY() - _myLastY;
		int steps = myChange / _myChangeSize;
		
		_myLastY += steps * _myChangeSize;
		
		value(_myValue - steps * _myStepSize * factor);
		
	}
	
	public void value(double theValue){
		if(theValue == _myValue)return;
		
//		_myValue = CCMath.quantize(theValue, _myStepSize);
		_myValue = CCMath.constrain(theValue, _myMin, _myMax);
		
		for(CCChangeValueBoxListener myListener:_myListener){
			myListener.changeValue(_myValue);
		}
		
		setText(CCFormatUtil.nd(_myValue, 2));
	}
	
	public double value(){
		return _myValue;
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
