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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.SocketHandler;
import java.util.logging.XMLFormatter;
/**
 * <p>This is a little wrapper around java.util.logging to create a more convenient
 * way to log application states to stderr, stdout and/or a logfile. Usage sample:</p>
 * <blockquote><pre>CCLog.warn("what is wrong here?!");</pre></blockquote>
 * 
 * The logger can be configured using environment variables. Default logging to the console
 * is always active and cannot be disabled. The logging is enabled while calling CCLog.[warn,error,info]("")
 * for the first time.
 * 
 * Available Environment Variables are: 
 * CC_LOG_HANDLERS=[FILE,TCP] - enable additional handlers. Multiple values allowed
 * Note about TCP: the receiver needs to be up and running before the application starts. 
 * Otherwise an exception will be thrown. There is no reconnection in case of connection
 * loss, simple because it takes to long.
 * 
 * Configure console output:
 * CC_LOG_CONSOLE_FORMAT=[CCSimpleLogFormatter,SimpleFormatter,XMLFormatter] - choose one. 
 * CC_LOG_CONSOLE_VERBOSITY=[SEVERE,WARNING,INFO,CONFIG,FINE,FINER,FINEST] - set verbosity level. Default is INFO
 * 
 * Configure file output:
 * CC_LOG_FILE_FORMAT=[CCSimpleLogFormatter,SimpleFormatter,XMLFormatter] - choose one. 
 * CC_LOG_FILE_VERBOSITY=[SEVERE,WARNING,INFO,CONFIG,FINE,FINER,FINEST] - set verbosity level. Default is INFO
 * CC_LOG_FILE_PATH=[path to write the logfile to] - Default is "log/"
 * 
 * Configure TCP output:
 * CC_LOG_TCP_FORMAT=[CCSimpleLogFormatter,SimpleFormatter,XMLFormatter] - choose one. 
 * CC_LOG_TCP_VERBOSITY=[SEVERE,WARNING,INFO,CONFIG,FINE,FINER,FINEST] - set verbosity level. Default is INFO
 * CC_LOG_TCP_HOST=[hostname] - set the hostname to send the log data to. Default is "127.0.0.1" 
 * CC_LOG_TCP_PORT=[port] - set the port to send the data to. Default is "6767"
 * 
 * @example cc.creativecomputing.demo.logging;
 * @author sebastian heymann
 * @nosuperclasses
 */

// TODO: Timing the whole thing would be interesting.
// TODO: add more logging functions for FINE, FINER, FINEST.
// TODO: add AC_LOG_MODULE_VERBOSITY style per file verbosity settings.

public class CCLog {
	
	private static HashMap<String, Logger> ourLoggers = new HashMap<String, Logger>();
	private static FileHandler ourFileHandler = null;
	private static SocketHandler ourTCPHandler = null;
	private static boolean ourInitialized = false;
	
	private static String HANDLERS          = getEnv("CC_LOG_HANDLERS", "FILE");
	
	private static String CONSOLE_FORMAT    = getFormatter("CC_LOG_CONSOLE_FORMAT", "CCSimpleLogFormatter");
	private static Level  CONSOLE_VERBOSITY = getLevel("CC_LOG_CONSOLE_VERBOSITY",  "INFO");
	
	private static String FILE_FORMAT       = getFormatter("CC_LOG_FILE_FORMAT", "CCSimpleLogFormatter");
	private static Level  FILE_VERBOSITY    = getLevel("CC_LOG_FILE_VERBOSITY",  "INFO");
	private static String FILE_PATH         = getEnv("CC_LOG_FILE_PATH",         "log/");

	private static String TCP_FORMAT        = getFormatter("CC_LOG_TCP_FORMAT", "CCSimpleLogFormatter");
	private static Level  TCP_VERBOSITY     = getLevel("CC_LOG_TCP_VERBOSITY",  "INFO");
	private static String TCP_HOST          = getEnv("CC_LOG_TCP_HOST",         "127.0.0.1");
	private static String TCP_PORT          = getEnv("CC_LOG_TCP_PORT",         "6767");
		
	private static Level getLevel(String theVar, String theDefault) {
		
		String myLevel = getEnv(theVar, theDefault);
		Level myNewLevel = Level.INFO;

		try {
			myNewLevel = Level.parse(myLevel);
		} catch (NullPointerException ex) {
			return Level.parse(theDefault);
		} catch (IllegalArgumentException ex) {
			return Level.parse(theDefault);
		}

		return myNewLevel;
	}
	
	private static String getFormatter(String theVar, String theDefault) {
		String myFormatter = getEnv(theVar, theDefault);
		if (myFormatter == "CCSimpleLogFormatter" || 
			myFormatter == "XMLFormatter" ||
			myFormatter == "SimpleFormatter") 
		{
			return myFormatter;
		}
		
		return theDefault;
	}
	
