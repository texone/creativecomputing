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
package cc.creativecomputing.app.modules;

import java.awt.EventQueue;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import cc.creativecomputing.core.CCProperty;

// http://www.java-gaming.org/index.php?topic=24220.0

/**
 * 
 */
public class CCAnimator extends CCAbstractAppModule<CCAnimatorListener>{
	
	/**
	 * Timers in java can 
	 * @author christianr
	 *
	 */
	public static enum CCAnimationMode{
		FRAMERATE_PERFORMANT,
		FRAMERATE_PRECISE,
		AS_FAST_AS_POSSIBLE,
		PERFORMANT
	}
	
	/**
	 * Defines ow to handle exceptions
	 * @author christianr
	 *
	 */
	public static enum CCExceptionHandling{
		/**
		 * Causes the Animator to ignore exceptions produced while animating
		 */
		IGNORE,
		/**
		 * Causes the Animator to print exceptions produced while animating
		 */
		PRINT,
		/**
		 * Causes the Animator to throw exceptions produced while animating
		 */
		THROW
	}
	
	/**
	 * Forces the animator to run with a fixed frame rate. Be aware that you also have to set
	 * the {@linkplain #animationMode(CCAnimationMode)} to {@linkplain CCAnimationMode#FRAMERATE_PERFORMANT} or
	 * {@linkplain CCAnimationMode#FRAMERATE_PRECISE} for the forced frame rate to be effective. 
	 * @param theFrameRate frame rate to use for animation
	 */
	@CCProperty(desc = "framerate of the application -1 for as fast as possible")
	public int framerate = 60;
	
	/**
	 * animation mode. there are different animation modes focusing on performance or precision.
	 * <ul>
	 * <li>{@linkplain CCAnimationMode#PERFORMANT}</li> yields the animation thread for better performance this is the default mode
	 * <li>{@linkplain CCAnimationMode#AS_FAST_AS_POSSIBLE}</li> runs as fast as possible taking more performance
	 * <li>{@linkplain CCAnimationMode#FRAMERATE_PERFORMANT}</li> running with the define forced frame rate, weighting performance over precision
	 * <li>{@linkplain CCAnimationMode#FRAMERATE_PRECISE}</li> running with the define forced frame rate, weighting performance of precision
	 * </ul>
	 */
	@CCProperty(desc = "timer implementation")
	public CCAnimationMode animationMode = CCAnimationMode.PERFORMANT;
	
	@CCProperty(desc = "define how exceptions shoud be handled ether IGNORE, PRINT or THROW")
	public CCExceptionHandling exceptionHandling = CCExceptionHandling.THROW;
	
	private Thread _myMainLoop;
	private volatile boolean _myShouldStop;
	
	 Scanner scanner = new Scanner(new InputStreamReader(System.in));

	/** 
	 * Creates a new, empty Animator. 
	 **/
	public CCAnimator(String theID) {
		super(CCAnimatorListener.class, theID);
	}
	
	public CCAnimator(){
		this("animator");
	}
	
	@CCProperty(name = "fix update time")
	public boolean fixUpdateTime = false;
	
	@CCProperty(name = "fixed update time", min = 0, max = 1)
	public double fixedUpdateTime = 1;
	
	private double _myFrameRate = 0;
	private int _myFrameCount = 0;

	private long _myFrameRateLastNanos = 0;
	private long _myDeltaTimeNanos = 0;
	
	private double _myDeltatime = 0;
	private double _myDeltaVariation = 0;
	private double _myTimeSinceStart = 0;
	
	private void calculateDeltaTime(){
		if(fixUpdateTime){
			_myFrameRateLastNanos = System.nanoTime();
			_myDeltatime =  fixedUpdateTime;
			_myTimeSinceStart += _myDeltatime;
			return;
		}
		
		if (_myFrameRateLastNanos == 0){
			_myFrameRateLastNanos = System.nanoTime();
			_myDeltatime = 0;
			return;
		}
		
		long myNanos = System.nanoTime();
		_myDeltaTimeNanos = myNanos - _myFrameRateLastNanos;
		_myFrameRateLastNanos = myNanos;
		
		if (_myDeltaTimeNanos == 0)return;
		
		_myDeltaVariation = _myDeltatime;
		_myDeltatime = (double)(_myDeltaTimeNanos / 1e9);
		_myDeltaVariation -= _myDeltatime;
		_myTimeSinceStart += _myDeltatime;
		_myFrameRate = 1f / _myDeltatime;
	}
	
