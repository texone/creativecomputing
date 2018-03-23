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
package cc.creativecomputing.core.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.core.logging.CCLog;

/**
 * 
 */
public class CCConsoleLineReader implements Runnable{
	

	private Thread _myThread;
	private BufferedReader _myReader;

	public final CCEventManager<CCConsoleLineReader> startEvents = new CCEventManager<>();
	public final CCEventManager<String> inputEvents = new CCEventManager<>();

	public CCConsoleLineReader() {
	}

	public void start() {
		try {
			_myReader = new BufferedReader(new InputStreamReader(System.in));
			_myThread = new Thread(this);
			_myThread.start();
		}
		catch (Exception e) {
			CCLog.error(e);
		}
		startEvents.event(this);
	}

	public void stop() {
		
	}


	@Override
	public void run() {
		while (true) {
			try {
				inputEvents.event(_myReader.readLine());
			}
			catch (Exception e) {}
		}
	}
}
