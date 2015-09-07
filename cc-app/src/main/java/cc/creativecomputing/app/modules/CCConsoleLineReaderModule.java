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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;

import cc.creativecomputing.core.logging.CCLog;

/**
 * 
 */
public class CCConsoleLineReaderModule extends CCAbstractAppModule<CCConsoleLineListener> implements Runnable{
	

	private Thread _myThread;
	private BufferedReader _myReader;


	public CCConsoleLineReaderModule(String theID) {
		super(CCConsoleLineListener.class, theID);
	}


	@Override
	public void start() {
		try {
			_myReader = new BufferedReader(new InputStreamReader(System.in));
			_myThread = new Thread(this);
			_myThread.start();
		}
		catch (Exception e) {
			CCLog.error(e);
		}
		Iterator<CCConsoleLineListener> it = _myListeners.iterator();
		while (it.hasNext()) {
			it.next().start(this);
		}
	}


	@Override
	public void stop() {
		
	}


	@Override
	public void run() {
		while (true) {
			try {
				String theLine = _myReader.readLine();
				Iterator<CCConsoleLineListener> it = _myListeners.iterator();
				while (it.hasNext()) {
					it.next().onLine(theLine);
				}
			}
			catch (Exception e) {}
		}
	}
}