	private static String getEnv(String theVariableName, String theDefaultValue) {
		String myValue = System.getenv(theVariableName);
		if (myValue == null) {
			return theDefaultValue;
		}
		
		return myValue;
	}
	
	private static Logger getLogger(String loggerName) {
		if (!ourInitialized) {
			initialize();
		}
		
		if (ourLoggers.containsKey(loggerName)) {
			return ourLoggers.get(loggerName);
		}
		
		Logger myLogger = Logger.getLogger(loggerName);
		setConfiguration(myLogger);
		ourLoggers.put(loggerName, myLogger);
		return myLogger; 
	}
	
	private static String getFilename() {
		String myFilename = "";
		
		// hostname part
//		try {
//			InetAddress addr = InetAddress.getLocalHost();
//
//		    // Get hostname
//		    String hostname = addr.getHostName();
//		    myFilename = hostname;
//		} catch (UnknownHostException e) {
//			myFilename = "default";
//		}
		
//		myFilename += "-";
		
		// date part
		Date myDate = new Date();
        SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd_kk-mm-ss");
        StringBuilder myDateString = new StringBuilder(myDateFormat.format(myDate));
		myFilename += myDateString;
		
		// ending
		myFilename += ".txt";
		
		return FILE_PATH+myFilename;
	}
	
	private static Formatter createFormatter(String theName) {

		if (theName == "XMLFormatter") 
			return new XMLFormatter();
		
		if (theName == "SimpleFormatter") 
			return new SimpleFormatter();

		return new CCSimpleLogFormatter();
	}
	
