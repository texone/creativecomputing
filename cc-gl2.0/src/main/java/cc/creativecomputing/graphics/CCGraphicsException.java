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
package cc.creativecomputing.graphics;

public class CCGraphicsException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5270268084415418293L;

	/**
	 * 
	 */
	public CCGraphicsException() {
		super();
	}

	/**
	 * @param theMessage
	 * @param theCause
	 */
	public CCGraphicsException(String theMessage, Throwable theCause) {
		super(theMessage, theCause);
	}

	/**
	 * @param theMessage
	 */
	public CCGraphicsException(String theMessage) {
		super(theMessage);
	}

	/**
	 * @param theCause
	 */
	public CCGraphicsException(Throwable theCause) {
		super(theCause);
	}

}
