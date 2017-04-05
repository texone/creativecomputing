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
package cc.creativecomputing.io.netty.codec.osc;


/**
 * Exception thrown by some OSC related methods. Typical reasons are communication timeout and buffer underflows or
 * overflows.
 * 
 * @author Hanns Holger Rutz
 * @version 0.10, 26-May-05
 */
public class CCOSCException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8433613945749257554L;
	/**
	 * causeType : communication timeout
	 */
	public static final int TIMEOUT = 0;
	/**
	 * causeType : supercollider replies "fail"
	 */
	public static final int FAILED = 1;
	/**
	 * causeType : buffer overflow or underflow
	 */
	public static final int BUFFER = 2;
	/**
	 * causeType : OSC message has invalid format
	 */
	public static final int FORMAT = 3;
	/**
	 * causeType : OSC message has invalid or unsupported type tags
	 */
	public static final int TYPETAG = 4;
	/**
	 * causeType : OSC message cannot convert given java class to OSC primitive
	 */
	public static final int JAVACLASS = 5;
	/**
	 * causeType : network error while receiving OSC message
	 */
	public static final int RECEIVE = 6;

	public CCOSCException() {
		super();
	}

	public CCOSCException(String theArg0, Throwable theArg1) {
		super(theArg0, theArg1);
	}

	public CCOSCException(String theArg0) {
		super(theArg0);
	}

	public CCOSCException(Throwable theArg0) {
		super(theArg0);
	}
}
