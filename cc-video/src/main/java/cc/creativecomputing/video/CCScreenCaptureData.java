/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.video;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.image.CCImageUtil;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelInternalFormat;
import cc.creativecomputing.image.CCPixelType;
import cc.creativecomputing.math.CCMath;

import com.sun.awt.AWTUtilities;

/**
 * @author christianriekoff
 * 
 */
public class CCScreenCaptureData extends CCVideo {
	
	public static class CCScreenGrabArea extends JFrame implements MouseMotionListener, MouseListener, WindowListener {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -8104620285897870152L;

		public static interface CCScreenGrabAreaListener{
			public void onChange();
		}
		
		private CCListenerManager<CCScreenGrabAreaListener> _mEvents = new CCListenerManager<CCScreenGrabAreaListener>(CCScreenGrabAreaListener.class);
		
		private boolean _myIsActive = true;

		public CCScreenGrabArea() {
			super("GradientTranslucentWindow");

			setUndecorated(true);

			setBackground(new Color(0, 0, 0, 0));
			setSize(new Dimension(300, 200));
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

			setContentPane(createContentPane());
			setLayout(new GridBagLayout());
			setAlwaysOnTop(true);
			
			addMouseListener(this);
			addMouseMotionListener(this);
			
			AWTUtilities.setWindowOpaque(this, false);
		}
		
		public void isActive(boolean theIsActive) {
			_myIsActive = theIsActive;
			setVisible(_myIsActive);
		}
		
		public boolean isActive() {
			return _myIsActive;
		}
		
		public void update(final float theDeltaTime) {
			if(!_myDoUpdate)return;
			if(!isVisible())return;
			try {
			Point myMousePos = MouseInfo.getPointerInfo().getLocation();
			_myDrawBackGround = 
				myMousePos.getX() >= getX() && 
				myMousePos.getX() <= getX() + getWidth() &&
				myMousePos.getY() >= getY() && 
				myMousePos.getY() <= getY() + getHeight();
				
			repaint();
			}catch(Exception e) {
				
			}
		}
		
		public CCListenerManager<CCScreenGrabAreaListener> events(){
			return _mEvents;
		}

		private JPanel createContentPane() {
			return new JPanel() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void paintComponent(Graphics g) {
					
					if (g instanceof Graphics2D) {
						Graphics2D g2d = (Graphics2D) g;
						g2d.clearRect(0, 0, getWidth(), getHeight());
						g2d.setPaint(new Color(240, 240, 240, 100));
						if(_myDrawBackGround)g2d.fillRect(0, 0, getWidth(), getHeight());

						g2d.setColor(new Color(255, 0, 0));
						g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
					} 
				}
			};
		}

		private int _myClickX;
		private int _myClickY;

		private int _myWidth;
		private int _myHeight;

		private int _myX;
		private int _myY;

		private boolean _myDoUpdate = true;
		private boolean _myDrawBackGround = false;
		private boolean _myMoveAll;
		private boolean _myLeftResize;
		private boolean _myRightResize;
		private boolean _myTopResize;
		private boolean _myBottomResize;

		@Override
		public void mousePressed(MouseEvent theEvent) {
			_myClickX = theEvent.getXOnScreen();
			_myClickY = theEvent.getYOnScreen();

			_myWidth = getWidth();
			_myHeight = getHeight();
			_myX = getX();
			_myY = getY();
			
//			_myDoUpdate = false;
		}

		@Override
		public void mouseReleased(MouseEvent theE) {
			_mEvents.proxy().onChange();
		}

		public void mouseDragged(MouseEvent theEvent) {
			int myWidth = _myWidth;
			int myHeight = _myHeight;

			int myX = getX();
			int myY = getY();
			if (_myRightResize) {
				myWidth = _myWidth + theEvent.getXOnScreen() - _myClickX;
			}
			if (_myLeftResize) {
				myWidth = _myWidth - theEvent.getXOnScreen() + _myClickX;
				myX = _myX + theEvent.getXOnScreen() - _myClickX;
			}
			if (_myBottomResize) {
				myHeight = _myHeight + theEvent.getYOnScreen() - _myClickY;
			}
			if (_myTopResize) {
				myHeight = _myHeight - theEvent.getYOnScreen() + _myClickY;
				myY = _myY + theEvent.getYOnScreen() - _myClickY;
			}
			if(_myMoveAll){
				myX = _myX + theEvent.getXOnScreen() - _myClickX;
				myY = _myY + theEvent.getYOnScreen() - _myClickY;
			}
			setBounds(myX, myY, myWidth, myHeight);
		}

