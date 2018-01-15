package cc.creativecomputing.controlui;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.Highlighter;

import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.math.CCMath;
import net.objecthunter.exp4j.ExpressionBuilder;

public class CCNumberBox extends JTextField implements MouseListener, MouseMotionListener {

	public interface CCChangeValueBoxListener {
		void changeValue(double theValue);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double _myValue;
	private double _myMin;
	private double _myMax;

	private int _myDigits;

	private int _myChangeSize = 3;
	private int _myLastY;
	private int _myCaretPosition;
	private double _myFactor;

	private CCListenerManager<CCChangeValueBoxListener> _myListener = CCListenerManager
			.create(CCChangeValueBoxListener.class);

	private Highlighter _myHighighter;

	private boolean _myFirstPress = true;

	public CCNumberBox(double theValue, double theMin, double theMax, int theDigits) {
		_myMin = theMin;
		_myMax = theMax;
		_myDigits = theDigits;

		value(theValue);
		setEditable(true);
		setHorizontalAlignment(JTextField.RIGHT);

		addMouseListener(this);
		addMouseMotionListener(this);

		addActionListener(theE -> {
			double myValue = value();
			try {
				value(new ExpressionBuilder(getText()).build().evaluate());
			} catch (Exception e) {
				value(myValue);
			}
		});

		getInputMap().put(KeyStroke.getKeyStroke("pressed DOWN"), "Nothing");
		getInputMap().put(KeyStroke.getKeyStroke("pressed UP"), "Nothing");

		addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					changeby(1);
					break;
				case KeyEvent.VK_DOWN:
					changeby(-1);
					break;

				default:
					break;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
				case KeyEvent.VK_DOWN:
					setCaretPosition(_myCaretPosition);
					_myFirstPress = true;
					endEdit();
					break;
				}
			}
		});

		_myHighighter = getHighlighter();

	}

	public CCListenerManager<CCChangeValueBoxListener> changeEvents() {
		return _myListener;
	}

	private void changeby(double theChange) {
		if (_myFirstPress) {
			updateCaret();
			startEdit();
		} else
			setCaretPosition(_myCaretPosition);
		_myFirstPress = false;
		value(_myValue + theChange * _myFactor);
	}

	private void startEdit() {
		setHighlighter(null);
		setEditable(false);
	}

	private void endEdit() {
		if (getHighlighter() == null) {
			setHighlighter(_myHighighter);
			setEditable(true);
			getCaret().setVisible(true);
		}
	}

	private void updateCaret() {
		_myCaretPosition = getCaretPosition();
		String myText = getText();
		if (myText.contains(".")) {
			int myDotIndex = getText().indexOf(".");
			if (_myCaretPosition > myDotIndex + 1) {
				int myPow = _myCaretPosition - myDotIndex - 1;
				_myFactor = CCMath.pow(10d, -myPow);
			} else {
				int myPow = CCMath.max(0, myDotIndex - _myCaretPosition);
				_myFactor = CCMath.pow(10d, myPow);
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		_myLastY = e.getY();
		updateCaret();
	}

	@Override
	public void mouseDragged(MouseEvent e) {

		int myChange = e.getY() - _myLastY;
		int steps = myChange / _myChangeSize;

		_myLastY += steps * _myChangeSize;
		if (steps != 0) {
			startEdit();
		}
		double myVal = _myValue / _myFactor;
		int myCom = (int) myVal;
		double myRest = myVal - myCom;
		myCom -= steps;
		myVal = myCom + myRest;
		myVal *= _myFactor;
		value(myVal);

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		endEdit();
	}

	public void value(double theValue) {
		if (theValue == _myValue) {
			setText(CCFormatUtil.nd(_myValue, _myDigits));
			return;
		}

		// _myValue = CCMath.quantize(theValue, _myStepSize);
		_myValue = CCMath.constrain(theValue, _myMin, _myMax);

		for (CCChangeValueBoxListener myListener : _myListener) {
			myListener.changeValue(_myValue);
		}

		setText(CCFormatUtil.nd(_myValue, _myDigits));
	}

	public double value() {
		return _myValue;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	public static void main(String[] args) {
		JFrame myFrame = new JFrame("test value box");

		myFrame.getContentPane().setLayout(new BorderLayout());
		myFrame.getContentPane().add(new CCNumberBox(0.5, -500.0, 1500.0, 4), BorderLayout.CENTER);

		myFrame.pack();

		myFrame.setVisible(true);
	}
}
