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
package cc.creativecomputing.graphics.shader;

public class CCShaderException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3250584933010270551L;

	/**
	 * 
	 */
	public CCShaderException() {
		super();
	}

	/**
	 * @param theMessage
	 * @param theCause
	 */
	public CCShaderException(String theMessage, Throwable theCause) {
		super(theMessage, theCause);
	}

	/**
	 * @param theMessage
	 */
	public CCShaderException(String theMessage) {
		super(theMessage);
	}

	/**
	 * @param theCause
	 */
	public CCShaderException(Throwable theCause) {
		super(theCause);
	}

}
