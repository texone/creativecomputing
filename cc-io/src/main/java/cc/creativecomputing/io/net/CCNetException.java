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
package cc.creativecomputing.io.net;


/**
 * Exception thrown by some network related methods. Typical reasons are communication timeout and buffer underflows or
 * overflows.
 * 
 * @author Christian Riekoff
 */
public class CCNetException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8433613945749257554L;
	
	public CCNetException() {
		super();
	}

	public CCNetException(String theArg0, Throwable theArg1) {
		super(theArg0, theArg1);
	}

	public CCNetException(String theArg0) {
		super(theArg0);
	}

	public CCNetException(Throwable theArg0) {
		super(theArg0);
	}
}
