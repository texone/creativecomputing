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
package cc.creativecomputing.core.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

class CCStandardOutHandler extends Handler {

	public CCStandardOutHandler() {
		super();
	}

	public void publish(LogRecord record) {
		if (!isLoggable(record)) {
			return;
		}
		
		if (record.getLevel() == Level.SEVERE || record.getLevel() == Level.WARNING) {
			// print error messages in red in eclipse console window
			System.err.print(getFormatter().format(record));		
		} else {
			System.out.print(getFormatter().format(record));
		}
	}

	public void flush() {
		System.out.flush();
	}

	public void close() {
	}
}