	public void update(){
		calculateDeltaTime();
		_myFrameCount++;
		_myListeners.proxy().update(this);
	}
	
//	public String appTime(){
//		long myTime = _myMillisSinceStart;
//		int millis = (int)(myTime % 1000);
//		
//		myTime /= 1000;
//		int seconds = (int)myTime;
//		seconds %= 60;
//		
//		myTime /= 60;
//		int minutes = (int)myTime;
//		minutes %= 60;
//		
//		myTime /= 60;
//		int hours = (int)myTime;
//		
//		return
//			"HOURS:" + CCFormatUtil.nf(hours,2) +
//			" MINUTES:"+CCFormatUtil.nf(minutes, 2)+
//			" SECONDS:"+CCFormatUtil.nf(seconds, 2)+
//			" MILLIS:"+CCFormatUtil.nf(millis, 4);
//	}

	private class MainLoop extends Thread {
		
		private long _myDiff = 0;
		
		
		
		public void run() {
			try {
				while (!_myShouldStop) {
					try {
						// Don't consume CPU unless there is work to be done
						if (_myListeners.size() == 0) {
							synchronized (CCAnimator.this) {
								while (_myListeners.size() == 0 && !_myShouldStop) {
									try {
										CCAnimator.this.wait();
									} catch (InterruptedException e) {
									}
								}
							}
						}
						long myNanosBeforeUpdate = System.nanoTime();
						update();
						long myUpdateTime = System.nanoTime() - myNanosBeforeUpdate;
						long myForceTimeNanos = (long)(1f / framerate * 1e9);
						
						switch (animationMode) {
						case FRAMERATE_PERFORMANT:
							long myWaitTime = myForceTimeNanos - myUpdateTime - _myDiff;
							long myTime = System.nanoTime();
							TimeUnit.NANOSECONDS.sleep(myWaitTime);
							_myDiff = ((System.nanoTime() - myTime) - myWaitTime);
							break;
						case FRAMERATE_PRECISE:
							long myDelay = System.nanoTime() + myForceTimeNanos - myUpdateTime;
							while(System.nanoTime() < myDelay){
								Thread.sleep(1);
							}
							break;
						case PERFORMANT:
							Thread.yield();
						case AS_FAST_AS_POSSIBLE:
						default:
							break;
						}
					} catch (Exception e) {
						switch(exceptionHandling){
						case PRINT:
							e.printStackTrace();
							break;
						case THROW:
							throw new RuntimeException(e);
						default:
						}
					}
				}
			} finally {
				_myShouldStop = false;
				synchronized (CCAnimator.this) {
					_myMainLoop = null;
					CCAnimator.this.notify();
				}
			}
		}
	}

	/** 
	 * Starts this animator. 
	 **/
	public synchronized void start() {
		if (_myMainLoop == null) {
			_myMainLoop = new MainLoop();
		}
		if (!_myMainLoop.isAlive()) {
			_myListeners.proxy().start(this);
			_myMainLoop.start();
		}
	}

	/**
	 * Indicates whether this animator is currently running. This should only be
	 * used as a heuristic to applications because in some circumstances the
	 * Animator may be in the process of shutting down and this method will
	 * still return true.
	 */
	public synchronized boolean isAnimating() {
		return (_myMainLoop != null);
	}

	/**
	 * Stops this animator. In most situations this method blocks until
	 * completion, except when called from the animation thread itself or in
	 * some cases from an implementation-internal thread like the AWT event
	 * queue thread.
	 */
	public synchronized void stop() {
		_myShouldStop = true;
		notifyAll();
		// It's hard to tell whether the thread which calls stop() has
		// dependencies on the Animator's internal thread. Currently we
		// use a couple of heuristics to determine whether we should do
		// the blocking wait().
		if ((Thread.currentThread() == _myMainLoop) || EventQueue.isDispatchThread()) {
			return;
		}
		while (_myShouldStop && _myMainLoop != null) {
			try {
				wait();
			} catch (InterruptedException ie) {
			}
		}
		_myListeners.proxy().stop(this);
	}

	/**
	 * Returns the current frame rate of the animator
	 * @return current frame rate of the animator
	 */
	@CCProperty(name = "show frame rate")
	public double frameRate(){
		return _myFrameRate;
	}
	
	/**
	 * Returns the time since the last frame of the animator in seconds
	 * @return time since the last frame of the animator 
	 */
	public double deltaTime(){
		return _myDeltatime;
	}
	
	public double deltaTimeVariation(){
		return _myDeltaVariation;
	}
	
	/**
	 * Returns the time since the start of the animator in seconds
	 * @return
	 */
	public double time(){
		return _myTimeSinceStart;
	}
	
	/**
	 * Returns the number of frames since application start.
	 * @return number of frames since application start
	 */
	public int frames(){
		return _myFrameCount;
	}
}
