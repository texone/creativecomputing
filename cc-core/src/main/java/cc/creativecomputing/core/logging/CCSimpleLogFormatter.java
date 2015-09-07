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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * MyCustomFormatter formats the LogRecord as follows:
 * date   level   localized message with parameters 
 */
class CCSimpleLogFormatter extends Formatter {

	public CCSimpleLogFormatter() {
		super();
	}

	public String format(LogRecord record) {
		
		// Create a StringBuffer to contain the formatted record
		// start with the date.
		StringBuffer sb = new StringBuffer();
		
		// Get the date from the LogRecord and add it to the buffer
		Date date = new Date(record.getMillis());
		sb.append("[" + date.toString() + "] ");
		
		int mySplit = record.getSourceClassName().toString().lastIndexOf(".");
		mySplit = (mySplit==0)?0:mySplit+1;
		sb.append("[" + record.getSourceClassName().substring(mySplit) + ":" + record.getSourceMethodName()+ "] ");
			
		// Get the level name and add it to the buffer
		sb.append("[" + record.getLevel().getName() + "] ");
	
		sb.append(formatMessage(record));
		sb.append("\n");
		
		if (record.getThrown() != null) {			
			sb.append( stack2string( record.getThrown() ) );
		}
		
		return sb.toString();
	}
	
	private static String stack2string(Throwable e) {
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return sw.toString();
		} catch(Exception e2) {
			return "bad stack2string";
		}
	}	
}
