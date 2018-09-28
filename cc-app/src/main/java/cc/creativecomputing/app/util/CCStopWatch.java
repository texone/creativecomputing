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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.core.util.CCStringUtil;

/**
 * Use the stop watch class to measure the time of certain part of your application. The Stop watch watch makes it easy
 * to measure different processes and print out the values.
 * 
 * @author artcom
 * 
 */
public class CCStopWatch{

	@CCProperty(name = "samples", min = 50, max = 2000)
	protected int _cSamples = 200;
	
	@CCProperty(name = "print")
	private boolean _cPrint = false;

	public class CCStopWatchItem {
		@CCProperty(name = "active")
		protected boolean _cActive = true;

		private long _myNanoTime = 0;
		private double _myCurrentTime = 0;
		
		public double _myLastHeight = -1;

		private String _myName = "undefined";
		private LinkedList<Double> _myHistory = null;

		public CCStopWatchItem(String theItemName) {
			_myName = theItemName;
			_myHistory = new LinkedList<>();
		}
		
		public boolean active() {
			return _cActive;
		}
		
		public String name(){
			return _myName;
		}
		
		public LinkedList<Double> history(){
			return _myHistory;
		}

		public double record() {
			if(!_cActive)return 0;
			while (_myHistory.size() >= _cSamples) {
				_myHistory.pollLast();
			}
			_myHistory.push(_myCurrentTime);
			double myResult = _myCurrentTime;
			_myCurrentTime = 0;
			
			return myResult;
		}

		public void start() {
			if(!_cActive)return;
			_myNanoTime = System.nanoTime();
		}

		public double end() {
			if(!_cActive)return 0;
			long myDeltaTime = System.nanoTime() - _myNanoTime;
			double myLastDeltaTime = myDeltaTime * 1e-6;
			if(_cPrint)System.out.println(_myName + " took " + CCFormatUtil.nd(myLastDeltaTime, 4)+" sec");
			_myCurrentTime += myLastDeltaTime;
			return myLastDeltaTime;
		}
	}
	
	public class CCStopWatchBlock{
		@CCProperty(name = "active")
		protected boolean _cActive = true;
		
		@CCProperty(name = "blocks")
		public Map<String, CCStopWatchBlock> blocks = new LinkedHashMap<>();

		@CCProperty(name = "items")
		public Map<String, CCStopWatchItem> items = new LinkedHashMap<>();
		
		private Optional<CCStopWatchItem> item(String theWatchName, boolean theCreate) {
			if (theWatchName.indexOf('/') != -1) {
				return itemRecursive(CCStringUtil.split(theWatchName, '/'), 0, theCreate);
			}
			
			CCStopWatchItem myResult = items.get(theWatchName);
			if(theCreate && myResult == null) {
				items.put(theWatchName, myResult = new CCStopWatchItem(theWatchName));
			}
			return Optional.ofNullable(myResult);
		}
		
		protected Optional<CCStopWatchItem> itemRecursive(String[] theItems, int theOffset, boolean theCreate) {
			if (theOffset == theItems.length - 1) {
				CCStopWatchItem myResult = items.get(theItems[theOffset]);
				if(theCreate && myResult == null) {
					items.put(theItems[theOffset], myResult = new CCStopWatchItem(theItems[theOffset]));
				}
				return Optional.ofNullable(items.get(theItems[theOffset]));
			} else {
				CCStopWatchBlock myResult = blocks.get(theItems[theOffset]);
				if(theCreate && myResult == null) {
					blocks.put(theItems[theOffset], myResult = new CCStopWatchBlock());
				}
				return myResult.itemRecursive(theItems, theOffset + 1, theCreate);
			}
		}
		
		public void start(String theWatchName) {
			if(!_cActive)return;
			item(theWatchName, true).ifPresent(i -> i.start());
		}
		
		public double end(String theWatchName) {
			if(!_cActive)return 0;
			double myResult = 0;
			Optional<CCStopWatchItem> myItem = item(theWatchName, false);
			if(myItem.isPresent()) {
				myResult = myItem.get().end();
			}
			return myResult;
		}
		
		public double record() {
			if(!_cActive)return 0;
			double mySum = 0;
			for (CCStopWatchBlock myBlock : blocks.values()) {
				mySum += myBlock.record();
			}
			for (CCStopWatchItem myItem : items.values()) {
				mySum += myItem.record();
			}
			return mySum;
		}
		
		public List<CCStopWatchItem> items(){
			if(!_cActive)return new ArrayList<>();
			List<CCStopWatchItem> myResult = new ArrayList<>(items.values());
			for (CCStopWatchBlock myBlock : blocks.values()) {
				myResult.addAll(myBlock.items());
			}
			return myResult;
		}
	}

	@CCProperty(name = "main block", hide = true)
	protected CCStopWatchBlock _myMainBlock = new CCStopWatchBlock();
	
	private LinkedList<Double> _myHistorySum = new LinkedList<>();
	
//	protected double _myMax = 0;
//	private String _myCurrentBlock = "default";
	private String _myCurrentItemName = null;

	private static CCStopWatch _myInstance = null;

	public CCStopWatch() {
	}

	public static CCStopWatch instance() {
		if (_myInstance == null) {
			_myInstance = new CCStopWatch();
		}
		return _myInstance;
	}
	
	public void print(boolean theDoPrint){
		_cPrint = theDoPrint;
	}

	public void endLastAndStartWatch(String theName) {
		endLast();
		startWatch(theName);
	}

	public void endLast() {
		endWatch(_myCurrentItemName);
	}
	
	public void startWatch(String theName) {
		_myCurrentItemName = theName;
		
		_myMainBlock.start(theName);
	}

	public double endWatch(String theName) {
		return _myMainBlock.end(theName);
	}

	public void update(CCAnimator theAnimator) {
		while (_myHistorySum.size() >= _cSamples) {
			_myHistorySum.pollLast();
		}
	
		_myHistorySum.push(_myMainBlock.record());
	}
	
	public boolean active() {
		return _myMainBlock._cActive;
	}
	
	public List<CCStopWatchItem> items(){
		return _myMainBlock.items();
	}
}
