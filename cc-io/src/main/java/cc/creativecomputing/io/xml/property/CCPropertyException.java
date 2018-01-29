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
package cc.creativecomputing.io.xml.property;

public class CCPropertyException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4664231192209191256L;

	public CCPropertyException() {
		super();
	}

	public CCPropertyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CCPropertyException(String message, Throwable cause) {
		super(message, cause);
	}

	public CCPropertyException(String message) {
		super(message);
	}

	public CCPropertyException(Throwable cause) {
		super(cause);
	}

}