		public void mouseMoved(MouseEvent mouseEvent) {
			_myLeftResize = mouseEvent.getX() < 5;
			_myRightResize = mouseEvent.getX() >= getWidth() - 5;
			_myTopResize = mouseEvent.getY() < 5;
			_myBottomResize = mouseEvent.getY() >= getHeight() - 5;
			
			_myMoveAll = false;

			if (_myLeftResize) {
				if (_myTopResize) {
					setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
				} else if (_myBottomResize) {
					setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
				} else {
					setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
				}
			} else if (_myRightResize) {
				if (_myTopResize) {
					setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
				} else if (_myBottomResize) {
					setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
				} else {
					setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
				}
			} else if (_myTopResize) {
				setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
			} else if (_myBottomResize) {
				setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
			} else {
				_myMoveAll = true;
				setCursor(new Cursor(Cursor.MOVE_CURSOR));
			}
		}

		@Override
		public void mouseClicked(MouseEvent theE) {}

		@Override
		public void mouseEntered(MouseEvent theE) {}

		@Override
		public void mouseExited(MouseEvent theE) {}

		@Override
		public void windowActivated(WindowEvent theArg0) {}

		@Override
		public void windowClosed(WindowEvent theArg0) {}

		@Override
		public void windowClosing(WindowEvent theArg0) {
			if(_myIsActive)setVisible(false);
		}

		@Override
		public void windowDeactivated(WindowEvent theArg0) {}

		@Override
		public void windowDeiconified(WindowEvent theArg0) {}

		@Override
		public void windowIconified(WindowEvent theArg0) {}
		
