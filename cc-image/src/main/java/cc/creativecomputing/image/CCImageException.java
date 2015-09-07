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
package cc.creativecomputing.image;

/**
 * @author christianriekoff
 *
 */
@SuppressWarnings("serial")
public class CCImageException extends RuntimeException{

	public CCImageException() {
		super();
	}

	public CCImageException(String theMessage, Throwable theCause) {
		super(theMessage, theCause);
	}

	public CCImageException(String theMessage) {
		super(theMessage);
	}

	public CCImageException(Throwable theCause) {
		super(theCause);
	}

}
