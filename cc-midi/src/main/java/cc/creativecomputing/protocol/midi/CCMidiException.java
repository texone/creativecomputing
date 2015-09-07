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
package cc.creativecomputing.protocol.midi;

/**
 * @invisible
 * @author tex
 *
 */
public class CCMidiException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2585312586392145799L;

	/**
	 * 
	 */
	public CCMidiException() {
		super();
	}

	/**
	 * @param theMessage
	 * @param theCause
	 */
	public CCMidiException(String theMessage, Throwable theCause) {
		super(theMessage, theCause);
	}

	/**
	 * @param theMessage
	 */
	public CCMidiException(String theMessage) {
		super(theMessage);
	}

	/**
	 * @param theCause
	 */
	public CCMidiException(Throwable theCause) {
		super(theCause);
	}


}
