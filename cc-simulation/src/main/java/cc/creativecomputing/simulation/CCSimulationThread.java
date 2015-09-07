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
package cc.creativecomputing.simulation;


import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;


public class CCSimulationThread{

	private Runnable runnable;
	private Thread thread;
	private volatile boolean shouldStop;
	private boolean runAsFastAsPossible;
	
	private long _myFrameRateLastMillis = System.currentTimeMillis();
	private float _myFrameRate = 0;
	private float _myTargetFrameRate = 0;
	
	private List<CCParticleGroup<?>> _myParticleGroups = new ArrayList<CCParticleGroup<?>>();
	
	class MainLoop implements Runnable {
		public void run() {
			try {
				while (!shouldStop) {
					if (!runAsFastAsPossible) {
						// Avoid swamping the CPU
						Thread.yield();
					}
					update();
				}
			} finally {
				shouldStop = false;
				synchronized (CCSimulationThread.this) {
					thread = null;
					CCSimulationThread.this.notify();
				}
			}
		}
	}

    private boolean _myFixFramerate = false;

    public CCSimulationThread(final CCParticleGroup<?> theParticleGroup) {
    	_myParticleGroups.add(theParticleGroup);
    	runAsFastAsPossible = true;
    }
    
    protected void addParticleGroup(final CCParticleGroup<?> theParticleGroup) {
    	synchronized (_myParticleGroups) {
        	_myParticleGroups.add(theParticleGroup);
		}
    }

    public void framerate(int theFramerate) {
    	_myTargetFrameRate = theFramerate;
    	runAsFastAsPossible = false;
    }

    public float framerate() {
    	return _myFrameRate;
    }

    /** Starts this animator. */
	public synchronized void start() {
		if (thread != null) {
			throw new RuntimeException("Already started");
		}
		if (runnable == null) {
			runnable = new MainLoop();
		}
		thread = new Thread(runnable);
		thread.start();
	}
    
    /**
	 * Indicates whether this animator is currently running. This should only be
	 * used as a heuristic to applications because in some circumstances the
	 * Animator may be in the process of shutting down and this method will
	 * still return true.
	 */
	public synchronized boolean isAnimating() {
		return (thread != null);
	}

	/**
	 * Stops this animator. In most situations this method blocks until
	 * completion, except when called from the animation thread itself or in
	 * some cases from an implementation-internal thread like the AWT event
	 * queue thread.
	 */
	public synchronized void stop() {
		shouldStop = true;
		notifyAll();
		// It's hard to tell whether the thread which calls stop() has
		// dependencies on the Animator's internal thread. Currently we
		// use a couple of heuristics to determine whether we should do
		// the blocking wait().
		if ((Thread.currentThread() == thread) || EventQueue.isDispatchThread()) {
			return;
		}
		
		while (shouldStop && thread != null) {
			try {
				wait();
			} catch (InterruptedException ie) {
			}
		}
	}


    public void fixFrameRate(boolean theFixFramerateFlag) {
        _myFixFramerate = theFixFramerateFlag;
    }
    
    private void update(){
		if (_myFrameRateLastMillis != 0){
			float myElapsedTime = (float) (System.currentTimeMillis() - _myFrameRateLastMillis);
			
			if (myElapsedTime != 0){
				_myFrameRate = (_myFrameRate * 0.9f) + ((1.0f / (myElapsedTime / 1000.0f)) * 0.1f);
			}
		}
		_myFrameRateLastMillis = System.currentTimeMillis();
		
		update(1/_myFrameRate);
	}


    public void update(float theDeltaTime) {
        if (_myFixFramerate) {
        	theDeltaTime = (1f / _myTargetFrameRate);
        }
        synchronized (_myParticleGroups) {
        	for(CCParticleGroup<?> myParticleGroup:_myParticleGroups){
            	myParticleGroup.update(theDeltaTime);
            }
		}
    }
}