	private static void initialize() {
		if (ourFileHandler == null && HANDLERS.contains("FILE")) {
			String myLogFilename = getFilename();
			
			try {
				ourFileHandler = new FileHandler(myLogFilename);
				ourFileHandler.setLevel(FILE_VERBOSITY);
				ourFileHandler.setFormatter(createFormatter(FILE_FORMAT));
				CCLog.info("logging to file: " + myLogFilename);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
//				e.printStackTrace();
			}
		}	
		
		if (ourTCPHandler == null && HANDLERS.contains("TCP")) {
			try {
				ourTCPHandler = new SocketHandler(TCP_HOST, Integer.parseInt(TCP_PORT));
				ourTCPHandler.setLevel(TCP_VERBOSITY);
				ourTCPHandler.setFormatter(createFormatter(TCP_FORMAT));
				CCLog.info("logging to socket: " + TCP_HOST + ":" + TCP_PORT);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
		
		// register CCLogExceptionHandler as default.
		CCLogExceptionHandler.register();
		
		Logger logger = Logger.getLogger ("");
		if (logger.getHandlers ().length > 0) {
			try{
				logger.removeHandler (logger.getHandlers ()[0]);
			}catch(ArrayIndexOutOfBoundsException myException){
				
			}
		} 
		
		// configure default logger
		setConfiguration(logger);
		ourInitialized = true;
	}
	
	private static void setConfiguration(Logger theLogger) {
		CCStandardOutHandler myDefaultHandler = new CCStandardOutHandler();
		myDefaultHandler.setFormatter(createFormatter(CONSOLE_FORMAT));
		theLogger.addHandler(myDefaultHandler);
		
		theLogger.setLevel(CONSOLE_VERBOSITY);
		if (ourFileHandler != null) {
			theLogger.addHandler(ourFileHandler);
		}
		
		if (ourTCPHandler != null) {
			theLogger.addHandler(ourTCPHandler);
		}
		
		// ignore default parent handler
		theLogger.setUseParentHandlers(false);
	}

	@SuppressWarnings("deprecation")
	private static void log(Level theLevel, Throwable theMessage) {
		final Throwable myT = new Throwable();
        final StackTraceElement myMethodCaller = myT.getStackTrace()[2];
        		
		Logger myLogger = getLogger(myMethodCaller.getClassName());		
		String myClass  = myMethodCaller.getClassName();
		String myMethod = myMethodCaller.getMethodName();
		String myBundle = myMethodCaller.getClass().getPackage().toString();
	
		String myException = throwableToString(theMessage);
		myLogger.logrb(theLevel, myClass, myMethod, myBundle, myException);
	}

	private static String throwableToString(Throwable e) {
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return sw.toString();
		} catch(Exception e2) {
			e2.printStackTrace();
			return "throwableToString error";
		}
	}	

	@SuppressWarnings("deprecation")
	private static void log(Level theLevel, String theMessage) {
		final Throwable myT = new Throwable();
        final StackTraceElement myMethodCaller = myT.getStackTrace()[2];
      
		Logger myLogger = getLogger(myMethodCaller.getClassName());		
		String myClass  = myMethodCaller.getClassName();
		String myMethod = myMethodCaller.getMethodName();
		String myBundle = myMethodCaller.getClass().getPackage().toString();
		int myLineNumber = myMethodCaller.getLineNumber();
	
		myLogger.logrb(theLevel, myClass + " Line:" + myLineNumber, myMethod, myBundle, theMessage);
	}

	/**
	 * Log an exception with WARNING level.
	 * @param theMessage the message to be logged
	 */
	public static void warn(Throwable theMessage) {
		log(Level.WARNING, theMessage);
	}

	/**
	 * Log an exception with ERROR level.
	 * @param theMessage the message to be logged
	 */	
	public static void error(Throwable theMessage) {		
		log(Level.SEVERE, theMessage);
	}

	/**
	 * Log an exception with ERROR level and a causing exception
	 * @param theMessage the message to be logged
	 * @param theCause the causing exception
	 */	
	public static void error(String theMessage, Throwable theCause) {		
		log(Level.SEVERE, new RuntimeException(theMessage, theCause));
	}

	/**
	 * Log an exception with INFO level.
	 * @param theMessage the message to be logged
	 */
	public static void info(Throwable theMessage) {		
		log(Level.INFO, theMessage);
	}

	/**
	 * Log an exception with INFO level and a causing exception
	 * @param theMessage the message to be logged
	 * @param theCause the causing exception
	 */	
	public static void info(String theMessage, Throwable theCause) {		
		log(Level.INFO, new RuntimeException(theMessage, theCause));
	}

//	/**
//	 * Log a message with INFO level.
//	 * @param theMessage the message to be logged
//	 */
//	public static void info(Object theMessage) {
//		if (theMessage == null) {
//			log(Level.INFO, "null");
//			return;
//		} 
//		if(theMessage.getClass().isArray()) {
//			StringBuffer myMessage = new StringBuffer();
//			
//			for(int i = 0; i < Array.getLength(theMessage);i++) {
//				myMessage.append(Array.get(theMessage, i));
//				myMessage.append(" : ");
//			}
//			myMessage.delete(myMessage.length()-3, myMessage.length());
//			log(Level.INFO, myMessage.toString());
//			return;
//			
//		}
//		
//		log(Level.INFO, theMessage.toString());
//	}
	
	/**
	 * Log a message with INFO level all objects are concatenated with " : " String
	 * @param theObjects the Objects to log
	 */
	public static void info(Object...theObjects) {
		StringBuffer myMessage = new StringBuffer();
		for(Object myObject:theObjects) {
			if(myObject == null)continue;
			if(myObject.getClass().isArray()){
				for(int i = 0; i < Array.getLength(myObject);i++) {
					myMessage.append(Array.get(myObject, i));
					myMessage.append(" : ");
				}
			}else{
				myMessage.append(myObject);
				myMessage.append(" : ");
			}
		}
		myMessage.delete(myMessage.length()-3, myMessage.length());
		log(Level.INFO, myMessage.toString());
	}

	/**
	 * Log an exception with INFO level.
	 * @param theMessage the message to be logged
	 */
	public static void fine(Throwable theMessage) {		
		log(Level.FINE, theMessage);
	}
	/**
	 * Log a message with INFO level.
	 * @param theMessage the message to be logged
	 */
	public static void fine(Object theMessage) {
		if (theMessage == null) {
			log(Level.FINE, "null");
			return;
		} 
		log(Level.FINE, theMessage.toString());
	}

	/**
	 * Log an exception with INFO level and a causing exception
	 * @param theMessage the message to be logged
	 * @param theCause the causing exception
	 */	
	public static void fine(String theMessage, Throwable theCause) {		
		log(Level.FINE, new RuntimeException(theMessage, theCause));
	}
	
	/**
	 * Log a message with WARNING level.
	 * @param theMessage the message to be logged
	 */
	public static void warn(Object theMessage) {
		if (theMessage == null) {
			log(Level.WARNING, "null");
			return;
		} 
		log(Level.WARNING, theMessage.toString());
	}
	
	/**
	 * Log a message with WARNING level and a causing exception
	 * @param theMessage the message to be logged
	 * @param theCause the exception causing the warning
	 */
	public static void warn(String theMessage, Throwable theCause){
		log(Level.WARNING, new RuntimeException(theMessage, theCause));
	}

	/**
	 * Log a message with ERROR level.
	 * @param theMessage the message to be logged
	 */	
	public static void error(Object theMessage) {		
		if (theMessage == null) {
			log(Level.SEVERE, "null");
			return;
		} 
		log(Level.SEVERE, theMessage.toString());
		
	}
}