		@Override
		public void windowOpened(WindowEvent theArg0) {
			if(_myIsActive)setVisible(true);
		}
	}
	
	private class CaptureThread extends Thread {
		
		public void run() {
			
			while ((Thread.currentThread() == _myCaptureThread)) {
				long myStopTime = System.currentTimeMillis();
				updateTexture();
				_myIsDataUpdated = true;
				
				long myScreenhotTime = System.currentTimeMillis() - myStopTime;
				try {
					if(_myFrameRate > 0) {
						Thread.sleep(CCMath.max(0,(int)(1000 /  _myFrameRate - myScreenhotTime)));
					}
				} catch (InterruptedException e) {
				
				}
			}
		}
	}

	protected CaptureThread _myCaptureThread;
	private float _myFrameRate;
	private float _myCaptureRate = 30;

	/** True if this image is currently being cropped */
	protected boolean _myIsCrop;

	protected int _myCropX;
	protected int _myCropY;
	
	private int _myCaptureX;
	private int _myCaptureY;

	protected int _myCaptureWidth;
	protected int _myCaptureHeight;

	protected BufferedImage _myScreenShot;
	
	private Robot _myRobot;
	
	private boolean _myRunParallel;
	private boolean _myIsRunning = true;
	
	/**
	 * Indicates that data has been updated inside the thread
	 */
	private boolean _myIsDataUpdated = false;
	
	private CCScreenGrabArea _myGrabArea;

	/**
	 * @param theAnimator
	 */
	public CCScreenCaptureData(
		final CCAnimator theAnimator, 
		final int theX, final int theY,
		final int theWidth, final int theHeight, final int theFrameRate,
		final boolean theRunParallel
	) {
		super(theAnimator);
		
		_myGrabArea = new CCScreenGrabArea();
		_myGrabArea.setBounds(theX, theY, theWidth, theHeight);
		
		_myCaptureX = theX;
		_myCaptureY = theY;
		
		_myCaptureWidth = _myWidth = theWidth;
		_myCaptureHeight = _myHeight = theHeight;
		_myBorder = 0;
		
		_myPixelInternalFormat = CCPixelInternalFormat.RGBA;
		_myPixelFormat = CCPixelFormat.BGRA;
		_myPixelType = CCPixelType.UNSIGNED_BYTE;
		
		_myIsDataCompressed = false;
		_myMustFlipVertically = true;
		
		_myFrameRate = theFrameRate;
		
		try {
			_myRobot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(_myRunParallel) {
			_myCaptureThread = new CaptureThread();
			_myCaptureThread.start();
		}
		_myIsFirstFrame = true;
	}
	
	public CCScreenGrabArea grabArea(){
		return _myGrabArea;
	}
	
	public CCScreenCaptureData(
		final CCAnimator theAnimator, 
		final int theX, final int theY,
		final int theWidth, final int theHeight, final int theFrameRate
	) {
		this(theAnimator, theX, theY, theWidth, theHeight, theFrameRate, true);
	}
	
	/**
	 * Returns the rate the screen is currently captured with
	 * @return the rate the screen is currently captured with
	 */
	public float captureRate() {
		return _myCaptureRate;
	}
	
	/**
	 * Defines the area of the screen that is captured
	 * @param theCaptureX
	 * @param theCaptureY
	 * @param theCaptureWidth
	 * @param theCaptureHeight
	 */
	public void captureArea(int theCaptureX, int theCaptureY, int theCaptureWidth, int theCaptureHeight) {
		_myCaptureX = theCaptureX;
		_myCaptureY = theCaptureY;
		_myCaptureWidth = theCaptureWidth;
		_myCaptureHeight = theCaptureHeight;
	}
	
	public void captureX(int theCaptureX) {
		_myCaptureX = theCaptureX;
	}
	
	public void captureY(int theCaptureY) {
		_myCaptureY = theCaptureY;
	}
	
	public void captureWidth(int theCaptureWidth) {
		_myCaptureWidth = theCaptureWidth;
	}
	
	public void captureHeight(int theCaptureHeight) {
		_myCaptureHeight = theCaptureHeight;
	}
	
	private long _myLastMillis = -1;

	protected void updateTexture() {
		_myIsDataUpdated = true;
		try {
			_myScreenShot = _myRobot.createScreenCapture(new Rectangle(_myCaptureX, _myCaptureY, _myCaptureWidth, _myCaptureHeight));
			
			if(_myLastMillis < 0) {
				_myLastMillis = System.currentTimeMillis();
			}else {
				long myDeltaTime = System.currentTimeMillis() - _myLastMillis;
					
				_myLastMillis = System.currentTimeMillis();
					
				float myCurrentCaptureRate = 1f / (myDeltaTime / 1000f);
				_myCaptureRate = _myCaptureRate * 0.9f + myCurrentCaptureRate * 0.1f;
			}
		}catch(Exception e) {
			// for now just ignore exceptions
		}
	}

	public void stop() {
		if(_myRunParallel) {
			_myCaptureThread = null; // unwind the thread
		}else {
			_myIsRunning = false;
		}
	}
	
	public void start() {
		if(_myRunParallel) {
			
		}else {
			_myIsRunning = true;
		}
	}

	public void framerate(final float theFramerate) {
//		if (theFramerate <= 0) {
//			CCLog.error("Capture: ignoring bad framerate of " + theFramerate + " fps.");
//			return;
//		}
		_myFrameRate = theFramerate;
	}

	public float framerate() {
		return _myFrameRate;
	}

	private float _myTime = 0;
	/*
	 * (non-Javadoc)
	 * 
	 * @see cc.creativecomputing.events.CCUpdateListener#update(float)
	 */
	@Override
	public void update(CCAnimator theAnimator) {

		if(_myGrabArea.isActive())captureArea(_myGrabArea.getX() + 1, _myGrabArea.getY() + 1, _myGrabArea.getWidth() - 2, _myGrabArea.getHeight() - 2);
		
		if (!_myRunParallel) {
			if(!_myIsRunning)return;
			if(_myFrameRate > 0) {
				_myTime += theAnimator.deltaTime();
				if(_myTime > 1 / _myFrameRate) {
					_myTime -= 1 / _myFrameRate;
				}else {
					return;
				}
			}
			updateTexture();
		}
		if (_myScreenShot == null)
			return;
		if (_myIsDataUpdated && _myScreenShot != null) {
			_myIsDataUpdated = false;
			CCImageUtil.toImage(_myScreenShot, this);
		}

		if (_myIsFirstFrame) {
			_myIsFirstFrame = false;
			_myListener.proxy().onInit(this);
		} else {
			_myListener.proxy().onUpdate(this);
		}
	}

}
