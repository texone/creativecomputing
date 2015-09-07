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
package cc.creativecomputing.io;

public class CCIOException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6260852642259168506L;

	/**
	 * 
	 */
	public CCIOException() {
		super();
	}

	/**
	 * @param theMessage
	 * @param theCause
	 */
	public CCIOException(String theMessage, Throwable theCause) {
		super(theMessage, theCause);
	}

	/**
	 * @param theMessage
	 */
	public CCIOException(String theMessage) {
		super(theMessage);
	}

	/**
	 * @param theCause
	 */
	public CCIOException(Throwable theCause) {
		super(theCause);
	}

}
