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
package cc.creativecomputing.app.util;

import java.util.HashMap;
import java.util.LinkedList;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorListener;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCFormatUtil;

/**
 * Use the stop watch class to measure the time of certain part of your application. The Stop watch watch makes it easy
 * to measure different processes and print out the values.
 * 
 * @author artcom
 * 
 */
public class CCStopWatch implements CCAnimatorListener {

	@CCProperty(name = "active")
	protected boolean _cActive = false;

	@CCProperty(name = "samples", min = 50, max = 2000)
	protected int _cSamples = 200;

	@CCProperty(name = "pause")
	private boolean _cPause = false;
	
	@CCProperty(name = "print")
	private boolean _cPrint = false;

	public class CCStopWatchItem {

		private long _myNanoTime = 0;
		private double _myCurrentTime = 0;
		
		public double _myLastHeight = -1;

		private String _myName = "undefined";
		private LinkedList<Double> _myHistory = null;

		public CCStopWatchItem(String theItemName) {
			_myName = theItemName;
			_myHistory = new LinkedList<>();
		}
		
		public String name(){
			return _myName;
		}
		
		public LinkedList<Double> history(){
			return _myHistory;
		}

		public double record() {
			while (_myHistory.size() >= _cSamples) {
				_myHistory.pollLast();
			}
			_myHistory.push(_myCurrentTime);
			double myResult = _myCurrentTime;
			_myCurrentTime = 0;
			
			return myResult;
		}

		public void start() {
			_myNanoTime = System.nanoTime();
		}

		public double end() {
			long myDeltaTime = System.nanoTime() - _myNanoTime;
			double myLastDeltaTime = myDeltaTime * 1e-6;
			if(_cPrint)System.out.println(_myName + " took " + CCFormatUtil.nd(myLastDeltaTime, 4)+" sec");
			_myCurrentTime += myLastDeltaTime;
			return myLastDeltaTime;
		}
	}

	protected HashMap<String, HashMap<String, CCStopWatchItem>> _myBlocks;
	private LinkedList<Double> _myHistorySum = new LinkedList<>();
	protected double _myMax = 0;
	private String _myCurrentBlock = "default";
	private String _myCurrentItemName = null;

	private static CCStopWatch _myInstance = null;

	public CCStopWatch() {
		_myBlocks = new HashMap<String, HashMap<String, CCStopWatchItem>>();
		startBlock("default");
	}

	public static CCStopWatch instance() {
		if (_myInstance == null) {
			_myInstance = new CCStopWatch();
		}
		return _myInstance;
	}

	private boolean breakExecution() {
		return _cPause || !_cActive;
	}
	
	public void print(boolean theDoPrint){
		_cPrint = theDoPrint;
	}

	public void startBlock(String theBlockName) {
		if (breakExecution())
			return;

		_myCurrentBlock = theBlockName;

		if (!_myBlocks.containsKey(_myCurrentBlock)) {
			_myBlocks.put(_myCurrentBlock, new HashMap<String, CCStopWatchItem>());
		}
	}

	public void endLastAndStartWatch(String theName) {
		endLast();
		startWatch(theName);
	}

	public void endLast() {
		if (breakExecution())
			return;
		endWatch(_myCurrentItemName);
	}

	public void startWatch(String theName) {
		if (breakExecution())
			return;

		_myCurrentItemName = theName;

		if (!_myBlocks.get(_myCurrentBlock).containsKey(theName)) {
			_myBlocks.get(_myCurrentBlock).put(theName, new CCStopWatchItem(theName));
		}
		_myBlocks.get(_myCurrentBlock).get(theName).start();
	}

	public double endWatch(String theName) {
		if (breakExecution())
			return 0;

		CCStopWatchItem myCurrentItem = _myBlocks.get(_myCurrentBlock).get(theName);
		if(myCurrentItem == null)throw new RuntimeException("You end a watch with the name: +\"" + theName +"\" that does not have been started.");
		return myCurrentItem.end();
	}

	@Override
	public void update(CCAnimator theAnimator) {
		if (breakExecution())
			return;
		
		while (_myHistorySum.size() >= _cSamples) {
			_myHistorySum.pollLast();
		}
		double mySum = 0;
		
		for (HashMap<String, CCStopWatchItem> myItems : _myBlocks.values()) {
			for (CCStopWatchItem myItem : myItems.values()) {
				mySum += myItem.record();
			}
		}
		_myHistorySum.push(mySum);
	}

	@Override
	public void start(CCAnimator theAnimator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop(CCAnimator theAnimator) {
		// TODO Auto-generated method stub
		
	}

	
}
